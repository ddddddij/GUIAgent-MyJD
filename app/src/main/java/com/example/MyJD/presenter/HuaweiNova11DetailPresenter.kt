package com.example.MyJD.presenter

import com.example.MyJD.model.ProductDetail
import com.example.MyJD.repository.DataRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HuaweiNova11DetailPresenter(
    private val repository: DataRepository
) : HuaweiNova11DetailContract.Presenter {
    
    private var view: HuaweiNova11DetailContract.View? = null
    private val presenterScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    
    private var currentProductDetail: ProductDetail? = null
    
    override fun attachView(view: HuaweiNova11DetailContract.View) {
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
                    repository.getHuaweiNova11ProductDetail(productId)
                }
                
                if (productDetail != null) {
                    currentProductDetail = productDetail
                    view?.showProductDetail(productDetail)
                } else {
                    view?.showError("商品信息加载失败")
                }
            } catch (e: Exception) {
                view?.showError("加载商品信息时出错: ${e.message}")
            } finally {
                view?.hideLoading()
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
                view?.showProductDetail(currentProductDetail!!)
            }
        }
    }
    
    override fun selectPurchaseType(typeIndex: Int) {
        currentProductDetail?.let { detail ->
            if (typeIndex in detail.purchaseTypes.indices) {
                currentProductDetail = detail.copy(selectedPurchaseType = typeIndex)
                view?.showProductDetail(currentProductDetail!!)
            }
        }
    }
    
    override fun addToCart() {
        currentProductDetail?.let { detail ->
            // 这里可以添加将商品加入购物车的逻辑
            // 目前只显示成功提示
            view?.showAddToCartSuccess()
        }
    }
    
    override fun onReviewSectionViewed() {
        // 记录任务十四相关日志：查看评论区域
        // repository.logTaskFourteenReviewSectionViewed()
    }
    
    override fun onReviewsLoaded(reviewCount: Int) {
        // 记录任务十四相关日志：评论加载完成
        // repository.logTaskFourteenReviewsLoaded(reviewCount)
    }
}