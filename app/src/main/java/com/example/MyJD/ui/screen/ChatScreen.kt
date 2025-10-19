package com.example.MyJD.ui.screen

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.MyJD.repository.DataRepository
import com.example.MyJD.viewmodel.ChatViewModel
import com.example.MyJD.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    onNavigateToCart: () -> Unit = {},
    onNavigateToDetail: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val repository = remember { DataRepository.getInstance(context) }
    val viewModel: ChatViewModel = viewModel(
        factory = com.example.MyJD.viewmodel.ViewModelFactory(repository)
    )
    
    val filteredMessages by viewModel.filteredMessages.collectAsState()
    val selectedMessageType by viewModel.selectedMessageType.collectAsState()
    val selectedSubType by viewModel.selectedSubType.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            ChatTopBar(
                onCartClick = onNavigateToCart,
                onMoreClick = {
                    Toast.makeText(context, "更多功能待开发", Toast.LENGTH_SHORT).show()
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 消息类型标签栏
            MessageTabs(
                tabs = viewModel.getMessageTabs(),
                selectedTab = selectedMessageType,
                onTabSelected = { type ->
                    viewModel.selectMessageType(type)
                },
                getTabDisplayName = viewModel::getMessageTypeDisplayName
            )
            
            // 次级筛选标签栏
            SubTabs(
                tabs = viewModel.getSubTabs(),
                selectedTab = selectedSubType,
                onTabSelected = { subType ->
                    viewModel.selectSubType(subType)
                },
                getTabDisplayName = viewModel::getSubTypeDisplayName
            )
            
            // 消息列表
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                MessageList(
                    messages = filteredMessages,
                    onMessageClick = { message ->
                        onNavigateToDetail(message.id)
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}