package com.example.jd_sim.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jd_sim.domain.model.AssetItem
import com.example.jd_sim.domain.model.ServiceItem

@Composable
fun AssetServiceSection(
    assetItems: List<AssetItem>,
    serviceItems: List<ServiceItem>,
    onAssetClick: (AssetItem) -> Unit,
    onServiceClick: (ServiceItem) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(248.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        BigInfoPanel(
            title = "钱包",
            subtitle = "查看账单",
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            assetItems.forEachIndexed { index, asset ->
                WalletItem(asset = asset, onClick = { onAssetClick(asset) })
                if (index != assetItems.lastIndex) {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        BigInfoPanel(
            title = "服务家",
            subtitle = "洗车美容1.9起",
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            serviceItems.forEachIndexed { index, service ->
                ServiceEntryItem(service = service, onClick = { onServiceClick(service) })
                if (index != serviceItems.lastIndex) {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun BigInfoPanel(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp, vertical = 18.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF191919)
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Filled.ChevronRight,
                    contentDescription = null,
                    tint = Color(0xFF6F6F6F),
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = Color(0xFF8D8D8D),
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(14.dp))
            content()
        }
    }
}

@Composable
private fun WalletItem(
    asset: AssetItem,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "¥ ${asset.value}",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF191919)
                )
                asset.description?.takeIf { it.isNotEmpty() }?.let { desc ->
                    Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                    Text(
                        text = desc,
                        fontSize = 10.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .background(Color(0xFFFF4A52), RoundedCornerShape(999.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = asset.name,
                fontSize = 13.sp,
                color = Color(0xFF9A9A9A)
            )
        }
        Icon(
            imageVector = Icons.Filled.ChevronRight,
            contentDescription = null,
            tint = Color(0xFFB7B7B7)
        )
    }
}

@Composable
private fun ServiceEntryItem(
    service: ServiceItem,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = service.iconEmoji,
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.padding(horizontal = 6.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = service.name,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF191919),
                    maxLines = 1
                )
                if (service.description.isNotEmpty()) {
                    Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                    Text(
                        text = service.description,
                        fontSize = 11.sp,
                        color = Color(0xFFFF3F4F),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
