package com.example.MyJD.presenter

import com.example.MyJD.model.Address
import com.example.MyJD.repository.DataRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddressDetailPresenter(
    private val repository: DataRepository
) : AddressDetailContract.Presenter {
    
    private var view: AddressDetailContract.View? = null
    private val presenterScope = CoroutineScope(Dispatchers.Main + Job())
    
    override fun attachView(view: AddressDetailContract.View) {
        this.view = view
    }
    
    override fun detachView() {
        this.view = null
    }
    
    override fun loadAddress(addressId: String?) {
        if (addressId == null) {
            // 新增地址，清空表单
            view?.clearForm()
            return
        }
        
        view?.showLoading()
        
        presenterScope.launch {
            try {
                val address = repository.getAddressById(addressId)
                
                withContext(Dispatchers.Main) {
                    view?.hideLoading()
                    if (address != null) {
                        view?.showAddress(address)
                        view?.setFormData(
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
                        view?.showError("地址不存在")
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("AddressDetailPresenter", "Error loading address", e)
                withContext(Dispatchers.Main) {
                    view?.hideLoading()
                    view?.showError("加载地址失败：${e.message}")
                }
            }
        }
    }
    
    override fun saveAddress(
        addressId: String?,
        name: String,
        phone: String,
        province: String,
        city: String,
        district: String,
        detailAddress: String,
        isDefault: Boolean,
        tag: String
    ) {
        // 验证表单
        if (!validateForm(name, phone, province, city, district, detailAddress)) {
            return
        }
        
        view?.showLoading()
        
        presenterScope.launch {
            try {
                val success = withContext(Dispatchers.IO) {
                    val address = Address(
                        id = addressId ?: "addr_${System.currentTimeMillis()}",
                        recipientName = name.trim(),
                        phoneNumber = phone.trim(),
                        province = province.trim(),
                        city = city.trim(),
                        district = district.trim(),
                        detailAddress = detailAddress.trim(),
                        isDefault = isDefault,
                        tag = tag,
                        createTime = System.currentTimeMillis()
                    )
                    
                    if (addressId == null) {
                        // 新增地址
                        repository.addAddress(address)
                    } else {
                        // 更新地址
                        repository.updateAddress(address)
                    }
                }
                
                withContext(Dispatchers.Main) {
                    view?.hideLoading()
                    if (success) {
                        view?.showSaveSuccess()
                        // 不再直接调用navigateBack，由ViewModel处理导航
                    } else {
                        view?.showError("保存地址失败")
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("AddressDetailPresenter", "Error saving address", e)
                withContext(Dispatchers.Main) {
                    view?.hideLoading()
                    view?.showError("保存地址失败：${e.message}")
                }
            }
        }
    }
    
    override fun validateForm(
        name: String,
        phone: String,
        province: String,
        city: String,
        district: String,
        detailAddress: String
    ): Boolean {
        // 验证收货人姓名
        if (name.trim().isEmpty()) {
            view?.showValidationError("name", "请输入收货人姓名")
            return false
        }
        
        if (name.trim().length > 20) {
            view?.showValidationError("name", "收货人姓名不能超过20个字符")
            return false
        }
        
        // 验证手机号码
        if (phone.trim().isEmpty()) {
            view?.showValidationError("phone", "请输入手机号码")
            return false
        }
        
        val phonePattern = "^1[3-9]\\d{9}$".toRegex()
        if (!phone.trim().matches(phonePattern)) {
            view?.showValidationError("phone", "请输入正确的手机号码")
            return false
        }
        
        // 验证省市区
        if (province.trim().isEmpty()) {
            view?.showValidationError("province", "请选择省份")
            return false
        }
        
        if (city.trim().isEmpty()) {
            view?.showValidationError("city", "请选择城市")
            return false
        }
        
        if (district.trim().isEmpty()) {
            view?.showValidationError("district", "请选择区县")
            return false
        }
        
        // 验证详细地址
        if (detailAddress.trim().isEmpty()) {
            view?.showValidationError("detailAddress", "请输入详细地址")
            return false
        }
        
        if (detailAddress.trim().length < 5) {
            view?.showValidationError("detailAddress", "详细地址至少需要5个字符")
            return false
        }
        
        if (detailAddress.trim().length > 100) {
            view?.showValidationError("detailAddress", "详细地址不能超过100个字符")
            return false
        }
        
        return true
    }
}