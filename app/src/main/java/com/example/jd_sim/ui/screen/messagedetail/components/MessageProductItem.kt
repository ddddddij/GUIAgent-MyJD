package com.example.jd_sim.ui.screen.messagedetail.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jd_sim.domain.model.ChatMessage
import com.example.jd_sim.domain.model.ProductCard
import com.google.gson.Gson

/**
 * 商品卡片消息项
 */
@Composable
fun MessageProductItem(
    message: ChatMessage,
    onProductClick: (String) -> Unit
) {
    val productCard = remember {
        try {
            val gson = Gson()
            gson.fromJson(message.content, ProductCard::class.java)
        } catch (e: Exception) {
            android.util.Log.e("MessageProductItem", "Error parsing product card", e)
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
        MessageOtherItem(message, "🛍️")
    }
}
