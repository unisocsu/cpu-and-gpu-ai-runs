package com.example.calculator;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView displayTextView;
    private StringBuilder expression = new StringBuilder();
    private boolean newInput = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        displayTextView = findViewById(R.id.displayTextView);

        // Number buttons
        setupNumberButton(R.id.btn0, "0");
        setupNumberButton(R.id.btn1, "1");
        setupNumberButton(R.id.btn2, "2");
        setupNumberButton(R.id.btn3, "3");
        setupNumberButton(R.id.btn4, "4");
        setupNumberButton(R.id.btn5, "5");
        setupNumberButton(R.id.btn6, "6");
        setupNumberButton(R.id.btn7, "7");
        setupNumberButton(R.id.btn8, "8");
        setupNumberButton(R.id.btn9, "9");

        // Operator buttons
        setupOperatorButton(R.id.btnAdd, "+");
        setupOperatorButton(R.id.btnSubtract, "-");
        setupOperatorButton(R.id.btnMultiply, "*");
        setupOperatorButton(R.id.btnDivide, "/");

        // Special buttons
        findViewById(R.id.btnClear).setOnClickListener(v -> clearExpression());
        findViewById(R.id.btnDelete).setOnClickListener(v -> deleteLastChar());
        findViewById(R.id.btnEquals).setOnClickListener(v -> calculateResult());
        findViewById(R.id.btnDecimal).setOnClickListener(v -> addDecimal());
        findViewById(R.id.btnPercent).setOnClickListener(v -> calculatePercent());
    }

    private void setupNumberButton(int buttonId, String value) {
        findViewById(buttonId).setOnClickListener(v -> appendNumber(value));
    }

    private void setupOperatorButton(int buttonId, String operator) {
        findViewById(buttonId).setOnClickListener(v -> appendOperator(operator));
    }

    private void appendNumber(String num) {
        if (newInput) {
            expression.setLength(0);
            newInput = false;
        }
        expression.append(num);
        updateDisplay();
    }

    private void appendOperator(String op) {
        if (expression.length() > 0) {
            String lastChar = expression.substring(expression.length() - 1);
            if (lastChar.equals("+") || lastChar.equals("-") || 
                lastChar.equals("*") || lastChar.equals("/")) {
                expression.deleteCharAt(expression.length() - 1);
            }
            expression.append(" " + op + " ");
            newInput = true;
            updateDisplay();
        }
    }

    private void addDecimal() {
        if (newInput) {
            expression.append("0.");
            newInput = false;
        } else {
            // Check if current number already has a decimal
            String[] parts = expression.toString().split("[+\\-*/]");
            String currentNumber = parts[parts.length - 1].trim();
            if (!currentNumber.contains(".")) {
                expression.append(".");
            }
        }
        updateDisplay();
    }

    private void calculateResult() {
        String expr = expression.toString().trim();
        if (expr.isEmpty()) return;

        try {
            double result = evaluateExpression(expr);
            String resultStr = formatResult(result);
            expression.setLength(0);
            expression.append(resultStr);
            newInput = true;
            updateDisplay();
        } catch (Exception e) {
            displayTextView.setText("Error");
            expression.setLength(0);
            newInput = true;
        }
    }

    private double evaluateExpression(String expr) {
        List<String> tokens = tokenize(expr);
        return parseExpression(tokens, new int[]{0});
    }

    private List<String> tokenize(String expr) {
        List<String> tokens = new ArrayList<>();
        StringBuilder currentToken = new StringBuilder();
        
        for (char c : expr.toCharArray()) {
            if (Character.isWhitespace(c)) {
                if (currentToken.length() > 0) {
                    tokens.add(currentToken.toString());
                    currentToken.setLength(0);
                }
            } else {
                currentToken.append(c);
            }
        }
        if (currentToken.length() > 0) {
            tokens.add(currentToken.toString());
        }
        return tokens;
    }

    private double parseExpression(List<String> tokens, int[] pos) {
        double result = parseTerm(tokens, pos);
        
        while (pos[0] < tokens.size()) {
            String op = tokens.get(pos[0]);
            if (!op.equals("+") && !op.equals("-")) break;
            pos[0]++;
            double right = parseTerm(tokens, pos);
            if (op.equals("+")) result += right;
            else result -= right;
        }
        return result;
    }

    private double parseTerm(List<String> tokens, int[] pos) {
        double result = parseNumber(tokens, pos);
        
        while (pos[0] < tokens.size()) {
            String op = tokens.get(pos[0]);
            if (!op.equals("*") && !op.equals("/")) break;
            pos[0]++;
            double right = parseNumber(tokens, pos);
            if (op.equals("*")) result *= right;
            else {
                if (right == 0) throw new ArithmeticException("Division by zero");
                result /= right;
            }
        }
        return result;
    }

    private double parseNumber(List<String> tokens, int[] pos) {
        String token = tokens.get(pos[0]);
        pos[0]++;
        return Double.parseDouble(token);
    }

    private void calculatePercent() {
        String expr = expression.toString().trim();
        if (expr.isEmpty()) return;
        
        try {
            double result = evaluateExpression(expr);
            double percentResult = result / 100;
            String resultStr = formatResult(percentResult);
            expression.setLength(0);
            expression.append(resultStr);
            newInput = true;
            updateDisplay();
        } catch (Exception e) {
            displayTextView.setText("Error");
            expression.setLength(0);
            newInput = true;
        }
    }

    private void clearExpression() {
        expression.setLength(0);
        newInput = true;
        updateDisplay();
    }

    private void deleteLastChar() {
        if (expression.length() > 0) {
            expression.deleteCharAt(expression.length() - 1);
            if (expression.length() == 0) {
                newInput = true;
            }
            updateDisplay();
        }
    }

    private String formatResult(double result) {
        if (result == (long) result) {
            return String.valueOf((long) result);
        }
        return String.valueOf(result);
    }

    private void updateDisplay() {
        displayTextView.setText(expression.toString());
    }
}
