# 🧮 מחשבון קטן ויפה ב-C++

אפליקציית מחשבון Console עם ממשק נקי בעברית.

## ✨ תכונות

- ➕ חיבור, ➖ חיסור, ✖️ כפל, ➗ חילוק
- 🔢 חזקות ו-√ שורש ריבועי
- 📊 סטטיסטיקה (ממוצע, מקסימום, מינימום)
- 📐 פונקציות מתמטיות (sin, cos, tan)
- 🔄 ממיר יחידות (טמפרטורה, אורך, משקל)
- 🛡️ טיפול שגיאות - לא מחלקים באפס, שורש שלילי וכו'

## 🛠️ בנייה

### דרך CMake (מומלץ)

```bash
cd calculator
mkdir build && cd build
cmake ..
cmake --build .
```

### דרך g++ ישירות

```bash
g++ -std=c++17 -o Calculator.exe Calculator.cpp
```

### דרך Visual Studio (MSVC)

```bash
cd calculator
mkdir build && cd build
cmake .. -G "Visual Studio 17 2022"
cmake --build . --config Release
```

## 🚀 הרצה

```bash
./Calculator.exe
```

או פשוט לחץ כפול על `Calculator.exe`

## 📝 דרישות

- C++17 ומעלה
- CMake 3.10 ומעלה (אופציונלי)
- Windows 10/11 (מומלץ)
