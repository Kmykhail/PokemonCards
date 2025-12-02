package com.kote.obrio.ui.common

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp

@Composable
fun PokemonImage(bmp: Bitmap?) {
    bmp?.let {
        Image(
            bitmap = bmp.asImageBitmap(),
            contentDescription = "pokemon image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(120.dp)
        )
    } ?: run{
        Icon(imageVector = Icons.Default.ErrorOutline, contentDescription = null)
    }
}