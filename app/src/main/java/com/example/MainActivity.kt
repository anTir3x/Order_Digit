package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.GameMode
import com.example.data.model.GameState
import com.example.ui.OrderDigitViewModel
import com.example.ui.components.EvaluationTesterDialog
import com.example.ui.components.HowToPlayDialog
import com.example.ui.screens.GameScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.PassTurnScreen
import com.example.ui.screens.SetupSecretScreen
import com.example.ui.screens.StatsScreen
import com.example.ui.screens.VictoryScreen
import com.example.ui.theme.OrderDigitTheme

class MainActivity : ComponentActivity() {

    private val viewModel: OrderDigitViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OrderDigitTheme {
                OrderDigitApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun OrderDigitApp(
    viewModel: OrderDigitViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val records by viewModel.gameRecords.collectAsStateWithLifecycle()
    var showStatsScreen by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        val screenModifier = Modifier.padding(innerPadding)

        if (showStatsScreen) {
            StatsScreen(
                records = records,
                onBack = { showStatsScreen = false },
                onClearHistory = { viewModel.clearGameHistory() },
                modifier = screenModifier
            )
        } else {
            when (uiState.gameState) {
                GameState.MODE_SELECT -> {
                    HomeScreen(
                        onSelectMode = { mode -> viewModel.setGameMode(mode) },
                        selectedDifficulty = uiState.aiDifficulty,
                        onSelectDifficulty = { diff -> viewModel.setAIDifficulty(diff) },
                        onOpenHowToPlay = { viewModel.toggleHowToPlay(true) },
                        onOpenEvaluationTester = { viewModel.toggleEvaluationTester(true) },
                        onOpenStats = { showStatsScreen = true },
                        modifier = screenModifier
                    )
                }

                GameState.SETUP_PLAYER1_SECRET -> {
                    SetupSecretScreen(
                        title = if (uiState.gameMode == GameMode.SOLO_PRACTICE) "Practice Setup" else "Player 1: Secret Code",
                        subtitle = if (uiState.gameMode == GameMode.SOLO_PRACTICE) "Pick a 4-digit number to practice on or randomize" else "Choose your secret 4-digit number for Player 2 to guess",
                        currentInput = uiState.currentInput,
                        isMasked = uiState.isSecretMasked,
                        errorMessage = uiState.inputErrorMessage,
                        onDigitClick = { viewModel.onDigitInput(it) },
                        onBackspace = { viewModel.onBackspace() },
                        onClear = { viewModel.onClearInput() },
                        onToggleMask = { viewModel.toggleSecretMasking() },
                        onRandomize = { viewModel.randomizeSecret() },
                        onSubmit = { viewModel.submitSecret() },
                        onBack = { viewModel.resetToHome() },
                        modifier = screenModifier
                    )
                }

                GameState.PASS_TO_PLAYER2_SETUP -> {
                    PassTurnScreen(
                        targetPlayerName = "Player 2",
                        actionDescription = "Player 1 has set their secret code. Please pass device to Player 2 to select their secret number.",
                        onReadyClicked = { viewModel.onReadyForPlayer2Setup() },
                        modifier = screenModifier
                    )
                }

                GameState.SETUP_PLAYER2_SECRET -> {
                    SetupSecretScreen(
                        title = "Player 2: Secret Code",
                        subtitle = "Choose your secret 4-digit number for Player 1 to guess",
                        currentInput = uiState.currentInput,
                        isMasked = uiState.isSecretMasked,
                        errorMessage = uiState.inputErrorMessage,
                        onDigitClick = { viewModel.onDigitInput(it) },
                        onBackspace = { viewModel.onBackspace() },
                        onClear = { viewModel.onClearInput() },
                        onToggleMask = { viewModel.toggleSecretMasking() },
                        onRandomize = { viewModel.randomizeSecret() },
                        onSubmit = { viewModel.submitSecret() },
                        onBack = { viewModel.resetToHome() },
                        modifier = screenModifier
                    )
                }

                GameState.PLAYING_PASS_INTERMISSION -> {
                    val nextPlayerName = if (uiState.activePlayerIndex == 1) "Player 1" else "Player 2"
                    val prompt = if (uiState.activePlayerIndex == 1) "Turn to guess Player 2's hidden number." else "Turn to guess Player 1's hidden number."
                    PassTurnScreen(
                        targetPlayerName = nextPlayerName,
                        actionDescription = "Pass device to $nextPlayerName. $prompt",
                        onReadyClicked = { viewModel.onReadyForNextTurn() },
                        modifier = screenModifier
                    )
                }

                GameState.PLAYING_TURN -> {
                    val active = uiState.activePlayerIndex
                    val activeGuesses = if (active == 1) uiState.player1Guesses else uiState.player2Guesses
                    val opponentGuesses = if (active == 1) uiState.player2Guesses else uiState.player1Guesses
                    val mySecret = if (active == 1) uiState.player1Secret else uiState.player2Secret
                    val targetSecret = if (active == 1) uiState.player2Secret else uiState.player1Secret
                    val notes = if (active == 1) uiState.player1Notes else uiState.player2Notes

                    GameScreen(
                        gameMode = uiState.gameMode,
                        activePlayerIndex = active,
                        roundNumber = uiState.roundNumber,
                        currentInput = uiState.currentInput,
                        errorMessage = uiState.inputErrorMessage,
                        guesses = activeGuesses,
                        opponentGuesses = opponentGuesses,
                        mySecret = mySecret,
                        targetSecret = targetSecret,
                        isTargetSecretRevealed = uiState.isTargetSecretRevealed,
                        onToggleTargetSecretRevealed = { viewModel.toggleTargetSecretRevealed() },
                        isAiThinking = uiState.isAiThinking,
                        showDeductionSheet = uiState.showDeductionSheet,
                        deductionNotes = notes,
                        onDigitClick = { viewModel.onDigitInput(it) },
                        onBackspace = { viewModel.onBackspace() },
                        onClear = { viewModel.onClearInput() },
                        onSubmitGuess = { viewModel.submitGuess() },
                        onToggleDeductionSheet = { viewModel.toggleDeductionSheet(it) },
                        onToggleDigitStatus = { viewModel.toggleDigitStatus(it) },
                        onTogglePositionStatus = { pos, digit -> viewModel.togglePositionStatus(pos, digit) },
                        onResetDeductionNotes = { viewModel.resetDeductionNotes() },
                        onAutoAnalyzeDeductions = { viewModel.autoAnalyzeDeductions() },
                        onOpenEvaluationTester = { viewModel.toggleEvaluationTester(true) },
                        onOpenHowToPlay = { viewModel.toggleHowToPlay(true) },
                        onQuitGame = { viewModel.resetToHome() },
                        modifier = screenModifier
                    )
                }

                GameState.GAME_OVER -> {
                    VictoryScreen(
                        winnerName = uiState.winnerName ?: "Player 1",
                        winnerPlayerIndex = uiState.winnerPlayerIndex,
                        gameMode = uiState.gameMode,
                        player1Secret = uiState.player1Secret,
                        player2Secret = uiState.player2Secret,
                        player1Guesses = uiState.player1Guesses,
                        player2Guesses = uiState.player2Guesses,
                        onRematch = { viewModel.setGameMode(uiState.gameMode) },
                        onBackHome = { viewModel.resetToHome() },
                        modifier = screenModifier
                    )
                }
            }
        }

        // How to Play Modal Dialog
        if (uiState.showHowToPlay) {
            HowToPlayDialog(
                onDismiss = { viewModel.toggleHowToPlay(false) },
                onOpenEvaluationTester = { viewModel.toggleEvaluationTester(true) }
            )
        }

        // Interactive Evaluation Tester Dialog
        if (uiState.showEvaluationTester) {
            val activeTarget = if (uiState.activePlayerIndex == 1) uiState.player2Secret else uiState.player1Secret
            EvaluationTesterDialog(
                initialSecret = if (activeTarget.isNotEmpty()) activeTarget else "9712",
                initialGuess = "9152",
                onDismiss = { viewModel.toggleEvaluationTester(false) }
            )
        }
    }
}
