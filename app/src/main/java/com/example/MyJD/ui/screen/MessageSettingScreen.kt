package com.example.MyJD.ui.screen

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
import com.example.MyJD.presenter.MessageSettingContract
import com.example.MyJD.presenter.MessageSettingPresenter
import com.example.MyJD.repository.DataRepository
import com.example.MyJD.ui.components.SettingItemView
import com.example.MyJD.ui.components.SettingSection
import com.example.MyJD.ui.components.SettingDivider

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
    val presenter = remember { MessageSettingPresenter(repository) }
    
    var displayShopName by remember { mutableStateOf(shopName) }
    var displayShopAvatar by remember { mutableStateOf(shopAvatar) }
    var notificationEnabled by remember { mutableStateOf(false) }
    
    // MVP View implementation
    val view = remember {
        object : MessageSettingContract.View {
            override fun setShopInfo(shopName: String, shopAvatar: String) {
                displayShopName = shopName
                displayShopAvatar = shopAvatar
            }
            
            override fun showToast(message: String) {
                android.widget.Toast.makeText(context, message, android.widget.Toast.LENGTH_SHORT).show()
            }
            
            override fun updateNotificationSwitch(enabled: Boolean) {
                notificationEnabled = enabled
            }
            
            override fun navigateToShop() {
                onNavigateToShop()
            }
            
            override fun navigateBack() {
                onBackClick()
            }
        }
    }
    
    // Attach presenter
    LaunchedEffect(Unit) {
        presenter.attach(view)
        presenter.loadShopInfo(shopName, shopAvatar)
    }
    
    DisposableEffect(Unit) {
        onDispose {
            presenter.detach()
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
                    IconButton(onClick = {
                        presenter.onBackClick()
                    }) {
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
                shopName = displayShopName,
                shopAvatar = displayShopAvatar,
                modifier = Modifier.padding(vertical = 24.dp)
            )
            
            // 功能选项列表
            SettingSection {
                SettingItemView(
                    title = "进入店铺",
                    onClick = { presenter.onEnterShopClick() }
                )
                
                SettingDivider()
                
                SettingItemView(
                    title = "搜索聊天记录",
                    onClick = { presenter.onSearchChatHistoryClick() }
                )
                
                SettingDivider()
                
                SettingItemView(
                    title = "消息免打扰",
                    showArrow = false,
                    showSwitch = true,
                    switchEnabled = !notificationEnabled, // 免打扰开关与通知开关相反
                    onSwitchChanged = { enabled ->
                        presenter.onNotificationSwitchChanged(!enabled) // 传递通知开关状态
                    }
                )
                
                SettingDivider()
                
                SettingItemView(
                    title = "消息接收设置",
                    onClick = { presenter.onMessageSettingsClick() }
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // 清除本地记录按钮
            ClearRecordsButton(
                onClick = { presenter.onClearLocalRecordsClick() },
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