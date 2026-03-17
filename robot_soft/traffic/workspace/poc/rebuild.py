import base64
import re
import struct
import sys
from pathlib import Path

BASE64_RE = re.compile(r"^[A-Za-z0-9+/=]+$")

def class_utf8_constants(path: Path) -> list[str]: #解析class中的utf-8字符串
    data = path.read_bytes()
    pos = 8
    cp_count = struct.unpack(">H", data[pos:pos + 2])[0]
    pos += 2
    vals = []
    i = 1
    while i < cp_count:
        tag = data[pos]
        pos += 1
        if tag == 1:
            ln = struct.unpack(">H", data[pos:pos + 2])[0]
            pos += 2
            vals.append(data[pos:pos + ln].decode("utf-8", "replace"))
            pos += ln
        elif tag in (3, 4, 9, 10, 11, 12, 17, 18):
            pos += 4
        elif tag in (5, 6):
            pos += 8
            i += 1
        elif tag in (7, 8, 16, 19, 20):
            pos += 2
        elif tag == 15:
            pos += 3
        else:
            raise ValueError(f"bad tag {tag} in {path}")
        i += 1
    return vals

def extract_chunk(path: Path): #提取base64和index
    vals = class_utf8_constants(path)
    
    if not {"FileOperation.java", "update", "/var/tmp/out", "30720"}.issubset(set(vals)):
        return None

    chunk_data, block_index = None, None
    for s in vals:
        # 块索引
        if s.isdigit() and s != "30720":
            block_index = int(s)
        # 合法Base64
        elif len(s) >= 64 and len(s) % 4 == 0 and BASE64_RE.fullmatch(s):
            try:
                chunk_data = base64.b64decode(s)
            except Exception:
                pass

    if chunk_data and block_index is not None:
        return block_index, chunk_data
    return None

def main():
    if len(sys.argv) != 3:
        print("python3 rebuild.py <输入目录(有.class文件)> <输出路径>")
        sys.exit(1)

    input_dir = Path(sys.argv[1]).resolve()
    output_path = Path(sys.argv[2]).resolve()

    chunks = []
    for class_path in input_dir.glob("*.class"):
        try:
            info = extract_chunk(class_path)
            if info:
                chunks.append(info)
        except Exception:
            continue
    try:
        max_end = max((idx * 30720) + len(data) for idx, data in chunks)
        buf = bytearray(max_end)

        # 拼接数据块
        for idx, data in chunks:
            start = idx * 30720
            buf[start : start + len(data)] = data

        output_path.parent.mkdir(parents=True, exist_ok=True)
        output_path.write_bytes(buf)
        
        print(f"总块数: {len(chunks)}")
        print(f"输出: {output_path}")
        
    except Exception as e:
        print(f"异常 -> {e}")

if __name__ == "__main__":
    main()
