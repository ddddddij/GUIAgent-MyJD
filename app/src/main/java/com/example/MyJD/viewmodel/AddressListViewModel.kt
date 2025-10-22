package com.example.MyJD.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.MyJD.model.Address
import com.example.MyJD.presenter.AddressListContract
import com.example.MyJD.presenter.AddressListPresenter
import com.example.MyJD.repository.DataRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddressListViewModel(
    private val repository: DataRepository
) : ViewModel(), AddressListContract.View {
    
    private val presenter: AddressListPresenter = AddressListPresenter(repository)
    
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
        presenter.attachView(this)
        loadAddresses()
    }
    
    override fun onCleared() {
        super.onCleared()
        presenter.detachView()
    }
    
    // Public methods for UI
    fun loadAddresses() {
        presenter.loadAddresses()
    }
    
    fun onAddNewAddressClick() {
        presenter.onAddNewAddressClick()
    }
    
    fun onAddressClick(address: Address) {
        presenter.onAddressClick(address)
    }
    
    fun onEditAddressClick(address: Address) {
        presenter.onEditAddressClick(address)
    }
    
    fun onDeleteAddressClick(address: Address) {
        presenter.onDeleteAddressClick(address)
    }
    
    fun onSetDefaultAddressClick(address: Address) {
        presenter.onSetDefaultAddressClick(address)
    }
    
    fun onCopyAddressClick(address: Address) {
        presenter.onCopyAddressClick(address)
    }
    
    fun confirmDeleteAddress(address: Address) {
        presenter.confirmDeleteAddress(address)
        _showDeleteDialog.value = null
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
    
    // AddressListContract.View implementation
    override fun showLoading() {
        _isLoading.value = true
    }
    
    override fun hideLoading() {
        _isLoading.value = false
    }
    
    override fun showAddresses(addresses: List<Address>) {
        _addresses.value = addresses
        _isEmpty.value = false
    }
    
    override fun showEmptyAddresses() {
        _addresses.value = emptyList()
        _isEmpty.value = true
    }
    
    override fun showError(message: String) {
        _errorMessage.value = message
    }
    
    override fun navigateToAddressDetail(addressId: String?) {
        _navigationEvent.value = NavigationEvent.ToAddressDetail(addressId)
    }
    
    override fun navigateToSettleScreen(selectedAddress: Address) {
        _navigationEvent.value = NavigationEvent.ToSettleScreen(selectedAddress)
    }
    
    override fun showDeleteConfirmation(address: Address) {
        _showDeleteDialog.value = address
    }
    
    override fun showAddressDeleted() {
        _toastMessage.value = "地址删除成功"
    }
    
    override fun showDefaultAddressSet(address: Address) {
        _toastMessage.value = "已设为默认地址"
    }
    
    override fun showAddressCopied() {
        _toastMessage.value = "地址已复制到剪贴板"
    }
    
    sealed class NavigationEvent {
        data class ToAddressDetail(val addressId: String?) : NavigationEvent()
        data class ToSettleScreen(val selectedAddress: Address) : NavigationEvent()
    }
}