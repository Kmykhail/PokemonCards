package com.kote.obrio.data.repository

import android.graphics.Bitmap
import com.kote.obrio.data.cache.ImageMemoryCache
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for ImageRepository and ImageMemoryCache.
 * Tests verify caching functionality and LRU eviction behavior.
 */
class ImageRepositoryTest {

    private lateinit var mockBitmaps: List<Bitmap>

    @Before
    fun setup() {
        clearCache()

        mockBitmaps = (1..25).map { mockk<Bitmap>(relaxed = true) }
    }

    @After
    fun tearDown() {
        clearCache()
    }

    private fun clearCache() {
        ImageMemoryCache.clear()
    }

    @Test
    fun testCachePutAndGet() = runTest {
        val url = "https://example.com/image1.png"
        val bitmap = mockBitmaps[0]

        ImageMemoryCache.put(url, bitmap)
        val result = ImageMemoryCache.get(url)

        assertNotNull("Bitmap should be retrieved from cache", result)
        assertEquals("Retrieved bitmap should match stored bitmap", bitmap, result)
    }

    @Test
    fun testCacheSize() = runTest {
        assertEquals("Cache should start empty", 0, ImageMemoryCache.size())

        repeat(5) { i ->
            ImageMemoryCache.put("url$i", mockBitmaps[i])
        }

        assertEquals("Cache should contain 5 items", 5, ImageMemoryCache.size())
    }

    @Test
    fun testCacheKeys() = runTest {
        val urls = listOf("url1", "url2", "url3")
        urls.forEachIndexed { index, url ->
            ImageMemoryCache.put(url, mockBitmaps[index])
        }

        val keys = ImageMemoryCache.keys()

        assertEquals("Cache should contain 3 keys", 3, keys.size)
        assertTrue("Cache should contain all stored keys", keys.containsAll(urls))
    }

    @Test
    fun testCacheLimit20Items() = runTest {
        repeat(20) { i ->
            ImageMemoryCache.put("url$i", mockBitmaps[i])
        }

        val size = ImageMemoryCache.size()
        val keys = ImageMemoryCache.keys()

        assertEquals("Cache should contain exactly 20 items", 20, size)
        assertEquals("Cache keys should contain exactly 20 entries", 20, keys.size)

        repeat(20) { i ->
            assertNotNull("Item url$i should be in cache", ImageMemoryCache.get("url$i"))
        }
    }

    @Test
    fun testEldestEntryRemoval() = runTest {
        repeat(20) { i ->
            ImageMemoryCache.put("url$i", mockBitmaps[i])
        }

        assertEquals("Cache should contain 20 items before adding 21st", 20, ImageMemoryCache.size())

        ImageMemoryCache.put("url20", mockBitmaps[20])

        assertEquals("Cache should still contain 20 items after adding 21st", 20, ImageMemoryCache.size())

        assertNull("Oldest item (url0) should have been evicted", ImageMemoryCache.get("url0"))

        assertNotNull("Newest item (url20) should be in cache", ImageMemoryCache.get("url20"))

        for (i in 1..19) {
            assertNotNull("Item url$i should still be in cache", ImageMemoryCache.get("url$i"))
        }

        val keys = ImageMemoryCache.keys()
        assertFalse("Cache keys should not contain evicted url0", keys.contains("url0"))
        assertTrue("Cache keys should contain url20", keys.contains("url20"))
    }

    @Test
    fun testMultipleEvictions() = runTest {
        // Given - add 25 items
        repeat(25) { i ->
            ImageMemoryCache.put("url$i", mockBitmaps[i])
        }

        // Then - only the last 20 should remain
        assertEquals("Cache should contain 20 items", 20, ImageMemoryCache.size())
        
        // First 5 items (url0-url4) should be evicted
        for (i in 0..4) {
            assertNull("Item url$i should have been evicted", ImageMemoryCache.get("url$i"))
        }
        
        // Last 20 items (url5-url24) should remain
        for (i in 5..24) {
            assertNotNull("Item url$i should be in cache", ImageMemoryCache.get("url$i"))
        }
    }
}
