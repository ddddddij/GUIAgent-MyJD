package com.example.MyJD.ui.screen

import android.widget.Toast
import androidx.compose.foundation.background
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
import com.example.MyJD.viewmodel.MeViewModel
import com.example.MyJD.ui.components.*
import com.example.MyJD.utils.TaskVerifier

@Composable
fun MeScreen(
    onNavigateToSettings: () -> Unit = {},
    onNavigateToAddress: () -> Unit = {},
    onNavigateToChat: () -> Unit = {},
    onNavigateToOrderList: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val repository = remember { DataRepository.getInstance(context) }
    val viewModel: MeViewModel = viewModel(
        factory = com.example.MyJD.viewmodel.ViewModelFactory(repository)
    )
    
    val meTabData by viewModel.meTabData.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        meTabData?.let { data ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF5F5F5)),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                item {
                    UserHeader(
                        userName = viewModel.getUserDisplayName(),
                        memberLevel = viewModel.getUserMemberLevel(),
                        avatar = viewModel.getUserAvatar(),
                        hasStudentBenefit = viewModel.hasStudentBenefit(),
                        plusStatus = viewModel.getPlusStatus(),
                        redPacketStatus = viewModel.getRedPacketStatus(),
                        quickActions = data.quickActions,
                        onQuickActionClick = { action ->
                            when (action.route) {
                                "chat" -> onNavigateToChat()
                                "address" -> onNavigateToAddress()
                                "settings" -> onNavigateToSettings()
                                else -> Toast.makeText(context, "${action.name}功能待开发", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onAvatarClick = {
                            Toast.makeText(context, "个人信息页待开发", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
                
                item {
                    MemberSection(
                        memberBenefits = data.memberBenefits,
                        userStats = data.userStats,
                        onBenefitClick = { benefit ->
                            Toast.makeText(context, "${benefit.name}功能待开发", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
                
                item {
                    PromoBanner(
                        promoBanners = data.promoBanners,
                        onBannerClick = { banner ->
                            Toast.makeText(context, "${banner.title}活动页面待开发", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
                
                item {
                    OrderSection(
                        orderStatuses = data.orderStatuses,
                        onOrderStatusClick = { orderStatus ->
                            onNavigateToOrderList(orderStatus.id)
                        },
                        onViewAllClick = {
                            onNavigateToOrderList("all")
                        }
                    )
                }
                
                item {
                    AssetServiceSection(
                        assetItems = data.assetItems,
                        serviceItems = data.serviceItems,
                        onAssetClick = { asset ->
                            Toast.makeText(context, "${asset.name}功能待开发", Toast.LENGTH_SHORT).show()
                        },
                        onServiceClick = { service ->
                            Toast.makeText(context, "${service.name}功能待开发", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
                
                item {
                    InteractionSection(
                        interactionItems = data.interactionItems,
                        onInteractionClick = { interaction ->
                            Toast.makeText(context, "${interaction.name}功能待开发", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
                
                // 调试区域 - 任务验证
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "调试功能",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                            
                            Button(
                                onClick = {
                                    // 执行任务验证并输出到logcat
                                    TaskVerifier.verifyAllTasksAndLogToLogcat(context)
                                    Toast.makeText(context, "任务验证结果已输出到logcat，请查看Android Studio的Logcat", Toast.LENGTH_LONG).show()
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("验证所有任务 (查看logcat)")
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Button(
                                onClick = {
                                    // 测试数据持久化
                                    val repository = DataRepository.getInstance(context)
                                    
                                    // 添加一个测试购物车项目
                                    val testCartItem = com.example.MyJD.model.CartItemSpec(
                                        id = "test_${System.currentTimeMillis()}",
                                        productId = "test_product",
                                        productName = "测试商品",
                                        storeName = "测试店铺",
                                        image = "",
                                        price = 99.0,
                                        originalPrice = 199.0,
                                        series = "测试系列",
                                        color = "红色",
                                        storage = "64GB",
                                        quantity = 1,
                                        selected = true
                                    )
                                    repository.addToSpecCart(testCartItem)
                                    
                                    Toast.makeText(context, "已添加测试数据到购物车，重启应用测试持久化", Toast.LENGTH_LONG).show()
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("测试数据持久化")
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Button(
                                onClick = {
                                    // 测试新消息持久化
                                    val repository = DataRepository.getInstance(context)
                                    val testMessage = com.example.MyJD.model.ChatMessage(
                                        id = "test_msg_${System.currentTimeMillis()}",
                                        sender = com.example.MyJD.model.ChatSender.USER,
                                        type = com.example.MyJD.model.ChatMessageType.TEXT,
                                        content = "这是一条测试消息 - ${System.currentTimeMillis()}",
                                        timestamp = System.currentTimeMillis()
                                    )
                                    repository.addNewMessage(testMessage)
                                    
                                    val currentCount = repository.getNewMessages().size
                                    Toast.makeText(context, "已添加测试消息，当前共${currentCount}条新消息", Toast.LENGTH_LONG).show()
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("测试消息持久化 (${repository.getNewMessages().size}条)")
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Button(
                                onClick = {
                                    // 测试免打扰设置持久化
                                    val repository = DataRepository.getInstance(context)
                                    repository.setMuteSetting("Apple产品京东自营旗舰店", true)
                                    
                                    Toast.makeText(context, "已设置Apple店铺免打扰，重启应用查看持久化效果", Toast.LENGTH_LONG).show()
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("测试免打扰设置")
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Button(
                                onClick = {
                                    // 立即验证新的消息和免打扰功能
                                    TaskVerifier.verifySingleTaskAndLog(context, 20) // Task 20 是免打扰设置验证
                                    Toast.makeText(context, "已验证任务20(免打扰设置)，请查看logcat", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("验证任务20(免打扰)")
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Button(
                                onClick = {
                                    // 显示当前所有免打扰设置状态
                                    val repository = DataRepository.getInstance(context)
                                    val muteSettings = repository.getAllMuteSettings()
                                    if (muteSettings.isEmpty()) {
                                        Toast.makeText(context, "当前没有免打扰设置", Toast.LENGTH_SHORT).show()
                                    } else {
                                        val message = muteSettings.joinToString("\n") { 
                                            "${it.senderName}: ${if (it.isMuted) "已免打扰" else "未免打扰"}" 
                                        }
                                        android.util.Log.d("MeScreen", "当前免打扰设置:\n$message")
                                        Toast.makeText(context, "共${muteSettings.size}个免打扰设置，详情见logcat", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("查看免打扰状态")
                            }
                        }
                    }
                }
                
                // Add some bottom padding for better scrolling
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}