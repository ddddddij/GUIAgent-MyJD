package com.example.MyJD.presentation.ui.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.MyJD.data.repository.DataRepository
import com.example.MyJD.presentation.ui.theme.JDRed
import com.example.MyJD.presentation.ui.theme.JDTextPrimary
import com.example.MyJD.viewmodel.ThinkPadDetailViewModel
import com.example.MyJD.viewmodel.ThinkPadDetailViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThinkPadDetailScreen(
    productId: String,
    onBackClick: () -> Unit,
    onCartClick: () -> Unit,
    onBuyNowClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val repository = DataRepository.getInstance(context)
    val viewModel: ThinkPadDetailViewModel = viewModel(
        factory = ThinkPadDetailViewModelFactory(repository, context)
    )
    
    val productDetail by viewModel.productDetail.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    LaunchedEffect(productId) {
        viewModel.loadProductDetail(productId)
    }
    
    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }
    
    productDetail?.let { detail ->
        Scaffold(
            modifier = modifier.fillMaxSize(),
            containerColor = Color(0xFFFFFFFF),
            topBar = {
                TopAppBar(
                    title = { 
                        Text(
                            text = "商品详情",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "返回"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.White,
                        titleContentColor = JDTextPrimary
                    )
                )
            },
            bottomBar = {
                // 底部操作栏
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .background(Color.White)
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // 加入购物车按钮
                    Button(
                        onClick = { 
                            Toast.makeText(context, "已加入购物车", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFF8F8F8)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "加入购物车",
                            color = Color(0xFF333333),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    
                    // 立即购买按钮
                    Button(
                        onClick = { 
                            Toast.makeText(context, "立即购买", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFF0000)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "立即购买",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    // 商品主图
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .padding(horizontal = 16.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFF5F5F5)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (detail.images.isNotEmpty()) {
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data("file:///android_asset/${detail.images.first()}")
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "商品图片",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Text(
                                text = "💻",
                                fontSize = 64.sp,
                                color = Color(0xFFCCCCCC)
                            )
                        }
                    }
                }
                
                item {
                    // 商品名称
                    Text(
                        text = detail.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF333333),
                        modifier = Modifier.padding(horizontal = 16.dp),
                        lineHeight = 24.sp
                    )
                }
                
                item {
                    // 价格与补贴信息
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Text(
                                text = "¥",
                                fontSize = 18.sp,
                                color = Color(0xFFFF0000),
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = String.format("%.1f", detail.currentPrice),
                                fontSize = 22.sp,
                                color = Color(0xFFFF0000),
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            if (detail.originalPrice > detail.currentPrice) {
                                Text(
                                    text = "¥${String.format("%.0f", detail.originalPrice)}",
                                    fontSize = 14.sp,
                                    color = Color(0xFF666666),
                                    style = androidx.compose.ui.text.TextStyle(
                                        textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                                    )
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // 政府补贴标识
                        if (detail.subsidyPrice.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .background(
                                        Color(0xFF00BFA5),
                                        RoundedCornerShape(12.dp)
                                    )
                                    .padding(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = detail.subsidyPrice,
                                    fontSize = 12.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
                
                item {
                    // 配置规格
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        Text(
                            text = "配置规格",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = JDTextPrimary,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        
                        // 规格信息
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Color(0xFFF0F0F0),
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(12.dp)
                        ) {
                            Column {
                                Text(
                                    text = "E14超能版 黑色",
                                    fontSize = 14.sp,
                                    color = Color(0xFF333333),
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "酷睿5 220H，16G，512G固态",
                                    fontSize = 12.sp,
                                    color = Color(0xFF666666)
                                )
                            }
                        }
                    }
                }
                
                item {
                    // 优惠信息区
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        Text(
                            text = "优惠活动",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = JDTextPrimary,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        
                        // 优惠标签
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(detail.tags) { tag ->
                                Box(
                                    modifier = Modifier
                                        .background(
                                            if (tag.contains("国家补贴")) Color(0xFFE8F5E8) else Color(0xFFF0F0F0),
                                            RoundedCornerShape(12.dp)
                                        )
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = tag,
                                        fontSize = 12.sp,
                                        color = if (tag.contains("国家补贴")) Color(0xFF00BFA5) else Color(0xFF666666),
                                        fontWeight = if (tag.contains("国家补贴")) FontWeight.Medium else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }
                }
                
                item {
                    // 购买类型选择
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        Text(
                            text = "购买方式",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = JDTextPrimary,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(detail.purchaseTypes) { type ->
                                Box(
                                    modifier = Modifier
                                        .background(
                                            Color(0xFFF0F0F0),
                                            RoundedCornerShape(8.dp)
                                        )
                                        .padding(horizontal = 12.dp, vertical = 8.dp)
                                ) {
                                    Text(
                                        text = type,
                                        fontSize = 14.sp,
                                        color = Color(0xFF666666)
                                    )
                                }
                            }
                        }
                    }
                }
                
                item {
                    // 分隔线
                    HorizontalDivider(
                        color = Color(0xFFEEEEEE),
                        thickness = 1.dp,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
                
                // 底部间距
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}