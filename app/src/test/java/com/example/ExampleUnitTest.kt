package com.example

import com.example.logic.OrderDigitEvaluator
import com.example.logic.ValidationResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testOrderDigitEvaluationPromptExample() {
        // From prompt: "hidden number is 9712 and the opponent guesses 9152, the result would be 2 orders and 3 digit"
        val hidden = "9712"
        val guess = "9152"
        val (orders, digits) = OrderDigitEvaluator.evaluate(hidden, guess)

        assertEquals("Orders must be 2 (positions 0 and 3 match)", 2, orders)
        assertEquals("Digits must be 3 (9, 1, 2 are present)", 3, digits)
    }

    @Test
    fun testOrderDigitAllMatchWin() {
        val hidden = "9712"
        val guess = "9712"
        val (orders, digits) = OrderDigitEvaluator.evaluate(hidden, guess)

        assertEquals(4, orders)
        assertEquals(4, digits)
    }

    @Test
    fun testOrderDigitNoMatch() {
        val hidden = "9712"
        val guess = "3456"
        val (orders, digits) = OrderDigitEvaluator.evaluate(hidden, guess)

        assertEquals(0, orders)
        assertEquals(0, digits)
    }

    @Test
    fun testOrderDigitAllDigitsWrongPosition() {
        val hidden = "1234"
        val guess = "4321"
        val (orders, digits) = OrderDigitEvaluator.evaluate(hidden, guess)

        assertEquals(0, orders)
        assertEquals(4, digits)
    }

    @Test
    fun testValidationRules() {
        // Valid
        assertTrue(OrderDigitEvaluator.validateNumber("1234") is ValidationResult.Valid)
        assertTrue(OrderDigitEvaluator.validateNumber("9712") is ValidationResult.Valid)

        // Invalid: Starts with 0
        assertTrue(OrderDigitEvaluator.validateNumber("0123") is ValidationResult.Invalid)

        // Invalid: Duplicate digits
        assertTrue(OrderDigitEvaluator.validateNumber("1123") is ValidationResult.Invalid)
        assertTrue(OrderDigitEvaluator.validateNumber("1223") is ValidationResult.Invalid)

        // Invalid: Length
        assertTrue(OrderDigitEvaluator.validateNumber("123") is ValidationResult.Invalid)
        assertTrue(OrderDigitEvaluator.validateNumber("12345") is ValidationResult.Invalid)
    }

    @Test
    fun testRandomSecretGeneration() {
        for (i in 1..20) {
            val secret = OrderDigitEvaluator.generateRandomSecret()
            assertEquals(4, secret.length)
            assertTrue(secret[0] != '0')
            assertEquals(4, secret.toSet().size)
        }
    }
}
