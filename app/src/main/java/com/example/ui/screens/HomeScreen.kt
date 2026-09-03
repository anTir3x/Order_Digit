package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Adjust
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.AIDifficulty
import com.example.data.model.GameMode
import com.example.ui.theme.DigitAmber
import com.example.ui.theme.DigitAmberContainer
import com.example.ui.theme.IndigoDark
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.OrderCyan
import com.example.ui.theme.OrderCyanContainer

@Composable
fun HomeScreen(
    onSelectMode: (GameMode) -> Unit,
    selectedDifficulty: AIDifficulty,
    onSelectDifficulty: (AIDifficulty) -> Unit,
    onOpenHowToPlay: () -> Unit,
    onOpenEvaluationTester: () -> Unit,
    onOpenStats: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Top Toolbar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .border(1.dp, OrderCyan.copy(alpha = 0.5f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_order_digit_logo),
                        contentDescription = "Order Digit Logo",
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Text(
                    text = "Order Digit",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black)
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(
                    onClick = onOpenEvaluationTester,
                    modifier = Modifier.testTag("btn_home_tester")
                ) {
                    Icon(imageVector = Icons.Default.Science, contentDescription = "Evaluation Tester", tint = OrderCyan)
                }
                IconButton(
                    onClick = onOpenStats,
                    modifier = Modifier.testTag("btn_home_stats")
                ) {
                    Icon(imageVector = Icons.Default.BarChart, contentDescription = "Stats")
                }
                IconButton(
                    onClick = onOpenHowToPlay,
                    modifier = Modifier.testTag("btn_home_help")
                ) {
                    Icon(imageVector = Icons.Default.HelpOutline, contentDescription = "How to play")
                }
            }
        }

        // Hero Banner with Graphic
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            border = CardDefaults.outlinedCardBorder()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                IndigoPrimary.copy(alpha = 0.25f),
                                Color.Transparent
                            )
                        )
                    )
                    .padding(20.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Logo Image Preview
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .border(2.dp, OrderCyan.copy(alpha = 0.4f), RoundedCornerShape(18.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_order_digit_logo),
                            contentDescription = "Order Digit Logo",
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Deduce the Hidden 4 Digits",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Match exact positions for Orders and discover present digits to outsmart your opponent.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Evaluation pills preview
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(OrderCyanContainer.copy(alpha = 0.8f))
                                .padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.Adjust, contentDescription = null, tint = OrderCyan, modifier = Modifier.size(16.dp))
                            Text("ORDER = Position", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = OrderCyan)
                        }

                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(DigitAmberContainer.copy(alpha = 0.8f))
                                .padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.Numbers, contentDescription = null, tint = DigitAmber, modifier = Modifier.size(16.dp))
                            Text("DIGIT = Number Exists", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = DigitAmber)
                        }
                    }
                }
            }
        }

        // Section Title
        Text(
            text = "Select Game Mode",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.fillMaxWidth()
        )

        // Mode 1: 2-Player Pass & Play
        ModeCard(
            title = GameMode.TWO_PLAYER_PASS_AND_PLAY.displayName,
            subtitle = GameMode.TWO_PLAYER_PASS_AND_PLAY.description,
            icon = Icons.Default.People,
            accentColor = IndigoPrimary,
            testTag = "btn_mode_pass_and_play",
            onClick = { onSelectMode(GameMode.TWO_PLAYER_PASS_AND_PLAY) }
        )

        // Mode 2: Play vs AI
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, OrderCyan.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                .clickable { onSelectMode(GameMode.VS_AI) }
                .testTag("btn_mode_vs_ai"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(OrderCyanContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.SmartToy,
                                contentDescription = null,
                                tint = OrderCyan,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Single Player vs AI",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Battle smart deduction logic",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = OrderCyan
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // AI Difficulty selector chips
                Text(
                    text = "AI Difficulty:",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AIDifficulty.entries.forEach { diff ->
                        val isSelected = selectedDifficulty == diff
                        FilterChip(
                            selected = isSelected,
                            onClick = { onSelectDifficulty(diff) },
                            label = { Text(diff.label, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = OrderCyanContainer,
                                selectedLabelColor = OrderCyan
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = if (isSelected) OrderCyan else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            ),
                            modifier = Modifier.testTag("chip_difficulty_${diff.name.lowercase()}")
                        )
                    }
                }
            }
        }

        // Mode 3: Solo Mystery Practice
        ModeCard(
            title = GameMode.SOLO_PRACTICE.displayName,
            subtitle = GameMode.SOLO_PRACTICE.description,
            icon = Icons.Default.Person,
            accentColor = DigitAmber,
            testTag = "btn_mode_solo",
            onClick = { onSelectMode(GameMode.SOLO_PRACTICE) }
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Interactive Evaluation Tester Button
        Button(
            onClick = onOpenEvaluationTester,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("btn_home_eval_tester"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = OrderCyanContainer,
                contentColor = OrderCyan
            )
        ) {
            Icon(imageVector = Icons.Default.Science, contentDescription = null, modifier = Modifier.size(20.dp), tint = OrderCyan)
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "🧪 Interactive Clue Tester (e.g. 9712 vs 9152)",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = OrderCyan
            )
        }

        // Quick How to Play Button
        OutlinedButton(
            onClick = onOpenHowToPlay,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("btn_home_learn_rules"),
            shape = RoundedCornerShape(14.dp)
        ) {
            Icon(imageVector = Icons.Default.HelpOutline, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Review Rules & Walkthrough Example")
        }
    }
}

@Composable
private fun ModeCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    testTag: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, accentColor.copy(alpha = 0.25f), RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .testTag(testTag),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(accentColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(26.dp)
                    )
                }
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = accentColor
            )
        }
    }
}
