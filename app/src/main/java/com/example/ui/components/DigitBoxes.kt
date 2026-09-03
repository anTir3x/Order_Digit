package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.OrderCyan
import com.example.ui.theme.OrderCyanLight

@Composable
fun DigitBoxes(
    input: String,
    modifier: Modifier = Modifier,
    isMasked: Boolean = false,
    hasError: Boolean = false,
    maxDigits: Int = 4
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until maxDigits) {
            val char = input.getOrNull(i)
            val isCurrentActive = input.length == i
            val isFilled = char != null

            val borderColor by animateColorAsState(
                targetValue = when {
                    hasError -> MaterialTheme.colorScheme.error
                    isCurrentActive -> OrderCyan
                    isFilled -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                },
                label = "digit_box_border"
            )

            val boxBg by animateColorAsState(
                targetValue = when {
                    hasError -> MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
                    isFilled -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
                    isCurrentActive -> OrderCyanLight.copy(alpha = 0.12f)
                    else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                },
                label = "digit_box_bg"
            )

            val scale by animateFloatAsState(
                targetValue = if (isCurrentActive) 1.05f else 1.0f,
                animationSpec = spring(),
                label = "digit_box_scale"
            )

            Box(
                modifier = Modifier
                    .size(width = 58.dp, height = 68.dp)
                    .scale(scale)
                    .clip(RoundedCornerShape(16.dp))
                    .background(boxBg)
                    .border(
                        width = if (isCurrentActive || isFilled) 2.dp else 1.5.dp,
                        color = borderColor,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .testTag("digit_box_$i"),
                contentAlignment = Alignment.Center
            ) {
                if (char != null) {
                    val displayText = if (isMasked) "●" else char.toString()
                    Text(
                        text = displayText,
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            fontSize = if (isMasked) 24.sp else 32.sp
                        ),
                        color = if (hasError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                    )
                } else if (isCurrentActive) {
                    // Subtle cursor indicator
                    Box(
                        modifier = Modifier
                            .size(width = 16.dp, height = 3.dp)
                            .background(
                                color = OrderCyan,
                                shape = RoundedCornerShape(2.dp)
                            )
                    )
                }
            }
        }
    }
}
