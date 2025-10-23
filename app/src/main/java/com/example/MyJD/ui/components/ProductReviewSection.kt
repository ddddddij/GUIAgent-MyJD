package com.example.MyJD.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.MyJD.model.ReviewInfo
import com.example.MyJD.model.ReviewItem
import com.example.MyJD.model.ReviewTag
import android.graphics.BitmapFactory

@Composable
fun ProductReviewSection(
    reviews: ReviewInfo,
    onTagClick: (Int) -> Unit,
    onMoreReviewsClick: () -> Unit,
    onImageClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 评价统计
            ReviewStatsHeader(
                totalCount = reviews.totalCount,
                positiveRate = reviews.positiveRate,
                onMoreClick = onMoreReviewsClick
            )
            
            // 评价标签云
            ReviewTagsSection(
                tags = reviews.tags,
                onTagClick = onTagClick
            )
            
            // 评价列表
            ReviewList(
                reviews = reviews.list,
                onImageClick = onImageClick
            )
        }
    }
}

@Composable
private fun ReviewStatsHeader(
    totalCount: String,
    positiveRate: String,
    onMoreClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onMoreClick() },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "买家评价 $totalCount",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF333333)
            )
            
            Text(
                text = positiveRate,
                fontSize = 14.sp,
                color = Color(0xFFE2231A)
            )
        }
        
        Icon(
            imageVector = Icons.Filled.ArrowForward,
            contentDescription = null,
            tint = Color(0xFF999999),
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun ReviewTagsSection(
    tags: List<ReviewTag>,
    onTagClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(tags) { index, tag ->
            ReviewTagChip(
                tag = tag,
                onClick = { onTagClick(index) }
            )
        }
    }
}

@Composable
private fun ReviewTagChip(
    tag: ReviewTag,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (tag.isSelected) Color(0xFFE2231A) else Color(0xFFFFF3E0)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = "${tag.text} ${tag.count}",
            fontSize = 12.sp,
            color = if (tag.isSelected) Color.White else Color(0xFFFF6F00),
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun ReviewList(
    reviews: List<ReviewItem>,
    onImageClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        reviews.take(3).forEachIndexed { reviewIndex, review ->
            ReviewItemCard(
                review = review,
                reviewIndex = reviewIndex,
                onImageClick = onImageClick
            )
        }
    }
}

@Composable
private fun ReviewItemCard(
    review: ReviewItem,
    reviewIndex: Int,
    onImageClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isLiked by remember { mutableStateOf(review.isLiked) }
    
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF8F8F8)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 用户信息
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 用户头像
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = Color(0xFFE0E0E0),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "👤",
                        fontSize = 20.sp
                    )
                }
                
                Column {
                    Text(
                        text = review.username,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF333333)
                    )
                    
                    Text(
                        text = "💎${review.memberLevel}",
                        fontSize = 12.sp,
                        color = Color(0xFF9C27B0)
                    )
                }
            }
            
            // 评价内容
            Text(
                text = review.content,
                fontSize = 14.sp,
                color = Color(0xFF333333),
                lineHeight = 20.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            
            // 评价图片
            if (review.images.isNotEmpty()) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(review.images.take(3)) { index, image ->
                        ReviewImageItem(
                            image = image,
                            imageIndex = reviewIndex * 3 + index, // 确保跨评论的图片都不重复
                            imageCount = if (review.imageCount > 3) review.imageCount - 2 else 0,
                            isLast = index == 2,
                            onClick = onImageClick
                        )
                    }
                }
            }
            
            // 点赞按钮
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = { isLiked = !isLiked },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = if (isLiked) Color(0xFFE2231A) else Color(0xFF666666)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Filled.ThumbUp,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(4.dp))
                    
                    Text(
                        text = if (isLiked) "已点赞" else "有用",
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun ReviewImageItem(
    image: String,
    imageIndex: Int,
    imageCount: Int,
    isLast: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    // 根据实际的imageIndex使用不同的评论区图片
    val reviewImages = listOf("评论区1.jpg", "评论区2.jpg", "评论区3.jpg", "评论区4.jpg")
    val imageName = reviewImages[imageIndex % reviewImages.size]
    
    // 使用remember和derivedStateOf来处理图片加载
    val bitmap by remember(imageName) {
        derivedStateOf {
            try {
                val inputStream = context.assets.open("image/$imageName")
                val loadedBitmap = BitmapFactory.decodeStream(inputStream)
                inputStream.close()
                loadedBitmap
            } catch (e: Exception) {
                null
            }
        }
    }
    
    Box(
        modifier = modifier
            .size(80.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(Color(0xFFE0E0E0))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        bitmap?.let {
            Image(
                painter = BitmapPainter(it.asImageBitmap()),
                contentDescription = "评论图片",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(4.dp)),
                contentScale = ContentScale.Crop
            )
        } ?: Text(
            text = image,
            fontSize = 32.sp
        )
        
        // 显示更多图片数量
        if (isLast && imageCount > 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .background(
                        Color.Black.copy(alpha = 0.6f),
                        RoundedCornerShape(topStart = 8.dp)
                    )
                    .padding(4.dp)
            ) {
                Text(
                    text = "+$imageCount",
                    fontSize = 10.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}