package com.kote.obrio.data.repository

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.kote.obrio.data.cache.ImageMemoryCache
import com.kote.obrio.domain.ImageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.net.URL

class ImageRepositoryImpl : ImageRepository {
    private val imageCache = ImageMemoryCache
    override suspend fun downloadBitmap(url: String, addToCacheIfNotExist: Boolean): Bitmap? = withContext(Dispatchers.IO) {
        imageCache.get(url)?.let { return@withContext it }

        val bitmap= try {
            val stream = URL(url).openStream()
            BitmapFactory.decodeStream(stream)
        } catch (e: Exception) {
            Timber.e("Failed decodeStream by url: $url\nexception: $e")
            null
        }

        if (addToCacheIfNotExist && bitmap != null) {
            imageCache.put(url, bitmap)
        }

        bitmap
    }
}