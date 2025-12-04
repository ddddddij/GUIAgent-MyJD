package com.example.myjd.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myjd.domain.model.Address
import com.example.myjd.repository.DataRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddressListViewModel(
    private val repository: DataRepository
) : ViewModel() {

    // UI State
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _addresses = MutableStateFlow<List<Address>>(emptyList())
    val addresses: StateFlow<List<Address>> = _addresses.asStateFlow()

    private val _isEmpty = MutableStateFlow(false)
    val isEmpty: StateFlow<Boolean> = _isEmpty.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _navigationEvent = MutableStateFlow<NavigationEvent?>(null)
    val navigationEvent: StateFlow<NavigationEvent?> = _navigationEvent.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    private val _showDeleteDialog = MutableStateFlow<Address?>(null)
    val showDeleteDialog: StateFlow<Address?> = _showDeleteDialog.asStateFlow()

    init {
        loadAddresses()
    }

    // Public methods for UI
    fun loadAddresses() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val addresses = repository.loadAddresses()
                _isLoading.value = false
                if (addresses.isEmpty()) {
                    _addresses.value = emptyList()
                    _isEmpty.value = true
                } else {
                    _addresses.value = addresses
                    _isEmpty.value = false
                }
            } catch (e: Exception) {
                android.util.Log.e("AddressListViewModel", "Error loading addresses", e)
                _isLoading.value = false
                _errorMessage.value = "加载地址失败：${e.message}"
            }
        }
    }

    fun onAddNewAddressClick() {
        _navigationEvent.value = NavigationEvent.ToAddressDetail(null)
    }

    fun onAddressClick(address: Address) {
        // If coming from the checkout page, select the address and return
        _navigationEvent.value = NavigationEvent.ToSettleScreen(address)
    }

    fun onEditAddressClick(address: Address) {
        _navigationEvent.value = NavigationEvent.ToAddressDetail(address.id)
    }

    fun onDeleteAddressClick(address: Address) {
        _showDeleteDialog.value = address
    }

    fun onSetDefaultAddressClick(address: Address) {
        viewModelScope.launch {
            try {
                val success = repository.setDefaultAddress(address.id)
                if (success) {
                    _toastMessage.value = "已设为默认地址"
                    // Reload addresses to update the default status
                    loadAddresses()
                } else {
                    _errorMessage.value = "设置默认地址失败"
                }
            } catch (e: Exception) {
                android.util.Log.e("AddressListViewModel", "Error setting default address", e)
                _errorMessage.value = "设置默认地址失败：${e.message}"
            }
        }
    }

    fun onCopyAddressClick(address: Address) {
        val addressText = repository.copyAddressToClipboard(address)
        android.util.Log.d("AddressListViewModel", "Address copied: $addressText")
        _toastMessage.value = "地址已复制到剪贴板"
    }

    fun confirmDeleteAddress(address: Address) {
        _showDeleteDialog.value = null
        viewModelScope.launch {
            try {
                val success = repository.deleteAddress(address.id)
                if (success) {
                    _toastMessage.value = "地址删除成功"
                    // Reload address list
                    loadAddresses()
                } else {
                    _errorMessage.value = "删除地址失败"
                }
            } catch (e: Exception) {
                android.util.Log.e("AddressListViewModel", "Error deleting address", e)
                _errorMessage.value = "删除地址失败：${e.message}"
            }
        }
    }

    fun dismissDeleteDialog() {
        _showDeleteDialog.value = null
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

    sealed class NavigationEvent {
        data class ToAddressDetail(val addressId: String?) : NavigationEvent()
        data class ToSettleScreen(val selectedAddress: Address) : NavigationEvent()
    }
}