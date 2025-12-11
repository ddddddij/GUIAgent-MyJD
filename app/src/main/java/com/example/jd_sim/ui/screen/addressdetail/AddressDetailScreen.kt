package com.example.jd_sim.ui.screen.addressdetail

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.jd_sim.ui.screen.addressdetail.components.*
import com.example.jd_sim.ui.screen.addressdetail.AddressDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressDetailScreen(
    addressId: String?,
    onBackClick: () -> Unit,
    onSaveSuccess: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: AddressDetailViewModel = hiltViewModel()
) {
    val context = LocalContext.current

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
                    AddressReceiverInfoCard(
                        name = formData.name,
                        phone = formData.phone,
                        onNameChange = {
                            viewModel.updateFormData(name = it)
                            viewModel.clearValidationError("name")
                        },
                        onPhoneChange = {
                            viewModel.updateFormData(phone = it)
                            viewModel.clearValidationError("phone")
                        },
                        nameError = validationErrors["name"],
                        phoneError = validationErrors["phone"]
                    )

                    // 地址选择区域
                    AddressDetailCard(
                        detailAddress = formData.detailAddress,
                        onDetailAddressChange = {
                            viewModel.updateFormData(detailAddress = it)
                            viewModel.clearValidationError("detailAddress")
                        },
                        detailAddressError = validationErrors["detailAddress"]
                    )

                    // 设为默认地址
                    AddressDefaultSwitchCard(
                        isDefault = formData.isDefault,
                        onDefaultChange = {
                            viewModel.updateFormData(isDefault = it)
                        }
                    )

                    // 标签选择
                    AddressTagCard(
                        selectedTag = formData.tag,
                        onTagSelect = {
                            viewModel.updateFormData(tag = it)
                        }
                    )

                    // 底部间距，避免被确认按钮遮挡
                    Spacer(modifier = Modifier.height(80.dp))
                }

                // 确认按钮
                AddressSaveButton(
                    onClick = { viewModel.saveAddress(addressId) },
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
    }
}
