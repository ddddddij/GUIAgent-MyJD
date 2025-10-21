package com.example.MyJD.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.MyJD.model.*
import com.example.MyJD.presenter.MessageDetailContract
import com.example.MyJD.presenter.MessageDetailPresenter
import com.example.MyJD.repository.DataRepository
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageDetailScreen(
    conversationId: String,
    onBackClick: () -> Unit,
    onNavigateToProduct: (String) -> Unit,
    onNavigateToSettings: (String, String) -> Unit = { _, _ -> }
) {
    val context = LocalContext.current
    val repository = remember { DataRepository.getInstance(context) }
    val presenter = remember { MessageDetailPresenter(repository) }
    
    var conversation by remember { mutableStateOf<Conversation?>(null) }
    var messages by remember { mutableStateOf<List<ChatMessage>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var inputText by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("消息详情") }
    var avatar by remember { mutableStateOf("") }
    
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    
    // MVP View implementation
    val view = remember {
        object : MessageDetailContract.View {
            override fun showConversation(newConversation: Conversation) {
                conversation = newConversation
            }
            
            override fun showMessages(newMessages: List<ChatMessage>) {
                messages = newMessages
            }
            
            override fun addMessage(message: ChatMessage) {
                messages = messages + message
            }
            
            override fun showToast(message: String) {
                android.widget.Toast.makeText(context, message, android.widget.Toast.LENGTH_SHORT).show()
            }
            
            override fun clearInputText() {
                inputText = ""
            }
            
            override fun scrollToBottom() {
                scope.launch {
                    if (messages.isNotEmpty()) {
                        listState.animateScrollToItem(messages.size - 1)
                    }
                }
            }
            
            override fun showLoading(show: Boolean) {
                isLoading = show
            }
            
            override fun navigateToProduct(productId: String) {
                onNavigateToProduct(productId)
            }
            
            override fun setTitle(newTitle: String) {
                title = newTitle
            }
            
            override fun setAvatar(newAvatar: String) {
                avatar = newAvatar
            }
        }
    }
    
    // Attach presenter
    LaunchedEffect(Unit) {
        presenter.attach(view)
        presenter.loadConversation(conversationId)
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = avatar,
                            fontSize = 20.sp,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        presenter.onBackClick()
                        onBackClick()
                    }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        onNavigateToSettings(title, avatar)
                    }) {
                        Icon(Icons.Filled.Settings, contentDescription = "设置")
                    }
                }
            )
        },
        bottomBar = {
            MessageInputBar(
                inputText = inputText,
                onInputChange = { inputText = it },
                onSendClick = {
                    keyboardController?.hide()
                    presenter.sendMessage(inputText)
                },
                onQuickActionClick = { action ->
                    presenter.onQuickActionClick(action)
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5))
        ) {
            if (isLoading) {
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
                    items(messages) { message ->
                        MessageItem(
                            message = message,
                            chatAvatar = avatar,
                            onProductClick = { productId ->
                                presenter.onProductCardClick(productId)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MessageItem(
    message: ChatMessage,
    chatAvatar: String,
    onProductClick: (String) -> Unit
) {
    when (message.type) {
        ChatMessageType.SYSTEM -> {
            SystemMessageItem(message)
        }
        ChatMessageType.PRODUCT -> {
            ProductMessageItem(message, onProductClick)
        }
        else -> {
            if (message.sender == ChatSender.USER) {
                UserMessageItem(message)
            } else {
                OtherMessageItem(message, chatAvatar)
            }
        }
    }
}

@Composable
fun UserMessageItem(message: ChatMessage) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Spacer(modifier = Modifier.width(64.dp))
        
        Column(
            horizontalAlignment = Alignment.End
        ) {
            Card(
                modifier = Modifier.padding(start = 64.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE74C3C))
            ) {
                Text(
                    text = message.content,
                    modifier = Modifier.padding(12.dp),
                    color = Color.White,
                    fontSize = 14.sp
                )
            }
            
            Text(
                text = formatTime(message.timestamp),
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp, end = 4.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(Color(0xFF3498DB), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "我",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun OtherMessageItem(message: ChatMessage, avatar: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(Color(0xFFECF0F1), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = avatar,
                fontSize = 16.sp
            )
        }
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Column(
            horizontalAlignment = Alignment.Start
        ) {
            Card(
                modifier = Modifier.padding(end = 64.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Text(
                    text = message.content,
                    modifier = Modifier.padding(12.dp),
                    color = Color.Black,
                    fontSize = 14.sp
                )
            }
            
            Text(
                text = formatTime(message.timestamp),
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp, start = 4.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(64.dp))
    }
}

@Composable
fun SystemMessageItem(message: ChatMessage) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA))
        ) {
            Text(
                text = message.content,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                fontSize = 12.sp,
                color = Color(0xFF6C757D),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ProductMessageItem(
    message: ChatMessage,
    onProductClick: (String) -> Unit
) {
    val productCard = remember {
        try {
            val gson = Gson()
            gson.fromJson(message.content, ProductCard::class.java)
        } catch (e: Exception) {
            android.util.Log.e("MessageDetailScreen", "Error parsing product card", e)
            null
        }
    }
    
    if (productCard != null) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Spacer(modifier = Modifier.width(44.dp))
            
            Card(
                modifier = Modifier
                    .padding(end = 64.dp)
                    .clickable { onProductClick(productCard.productId) },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = productCard.productImage,
                            fontSize = 40.sp,
                            modifier = Modifier.padding(end = 12.dp)
                        )
                        
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = productCard.productName,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black
                            )
                            
                            Text(
                                text = "¥${productCard.productPrice}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFE74C3C),
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                    
                    Divider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = Color(0xFFEEEEEE)
                    )
                    
                    Text(
                        text = "查看详情",
                        fontSize = 12.sp,
                        color = Color(0xFF3498DB),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    } else {
        // Fallback to text display
        OtherMessageItem(message, "🛍️")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageInputBar(
    inputText: String,
    onInputChange: (String) -> Unit,
    onSendClick: () -> Unit,
    onQuickActionClick: (String) -> Unit
) {
    val quickActions = listOf("发订单", "申请价保", "什么时间配送", "我要退换货")
    
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            // Quick actions
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
            ) {
                items(quickActions) { action ->
                    OutlinedButton(
                        onClick = { onQuickActionClick(action) },
                        modifier = Modifier.height(32.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE74C3C)),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFFE74C3C)
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp)
                    ) {
                        Text(
                            text = action,
                            fontSize = 12.sp
                        )
                    }
                }
            }
            
            // Input area
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                // Voice button
                IconButton(onClick = { onQuickActionClick("语音消息") }) {
                    Icon(
                        Icons.Filled.Mic,
                        contentDescription = "语音",
                        tint = Color(0xFF6C757D)
                    )
                }
                
                // Text input
                OutlinedTextField(
                    value = inputText,
                    onValueChange = onInputChange,
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("请输入您要咨询的内容…", fontSize = 14.sp) },
                    maxLines = 4,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(
                        onSend = { 
                            if (inputText.trim().isNotEmpty()) {
                                onSendClick()
                            }
                        }
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
                
                // Emoji button
                IconButton(onClick = { onQuickActionClick("表情") }) {
                    Icon(
                        Icons.Filled.EmojiEmotions,
                        contentDescription = "表情",
                        tint = Color(0xFF6C757D)
                    )
                }
                
                // Send button
                Button(
                    onClick = onSendClick,
                    enabled = inputText.trim().isNotEmpty(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE74C3C),
                        disabledContainerColor = Color(0xFFCCCCCC)
                    ),
                    shape = CircleShape,
                    modifier = Modifier.size(48.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(
                        Icons.Filled.Send,
                        contentDescription = "发送",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

private fun formatTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}