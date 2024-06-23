package com.example.insight.domain_handlers

import android.graphics.Bitmap
import android.graphics.Color
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.insight.domain.gesture.GestureHandlerDomain
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.whenever
import java.nio.ByteBuffer

class GestureHandlerDomainTest {

    @Test
    fun `test trimBorders`() {
        val domain = GestureHandlerDomain()

        // Create a bitmap with a white border and a black center
        val width = 100
        val height = 100
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        for (x in 0 until width) {
            for (y in 0 until height) {
                if (x == 0 || x == width - 1 || y == 0 || y == height - 1) {
                    bitmap.setPixel(x, y, Color.WHITE)
                } else {
                    bitmap.setPixel(x, y, Color.BLACK)
                }
            }
        }

        val result = domain.trimBorders(bitmap)

        assertTrue(result.width < bitmap.width)
        assertTrue(result.height < bitmap.height)
    }

    @Test
    fun `test addWhiteSpace`() {
        val domain = GestureHandlerDomain()

        // Create a simple bitmap
        val width = 100
        val height = 100
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, Color.BLACK)
            }
        }

        val result = domain.addWhiteSpace(bitmap)

        assertTrue(result.width > bitmap.width)
        assertTrue(result.height > bitmap.height)
    }

    @Test
    fun `test preprocessBitmap`() {
        val domain = GestureHandlerDomain()

        // Create a bitmap with a white border and a black center
        val width = 100
        val height = 100
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        for (x in 0 until width) {
            for (y in 0 until height) {
                if (x == 0 || x == width - 1 || y == 0 || y == height - 1) {
                    bitmap.setPixel(x, y, Color.WHITE)
                } else {
                    bitmap.setPixel(x, y, Color.BLACK)
                }
            }
        }

        val result = domain.preprocessBitmap(bitmap)

        assertTrue(result.width < bitmap.width)
        assertTrue(result.height < bitmap.height)
    }
}