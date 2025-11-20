package com.example.MyJD.viewmodel

import androidx.lifecycle.ViewModel
import com.example.MyJD.model.Address
import com.example.MyJD.presenter.AddressDetailContract
import com.example.MyJD.presenter.AddressDetailPresenter
import com.example.MyJD.repository.DataRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AddressDetailViewModel(
    private val repository: DataRepository
) : ViewModel(), AddressDetailContract.View {
    
    private val presenter: AddressDetailPresenter = AddressDetailPresenter(repository)
    
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
    
    init {
        presenter.attachView(this)
    }
    
    override fun onCleared() {
        super.onCleared()
        presenter.detachView()
    }
    
    // Public methods for UI
    fun loadAddress(addressId: String?) {
        presenter.loadAddress(addressId)
    }
    
    fun saveAddress(addressId: String?) {
        val form = _formData.value
        presenter.saveAddress(
            addressId = addressId,
            name = form.name,
            phone = form.phone,
            province = form.province,
            city = form.city,
            district = form.district,
            detailAddress = form.detailAddress,
            isDefault = form.isDefault,
            tag = form.tag
        )
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
    
    // AddressDetailContract.View implementation
    override fun showLoading() {
        _isLoading.value = true
    }
    
    override fun hideLoading() {
        _isLoading.value = false
    }
    
    override fun showAddress(address: Address) {
        _address.value = address
    }
    
    override fun showError(message: String) {
        _errorMessage.value = message
    }
    
    override fun showValidationError(field: String, message: String) {
        val currentErrors = _validationErrors.value.toMutableMap()
        currentErrors[field] = message
        _validationErrors.value = currentErrors
    }
    
    override fun showSaveSuccess() {
        _toastMessage.value = "地址保存成功"
        _navigationEvent.value = NavigationEvent.SaveSuccess
    }
    
    override fun navigateBack() {
        _navigationEvent.value = NavigationEvent.Back
    }
    
    override fun clearForm() {
        _formData.value = FormData()
        _validationErrors.value = emptyMap()
    }
    
    override fun setFormData(
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