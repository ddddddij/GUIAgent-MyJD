package com.example.MyJD.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun ProductDetailTopBar(
    isFavorite: Boolean,
    onBackClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onShareClick: () -> Unit,
    onMoreClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 返回按钮
        TopBarIconButton(
            icon = Icons.Filled.ArrowBack,
            onClick = onBackClick,
            contentDescription = "返回"
        )
        
        // 右侧图标组
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TopBarIconButton(
                icon = if (isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                onClick = onFavoriteClick,
                contentDescription = "收藏",
                tint = if (isFavorite) Color(0xFFFFD700) else Color.White
            )
            
            TopBarIconButton(
                icon = Icons.Filled.Share,
                onClick = onShareClick,
                contentDescription = "分享"
            )
            
            TopBarIconButton(
                icon = Icons.Filled.MoreVert,
                onClick = onMoreClick,
                contentDescription = "更多"
            )
        }
    }
}

@Composable
private fun TopBarIconButton(
    icon: ImageVector,
    onClick: () -> Unit,
    contentDescription: String,
    tint: Color = Color.White,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(40.dp)
            .background(
                color = Color.Black.copy(alpha = 0.3f),
                shape = CircleShape
            )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(24.dp)
        )
    }
}