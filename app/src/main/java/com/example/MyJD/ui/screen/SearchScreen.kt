package com.example.MyJD.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.MyJD.ui.theme.JDRed
import com.example.MyJD.viewmodel.SearchViewModel
import com.example.MyJD.viewmodel.SearchViewModelFactory
import com.example.MyJD.repository.DataRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onBackClick: () -> Unit = {},
    onNavigateToSearchResult: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val repository = DataRepository.getInstance(context)
    val viewModel: SearchViewModel = viewModel(factory = SearchViewModelFactory(repository))
    val suggestions by viewModel.suggestions.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val toastMessage by viewModel.toastMessage.collectAsState()
    val navigationEvent by viewModel.navigationEvent.collectAsState()
    val searchKeyword by viewModel.searchKeyword.collectAsState()
    
    // 处理Toast消息
    LaunchedEffect(toastMessage) {
        toastMessage?.let { message ->
            android.widget.Toast.makeText(context, message, android.widget.Toast.LENGTH_SHORT).show()
            viewModel.clearToast()
        }
    }
    
    // 处理导航事件
    LaunchedEffect(navigationEvent) {
        navigationEvent?.let { keyword ->
            onNavigateToSearchResult(keyword)
            viewModel.clearNavigationEvent()
        }
    }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // 顶部搜索栏
        SearchTopBar(
            searchKeyword = searchKeyword,
            onBackClick = onBackClick,
            onSearchClick = { viewModel.onSearchClicked(searchKeyword) },
            onKeywordChange = { viewModel.updateSearchKeyword(it) }
        )
        
        // 搜索建议列表
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = JDRed)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(horizontal = 16.dp)
            ) {
                items(suggestions) { suggestion ->
                    SearchSuggestionItem(
                        suggestion = suggestion,
                        highlightKeyword = "iphone15",
                        onClick = { viewModel.onSuggestionClicked(suggestion) }
                    )
                    if (suggestion != suggestions.last()) {
                        Divider(
                            color = Color(0xFFE5E5E5),
                            thickness = 0.5.dp,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchTopBar(
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
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = "搜索价",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                BasicTextField(
                    value = searchKeyword,
                    onValueChange = onKeywordChange,
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    textStyle = androidx.compose.ui.text.TextStyle(
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

@Composable
private fun SearchSuggestionItem(
    suggestion: String,
    highlightKeyword: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(18.dp)
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        // 高亮文本
        Text(
            text = buildAnnotatedString {
                val lowerSuggestion = suggestion.lowercase()
                val lowerKeyword = highlightKeyword.lowercase()
                var currentIndex = 0
                
                while (currentIndex < suggestion.length) {
                    val keywordIndex = lowerSuggestion.indexOf(lowerKeyword, currentIndex)
                    
                    if (keywordIndex == -1) {
                        // 没有找到关键字，添加剩余文本
                        append(suggestion.substring(currentIndex))
                        break
                    } else {
                        // 添加关键字前的文本
                        if (keywordIndex > currentIndex) {
                            append(suggestion.substring(currentIndex, keywordIndex))
                        }
                        
                        // 添加高亮的关键字
                        withStyle(style = SpanStyle(color = JDRed, fontWeight = FontWeight.Medium)) {
                            append(suggestion.substring(keywordIndex, keywordIndex + highlightKeyword.length))
                        }
                        
                        currentIndex = keywordIndex + highlightKeyword.length
                    }
                }
            },
            fontSize = 16.sp,
            color = Color.Black,
            modifier = Modifier.weight(1f)
        )
        
        // AI帮你挑标签
        if (suggestion.contains("iphone15") && suggestion.length < 20) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFFFE4E1),
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text(
                    text = "AI帮你挑",
                    color = Color(0xFFFF69B4),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}