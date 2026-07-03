package com.example.jd_sim.ui.screen.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.jd_sim.domain.model.CartItemSpec
import com.example.jd_sim.ui.screen.cart.CartViewModel
import com.example.jd_sim.ui.components.CartHeader
import com.example.jd_sim.ui.components.CartTabs
import com.example.jd_sim.ui.components.CartStoreSection
import com.example.jd_sim.ui.components.CartProductCard
import com.example.jd_sim.ui.screen.cart.components.*
import com.example.jd_sim.common.utils.TaskNineLogger
import com.example.jd_sim.common.utils.TaskSixteenLogger
import android.widget.Toast

@Composable
fun CartScreen(
    onNavigateBack: () -> Unit = {},
    onNavigateToHome: () -> Unit = {},
    onNavigateToCheckout: () -> Unit = {},
    viewModel: CartViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    // 使用StateFlow响应式获取购物车数据
    val specCartItems by viewModel.specCartFlow.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableStateOf("全部") }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<CartItemSpec?>(null) }

    // 计算是否全选
    val allSelected = remember(specCartItems) {
        viewModel.isAllSpecCartSelected()
    }

    // 任务九日志记录：进入购物车页面和数据加载
    LaunchedEffect(Unit) {
        TaskNineLogger.logTaskStart(context)
        TaskNineLogger.logCartPageEntered(context)

        // 强制加载购物车数据（如果为空）
        if (specCartItems.isEmpty()) {
            viewModel.forceLoadCartDataFromAssets()
        }
    }

    // 日志记录购物车数据变化
    LaunchedEffect(specCartItems) {
        android.util.Log.d("CartScreen", "Cart data updated via StateFlow: ${specCartItems.size} items")

        // 任务九日志记录：商品加载和总价计算
        TaskNineLogger.logCartItemsLoaded(context, specCartItems.size)
        val calculatedTotalPrice = viewModel.getSelectedSpecCartTotalPrice()
        TaskNineLogger.logTotalPriceCalculated(context, calculatedTotalPrice)
        TaskNineLogger.logTaskCompleted(context, calculatedTotalPrice)

        // 任务十六日志记录：检查购物车中是否有iPhone15商品
        val hasIphone15 = specCartItems.any { item ->
            item.productName.contains("iPhone15") || item.productName.contains("iPhone 15")
        }
        if (hasIphone15) {
            val iphone15Items = specCartItems.filter { item ->
                (item.productName.contains("iPhone15") || item.productName.contains("iPhone 15")) &&
                item.getSpecText().contains("黑色") && item.getSpecText().contains("256GB")
            }
            if (iphone15Items.isNotEmpty()) {
                TaskSixteenLogger.logAddToCartSuccess(context, "iPhone15 黑色 256GB", iphone15Items.first().quantity)
                TaskSixteenLogger.logCartPageEntered(context, specCartItems.size)
            }
        }
    }

    val totalCount = viewModel.getSpecCartTotalCount()
    val selectedCount = viewModel.getSelectedSpecCartCount()
    val totalPrice = viewModel.getSelectedSpecCartTotalPrice()

    if (showDeleteDialog && itemToDelete != null) {
        CartDeleteDialog(
            cartItem = itemToDelete!!,
            onConfirm = {
                viewModel.removeFromSpecCart(itemToDelete!!.id)
                showDeleteDialog = false
                itemToDelete = null
            },
            onDismiss = {
                showDeleteDialog = false
                itemToDelete = null
            }
        )
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        topBar = {
            CartHeader(
                cartCount = totalCount,
                onLocationClick = {
                    Toast.makeText(context, "地址管理功能开发中", Toast.LENGTH_SHORT).show()
                },
                onManageClick = {
                    Toast.makeText(context, "管理功能开发中", Toast.LENGTH_SHORT).show()
                },
                onMoreClick = {
                    Toast.makeText(context, "更多功能开发中", Toast.LENGTH_SHORT).show()
                }
            )
        },
        bottomBar = {
            if (specCartItems.isNotEmpty()) {
                CartNewBottomBar(
                    isAllSelected = allSelected,
                    onAllSelectToggle = {
                        viewModel.toggleAllSpecCartSelection()
                    },
                    selectedCount = selectedCount,
                    totalPrice = totalPrice,
                    onCheckout = {
                        if (selectedCount > 0) {
                            onNavigateToCheckout()
                        } else {
                            Toast.makeText(context, "请选择要结算的商品", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        if (specCartItems.isEmpty()) {
            CartEmptyContent(
                onNavigateToHome = onNavigateToHome,
                modifier = Modifier.padding(paddingValues)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFFF5F5F5))
            ) {
                item {
                    // 标签页
                    CartTabs(
                        selectedTab = selectedTab,
                        onTabSelected = { selectedTab = it }
                    )
                }

                // 按店铺分组显示商品
                val groupedItems = specCartItems.groupBy { it.storeName }
                groupedItems.forEach { (storeName, items) ->
                    item {
                        // 店铺信息
                        CartStoreSection(
                            storeName = storeName,
                            subsidyInfo = "政府补贴满1000减100",
                            isSelected = items.all { it.selected },
                            onSelectionToggle = {
                                viewModel.toggleStoreSpecCartSelection(storeName)
                            },
                            onCouponClick = {
                                Toast.makeText(context, "领券功能开发中", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }

                    items(items) { cartItem ->
                        CartProductCard(
                            cartItem = cartItem,
                            onSelectionToggle = {
                                viewModel.toggleSpecCartItemSelection(cartItem.id)
                            },
                            onQuantityChange = { newQuantity ->
                                viewModel.updateSpecCartItemQuantity(cartItem.id, newQuantity)
                            },
                            onSpecChange = {
                                Toast.makeText(context, "规格修改功能开发中", Toast.LENGTH_SHORT).show()
                            },
                            onRemove = {
                                itemToDelete = it
                                showDeleteDialog = true
                            }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                }
            }
        }
    }
}
