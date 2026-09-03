package com.example.logic

import com.example.data.model.AIDifficulty
import com.example.data.model.GuessResult
import kotlin.random.Random

sealed class ValidationResult {
    data object Valid : ValidationResult()
    data class Invalid(val message: String) : ValidationResult()
}

object OrderDigitEvaluator {

    val ALL_VALID_NUMBERS: List<String> by lazy {
        val list = mutableListOf<String>()
        for (d1 in 1..9) {
            for (d2 in 0..9) {
                if (d2 == d1) continue
                for (d3 in 0..9) {
                    if (d3 == d1 || d3 == d2) continue
                    for (d4 in 0..9) {
                        if (d4 == d1 || d4 == d2 || d4 == d3) continue
                        list.add("$d1$d2$d3$d4")
                    }
                }
            }
        }
        list
    }

    fun validateNumber(number: String): ValidationResult {
        if (number.length != 4) {
            return ValidationResult.Invalid("Number must be exactly 4 digits")
        }
        if (!number.all { it.isDigit() }) {
            return ValidationResult.Invalid("Only numbers 0-9 are allowed")
        }
        if (number.startsWith('0')) {
            return ValidationResult.Invalid("First digit cannot be 0")
        }
        if (number.toSet().size != 4) {
            return ValidationResult.Invalid("Digits cannot be repeated")
        }
        return ValidationResult.Valid
    }

    /**
     * Order: count of digits matching exact position
     * Digit: count of digits in guess that exist anywhere in hidden number
     */
    fun evaluate(secret: String, guess: String): Pair<Int, Int> {
        require(secret.length == 4 && guess.length == 4) { "Secret and guess must be 4 digits" }

        var order = 0
        var digit = 0

        val secretSet = secret.toSet()

        for (i in 0..3) {
            if (guess[i] == secret[i]) {
                order++
            }
            if (guess[i] in secretSet) {
                digit++
            }
        }

        return Pair(order, digit)
    }

    fun generateRandomSecret(): String {
        val firstDigit = Random.nextInt(1, 10)
        val available = (0..9).filter { it != firstDigit }.shuffled()
        return "$firstDigit${available[0]}${available[1]}${available[2]}"
    }

    /**
     * AI candidate filter:
     * Given past AI guesses and their (order, digit) feedback,
     * filter all candidate secrets consistent with all observations.
     */
    fun filterCandidates(
        candidates: List<String>,
        lastGuess: String,
        lastOrders: Int,
        lastDigits: Int
    ): List<String> {
        return candidates.filter { candidate ->
            val (o, d) = evaluate(secret = candidate, guess = lastGuess)
            o == lastOrders && d == lastDigits
        }
    }

    /**
     * Choose AI guess based on difficulty
     */
    fun chooseAIGuess(
        candidates: List<String>,
        difficulty: AIDifficulty,
        aiHistory: List<GuessResult>
    ): String {
        if (candidates.isEmpty()) {
            return generateRandomSecret()
        }

        if (aiHistory.isEmpty()) {
            // Good opening guesses: 1234, 5678, etc.
            return when (difficulty) {
                AIDifficulty.EASY -> ALL_VALID_NUMBERS.random()
                AIDifficulty.MEDIUM -> listOf("1234", "5678", "9876", "2345").random()
                AIDifficulty.MASTER -> "1234"
            }
        }

        return when (difficulty) {
            AIDifficulty.EASY -> {
                // 50% chance to pick from consistent candidates, 50% chance random valid number
                if (Random.nextBoolean()) candidates.random() else ALL_VALID_NUMBERS.random()
            }
            AIDifficulty.MEDIUM -> {
                // Always pick a consistent candidate
                candidates.random()
            }
            AIDifficulty.MASTER -> {
                if (candidates.size <= 2) {
                    candidates.first()
                } else if (candidates.size < 80) {
                    // Minimax: find guess (from candidates or all) that minimizes maximum remaining candidates
                    var bestGuess = candidates.first()
                    var minMaxRemaining = Int.MAX_VALUE

                    // Test against candidate pool for optimal partition
                    val testSet = if (candidates.size < 40) ALL_VALID_NUMBERS.take(200) + candidates else candidates
                    for (guess in testSet) {
                        val outcomeCounts = mutableMapOf<Pair<Int, Int>, Int>()
                        for (secret in candidates) {
                            val key = evaluate(secret, guess)
                            outcomeCounts[key] = (outcomeCounts[key] ?: 0) + 1
                        }
                        val maxGroup = outcomeCounts.values.maxOrNull() ?: candidates.size
                        if (maxGroup < minMaxRemaining) {
                            minMaxRemaining = maxGroup
                            bestGuess = guess
                        }
                    }
                    bestGuess
                } else {
                    candidates.random()
                }
            }
        }
    }
}
