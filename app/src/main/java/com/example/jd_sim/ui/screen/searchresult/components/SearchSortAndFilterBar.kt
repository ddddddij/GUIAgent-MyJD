package com.example.jd_sim.ui.screen.searchresult.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jd_sim.ui.theme.JDRed
import com.example.jd_sim.viewmodel.SearchSortType

/**
 * 排序和筛选栏
 */
@Composable
fun SearchSortAndFilterBar(
    currentSortType: SearchSortType,
    onSortClick: (SearchSortType) -> Unit,
    onFilterClick: () -> Unit,
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
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // 综合
            SearchSortButton(
                text = "综合",
                isSelected = currentSortType == SearchSortType.COMPREHENSIVE,
                onClick = { onSortClick(SearchSortType.COMPREHENSIVE) }
            )

            // 销量
            SearchSortButton(
                text = "销量",
                isSelected = currentSortType == SearchSortType.SALES,
                onClick = { onSortClick(SearchSortType.SALES) }
            )

            // 价格
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable {
                    val nextSortType = if (currentSortType == SearchSortType.PRICE_ASC) {
                        SearchSortType.PRICE_DESC
                    } else {
                        SearchSortType.PRICE_ASC
                    }
                    onSortClick(nextSortType)
                }
            ) {
                Text(
                    text = "价格",
                    color = if (currentSortType == SearchSortType.PRICE_ASC || currentSortType == SearchSortType.PRICE_DESC)
                        JDRed else Color.Gray,
                    fontSize = 14.sp
                )

                Column {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = null,
                        tint = if (currentSortType == SearchSortType.PRICE_ASC) JDRed else Color.Gray,
                        modifier = Modifier.size(12.dp)
                    )
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = if (currentSortType == SearchSortType.PRICE_DESC) JDRed else Color.Gray,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }

            // 筛选
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onFilterClick() }
            ) {
                Text(
                    text = "筛选",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

/**
 * 排序按钮
 */
@Composable
fun SearchSortButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = text,
            color = if (isSelected) JDRed else Color.Gray,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
        )
        if (isSelected) {
            Box(
                modifier = Modifier
                    .width(20.dp)
                    .height(2.dp)
                    .background(JDRed)
            )
        }
    }
}
