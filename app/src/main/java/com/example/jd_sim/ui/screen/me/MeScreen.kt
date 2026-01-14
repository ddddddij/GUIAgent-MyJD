package com.example.jd_sim.ui.screen.me

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
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.jd_sim.ui.screen.me.MeViewModel
import com.example.jd_sim.ui.components.*

@Composable
fun MeScreen(
    onNavigateToSettings: () -> Unit = {},
    onNavigateToAddress: () -> Unit = {},
    onNavigateToChat: () -> Unit = {},
    onNavigateToOrderList: (String) -> Unit = {},
    onNavigateToPlaceholder: (String) -> Unit = {},
    viewModel: MeViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val meTabData by viewModel.meTabData.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // 任务四日志记录：访问「我的」页面
    LaunchedEffect(Unit) {
        viewModel.logTaskFourMePageVisited()
    }

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
                                else -> onNavigateToPlaceholder(action.name)
                            }
                        },
                        onAvatarClick = {
                            onNavigateToPlaceholder("个人信息")
                        }
                    )
                }
                
                item {
                    MemberSection(
                        memberBenefits = data.memberBenefits,
                        userStats = data.userStats,
                        onBenefitClick = { benefit ->
                            onNavigateToPlaceholder(benefit.name)
                        }
                    )
                }
                
                item {
                    PromoBanner(
                        promoBanners = data.promoBanners,
                        onBannerClick = { banner ->
                            onNavigateToPlaceholder(banner.title)
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
                        },
                        onOrdersSectionFound = {
                            viewModel.logTaskFourOrdersSectionFound()
                        },
                        onAllOrdersClick = {
                            viewModel.logTaskFourAllOrdersClicked()
                        }
                    )
                }
                
                item {
                    AssetServiceSection(
                        assetItems = data.assetItems,
                        serviceItems = data.serviceItems,
                        onAssetClick = { asset ->
                            onNavigateToPlaceholder(asset.name)
                        },
                        onServiceClick = { service ->
                            onNavigateToPlaceholder(service.name)
                        }
                    )
                }
                
                item {
                    InteractionSection(
                        interactionItems = data.interactionItems,
                        onInteractionClick = { interaction ->
                            onNavigateToPlaceholder(interaction.name)
                        }
                    )
                }
                
                // Add some bottom padding for better scrolling
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}