package com.example.MyJD.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.MyJD.model.SeriesOption
import com.google.accompanist.flowlayout.FlowRow

@Composable
fun SeriesSelector(
    seriesOptions: List<SeriesOption>,
    selectedSeries: String,
    onSeriesSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 标题
        Text(
            text = "更多系列",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF333333)
        )
        
        // 系列选项
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            mainAxisSpacing = 8.dp,
            crossAxisSpacing = 8.dp
        ) {
            seriesOptions.forEach { series ->
                SeriesOptionChip(
                    series = series,
                    isSelected = series.name == selectedSeries,
                    onClick = { 
                        if (series.available) {
                            onSeriesSelected(series.name)
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun SeriesOptionChip(
    series: SeriesOption,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected) Color(0xFFFFF0F0) else Color(0xFFF5F5F5)
    val textColor = if (isSelected) Color(0xFFE2231A) else Color(0xFF333333)
    val borderColor = if (isSelected) Color(0xFFE2231A) else Color(0xFFE0E0E0)
    val borderWidth = if (isSelected) 2.dp else 1.dp
    
    Card(
        modifier = modifier
            .clickable(enabled = series.available) { onClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (series.available) backgroundColor else Color(0xFFF8F8F8)
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = borderWidth,
            color = if (series.available) borderColor else Color(0xFFE8E8E8)
        )
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = series.name,
                fontSize = 14.sp,
                color = if (series.available) textColor else Color(0xFF999999)
            )
        }
    }
}