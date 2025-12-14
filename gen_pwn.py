import argparse
import os
import sys
from string import Template

# ---------------------------------------------------------
# 1. 模板库 (Templates)
# ---------------------------------------------------------

# === 模板 A: 简单版 (你原本的 exp.py) ===
TEMPLATE_SIMPLE = Template(r"""
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

filename = "${binary_name}"
libc_name = "${libc_name}"
arch = "${arch}"
remote_addr = "${ip}"
remote_port = "${port}"

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
        ''',
    )
elif args.mode == 2:
    io = remote(remote_addr, remote_port)
else:
    sys.exit(1)

elf = ELF(filename, checksec=False)
if libc_name:
    libc = ELF(libc_name, checksec=False)

# --- Exploit Start ---

io.interactive()
""")

# === 模板 B: 多线程/高级版 ===
TEMPLATE_THREAD = Template(r"""
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
filename = "${binary_name}"
libc_name = "${libc_name}"
arch = "${arch}"
remote_addr = "${ip}" 
remote_port = ${port}

context(log_level="debug", os="linux", arch=arch)

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

    # 优先处理多线程模式 (如果 -T 被设置)
    if args.threads is not None:
        if args.threads <= 0:
            raise ValueError("Thread count must be positive.")
        
        threads = [remote(remote_addr, remote_port, ssl=False) for _ in range(args.threads)]
        CLEAR_TEXT(f"[*] Started {args.threads} remote threads on {remote_addr}:{remote_port}")
        return threads

    elif args.mode == 0:
        io = process(filename)
        CLEAR_TEXT("[*] Running on local machine (mode 0)")
        return io
        
    elif args.mode == 1:
        io = process(filename)
        CLEAR_TEXT("[*] Running on local machine with GDB (mode 1)")
        gdb.attach(io, gdbscript='''
        ''')
        return io
        
    elif args.mode == 2:
        io = remote(remote_addr, remote_port)
        CLEAR_TEXT(f"[*] Running on remote: {remote_addr}:{remote_port} (mode 2)")
        return io
    else:
        sys.exit(1)

def main(threads):
    t0=threads[0]
    t0:cube 
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
        
        # --- Exploit Logic ---
        # main([io])  # Wrap io in a list to match the threads parameter
        io.interactive()
""")

# ---------------------------------------------------------
# 2. 生成器逻辑
# ---------------------------------------------------------
def get_args():
    parser = argparse.ArgumentParser(description="Generate Pwn exploit script from template.")
    
    # 基础参数
    parser.add_argument("-b", "--binary", default="./pwn", help="Binary path (default: ./pwn)")
    parser.add_argument("-i", "--ip", default="localhost", help="Remote IP (default: localhost)")
    parser.add_argument("-p", "--port", default="1337", help="Remote Port (default: 1337)")
    parser.add_argument("-l", "--libc", default="", help="Libc path (optional)")
    parser.add_argument("-a", "--arch", default="amd64", help="Architecture (default: amd64)")
    parser.add_argument("-o", "--output", default="exp.py", help="Output filename (default: exp.py)")
    
    # 模板选择参数
    parser.add_argument("-t", "--type", choices=['simple', 'thread'], default='simple', 
                        help="Template type: 'simple' (default) or 'thread' (multi-thread support)")
    
    return parser.parse_args()

def handle_file_conflict(filepath):
    """处理文件已存在的逻辑"""
    if not os.path.exists(filepath):
        return filepath

    print(f"\033[33mWarning: '{filepath}' already exists.\033[0m")
    while True:
        choice = input("Overwrite? (y/n/rename): ").strip().lower()
        if choice in ['y', 'yes']:
            print(f"Overwriting {filepath}...")
            return filepath
        elif choice in ['n', 'no']:
            print("Operation cancelled.")
            sys.exit(0)
        elif choice in ['r', 'rename']:
            new_name = input("Enter new filename: ").strip()
            return new_name
        else:
            print("Invalid choice.")

def main():
    args = get_args()

    # 1. 确定输出文件名
    output_file = handle_file_conflict(args.output)

    # 2. 准备数据
    data = {
        "binary_name": args.binary,
        "ip": args.ip,
        "port": args.port,
        "libc_name": args.libc,
        "arch": args.arch
    }

    # 3. 选择模板
    if args.type == 'thread':
        selected_template = TEMPLATE_THREAD
        template_name = "Multi-Threaded"
    else:
        selected_template = TEMPLATE_SIMPLE
        template_name = "Simple (Classic)"

    # 4. 渲染并写入
    try:
        content = selected_template.substitute(data)
        with open(output_file, 'w') as f:
            f.write(content)
        
        # 赋予执行权限
        st = os.stat(output_file)
        os.chmod(output_file, st.st_mode | 0o111)
        
        print(f"\033[32m[+] Generated {template_name} exploit at: {output_file}\033[0m")
        print(f"    Target: {args.ip}:{args.port} | Binary: {args.binary}")

    except Exception as e:
        print(f"\033[31m[-] Error: {e}\033[0m")

if __name__ == "__main__":
    main()
