package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.data.model.DigitStatus
import com.example.data.model.PlayerDeductionNotes
import com.example.ui.theme.DigitAmber
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.OrderCyan
import com.example.ui.theme.SuccessGreen

@Composable
fun DeductionPadSheet(
    notes: PlayerDeductionNotes,
    onToggleDigitStatus: (Int) -> Unit,
    onTogglePositionStatus: (position: Int, digit: Int) -> Unit,
    onResetNotes: () -> Unit,
    onAutoAnalyze: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("deduction_pad_sheet"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header with title and quick actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Deduction Scratchpad",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Tap to track digits & eliminate possibilities",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(
                        onClick = onAutoAnalyze,
                        modifier = Modifier.testTag("btn_auto_analyze")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Smart Deduce Hints",
                            tint = OrderCyan
                        )
                    }
                    IconButton(
                        onClick = onResetNotes,
                        modifier = Modifier.testTag("btn_reset_notes")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reset Scratchpad",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                contentColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clip(RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Digit Pool (0-9)") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Positions (1-4)") }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (selectedTab == 0) {
                // Digits Pool (0-9)
                DigitPoolGrid(
                    digitStatusMap = notes.digitStatusMap,
                    onToggleDigit = onToggleDigitStatus
                )
            } else {
                // 4-Position Matrix
                PositionMatrixView(
                    positionMatrix = notes.positionMatrix,
                    onTogglePositionDigit = onTogglePositionStatus
                )
            }
        }
    }
}

@Composable
private fun DigitPoolGrid(
    digitStatusMap: Map<Int, DigitStatus>,
    onToggleDigit: (Int) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val rows = listOf(
            listOf(0, 1, 2, 3, 4),
            listOf(5, 6, 7, 8, 9)
        )

        for (row in rows) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (digit in row) {
                    val status = digitStatusMap[digit] ?: DigitStatus.UNKNOWN
                    DigitStatusChip(
                        digit = digit,
                        status = status,
                        onClick = { onToggleDigit(digit) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Legend
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            LegendItem(label = "Unknown", color = MaterialTheme.colorScheme.outline)
            LegendItem(label = "Eliminated ❌", color = ErrorRed)
            LegendItem(label = "Possible ❓", color = DigitAmber)
            LegendItem(label = "Confirmed ⭐", color = SuccessGreen)
        }
    }
}

@Composable
private fun DigitStatusChip(
    digit: Int,
    status: DigitStatus,
    onClick: () -> Unit
) {
    val bgColor by animateColorAsState(
        targetValue = when (status) {
            DigitStatus.UNKNOWN -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            DigitStatus.ELIMINATED -> ErrorRed.copy(alpha = 0.15f)
            DigitStatus.POSSIBLE -> DigitAmber.copy(alpha = 0.18f)
            DigitStatus.CONFIRMED -> SuccessGreen.copy(alpha = 0.2f)
        },
        label = "chip_bg"
    )

    val borderColor by animateColorAsState(
        targetValue = when (status) {
            DigitStatus.UNKNOWN -> MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            DigitStatus.ELIMINATED -> ErrorRed
            DigitStatus.POSSIBLE -> DigitAmber
            DigitStatus.CONFIRMED -> SuccessGreen
        },
        label = "chip_border"
    )

    Box(
        modifier = Modifier
            .size(width = 56.dp, height = 48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(1.5.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .testTag("deduction_digit_$digit"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "$digit",
                modifier = if (status == DigitStatus.ELIMINATED) Modifier.blur(2.dp) else Modifier,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    textDecoration = if (status == DigitStatus.ELIMINATED) TextDecoration.LineThrough else null
                ),
                color = when (status) {
                    DigitStatus.UNKNOWN -> MaterialTheme.colorScheme.onSurface
                    DigitStatus.ELIMINATED -> ErrorRed.copy(alpha = 0.6f)
                    DigitStatus.POSSIBLE -> DigitAmber
                    DigitStatus.CONFIRMED -> SuccessGreen
                }
            )
            when (status) {
                DigitStatus.ELIMINATED -> Text("✕", fontSize = 9.sp, color = ErrorRed.copy(alpha = 0.8f), fontWeight = FontWeight.Bold)
                DigitStatus.POSSIBLE -> Text("❓", fontSize = 9.sp)
                DigitStatus.CONFIRMED -> Text("⭐", fontSize = 9.sp)
                DigitStatus.UNKNOWN -> Text("•", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun PositionMatrixView(
    positionMatrix: Map<Int, Map<Int, DigitStatus>>,
    onTogglePositionDigit: (position: Int, digit: Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Column headers: Pos 1, Pos 2, Pos 3, Pos 4
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            for (pos in 0..3) {
                Column(
                    modifier = Modifier
                        .width(76.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                        .padding(vertical = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Pos ${pos + 1}",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = OrderCyan
                    )
                    if (pos == 0) {
                        Text(
                            text = "(No 0)",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Spacer(modifier = Modifier.height(13.dp))
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // 0..9 pills
                    for (digit in 0..9) {
                        val status = positionMatrix[pos]?.get(digit) ?: DigitStatus.UNKNOWN
                        val isInvalidFirstZero = pos == 0 && digit == 0

                        val bg = when {
                            isInvalidFirstZero -> ErrorRed.copy(alpha = 0.08f)
                            status == DigitStatus.ELIMINATED -> ErrorRed.copy(alpha = 0.2f)
                            status == DigitStatus.CONFIRMED -> SuccessGreen.copy(alpha = 0.25f)
                            status == DigitStatus.POSSIBLE -> DigitAmber.copy(alpha = 0.2f)
                            else -> Color.Transparent
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(bg)
                                .border(
                                    0.8.dp,
                                    if (status != DigitStatus.UNKNOWN) bg else MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                                    RoundedCornerShape(6.dp)
                                )
                                .clickable(enabled = !isInvalidFirstZero) {
                                    onTogglePositionDigit(pos, digit)
                                }
                                .padding(vertical = 3.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isInvalidFirstZero) "0 ✕" else when (status) {
                                    DigitStatus.ELIMINATED -> "$digit ✕"
                                    DigitStatus.POSSIBLE -> "$digit ?"
                                    DigitStatus.CONFIRMED -> "$digit ★"
                                    DigitStatus.UNKNOWN -> "$digit"
                                },
                                modifier = if (status == DigitStatus.ELIMINATED || isInvalidFirstZero) Modifier.blur(1.dp) else Modifier,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = if (status == DigitStatus.CONFIRMED) FontWeight.Bold else FontWeight.Normal,
                                    textDecoration = if (status == DigitStatus.ELIMINATED || isInvalidFirstZero) TextDecoration.LineThrough else null
                                ),
                                color = if (isInvalidFirstZero) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                                else when (status) {
                                    DigitStatus.ELIMINATED -> ErrorRed.copy(alpha = 0.6f)
                                    DigitStatus.CONFIRMED -> SuccessGreen
                                    DigitStatus.POSSIBLE -> DigitAmber
                                    DigitStatus.UNKNOWN -> MaterialTheme.colorScheme.onSurface
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LegendItem(label: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
