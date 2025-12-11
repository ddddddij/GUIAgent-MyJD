package com.example.jd_sim.ui.screen.searchresult.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jd_sim.ui.theme.JDRed

/**
 * 搜索结果页顶部搜索栏
 */
@Composable
fun SearchResultTopBar(
    searchKeyword: String,
    onBackClick: () -> Unit,
    onSearchClick: () -> Unit,
    onKeywordChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color(0xFFFF6600),
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 返回按钮
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "返回",
                    tint = Color.White
                )
            }

            // 搜索框
            Row(
                modifier = Modifier
                    .weight(1f)
                    .height(36.dp)
                    .background(
                        Color.White,
                        RoundedCornerShape(18.dp)
                    )
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BasicTextField(
                    value = searchKeyword,
                    onValueChange = onKeywordChange,
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    textStyle = TextStyle(
                        fontSize = 14.sp,
                        color = Color.Black
                    )
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // 搜索按钮
            Button(
                onClick = onSearchClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = JDRed
                ),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.height(36.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                Text(
                    text = "搜索",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
