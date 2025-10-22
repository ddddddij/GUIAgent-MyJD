package com.example.MyJD.ui.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.MyJD.model.Address
import com.example.MyJD.repository.DataRepository
import com.example.MyJD.ui.components.AddressItemCard
import com.example.MyJD.viewmodel.AddressListViewModel
import com.example.MyJD.viewmodel.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressListScreen(
    refresh: Boolean = false,
    onBackClick: () -> Unit,
    onNavigateToAddressDetail: (String?) -> Unit = {},
    onNavigateToSettleScreen: (Address) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val repository = remember { DataRepository.getInstance(context) }
    val viewModel: AddressListViewModel = viewModel(
        factory = ViewModelFactory(repository)
    )
    
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val addresses by viewModel.addresses.collectAsStateWithLifecycle()
    val isEmpty by viewModel.isEmpty.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
    val navigationEvent by viewModel.navigationEvent.collectAsStateWithLifecycle()
    val toastMessage by viewModel.toastMessage.collectAsStateWithLifecycle()
    val showDeleteDialog by viewModel.showDeleteDialog.collectAsStateWithLifecycle()
    
    // Handle refresh trigger
    LaunchedEffect(refresh) {
        if (refresh) {
            viewModel.loadAddresses()
        }
    }
    
    // Handle navigation events
    LaunchedEffect(navigationEvent) {
        navigationEvent?.let { event ->
            when (event) {
                is AddressListViewModel.NavigationEvent.ToAddressDetail -> {
                    onNavigateToAddressDetail(event.addressId)
                }
                is AddressListViewModel.NavigationEvent.ToSettleScreen -> {
                    onNavigateToSettleScreen(event.selectedAddress)
                }
            }
            viewModel.clearNavigationEvent()
        }
    }
    
    // Handle toast messages
    LaunchedEffect(toastMessage) {
        toastMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.clearToastMessage()
        }
    }
    
    // Handle error messages
    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.clearErrorMessage()
        }
    }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8F8F8))
    ) {
        // 顶部导航栏
        TopAppBar(
            title = {
                Text(
                    text = "收货地址",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "返回",
                        tint = Color.White
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xFFE2231A)
            )
        )
        
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            if (isLoading) {
                // 加载状态
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Color(0xFFE2231A)
                    )
                }
            } else if (isEmpty) {
                // 空状态
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "📭",
                        fontSize = 48.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "暂无地址，请新增收货地址",
                        fontSize = 16.sp,
                        color = Color(0xFF999999)
                    )
                }
            } else {
                // 地址列表
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    items(addresses) { address ->
                        AddressItemCard(
                            address = address,
                            onAddressClick = { viewModel.onAddressClick(it) },
                            onEditClick = { viewModel.onEditAddressClick(it) },
                            onDeleteClick = { viewModel.onDeleteAddressClick(it) },
                            onCopyClick = { viewModel.onCopyAddressClick(it) },
                            onSetDefaultClick = { viewModel.onSetDefaultAddressClick(it) }
                        )
                    }
                    
                    // 底部间距，避免被新增按钮遮挡
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
            
            // 新增收货地址按钮
            Button(
                onClick = { viewModel.onAddNewAddressClick() },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(48.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFFFF4D4F),
                                    Color(0xFFFF7A45)
                                )
                            ),
                            shape = RoundedCornerShape(24.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "新增收货地址",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
            }
        }
    }
    
    // 删除确认对话框
    showDeleteDialog?.let { address ->
        AlertDialog(
            onDismissRequest = { viewModel.dismissDeleteDialog() },
            title = {
                Text(text = "删除地址")
            },
            text = {
                Text(text = "确定要删除这个收货地址吗？")
            },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.confirmDeleteAddress(address) }
                ) {
                    Text(
                        text = "删除",
                        color = Color(0xFFE53935)
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.dismissDeleteDialog() }
                ) {
                    Text(text = "取消")
                }
            }
        )
    }
}