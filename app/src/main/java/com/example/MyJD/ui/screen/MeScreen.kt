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