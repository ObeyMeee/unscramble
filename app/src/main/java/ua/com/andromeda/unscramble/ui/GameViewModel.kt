package ua.com.andromeda.unscramble.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import ua.com.andromeda.unscramble.data.MAX_NO_OF_WORDS
import ua.com.andromeda.unscramble.data.SCORE_INCREASE
import ua.com.andromeda.unscramble.data.allWords

class GameViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private lateinit var currentWord: String
    private val usedWords = mutableSetOf<String>()
    var userGuess by mutableStateOf("")
        private set

    init {
        resetGame()
    }

    private fun pickRandomWordAndShuffle(): String {
        currentWord = allWords.random()
        if (currentWord in usedWords) {
            return pickRandomWordAndShuffle()
        }
        usedWords.add(currentWord)
        return shuffleCurrentWord(currentWord)
    }

    private fun shuffleCurrentWord(word: String): String {
        val wordAsCharArray = word.toCharArray()
        wordAsCharArray.shuffle()
        while (String(wordAsCharArray) == word) {
            wordAsCharArray.shuffle()
        }
        return String(wordAsCharArray)
    }

    fun updateUserGuess(value: String) {
        userGuess = value
    }

    fun checkUserGuess() {
        if (userGuess.equals(currentWord, ignoreCase = true)) {
            val updatedScore = _uiState.value.score.plus(SCORE_INCREASE)
            updateGameState(updatedScore)
        } else {
            _uiState.update { currentState ->
                currentState.copy(isGuessedWrong = true)
            }
        }
        updateUserGuess("")
    }

    private fun updateGameState(updatedScore: Int) {
        if (usedWords.size == MAX_NO_OF_WORDS) {
            _uiState.update { currentState ->
                currentState.copy(
                    isGuessedWrong = false,
                    score = updatedScore,
                    isGameOver = true
                )
            }
        } else {
            _uiState.update { currentState ->
                currentState.copy(
                    currentScrambledWord = pickRandomWordAndShuffle(),
                    isGuessedWrong = false,
                    wordCount = currentState.wordCount.inc(),
                    score = updatedScore
                )
            }
        }
    }

    fun skipWord() {
        updateGameState(uiState.value.score)
        updateUserGuess("")
    }

    fun resetGame() {
        usedWords.clear()
        _uiState.value = GameUiState(currentScrambledWord = pickRandomWordAndShuffle())
    }
}