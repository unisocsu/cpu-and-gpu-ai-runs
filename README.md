# cpu-and-gpu-ai-runs

מודל שפה (Qwen3.6-35B-A3B) שרץ על GPU חינמי של Kaggle, כשהכול מופעל ומנוהל מ-GitHub:
- **Run on Kaggle** – משימה אחת, תשובה ב-Summary, קבצי קוד שחולצו עם קישור הורדה.
- **צ'אט** – דף ב-GitHub Pages. ההודעות נשמרות כתגובות ב-Issues, וworker ב-Kaggle עונה להן.

## התקנה חד-פעמית

1. **Secrets בריפו** (Settings → Secrets and variables → Actions):
   - `KAGGLE_USERNAME` – שם המשתמש (handle) ב-Kaggle, כפי שהוא מופיע ב-`kaggle.com/code/<שם>/...`
   - `KAGGLE_API_TOKEN` – אסימון (kaggle.com/settings/api → Generate New Token)
2. ב-Kaggle: אימות טלפון בחשבון (בלי זה אין GPU).
3. Actions → **Build llama.cpp (CUDA, T4)** → Run workflow (פעם אחת, כ-25 דקות).
4. **GitHub Pages:** Settings → Pages → Source: *Deploy from a branch* → Branch: `main`, תיקייה `/docs`.
   הדף יהיה ב-`https://<משתמש>.github.io/<ריפו>/`.
5. **טוקן לדף הצ'אט:** github.com/settings/personal-access-tokens/new
   - Repository access: *Only select repositories* ← הריפו הזה בלבד
   - Permissions: **Issues: Read and write**, **Actions: Read and write**, Metadata: Read
   - הדביק אותו בהגדרות הדף (⚙️). הוא נשמר רק ב-localStorage של הדפדפן.

## שימוש

### משימה בודדת
Actions → **Run on Kaggle** → כתוב prompt (והגדל `max_tokens` לדף HTML מלא, 8000 ומעלה).
בסוף ב-Summary: התשובה, וקישור **הורדת קבצי הקוד** (הקישור דורש התחברות ל-GitHub).

### צ'אט
1. פתח את דף ה-Pages ולחץ **הפעל Worker** (או Actions → **Start chat worker**). לוקח כ-4-6 דקות.
2. כשהסימן הופך ל"Worker מחובר", כתוב הודעה. התשובה מגיעה כתגובה ב-Issue.
3. ה-worker נסגר לבד אחרי 30 דקות בלי הודעות (ניתן לשנות), או אחרי כ-5 שעות. אפשר גם ללחוץ **עצור Worker**.
4. הודעה שנשלחה כשה-worker כבוי תיענה כשיופעל.
5. `/think` בתחילת הודעה מפעיל מצב חשיבה.

אפשר גם לכתוב ישירות תגובה ב-Issue עם התווית `chat` (למשל מאפליקציית GitHub בטלפון).

## אבטחה ופרטיות

- **הריפו ציבורי: כל השיחות והקוד גלויים לכולם.** אל תכתוב מידע פרטי.
- ה-worker עונה **רק** להודעות של בעל הריפו. תגובות של אחרים מתעלמים מהן.
- ה-worker מקבל `GITHUB_TOKEN` של ההרצה שהפעילה אותו: מוגבל לריפו, הרשאת Issues בלבד, ופג בסיום ההרצה. הוא מוטמע במחברת Kaggle **הפרטית**. אל תהפוך אותה לציבורית.
- בתצוגה המקדימה של HTML בדף, הקוד רץ ב-iframe מבודד (sandbox) בלי גישה לטוקן.
- מכסת Kaggle: כ-30 שעות GPU בשבוע, עד 12 שעות לסשן. worker פעיל שומר את ה-GPU תפוס גם כשאין הודעות, ולכן יש כיבוי אוטומטי.

## בדיקות מקומיות

```
python tests/test_runner.py        # חילוץ קוד, היסטוריה ולולאת ה-worker מול GitHub ו-LLM מדומים
npm i jsdom && node tests/ui_smoke.js   # דף הצ'אט (רינדור, XSS, שליחה)
```

## תקלות נפוצות

- `409 Conflict` בדחיפה: יש ריצה פעילה של אותה מחברת, או ש-`KAGGLE_USERNAME` שגוי.
- ההפעלה מהדף נכשלת: חסרה הרשאת *Actions: Read and write* בטוקן.
- ב-GPU אחד בלבד: נסה `NvidiaTeslaT4Highmem` בשדה accelerator.
