package com.example.MyJD.ui.screen

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.MyJD.repository.DataRepository
import com.example.MyJD.ui.components.*
import com.example.MyJD.viewmodel.ProductDetailViewModel
import com.example.MyJD.viewmodel.ProductDetailViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    productId: String,
    onBackClick: () -> Unit,
    onCartClick: () -> Unit,
    onBuyNowClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val repository = DataRepository(context)
    val viewModel: ProductDetailViewModel = viewModel(
        factory = ProductDetailViewModelFactory(repository)
    )
    
    val productDetail by viewModel.productDetail.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val selectedColorIndex by viewModel.selectedColorIndex.collectAsState()
    val selectedPurchaseType by viewModel.selectedPurchaseType.collectAsState()
    val isFavorite by viewModel.isFavorite.collectAsState()
    
    // 规格选择弹窗状态
    var showSpecDialog by remember { mutableStateOf(false) }
    var isAddToCartMode by remember { mutableStateOf(true) }
    
    // 加载商品详情
    LaunchedEffect(productId) {
        viewModel.loadProductDetail(productId)
    }
    
    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }
    
    productDetail?.let { detail ->
        Scaffold(
            modifier = modifier.fillMaxSize(),
            containerColor = Color(0xFFF5F5F5),
            bottomBar = {
                ProductDetailBottomBar(
                    currentPrice = detail.currentPrice,
                    onStoreClick = {
                        Toast.makeText(context, "店铺功能开发中", Toast.LENGTH_SHORT).show()
                    },
                    onServiceClick = {
                        Toast.makeText(context, "客服功能开发中", Toast.LENGTH_SHORT).show()
                    },
                    onCartClick = onCartClick,
                    onAddToCartClick = {
                        isAddToCartMode = true
                        showSpecDialog = true
                    },
                    onBuyNowClick = {
                        isAddToCartMode = false
                        showSpecDialog = true
                    }
                )
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                item {
                    Box {
                        // 商品图片区域
                        ProductImageSection(
                            images = detail.images
                        )
                        
                        // 顶部导航栏（悬浮）
                        ProductDetailTopBar(
                            isFavorite = isFavorite,
                            onBackClick = onBackClick,
                            onFavoriteClick = {
                                viewModel.toggleFavorite()
                                Toast.makeText(
                                    context,
                                    if (!isFavorite) "已收藏" else "已取消收藏",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            onShareClick = {
                                Toast.makeText(context, "分享功能开发中", Toast.LENGTH_SHORT).show()
                            },
                            onMoreClick = {
                                Toast.makeText(context, "更多功能开发中", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
                
                item {
                    // 价格信息区
                    ProductPriceSection(
                        currentPrice = detail.currentPrice,
                        originalPrice = detail.originalPrice,
                        subsidyPrice = detail.subsidyPrice,
                        soldCount = detail.soldCount
                    )
                }
                
                item {
                    // 购买方式和颜色规格选择
                    ProductVariantSection(
                        purchaseTypes = detail.purchaseTypes,
                        selectedPurchaseType = selectedPurchaseType,
                        onPurchaseTypeSelected = viewModel::selectPurchaseType,
                        colors = detail.colors,
                        selectedColorIndex = selectedColorIndex,
                        onColorSelected = viewModel::selectColor
                    )
                }
                
                item {
                    // 商品信息区
                    ProductInfoSection(
                        title = detail.title,
                        tags = detail.tags,
                        specifications = detail.specifications,
                        onGiftClick = {
                            Toast.makeText(context, "送礼功能开发中", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
                
                item {
                    // 配送信息区
                    ProductDeliverySection(
                        deliveryInfo = detail.deliveryInfo,
                        onAddressClick = {
                            Toast.makeText(context, "地址修改功能开发中", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
                
                item {
                    // 促销信息区
                    ProductPromotionSection(
                        tradeIn = detail.tradeIn,
                        onTradeInClick = {
                            Toast.makeText(context, "回收功能开发中", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
                
                item {
                    // 门店信息区
                    ProductStoreSection(
                        stores = detail.stores,
                        onAppointmentClick = {
                            Toast.makeText(context, "预约功能开发中", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
                
                item {
                    // 评价信息区
                    ProductReviewSection(
                        reviews = detail.reviews,
                        onTagClick = { tagIndex ->
                            Toast.makeText(context, "评价标签功能开发中", Toast.LENGTH_SHORT).show()
                        },
                        onMoreReviewsClick = {
                            Toast.makeText(context, "查看更多评价功能开发中", Toast.LENGTH_SHORT).show()
                        },
                        onImageClick = {
                            Toast.makeText(context, "查看大图功能开发中", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
                
                // 底部间距
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
        
        // 规格选择弹窗
        if (showSpecDialog) {
            ProductSpecDialog(
                productId = productId,
                isAddToCart = isAddToCartMode,
                onDismiss = { showSpecDialog = false },
                onConfirm = { 
                    showSpecDialog = false
                    Toast.makeText(context, if (isAddToCartMode) "已加入购物车" else "正在跳转到订单页", Toast.LENGTH_SHORT).show()
                },
                onNavigateToOrder = {
                    showSpecDialog = false
                    onBuyNowClick()
                }
            )
        }
    }
}