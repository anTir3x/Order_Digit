package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class GameMode(val displayName: String, val description: String) {
    TWO_PLAYER_PASS_AND_PLAY("2 Players", "Pass & Play on same device"),
    VS_AI("vs AI", "Play against intelligent deduction bot"),
    SOLO_PRACTICE("Solo Mystery", "Deduce the secret in fewest turns")
}

enum class AIDifficulty(val label: String, val description: String) {
    EASY("Novice", "Random deductive guesses"),
    MEDIUM("Tactician", "Considers all past clues"),
    MASTER("Grandmaster", "Minimax optimal deduction")
}

enum class GameState {
    MODE_SELECT,
    SETUP_PLAYER1_SECRET,
    PASS_TO_PLAYER2_SETUP,
    SETUP_PLAYER2_SECRET,
    PLAYING_PASS_INTERMISSION,
    PLAYING_TURN,
    GAME_OVER
}

data class GuessResult(
    val roundNumber: Int,
    val guess: String,
    val orderCount: Int,
    val digitCount: Int,
    val playerIndex: Int, // 1 for Player 1, 2 for Player 2 / AI
    val timestamp: Long = System.currentTimeMillis()
) {
    val isWin: Boolean get() = orderCount == 4 && digitCount == 4
}

enum class DigitStatus {
    UNKNOWN,
    ELIMINATED, // Marked ❌
    POSSIBLE,   // Marked ❓
    CONFIRMED   // Marked ⭐
}

data class PlayerDeductionNotes(
    val digitStatusMap: Map<Int, DigitStatus> = (0..9).associateWith { DigitStatus.UNKNOWN },
    // 4 positions: index 0..3, for each position map digit 0..9 to DigitStatus
    val positionMatrix: Map<Int, Map<Int, DigitStatus>> = (0..3).associateWith {
        (0..9).associateWith { DigitStatus.UNKNOWN }
    }
)

@Entity(tableName = "game_records")
data class GameRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val gameMode: String,
    val winnerName: String,
    val player1Secret: String,
    val player2Secret: String,
    val player1GuessesCount: Int,
    val player2GuessesCount: Int,
    val aiDifficulty: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
