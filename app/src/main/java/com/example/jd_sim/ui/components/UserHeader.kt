package com.example.jd_sim.ui.components

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jd_sim.domain.model.QuickAction
import com.example.jd_sim.ui.theme.JDRed

@Composable
fun UserHeader(
    userName: String,
    memberLevel: String,
    avatar: String,
    hasStudentBenefit: Boolean,
    plusStatus: String,
    redPacketStatus: String,
    quickActions: List<QuickAction>,
    onQuickActionClick: (QuickAction) -> Unit,
    onAvatarClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFFF404A), Color(0xFFFF4A52), Color(0xFFF5F6F8))
                )
            )
            .padding(start = 16.dp, end = 16.dp, top = 18.dp, bottom = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0x33FFFFFF))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "快讯",
                            color = JDRed,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "购物车“顶流们”来了！",
                        fontSize = 12.sp,
                        color = Color.White,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Icon(
                        imageVector = Icons.Filled.ChevronRight,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                quickActions.forEach { action ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { onQuickActionClick(action) }
                    ) {
                        Text(
                            text = action.iconEmoji,
                            color = Color.White,
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = action.name,
                            fontSize = 10.sp,
                            color = Color.White,
                            maxLines = 1
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 15.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AvatarImage(
                        avatar = avatar,
                        onAvatarClick = onAvatarClick
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = userName,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF181818),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(7.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "JD",
                                fontSize = 10.sp,
                                color = Color(0xFFE3A25C),
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(999.dp))
                                    .background(Color(0xFFF9E7D4))
                                    .padding(horizontal = 7.dp, vertical = 3.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = memberLevel,
                                fontSize = 12.sp,
                                color = Color(0xFFC98C4B),
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1
                            )
                            if (hasStudentBenefit) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "学生优惠",
                                    fontSize = 12.sp,
                                    color = Color(0xFF20A8E8),
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF6B594F))
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = "PLUS会员",
                                fontSize = 13.sp,
                                color = Color(0xFFF6DFC8),
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (redPacketStatus.isNotBlank()) redPacketStatus else "您有红包未领取",
                                fontSize = 11.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = Color(0xFFF5E2D7)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                BenefitRow(plusStatus = plusStatus)
            }
        }
    }
}

@Composable
private fun AvatarImage(
    avatar: String,
    onAvatarClick: () -> Unit
) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .size(72.dp)
            .clip(CircleShape)
            .background(Color.White)
            .clickable { onAvatarClick() },
        contentAlignment = Alignment.Center
    ) {
        if (avatar.endsWith(".JPG") || avatar.endsWith(".jpg") || avatar.endsWith(".PNG") || avatar.endsWith(".png")) {
            val bitmap by remember(avatar) {
                derivedStateOf {
                    try {
                        val inputStream = context.assets.open("image/$avatar")
                        val loadedBitmap = BitmapFactory.decodeStream(inputStream)
                        inputStream.close()
                        loadedBitmap
                    } catch (_: Exception) {
                        null
                    }
                }
            }

            bitmap?.let {
                Image(
                    painter = BitmapPainter(it.asImageBitmap()),
                    contentDescription = "用户头像",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(72.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } ?: Text(text = "👤", fontSize = 30.sp)
        } else {
            Text(text = avatar, fontSize = 30.sp)
        }
    }
}

@Composable
private fun BenefitRow(
    plusStatus: String
) {
    val items = listOf(
        Pair("39张", "优惠券"),
        Pair("领66豆", "京豆"),
        Pair("抽¥888", "红包"),
        Pair("¥3000", "白条取现"),
        Pair("抽¥20", "秒送外卖"),
        Pair(if (plusStatus.isNotBlank()) "更多" else "更多", "")
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        items.forEach { (top, bottom) ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier.height(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = top,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF181818),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.height(7.dp))
                Text(
                    text = if (bottom.isBlank()) " " else bottom,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF616161),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
