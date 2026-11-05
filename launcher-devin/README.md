# Media Launcher - מסך בית למולטימדיה

אפליקציית מסך בית (Launcher) מותאמת למערכות מולטימדיה.

## תכונות
- מסך בית עם וידג'טים (מזג אוויר, שעה, מוזיקה, חדשות)
- חלונית ניווט (Waze/Google Maps)
- מסך "כל האפליקציות" עם ניהול (הסרה, סידור בלחיצה ארוכה)
- תמיכה ב-RTL (עברית)
- minSdk API 19 (Android 4.4 KitKat)

## מבנה הפרויקט
```
launcher-devin/
├── app/
│   ├── src/main/java/com/medialauncher/
│   │   ├── MainActivity.java
│   │   ├── HomeScreenActivity.java
│   │   ├── AllAppsActivity.java
│   │   ├── SettingsActivity.java
│   │   ├── widget/
│   │   ├── adapter/
│   │   └── model/
│   ├── src/main/res/
│   │   ├── layout/
│   │   ├── values/
│   │   └── drawable/
│   └── build.gradle
└── build.gradle
```

## בנייה
```bash
./gradlew assembleDebug
```
