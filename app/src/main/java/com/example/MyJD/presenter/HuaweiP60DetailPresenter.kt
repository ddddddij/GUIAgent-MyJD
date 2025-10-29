package com.example.MyJD.presenter

import com.example.MyJD.model.ProductDetail
import com.example.MyJD.repository.DataRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HuaweiP60DetailPresenter(
    private val repository: DataRepository
) : HuaweiP60DetailContract.Presenter {
    
    private var view: HuaweiP60DetailContract.View? = null
    private var currentProductDetail: ProductDetail? = null
    private val presenterScope = CoroutineScope(Dispatchers.Main + Job())
    
    override fun attachView(view: HuaweiP60DetailContract.View) {
        this.view = view
    }
    
    override fun detachView() {
        this.view = null
    }
    
    override fun loadProductDetail(productId: String) {
        view?.showLoading()
        
        presenterScope.launch {
            try {
                val productDetail = withContext(Dispatchers.IO) {
                    repository.getHuaweiP60ProductDetail(productId)
                }
                
                if (productDetail != null) {
                    currentProductDetail = productDetail
                    view?.hideLoading()
                    view?.showProductDetail(productDetail)
                } else {
                    view?.hideLoading()
                    view?.showError("商品详情加载失败")
                }
            } catch (e: Exception) {
                view?.hideLoading()
                view?.showError("网络错误：${e.message}")
            }
        }
    }
    
    override fun toggleFavorite() {
        currentProductDetail?.let { detail ->
            val newFavoriteState = !detail.isFavorite
            currentProductDetail = detail.copy(isFavorite = newFavoriteState)
            view?.showFavoriteToggled(newFavoriteState)
        }
    }
    
    override fun selectColor(colorIndex: Int) {
        currentProductDetail?.let { detail ->
            if (colorIndex in detail.colors.indices) {
                currentProductDetail = detail.copy(selectedColorIndex = colorIndex)
            }
        }
    }
    
    override fun selectPurchaseType(typeIndex: Int) {
        currentProductDetail?.let { detail ->
            if (typeIndex in detail.purchaseTypes.indices) {
                currentProductDetail = detail.copy(selectedPurchaseType = typeIndex)
            }
        }
    }
    
    override fun addToCart() {
        currentProductDetail?.let { detail ->
            view?.showAddToCartSuccess()
        }
    }
    
    override fun onReviewSectionViewed() {
        // 日志记录逻辑，如果需要的话
    }
    
    override fun onReviewsLoaded(reviewCount: Int) {
        // 日志记录逻辑，如果需要的话
    }
}