package com.example.jd_sim.ui.screen.order

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.jd_sim.domain.model.Order
import com.example.jd_sim.domain.model.OrderStatus
import com.example.jd_sim.ui.screen.order.OrderViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderScreen(
    orderType: String = "all",
    onBackClick: () -> Unit,
    onNavigateToPayment: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: OrderViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    // Initialize with specific tab if provided
    LaunchedEffect(orderType) {
        viewModel.initializeWithTab(orderType)
    }
    
    // Show toast messages
    LaunchedEffect(uiState.toastMessage) {
        uiState.toastMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.clearToast()
        }
    }
    
    // Handle navigation to payment
    LaunchedEffect(uiState.shouldNavigateToPayment) {
        uiState.shouldNavigateToPayment?.let { orderId ->
            onNavigateToPayment(orderId)
            viewModel.clearNavigation()
        }
    }
    
    // Show delete confirmation dialog
    uiState.showDeleteDialog?.let { orderId ->
        AlertDialog(
            onDismissRequest = { viewModel.clearDeleteDialog() },
            title = { Text("确认删除") },
            text = { Text("确定要删除这个订单吗？删除后将无法恢复。") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.onDeleteConfirmed(orderId)
                    }
                ) {
                    Text("确定", color = Color(0xFFE53E3E))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.clearDeleteDialog() }
                ) {
                    Text("取消")
                }
            }
        )
    }

    // Show payment success dialog
    if (uiState.showPaymentSuccessDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.clearPaymentSuccessDialog() },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("✓", fontSize = 32.sp, color = Color(0xFF4CAF50))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("支付成功", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Text(
                    "订单支付成功！订单状态已更新。",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.clearPaymentSuccessDialog() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("确定", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                }
            }
        )
    }

    // Show receipt confirmation dialog
    if (uiState.showReceiptConfirmDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.clearReceiptConfirmDialog() },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("✓", fontSize = 32.sp, color = Color(0xFF4CAF50))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("您已确认收货", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Text(
                    "确认收货成功！订单状态已更新。",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.clearReceiptConfirmDialog() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("确定", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                }
            }
        )
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        topBar = {
            Column {
                TopAppBar(
                    windowInsets = WindowInsets(0.dp),
                    title = { Text("我的订单", fontSize = 18.sp, fontWeight = FontWeight.Medium) },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                        }
                    },
                    actions = {
                        OutlinedTextField(
                            value = "",
                            onValueChange = { },
                            placeholder = { Text("搜索我的订单", fontSize = 14.sp) },
                            leadingIcon = {
                                Icon(Icons.Default.Search, contentDescription = "搜索")
                            },
                            modifier = Modifier
                                .width(200.dp)
                                .height(40.dp),
                            singleLine = true,
                            enabled = false
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFFE53E3E)
                    )
                )

                TabRow(
                    selectedTabIndex = uiState.selectedTabIndex,
                    containerColor = Color.White,
                    contentColor = Color(0xFFE53E3E)
                ) {
                    (0 until 5).forEachIndexed { index, _ ->
                        Tab(
                            selected = uiState.selectedTabIndex == index,
                            onClick = { viewModel.onTabSelected(index) },
                            text = {
                                Text(
                                    text = viewModel.getTabDisplayName(index),
                                    fontSize = 14.sp,
                                    fontWeight = if (uiState.selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        )
                    }
                }
            }
        },
        modifier = modifier
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5))
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.orders) { order ->
                        OrderCard(
                            order = order,
                            onActionClick = { action ->
                                viewModel.onActionClicked(action, order.id)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OrderCard(
    order: Order,
    onActionClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Store Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "🏪",
                        fontSize = 18.sp,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = order.items.firstOrNull()?.product?.storeName ?: "未知店铺",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Text(
                    text = getStatusDisplayText(order.status),
                    fontSize = 14.sp,
                    color = getStatusColor(order.status)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Order Items
            order.items.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    // Product Image
                    AsyncImage(
                        model = "file:///android_asset/${item.product.imageUrl}",
                        contentDescription = item.product.name,
                        modifier = Modifier
                            .size(60.dp)
                            .background(Color.Gray.copy(alpha = 0.1f), RoundedCornerShape(4.dp)),
                        contentScale = ContentScale.Crop
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    // Product Info
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.product.name,
                            fontSize = 14.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (item.selectedColor != null || item.selectedVersion != null) {
                            Text(
                                text = listOfNotNull(item.selectedColor, item.selectedVersion).joinToString(" "),
                                fontSize = 12.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                    
                    // Price and Quantity
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "¥${item.price}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Red
                        )
                        Text(
                            text = "共${item.quantity}件",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                val actions = getOrderActions(order.status)
                actions.forEach { action ->
                    OutlinedButton(
                        onClick = { onActionClick(action) },
                        modifier = Modifier.padding(start = 8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = if (action.contains("去支付") || action.contains("确认收货")) 
                                Color(0xFFE53E3E) else Color.Gray
                        )
                    ) {
                        Text(
                            text = action,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

private fun getStatusDisplayText(status: OrderStatus): String {
    return when (status) {
        OrderStatus.PENDING_PAYMENT -> "待付款"
        OrderStatus.PENDING_SHIPMENT -> "待使用"
        OrderStatus.PENDING_RECEIPT -> "待收货"
        OrderStatus.PENDING_REVIEW -> "待评价"
        OrderStatus.COMPLETED -> "已完成"
        OrderStatus.CANCELLED -> "已取消"
    }
}

private fun getStatusColor(status: OrderStatus): Color {
    return when (status) {
        OrderStatus.PENDING_PAYMENT -> Color(0xFFFF9800)
        OrderStatus.PENDING_SHIPMENT -> Color(0xFF2196F3)
        OrderStatus.PENDING_RECEIPT -> Color(0xFF4CAF50)
        OrderStatus.PENDING_REVIEW -> Color(0xFF9C27B0)
        OrderStatus.COMPLETED -> Color.Gray
        OrderStatus.CANCELLED -> Color.Red
    }
}

private fun getOrderActions(status: OrderStatus): List<String> {
    return when (status) {
        OrderStatus.PENDING_PAYMENT -> listOf("去支付", "取消订单")
        OrderStatus.PENDING_SHIPMENT -> listOf("查看券码", "申请售后")
        OrderStatus.PENDING_RECEIPT -> listOf("查看物流", "确认收货")
        OrderStatus.PENDING_REVIEW -> listOf("去评价")
        OrderStatus.COMPLETED -> listOf("删除订单", "再次购买")
        OrderStatus.CANCELLED -> listOf("删除订单", "再次购买")
    }
}