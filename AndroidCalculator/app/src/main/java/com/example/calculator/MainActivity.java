package com.example.calculator;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView displayTextView;
    private StringBuilder currentInput = new StringBuilder();
    private double firstOperand = 0;
    private String operator = "";
    private boolean isNewInput = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        displayTextView = findViewById(R.id.displayTextView);

        // Find all buttons and set click listeners
        Button btn0 = findViewById(R.id.btn0);
        Button btn1 = findViewById(R.id.btn1);
        Button btn2 = findViewById(R.id.btn2);
        Button btn3 = findViewById(R.id.btn3);
        Button btn4 = findViewById(R.id.btn4);
        Button btn5 = findViewById(R.id.btn5);
        Button btn6 = findViewById(R.id.btn6);
        Button btn7 = findViewById(R.id.btn7);
        Button btn8 = findViewById(R.id.btn8);
        Button btn9 = findViewById(R.id.btn9);
        Button btnAdd = findViewById(R.id.btnAdd);
        Button btnSubtract = findViewById(R.id.btnSubtract);
        Button btnMultiply = findViewById(R.id.btnMultiply);
        Button btnDivide = findViewById(R.id.btnDivide);
        Button btnEquals = findViewById(R.id.btnEquals);
        Button btnClear = findViewById(R.id.btnClear);
        Button btnDelete = findViewById(R.id.btnDelete);
        Button btnDecimal = findViewById(R.id.btnDecimal);
        Button btnPercent = findViewById(R.id.btnPercent);

        // Set click listeners
        btn0.setOnClickListener(this);
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        btn4.setOnClickListener(this);
        btn5.setOnClickListener(this);
        btn6.setOnClickListener(this);
        btn7.setOnClickListener(this);
        btn8.setOnClickListener(this);
        btn9.setOnClickListener(this);
        btnAdd.setOnClickListener(this);
        btnSubtract.setOnClickListener(this);
        btnMultiply.setOnClickListener(this);
        btnDivide.setOnClickListener(this);
        btnEquals.setOnClickListener(this);
        btnClear.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        btnDecimal.setOnClickListener(this);
        btnPercent.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.btn0) {
            appendToDisplay("0");
        } else if (id == R.id.btn1) {
            appendToDisplay("1");
        } else if (id == R.id.btn2) {
            appendToDisplay("2");
        } else if (id == R.id.btn3) {
            appendToDisplay("3");
        } else if (id == R.id.btn4) {
            appendToDisplay("4");
        } else if (id == R.id.btn5) {
            appendToDisplay("5");
        } else if (id == R.id.btn6) {
            appendToDisplay("6");
        } else if (id == R.id.btn7) {
            appendToDisplay("7");
        } else if (id == R.id.btn8) {
            appendToDisplay("8");
        } else if (id == R.id.btn9) {
            appendToDisplay("9");
        } else if (id == R.id.btnAdd) {
            setOperator("+");
        } else if (id == R.id.btnSubtract) {
            setOperator("-");
        } else if (id == R.id.btnMultiply) {
            setOperator("×");
        } else if (id == R.id.btnDivide) {
            setOperator("÷");
        } else if (id == R.id.btnEquals) {
            calculateResult();
        } else if (id == R.id.btnClear) {
            clearDisplay();
        } else if (id == R.id.btnDelete) {
            deleteLastChar();
        } else if (id == R.id.btnDecimal) {
            appendToDisplay(".");
        } else if (id == R.id.btnPercent) {
            appendToDisplay("%");
        }
    }

    private void appendToDisplay(String value) {
        if (isNewInput) {
            if (value.equals(".") || isOperator(value)) {
                currentInput.setLength(0);
                if (value.equals(".")) {
                    currentInput.append("0.");
                } else {
                    currentInput.append(value);
                }
            } else {
                currentInput.setLength(0);
                isNewInput = false;
                currentInput.append(value);
            }
        } else {
            if (value.equals(".") && currentInput.toString().contains(".")) {
                return;
            }
            currentInput.append(value);
        }
        displayTextView.setText(currentInput.toString());
    }

    private void setOperator(String op) {
        if (!currentInput.toString().isEmpty()) {
            if (operator != null && !operator.isEmpty() && !isNewInput) {
                calculateResult();
            }
            firstOperand = Double.parseDouble(currentInput.toString());
            operator = op;
            isNewInput = true;
        }
    }

    private void calculateResult() {
        if (operator != null && !operator.isEmpty() && !currentInput.toString().isEmpty()) {
            double secondOperand = Double.parseDouble(currentInput.toString());
            double result = 0;

            switch (operator) {
                case "+":
                    result = firstOperand + secondOperand;
                    break;
                case "-":
                    result = firstOperand - secondOperand;
                    break;
                case "×":
                    result = firstOperand * secondOperand;
                    break;
                case "÷":
                    if (secondOperand != 0) {
                        result = firstOperand / secondOperand;
                    } else {
                        displayTextView.setText("שגיאה");
                        currentInput.setLength(0);
                        operator = "";
                        isNewInput = true;
                        return;
                    }
                    break;
                default:
                    return;
            }

            // Format result
            String resultString;
            if (result == (long) result) {
                resultString = String.valueOf((long) result);
            } else {
                resultString = String.valueOf(result);
            }

            // Limit length to prevent overflow
            if (resultString.length() > 12) {
                resultString = String.format("%.6g", result);
            }

            currentInput.setLength(0);
            currentInput.append(resultString);
            displayTextView.setText(currentInput.toString());
            operator = "";
            isNewInput = true;
        }
    }

    private void clearDisplay() {
        currentInput.setLength(0);
        firstOperand = 0;
        operator = "";
        isNewInput = true;
        displayTextView.setText("0");
    }

    private void deleteLastChar() {
        if (currentInput.length() > 0) {
            currentInput.deleteCharAt(currentInput.length() - 1);
            if (currentInput.toString().isEmpty()) {
                isNewInput = true;
            }
            displayTextView.setText(currentInput.toString().isEmpty() ? "0" : currentInput.toString());
        }
    }

    private boolean isOperator(String value) {
        return value.equals("+") || value.equals("-") || value.equals("×") || value.equals("÷");
    }
}
