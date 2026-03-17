#!/usr/bin/env python3
import argparse
import base64
import binascii
import hashlib
import json
import struct
import subprocess
from pathlib import Path

from Crypto.Cipher import AES


DEFAULT_KEY = b"1f2c8075acd3d118"


def pkcs7_unpad(data: bytes) -> bytes:
    if not data:
        return data
    pad_len = data[-1]
    if pad_len < 1 or pad_len > 16:
        return data
    if data[-pad_len:] != bytes([pad_len]) * pad_len:
        return data
    return data[:-pad_len]


def run_tshark(pcap: Path) -> list[dict]:
    cmd = [
        "tshark",
        "-r",
        str(pcap),
        "-Y",
        'http.request.method=="POST" && http.request.uri contains "favicondemo.ico"',
        "-T",
        "fields",
        "-e",
        "frame.number",
        "-e",
        "frame.time_epoch",
        "-e",
        "ip.src",
        "-e",
        "ip.dst",
        "-e",
        "http.content_length",
        "-e",
        "data.data",
    ]
    proc = subprocess.run(cmd, capture_output=True, text=True, check=True)
    rows = []
    for line in proc.stdout.splitlines():
        if not line.strip():
            continue
        parts = line.split("\t")
        if len(parts) != 6:
            continue
        frame, epoch, ip_src, ip_dst, content_length, data_hex = parts
        rows.append(
            {
                "frame": int(frame),
                "time_epoch": float(epoch),
                "ip_src": ip_src,
                "ip_dst": ip_dst,
                "content_length": int(content_length) if content_length else None,
                "data_hex": data_hex,
            }
        )
    rows.sort(key=lambda x: (x["time_epoch"], x["frame"]))
    return rows


def decrypt_body(data_hex: str, key: bytes) -> tuple[bytes, bytes, bytes]:
    raw_body = bytes.fromhex(data_hex)
    base64_text = raw_body.decode("ascii")
    encrypted = base64.b64decode(base64_text)
    decrypted = AES.new(key, AES.MODE_ECB).decrypt(encrypted)
    return raw_body, encrypted, pkcs7_unpad(decrypted)


def sniff_plaintext_type(plaintext: bytes) -> dict:
    info = {
        "starts_with_cafebabe": plaintext.startswith(b"\xca\xfe\xba\xbe"),
        "sha256": hashlib.sha256(plaintext).hexdigest(),
        "length": len(plaintext),
        "type": "binary",
    }
    if info["starts_with_cafebabe"]:
        info["type"] = "java_class"
        info["class_version"] = struct.unpack(">H", plaintext[6:8])[0] if len(plaintext) >= 8 else None
        return info
    try:
        text = plaintext.decode("utf-8")
        info["type"] = "utf8_text"
        info["preview"] = text[:200]
        return info
    except UnicodeDecodeError:
        pass
    try:
        text = plaintext.decode("latin1")
        info["type"] = "latin1_text"
        info["preview"] = text[:200]
        return info
    except UnicodeDecodeError:
        return info


def main() -> None:
    parser = argparse.ArgumentParser(
        description="Extract and decrypt all /favicondemo.ico POST bodies from a PCAP in time order."
    )
    parser.add_argument(
        "-p",
        "--pcap",
        default="traffic_hunt.pcapng",
        help="Path to the PCAP/PCAPNG file",
    )
    parser.add_argument(
        "-o",
        "--outdir",
        default="workspace/poc/favicondemo_plaintext",
        help="Directory to store extracted plaintext bodies",
    )
    parser.add_argument(
        "-k",
        "--key",
        default=DEFAULT_KEY.decode(),
        help="Behinder AES key used for /favicondemo.ico body decryption",
    )
    args = parser.parse_args()

    pcap = Path(args.pcap).resolve()
    outdir = Path(args.outdir).resolve()
    key = args.key.encode()

    outdir.mkdir(parents=True, exist_ok=True)
    classes_dir = outdir / "bodies"
    classes_dir.mkdir(parents=True, exist_ok=True)
    metadata_path = outdir / "metadata.jsonl"

    rows = run_tshark(pcap)
    with metadata_path.open("w", encoding="utf-8") as meta_fp:
        for index, row in enumerate(rows, start=1):
            raw_body, encrypted, plaintext = decrypt_body(row["data_hex"], key)
            info = sniff_plaintext_type(plaintext)
            suffix = ".class" if info["starts_with_cafebabe"] else ".bin"
            out_name = f"{index:03d}_frame_{row['frame']}{suffix}"
            out_path = classes_dir / out_name
            out_path.write_bytes(plaintext)

            record = {
                "index": index,
                "frame": row["frame"],
                "time_epoch": row["time_epoch"],
                "ip_src": row["ip_src"],
                "ip_dst": row["ip_dst"],
                "content_length": row["content_length"],
                "raw_body_length": len(raw_body),
                "encrypted_length": len(encrypted),
                "plaintext_path": str(out_path),
                **info,
            }
            meta_fp.write(json.dumps(record, ensure_ascii=False) + "\n")

    print(f"[+] extracted {len(rows)} bodies")
    print(f"[+] plaintext files: {classes_dir}")
    print(f"[+] metadata: {metadata_path}")


if __name__ == "__main__":
    main()
