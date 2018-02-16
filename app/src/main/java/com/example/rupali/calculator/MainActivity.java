package com.example.rupali.calculator;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Stack;

public class MainActivity extends AppCompatActivity implements View.OnLongClickListener {
    TextView textView;
    TextView resultTextView;
    boolean prevButtonEquals = false;
    HashMap<Character, Integer> precedence;
    Button delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textView);
        resultTextView = findViewById(R.id.resultTextView);
        delete = findViewById(R.id.buttonDelete);
        delete.setOnLongClickListener(this);
        precedence = new HashMap<Character, Integer>();
        precedence.put('/', 2);
        precedence.put('*', 2);
        precedence.put('+', 1);
        precedence.put('-', 1);

    }

    public void onButtonClick(View view) {
        Button button = (Button) view;
        int i = 0;
        String infix;
        String postfix;
        Double result;
        int id = view.getId();
        switch (id) {
            case R.id.buttonDelete:
                if (prevButtonEquals || textView.getText().toString().length() == 0) {
                    textView.setText("");
                    resultTextView.setText("");
                    prevButtonEquals = false;
                    delete.setText("DEL");
                    break;
                }
                i = textView.getText().toString().length() - 1;
                textView.setText(textView.getText().toString().substring(0, i));
                if (i == 0) {
                    resultTextView.setText("");
                    prevButtonEquals = false;
                    delete.setText("DEL");
                    break;
                }
                char lastOp = textView.getText().toString().charAt(i - 1);
                if (lastOp != '*' && lastOp != '/' && lastOp != '+' && lastOp != '-' && lastOp != '.') {
                    infix = textView.getText().toString();
                    postfix = infixToPostfix(infix);
                    result = evaluatePostfix(postfix);
                    if (result.intValue() == result) {
                        resultTextView.setText(Integer.toString(result.intValue()));
                    } else {
                        resultTextView.setText(result.toString());
                    }
                } else if (i != 1) {
                    infix = textView.getText().toString().substring(0, i - 1);
                    postfix = infixToPostfix(infix);
                    result = evaluatePostfix(postfix);
                    if (result.intValue() == result) {
                        resultTextView.setText(Integer.toString(result.intValue()));
                    } else {
                        resultTextView.setText(result.toString());
                    }
                } else {
                    resultTextView.setText("");
                }
                prevButtonEquals = false;
                delete.setText("DEL");
                break;
            case R.id.buttonEqual:
                infix = textView.getText().toString();
                if (infix.length() == 0) {
                    break;
                }
                postfix = infixToPostfix(infix);
                result = evaluatePostfix(postfix);
                if (result.intValue() == result) {
                    textView.setText(Integer.toString(result.intValue()));
                } else {
                    textView.setText(result.toString());
                }
                resultTextView.setText((""));
                prevButtonEquals = true;
                delete.setText("CLR");
                break;
            default:
                if (prevButtonEquals && id != R.id.buttonDivide && id != R.id.buttonMinus && id != R.id.buttonPlus && id != R.id.buttonMultiply) {
                    textView.setText(button.getText());
                    prevButtonEquals = false;
                    delete.setText("DEL");
                    break;

                }
                textView.setText(textView.getText().toString() + button.getText());
                if (id != R.id.buttonDivide && id != R.id.buttonDot && id != R.id.buttonMinus && id != R.id.buttonPlus && id != R.id.buttonMultiply) {
                    infix = textView.getText().toString();
                    postfix = infixToPostfix(infix);
                    result = evaluatePostfix(postfix);
                    if (result.intValue() == result) {
                        resultTextView.setText(Integer.toString(result.intValue()));
                    } else {
                        resultTextView.setText(result.toString());
                    }
                }
                prevButtonEquals = false;
                delete.setText("DEL");
                break;
        }
    }

    private Double evaluatePostfix(String postfix) {
        Double ans = 0.0;
        Stack<Double> operand = new Stack<Double>();
        for (int i = 0; i < postfix.length(); i++) {
            if (postfix.charAt(i) == '+' || postfix.charAt(i) == '-' || postfix.charAt(i) == '*' || postfix.charAt(i) == '/') {
                Double second = operand.peek();
                operand.pop();
                Double first = 0.0;
                if (!operand.empty()) {
                    first = operand.peek();
                    operand.pop();
                }
                switch (postfix.charAt(i)) {
                    case '+':
                        ans = first + second;
                        operand.push(ans);
                        break;
                    case '-':
                        ans = first - second;
                        operand.push(ans);
                        break;
                    case '*':
                        ans = first * second;
                        operand.push(ans);
                        break;
                    case '/':
                        ans = first / second;
                        operand.push(ans);
                        break;
                }
            } else if (postfix.charAt(i) == ' ') {

            } else {
                String s = new String();
                while (postfix.charAt(i) != ' ') {
                    s += postfix.charAt(i);
                    i++;
                }
                operand.push(Double.parseDouble(s));
            }
        }
        return operand.peek();
    }

    private String infixToPostfix(String infix) {
        infix = infix + '#';
        Stack<Character> operator = new Stack<Character>();
        String postfix = new String();
        for (int i = 0; i < infix.length(); i++) {
            if (infix.charAt(i) == '(') {
                operator.push('(');
            } else if (infix.charAt(i) == '+' || infix.charAt(i) == '-' || infix.charAt(i) == '*' || infix.charAt(i) == '/') {
                if (operator.empty() || operator.peek() == '(') {
                    operator.push(infix.charAt(i));
                } else if (precedence.get(infix.charAt(i)) > precedence.get(operator.peek())) {
                    operator.push(infix.charAt(i));
                } else {
                    while (!operator.empty() && operator.peek() != '(' && precedence.get(infix.charAt(i)) <= precedence.get(operator.peek())) {
                        postfix += operator.peek();
                        operator.pop();
                    }
                    operator.push(infix.charAt(i));
                }
            } else if (infix.charAt(i) == ')') {
                while (operator.peek() != '(') {
                    // postfix.concat(operator.peek()+"");
                    postfix += operator.peek();
                    operator.pop();
                }
                operator.pop();
            } else if (infix.charAt(i) == '#') {
                while (!operator.empty()) {
                    postfix += operator.peek();
                    operator.pop();
                }
            } else {
                while ((infix.charAt(i) >= '0' && infix.charAt(i) <= '9') || infix.charAt(i) == '.') {
                    postfix += infix.charAt(i);
                    i++;
                }
                i--;
                if (postfix.charAt(postfix.length() - 1) == '.') {
                    postfix += '0';
                }
                postfix += ' ';
            }
        }
        return postfix;
    }

    @Override
    public boolean onLongClick(View view) {
        resultTextView.setText("");
        textView.setText("");
        return true;
    }
}
