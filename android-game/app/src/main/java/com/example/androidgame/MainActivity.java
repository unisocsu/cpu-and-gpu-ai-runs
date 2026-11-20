package com.example.androidgame;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.Random;

/**
 * משחק "משימה למצוא" - אובייקט נע על המסך והמשתמש צריך ללחוץ עליו.
 * מבוסס על מנוע C++ דרך JNI.
 */
public class MainActivity extends Activity {
    
    private static final String TAG = "AndroidGame";
    private GameView gameView;
    private TextView scoreView;
    private TextView timeView;
    private TextView levelView;
    
    private int score = 0;
    private int level = 1;
    private int timeLeft = 30;
    private boolean isRunning = false;
    private Handler handler;
    private Runnable timerRunnable;
    
    // JNI - מנוע C++
    static {
        System.loadLibrary("game-engine");
    }
    
    // פונקציות JNI
    private native long nativeInit(int width, int height);
    private native void nativeUpdate(long nativePtr, float deltaTime);
    private native void nativeDraw(long nativePtr, int canvasPtr);
    private native void nativeClick(long nativePtr, float x, float y);
    private native void nativeRelease(long nativePtr);
    private native int nativeGetScore(long nativePtr);
    private native int nativeGetLevel(long nativePtr);
    private native int nativeGetTargetX(long nativePtr);
    private native int nativeGetTargetY(long nativePtr);
    private native int nativeGetTargetRadius(long nativePtr);
    private native boolean nativeIsTargetHit(long nativePtr);
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // מסך מלא ללא כותרת
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        
        handler = new Handler(Looper.getMainLooper());
        
        // יצירת מסך המשחק
        gameView = new GameView(this);
        setContentView(gameView);
        
        // התחלת המשחק
        startGame();
    }
    
    private void startGame() {
        score = 0;
        level = 1;
        timeLeft = 30;
        isRunning = true;
        
        // אתחול מנוע C++
        long nativePtr = nativeInit(gameView.getWidth(), gameView.getHeight());
        gameView.setNativePtr(nativePtr);
        
        // אתחול טיימר
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                if (isRunning && timeLeft > 0) {
                    timeLeft--;
                    updateUI();
                    
                    if (timeLeft <= 0) {
                        endGame();
                    } else {
                        handler.postDelayed(this, 1000);
                    }
                }
            }
        };
        handler.post(timerRunnable);
        
        Log.d(TAG, "Game started! Score: 0, Level: 1, Time: " + timeLeft);
    }
    
    private void endGame() {
        isRunning = false;
        handler.removeCallbacks(timerRunnable);
        
        long nativePtr = gameView.getNativePtr();
        if (nativePtr != 0) {
            nativeRelease(nativePtr);
            gameView.setNativePtr(0);
        }
        
        // הצגת מסך סיום
        gameView.showGameOver(score, level);
        
        Log.d(TAG, "Game over! Final Score: " + score + ", Level: " + level);
    }
    
    private void updateUI() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (scoreView != null) scoreView.setText("ניקוד: " + score);
                if (timeView != null) timeView.setText("זמן: " + timeLeft);
                if (levelView != null) levelView.setText("רמה: " + level);
            }
        });
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        isRunning = false;
        handler.removeCallbacks(timerRunnable);
        
        long nativePtr = gameView.getNativePtr();
        if (nativePtr != 0) {
            nativeRelease(nativePtr);
            gameView.setNativePtr(0);
        }
    }
    
    /**
     * View ראשי של המשחק - מצייר את האובייקטים ומטפל במגע
     */
    public class GameView extends View {
        
        private Paint paint;
        private Paint targetPaint;
        private Paint backgroundPaint;
        private long nativePtr = 0;
        private Random random;
        
        // נתוני ממשק משתמש
        private TextView tvScore;
        private TextView tvTime;
        private TextView tvLevel;
        private TextView tvGameOver;
        private boolean showGameOver = false;
        
        public GameView(Context context) {
            super(context);
            init();
        }
        
        public GameView(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }
        
        private void init() {
            random = new Random();
            
            // צבעים
            backgroundPaint = new Paint();
            backgroundPaint.setColor(Color.argb(255, 20, 30, 50));
            
            paint = new Paint();
            paint.setColor(Color.WHITE);
            paint.setTextSize(48);
            paint.setTextAlign(Paint.Align.CENTER);
            
            targetPaint = new Paint();
            targetPaint.setColor(Color.argb(255, 255, 100, 100));
            targetPaint.setAntiAlias(true);
            
            // יצירת טקסטים למשחק
            tvScore = new TextView(getContext());
            tvScore.setTextColor(Color.WHITE);
            tvScore.setTextSize(24);
            tvScore.setText("ניקוד: 0");
            
            tvTime = new TextView(getContext());
            tvTime.setTextColor(Color.WHITE);
            tvTime.setTextSize(24);
            tvTime.setText("זמן: 30");
            
            tvLevel = new TextView(getContext());
            tvLevel.setTextColor(Color.WHITE);
            tvLevel.setTextSize(24);
            tvLevel.setText("רמה: 1");
            
            tvGameOver = new TextView(getContext());
            tvGameOver.setTextColor(Color.YELLOW);
            tvGameOver.setTextSize(32);
            tvGameOver.setVisibility(View.GONE);
            tvGameOver.setText("סוף משחק!\nלחץ כדי להתחיל מחדש");
            tvGameOver.setGravity(android.view.Gravity.CENTER);
        }
        
        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            Log.d(TAG, "View size: " + w + "x" + h);
        }
        
        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            
            // ציור רקע
            canvas.drawPaint(backgroundPaint);
            
            if (showGameOver) {
                tvGameOver.setVisibility(View.VISIBLE);
                tvGameOver.draw(canvas);
                return;
            }
            
            // ציור ממשק משתמש
            tvScore.measure(MeasureSpec.makeMeasureSpec(getWidth(), MeasureSpec.EXACTLY),
                           MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            tvTime.measure(MeasureSpec.makeMeasureSpec(getWidth(), MeasureSpec.EXACTLY),
                          MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            tvLevel.measure(MeasureSpec.makeMeasureSpec(getWidth(), MeasureSpec.EXACTLY),
                           MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            
            tvScore.layout(0, 0, getWidth(), tvScore.getMeasuredHeight());
            tvTime.layout(0, tvScore.getMeasuredHeight(), getWidth(), 
                         tvScore.getMeasuredHeight() + tvTime.getMeasuredHeight());
            tvLevel.layout(0, tvScore.getMeasuredHeight() + tvTime.getMeasuredHeight(), 
                          getWidth(), tvScore.getMeasuredHeight() + tvTime.getMeasuredHeight() + tvLevel.getMeasuredHeight());
            
            tvScore.draw(canvas);
            tvTime.draw(canvas);
            tvLevel.draw(canvas);
            
            // ציור אובייקט המשחק דרך מנוע C++
            if (nativePtr != 0) {
                int targetX = nativeGetTargetX(nativePtr);
                int targetY = nativeGetTargetY(nativePtr);
                int targetRadius = nativeGetTargetRadius(nativePtr);
                
                if (targetX > 0 && targetY > 0 && targetRadius > 0) {
                    canvas.drawCircle(targetX, targetY, targetRadius, targetPaint);
                    
                    // הוספת אפקט זוהר
                    Paint glowPaint = new Paint();
                    glowPaint.setColor(Color.argb(100, 255, 100, 100));
                    glowPaint.setAntiAlias(true);
                    canvas.drawCircle(targetX, targetY, targetRadius * 1.5f, glowPaint);
                }
            }
            
            // עדכון רינדור
            invalidate();
        }
        
        @Override
        public boolean onTouchEvent(MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                float x = event.getX();
                float y = event.getY();
                
                if (showGameOver) {
                    // התחלה מחדש
                    showGameOver = false;
                    tvGameOver.setVisibility(View.GONE);
                    startGame();
                    return true;
                }
                
                if (nativePtr != 0) {
                    nativeClick(nativePtr, x, y);
                    
                    if (nativeIsTargetHit(nativePtr)) {
                        score++;
                        level = score / 5 + 1;
                        updateUI();
                        
                        // אם עברנו לרמה חדשה, הוסף זמן
                        if (score % 5 == 0) {
                            timeLeft += 5;
                            updateUI();
                        }
                    }
                }
            }
            return true;
        }
        
        public void setNativePtr(long ptr) {
            this.nativePtr = ptr;
        }
        
        public long getNativePtr() {
            return nativePtr;
        }
        
        public void showGameOver(int finalScore, int finalLevel) {
            showGameOver = true;
            tvGameOver.setText("סוף משחק!\nניקוד: " + finalScore + "\nרמה: " + finalLevel + "\n\nלחץ כדי להתחיל מחדש");
            tvGameOver.setVisibility(View.VISIBLE);
        }
    }
}
