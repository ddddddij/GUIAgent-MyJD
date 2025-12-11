package com.example.jd_sim.ui.screen.addressdetail.components

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 收货人信息卡片
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressReceiverInfoCard(
    name: String,
    phone: String,
    onNameChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    nameError: String?,
    phoneError: String?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "收货人信息",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF333333)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 收货人姓名
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "收货人",
                    fontSize = 14.sp,
                    color = Color(0xFF333333),
                    modifier = Modifier.width(80.dp)
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = onNameChange,
                    modifier = Modifier.weight(1f),
                    placeholder = {
                        Text(
                            text = "请输入收货人姓名",
                            color = Color(0xFF999999)
                        )
                    },
                    isError = nameError != null,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFE2231A),
                        errorBorderColor = Color(0xFFE53935)
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.width(8.dp))

                // 通讯录图标
                IconButton(onClick = {
                    Toast.makeText(context, "通讯录功能待开发", Toast.LENGTH_SHORT).show()
                }) {
                    Icon(
                        imageVector = Icons.Default.Contacts,
                        contentDescription = "通讯录",
                        tint = Color(0xFF999999)
                    )
                }
            }

            nameError?.let { error ->
                Text(
                    text = error,
                    color = Color(0xFFE53935),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 80.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 手机号
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "手机号",
                    fontSize = 14.sp,
                    color = Color(0xFF333333),
                    modifier = Modifier.width(80.dp)
                )

                // 区号选择
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFF5F5F5),
                    modifier = Modifier.clickable {
                        Toast.makeText(context, "区号选择功能待开发", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "+86",
                            fontSize = 14.sp,
                            color = Color(0xFF333333)
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "选择区号",
                            tint = Color(0xFF999999),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = onPhoneChange,
                    modifier = Modifier.weight(1f),
                    placeholder = {
                        Text(
                            text = "请输入手机号",
                            color = Color(0xFF999999)
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    isError = phoneError != null,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFE2231A),
                        errorBorderColor = Color(0xFFE53935)
                    ),
                    singleLine = true
                )
            }

            phoneError?.let { error ->
                Text(
                    text = error,
                    color = Color(0xFFE53935),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 80.dp, top = 4.dp)
                )
            }
        }
    }
}
