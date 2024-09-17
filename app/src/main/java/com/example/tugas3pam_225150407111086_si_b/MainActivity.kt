package com.example.tugas3pam_225150407111086_si_b // Ganti dengan package name proyek Anda

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.example.tugas3pam_225150407111086_si_b.R // Ganti dengan nama package proyek Anda

class MainActivity : AppCompatActivity() {
    private lateinit var solutionTv: TextView
    private lateinit var resultTv: TextView
    private var canAddOperation = false
    private var canAddDecimal = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        solutionTv = findViewById(R.id.solution_tv)
        resultTv = findViewById(R.id.result_tv)

        // Numeric buttons
        setNumericOnClickListener(R.id.button_0, "0")
        setNumericOnClickListener(R.id.button_1, "1")
        setNumericOnClickListener(R.id.button_2, "2")
        setNumericOnClickListener(R.id.button_3, "3")
        setNumericOnClickListener(R.id.button_4, "4")
        setNumericOnClickListener(R.id.button_5, "5")
        setNumericOnClickListener(R.id.button_6, "6")
        setNumericOnClickListener(R.id.button_7, "7")
        setNumericOnClickListener(R.id.button_8, "8")
        setNumericOnClickListener(R.id.button_9, "9")

        // Operator buttons
        setOperationOnClickListener(R.id.button_tambah, "+")
        setOperationOnClickListener(R.id.button_kurang, "-")
        setOperationOnClickListener(R.id.button_kali, "*")
        setOperationOnClickListener(R.id.button_bagi, "/")

        // Other buttons
        findViewById<Button>(R.id.button_c).setOnClickListener { clearCalculator() }
        findViewById<Button>(R.id.button_koma).setOnClickListener { addDecimal() }
        findViewById<Button>(R.id.button_kurung_buka).setOnClickListener { appendToExpression("(") }
        findViewById<Button>(R.id.button_kurung_tutup).setOnClickListener { appendToExpression(")") }
        findViewById<Button>(R.id.button_sama_dengan).setOnClickListener { calculateResult() }
    }

    private fun setNumericOnClickListener(buttonId: Int, number: String) {
        findViewById<Button>(buttonId).setOnClickListener {
            appendToExpression(number)
        }
    }

    private fun setOperationOnClickListener(buttonId: Int, operation: String) {
        findViewById<Button>(buttonId).setOnClickListener {
            if (canAddOperation) {
                appendToExpression(operation)
                canAddOperation = false
                canAddDecimal = true
            }
        }
    }

    private fun appendToExpression(string: String) {
        if (resultTv.text.toString() == "0") {
            resultTv.text = ""
        }
        resultTv.append(string)
        canAddOperation = true
    }

    private fun clearCalculator() {
        resultTv.text = "0"
        solutionTv.text = ""
        canAddOperation = false
        canAddDecimal = true
    }

    private fun addDecimal() {
        if (canAddDecimal) {
            if (resultTv.text.toString().isEmpty() || !Character.isDigit(resultTv.text.toString().last())) {
                appendToExpression("0.")
            } else {
                appendToExpression(".")
            }
            canAddDecimal = false
        }
    }
    private fun calculateResult() {
        try {
            val result = SimpleEvaluator.evaluate(resultTv.text.toString())
            if (result.isNaN()) {
                solutionTv.text = "Error"
            } else {
                solutionTv.text = result.toString()
            }
            resultTv.text = solutionTv.text.toString()
            canAddOperation = true
            canAddDecimal = true
        } catch (e: Exception) {
            solutionTv.text = "Error"
            resultTv.text = ""
            canAddOperation = false
            canAddDecimal = true
        }
    }
    object SimpleEvaluator {
        fun evaluate(expression: String): Double {
            return try {
                val tokens = tokenize(expression)
                val postfix = infixToPostfix(tokens)
                evaluatePostfix(postfix)
            } catch (e: Exception) {
                Double.NaN
            }
        }

        private fun tokenize(expression: String): List<String> {
            return expression.replace("(", " ( ")
                .replace(")", " ) ")
                .replace("+", " + ")
                .replace("-", " - ")
                .replace("*", " * ")
                .replace("/", " / ")
                .trim()
                .split("\\s+".toRegex())
        }

        private fun infixToPostfix(tokens: List<String>): List<String> {
            val output = mutableListOf<String>()
            val stack = mutableListOf<String>()

            for (token in tokens) {
                when {
                    token.toDoubleOrNull() != null -> output.add(token)
                    token == "(" -> stack.add(token)
                    token == ")" -> {
                        while (stack.isNotEmpty() && stack.last() != "(") {
                            output.add(stack.removeAt(stack.lastIndex))
                        }
                        if (stack.isNotEmpty() && stack.last() == "(") {
                            stack.removeAt(stack.lastIndex)
                        }
                    }
                    token in setOf("+", "-", "*", "/") -> {
                        while (stack.isNotEmpty() && precedence(stack.last()) >= precedence(token)) {
                            output.add(stack.removeAt(stack.lastIndex))
                        }
                        stack.add(token)
                    }
                }
            }

            while (stack.isNotEmpty()) {
                output.add(stack.removeAt(stack.lastIndex))
            }

            return output
        }

        private fun evaluatePostfix(tokens: List<String>): Double {
            val stack = mutableListOf<Double>()

            for (token in tokens) {
                when {
                    token.toDoubleOrNull() != null -> stack.add(token.toDouble())
                    token in setOf("+", "-", "*", "/") -> {
                        val b = stack.removeAt(stack.lastIndex)
                        val a = stack.removeAt(stack.lastIndex)
                        stack.add(when (token) {
                            "+" -> a + b
                            "-" -> a - b
                            "*" -> a * b
                            "/" -> a / b
                            else -> throw IllegalArgumentException("Unknown operator: $token")
                        })
                    }
                }
            }

            return stack.first()
        }

        private fun precedence(operator: String): Int = when (operator) {
            "+", "-" -> 1
            "*", "/" -> 2
            else -> 0
        }
    }
}