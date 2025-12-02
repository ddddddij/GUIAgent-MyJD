package com.example.MyJD.presenter

import com.example.MyJD.model.Address
import com.example.MyJD.repository.DataRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddressListPresenter(
    private val repository: DataRepository
) : AddressListContract.Presenter {
    
    private var view: AddressListContract.View? = null
    private val presenterScope = CoroutineScope(Dispatchers.Main + Job())
    
    override fun attachView(view: AddressListContract.View) {
        this.view = view
    }
    
    override fun detachView() {
        this.view = null
    }
    
    override fun loadAddresses() {
        view?.showLoading()
        
        presenterScope.launch {
            try {
                val addresses = repository.loadAddresses()
                
                withContext(Dispatchers.Main) {
                    view?.hideLoading()
                    if (addresses.isEmpty()) {
                        view?.showEmptyAddresses()
                    } else {
                        view?.showAddresses(addresses)
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("AddressListPresenter", "Error loading addresses", e)
                withContext(Dispatchers.Main) {
                    view?.hideLoading()
                    view?.showError("加载地址失败：${e.message}")
                }
            }
        }
    }
    
    override fun onAddNewAddressClick() {
        view?.navigateToAddressDetail()
    }
    
    override fun onAddressClick(address: Address) {
        // 如果从结算页面进入，选择地址后返回结算页面
        view?.navigateToSettleScreen(address)
    }
    
    override fun onEditAddressClick(address: Address) {
        view?.navigateToAddressDetail(address.id)
    }
    
    override fun onDeleteAddressClick(address: Address) {
        view?.showDeleteConfirmation(address)
    }
    
    override fun onSetDefaultAddressClick(address: Address) {
        presenterScope.launch {
            try {
                val success = withContext(Dispatchers.IO) {
                    repository.setDefaultAddress(address.id)
                }
                
                withContext(Dispatchers.Main) {
                    if (success) {
                        view?.showDefaultAddressSet(address)
                        // 重新加载地址列表以更新默认状态
                        loadAddresses()
                    } else {
                        view?.showError("设置默认地址失败")
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("AddressListPresenter", "Error setting default address", e)
                withContext(Dispatchers.Main) {
                    view?.showError("设置默认地址失败：${e.message}")
                }
            }
        }
    }
    
    override fun onCopyAddressClick(address: Address) {
        val addressText = repository.copyAddressToClipboard(address)
        android.util.Log.d("AddressListPresenter", "Address copied: $addressText")
        view?.showAddressCopied()
    }
    
    override fun confirmDeleteAddress(address: Address) {
        presenterScope.launch {
            try {
                val success = withContext(Dispatchers.IO) {
                    repository.deleteAddress(address.id)
                }
                
                withContext(Dispatchers.Main) {
                    if (success) {
                        view?.showAddressDeleted()
                        // 重新加载地址列表
                        loadAddresses()
                    } else {
                        view?.showError("删除地址失败")
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("AddressListPresenter", "Error deleting address", e)
                withContext(Dispatchers.Main) {
                    view?.showError("删除地址失败：${e.message}")
                }
            }
        }
    }
}