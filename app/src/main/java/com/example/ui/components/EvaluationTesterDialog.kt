package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Adjust
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.logic.OrderDigitEvaluator
import com.example.logic.ValidationResult
import com.example.ui.theme.DigitAmber
import com.example.ui.theme.DigitAmberContainer
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.OrderCyan
import com.example.ui.theme.OrderCyanContainer
import com.example.ui.theme.SuccessGreen

@Composable
fun EvaluationTesterDialog(
    initialSecret: String = "9712",
    initialGuess: String = "9152",
    onDismiss: () -> Unit
) {
    var secretInput by remember { mutableStateOf(initialSecret) }
    var guessInput by remember { mutableStateOf(initialGuess) }

    val secretValidation = OrderDigitEvaluator.validateNumber(secretInput)
    val guessValidation = OrderDigitEvaluator.validateNumber(guessInput)

    val canEvaluate = secretValidation is ValidationResult.Valid && guessValidation is ValidationResult.Valid

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .clip(RoundedCornerShape(24.dp))
                .testTag("evaluation_tester_dialog"),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(OrderCyanContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Science,
                                contentDescription = null,
                                tint = OrderCyan,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Evaluation Tester",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Verify Order & Digit calculations",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("btn_close_eval_tester")
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                // Preset button to test 9712 vs 9152
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            secretInput = "9712"
                            guessInput = "9152"
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Prompt (9712 vs 9152)", fontSize = 11.sp)
                    }

                    OutlinedButton(
                        onClick = {
                            secretInput = "9712"
                            guessInput = "9712"
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Win (9712 vs 9712)", fontSize = 11.sp)
                    }
                }

                // Input fields
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Hidden Secret Number
                    Column {
                        Text(
                            text = "Hidden Number (Target):",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = secretInput,
                            onValueChange = { if (it.length <= 4 && it.all { ch -> ch.isDigit() }) secretInput = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("tester_secret_input"),
                            singleLine = true,
                            textStyle = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            ),
                            placeholder = { Text("4 unique digits, no leading 0") }
                        )
                        if (secretValidation is ValidationResult.Invalid) {
                            Text(
                                text = secretValidation.message,
                                style = MaterialTheme.typography.labelSmall,
                                color = ErrorRed,
                                modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                            )
                        }
                    }

                    // Guess Number
                    Column {
                        Text(
                            text = "Opponent's Guess:",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = OrderCyan
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = guessInput,
                            onValueChange = { if (it.length <= 4 && it.all { ch -> ch.isDigit() }) guessInput = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("tester_guess_input"),
                            singleLine = true,
                            textStyle = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            ),
                            placeholder = { Text("4 unique digits, no leading 0") }
                        )
                        if (guessValidation is ValidationResult.Invalid) {
                            Text(
                                text = guessValidation.message,
                                style = MaterialTheme.typography.labelSmall,
                                color = ErrorRed,
                                modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                            )
                        }
                    }
                }

                if (canEvaluate) {
                    val (orders, digits) = OrderDigitEvaluator.evaluate(secret = secretInput, guess = guessInput)
                    val secretSet = secretInput.toSet()

                    // Evaluation Summary Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "Evaluation Result:",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Order badge
                                ScorePill(
                                    count = orders,
                                    label = "ORDER",
                                    icon = Icons.Default.Adjust,
                                    accentColor = OrderCyan,
                                    containerColor = OrderCyanContainer,
                                    testTag = "tester_result_order"
                                )

                                // Digit badge
                                ScorePill(
                                    count = digits,
                                    label = "DIGIT",
                                    icon = Icons.Default.Numbers,
                                    accentColor = DigitAmber,
                                    containerColor = DigitAmberContainer,
                                    testTag = "tester_result_digit"
                                )
                            }

                            // Step by step breakdown for each digit
                            Text(
                                text = "Detailed Digit-by-Digit Inspection:",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                for (i in 0..3) {
                                    val guessChar = guessInput[i]
                                    val secretChar = secretInput[i]
                                    val isExact = guessChar == secretChar
                                    val existsAnywhere = guessChar in secretSet

                                    val statusColor = when {
                                        isExact -> OrderCyan
                                        existsAnywhere -> DigitAmber
                                        else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                    }

                                    val statusText = when {
                                        isExact -> "Position ${i + 1}: '$guessChar' matches exact position! (+1 Order, +1 Digit)"
                                        existsAnywhere -> "Position ${i + 1}: '$guessChar' exists in hidden number at pos ${secretInput.indexOf(guessChar) + 1} (+1 Digit)"
                                        else -> "Position ${i + 1}: '$guessChar' is NOT in the hidden number (0)"
                                    }

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(statusColor)
                                        )
                                        Text(
                                            text = statusText,
                                            style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                                            color = statusColor
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(2.dp))

                            if (orders == 0 && digits == 0) {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = CardDefaults.cardColors(containerColor = ErrorRed.copy(alpha = 0.12f)),
                                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(ErrorRed.copy(alpha = 0.4f)))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(
                                            text = "🚫",
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        Column {
                                            Text(
                                                text = "0-0 Elimination Clue!",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                color = ErrorRed
                                            )
                                            Text(
                                                text = "None of ${guessInput.map { it }.joinToString(", ")} exist in the secret. During gameplay, these digits are automatically blurred on the keypad and deduction pad.",
                                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                }
                            }

                            Text(
                                text = "Total: $orders Order(s) and $digits Digit(s).",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (orders == 4) SuccessGreen else MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Done")
                }
            }
        }
    }
}
