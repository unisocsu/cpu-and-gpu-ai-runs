# kaggle-llm-runner

הרצת מודל שפה על GPU חינמי של Kaggle (T4), כשהכול מופעל מ-GitHub Actions.

## התקנה חד-פעמית

1. **Secrets בריפו** (Settings → Secrets and variables → Actions):
   - `KAGGLE_USERNAME` – שם המשתמש שלך ב-Kaggle
   - `KAGGLE_API_TOKEN` – אסימון ה-API (kaggle.com/settings/api → Generate New Token, מוצג פעם אחת)
2. ב-Kaggle: אימות טלפון בחשבון (בלי זה אין GPU).
3. Actions → **Build llama.cpp (CUDA, T4)** → Run workflow (כ-10-20 דקות, פעם אחת).

## שימוש

Actions → **Run on Kaggle** → Run workflow → כתוב prompt.
התשובה מופיעה ב-Summary של ההרצה, והפלט המלא ב-artifact בשם `kaggle-output`.

הריצה הראשונה כוללת הורדת מודל (כמה דקות) והפעלת השרת, ולכן היא לוקחת זמן.

## חשוב

- הריפו ציבורי: ה-prompt והתשובה גלויים לכולם בלוג של ההרצה. אל תכניס מידע פרטי.
- ה-Kernel עצמו ב-Kaggle פרטי.
- המכסה של Kaggle: כ-30 שעות GPU בשבוע, ועד 12 שעות לריצה.
- אם `NvidiaTeslaT4` נותן GPU אחד בלבד, נסה `NvidiaTeslaT4Highmem` בשדה accelerator.
