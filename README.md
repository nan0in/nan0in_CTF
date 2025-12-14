# introduction 

由于很多比赛都不会有复盘的平台，所以我打算把自己做过的题目都放在这里，方便大家进行查看和复现，并且我也可以通过这个仓库来记录自己的成长轨迹。  
我会将题目的解题脚本和部分不放在我博客中的文件等放在这里，大部分有exp或poc的部分都可以在我的博客中找到具体的解题思路  
[博客地址](https://nan0in27.cn)
过去的部分也会陆续补上  

## 关于pwn

exp部分使用了我的pwn模板，在`gen_pwn.py` ，拉下来后 
在你的bash文件中添加  
```sh 
export PWN_TOOL_PATH="/your_path/gen_pwn.py"
function exp(){
      python "$PWN_TOOL_PATH" "$@"
}
```
然后就可以在终端中使用exp命令来快速生成pwn模板了  
目前有两个生成脚本，通过`-t`参数来选择  
```sh 
options:
  -h, --help            show this help message and exit
  -b BINARY, --binary BINARY
                        Binary path (default: ./pwn)
  -i IP, --ip IP        Remote IP (default: localhost)
  -p PORT, --port PORT  Remote Port (default: 1337)
  -l LIBC, --libc LIBC  Libc path (optional)
  -a ARCH, --arch ARCH  Architecture (default: amd64)
  -o OUTPUT, --output OUTPUT
                        Output filename (default: exp.py)
  -t {simple,thread}, --type {simple,thread}
                        Template type: 'simple' (default) or 'thread' (multi-thread support)
```
分别是simple和thread版本，thread版本支持多线程等功能，更加便于特殊调试情况，后续也会添加异构、爆破等功能  

### 关于exp
exp脚本有如下使用范例  
1. `python exp.py` 直接本地运行 
2. `python exp.py 1` 本地打开gdb调试运行 
3. `python exp.py 2` 远程连接运行  
如果是thread版本的exp脚本，还可以使用如下方式进行多线程调试 
4. `python exp.py -T 4` 本地开启4线程进行调试运行

## 关于其他方向  
会在文件中提供附件和必要脚本，如果有需要注意的部分会在对应的文件夹中进行说明 

## Copyright 
Distributed under the CC BY-NC-SA4.0.  
Copyright Nan0in 2025-
