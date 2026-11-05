package com.example.calculator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Random;

public class SnakeActivity extends AppCompatActivity {

    private SnakeView snakeView;
    private TextView scoreView;
    private Button btnStart, btnBack;
    private int score = 0;
    private boolean isRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snake);
        
        snakeView = findViewById(R.id.snakeView);
        scoreView = findViewById(R.id.scoreText);
        btnStart = findViewById(R.id.btnStart);
        btnBack = findViewById(R.id.btnBack);
        
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRunning) {
                    snakeView.startGame();
                    isRunning = true;
                    btnStart.setText("Stop");
                } else {
                    snakeView.stopGame();
                    isRunning = false;
                    btnStart.setText("Start");
                }
            }
        });
        
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snakeView.stopGame();
                finish();
            }
        });
        
        // Touch controls
        snakeView.setOnTouchListener(new View.OnTouchListener() {
            private float startX, startY;
            
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    startX = event.getX();
                    startY = event.getY();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    float endX = event.getX();
                    float endY = event.getY();
                    
                    float deltaX = endX - startX;
                    float deltaY = endY - startY;
                    
                    if (Math.abs(deltaX) > Math.abs(deltaY)) {
                        if (deltaX > 0) {
                            snakeView.changeDirection("RIGHT");
                        } else {
                            snakeView.changeDirection("LEFT");
                        }
                    } else {
                        if (deltaY > 0) {
                            snakeView.changeDirection("DOWN");
                        } else {
                            snakeView.changeDirection("UP");
                        }
                    }
                }
                return true;
            }
        });
    }
    
    public void updateScore(int newScore) {
        score = newScore;
        scoreView.setText("Score: " + score);
    }
    
    public void gameOver() {
        isRunning = false;
        btnStart.setText("Start");
    }
}