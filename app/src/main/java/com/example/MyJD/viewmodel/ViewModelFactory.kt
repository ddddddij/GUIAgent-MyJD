package com.example.MyJD.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.MyJD.repository.DataRepository

class ViewModelFactory(
    private val repository: DataRepository,
    private val context: Context
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when (modelClass) {
            HomeViewModel::class.java -> HomeViewModel(repository, context) as T
            ChatViewModel::class.java -> ChatViewModel(repository, context) as T
            MeViewModel::class.java -> MeViewModel(repository) as T
            OrderViewModel::class.java -> OrderViewModel(repository, context) as T
            SettleViewModel::class.java -> SettleViewModel(repository, context) as T
            AddressListViewModel::class.java -> AddressListViewModel(repository) as T
            AddressDetailViewModel::class.java -> AddressDetailViewModel(repository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}