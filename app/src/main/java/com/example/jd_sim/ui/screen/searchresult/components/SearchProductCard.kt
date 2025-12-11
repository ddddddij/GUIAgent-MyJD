package com.example.jd_sim.ui.screen.searchresult.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.jd_sim.domain.model.Product
import com.example.jd_sim.ui.theme.JDRed

/**
 * 搜索结果商品卡片
 */
@Composable
fun SearchProductCard(
    product: Product,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            // 商品图片
            AsyncImage(
                model = "file:///android_asset/${product.imageUrl}",
                contentDescription = product.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(4.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 商品名称
            Text(
                text = product.name,
                fontSize = 14.sp,
                color = Color.Black,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            // 商品价格
            Text(
                text = "¥${product.price}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = JDRed
            )

            Spacer(modifier = Modifier.height(4.dp))

            // 店铺名称
            Text(
                text = product.storeName,
                fontSize = 12.sp,
                color = Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
