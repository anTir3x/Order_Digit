package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Adjust
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GuessResult
import com.example.ui.theme.DigitAmber
import com.example.ui.theme.DigitAmberContainer
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.OrderCyan
import com.example.ui.theme.OrderCyanContainer
import com.example.ui.theme.SuccessGreen

@Composable
fun GuessHistoryItem(
    result: GuessResult,
    modifier: Modifier = Modifier,
    isLatest: Boolean = false,
    targetSecret: String? = null,
    isTargetRevealed: Boolean = false
) {
    val isZeroZero = result.orderCount == 0 && result.digitCount == 0

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("guess_item_round_${result.roundNumber}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                result.isWin -> SuccessGreen.copy(alpha = 0.15f)
                isZeroZero -> ErrorRed.copy(alpha = 0.07f)
                isLatest -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
            }
        ),
        border = when {
            result.isWin -> CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(SuccessGreen))
            isZeroZero -> CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(ErrorRed.copy(alpha = 0.35f)))
            isLatest -> CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(OrderCyan.copy(alpha = 0.5f)))
            else -> null
        }
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left: Round index & Guess digits
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Round badge
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    result.isWin -> SuccessGreen.copy(alpha = 0.25f)
                                    isZeroZero -> ErrorRed.copy(alpha = 0.2f)
                                    else -> MaterialTheme.colorScheme.surfaceVariant
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "#${result.roundNumber}",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = when {
                                result.isWin -> SuccessGreen
                                isZeroZero -> ErrorRed
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }

                    // Digits spaced out
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        for (char in result.guess) {
                            Text(
                                text = char.toString(),
                                modifier = if (isZeroZero) Modifier.blur(1.5.dp) else Modifier,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 20.sp,
                                    textDecoration = if (isZeroZero) TextDecoration.LineThrough else null
                                ),
                                color = if (isZeroZero) ErrorRed.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                // Right: Order and Digit pills
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Order Pill
                    ScorePill(
                        count = result.orderCount,
                        label = "Order",
                        icon = Icons.Default.Adjust,
                        accentColor = if (isZeroZero) ErrorRed else OrderCyan,
                        containerColor = if (isZeroZero) ErrorRed.copy(alpha = 0.15f) else OrderCyanContainer.copy(alpha = 0.8f),
                        testTag = "order_badge_${result.roundNumber}"
                    )

                    // Digit Pill
                    ScorePill(
                        count = result.digitCount,
                        label = "Digit",
                        icon = Icons.Default.Numbers,
                        accentColor = if (isZeroZero) ErrorRed else DigitAmber,
                        containerColor = if (isZeroZero) ErrorRed.copy(alpha = 0.15f) else DigitAmberContainer.copy(alpha = 0.8f),
                        testTag = "digit_badge_${result.roundNumber}"
                    )
                }
            }

            // 0-0 Elimination Banner
            if (isZeroZero) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(ErrorRed.copy(alpha = 0.1f))
                        .padding(horizontal = 14.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Block,
                        contentDescription = null,
                        tint = ErrorRed,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = "0-0 Clue: ${result.guess.map { it }.joinToString(", ")} eliminated & blurred on keypad!",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        ),
                        color = ErrorRed
                    )
                }
            }

            if (isTargetRevealed && !targetSecret.isNullOrEmpty() && targetSecret.length == 4) {
                val exactMatches = (0..3).filter { result.guess[it] == targetSecret[it] }.map { result.guess[it] }
                val presentDigits = result.guess.filter { it in targetSecret }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                        .padding(horizontal = 14.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "🎯 Exact: ${if (exactMatches.isEmpty()) "None" else exactMatches.joinToString(", ")}",
                        style = MaterialTheme.typography.labelSmall,
                        color = OrderCyan
                    )
                    Text(
                        text = "🔢 Present: ${if (presentDigits.isEmpty()) "None" else presentDigits.toList().joinToString(", ")}",
                        style = MaterialTheme.typography.labelSmall,
                        color = DigitAmber
                    )
                }
            }
        }
    }
}

@Composable
fun ScorePill(
    count: Int,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    containerColor: Color,
    testTag: String
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(containerColor)
            .border(
                width = 1.dp,
                color = accentColor.copy(alpha = 0.4f),
                shape = RoundedCornerShape(10.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = accentColor,
            modifier = Modifier.size(14.dp)
        )
        Text(
            text = "$count",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace,
                fontSize = 15.sp
            ),
            color = accentColor
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
            color = Color.White.copy(alpha = 0.85f)
        )
    }
}
