import sys
import os
import platform

print("=" * 50)
print("  Hello from Python on GitHub Actions!")
print("=" * 50)
print()

# מידע על הסביבה
print("Environment Info:")
print(f"  Python Version: {sys.version}")
print(f"  Platform: {platform.platform()}")
print(f"  System: {platform.system()} {platform.release()}")
print(f"  Machine: {platform.machine()}")
print(f"  Node: {platform.node()}")
print(f"  Working Dir: {os.getcwd()}")
print()

# יצירת מספר אקראי
import random
random_num = random.randint(1, 100)
print(f"Random number: {random_num}")
print()

# בדיקת קבצים בסביבה
print("Files in current directory:")
for f in sorted(os.listdir('.')):
    size = os.path.getsize(f)
    print(f"  {f} ({size} bytes)")
print()

print("Done! ✓")
