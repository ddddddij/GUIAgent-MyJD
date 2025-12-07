package com.example.jd_sim.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jd_sim.domain.model.Address
import com.example.jd_sim.repository.DataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddressDetailViewModel @Inject constructor(
    private val repository: DataRepository
) : ViewModel() {

    // UI State
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _address = MutableStateFlow<Address?>(null)
    val address: StateFlow<Address?> = _address.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _validationErrors = MutableStateFlow<Map<String, String>>(emptyMap())
    val validationErrors: StateFlow<Map<String, String>> = _validationErrors.asStateFlow()

    private val _navigationEvent = MutableStateFlow<NavigationEvent?>(null)
    val navigationEvent: StateFlow<NavigationEvent?> = _navigationEvent.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    // Form data
    private val _formData = MutableStateFlow(FormData())
    val formData: StateFlow<FormData> = _formData.asStateFlow()

    // Public methods for UI
    fun loadAddress(addressId: String?) {
        if (addressId == null) {
            // 新增地址，清空表单
            clearForm()
            return
        }

        _isLoading.value = true

        viewModelScope.launch {
            try {
                val address = repository.getAddressById(addressId)
                _isLoading.value = false
                if (address != null) {
                    _address.value = address
                    setFormData(
                        name = address.recipientName,
                        phone = address.phoneNumber,
                        province = address.province,
                        city = address.city,
                        district = address.district,
                        detailAddress = address.detailAddress,
                        isDefault = address.isDefault,
                        tag = address.tag
                    )
                } else {
                    _errorMessage.value = "地址不存在"
                }
            } catch (e: Exception) {
                android.util.Log.e("AddressDetailViewModel", "Error loading address", e)
                _isLoading.value = false
                _errorMessage.value = "加载地址失败：${e.message}"
            }
        }
    }

    fun saveAddress(addressId: String?) {
        val form = _formData.value
        if (!validateForm(form.name, form.phone, form.province, form.city, form.district, form.detailAddress)) {
            return
        }

        _isLoading.value = true

        viewModelScope.launch {
            try {
                val address = Address(
                    id = addressId ?: "addr_${System.currentTimeMillis()}",
                    recipientName = form.name.trim(),
                    phoneNumber = form.phone.trim(),
                    province = form.province.trim(),
                    city = form.city.trim(),
                    district = form.district.trim(),
                    detailAddress = form.detailAddress.trim(),
                    isDefault = form.isDefault,
                    tag = form.tag,
                    createTime = System.currentTimeMillis()
                )

                val success = if (addressId == null) {
                    repository.addAddress(address)
                } else {
                    repository.updateAddress(address)
                }

                _isLoading.value = false
                if (success) {
                    _toastMessage.value = "地址保存成功"
                    _navigationEvent.value = NavigationEvent.SaveSuccess
                } else {
                    _errorMessage.value = "保存地址失败"
                }
            } catch (e: Exception) {
                android.util.Log.e("AddressDetailViewModel", "Error saving address", e)
                _isLoading.value = false
                _errorMessage.value = "保存地址失败：${e.message}"
            }
        }
    }

    private fun validateForm(
        name: String,
        phone: String,
        province: String,
        city: String,
        district: String,
        detailAddress: String
    ): Boolean {
        val currentErrors = _validationErrors.value.toMutableMap()

        // 验证收货人姓名
        if (name.trim().isEmpty()) {
            currentErrors["name"] = "请输入收货人姓名"
        } else if (name.trim().length > 20) {
            currentErrors["name"] = "收货人姓名不能超过20个字符"
        }

        // 验证手机号码
        if (phone.trim().isEmpty()) {
            currentErrors["phone"] = "请输入手机号码"
        } else {
            val phonePattern = "^1[3-9]\\d{9}$".toRegex()
            if (!phone.trim().matches(phonePattern)) {
                currentErrors["phone"] = "请输入正确的手机号码"
            }
        }

        // 验证省市区
        if (province.trim().isEmpty()) {
            currentErrors["province"] = "请选择省份"
        }
        if (city.trim().isEmpty()) {
            currentErrors["city"] = "请选择城市"
        }
        if (district.trim().isEmpty()) {
            currentErrors["district"] = "请选择区县"
        }

        // 验证详细地址
        if (detailAddress.trim().isEmpty()) {
            currentErrors["detailAddress"] = "请输入详细地址"
        } else if (detailAddress.trim().length < 5) {
            currentErrors["detailAddress"] = "详细地址至少需要5个字符"
        } else if (detailAddress.trim().length > 100) {
            currentErrors["detailAddress"] = "详细地址不能超过100个字符"
        }

        _validationErrors.value = currentErrors
        return currentErrors.isEmpty()
    }

    fun updateFormData(
        name: String? = null,
        phone: String? = null,
        province: String? = null,
        city: String? = null,
        district: String? = null,
        detailAddress: String? = null,
        isDefault: Boolean? = null,
        tag: String? = null
    ) {
        val current = _formData.value
        _formData.value = current.copy(
            name = name ?: current.name,
            phone = phone ?: current.phone,
            province = province ?: current.province,
            city = city ?: current.city,
            district = district ?: current.district,
            detailAddress = detailAddress ?: current.detailAddress,
            isDefault = isDefault ?: current.isDefault,
            tag = tag ?: current.tag
        )
    }

    fun clearNavigationEvent() {
        _navigationEvent.value = null
    }

    fun clearToastMessage() {
        _toastMessage.value = null
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    fun clearValidationError(field: String) {
        val currentErrors = _validationErrors.value.toMutableMap()
        currentErrors.remove(field)
        _validationErrors.value = currentErrors
    }

    private fun clearForm() {
        _formData.value = FormData()
        _validationErrors.value = emptyMap()
    }

    private fun setFormData(
        name: String,
        phone: String,
        province: String,
        city: String,
        district: String,
        detailAddress: String,
        isDefault: Boolean,
        tag: String
    ) {
        _formData.value = FormData(
            name = name,
            phone = phone,
            province = province,
            city = city,
            district = district,
            detailAddress = detailAddress,
            isDefault = isDefault,
            tag = tag
        )
    }

    data class FormData(
        val name: String = "",
        val phone: String = "",
        val province: String = "湖北省",
        val city: String = "武汉市",
        val district: String = "江夏区",
        val detailAddress: String = "",
        val isDefault: Boolean = false,
        val tag: String = "家"
    )

    sealed class NavigationEvent {
        object Back : NavigationEvent()
        object SaveSuccess : NavigationEvent()
    }
}