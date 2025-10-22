package com.example.MyJD.presenter

import com.example.MyJD.model.Address

interface AddressDetailContract {
    interface View {
        fun showLoading()
        fun hideLoading()
        fun showAddress(address: Address)
        fun showError(message: String)
        fun showValidationError(field: String, message: String)
        fun showSaveSuccess()
        fun navigateBack()
        fun clearForm()
        fun setFormData(
            name: String,
            phone: String,
            province: String,
            city: String,
            district: String,
            detailAddress: String,
            isDefault: Boolean,
            tag: String
        )
    }
    
    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun loadAddress(addressId: String?)
        fun saveAddress(
            addressId: String?,
            name: String,
            phone: String,
            province: String,
            city: String,
            district: String,
            detailAddress: String,
            isDefault: Boolean,
            tag: String
        )
        fun validateForm(
            name: String,
            phone: String,
            province: String,
            city: String,
            district: String,
            detailAddress: String
        ): Boolean
    }
}