package com.example.app

import java.util.UUID

data class Card(
    val id: String = UUID.randomUUID().toString(),
    val imageId: Int,
    var isFlipped: Boolean = false,
    var isMatched: Boolean = false
)