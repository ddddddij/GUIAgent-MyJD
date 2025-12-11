package com.example.jd_sim.ui.screen.cart.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import com.example.jd_sim.domain.model.CartItemSpec
import com.example.jd_sim.ui.theme.JDRed

/**
 * 购物车删除确认对话框
 */
@Composable
fun CartDeleteDialog(
    cartItem: CartItemSpec,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("确认删除") },
        text = { Text("您确定要删除商品 '${cartItem.productName}' 吗？") },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = JDRed)
            ) {
                Text("确认")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}
