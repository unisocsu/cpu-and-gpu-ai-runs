package com.example.findthetarget

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.sqrt

class MainActivity : AppCompatActivity() {
    
    private lateinit var gameView: GameView
    private lateinit var scoreTextView: TextView
    private lateinit var timeTextView: TextView
    private lateinit var startButton: Button
    private lateinit var statusTextView: TextView
    
    private val handler = Handler(Looper.getMainLooper())
    private var isGameRunning = false
    private var score = 0
    private var timeLeft = 30
    private val gameTime = 30
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        gameView = findViewById(R.id.gameView)
        scoreTextView = findViewById(R.id.scoreTextView)
        timeTextView = findViewById(R.id.timeTextView)
        startButton = findViewById(R.id.startButton)
        statusTextView = findViewById(R.id.statusTextView)
        
        startButton.setOnClickListener {
            startGame()
        }
        
        gameView.setOnTouchListener { _, event ->
            if (isGameRunning) {
                handleTouch(event.x, event.y)
            }
            true
        }
        
        updateUI()
    }
    
    private fun startGame() {
        score = 0
        timeLeft = gameTime
        isGameRunning = true
        
        startButton.visibility = View.GONE
        statusTextView.text = "לחץ על העיגול האדום!"
        statusTextView.visibility = View.VISIBLE
        
        gameView.reset()
        
        // Timer countdown
        val timer = object : Runnable {
            override fun run() {
                if (timeLeft > 0 && isGameRunning) {
                    timeLeft--
                    updateUI()
                    handler.postDelayed(this, 1000)
                } else {
                    endGame()
                }
            }
        }
        handler.post(timer)
        
        // Start spawning targets
        spawnTarget()
    }
    
    private fun spawnTarget() {
        if (!isGameRunning) return
        
        gameView.spawnTarget()
        
        // Change target position every 1.5 seconds
        val moveRunnable = object : Runnable {
            override fun run() {
                if (isGameRunning) {
                    gameView.moveTarget()
                    handler.postDelayed(this, 1500)
                }
            }
        }
        handler.postDelayed(moveRunnable, 1500)
    }
    
    private fun handleTouch(x: Float, y: Float) {
        if (gameView.isTargetHit(x, y)) {
            score++
            gameView.spawnTarget()
            updateUI()
        }
    }
    
    private fun endGame() {
        isGameRunning = false
        statusTextView.text = "המשחק נגמר! ניקוד סופי: "
        startButton.text = "שחק שוב"
        startButton.visibility = View.VISIBLE
    }
    
    private fun updateUI() {
        scoreTextView.text = "ניקוד: "
        timeTextView.text = "זמן:  שניות"
    }
    
    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}
