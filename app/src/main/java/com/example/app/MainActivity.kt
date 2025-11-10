package com.example.app

import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import androidx.activity.viewModels

class MainActivity : BaseActivity() {

    private lateinit var cardsGrid: GridLayout
    private lateinit var restartButton: Button
    private lateinit var backButton: Button
    private lateinit var winTextView: TextView

    private val viewModel: GameViewModel by viewModels()
    private val cardViews = mutableMapOf<String, AspectRatioImageView>()
    private val animDuration by lazy {
        resources.getInteger(R.integer.anim_duration_standard).toLong()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupViews()
        setupObservers()

        if (savedInstanceState == null) {
            startNewGame()
        }
    }

    private fun setupViews() {
        cardsGrid = findViewById(R.id.cardsGrid)
        restartButton = findViewById(R.id.restartButton)
        backButton = findViewById(R.id.backButtonGame)
        winTextView = findViewById(R.id.winTextView)

        restartButton.setOnClickListener {
            startNewGame()
        }

        backButton.setOnClickListener {
            finish()
        }
    }

    private fun startNewGame() {
        cardViews.clear()
        val cardCount = intent.getIntExtra("CARD_COUNT", 8)
        viewModel.startGame(cardCount)
    }

    private fun setupObservers() {
        viewModel.cards.observe(this) { cards ->
            if (cards.isNullOrEmpty()) return@observe

            winTextView.visibility = View.GONE
            cardsGrid.visibility = View.VISIBLE
            cardsGrid.alpha = 1f

            if (cardViews.isEmpty()) {
                createGrid(cards)
            } else {
                updateGrid(cards)
            }
        }
        viewModel.winState.observe(this) { hasWon ->
            if (hasWon) showWinScreen()
        }
    }

    private fun createGrid(cards: List<Card>) {
        cardsGrid.removeAllViews()
        cardsGrid.columnCount = if (cards.size == 12) 3 else 4

        for (card in cards) {
            val cardView = layoutInflater.inflate(R.layout.card_layout, cardsGrid, false) as AspectRatioImageView
            cardView.tag = R.drawable.card_back
            cardView.layoutParams = GridLayout.LayoutParams().apply {
                width = 0
                height = GridLayout.LayoutParams.WRAP_CONTENT
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                setMargins(
                    resources.getDimensionPixelSize(R.dimen.spacing_small),
                    resources.getDimensionPixelSize(R.dimen.spacing_small),
                    resources.getDimensionPixelSize(R.dimen.spacing_small),
                    resources.getDimensionPixelSize(R.dimen.spacing_small)
                )
            }
            cardView.setOnClickListener { viewModel.onCardClicked(card) }
            cardViews[card.id] = cardView
            cardsGrid.addView(cardView)
            animateCardAppearance(cardView)
        }
    }

    private fun updateGrid(cards: List<Card>) {
        for (card in cards) {
            val cardView = cardViews[card.id] ?: continue

            if (card.isMatched && cardView.visibility == View.VISIBLE) {
                cardView.animate().alpha(0f).setDuration(animDuration).withEndAction {
                    cardView.visibility = View.INVISIBLE
                }.start()
            }

            val needsFlipToFront = card.isFlipped && cardView.tag as Int != card.imageId
            val needsFlipToBack = !card.isFlipped && cardView.tag as Int == card.imageId

            if (needsFlipToFront) {
                animateFlip(cardView, card, true)
            } else if (needsFlipToBack) {
                animateFlip(cardView, card, false)
            }
        }
    }

    private fun animateCardAppearance(cardView: View) {
        cardView.alpha = 0f
        cardView.animate().alpha(1f).setDuration(animDuration).start()
    }

    private fun animateFlip(cardView: View, card: Card, toFront: Boolean) {
        val flipOut = AnimatorInflater.loadAnimator(this, R.animator.card_flip_out)
        val flipIn = AnimatorInflater.loadAnimator(this, R.animator.card_flip_in)
        flipOut.setTarget(cardView)
        flipIn.setTarget(cardView)

        flipOut.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                val imageView = cardView as AspectRatioImageView
                val imageRes = if (toFront) card.imageId else R.drawable.card_back
                imageView.setImageResource(imageRes)
                imageView.tag = imageRes
                flipIn.start()
            }
        })
        flipOut.start()
    }

    private fun showWinScreen() {
        cardsGrid.animate().alpha(0f).setDuration(animDuration).withEndAction {
            cardsGrid.visibility = View.GONE
            winTextView.alpha = 0f
            winTextView.visibility = View.VISIBLE
            winTextView.animate().alpha(1f).setDuration(animDuration).start()
        }.start()
    }
}