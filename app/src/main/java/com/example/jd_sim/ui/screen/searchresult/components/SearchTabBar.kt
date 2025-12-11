package com.example.jd_sim.ui.screen.searchresult.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jd_sim.ui.theme.JDRed
import com.example.jd_sim.ui.screen.searchresult.SearchTabType

/**
 * 搜索Tab切换栏
 */
@Composable
fun SearchTabBar(
    currentTab: SearchTabType,
    onTabClick: (SearchTabType) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 0.dp)
        ) {
            // 商品Tab
            SearchTabItem(
                text = "商品",
                isSelected = currentTab == SearchTabType.PRODUCTS,
                onClick = { onTabClick(SearchTabType.PRODUCTS) },
                modifier = Modifier.weight(1f)
            )

            // 店铺Tab
            SearchTabItem(
                text = "店铺",
                isSelected = currentTab == SearchTabType.SHOPS,
                onClick = { onTabClick(SearchTabType.SHOPS) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/**
 * Tab项
 */
@Composable
fun SearchTabItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = text,
            color = if (isSelected) JDRed else Color.Gray,
            fontSize = 15.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )

        Spacer(modifier = Modifier.height(4.dp))

        if (isSelected) {
            Box(
                modifier = Modifier
                    .width(30.dp)
                    .height(3.dp)
                    .background(JDRed, RoundedCornerShape(2.dp))
            )
        }
    }
}
