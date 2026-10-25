"""ממתין עד ש-Kernel ב-Kaggle מסיים (הצלחה, שגיאה או ביטול)."""
import os
import subprocess
import sys
import time

ref = os.environ["KERNEL_REF"]
FINAL_OK = ("complete",)
FINAL_BAD = ("error", "cancel")


def status() -> str:
    r = subprocess.run(["kaggle", "kernels", "status", ref], capture_output=True, text=True)
    return (r.stdout + r.stderr).strip().lower()


# קודם מחכים (עד 4 דקות) לראות שההרצה החדשה התחילה, כדי לא לקרוא סטטוס ישן
start = time.time()
while time.time() - start < 240:
    s = status()
    print("[wait]", s, flush=True)
    if "running" in s or "queued" in s:
        break
    if any(x in s for x in FINAL_BAD):
        sys.exit("Kaggle run failed to start: " + s)
    time.sleep(20)

deadline = time.time() + 5.5 * 3600
while time.time() < deadline:
    s = status()
    print("[wait]", s, flush=True)
    if any(x in s for x in FINAL_BAD):
        sys.exit("Kaggle run failed: " + s)
    if any(x in s for x in FINAL_OK):
        print("Kaggle run completed.")
        sys.exit(0)
    time.sleep(30)

sys.exit("Timed out waiting for Kaggle")
