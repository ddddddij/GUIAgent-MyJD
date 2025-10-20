package com.example.MyJD.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.MyJD.repository.DataRepository
import com.example.MyJD.viewmodel.ProductSpecViewModel
import android.widget.Toast

@Composable
fun ProductSpecDialog(
    productId: String,
    isAddToCart: Boolean = true, // true为加入购物车，false为立即购买
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    onNavigateToOrder: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val repository = remember { DataRepository.getInstance(context) }
    val viewModel: ProductSpecViewModel = viewModel(
        factory = ProductSpecViewModel.Factory(repository, productId)
    )
    
    val productSpec by viewModel.productSpec.collectAsState()
    val specSelection by viewModel.specSelection.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    var showDialog by remember { mutableStateOf(true) }
    val alpha by animateFloatAsState(
        targetValue = if (showDialog) 1f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "dialog_alpha"
    )
    
    if (alpha > 0f) {
        Dialog(
            onDismissRequest = {
                showDialog = false
                onDismiss()
            },
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                decorFitsSystemWindows = false
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(alpha)
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable { 
                        showDialog = false
                        onDismiss()
                    }
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.8f)
                        .align(Alignment.BottomCenter)
                        .clickable(enabled = false) { /* 阻止事件传播 */ },
                    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // 顶部关闭按钮
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            IconButton(
                                onClick = {
                                    showDialog = false
                                    onDismiss()
                                },
                                modifier = Modifier.align(Alignment.CenterEnd)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = "关闭",
                                    tint = Color(0xFF666666),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        
                        if (isLoading) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    color = Color(0xFFE2231A)
                                )
                            }
                        } else if (productSpec != null) {
                            LazyColumn(
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                item {
                                    // 商品信息头部
                                    ProductSpecHeader(
                                        selection = specSelection,
                                        promotionInfo = productSpec!!.promotionInfo
                                    )
                                }
                                
                                item {
                                    // 数量选择
                                    QuantitySelectorWithLabel(
                                        quantity = specSelection.quantity,
                                        onQuantityChange = viewModel::updateQuantity
                                    )
                                }
                                
                                item {
                                    HorizontalDivider(
                                        modifier = Modifier.fillMaxWidth(),
                                        color = Color(0xFFF0F0F0)
                                    )
                                }
                                
                                item {
                                    // 系列选择
                                    SeriesSelector(
                                        seriesOptions = productSpec!!.series,
                                        selectedSeries = specSelection.selectedSeries,
                                        onSeriesSelected = viewModel::selectSeries
                                    )
                                }
                                
                                item {
                                    HorizontalDivider(
                                        modifier = Modifier.fillMaxWidth(),
                                        color = Color(0xFFF0F0F0)
                                    )
                                }
                                
                                item {
                                    // 颜色选择
                                    ColorSelector(
                                        colorOptions = productSpec!!.colors,
                                        selectedColor = specSelection.selectedColor,
                                        onColorSelected = viewModel::selectColor,
                                        onZoomClick = { 
                                            Toast.makeText(context, "查看大图功能开发中", Toast.LENGTH_SHORT).show()
                                        }
                                    )
                                }
                                
                                item {
                                    HorizontalDivider(
                                        modifier = Modifier.fillMaxWidth(),
                                        color = Color(0xFFF0F0F0)
                                    )
                                }
                                
                                item {
                                    // 存储容量选择
                                    StorageSelector(
                                        storageOptions = productSpec!!.storage,
                                        selectedStorage = specSelection.selectedStorage,
                                        onStorageSelected = viewModel::selectStorage
                                    )
                                }
                                
                                item {
                                    Spacer(modifier = Modifier.height(16.dp))
                                }
                            }
                            
                            // 底部确认按钮
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                                shape = RoundedCornerShape(0.dp)
                            ) {
                                Button(
                                    onClick = {
                                        if (viewModel.canAddToCart()) {
                                            if (isAddToCart) {
                                                val success = viewModel.addToCart()
                                                if (success) {
                                                    Toast.makeText(context, "已加入购物车", Toast.LENGTH_SHORT).show()
                                                    showDialog = false
                                                    onConfirm()
                                                }
                                            } else {
                                                // 立即购买流程
                                                val orderId = viewModel.buyNow()
                                                if (orderId != null) {
                                                    Toast.makeText(context, "订单创建成功，正在跳转到结算页", Toast.LENGTH_SHORT).show()
                                                    showDialog = false
                                                    onNavigateToOrder()
                                                } else {
                                                    Toast.makeText(context, "创建订单失败，请重试", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        } else {
                                            Toast.makeText(context, "请选择完整的商品规格", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    enabled = viewModel.canAddToCart(),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                        .height(48.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFE2231A),
                                        disabledContainerColor = Color(0xFFCCCCCC)
                                    ),
                                    shape = RoundedCornerShape(24.dp)
                                ) {
                                    Text(
                                        text = "确定",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}