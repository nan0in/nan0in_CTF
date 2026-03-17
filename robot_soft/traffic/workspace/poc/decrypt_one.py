import base64
from cryptography.hazmat.primitives.ciphers.aead import AESGCM

def decrypt_implant_data(hex_data: str, key_b64: str):
    encrypted_data = bytes.fromhex(hex_data)
    
    try:
        key = base64.b64decode(key_b64)
        if len(key) not in (16, 24, 32):
            print(f"错误：解密后的密钥长度为 {len(key)}，必须为 16, 24 或 32 字节。")
            return
    except Exception as e:
        print(f"密钥 Base64 解码失败: {e}")
        return

    nonce = encrypted_data[:12]
    ciphertext_with_tag = encrypted_data[12:]
    
    print(f"数据总长度: {len(encrypted_data)} 字节")
    print(f"Nonce (12 bytes): {nonce.hex()}")
    print(f"密文+Tag: {ciphertext_with_tag.hex()}")

    try:
        aesgcm = AESGCM(key)
        plaintext = aesgcm.decrypt(nonce, ciphertext_with_tag, None)
        print(f"\n明文 (Hex): {plaintext.hex()}")
        try:
            print(f"明文 (UTF-8): {plaintext.decode('utf-8')}")
        except UnicodeDecodeError:
            print("明文包含非 UTF-8 可打印字符。")
    except Exception as e:
        print(f"\n解密失败: {e}")

if __name__ == '__main__':
    data = "5fb656ee12487f33e75202b3bec1a6728977618d6b221fb887fa90d36cb5ff75949c1ae90608e22fc81a12fb2e576dd2df4330fcbf619b19455dcfe6c9ae2a8e730cf9010dcc3a15f04bec1fa70b051792d4e197cee0f075405b366472711d1d94f5bb349348bf05d5"
    aes_key_base64 = "IhbJfHI98nuSvs5JweD5qsNvSQ/HHcE/SNLyEBU9Phs="
    decrypt_implant_data(data, aes_key_base64)
