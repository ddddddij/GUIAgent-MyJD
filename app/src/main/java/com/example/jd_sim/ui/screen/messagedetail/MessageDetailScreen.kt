package com.example.jd_sim.ui.screen.messagedetail

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.jd_sim.ui.screen.messagedetail.MessageDetailViewModel
import com.example.jd_sim.ui.screen.messagedetail.NavigationEvent
import com.example.jd_sim.ui.screen.messagedetail.components.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageDetailScreen(
    conversationId: String,
    onBackClick: () -> Unit,
    onNavigateToProduct: (String) -> Unit,
    onNavigateToSettings: (String, String) -> Unit = { _, _ -> },
    onNavigateToShop: (String) -> Unit = {},
    viewModel: MessageDetailViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current

    // Load conversation data when screen is first displayed or conversationId changes
    LaunchedEffect(conversationId) {
        viewModel.loadConversation(conversationId)
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
            is NavigationEvent.ToProductDetail -> {
                onNavigateToProduct(event.productId)
                viewModel.clearNavigationEvent()
            }
            NavigationEvent.NavigateBack -> {
                onBackClick()
                viewModel.clearNavigationEvent()
            }
            NavigationEvent.None, null -> { /* Do nothing */ }
        }
    }

    // Handle scroll to bottom event
    LaunchedEffect(uiState.scrollToBottomEvent) {
        if (uiState.scrollToBottomEvent) {
            scope.launch {
                if (uiState.messages.isNotEmpty()) {
                    listState.animateScrollToItem(uiState.messages.size - 1)
                }
                viewModel.clearScrollToBottomEvent()
            }
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        topBar = {
            TopAppBar(
                windowInsets = WindowInsets(0.dp),
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = if (uiState.title == "Apple官方旗舰店") {
                            Modifier.clickable { onNavigateToShop(uiState.title) }
                        } else {
                            Modifier
                        }
                    ) {
                        Text(
                            text = uiState.avatar,
                            fontSize = 20.sp,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = uiState.title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.onBackClick() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    if (uiState.title == "Apple官方旗舰店") {
                        IconButton(onClick = {
                            onNavigateToShop(uiState.title)
                        }) {
                            Icon(Icons.Filled.Store, contentDescription = "店铺")
                        }
                    }
                    IconButton(onClick = {
                        onNavigateToSettings(uiState.title, uiState.avatar)
                    }) {
                        Icon(Icons.Filled.Settings, contentDescription = "设置")
                    }
                }
            )
        },
        bottomBar = {
            MessageInputBar(
                inputText = uiState.inputText,
                onInputChange = viewModel::onInputTextChange,
                onSendClick = {
                    keyboardController?.hide()
                    viewModel.sendMessage(uiState.inputText)
                },
                onQuickActionClick = viewModel::onQuickActionClick
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5))
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.messages) { message ->
                        MessageItem(
                            message = message,
                            chatAvatar = uiState.avatar,
                            onProductClick = viewModel::onProductCardClick
                        )
                    }
                }
            }
        }
    }
}
