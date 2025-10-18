package com.example.MyJD.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.MyJD.ui.theme.JDRed
import com.example.MyJD.ui.theme.JDTextHint

@Composable
fun HomeHeader(
    onSearchClick: (String) -> Unit,
    onCartClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchText by remember { mutableStateOf(TextFieldValue("")) }
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(JDRed)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Top section with search and icons
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Search bar
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White)
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    BasicTextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                onSearchClick(searchText.text)
                                keyboardController?.hide()
                            }
                        ),
                        singleLine = true,
                        decorationBox = { innerTextField ->
                            if (searchText.text.isEmpty()) {
                                Text(
                                    text = "搜索商品、品牌、类目",
                                    color = JDTextHint,
                                    fontSize = 14.sp
                                )
                            }
                            innerTextField()
                        }
                    )
                    
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "搜索",
                        tint = JDRed,
                        modifier = Modifier
                            .size(20.dp)
                            .clickable {
                                onSearchClick(searchText.text)
                                keyboardController?.hide()
                            }
                    )
                }
            }
            
            // Camera icon
            Icon(
                imageVector = Icons.Filled.CameraAlt,
                contentDescription = "扫码",
                tint = Color.White,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { /* TODO: Handle camera click */ }
            )
            
            // Shopping cart icon
            Icon(
                imageVector = Icons.Filled.ShoppingCart,
                contentDescription = "购物车",
                tint = Color.White,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onCartClick() }
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Navigation tabs
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            val tabs = listOf("推荐", "11.11抢先购", "国家补贴", "手机", "三分类")
            tabs.forEachIndexed { index, tab ->
                Text(
                    text = tab,
                    color = if (index == 0) Color.White else Color.White.copy(alpha = 0.7f),
                    fontSize = 14.sp,
                    modifier = Modifier.clickable { /* TODO: Handle tab click */ }
                )
            }
        }
    }
}