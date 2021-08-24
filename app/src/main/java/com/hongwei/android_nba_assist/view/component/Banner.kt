package com.hongwei.android_nba_assist.view.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter

@Composable
fun Banner(url: String?, modifier: Modifier = Modifier) {
    val painter = rememberImagePainter(
        data = url,
        builder = {
            crossfade(true)
        }
    )

    Surface(
        modifier = modifier
            .shadow(10.dp, RoundedCornerShape(bottomStart = 6.dp, bottomEnd = 6.dp))
    ) {
        Image(
            painter = painter,
            contentScale = ContentScale.FillWidth,
            contentDescription = null,
            modifier = modifier
                .clip(RoundedCornerShape(bottomStart = 6.dp, bottomEnd = 6.dp))
        )
    }
}