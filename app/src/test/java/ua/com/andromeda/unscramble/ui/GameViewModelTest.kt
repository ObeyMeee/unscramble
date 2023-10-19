package ua.com.andromeda.unscramble.ui

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import ua.com.andromeda.unscramble.data.MAX_NO_OF_WORDS
import ua.com.andromeda.unscramble.data.SCORE_INCREASE
import ua.com.andromeda.unscramble.data.getUnscrambledWord

class GameViewModelTest {
    private val viewModel = GameViewModel()

    companion object {
        private const val SCORES_AFTER_FIRST_CORRECT_ANSWER = SCORE_INCREASE
    }
    @Test
    fun gameViewModel_CorrectWordGuessed_ScoreUpdatedAndErrorFlagUnset() {
        var currentGameUiState = viewModel.uiState.value
        val correctPlayerWord = getUnscrambledWord(currentGameUiState.currentScrambledWord)
        viewModel.updateUserGuess(correctPlayerWord)
        viewModel.checkUserGuess()

        currentGameUiState = viewModel.uiState.value
        assertFalse(currentGameUiState.isGuessedWrong)
        assertEquals(SCORES_AFTER_FIRST_CORRECT_ANSWER, currentGameUiState.score)
    }

    @Test
    fun gameViewModel_IncorrectGuess_ErrorFlagSet() {
        viewModel.updateUserGuess("wrong answer")
        viewModel.checkUserGuess()

        val currentUiState = viewModel.uiState.value
        assertTrue(currentUiState.isGuessedWrong)
        assertEquals(0, currentUiState.score)
    }

    @Test
    fun gameViewModel_Initialization_FirstWordLoaded() {
        val currentUiState = viewModel.uiState.value
        val unscrambledWord = getUnscrambledWord(currentUiState.currentScrambledWord)
        assertFalse(currentUiState.isGameOver)
        assertFalse(currentUiState.isGuessedWrong)
        assertEquals(1, currentUiState.wordCount)
        assertEquals(0, currentUiState.score)
        assertNotEquals(unscrambledWord, currentUiState.currentScrambledWord)
    }

    @Test
    fun gameViewModel_AllWordsGuessed_UiStateUpdatesCorrectly() {
        var expectedScore = 0
        var currentUiState = viewModel.uiState.value
        var correctPlayerWord = getUnscrambledWord(currentUiState.currentScrambledWord)
        repeat(MAX_NO_OF_WORDS) {
            expectedScore += SCORE_INCREASE
            viewModel.updateUserGuess(correctPlayerWord)
            viewModel.checkUserGuess()
            currentUiState = viewModel.uiState.value
            correctPlayerWord = getUnscrambledWord(currentUiState.currentScrambledWord)
            assertEquals(expectedScore, currentUiState.score)
        }
        assertTrue(currentUiState.isGameOver)
        assertFalse(currentUiState.isGuessedWrong)
    }
    
    @Test
    fun gameViewModel_skipWord_ScoreUnchangedAndWordCountIncreased() {
        var currentUiState = viewModel.uiState.value
        val correctPlayerWord = getUnscrambledWord(currentUiState.currentScrambledWord)
        viewModel.updateUserGuess(correctPlayerWord)
        viewModel.checkUserGuess()

        currentUiState = viewModel.uiState.value
        val lastWordCount = currentUiState.wordCount
        viewModel.skipWord()
        currentUiState = viewModel.uiState.value

        assertEquals(SCORES_AFTER_FIRST_CORRECT_ANSWER, currentUiState.score)
        assertEquals(lastWordCount + 1, currentUiState.wordCount)
        assertFalse(currentUiState.isGameOver)
    }
}