"""ממתין עד ש-Kernel ב-Kaggle מסיים (הצלחה, שגיאה או ביטול)."""
import os
import subprocess
import sys
import time

ref = os.environ["KERNEL_REF"]
FINAL_OK = ("complete",)
FINAL_BAD = ("error", "cancel")
MAX_DENIED = 6  # כמה פעמים ברצף מותר לקבל "אין הרשאה" לפני שמפסיקים


def status() -> str:
    r = subprocess.run(["kaggle", "kernels", "status", ref], capture_output=True, text=True)
    return (r.stdout + r.stderr).strip().lower()


def is_denied(s: str) -> bool:
    # תשובה תקינה מכילה "has status", ושם ה-Kernel עלול להכיל מספרים, לכן לא מחפשים קודי HTTP
    if "has status" in s:
        return False
    return "denied" in s or "cannot access" in s or "unauthorized" in s or "forbidden" in s


def fail_denied(s: str) -> None:
    sys.exit(
        "\nאין הרשאה לבדוק סטטוס של ה-Kernel '" + ref + "'.\n"
        "בדוק: (1) ש-KAGGLE_USERNAME זהה לשם המשתמש ב-URL של המחברת (kaggle.com/code/<שם>/...), "
        "(2) שהאסימון נוצר עם הרשאות מלאות.\n"
        "ההרצה עצמה כנראה רצה ב-Kaggle, אפשר לראות אותה שם.\n\nשגיאה: " + s
    )


# קודם מחכים (עד 4 דקות) לראות שההרצה החדשה התחילה, כדי לא לקרוא סטטוס ישן
denied = 0
start = time.time()
while time.time() - start < 240:
    s = status()
    print("[wait]", s, flush=True)
    denied = denied + 1 if is_denied(s) else 0
    if denied >= MAX_DENIED:
        fail_denied(s)
    if "running" in s or "queued" in s:
        break
    if any(x in s for x in FINAL_BAD) and not is_denied(s):
        sys.exit("Kaggle run failed to start: " + s)
    time.sleep(20)

deadline = time.time() + 5.5 * 3600
while time.time() < deadline:
    s = status()
    print("[wait]", s, flush=True)
    denied = denied + 1 if is_denied(s) else 0
    if denied >= MAX_DENIED:
        fail_denied(s)
    if not is_denied(s):
        if any(x in s for x in FINAL_BAD):
            sys.exit("Kaggle run failed: " + s)
        if any(x in s for x in FINAL_OK):
            print("Kaggle run completed.")
            sys.exit(0)
    time.sleep(30)

sys.exit("Timed out waiting for Kaggle")
