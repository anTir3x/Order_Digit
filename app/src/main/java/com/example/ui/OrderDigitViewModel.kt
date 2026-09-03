package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.GameRecordRepository
import com.example.data.model.AIDifficulty
import com.example.data.model.DigitStatus
import com.example.data.model.GameMode
import com.example.data.model.GameRecord
import com.example.data.model.GameState
import com.example.data.model.GuessResult
import com.example.data.model.PlayerDeductionNotes
import com.example.logic.OrderDigitEvaluator
import com.example.logic.ValidationResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class OrderDigitUiState(
    val gameState: GameState = GameState.MODE_SELECT,
    val gameMode: GameMode = GameMode.TWO_PLAYER_PASS_AND_PLAY,
    val aiDifficulty: AIDifficulty = AIDifficulty.MEDIUM,

    // Secrets
    val player1Secret: String = "",
    val player2Secret: String = "", // Or AI secret / Solo mystery secret

    // Input state
    val currentInput: String = "",
    val isSecretMasked: Boolean = false,
    val inputErrorMessage: String? = null,

    // Current turn (1 for Player 1, 2 for Player 2 or AI)
    val activePlayerIndex: Int = 1,
    val roundNumber: Int = 1,

    // History logs
    val player1Guesses: List<GuessResult> = emptyList(),
    val player2Guesses: List<GuessResult> = emptyList(),

    // Scratchpad notes
    val player1Notes: PlayerDeductionNotes = PlayerDeductionNotes(),
    val player2Notes: PlayerDeductionNotes = PlayerDeductionNotes(),

    // AI state
    val isAiThinking: Boolean = false,
    val aiCandidatePool: List<String> = emptyList(),

    // End Game
    val winnerName: String? = null,
    val winnerPlayerIndex: Int = 0,
    val showHowToPlay: Boolean = false,
    val showDeductionSheet: Boolean = false,
    val showEvaluationTester: Boolean = false,
    val isTargetSecretRevealed: Boolean = false
)

class OrderDigitViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: GameRecordRepository =
        GameRecordRepository(AppDatabase.getDatabase(application).gameRecordDao())

    val gameRecords: StateFlow<List<GameRecord>> = repository.allRecords
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _uiState = MutableStateFlow(OrderDigitUiState())
    val uiState: StateFlow<OrderDigitUiState> = _uiState.asStateFlow()

    fun setGameMode(mode: GameMode) {
        _uiState.update {
            it.copy(
                gameMode = mode,
                gameState = GameState.SETUP_PLAYER1_SECRET,
                currentInput = "",
                inputErrorMessage = null,
                player1Secret = "",
                player2Secret = "",
                player1Guesses = emptyList(),
                player2Guesses = emptyList(),
                player1Notes = PlayerDeductionNotes(),
                player2Notes = PlayerDeductionNotes(),
                roundNumber = 1,
                activePlayerIndex = 1
            )
        }
    }

    fun setAIDifficulty(difficulty: AIDifficulty) {
        _uiState.update { it.copy(aiDifficulty = difficulty) }
    }

    fun toggleHowToPlay(show: Boolean) {
        _uiState.update { it.copy(showHowToPlay = show) }
    }

    fun toggleEvaluationTester(show: Boolean) {
        _uiState.update { it.copy(showEvaluationTester = show) }
    }

    fun toggleTargetSecretRevealed() {
        _uiState.update { it.copy(isTargetSecretRevealed = !it.isTargetSecretRevealed) }
    }

    fun toggleDeductionSheet(show: Boolean) {
        _uiState.update { it.copy(showDeductionSheet = show) }
    }

    fun toggleSecretMasking() {
        _uiState.update { it.copy(isSecretMasked = !it.isSecretMasked) }
    }

    fun onDigitInput(char: Char) {
        val current = _uiState.value.currentInput
        if (current.length >= 4) return
        if (current.isEmpty() && char == '0') {
            _uiState.update { it.copy(inputErrorMessage = "First digit cannot be 0") }
            return
        }
        if (current.contains(char)) {
            _uiState.update { it.copy(inputErrorMessage = "Digit '$char' already used") }
            return
        }

        val updated = current + char
        _uiState.update {
            it.copy(
                currentInput = updated,
                inputErrorMessage = null
            )
        }
    }

    fun onBackspace() {
        val current = _uiState.value.currentInput
        if (current.isNotEmpty()) {
            _uiState.update {
                it.copy(
                    currentInput = current.dropLast(1),
                    inputErrorMessage = null
                )
            }
        }
    }

    fun onClearInput() {
        _uiState.update {
            it.copy(
                currentInput = "",
                inputErrorMessage = null
            )
        }
    }

    fun randomizeSecret() {
        val randomSecret = OrderDigitEvaluator.generateRandomSecret()
        _uiState.update {
            it.copy(
                currentInput = randomSecret,
                inputErrorMessage = null
            )
        }
    }

    fun submitSecret() {
        val state = _uiState.value
        val validation = OrderDigitEvaluator.validateNumber(state.currentInput)
        if (validation is ValidationResult.Invalid) {
            _uiState.update { it.copy(inputErrorMessage = validation.message) }
            return
        }

        val enteredSecret = state.currentInput

        when (state.gameState) {
            GameState.SETUP_PLAYER1_SECRET -> {
                when (state.gameMode) {
                    GameMode.TWO_PLAYER_PASS_AND_PLAY -> {
                        _uiState.update {
                            it.copy(
                                player1Secret = enteredSecret,
                                currentInput = "",
                                inputErrorMessage = null,
                                gameState = GameState.PASS_TO_PLAYER2_SETUP
                            )
                        }
                    }
                    GameMode.VS_AI -> {
                        val aiSecret = OrderDigitEvaluator.generateRandomSecret()
                        _uiState.update {
                            it.copy(
                                player1Secret = enteredSecret,
                                player2Secret = aiSecret,
                                currentInput = "",
                                inputErrorMessage = null,
                                aiCandidatePool = OrderDigitEvaluator.ALL_VALID_NUMBERS,
                                gameState = GameState.PLAYING_TURN,
                                activePlayerIndex = 1
                            )
                        }
                    }
                    GameMode.SOLO_PRACTICE -> {
                        _uiState.update {
                            it.copy(
                                player1Secret = enteredSecret,
                                player2Secret = enteredSecret,
                                currentInput = "",
                                inputErrorMessage = null,
                                gameState = GameState.PLAYING_TURN,
                                activePlayerIndex = 1
                            )
                        }
                    }
                }
            }
            GameState.SETUP_PLAYER2_SECRET -> {
                _uiState.update {
                    it.copy(
                        player2Secret = enteredSecret,
                        currentInput = "",
                        inputErrorMessage = null,
                        gameState = GameState.PLAYING_PASS_INTERMISSION,
                        activePlayerIndex = 1
                    )
                }
            }
            else -> {}
        }
    }

    fun onReadyForPlayer2Setup() {
        _uiState.update {
            it.copy(
                gameState = GameState.SETUP_PLAYER2_SECRET,
                currentInput = "",
                inputErrorMessage = null
            )
        }
    }

    fun onReadyForNextTurn() {
        _uiState.update {
            it.copy(
                gameState = GameState.PLAYING_TURN,
                currentInput = "",
                inputErrorMessage = null
            )
        }
    }

    fun submitGuess() {
        val state = _uiState.value
        val guess = state.currentInput
        val validation = OrderDigitEvaluator.validateNumber(guess)
        if (validation is ValidationResult.Invalid) {
            _uiState.update { it.copy(inputErrorMessage = validation.message) }
            return
        }

        val activePlayer = state.activePlayerIndex
        // If Player 1 is guessing, evaluate against Player 2's secret
        // If Player 2 is guessing, evaluate against Player 1's secret
        val targetSecret = if (activePlayer == 1) state.player2Secret else state.player1Secret
        val (orders, digits) = OrderDigitEvaluator.evaluate(secret = targetSecret, guess = guess)

        val result = GuessResult(
            roundNumber = if (activePlayer == 1) state.player1Guesses.size + 1 else state.player2Guesses.size + 1,
            guess = guess,
            orderCount = orders,
            digitCount = digits,
            playerIndex = activePlayer
        )

        // Automatically eliminate digits in scratchpad if 0-0
        val updatedP1Notes = if (activePlayer == 1 && digits == 0) {
            applyEliminationToNotes(state.player1Notes, guess)
        } else state.player1Notes

        val updatedP2Notes = if (activePlayer == 2 && digits == 0) {
            applyEliminationToNotes(state.player2Notes, guess)
        } else state.player2Notes

        if (activePlayer == 1) {
            val updatedGuesses = state.player1Guesses + result
            if (result.isWin) {
                onGameOver(winnerName = if (state.gameMode == GameMode.SOLO_PRACTICE) "Solved!" else "Player 1", winnerIndex = 1)
                _uiState.update { it.copy(player1Guesses = updatedGuesses, player1Notes = updatedP1Notes, currentInput = "") }
                return
            }

            // Not a win yet
            when (state.gameMode) {
                GameMode.TWO_PLAYER_PASS_AND_PLAY -> {
                    _uiState.update {
                        it.copy(
                            player1Guesses = updatedGuesses,
                            player1Notes = updatedP1Notes,
                            currentInput = "",
                            activePlayerIndex = 2,
                            gameState = GameState.PLAYING_PASS_INTERMISSION
                        )
                    }
                }
                GameMode.VS_AI -> {
                    _uiState.update {
                        it.copy(
                            player1Guesses = updatedGuesses,
                            player1Notes = updatedP1Notes,
                            currentInput = "",
                            activePlayerIndex = 2,
                            isAiThinking = true
                        )
                    }
                    triggerAiTurn()
                }
                GameMode.SOLO_PRACTICE -> {
                    _uiState.update {
                        it.copy(
                            player1Guesses = updatedGuesses,
                            player1Notes = updatedP1Notes,
                            currentInput = ""
                        )
                    }
                }
            }
        } else {
            // Player 2 guess submitted (in 2P Pass & Play)
            val updatedGuesses = state.player2Guesses + result
            if (result.isWin) {
                onGameOver(winnerName = "Player 2", winnerIndex = 2)
                _uiState.update { it.copy(player2Guesses = updatedGuesses, player2Notes = updatedP2Notes, currentInput = "") }
                return
            }

            _uiState.update {
                it.copy(
                    player2Guesses = updatedGuesses,
                    player2Notes = updatedP2Notes,
                    currentInput = "",
                    activePlayerIndex = 1,
                    roundNumber = it.roundNumber + 1,
                    gameState = GameState.PLAYING_PASS_INTERMISSION
                )
            }
        }
    }

    private fun applyEliminationToNotes(
        notes: PlayerDeductionNotes,
        eliminatedGuess: String
    ): PlayerDeductionNotes {
        val newDigitMap = notes.digitStatusMap.toMutableMap()
        val newPosMatrix = notes.positionMatrix.mapValues { it.value.toMutableMap() }.toMutableMap()
        for (ch in eliminatedGuess) {
            val d = ch.digitToInt()
            newDigitMap[d] = DigitStatus.ELIMINATED
            for (pos in 0..3) {
                newPosMatrix[pos]?.put(d, DigitStatus.ELIMINATED)
            }
        }
        return notes.copy(digitStatusMap = newDigitMap, positionMatrix = newPosMatrix)
    }

    private fun triggerAiTurn() {
        viewModelScope.launch {
            delay(1200) // Realistic deduction thinking time
            val state = _uiState.value

            val aiGuess = OrderDigitEvaluator.chooseAIGuess(
                candidates = state.aiCandidatePool,
                difficulty = state.aiDifficulty,
                aiHistory = state.player2Guesses
            )

            val (orders, digits) = OrderDigitEvaluator.evaluate(
                secret = state.player1Secret,
                guess = aiGuess
            )

            val aiResult = GuessResult(
                roundNumber = state.player2Guesses.size + 1,
                guess = aiGuess,
                orderCount = orders,
                digitCount = digits,
                playerIndex = 2
            )

            val updatedAiHistory = state.player2Guesses + aiResult
            val newCandidates = OrderDigitEvaluator.filterCandidates(
                candidates = state.aiCandidatePool,
                lastGuess = aiGuess,
                lastOrders = orders,
                lastDigits = digits
            )

            if (aiResult.isWin) {
                onGameOver(winnerName = "AI Bot", winnerIndex = 2)
                _uiState.update {
                    it.copy(
                        player2Guesses = updatedAiHistory,
                        aiCandidatePool = newCandidates,
                        isAiThinking = false
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        player2Guesses = updatedAiHistory,
                        aiCandidatePool = newCandidates,
                        isAiThinking = false,
                        activePlayerIndex = 1,
                        roundNumber = it.roundNumber + 1
                    )
                }
            }
        }
    }

    private fun onGameOver(winnerName: String, winnerIndex: Int) {
        val state = _uiState.value
        _uiState.update {
            it.copy(
                gameState = GameState.GAME_OVER,
                winnerName = winnerName,
                winnerPlayerIndex = winnerIndex
            )
        }

        viewModelScope.launch {
            val record = GameRecord(
                gameMode = state.gameMode.displayName,
                winnerName = winnerName,
                player1Secret = state.player1Secret,
                player2Secret = state.player2Secret,
                player1GuessesCount = if (winnerIndex == 1) state.player1Guesses.size + 1 else state.player1Guesses.size,
                player2GuessesCount = if (winnerIndex == 2) state.player2Guesses.size + 1 else state.player2Guesses.size,
                aiDifficulty = if (state.gameMode == GameMode.VS_AI) state.aiDifficulty.label else null
            )
            repository.insertRecord(record)
        }
    }

    // Deduction Pad actions
    fun toggleDigitStatus(digit: Int) {
        val active = _uiState.value.activePlayerIndex
        val currentNotes = if (active == 1) _uiState.value.player1Notes else _uiState.value.player2Notes
        val currentStatus = currentNotes.digitStatusMap[digit] ?: DigitStatus.UNKNOWN
        val nextStatus = when (currentStatus) {
            DigitStatus.UNKNOWN -> DigitStatus.ELIMINATED
            DigitStatus.ELIMINATED -> DigitStatus.POSSIBLE
            DigitStatus.POSSIBLE -> DigitStatus.CONFIRMED
            DigitStatus.CONFIRMED -> DigitStatus.UNKNOWN
        }

        val updatedMap = currentNotes.digitStatusMap.toMutableMap().apply { put(digit, nextStatus) }
        val updatedNotes = currentNotes.copy(digitStatusMap = updatedMap)

        _uiState.update {
            if (active == 1) it.copy(player1Notes = updatedNotes)
            else it.copy(player2Notes = updatedNotes)
        }
    }

    fun togglePositionStatus(position: Int, digit: Int) {
        val active = _uiState.value.activePlayerIndex
        val currentNotes = if (active == 1) _uiState.value.player1Notes else _uiState.value.player2Notes
        val currentPosMap = currentNotes.positionMatrix[position]?.toMutableMap() ?: mutableMapOf()
        val currentStatus = currentPosMap[digit] ?: DigitStatus.UNKNOWN
        val nextStatus = when (currentStatus) {
            DigitStatus.UNKNOWN -> DigitStatus.ELIMINATED
            DigitStatus.ELIMINATED -> DigitStatus.POSSIBLE
            DigitStatus.POSSIBLE -> DigitStatus.CONFIRMED
            DigitStatus.CONFIRMED -> DigitStatus.UNKNOWN
        }
        currentPosMap[digit] = nextStatus

        val updatedMatrix = currentNotes.positionMatrix.toMutableMap().apply {
            put(position, currentPosMap)
        }
        val updatedNotes = currentNotes.copy(positionMatrix = updatedMatrix)

        _uiState.update {
            if (active == 1) it.copy(player1Notes = updatedNotes)
            else it.copy(player2Notes = updatedNotes)
        }
    }

    fun resetDeductionNotes() {
        val active = _uiState.value.activePlayerIndex
        _uiState.update {
            if (active == 1) it.copy(player1Notes = PlayerDeductionNotes())
            else it.copy(player2Notes = PlayerDeductionNotes())
        }
    }

    fun autoAnalyzeDeductions() {
        val state = _uiState.value
        val active = state.activePlayerIndex
        val guesses = if (active == 1) state.player1Guesses else state.player2Guesses
        if (guesses.isEmpty()) return

        val currentNotes = if (active == 1) state.player1Notes else state.player2Notes
        val newDigitMap = currentNotes.digitStatusMap.toMutableMap()
        val newPosMatrix = currentNotes.positionMatrix.mapValues { it.value.toMutableMap() }.toMutableMap()

        // Rule 1: Any guess with 0 digits means all 4 digits in that guess are NOT in the secret number!
        for (g in guesses) {
            if (g.digitCount == 0) {
                for (ch in g.guess) {
                    val d = ch.digitToInt()
                    newDigitMap[d] = DigitStatus.ELIMINATED
                    for (pos in 0..3) {
                        newPosMatrix[pos]?.put(d, DigitStatus.ELIMINATED)
                    }
                }
            }
            // Rule 2: If order is 0, the digits in those positions are not in those exact positions
            if (g.orderCount == 0) {
                for (pos in 0..3) {
                    val d = g.guess[pos].digitToInt()
                    if (newPosMatrix[pos]?.get(d) == DigitStatus.UNKNOWN) {
                        newPosMatrix[pos]?.put(d, DigitStatus.ELIMINATED)
                    }
                }
            }
        }

        // Rule 3: Pos 0 can never be 0
        newPosMatrix[0]?.put(0, DigitStatus.ELIMINATED)

        val updatedNotes = currentNotes.copy(
            digitStatusMap = newDigitMap,
            positionMatrix = newPosMatrix
        )

        _uiState.update {
            if (active == 1) it.copy(player1Notes = updatedNotes)
            else it.copy(player2Notes = updatedNotes)
        }
    }

    fun resetToHome() {
        _uiState.update {
            OrderDigitUiState()
        }
    }

    fun clearGameHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }
}
