package com.example.MyJD.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.MyJD.ui.theme.JDRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartHeader(
    cartCount: Int,
    onLocationClick: () -> Unit = {},
    onManageClick: () -> Unit = {},
    onMoreClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "购物车",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                
                if (cartCount > 0) {
                    Text(
                        text = "($cartCount)",
                        fontSize = 16.sp,
                        color = Color(0xFF666666)
                    )
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                // 地址信息
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = "地址",
                        tint = Color(0xFF666666),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "武汉纺织大学(阳光校区)-北门",
                        fontSize = 12.sp,
                        color = Color(0xFF666666)
                    )
                }
            }
        },
        actions = {
            TextButton(onClick = onManageClick) {
                Text(
                    text = "管理",
                    color = Color(0xFF333333),
                    fontSize = 14.sp
                )
            }
            
            IconButton(onClick = onMoreClick) {
                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = "更多",
                    tint = Color(0xFF666666)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White,
            titleContentColor = Color(0xFF333333)
        ),
        modifier = modifier
    )
}