"""בונה תיקיית kernel מהקובץ kaggle/runner.py, מזריק לו את פרטי המשימה ודוחף ל-Kaggle."""
import json
import os
import pathlib
import re
import shutil
import subprocess
import sys

SLUG = "llm-runner"


def main() -> None:
    user = os.environ["KAGGLE_USERNAME"]
    job = {
        "prompt": os.environ["IN_PROMPT"],
        "system": os.environ.get("IN_SYSTEM", ""),
        "model_repo": os.environ["IN_MODEL_REPO"],
        "model_pattern": os.environ["IN_MODEL_PATTERN"],
        "max_tokens": int(os.environ.get("IN_MAX_TOKENS") or 1024),
        "thinking": os.environ.get("IN_THINKING", "false").lower() == "true",
        "release_repo": os.environ["RELEASE_REPO"],
    }

    push_dir = pathlib.Path("/tmp/kaggle-push")
    shutil.rmtree(push_dir, ignore_errors=True)
    push_dir.mkdir(parents=True)

    src = pathlib.Path("kaggle/runner.py").read_text(encoding="utf-8")
    (push_dir / "runner.py").write_text("JOB = " + repr(job) + "\n\n" + src, encoding="utf-8")

    meta = {
        "id": f"{user}/{SLUG}",
        "title": SLUG,
        "code_file": "runner.py",
        "language": "python",
        "kernel_type": "script",
        "is_private": "true",
        "enable_gpu": "true",
        "enable_internet": "true",
        "dataset_sources": [],
        "competition_sources": [],
        "kernel_sources": [],
        "model_sources": [],
    }
    (push_dir / "kernel-metadata.json").write_text(json.dumps(meta, indent=2), encoding="utf-8")

    cmd = ["kaggle", "kernels", "push", "-p", str(push_dir)]
    acc = os.environ.get("IN_ACCELERATOR", "").strip()
    if acc:
        cmd += ["--accelerator", acc]
    print("Running:", " ".join(cmd), flush=True)
    res = subprocess.run(cmd, capture_output=True, text=True)
    print(res.stdout)
    print(res.stderr, file=sys.stderr)
    if res.returncode != 0:
        sys.exit(res.returncode)

    # ה-ref האמיתי של ה-Kernel נלקח מה-URL ש-Kaggle מחזיר (לא מההנחה ששם המשתמש תקין)
    m = re.search(r"kaggle\.com/code/([^/\s]+)/([^/\s?#]+)", res.stdout + res.stderr)
    ref = f"{m.group(1)}/{m.group(2)}" if m else f"{user}/{SLUG}"
    print("Kernel ref:", ref)

    gh_out = os.environ.get("GITHUB_OUTPUT")
    if gh_out:
        with open(gh_out, "a", encoding="utf-8") as f:
            f.write(f"ref={ref}\n")
    print("Pushed:", ref)


if __name__ == "__main__":
    try:
        main()
    except subprocess.CalledProcessError as e:
        sys.exit(e.returncode)
