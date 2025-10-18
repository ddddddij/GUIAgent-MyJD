package com.example.MyJD.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.MyJD.model.CartItem
import com.example.MyJD.repository.DataRepository
import com.example.MyJD.viewmodel.HomeViewModel
import com.example.MyJD.viewmodel.ViewModelFactory
import com.example.MyJD.ui.theme.JDRed
import com.example.MyJD.ui.theme.JDTextPrimary
import com.example.MyJD.ui.theme.JDTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    onNavigateBack: () -> Unit = {},
    onNavigateToHome: () -> Unit = {}
) {
    val context = LocalContext.current
    val repository = remember { DataRepository(context) }
    val viewModel: HomeViewModel = viewModel(
        factory = ViewModelFactory(repository)
    )
    
    val shoppingCart by viewModel.shoppingCart.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "购物车(${shoppingCart.totalItemsCount})",
                        fontWeight = FontWeight.Bold
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = JDRed,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        bottomBar = {
            if (shoppingCart.items.isNotEmpty()) {
                CartBottomBar(
                    totalPrice = shoppingCart.selectedItemsTotalPrice,
                    selectedCount = shoppingCart.selectedItemsCount,
                    onCheckout = { /* TODO: Implement checkout */ }
                )
            }
        }
    ) { paddingValues ->
        if (shoppingCart.isEmpty) {
            EmptyCartContent(
                onNavigateToHome = onNavigateToHome,
                modifier = Modifier.padding(paddingValues)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(shoppingCart.items) { cartItem ->
                    CartItemCard(
                        cartItem = cartItem,
                        onQuantityChange = { newQuantity ->
                            viewModel.updateCartItemQuantity(cartItem.id, newQuantity)
                        },
                        onRemove = {
                            viewModel.removeFromCart(cartItem.id)
                        },
                        onSelectionToggle = {
                            viewModel.toggleCartItemSelection(cartItem.id)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyCartContent(
    onNavigateToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.ShoppingCart,
                contentDescription = "空购物车",
                modifier = Modifier.size(64.dp),
                tint = Color.Gray
            )
            Text(
                text = "购物车还是空的",
                fontSize = 18.sp,
                color = JDTextSecondary
            )
            Text(
                text = "快去挑选心仪的商品吧",
                fontSize = 14.sp,
                color = JDTextSecondary
            )
            Button(
                onClick = onNavigateToHome,
                colors = ButtonDefaults.buttonColors(containerColor = JDRed)
            ) {
                Text("去逛逛", color = Color.White)
            }
        }
    }
}

@Composable
private fun CartItemCard(
    cartItem: CartItem,
    onQuantityChange: (Int) -> Unit,
    onRemove: () -> Unit,
    onSelectionToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = cartItem.isSelected,
                onCheckedChange = { onSelectionToggle() },
                colors = CheckboxDefaults.colors(checkedColor = JDRed)
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            // Product image placeholder
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "📱", fontSize = 24.sp)
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = cartItem.product.name,
                    fontSize = 14.sp,
                    color = JDTextPrimary,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "¥${cartItem.product.price}",
                    fontSize = 16.sp,
                    color = JDRed,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Quantity controls
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.background(
                            Color(0xFFF5F5F5),
                            RoundedCornerShape(4.dp)
                        )
                    ) {
                        IconButton(
                            onClick = { 
                                if (cartItem.quantity > 1) {
                                    onQuantityChange(cartItem.quantity - 1)
                                }
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.Filled.Remove,
                                contentDescription = "减少",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        
                        Text(
                            text = "${cartItem.quantity}",
                            fontSize = 14.sp,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        
                        IconButton(
                            onClick = { onQuantityChange(cartItem.quantity + 1) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.Filled.Add,
                                contentDescription = "增加",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.weight(1f))
                    
                    IconButton(onClick = onRemove) {
                        Icon(
                            Icons.Filled.Delete,
                            contentDescription = "删除",
                            tint = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CartBottomBar(
    totalPrice: Double,
    selectedCount: Int,
    onCheckout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "已选商品($selectedCount)件",
                    fontSize = 12.sp,
                    color = JDTextSecondary
                )
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "合计: ¥",
                        fontSize = 14.sp,
                        color = JDTextPrimary
                    )
                    Text(
                        text = "${totalPrice.toInt()}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = JDRed
                    )
                    val decimal = ((totalPrice - totalPrice.toInt()) * 100).toInt()
                    if (decimal > 0) {
                        Text(
                            text = ".${decimal}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = JDRed
                        )
                    }
                }
            }
            
            Button(
                onClick = onCheckout,
                enabled = selectedCount > 0,
                colors = ButtonDefaults.buttonColors(containerColor = JDRed),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(
                    text = "结算($selectedCount)",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}