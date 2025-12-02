package com.example.MyJD.presenter

import com.example.MyJD.model.Address

interface AddressListContract {
    interface View {
        fun showLoading()
        fun hideLoading()
        fun showAddresses(addresses: List<Address>)
        fun showEmptyAddresses()
        fun showError(message: String)
        fun navigateToAddressDetail(addressId: String? = null)
        fun navigateToSettleScreen(selectedAddress: Address)
        fun showDeleteConfirmation(address: Address)
        fun showAddressDeleted()
        fun showDefaultAddressSet(address: Address)
        fun showAddressCopied()
    }
    
    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun loadAddresses()
        fun onAddNewAddressClick()
        fun onAddressClick(address: Address)
        fun onEditAddressClick(address: Address)
        fun onDeleteAddressClick(address: Address)
        fun onSetDefaultAddressClick(address: Address)
        fun onCopyAddressClick(address: Address)
        fun confirmDeleteAddress(address: Address)
    }
}