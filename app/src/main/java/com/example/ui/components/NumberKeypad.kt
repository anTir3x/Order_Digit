package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.OrderCyan

@Composable
fun NumberKeypad(
    currentInput: String,
    onDigitClick: (Char) -> Unit,
    onBackspace: () -> Unit,
    onClear: () -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier,
    submitLabel: String = "Submit Guess",
    isSubmitEnabled: Boolean = currentInput.length == 4,
    showActionButtons: Boolean = true,
    eliminatedDigits: Set<Char> = emptySet()
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        // Quick Eliminated Summary Pill
        AnimatedVisibility(visible = eliminatedDigits.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(ErrorRed)
                    )
                    Text(
                        text = "${eliminatedDigits.size} blurred (0-0 clues)",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = "${10 - eliminatedDigits.size} viable digits remain",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = OrderCyan
                )
            }
        }

        val rows = listOf(
            listOf('1', '2', '3'),
            listOf('4', '5', '6'),
            listOf('7', '8', '9')
        )

        for (row in rows) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (digit in row) {
                    val isUsed = currentInput.contains(digit)
                    val isFull = currentInput.length >= 4
                    val isEliminated = eliminatedDigits.contains(digit)
                    val isEnabled = !isUsed && !isFull

                    KeypadButton(
                        label = digit.toString(),
                        isEnabled = isEnabled,
                        isUsed = isUsed,
                        isEliminated = isEliminated,
                        testTag = "keypad_digit_$digit",
                        onClick = { onDigitClick(digit) }
                    )
                }
            }
        }

        // Bottom numeric row: Clear, 0, Backspace
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Clear All button
            KeypadActionButton(
                icon = {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear All",
                        tint = if (currentInput.isNotEmpty()) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                    )
                },
                isEnabled = currentInput.isNotEmpty(),
                testTag = "keypad_clear_all",
                onClick = onClear
            )

            // '0' digit button (cannot be first digit, cannot be repeated)
            val isZeroUsed = currentInput.contains('0')
            val isZeroFirst = currentInput.isEmpty()
            val isZeroEliminated = eliminatedDigits.contains('0')
            val isZeroEnabled = !isZeroUsed && !isZeroFirst && currentInput.length < 4

            KeypadButton(
                label = "0",
                isEnabled = isZeroEnabled,
                isUsed = isZeroUsed,
                isEliminated = isZeroEliminated,
                testTag = "keypad_digit_0",
                onClick = { onDigitClick('0') }
            )

            // Backspace button
            KeypadActionButton(
                icon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Backspace,
                        contentDescription = "Backspace",
                        tint = if (currentInput.isNotEmpty()) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    )
                },
                isEnabled = currentInput.isNotEmpty(),
                testTag = "keypad_backspace",
                onClick = onBackspace
            )
        }

        if (showActionButtons) {
            Spacer(modifier = Modifier.height(6.dp))

            Button(
                onClick = onSubmit,
                enabled = isSubmitEnabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("keypad_submit_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = IndigoPrimary,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = submitLabel,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (isSubmitEnabled) Color.White else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = if (isSubmitEnabled) Color.White else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                    )
                }
            }
        }
    }
}

@Composable
private fun KeypadButton(
    label: String,
    isEnabled: Boolean,
    isUsed: Boolean,
    isEliminated: Boolean = false,
    testTag: String,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    val bg = when {
        isEliminated -> ErrorRed.copy(alpha = 0.08f)
        isUsed -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f)
        isEnabled -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
    }

    val textColor = when {
        isEliminated -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
        isUsed -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
        isEnabled -> MaterialTheme.colorScheme.onSurface
        else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
    }

    Surface(
        modifier = Modifier
            .size(width = 86.dp, height = 54.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = if (isEliminated) 1.dp else if (isEnabled && !isUsed) 1.dp else 0.dp,
                color = when {
                    isEliminated -> ErrorRed.copy(alpha = 0.25f)
                    isEnabled && !isUsed -> MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
                    else -> Color.Transparent
                },
                shape = RoundedCornerShape(16.dp)
            )
            .testTag(testTag)
            .minimumInteractiveComponentSize()
            .semantics {
                contentDescription = if (isEliminated) "$label (Eliminated from 0-0 clues)" else label
            },
        shape = RoundedCornerShape(16.dp),
        color = bg
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    enabled = isEnabled,
                    interactionSource = interactionSource,
                    indication = ripple(bounded = true),
                    onClick = onClick
                )
                .drawWithContent {
                    drawContent()
                    if (isEliminated) {
                        // Subtle diagonal strike line through eliminated digit
                        drawLine(
                            color = ErrorRed.copy(alpha = 0.45f),
                            start = Offset(16f, size.height - 14f),
                            end = Offset(size.width - 16f, 14f),
                            strokeWidth = 2.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                modifier = if (isEliminated) Modifier.blur(3.5.dp) else Modifier,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 24.sp,
                    textDecoration = if (isEliminated) TextDecoration.LineThrough else null
                ),
                color = textColor
            )

            // Corner marker for eliminated digits
            if (isEliminated) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 4.dp, end = 6.dp)
                ) {
                    Text(
                        text = "✕",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black
                        ),
                        color = ErrorRed.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

@Composable
private fun KeypadActionButton(
    icon: @Composable () -> Unit,
    isEnabled: Boolean,
    testTag: String,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    Surface(
        modifier = Modifier
            .size(width = 86.dp, height = 54.dp)
            .clip(RoundedCornerShape(16.dp))
            .testTag(testTag)
            .minimumInteractiveComponentSize(),
        shape = RoundedCornerShape(16.dp),
        color = if (isEnabled) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f)
    ) {
        Box(
            modifier = Modifier
                .clickable(
                    enabled = isEnabled,
                    interactionSource = interactionSource,
                    indication = ripple(bounded = true),
                    onClick = onClick
                ),
            contentAlignment = Alignment.Center
        ) {
            icon()
        }
    }
}
