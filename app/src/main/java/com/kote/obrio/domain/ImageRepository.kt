package com.kote.obrio.domain

import android.graphics.Bitmap

interface ImageRepository {
    suspend fun downloadBitmap(url: String, addToCacheIfNotExist: Boolean = true) : Bitmap?
}