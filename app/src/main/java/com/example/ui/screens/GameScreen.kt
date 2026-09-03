package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DigitStatus
import com.example.data.model.GameMode
import com.example.data.model.GuessResult
import com.example.data.model.PlayerDeductionNotes
import com.example.ui.components.DeductionPadSheet
import com.example.ui.components.DigitBoxes
import com.example.ui.components.GuessHistoryItem
import com.example.ui.components.NumberKeypad
import com.example.ui.theme.DigitAmber
import com.example.ui.theme.DigitAmberContainer
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.OrderCyan
import com.example.ui.theme.OrderCyanContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    gameMode: GameMode,
    activePlayerIndex: Int,
    roundNumber: Int,
    currentInput: String,
    errorMessage: String?,
    guesses: List<GuessResult>,
    opponentGuesses: List<GuessResult>,
    mySecret: String,
    targetSecret: String = "",
    isTargetSecretRevealed: Boolean = false,
    onToggleTargetSecretRevealed: () -> Unit = {},
    isAiThinking: Boolean,
    showDeductionSheet: Boolean,
    deductionNotes: PlayerDeductionNotes,
    onDigitClick: (Char) -> Unit,
    onBackspace: () -> Unit,
    onClear: () -> Unit,
    onSubmitGuess: () -> Unit,
    onToggleDeductionSheet: (Boolean) -> Unit,
    onToggleDigitStatus: (Int) -> Unit,
    onTogglePositionStatus: (position: Int, digit: Int) -> Unit,
    onResetDeductionNotes: () -> Unit,
    onAutoAnalyzeDeductions: () -> Unit,
    onOpenEvaluationTester: () -> Unit,
    onOpenHowToPlay: () -> Unit,
    onQuitGame: () -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    val eliminatedDigits = remember(guesses, deductionNotes) {
        val fromZeroZero = guesses.filter { it.orderCount == 0 && it.digitCount == 0 }
            .flatMap { it.guess.toList() }
            .toSet()
        val fromNotes = deductionNotes.digitStatusMap
            .filter { it.value == DigitStatus.ELIMINATED }
            .keys
            .map { it.digitToChar() }
            .toSet()
        fromZeroZero + fromNotes
    }

    // Auto scroll when new guess arrives
    LaunchedEffect(guesses.size) {
        if (guesses.isNotEmpty()) {
            listState.animateScrollToItem(guesses.size - 1)
        }
    }

    val playerName = when {
        gameMode == GameMode.SOLO_PRACTICE -> "Solo Mystery"
        gameMode == GameMode.VS_AI -> if (activePlayerIndex == 1) "Your Turn" else "AI Opponent"
        else -> if (activePlayerIndex == 1) "Player 1's Turn" else "Player 2's Turn"
    }

    val subtitle = when {
        gameMode == GameMode.SOLO_PRACTICE -> "Find the secret code in minimum turns"
        gameMode == GameMode.VS_AI -> if (activePlayerIndex == 1) "Guess the AI's secret number" else "AI is calculating deduction..."
        else -> if (activePlayerIndex == 1) "Deduce Player 2's hidden number" else "Deduce Player 1's hidden number"
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Game Header
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onQuitGame,
                    modifier = Modifier.testTag("btn_game_quit")
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back to Menu")
                }

                // Player Turn Tag
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(if (activePlayerIndex == 1) IndigoPrimary else OrderCyan)
                        )
                        Text(
                            text = playerName,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = "Attempt #${guesses.size + 1}",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(
                        onClick = onOpenEvaluationTester,
                        modifier = Modifier.testTag("btn_game_tester")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Science,
                            contentDescription = "Evaluation Tester",
                            tint = OrderCyan
                        )
                    }

                    IconButton(
                        onClick = { onToggleDeductionSheet(true) },
                        modifier = Modifier.testTag("btn_open_scratchpad")
                    ) {
                        Icon(
                            imageVector = Icons.Default.EditNote,
                            contentDescription = "Scratchpad",
                            tint = OrderCyan
                        )
                    }

                    IconButton(
                        onClick = onOpenHowToPlay,
                        modifier = Modifier.testTag("btn_game_help")
                    ) {
                        Icon(
                            imageVector = Icons.Default.HelpOutline,
                            contentDescription = "Help",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Secret status badge (your secret & target secret)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Target Card (The number you are guessing)
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = OrderCyanContainer.copy(alpha = 0.25f)),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(OrderCyan.copy(alpha = 0.4f)))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = when (gameMode) {
                                    GameMode.SOLO_PRACTICE -> "🎯 Target Code:"
                                    GameMode.VS_AI -> "🎯 AI Secret (Target):"
                                    else -> "🎯 P${3 - activePlayerIndex} Target:"
                                },
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = OrderCyan
                            )
                            Text(
                                text = if (isTargetSecretRevealed && targetSecret.isNotEmpty()) {
                                    targetSecret.map { "$it " }.joinToString("").trim()
                                } else {
                                    "● ● ● ●"
                                },
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace,
                                    color = OrderCyan
                                )
                            )
                        }

                        IconButton(
                            onClick = onToggleTargetSecretRevealed,
                            modifier = Modifier
                                .size(30.dp)
                                .testTag("btn_peek_target_secret")
                        ) {
                            Icon(
                                imageVector = if (isTargetSecretRevealed) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = "Reveal Target Secret",
                                tint = OrderCyan,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                // Defending Secret Card (if applicable)
                if (mySecret.isNotEmpty() && gameMode != GameMode.SOLO_PRACTICE) {
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "🛡️ Your Secret:",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = mySecret.map { "$it " }.joinToString("").trim(),
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = IndigoPrimary
                                )
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Center Area: Guess History Logs or AI Thinking
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
                .padding(8.dp)
        ) {
            if (isAiThinking) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        color = OrderCyan,
                        modifier = Modifier.size(44.dp),
                        strokeWidth = 3.dp
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "AI is computing possible permutations...",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = OrderCyan
                    )
                    Text(
                        text = "Filtering candidate numbers by Order and Digit clues",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else if (guesses.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(IndigoPrimary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = null,
                            tint = IndigoPrimary,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "No guesses yet",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Type a 4-digit guess below (e.g. 1234) to receive your first Order & Digit evaluation clue.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(guesses) { index, result ->
                        GuessHistoryItem(
                            result = result,
                            isLatest = index == guesses.lastIndex,
                            targetSecret = targetSecret,
                            isTargetRevealed = isTargetSecretRevealed
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Bottom Input Area: Boxes + Keypad
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Digit Boxes
            DigitBoxes(
                input = currentInput,
                isMasked = false,
                hasError = errorMessage != null,
                modifier = Modifier.testTag("game_guess_digit_boxes")
            )

            // Error banner
            AnimatedVisibility(visible = errorMessage != null) {
                if (errorMessage != null) {
                    Text(
                        text = errorMessage,
                        style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Quick Digit Tracker Bar (0-9)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Digit Status",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = if (eliminatedDigits.isNotEmpty())
                                "${eliminatedDigits.size} blurred (0-0 clues) • ${10 - eliminatedDigits.size} viable"
                            else "10 viable digits",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            ),
                            color = if (eliminatedDigits.isNotEmpty()) OrderCyan else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val orderedDigits = listOf('1', '2', '3', '4', '5', '6', '7', '8', '9', '0')
                        for (digit in orderedDigits) {
                            val isEliminated = eliminatedDigits.contains(digit)
                            val isSelected = currentInput.contains(digit)

                            Box(
                                modifier = Modifier
                                    .size(width = 28.dp, height = 24.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(
                                        when {
                                            isSelected -> IndigoPrimary
                                            isEliminated -> Color(0xFFEF4444).copy(alpha = 0.12f)
                                            else -> MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
                                        }
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = when {
                                            isSelected -> IndigoPrimary
                                            isEliminated -> Color(0xFFEF4444).copy(alpha = 0.35f)
                                            else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
                                        },
                                        shape = RoundedCornerShape(6.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = digit.toString(),
                                    modifier = if (isEliminated) Modifier.blur(2.dp) else Modifier,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp,
                                        textDecoration = if (isEliminated) TextDecoration.LineThrough else null
                                    ),
                                    color = when {
                                        isSelected -> Color.White
                                        isEliminated -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f)
                                        else -> MaterialTheme.colorScheme.onSurface
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Custom Keypad
            NumberKeypad(
                currentInput = currentInput,
                onDigitClick = onDigitClick,
                onBackspace = onBackspace,
                onClear = onClear,
                onSubmit = onSubmitGuess,
                submitLabel = "Submit Guess (${guesses.size + 1})",
                isSubmitEnabled = currentInput.length == 4 && !isAiThinking,
                eliminatedDigits = eliminatedDigits
            )
        }
    }

    // Modal BottomSheet for Deduction Scratchpad
    if (showDeductionSheet) {
        ModalBottomSheet(
            onDismissRequest = { onToggleDeductionSheet(false) },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            DeductionPadSheet(
                notes = deductionNotes,
                onToggleDigitStatus = onToggleDigitStatus,
                onTogglePositionStatus = onTogglePositionStatus,
                onResetNotes = onResetDeductionNotes,
                onAutoAnalyze = onAutoAnalyzeDeductions,
                modifier = Modifier.padding(bottom = 24.dp)
            )
        }
    }
}
