package com.kote.obrio.domain.model

import android.graphics.Bitmap

data class Pokemon(
    val id: Int,
    val name: String,
    val imageUrl: String,
    val bmp: Bitmap? = null
)
