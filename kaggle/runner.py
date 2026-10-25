# -*- coding: utf-8 -*-
# רץ בתוך Kaggle. המשתנה JOB מוזרק בראש הקובץ על ידי scripts/push_to_kaggle.py
import glob
import json
import os
import shutil
import subprocess
import tarfile
import time
import urllib.request

OUT = "/kaggle/working"
RELEASE_URL = "https://github.com/{repo}/releases/download/llama-cuda12-sm75/llama-bin.tar.gz"


def sh(cmd: str) -> str:
    r = subprocess.run(cmd, shell=True, capture_output=True, text=True)
    return (r.stdout + r.stderr).strip()


def free_gb(path: str) -> float:
    os.makedirs(path, exist_ok=True)
    return shutil.disk_usage(path).free / 1e9


def pick_workdir(min_gb: float = 40.0) -> str:
    for cand in ("/kaggle/temp", "/tmp"):
        g = free_gb(cand)
        print(f"[disk] {cand}: {g:.0f} GB free", flush=True)
        if g >= min_gb:
            return cand
    raise SystemExit("No directory with enough free disk space for the model")


def main() -> None:
    print(sh("nvidia-smi"), flush=True)
    n_gpu = len([l for l in sh("nvidia-smi -L").splitlines() if l.startswith("GPU")])
    print(f"[gpu] count={n_gpu}", flush=True)
    print(sh("free -h"), flush=True)

    work = pick_workdir()
    bindir = os.path.join(work, "llama-bin")
    os.makedirs(bindir, exist_ok=True)

    # 1) הורדת הבינארי המקומפל מה-Release
    url = RELEASE_URL.format(repo=JOB["release_repo"])
    print("[llama] downloading", url, flush=True)
    tgz = os.path.join(work, "llama-bin.tar.gz")
    try:
        urllib.request.urlretrieve(url, tgz)
    except Exception as e:  # noqa: BLE001
        raise SystemExit(f"Could not download llama binaries ({e}). Run the 'Build llama.cpp' workflow first.")
    with tarfile.open(tgz) as t:
        t.extractall(bindir)
    server = os.path.join(bindir, "llama-server")
    os.chmod(server, 0o755)

    # 2) הורדת המודל
    from huggingface_hub import snapshot_download

    print("[model] downloading", JOB["model_repo"], JOB["model_pattern"], flush=True)
    t0 = time.time()
    mdir = snapshot_download(
        JOB["model_repo"],
        allow_patterns=[JOB["model_pattern"]],
        local_dir=os.path.join(work, "models"),
    )
    ggufs = sorted(glob.glob(os.path.join(mdir, "**", "*.gguf"), recursive=True))
    if not ggufs:
        raise SystemExit("No .gguf file matched the pattern")
    print(f"[model] done in {time.time() - t0:.0f}s:", ggufs, flush=True)
    gguf = ggufs[0]  # במודל מפוצל llama.cpp מוצא לבד את שאר החלקים

    # 3) הפעלת השרת
    env = dict(os.environ)
    env["LD_LIBRARY_PATH"] = ":".join([bindir, "/usr/local/cuda/lib64", env.get("LD_LIBRARY_PATH", "")])
    cmd = [server, "-m", gguf, "-ngl", "99", "-c", "16384", "--host", "127.0.0.1", "--port", "8080"]
    if n_gpu > 1:
        cmd += ["--tensor-split", ",".join(["1"] * n_gpu)]
    log = open(os.path.join(OUT, "server.log"), "w")
    print("[server] starting:", " ".join(cmd), flush=True)
    proc = subprocess.Popen(cmd, env=env, stdout=log, stderr=subprocess.STDOUT)

    import requests

    ok = False
    for _ in range(180):  # עד 15 דקות
        if proc.poll() is not None:
            break
        try:
            if requests.get("http://127.0.0.1:8080/health", timeout=3).status_code == 200:
                ok = True
                break
        except Exception:  # noqa: BLE001
            pass
        time.sleep(5)
    if not ok:
        log.flush()
        print(sh(f"tail -n 60 {OUT}/server.log"), flush=True)
        raise SystemExit("llama-server did not become healthy")
    print("[server] ready", flush=True)

    # 4) המשימה
    messages = []
    if JOB.get("system"):
        messages.append({"role": "system", "content": JOB["system"]})
    messages.append({"role": "user", "content": JOB["prompt"]})
    body = {
        "messages": messages,
        "max_tokens": JOB["max_tokens"],
        "temperature": 0.6,
        "chat_template_kwargs": {"enable_thinking": bool(JOB.get("thinking"))},
    }
    t0 = time.time()
    r = requests.post("http://127.0.0.1:8080/v1/chat/completions", json=body, timeout=3600)
    dt = time.time() - t0
    data = r.json()
    with open(os.path.join(OUT, "result.json"), "w", encoding="utf-8") as f:
        json.dump(data, f, ensure_ascii=False, indent=2)

    msg = data["choices"][0]["message"]
    text = msg.get("content") or ""
    reasoning = msg.get("reasoning_content") or ""
    usage = data.get("usage", {})
    out = text
    if reasoning:
        out += "\n\n---\n(reasoning)\n" + reasoning
    out += f"\n\n---\n{dt:.1f}s, usage: {usage}"
    with open(os.path.join(OUT, "result.txt"), "w", encoding="utf-8") as f:
        f.write(out)
    print(out, flush=True)

    proc.terminate()
    try:
        proc.wait(timeout=30)
    except subprocess.TimeoutExpired:
        proc.kill()


if __name__ == "__main__":
    main()
