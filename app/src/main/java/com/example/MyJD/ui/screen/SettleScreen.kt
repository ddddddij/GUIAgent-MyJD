package com.example.MyJD.ui.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
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
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.MyJD.model.SettleData
import com.example.MyJD.model.SettlePricing
import com.example.MyJD.repository.DataRepository
import com.example.MyJD.viewmodel.SettleViewModel
import com.example.MyJD.viewmodel.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettleScreen(
    productId: String? = null,
    productName: String? = null,
    spec: String? = null,
    price: Double? = null,
    imageUrl: String? = null,
    onBackClick: () -> Unit,
    onNavigateToPaymentSuccess: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val repository = remember { DataRepository.getInstance(context) }
    val viewModel: SettleViewModel = viewModel(
        factory = ViewModelFactory(repository)
    )
    
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    // Load settle data
    LaunchedEffect(Unit) {
        viewModel.loadSettleData(productId, productName, spec, price, imageUrl)
    }
    
    // Show toast messages
    LaunchedEffect(uiState.toastMessage) {
        uiState.toastMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.clearToast()
        }
    }
    
    // Handle navigation to payment success
    LaunchedEffect(uiState.shouldNavigateToPaymentSuccess) {
        uiState.shouldNavigateToPaymentSuccess?.let { orderAmount ->
            onNavigateToPaymentSuccess(orderAmount)
            viewModel.clearNavigation()
        }
    }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Top Bar
        TopAppBar(
            title = { 
                Text(
                    "自己买", 
                    fontSize = 18.sp, 
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                ) 
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                }
            },
            actions = {
                TextButton(
                    onClick = { /* 送朋友功能暂不实现 */ }
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "送朋友",
                        tint = Color(0xFFE93B3D),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "送朋友",
                        color = Color(0xFFE93B3D),
                        fontSize = 14.sp
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.White
            )
        )
        
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            uiState.settleData?.let { settleData ->
                Box(modifier = Modifier.fillMaxSize()) {
                    // Main Content
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 80.dp), // 留出底部按钮空间
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            AddressSection(
                                address = settleData.address,
                                onClick = viewModel::onAddressClick
                            )
                        }
                        
                        item {
                            ProductSection(
                                product = settleData.product,
                                quantity = uiState.quantity,
                                onQuantityIncrease = viewModel::onQuantityIncrease,
                                onQuantityDecrease = viewModel::onQuantityDecrease
                            )
                        }
                        
                        item {
                            ServiceDeliverySection(
                                service = settleData.service,
                                delivery = settleData.delivery,
                                onServiceClick = viewModel::onServiceClick,
                                onDeliveryClick = viewModel::onDeliveryClick
                            )
                        }
                        
                        item {
                            PricingSection(
                                pricing = uiState.pricing ?: settleData.pricing,
                                onCouponClick = viewModel::onCouponClick
                            )
                        }
                        
                        item {
                            PaymentSection()
                        }
                    }
                    
                    // Bottom Payment Bar
                    BottomPaymentBar(
                        totalAmount = uiState.pricing?.totalAmount ?: settleData.pricing.totalAmount,
                        onPaymentClick = viewModel::onPaymentClick,
                        modifier = Modifier.align(Alignment.BottomCenter)
                    )
                }
            }
        }
    }
}

@Composable
private fun AddressSection(
    address: com.example.MyJD.model.SettleAddress,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Color(0xFFE93B3D),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text(
                        text = "默认",
                        color = Color.White,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Text(
                    text = "湖北武汉市江夏区",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "武汉纺织大学（阳光校区）-北门",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "${address.name} ${address.displayPhone}",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun ProductSection(
    product: com.example.MyJD.model.SettleProduct,
    quantity: Int,
    onQuantityIncrease: () -> Unit,
    onQuantityDecrease: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Store Header
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Color(0xFFE93B3D),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text(
                        text = "自营",
                        color = Color.White,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Text(
                    text = product.storeName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Product Info
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Product Image
                AsyncImage(
                    model = "file:///android_asset/${product.imageUrl}",
                    contentDescription = product.productName,
                    modifier = Modifier
                        .size(80.dp)
                        .background(Color.Gray.copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                // Product Details
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = product.productName,
                        fontSize = 14.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = Color.Black
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = product.spec,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "¥${product.price.toInt()}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE93B3D)
                    )
                }
                
                // Quantity Controls
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onQuantityDecrease,
                        modifier = Modifier.size(32.dp),
                        contentPadding = PaddingValues(0.dp),
                        shape = CircleShape
                    ) {
                        Text("-", fontSize = 14.sp)
                    }
                    
                    Text(
                        text = quantity.toString(),
                        modifier = Modifier.padding(horizontal = 16.dp),
                        fontSize = 14.sp
                    )
                    
                    OutlinedButton(
                        onClick = onQuantityIncrease,
                        modifier = Modifier.size(32.dp),
                        contentPadding = PaddingValues(0.dp),
                        shape = CircleShape
                    ) {
                        Text("+", fontSize = 14.sp)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Product Features
            Column {
                Text(
                    text = "支持7天无理由退货（防伪盘、密封条损毁不支持）",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Text(
                    text = "7天价保",
                    fontSize = 12.sp,
                    color = Color(0xFFFF9800)
                )
            }
        }
    }
}

@Composable
private fun ServiceDeliverySection(
    service: com.example.MyJD.model.SettleService,
    delivery: com.example.MyJD.model.SettleDelivery,
    onServiceClick: () -> Unit,
    onDeliveryClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Service
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onServiceClick() },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "服务",
                    fontSize = 14.sp,
                    color = Color.Black
                )
                Text(
                    text = service.title,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Delivery
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onDeliveryClick() },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "配送",
                    fontSize = 14.sp,
                    color = Color.Black
                )
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = delivery.method,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = delivery.time,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Receive Method
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onDeliveryClick() },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "收货方式",
                    fontSize = 14.sp,
                    color = Color.Black
                )
                Text(
                    text = delivery.receiveType,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
private fun PricingSection(
    pricing: SettlePricing,
    onCouponClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Product Amount
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "商品金额",
                    fontSize = 14.sp,
                    color = Color.Black
                )
                Text(
                    text = "¥${pricing.productAmount.toInt()}.00",
                    fontSize = 14.sp,
                    color = Color.Black
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Shipping Fee
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "运费",
                    fontSize = 14.sp,
                    color = Color.Black
                )
                Text(
                    text = "¥${pricing.shippingFee.toInt()}.00",
                    fontSize = 14.sp,
                    color = Color.Black
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Coupon
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onCouponClick() },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "优惠券",
                    fontSize = 14.sp,
                    color = Color.Black
                )
                Text(
                    text = "${pricing.couponCount}张可用",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Divider(color = Color.Gray.copy(alpha = 0.3f))
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Total
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "合计：",
                    fontSize = 16.sp,
                    color = Color.Black
                )
                Text(
                    text = "¥${pricing.totalAmount.toInt()}.00",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE93B3D)
                )
            }
        }
    }
}

@Composable
private fun PaymentSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "微信支付",
                tint = Color(0xFF4CAF50),
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Text(
                text = "微信支付",
                fontSize = 16.sp,
                color = Color.Black,
                modifier = Modifier.weight(1f)
            )
            
            RadioButton(
                selected = true,
                onClick = { /* 已选中，无需操作 */ }
            )
        }
    }
}

@Composable
private fun BottomPaymentBar(
    totalAmount: Double,
    onPaymentClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "¥${totalAmount.toInt()}.00",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE93B3D),
                modifier = Modifier.weight(1f)
            )
            
            Button(
                onClick = onPaymentClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE93B3D)
                ),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.width(120.dp)
            ) {
                Text(
                    text = "立即支付",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}