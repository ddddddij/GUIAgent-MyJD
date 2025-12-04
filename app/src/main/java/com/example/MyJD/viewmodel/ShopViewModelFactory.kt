package com.example.myjd.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myjd.repository.DataRepository

class ShopViewModelFactory(
    private val repository: DataRepository,
    private val context: Context,
    private val shopName: String
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ShopViewModel::class.java)) {
            return ShopViewModel(repository, context, shopName) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}