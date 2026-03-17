import argparse
import base64
import binascii
import json
import socket
import struct
import subprocess
from pathlib import Path

from cryptography.hazmat.primitives.ciphers.aead import AESGCM


DEFAULT_PCAP = "traffic_hunt.pcapng"
DEFAULT_STREAM = 40563
DEFAULT_KEY_B64 = "IhbJfHI98nuSvs5JweD5qsNvSQ/HHcE/SNLyEBU9Phs="
DEFAULT_OUTPUT = "workspace/poc/c2_7788_decrypted.txt"

def tshark_payloads(pcap_path: Path, stream_id: int):
    cmd = [
        "tshark",
        "-r",
        str(pcap_path),
        "-Y",
        f"tcp.stream=={stream_id} && tcp.len>0",
        "-T",
        "fields",
        "-e",
        "frame.number",
        "-e",
        "ip.src",
        "-e",
        "tcp.srcport",
        "-e",
        "ip.dst",
        "-e",
        "tcp.dstport",
        "-e",
        "tcp.payload",
    ]
    result = subprocess.run(cmd, check=True, capture_output=True, text=True)
    rows = []
    for line in result.stdout.splitlines():
        parts = line.split("\t")
        if len(parts) != 6:
            continue
        frame_no, src_ip, src_port, dst_ip, dst_port, payload_hex = parts
        if not payload_hex:
            continue
        rows.append(
            {
                "frame": int(frame_no),
                "src_ip": src_ip,
                "src_port": int(src_port),
                "dst_ip": dst_ip,
                "dst_port": int(dst_port),
                "payload": bytes.fromhex(payload_hex),
            }
        )
    return rows


def regroup_messages(rows):
    conversations = {}
    for row in rows:
        key = (row["src_ip"], row["src_port"], row["dst_ip"], row["dst_port"])
        conversations.setdefault(key, []).append(row)

    messages = []
    for key, packets in conversations.items():
        pending_len = None
        pending_len_frame = None
        for packet in packets:
            payload = packet["payload"]
            if len(payload) == 4:
                pending_len = struct.unpack("<I", payload)[0]
                pending_len_frame = packet["frame"]
                continue

            if pending_len is None:
                continue

            if len(payload) != pending_len:
                raise ValueError(
                    f"frame {packet['frame']} payload length mismatch: "
                    f"expected {pending_len}, got {len(payload)}"
                )

            messages.append(
                {
                    "direction": f"{packet['src_ip']}:{packet['src_port']} -> "
                    f"{packet['dst_ip']}:{packet['dst_port']}",
                    "len_frame": pending_len_frame,
                    "data_frame": packet["frame"],
                    "length": pending_len,
                    "ciphertext": payload,
                }
            )
            pending_len = None
            pending_len_frame = None

    messages.sort(key=lambda item: item["data_frame"])
    return messages


def decrypt_message(aesgcm: AESGCM, ciphertext: bytes) -> bytes:
    if len(ciphertext) < 28:
        raise ValueError("ciphertext too short for nonce + tag")
    nonce = ciphertext[:12]
    body = ciphertext[12:]
    return aesgcm.decrypt(nonce, body, None)


def best_effort_decode(data: bytes) -> str:
    for encoding in ("utf-8", "gbk"):
        try:
            return data.decode(encoding)
        except UnicodeDecodeError:
            continue
    return data.decode("utf-8", errors="replace")


def render_output(messages, output_path: Path):
    lines = []
    for idx, msg in enumerate(messages, 1):
        lines.append(f"[{idx}] {msg['direction']}")
        lines.append(
            f"len_frame={msg['len_frame']} data_frame={msg['data_frame']} "
            f"length={msg['length']}"
        )
        lines.append(f"nonce={msg['ciphertext'][:12].hex()}")
        lines.append(f"plaintext={msg['plaintext_text']}")
        lines.append("")

    output_path.parent.mkdir(parents=True, exist_ok=True)
    output_path.write_text("\n".join(lines), encoding="utf-8")


def main():
    parser = argparse.ArgumentParser(description="Decrypt the implant 7788 C2 stream.")
    parser.add_argument("--pcap", default=DEFAULT_PCAP, help="PCAP/PCAPNG path")
    parser.add_argument("--stream", type=int, default=DEFAULT_STREAM, help="tcp.stream id")
    parser.add_argument("--key-b64", default=DEFAULT_KEY_B64, help="AES-GCM key in Base64")
    parser.add_argument("--output", default=DEFAULT_OUTPUT, help="Output txt path")
    args = parser.parse_args()

    key = base64.b64decode(args.key_b64)
    aesgcm = AESGCM(key)

    rows = tshark_payloads(Path(args.pcap), args.stream)
    messages = regroup_messages(rows)

    for msg in messages:
        plaintext = decrypt_message(aesgcm, msg["ciphertext"])
        msg["plaintext_hex"] = plaintext.hex()
        msg["plaintext_text"] = best_effort_decode(plaintext)

    render_output(messages, Path(args.output))

    print(json.dumps(
        {
            "pcap": args.pcap,
            "stream": args.stream,
            "message_count": len(messages),
            "output": str(Path(args.output).resolve()),
        },
        ensure_ascii=False,
        indent=2,
    ))


if __name__ == "__main__":
    main()
