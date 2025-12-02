package com.kote.obrio.data.cache

import android.graphics.Bitmap
import androidx.collection.LruCache
import timber.log.Timber

const val MAX_ELEMENTS = 20

private class BitmapLruCache(maxSize: Int): LruCache<String, Bitmap>(maxSize) {
    override fun sizeOf(key: String, value: Bitmap): Int = 1
    override fun entryRemoved(
        evicted: Boolean,
        key: String,
        oldValue: Bitmap,
        newValue: Bitmap?
    ) {
        if (evicted) {
            Timber.d("Evicted oldest entry: $key")
        }
    }
}

object ImageMemoryCache {
    private val cache = BitmapLruCache(MAX_ELEMENTS)

    @Synchronized
    fun get(url: String) : Bitmap? = cache[url]

    @Synchronized
    fun put(url: String, bitmap: Bitmap) {
        cache.put(url, bitmap)
    }

    @Synchronized
    fun size(): Int = cache.size()

    @Synchronized
    fun keys(): List<String> = cache.snapshot().keys.toList()

    @Synchronized
    fun clear() {
        cache.evictAll()
    }
}