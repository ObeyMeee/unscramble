package ua.com.andromeda.unscramble.ui

data class GameUiState(
    val currentScrambledWord: String = "",
    val isGuessedWrong: Boolean = false,
    val score: Int = 0,
    val wordCount: Int = 1,
    val isGameOver: Boolean = false
)