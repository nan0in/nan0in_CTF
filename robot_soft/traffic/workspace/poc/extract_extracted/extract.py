import marshal, dis
from pathlib import Path
p = Path("./implant.pyc")
code = marshal.loads(p.read_bytes()[16:])
dis.dis(code)
