package com.example.jd_sim.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.jd_sim.domain.model.Banner
import com.example.jd_sim.domain.model.Product

@Composable
fun BannerSection(
    banners: List<Banner>,
    products: List<Product>,
    onBannerClick: (Banner) -> Unit,
    onProductClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val featuredProducts = products.take(6)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFFF4F5F7))
            .padding(horizontal = 12.dp)
    ) {
        Spacer(modifier = Modifier.height(6.dp))

        PromoRow(
            left = {
                SubsidyCard(
                    products = featuredProducts.take(2),
                    onProductClick = onProductClick
                )
            },
            right = { ServiceCard() }
        )

        Spacer(modifier = Modifier.height(8.dp))

        PromoRow(
            left = {
                SmallDealCard(
                    title = "7天价保",
                    accent = "1元特价",
                    products = featuredProducts.drop(2).take(2),
                    onProductClick = onProductClick
                )
            },
            right = {
                LivePriceCard(
                    products = featuredProducts.drop(4).take(2),
                    onProductClick = onProductClick
                )
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        PromoRow(
            left = { StudentZoneCard() },
            right = {
                RushSaleCard(
                    banner = banners.firstOrNull(),
                    onClick = { banners.firstOrNull()?.let(onBannerClick) }
                )
            }
        )

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun PromoRow(
    left: @Composable () -> Unit,
    right: @Composable () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(modifier = Modifier.weight(1f)) { left() }
        Box(modifier = Modifier.weight(1f)) { right() }
    }
}

@Composable
private fun SubsidyCard(
    products: List<Product>,
    onProductClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(188.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 11.dp)
                .fillMaxWidth()
        ) {
            SectionTitle(
                title = "国家补贴 × 百亿补贴",
                subtitle = null
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                products.forEach { product ->
                    PromoProductTile(
                        product = product,
                        modifier = Modifier.weight(1f),
                        priceSuffix = "补贴价",
                        onClick = { onProductClick(product.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ServiceCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(188.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 11.dp)
                .fillMaxWidth()
        ) {
            SectionTitle(title = "生活服务", subtitle = null)
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.LocationOn,
                    contentDescription = "城市",
                    tint = Color(0xFFC68A2D),
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "武汉",
                    color = Color(0xFFB77B20),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            val services = listOf(
                ServiceEntry("超市便利", Color(0xFFE95C60)),
                ServiceEntry("买药秒送", Color(0xFF8ED36C)),
                ServiceEntry("品质外卖", Color(0xFF8C5A22), "补贴"),
                ServiceEntry("酒店", Color(0xFF4B85F6)),
                ServiceEntry("特价团购", Color(0xFFF5972F), "团购")
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                userScrollEnabled = false
            ) {
                items(services) { service ->
                    ServiceItem(service = service)
                }
            }
        }
    }
}

@Composable
private fun SmallDealCard(
    title: String,
    accent: String,
    products: List<Product>,
    onProductClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(188.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 11.dp)
                .fillMaxWidth()
        ) {
            SectionTitle(title = title, subtitle = accent, subtitleColor = Color(0xFFF05B57))
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                products.forEach { product ->
                    DealProductTile(
                        product = product,
                        modifier = Modifier.weight(1f),
                        onClick = { onProductClick(product.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun LivePriceCard(
    products: List<Product>,
    onProductClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(188.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 11.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "直播低价",
                color = Color(0xFFE54A47),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFFFF4F4))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFFEFE6E8), Color(0xFFCDB8BC))
                        )
                    )
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data("file:///android_asset/image/用户头像.JPG")
                        .crossfade(true)
                        .build(),
                    contentDescription = "直播封面",
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.Crop
                )

                products.firstOrNull()?.let { product ->
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(10.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0x99141414))
                            .clickable { onProductClick(product.id) }
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            ProductThumb(product = product, modifier = Modifier.size(32.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "¥${formatPrice(product.price)}",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StudentZoneCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(188.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEAF7FF)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 11.dp)
                .fillMaxWidth()
        ) {
            SectionTitle(title = "学生专区", subtitle = "1元包邮", subtitleColor = Color(0xFF58AFDE))
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StudentBenefitTile(
                    title = "教育优惠",
                    value = "20%",
                    subtitle = "国补直降",
                    modifier = Modifier.weight(1f)
                )
                StudentBenefitTile(
                    title = "餐补券",
                    value = "20元",
                    subtitle = "学生外卖",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun RushSaleCard(
    banner: Banner?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(188.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF25A5B)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 11.dp)
                .fillMaxWidth()
        ) {
            SectionTitle(title = "11.11抢先购", subtitle = "主会场", titleColor = Color.White, subtitleColor = Color(0xFFF44C4E), subtitleFill = Color.White)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = banner?.subtitle ?: "直降1折起",
                color = Color(0xFFFFF2F2),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PromoInfoBox(
                    title = "¥50",
                    subtitle = "品类券",
                    modifier = Modifier.weight(1f)
                )
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PromoInfoBox(title = "好车", subtitle = "999起", compact = true)
                    PromoInfoBox(title = "教育", subtitle = "低至5折", compact = true)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Campaign,
                    contentDescription = "播报",
                    tint = Color(0xFFFFF2A6),
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "全民答题 瓜分红包",
                    color = Color(0xFFFFF2C0),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun SectionTitle(
    title: String,
    subtitle: String?,
    titleColor: Color = Color(0xFF181818),
    subtitleColor: Color = Color(0xFF7A7A7A),
    subtitleFill: Color = Color.Transparent
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = titleColor,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        subtitle?.let {
            val subtitleModifier = if (subtitleFill == Color.Transparent) {
                Modifier
            } else {
                Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(subtitleFill)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            }
            Text(
                text = it,
                color = subtitleColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                modifier = subtitleModifier
            )
        }
    }
}

@Composable
private fun PromoProductTile(
    product: Product,
    priceSuffix: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ProductThumb(
            product = product,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false)
                .aspectRatio(1f)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "¥${formatPrice(product.price)}",
            color = Color(0xFFE34B49),
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = priceSuffix,
            color = Color(0xFFEF7B73),
            fontSize = 10.sp
        )
    }
}

@Composable
private fun DealProductTile(
    product: Product,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ProductThumb(
            product = product,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.9f)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "¥${formatPrice(product.price)}",
            color = Color(0xFFE34B49),
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun ServiceItem(service: ServiceEntry) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFFF8F9FB))
            .padding(vertical = 8.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(service.color),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = service.name.take(1),
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = service.name,
            color = Color(0xFF4C4C4C),
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1
        )
        service.badge?.let {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = it,
                color = Color.White,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(Color(0xFFF44336))
                    .padding(horizontal = 5.dp, vertical = 1.dp)
            )
        }
    }
}

@Composable
private fun StudentBenefitTile(
    title: String,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.82f))
            .padding(horizontal = 10.dp, vertical = 12.dp)
    ) {
        Text(
            text = title,
            color = Color(0xFF5A5A5A),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = value,
            color = Color(0xFF46B24B),
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = subtitle,
            color = Color(0xFF74B633),
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun PromoInfoBox(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    compact: Boolean = false
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White)
            .padding(horizontal = 10.dp, vertical = if (compact) 8.dp else 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            color = Color(0xFFE34B49),
            fontSize = if (compact) 13.sp else 24.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = subtitle,
            color = Color(0xFF4A4A4A),
            fontSize = if (compact) 11.sp else 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun ProductThumb(
    product: Product,
    modifier: Modifier = Modifier
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data("file:///android_asset/${product.imageUrl}")
            .crossfade(true)
            .build(),
        contentDescription = product.name,
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFFF6F6F6)),
        contentScale = ContentScale.Fit
    )
}

private fun formatPrice(price: Double): String {
    val intPart = price.toInt()
    val decimal = ((price - intPart) * 100).toInt()
    return if (decimal == 0) {
        intPart.toString()
    } else {
        "$intPart.${decimal.toString().padStart(2, '0')}"
    }
}

private data class ServiceEntry(
    val name: String,
    val color: Color,
    val badge: String? = null
)
