package com.example.jd_sim.ui.screen.cart

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jd_sim.domain.model.CartItem
import com.example.jd_sim.domain.model.CartItemSpec
import com.example.jd_sim.domain.repository.DataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val repository: DataRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    // Expose repository's specCartFlow directly
    val specCartFlow: StateFlow<List<CartItemSpec>> = repository.specCartFlow

    // UI State
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _selectedItems = MutableStateFlow<Set<String>>(emptySet())
    val selectedItems: StateFlow<Set<String>> = _selectedItems.asStateFlow()

    private val _totalPrice = MutableStateFlow(0.0)
    val totalPrice: StateFlow<Double> = _totalPrice.asStateFlow()

    private val _selectAll = MutableStateFlow(false)
    val selectAll: StateFlow<Boolean> = _selectAll.asStateFlow()

    init {
        loadCartItems()
    }

    fun loadCartItems() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val items = repository.getCartItems()
                _cartItems.value = items
                recalculateTotalPrice()
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load cart items: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateItemQuantity(itemId: String, newQuantity: Int) {
        viewModelScope.launch {
            if (newQuantity > 0) {
                val success = repository.updateCartItemQuantity(itemId, newQuantity)
                if (success) {
                    loadCartItems()
                } else {
                    _errorMessage.value = "Failed to update item quantity."
                }
            }
        }
    }

    fun removeItem(itemId: String) {
        viewModelScope.launch {
            val success = repository.removeCartItem(itemId)
            if (success) {
                _selectedItems.value -= itemId
                loadCartItems()
            } else {
                _errorMessage.value = "Failed to remove item."
            }
        }
    }

    fun toggleItemSelection(itemId: String) {
        val currentSelection = _selectedItems.value.toMutableSet()
        if (currentSelection.contains(itemId)) {
            currentSelection.remove(itemId)
        } else {
            currentSelection.add(itemId)
        }
        _selectedItems.value = currentSelection
        recalculateTotalPrice()
        _selectAll.value = _cartItems.value.isNotEmpty() && currentSelection.size == _cartItems.value.size
    }

    fun toggleSelectAll() {
        val allSelected = _selectAll.value
        if (allSelected) {
            _selectedItems.value = emptySet()
        } else {
            _selectedItems.value = _cartItems.value.map { it.id }.toSet()
        }
        _selectAll.value = !allSelected
        recalculateTotalPrice()
    }

    private fun recalculateTotalPrice() {
        val total = _cartItems.value
            .filter { _selectedItems.value.contains(it.id) }
            .sumOf { it.product.price * it.quantity }
        _totalPrice.value = total
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    // Methods for spec cart operations (used by CartScreen)
    fun isAllSpecCartSelected(): Boolean {
        return repository.isAllSpecCartSelected()
    }

    fun forceLoadCartDataFromAssets() {
        viewModelScope.launch {
            repository.forceLoadCartDataFromAssets()
        }
    }

    fun getSelectedSpecCartTotalPrice(): Double {
        return repository.getSelectedSpecCartTotalPrice()
    }

    fun getSpecCartTotalCount(): Int {
        return repository.getSpecCartTotalCount()
    }

    fun getSelectedSpecCartCount(): Int {
        return repository.getSelectedSpecCartCount()
    }

    fun removeFromSpecCart(itemId: String) {
        viewModelScope.launch {
            repository.removeFromSpecCart(itemId)
        }
    }

    fun toggleAllSpecCartSelection() {
        repository.toggleAllSpecCartSelection()
    }

    fun toggleStoreSpecCartSelection(storeName: String) {
        repository.toggleStoreSpecCartSelection(storeName)
    }

    fun toggleSpecCartItemSelection(itemId: String) {
        repository.toggleSpecCartItemSelection(itemId)
    }

    fun updateSpecCartItemQuantity(itemId: String, quantity: Int) {
        repository.updateSpecCartItemQuantity(itemId, quantity)
    }
}
