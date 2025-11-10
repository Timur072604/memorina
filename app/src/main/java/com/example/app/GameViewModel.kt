package com.example.app

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val DELAY_ON_MATCH_MS = 600L
private const val DELAY_ON_MISMATCH_MS = 800L
private const val DELAY_BEFORE_WIN_MS = 200L

class GameViewModel : ViewModel() {

    private val _cards = MutableLiveData<List<Card>>()
    val cards: LiveData<List<Card>> = _cards

    private val _winState = MutableLiveData<Boolean>()
    val winState: LiveData<Boolean> = _winState

    private var openedCards = mutableListOf<Card>()
    private var matchedPairs = 0
    private var totalPairs = 0
    var isAnimating = false
        private set

    private val allCardImages = listOf(
        R.drawable.ic_animal_bear, R.drawable.ic_animal_cat, R.drawable.ic_animal_dog,
        R.drawable.ic_animal_elephant, R.drawable.ic_animal_lion, R.drawable.ic_animal_panda,
        R.drawable.ic_animal_rabbit, R.drawable.ic_animal_tiger
    )

    fun startGame(cardCount: Int) {
        totalPairs = cardCount / 2
        matchedPairs = 0
        openedCards.clear()
        isAnimating = false
        _winState.value = false

        val imagesForGame = allCardImages.take(totalPairs)
        val imagePairs = (imagesForGame + imagesForGame).shuffled()
        _cards.value = imagePairs.map { imageId -> Card(imageId = imageId) }
    }

    fun onCardClicked(card: Card) {
        if (isAnimating || card.isFlipped || openedCards.size >= 2) {
            return
        }

        card.isFlipped = true
        _cards.value = _cards.value?.toList()
        openedCards.add(card)

        if (openedCards.size == 2) {
            checkForMatch()
        }
    }

    private fun checkForMatch() {
        val (firstCard, secondCard) = openedCards
        if (firstCard.imageId == secondCard.imageId) {
            handleMatch()
        } else {
            handleMismatch()
        }
    }

    private fun handleMatch() {
        isAnimating = true
        viewModelScope.launch {
            delay(DELAY_ON_MATCH_MS)

            matchedPairs++
            openedCards.forEach { it.isMatched = true }
            _cards.value = _cards.value?.toList()
            openedCards.clear()

            if (matchedPairs == totalPairs) {
                delay(DELAY_BEFORE_WIN_MS)
                _winState.value = true
            }
            isAnimating = false
        }
    }

    private fun handleMismatch() {
        isAnimating = true
        viewModelScope.launch {
            delay(DELAY_ON_MISMATCH_MS)
            openedCards.forEach { it.isFlipped = false }
            openedCards.clear()
            _cards.value = _cards.value?.toList()
            isAnimating = false
        }
    }
}