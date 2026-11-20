#ifndef GAME_ENGINE_H
#define GAME_ENGINE_H

#include <cstdint>

#ifdef __cplusplus
extern "C" {
#endif

/**
 * אתחול מנוע המשחק
 * @param width רוחב המסך
 * @param height גובה המסך
 * @return מצביע למבנה המנוע
 */
void* nativeInit(int width, int height);

/**
 * עדכון לוגיקת המשחק
 * @param nativePtr מצביע למבנה המנוע
 * @param deltaTime זמן שעבר בין עדכונים
 */
void nativeUpdate(void* nativePtr, float deltaTime);

/**
 * ציור המשחק
 * @param nativePtr מצביע למבנה המנוע
 * @param canvasPtr מצביע לקנווס
 */
void nativeDraw(void* nativePtr, int canvasPtr);

/**
 * טיפול בלחיצה
 * @param nativePtr מצביע למבנה המנוע
 * @param x קואורדינטת X
 * @param y קואורדינטת Y
 */
void nativeClick(void* nativePtr, float x, float y);

/**
 * שחרור משאבים
 * @param nativePtr מצביע למבנה המנוע
 */
void nativeRelease(void* nativePtr);

/**
 * קבלת ניקוד
 * @param nativePtr מצביע למבנה המנוע
 * @return הניקוד הנוכחי
 */
int nativeGetScore(void* nativePtr);

/**
 * קבלת רמה
 * @param nativePtr מצביע למבנה המנוע
 * @return הרמה הנוכחית
 */
int nativeGetLevel(void* nativePtr);

/**
 * קבלת מיקום X של המטרה
 * @param nativePtr מצביע למבנה המנוע
 * @return קואורדינטת X
 */
int nativeGetTargetX(void* nativePtr);

/**
 * קבלת מיקום Y של המטרה
 * @param nativePtr מצביע למבנה המנוע
 * @return קואורדינטת Y
 */
int nativeGetTargetY(void* nativePtr);

/**
 * קבלת רדיוס המטרה
 * @param nativePtr מצביע למבנה המנוע
 * @return הרדיוס
 */
int nativeGetTargetRadius(void* nativePtr);

/**
 * בדיקה אם המטרה נפגעה
 * @param nativePtr מצביע למבנה המנוע
 * @return true אם נפגעה, false אחרת
 */
int nativeIsTargetHit(void* nativePtr);

#ifdef __cplusplus
}
#endif

#endif // GAME_ENGINE_H
