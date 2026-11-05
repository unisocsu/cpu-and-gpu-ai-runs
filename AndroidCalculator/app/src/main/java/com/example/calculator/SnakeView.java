package com.example.calculator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Random;

public class SnakeView extends View {

    private static final int GRID_SIZE = 20;
    private static final int GAME_SPEED = 150; // milliseconds

    private enum Direction { UP, DOWN, LEFT, RIGHT }
    
    private ArrayList<int[]> snake;
    private int[] food;
    private Direction currentDirection = Direction.RIGHT;
    private Direction nextDirection = Direction.RIGHT;
    private boolean isRunning = false;
    private boolean isGameOver = false;
    
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable gameLoop;
    
    private Paint snakePaint;
    private Paint foodPaint;
    private Paint backgroundPaint;
    private Paint textPaint;
    private Paint gridPaint;
    
    private int gridWidth;
    private int gridHeight;
    
    private int score = 0;
    private SnakeActivity activity;

    public SnakeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SnakeView(Context context) {
        super(context);
        init();
    }

    private void init() {
        snake = new ArrayList<>();
        
        snakePaint = new Paint();
        snakePaint.setColor(Color.GREEN);
        snakePaint.setStyle(Paint.Style.FILL);
        
        foodPaint = new Paint();
        foodPaint.setColor(Color.RED);
        foodPaint.setStyle(Paint.Style.FILL);
        
        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.parseColor("#1a1a2e"));
        
        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(40);
        textPaint.setTextAlign(Paint.Align.CENTER);
        
        gridPaint = new Paint();
        gridPaint.setColor(Color.parseColor("#16213e"));
        gridPaint.setStrokeWidth(1);
        
        initializeGame();
    }

    private void initializeGame() {
        snake.clear();
        // Start with a small snake
        snake.add(new int[]{5, 5});
        snake.add(new int[]{4, 5});
        snake.add(new int[]{3, 5});
        
        spawnFood();
        score = 0;
        isGameOver = false;
        currentDirection = Direction.RIGHT;
        nextDirection = Direction.RIGHT;
    }

    private void spawnFood() {
        Random rand = new Random();
        food = new int[]{
            rand.nextInt(gridWidth),
            rand.nextInt(gridHeight)
        };
        
        // Make sure food doesn't spawn on snake
        for (int[] segment : snake) {
            if (segment[0] == food[0] && segment[1] == food[1]) {
                spawnFood();
                return;
            }
        }
    }

    public void startGame() {
        if (isRunning) return;
        
        initializeGame();
        isRunning = true;
        
        gameLoop = new Runnable() {
            @Override
            public void run() {
                update();
                invalidate();
                
                if (isRunning) {
                    handler.postDelayed(this, GAME_SPEED);
                }
            }
        };
        
        handler.post(gameLoop);
    }

    public void stopGame() {
        isRunning = false;
        if (gameLoop != null) {
            handler.removeCallbacks(gameLoop);
        }
        invalidate();
    }

    private void update() {
        if (!isRunning || isGameOver) return;
        
        currentDirection = nextDirection;
        
        int[] head = snake.get(0);
        int newHeadX = head[0];
        int newHeadY = head[1];
        
        switch (currentDirection) {
            case UP:
                newHeadY--;
                break;
            case DOWN:
                newHeadY++;
                break;
            case LEFT:
                newHeadX--;
                break;
            case RIGHT:
                newHeadX++;
                break;
        }
        
        // Check collision with walls
        if (newHeadX < 0 || newHeadX >= gridWidth || 
            newHeadY < 0 || newHeadY >= gridHeight) {
            gameOver();
            return;
        }
        
        // Check collision with self
        for (int[] segment : snake) {
            if (segment[0] == newHeadX && segment[1] == newHeadY) {
                gameOver();
                return;
            }
        }
        
        // Add new head
        int[] newHead = new int[]{newHeadX, newHeadY};
        snake.add(0, newHead);
        
        // Check if food is eaten
        if (newHeadX == food[0] && newHeadY == food[1]) {
            score += 10;
            if (activity != null) {
                activity.updateScore(score);
            }
            spawnFood();
        } else {
            // Remove tail
            snake.remove(snake.size() - 1);
        }
    }

    private void gameOver() {
        isGameOver = true;
        isRunning = false;
        if (activity != null) {
            activity.gameOver();
        }
    }

    public void changeDirection(String direction) {
        switch (direction) {
            case "UP":
                if (currentDirection != Direction.DOWN) {
                    nextDirection = Direction.UP;
                }
                break;
            case "DOWN":
                if (currentDirection != Direction.UP) {
                    nextDirection = Direction.DOWN;
                }
                break;
            case "LEFT":
                if (currentDirection != Direction.RIGHT) {
                    nextDirection = Direction.LEFT;
                }
                break;
            case "RIGHT":
                if (currentDirection != Direction.LEFT) {
                    nextDirection = Direction.RIGHT;
                }
                break;
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        gridWidth = w / GRID_SIZE;
        gridHeight = h / GRID_SIZE;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        // Draw background
        canvas.drawColor(Color.parseColor("#1a1a2e"));
        
        // Draw grid
        drawGrid(canvas);
        
        // Draw snake
        drawSnake(canvas);
        
        // Draw food
        drawFood(canvas);
        
        // Draw game over message
        if (isGameOver) {
            drawGameOver(canvas);
        }
    }

    private void drawGrid(Canvas canvas) {
        for (int i = 0; i <= gridWidth; i++) {
            canvas.drawLine(i * GRID_SIZE, 0, i * GRID_SIZE, getHeight(), gridPaint);
        }
        
        for (int i = 0; i <= gridHeight; i++) {
            canvas.drawLine(0, i * GRID_SIZE, getWidth(), i * GRID_SIZE, gridPaint);
        }
    }

    private void drawSnake(Canvas canvas) {
        for (int i = 0; i < snake.size(); i++) {
            int[] segment = snake.get(i);
            Paint segmentPaint = new Paint();
            
            if (i == 0) {
                // Head is brighter
                segmentPaint.setColor(Color.parseColor("#00ff00"));
            } else {
                // Body gets darker
                int alpha = 255 - (i * 5);
                if (alpha < 100) alpha = 100;
                segmentPaint.setColor(Color.argb(alpha, 0, 200, 0));
            }
            
            segmentPaint.setStyle(Paint.Style.FILL);
            canvas.drawRect(
                segment[0] * GRID_SIZE + 1,
                segment[1] * GRID_SIZE + 1,
                (segment[0] + 1) * GRID_SIZE - 1,
                (segment[1] + 1) * GRID_SIZE - 1,
                segmentPaint
            );
        }
    }

    private void drawFood(Canvas canvas) {
        canvas.drawCircle(
            food[0] * GRID_SIZE + GRID_SIZE / 2,
            food[1] * GRID_SIZE + GRID_SIZE / 2,
            GRID_SIZE / 2 - 2,
            foodPaint
        );
    }

    private void drawGameOver(Canvas canvas) {
        Paint overlayPaint = new Paint();
        overlayPaint.setColor(Color.parseColor("#80000000"));
        canvas.drawRect(0, 0, getWidth(), getHeight(), overlayPaint);
        
        textPaint.setTextSize(60);
        canvas.drawText("GAME OVER", getWidth() / 2, getHeight() / 2 - 30, textPaint);
        
        textPaint.setTextSize(40);
        canvas.drawText("Score: " + score, getWidth() / 2, getHeight() / 2 + 30, textPaint);
    }
}