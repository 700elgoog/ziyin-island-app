package com.ziyin.island.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ziyin.island.ui.theme.Ink
import com.ziyin.island.ui.theme.SkyLight
import com.ziyin.island.ui.theme.Sunflower

@Composable
fun SceneBackground(
    @DrawableRes drawable: Int,
    contentDescription: String,
    overlayAlpha: Float = 0.1f,
    content: @Composable () -> Unit,
) {
    Box(Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(drawable),
            contentDescription = contentDescription,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
        if (overlayAlpha > 0f) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Black.copy(alpha = overlayAlpha), Color.Transparent, Color.Black.copy(alpha = overlayAlpha / 2f)),
                        ),
                    ),
            )
        }
        content()
    }
}

@Composable
fun PageHeader(
    title: String,
    subtitle: String? = null,
    stars: Int? = null,
    onBack: () -> Unit,
    trailing: (@Composable () -> Unit)? = null,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White.copy(alpha = 0.94f),
        shadowElevation = 8.dp,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Surface(
                modifier = Modifier
                    .size(54.dp)
                    .clickable(onClick = onBack),
                shape = CircleShape,
                color = SkyLight,
            ) {
                Box(contentAlignment = Alignment.Center) { Text("←", fontSize = 29.sp, fontWeight = FontWeight.Black) }
            }
            androidx.compose.foundation.layout.Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.headlineSmall, color = Ink, fontWeight = FontWeight.Black)
                if (subtitle != null) Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = Ink.copy(alpha = 0.72f))
            }
            if (stars != null) {
                Surface(shape = RoundedCornerShape(20.dp), color = Sunflower.copy(alpha = 0.9f)) {
                    Text("★ $stars", Modifier.padding(horizontal = 16.dp, vertical = 9.dp), fontWeight = FontWeight.Black, fontSize = 18.sp)
                }
            }
            trailing?.invoke()
        }
    }
}
