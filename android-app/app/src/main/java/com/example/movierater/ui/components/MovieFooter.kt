package com.example.movierater.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

enum class FooterTab { Movies, Favorites }

@Composable
fun MovieFooter(
    currentTab: FooterTab,
    favoritesCount: Int,
    onTabSelected: (FooterTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    // Bounce when count changes
    var bounce by remember { mutableStateOf(false) }
    LaunchedEffect(favoritesCount) {
        bounce = true
        delay(350)
        bounce = false
    }
    val scale by animateFloatAsState(
        targetValue = if (bounce) 1.12f else 1f,
        animationSpec = tween(durationMillis = 220),
        label = "favBounce",
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(Color.Black.copy(alpha = 0.7f))
                .padding(4.dp)
                .height(48.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SegmentButton(
                label = "Movies",
                selected = currentTab == FooterTab.Movies,
                onClick = { onTabSelected(FooterTab.Movies) },
                modifier = Modifier.weight(1f),
            )
            SegmentButton(
                label = "$favoritesCount favorite${if (favoritesCount != 1) "s" else ""}",
                selected = currentTab == FooterTab.Favorites,
                onClick = { onTabSelected(FooterTab.Favorites) },
                modifier = Modifier
                    .weight(1f)
                    .scale(scale),
            )
        }
    }
}

@Composable
private fun SegmentButton(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(if (selected) Color.White else Color.Transparent)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            color = if (selected) Color.Black else Color.White,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
            style = MaterialTheme.typography.labelLarge,
        )
    }
}
