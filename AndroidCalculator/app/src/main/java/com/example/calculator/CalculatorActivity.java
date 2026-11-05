package com.example.calculator;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class CalculatorActivity extends AppCompatActivity {

    private TextView display;
    private String currentInput = "";
    private String operator = "";
    private double firstValue = 0;
    private boolean isNewOperation = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);
        
        display = findViewById(R.id.display);
        
        // מספרים
        findViewById(R.id.btn7).setOnClickListener(onClickListener);
        findViewById(R.id.btn8).setOnClickListener(onClickListener);
        findViewById(R.id.btn9).setOnClickListener(onClickListener);
        findViewById(R.id.btn4).setOnClickListener(onClickListener);
        findViewById(R.id.btn5).setOnClickListener(onClickListener);
        findViewById(R.id.btn6).setOnClickListener(onClickListener);
        findViewById(R.id.btn1).setOnClickListener(onClickListener);
        findViewById(R.id.btn2).setOnClickListener(onClickListener);
        findViewById(R.id.btn3).setOnClickListener(onClickListener);
        findViewById(R.id.btn0).setOnClickListener(onClickListener);
        findViewById(R.id.btnDot).setOnClickListener(onClickListener);
        
        // פעולות
        findViewById(R.id.btnPlus).setOnClickListener(onClickListener);
        findViewById(R.id.btnMinus).setOnClickListener(onClickListener);
        findViewById(R.id.btnMultiply).setOnClickListener(onClickListener);
        findViewById(R.id.btnDivide).setOnClickListener(onClickListener);
        findViewById(R.id.btnEquals).setOnClickListener(onClickListener);
        
        // פונקציות
        findViewById(R.id.btnClear).setOnClickListener(onClickListener);
        findViewById(R.id.btnDelete).setOnClickListener(onClickListener);
        
        // חזרה
        findViewById(R.id.btnBack).setOnClickListener(onClickListener);
    }
    
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            
            if (id == R.id.btn7 || id == R.id.btn8 || id == R.id.btn9 ||
                id == R.id.btn4 || id == R.id.btn5 || id == R.id.btn6 ||
                id == R.id.btn1 || id == R.id.btn2 || id == R.id.btn3 ||
                id == R.id.btn0 || id == R.id.btnDot) {
                
                String number = ((Button)v).getText().toString();
                if (id == R.id.btnDot && currentInput.contains(".")) return;
                
                if (isNewOperation) {
                    currentInput = "";
                    isNewOperation = false;
                }
                
                if (number.equals(".") && currentInput.isEmpty()) {
                    currentInput = "0.";
                } else {
                    currentInput += number;
                }
                display.setText(currentInput);
            }
            else if (id == R.id.btnPlus || id == R.id.btnMinus || 
                     id == R.id.btnMultiply || id == R.id.btnDivide) {
                
                if (!currentInput.isEmpty()) {
                    if (!operator.isEmpty() && !isNewOperation) {
                        calculate();
                    } else {
                        firstValue = Double.parseDouble(currentInput);
                    }
                    operator = ((Button)v).getText().toString();
                    isNewOperation = true;
                }
            }
            else if (id == R.id.btnEquals) {
                if (!operator.isEmpty() && !currentInput.isEmpty()) {
                    double secondValue = Double.parseDouble(currentInput);
                    calculate();
                    operator = "";
                    isNewOperation = true;
                }
            }
            else if (id == R.id.btnClear) {
                currentInput = "";
                operator = "";
                firstValue = 0;
                isNewOperation = true;
                display.setText("0");
            }
            else if (id == R.id.btnDelete) {
                if (!currentInput.isEmpty()) {
                    currentInput = currentInput.substring(0, currentInput.length() - 1);
                    display.setText(currentInput.isEmpty() ? "0" : currentInput);
                }
            }
            else if (id == R.id.btnBack) {
                finish();
            }
        }
    };
    
    private void calculate() {
        double result = 0;
        double secondValue = Double.parseDouble(currentInput);
        
        switch (operator) {
            case "+":
                result = firstValue + secondValue;
                break;
            case "-":
                result = firstValue - secondValue;
                break;
            case "×":
                result = firstValue * secondValue;
                break;
            case "÷":
                if (secondValue == 0) {
                    display.setText("שגיאה");
                    currentInput = "";
                    operator = "";
                    return;
                }
                result = firstValue / secondValue;
                break;
        }
        
        currentInput = String.valueOf(result);
        display.setText(currentInput);
    }
}