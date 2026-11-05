package com.example.calculator;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView display;
    private List<String> expression = new ArrayList<>();
    private boolean newInput = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        display = findViewById(R.id.display);

        // Numbers
        findViewById(R.id.btn0).setOnClickListener(v -> onDigitPressed("0"));
        findViewById(R.id.btn1).setOnClickListener(v -> onDigitPressed("1"));
        findViewById(R.id.btn2).setOnClickListener(v -> onDigitPressed("2"));
        findViewById(R.id.btn3).setOnClickListener(v -> onDigitPressed("3"));
        findViewById(R.id.btn4).setOnClickListener(v -> onDigitPressed("4"));
        findViewById(R.id.btn5).setOnClickListener(v -> onDigitPressed("5"));
        findViewById(R.id.btn6).setOnClickListener(v -> onDigitPressed("6"));
        findViewById(R.id.btn7).setOnClickListener(v -> onDigitPressed("7"));
        findViewById(R.id.btn8).setOnClickListener(v -> onDigitPressed("8"));
        findViewById(R.id.btn9).setOnClickListener(v -> onDigitPressed("9"));

        // Operators
        findViewById(R.id.btnPlus).setOnClickListener(v -> onOperatorPressed("+"));
        findViewById(R.id.btnMinus).setOnClickListener(v -> onOperatorPressed("-"));
        findViewById(R.id.btnMultiply).setOnClickListener(v -> onOperatorPressed("*"));
        findViewById(R.id.btnDivide).setOnClickListener(v -> onOperatorPressed("/"));

        // Special
        findViewById(R.id.btnClear).setOnClickListener(v -> onClear());
        findViewById(R.id.btnEquals).setOnClickListener(v -> onEquals());
        findViewById(R.id.btnDot).setOnClickListener(v -> onDot());
        findViewById(R.id.btnDelete).setOnClickListener(v -> onDelete());
    }

    private void onDigitPressed(String digit) {
        if (newInput) {
            expression.clear();
            newInput = false;
        }
        expression.add(digit);
        updateDisplay();
    }

    private void onOperatorPressed(String operator) {
        if (!expression.isEmpty()) {
            expression.add(operator);
            newInput = true;
            updateDisplay();
        }
    }

    private void onDot() {
        if (newInput) {
            expression.add("0.");
            newInput = false;
        } else {
            // Check if last number already has a dot
            StringBuilder currentNumber = new StringBuilder();
            for (int i = expression.size() - 1; i >= 0; i--) {
                String token = expression.get(i);
                if (token.matches("[0-9]+")) {
                    currentNumber.insert(0, token);
                    expression.remove(i);
                } else {
                    break;
                }
            }
            if (!currentNumber.toString().contains(".")) {
                currentNumber.append(".");
                expression.add(currentNumber.toString());
                updateDisplay();
            }
        }
    }

    private void onClear() {
        expression.clear();
        newInput = true;
        updateDisplay();
    }

    private void onDelete() {
        if (!expression.isEmpty()) {
            String last = expression.get(expression.size() - 1);
            if (last.length() > 1) {
                expression.set(expression.size() - 1, last.substring(0, last.length() - 1));
            } else {
                expression.remove(expression.size() - 1);
            }
            updateDisplay();
        }
    }

    private void onEquals() {
        if (expression.isEmpty()) return;
        
        try {
            String result = evaluateExpression(expression);
            expression.clear();
            expression.add(result);
            newInput = true;
            updateDisplay();
        } catch (Exception e) {
            display.setText("Error");
            expression.clear();
            newInput = true;
        }
    }

    private String evaluateExpression(List<String> tokens) {
        // Simple parser for basic arithmetic
        List<Double> numbers = new ArrayList<>();
        List<Character> operators = new ArrayList<>();

        for (String token : tokens) {
            if (token.matches("[0-9.]+")) {
                numbers.add(Double.parseDouble(token));
            } else if (token.equals("+") || token.equals("-")) {
                while (!operators.isEmpty() && hasPrecedence(operators.get(operators.size() - 1), token.charAt(0))) {
                    numbers.push(applyOp(operators.pop(), numbers.pop(), numbers.pop()));
                }
                operators.add(token.charAt(0));
            } else if (token.equals("*") || token.equals("/")) {
                while (!operators.isEmpty() && hasPrecedence(operators.get(operators.size() - 1), token.charAt(0))) {
                    numbers.push(applyOp(operators.pop(), numbers.pop(), numbers.pop()));
                }
                operators.add(token.charAt(0));
            }
        }

        while (!operators.isEmpty()) {
            numbers.push(applyOp(operators.pop(), numbers.pop(), numbers.pop()));
        }

        double result = numbers.pop();
        if (result == (long) result) {
            return String.valueOf((long) result);
        } else {
            return String.valueOf(result);
        }
    }

    private boolean hasPrecedence(char op1, char op2) {
        if (op2 == '+' || op2 == '-') return false;
        if ((op1 == '*' || op1 == '/') && (op2 == '+' || op2 == '-')) return false;
        return true;
    }

    private double applyOp(char op, double b, double a) {
        switch (op) {
            case '+': return a + b;
            case '-': return a - b;
            case '*': return a * b;
            case '/': 
                if (b == 0) throw new ArithmeticException("Division by zero");
                return a / b;
        }
        return 0;
    }

    private void updateDisplay() {
        display.setText(String.join(" ", expression));
    }
}
