package com.example.myjd.ui.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myjd.repository.DataRepository
import com.example.myjd.ui.components.SettingItemView
import com.example.myjd.ui.components.SettingSection
import com.example.myjd.ui.components.SettingDivider
import com.example.myjd.viewmodel.MessageSettingViewModel
import com.example.myjd.viewmodel.MessageSettingUiState
import com.example.myjd.viewmodel.MessageSettingNavigationEvent
import com.example.myjd.viewmodel.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageSettingScreen(
    shopName: String,
    shopAvatar: String,
    onBackClick: () -> Unit,
    onNavigateToShop: () -> Unit
) {
    val context = LocalContext.current
    val repository = remember { DataRepository.getInstance(context) }
    val viewModel: MessageSettingViewModel = viewModel(
        factory = ViewModelFactory(repository, context)
    )

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Load shop info when screen is first displayed or shopName/shopAvatar changes
    LaunchedEffect(shopName, shopAvatar) {
        viewModel.loadShopInfo(shopName, shopAvatar)
    }

    // Handle toast messages
    LaunchedEffect(uiState.toastMessage) {
        uiState.toastMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.clearToast()
        }
    }

    // Handle navigation events
    LaunchedEffect(uiState.navigationEvent) {
        when (val event = uiState.navigationEvent) {
            MessageSettingNavigationEvent.ToShop -> {
                onNavigateToShop()
                viewModel.clearNavigationEvent()
            }
            MessageSettingNavigationEvent.NavigateBack -> {
                onBackClick()
                viewModel.clearNavigationEvent()
            }
            MessageSettingNavigationEvent.None, null -> { /* Do nothing */ }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "消息设置",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.onBackClick() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black,
                    navigationIconContentColor = Color.Black
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5))
                .verticalScroll(rememberScrollState())
        ) {
            // 店铺信息头部区域
            ShopInfoHeader(
                shopName = uiState.shopName,
                shopAvatar = uiState.shopAvatar,
                modifier = Modifier.padding(vertical = 24.dp)
            )
            
            // 功能选项列表
            SettingSection {
                SettingItemView(
                    title = "进入店铺",
                    onClick = { viewModel.onEnterShopClick() }
                )
                
                SettingDivider()
                
                SettingItemView(
                    title = "搜索聊天记录",
                    onClick = { viewModel.onSearchChatHistoryClick() }
                )
                
                SettingDivider()
                
                SettingItemView(
                    title = "消息免打扰",
                    showArrow = false,
                    showSwitch = true,
                    switchEnabled = !uiState.isNotificationEnabled, // 免打扰状态是通知开启的反向
                    onSwitchChanged = { enabled ->
                        viewModel.onNotificationSwitchChanged(!enabled) // 传递通知开启状态（免打扰的反向）
                    }
                )
                
                SettingDivider()
                
                SettingItemView(
                    title = "消息接收设置",
                    onClick = { viewModel.onMessageSettingsClick() }
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // 清除本地记录按钮
            ClearRecordsButton(
                onClick = { viewModel.onClearLocalRecordsClick() },
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun ShopInfoHeader(
    shopName: String,
    shopAvatar: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 店铺头像
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFECF0F1)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = shopAvatar,
                    fontSize = 24.sp
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // 店铺信息
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // 认证标识
                if (shopName.contains("京东") || shopName.contains("自营")) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = Color(0xFF2196F3),
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(2.dp),
                            modifier = Modifier.padding(bottom = 4.dp)
                        ) {
                            Text(
                                text = "Authorized Reseller",
                                color = Color.White,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                    
                    Text(
                        text = "授权经销商",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                
                // 店铺名称
                Text(
                    text = shopName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
fun ClearRecordsButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFF8F8F8),
            contentColor = Color(0xFF333333)
        ),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
    ) {
        Text(
            text = "清除本地记录",
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal
        )
    }
}