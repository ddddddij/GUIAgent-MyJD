package com.example.MyJD.presenter

import com.example.MyJD.model.ShopPageData
import com.example.MyJD.model.ShopCategory
import com.example.MyJD.model.Product
import com.example.MyJD.model.CartItemSpec
import com.example.MyJD.repository.DataRepository
import kotlinx.coroutines.*

class ShopPagePresenter(
    private val repository: DataRepository
) : ShopPageContract.Presenter {
    
    private var view: ShopPageContract.View? = null
    private var shopData: ShopPageData? = null
    private val presenterScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    
    override fun attach(view: ShopPageContract.View) {
        this.view = view
    }
    
    override fun detach() {
        this.view = null
        presenterScope.cancel()
    }
    
    override fun loadShopData() {
        presenterScope.launch {
            view?.showLoading(true)
            try {
                val data = repository.loadShopPageData()
                shopData = data
                view?.showShopData(data)
                android.util.Log.d("ShopPagePresenter", "Loaded shop data: ${data.shopInfo.name}")
            } catch (e: Exception) {
                view?.showToast("店铺数据加载失败")
                android.util.Log.e("ShopPagePresenter", "Failed to load shop data", e)
            } finally {
                view?.showLoading(false)
            }
        }
    }
    
    override fun onCategorySelected(categoryId: String) {
        val currentData = shopData ?: return
        
        // 更新分类选中状态
        val updatedCategories = currentData.categories.map { category ->
            category.copy(isSelected = category.id == categoryId)
        }
        
        view?.updateCategories(updatedCategories)
        
        // 更新shopData中的分类状态
        shopData = currentData.copy(categories = updatedCategories)
        
        android.util.Log.d("ShopPagePresenter", "Category selected: $categoryId")
        
        // 按要求：分类选择仅为UI展示，无实际过滤功能
    }
    
    override fun onProductClick(product: Product) {
        android.util.Log.d("ShopPagePresenter", "Product clicked: ${product.name}")
        view?.navigateToProductDetail(product.id)
    }
    
    override fun onAddToCartClick(product: Product) {
        try {
            // 创建购物车商品规格
            val cartItem = CartItemSpec(
                id = "cart_${product.id}_${System.currentTimeMillis()}",
                productId = product.id,
                productName = product.name,
                series = product.name, // 使用商品名作为系列
                color = product.colors.firstOrNull() ?: "默认",
                storage = product.versions.firstOrNull() ?: "标准版",
                image = product.imageUrl,
                price = product.price,
                originalPrice = product.originalPrice ?: product.price,
                quantity = 1,
                selected = false,
                storeName = product.storeName
            )
            
            // 添加到购物车
            repository.addToSpecCart(cartItem)
            
            view?.showToast("已添加到购物车")
            android.util.Log.d("ShopPagePresenter", "Added to cart: ${product.name}")
            
        } catch (e: Exception) {
            view?.showToast("添加到购物车失败")
            android.util.Log.e("ShopPagePresenter", "Failed to add to cart", e)
        }
    }
    
    override fun onBackClick() {
        android.util.Log.d("ShopPagePresenter", "Back button clicked")
        view?.navigateBack()
    }
    
    fun getCurrentShopData(): ShopPageData? = shopData
    
    fun getSelectedCategory(): ShopCategory? {
        return shopData?.categories?.find { it.isSelected }
    }
}