package com.example.jd_sim.ui.screen.settle

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.jd_sim.ui.screen.settle.SettleViewModel
import com.example.jd_sim.ui.screen.settle.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettleScreen(
    productId: String? = null,
    productName: String? = null,
    spec: String? = null,
    price: Double? = null,
    imageUrl: String? = null,
    fromCart: Boolean = false,
    fromOrder: String? = null,
    selectedAddressId: String? = null,
    onBackClick: () -> Unit,
    onNavigateToPaymentSuccess: (String) -> Unit = {},
    onNavigateToAddressList: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: SettleViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Handle selected address ID from address list
    LaunchedEffect(selectedAddressId) {
        selectedAddressId?.let { addressId ->
            viewModel.loadAddressById(addressId)
        }
    }

    // Load settle data from cart (if applicable)
    // The fromOrder case is handled by the ViewModel's init block
    LaunchedEffect(fromCart, fromOrder) {
        if (fromCart && uiState.settleData == null) {
            viewModel.loadCartSettleData()
        } else if (fromOrder != null && uiState.settleData == null) {
            viewModel.loadOrderSettleData(fromOrder)
        }
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

    // Handle navigation to address list
    LaunchedEffect(uiState.shouldNavigateToAddressList) {
        if (uiState.shouldNavigateToAddressList) {
            onNavigateToAddressList()
            viewModel.clearAddressListNavigation()
        }
    }

    // 优惠券选择对话框
    if (uiState.showCouponDialog) {
        SettleCouponSelectionDialog(
            availableCoupons = uiState.availableCoupons,
            orderAmount = uiState.currentOrderAmount,
            selectedCoupon = uiState.settleData?.selectedCoupon,
            onCouponSelected = { coupon ->
                viewModel.onCouponSelected(coupon)
                viewModel.dismissCouponDialog()
            },
            onDismiss = {
                viewModel.dismissCouponDialog()
            }
        )
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
                            SettleAddressSection(
                                address = settleData.address,
                                onClick = viewModel::onAddressClick
                            )
                        }

                        item {
                            SettleProductSection(
                                product = settleData.product,
                                quantity = uiState.quantity,
                                onQuantityIncrease = viewModel::onQuantityIncrease,
                                onQuantityDecrease = viewModel::onQuantityDecrease
                            )
                        }

                        item {
                            SettleServiceDeliverySection(
                                service = settleData.service,
                                delivery = settleData.delivery,
                                onServiceClick = viewModel::onServiceClick,
                                onDeliveryClick = viewModel::onDeliveryClick
                            )
                        }

                        item {
                            SettlePricingSection(
                                pricing = uiState.pricing ?: settleData.pricing,
                                selectedCoupon = settleData.selectedCoupon,
                                onCouponClick = viewModel::onCouponClick
                            )
                        }

                        item {
                            SettlePaymentSection()
                        }
                    }

                    // Bottom Payment Bar
                    SettleBottomPaymentBar(
                        totalAmount = uiState.pricing?.totalAmount ?: settleData.pricing.totalAmount,
                        onPaymentClick = viewModel::onPaymentClick,
                        modifier = Modifier.align(Alignment.BottomCenter)
                    )
                }
            }
        }
    }
}
