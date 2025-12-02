package com.example.MyJD.ui.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.MyJD.repository.DataRepository
import com.example.MyJD.viewmodel.AddressDetailViewModel
import com.example.MyJD.viewmodel.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressDetailScreen(
    addressId: String?,
    onBackClick: () -> Unit,
    onSaveSuccess: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val repository = remember { DataRepository.getInstance(context) }
    val viewModel: AddressDetailViewModel = viewModel(
        factory = ViewModelFactory(repository, context)
    )
    
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val formData by viewModel.formData.collectAsStateWithLifecycle()
    val validationErrors by viewModel.validationErrors.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
    val navigationEvent by viewModel.navigationEvent.collectAsStateWithLifecycle()
    val toastMessage by viewModel.toastMessage.collectAsStateWithLifecycle()
    
    val scrollState = rememberScrollState()
    
    // Load address on first composition
    LaunchedEffect(addressId) {
        viewModel.loadAddress(addressId)
    }
    
    // Handle navigation events
    LaunchedEffect(navigationEvent) {
        navigationEvent?.let { event ->
            when (event) {
                is AddressDetailViewModel.NavigationEvent.Back -> {
                    onBackClick()
                }
                is AddressDetailViewModel.NavigationEvent.SaveSuccess -> {
                    onSaveSuccess()
                }
            }
            viewModel.clearNavigationEvent()
        }
    }
    
    // Handle toast messages
    LaunchedEffect(toastMessage) {
        toastMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.clearToastMessage()
        }
    }
    
    // Handle error messages
    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.clearErrorMessage()
        }
    }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8F8F8))
    ) {
        // 顶部导航栏
        TopAppBar(
            title = {
                Text(
                    text = if (addressId == null) "新增收货地址" else "编辑收货地址",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "返回",
                        tint = Color.White
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xFFE2231A)
            )
        )
        
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            if (isLoading) {
                // 加载状态
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Color(0xFFE2231A)
                    )
                }
            } else {
                // 表单内容
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                ) {
                    // 收货人信息区域
                    Card(
                        modifier = Modifier
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
                                    value = formData.name,
                                    onValueChange = { 
                                        viewModel.updateFormData(name = it)
                                        viewModel.clearValidationError("name")
                                    },
                                    modifier = Modifier.weight(1f),
                                    placeholder = { 
                                        Text(
                                            text = "请输入收货人姓名",
                                            color = Color(0xFF999999)
                                        )
                                    },
                                    isError = validationErrors.containsKey("name"),
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
                            
                            validationErrors["name"]?.let { error ->
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
                                    value = formData.phone,
                                    onValueChange = { 
                                        viewModel.updateFormData(phone = it)
                                        viewModel.clearValidationError("phone")
                                    },
                                    modifier = Modifier.weight(1f),
                                    placeholder = { 
                                        Text(
                                            text = "请输入手机号",
                                            color = Color(0xFF999999)
                                        )
                                    },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                    isError = validationErrors.containsKey("phone"),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFFE2231A),
                                        errorBorderColor = Color(0xFFE53935)
                                    ),
                                    singleLine = true
                                )
                            }
                            
                            validationErrors["phone"]?.let { error ->
                                Text(
                                    text = error,
                                    color = Color(0xFFE53935),
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(start = 80.dp, top = 4.dp)
                                )
                            }
                        }
                    }
                    
                    // 地址选择区域
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            // 地址
                            Text(
                                text = "地址",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF333333)
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            
                            // 详细地址
                            OutlinedTextField(
                                value = formData.detailAddress,
                                onValueChange = { 
                                    viewModel.updateFormData(detailAddress = it)
                                    viewModel.clearValidationError("detailAddress")
                                },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { 
                                    Text(
                                        text = "详细地址，如道路、门牌号、小区、楼栋号、单元室等",
                                        color = Color(0xFF999999)
                                    )
                                },
                                isError = validationErrors.containsKey("detailAddress"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFFE2231A),
                                    errorBorderColor = Color(0xFFE53935)
                                ),
                                minLines = 2,
                                maxLines = 3
                            )
                            
                            validationErrors["detailAddress"]?.let { error ->
                                Text(
                                    text = error,
                                    color = Color(0xFFE53935),
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                    
                    // 设为默认地址
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "设为默认地址",
                                    fontSize = 16.sp,
                                    color = Color(0xFF333333)
                                )
                                Text(
                                    text = "提醒：下单时会优先使用该地址",
                                    fontSize = 12.sp,
                                    color = Color(0xFF999999)
                                )
                            }
                            
                            Switch(
                                checked = formData.isDefault,
                                onCheckedChange = { 
                                    viewModel.updateFormData(isDefault = it)
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = Color(0xFFE2231A),
                                    uncheckedThumbColor = Color.White,
                                    uncheckedTrackColor = Color(0xFFDDDDDD)
                                )
                            )
                        }
                    }
                    
                    // 标签选择
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "标签",
                                fontSize = 16.sp,
                                color = Color(0xFF333333)
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // 标签选项
                            val tags = listOf("学校", "家", "公司", "购物", "秒送/外卖", "自定义")
                            
                            Column(
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                for (i in tags.indices step 3) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        for (j in 0 until 3) {
                                            val index = i + j
                                            if (index < tags.size) {
                                                val tag = tags[index]
                                                val isSelected = formData.tag == tag
                                                
                                                Surface(
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .clickable {
                                                            viewModel.updateFormData(tag = tag)
                                                        },
                                                    shape = RoundedCornerShape(8.dp),
                                                    color = if (isSelected) Color(0xFFFFF2F0) else Color.Transparent,
                                                    border = if (isSelected) 
                                                        androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE53935))
                                                    else 
                                                        androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDDDDDD))
                                                ) {
                                                    Text(
                                                        text = tag,
                                                        modifier = Modifier.padding(12.dp),
                                                        fontSize = 14.sp,
                                                        color = if (isSelected) Color(0xFFE53935) else Color(0xFF333333)
                                                    )
                                                }
                                            } else {
                                                Spacer(modifier = Modifier.weight(1f))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    // 底部间距，避免被确认按钮遮挡
                    Spacer(modifier = Modifier.height(80.dp))
                }
                
                // 确认按钮
                Button(
                    onClick = { viewModel.saveAddress(addressId) },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(48.dp),
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFFFF4D4F),
                                        Color(0xFFFF7A45)
                                    )
                                ),
                                shape = RoundedCornerShape(25.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "确认",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}