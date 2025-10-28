package com.example.MyJD.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.MyJD.model.ConversationSummary

@Composable
fun ConversationSummaryList(
    conversationSummaries: List<ConversationSummary>,
    onConversationClick: (ConversationSummary) -> Unit,
    modifier: Modifier = Modifier
) {
    if (conversationSummaries.isEmpty()) {
        ConversationEmptyState(
            modifier = modifier.fillMaxSize()
        )
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(conversationSummaries) { conversationSummary ->
                ConversationSummaryItem(
                    conversationSummary = conversationSummary,
                    onClick = onConversationClick
                )
            }
        }
    }
}

@Composable
private fun ConversationEmptyState(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "💬",
            style = MaterialTheme.typography.displayMedium
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "暂无聊天记录～",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray
        )
    }
}