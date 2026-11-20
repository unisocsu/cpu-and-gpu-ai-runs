package com.example.findthetarget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.math.sqrt
import kotlin.random.Random

class GameView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    
    private val targetPaint = Paint().apply {
        color = Color.RED
        isAntiAlias = true
    }
    
    private val backgroundPaint = Paint().apply {
        color = Color.parseColor("#1a237e")
        isAntiAlias = true
    }
    
    private var targetX = 100f
    private var targetY = 100f
    private val targetRadius = 50f
    
    private var isTargetVisible = false
    
    init {
        background()
    }
    
    private fun background() {
        setBackgroundColor(Color.parseColor("#0d47a1"))
    }
    
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        // Draw background
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), backgroundPaint)
        
        // Draw target if visible
        if (isTargetVisible) {
            canvas.drawCircle(targetX, targetY, targetRadius, targetPaint)
            
            // Draw inner circle
            val innerPaint = Paint().apply {
                color = Color.WHITE
                isAntiAlias = true
            }
            canvas.drawCircle(targetX, targetY, targetRadius * 0.5f, innerPaint)
        }
    }
    
    fun spawnTarget() {
        isTargetVisible = true
        moveTarget()
        invalidate()
    }
    
    fun moveTarget() {
        if (!isTargetVisible) return
        
        // Calculate new position within bounds
        val minX = targetRadius + 20f
        val maxX = width.toFloat() - targetRadius - 20f
        val minY = targetRadius + 20f
        val maxY = height.toFloat() - targetRadius - 20f
        
        targetX = Random.nextFloat() * (maxX - minX) + minX
        targetY = Random.nextFloat() * (maxY - minY) + minY
        
        invalidate()
    }
    
    fun isTargetHit(x: Float, y: Float): Boolean {
        if (!isTargetVisible) return false
        
        val distance = sqrt((x - targetX) * (x - targetX) + (y - targetY) * (y - targetY))
        return distance <= targetRadius
    }
    
    fun reset() {
        isTargetVisible = false
        invalidate()
    }
}
