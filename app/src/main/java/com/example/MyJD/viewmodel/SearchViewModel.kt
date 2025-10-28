package com.example.MyJD.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.MyJD.presenter.SearchContract
import com.example.MyJD.presenter.SearchPresenter
import com.example.MyJD.repository.DataRepository
import com.example.MyJD.utils.TaskSixteenLogger

class SearchViewModel(
    private val repository: DataRepository,
    private val context: Context
) : ViewModel(), SearchContract.View {
    
    private val presenter: SearchContract.Presenter = SearchPresenter()
    
    private val _suggestions = MutableStateFlow<List<String>>(emptyList())
    val suggestions: StateFlow<List<String>> = _suggestions.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()
    
    private val _navigationEvent = MutableStateFlow<String?>(null)
    val navigationEvent: StateFlow<String?> = _navigationEvent.asStateFlow()
    
    private val _searchKeyword = MutableStateFlow("iPhone 15")
    val searchKeyword: StateFlow<String> = _searchKeyword.asStateFlow()
    
    init {
        presenter.attach(this)
        loadSuggestions()
    }
    
    override fun onCleared() {
        super.onCleared()
        presenter.detach()
    }
    
    fun loadSuggestions() {
        presenter.loadSuggestions()
    }
    
    fun onSuggestionClicked(suggestion: String) {
        presenter.onSuggestionClicked(suggestion)
    }
    
    fun onSearchClicked(keyword: String = _searchKeyword.value) {
        // 任务一日志记录：搜索发起
        if (keyword.contains("iPhone 15", ignoreCase = true)) {
            repository.logTaskOneSearchInitiated(keyword)
        }
        
        // 任务十六日志记录：搜索iPhone15
        if (keyword.contains("iPhone15") || keyword.contains("iPhone 15")) {
            TaskSixteenLogger.logTaskStart(context)
            TaskSixteenLogger.logSearchInitiated(context, keyword)
        }
        
        presenter.onSearchClicked(keyword)
    }
    
    fun updateSearchKeyword(keyword: String) {
        _searchKeyword.value = keyword
    }
    
    fun clearToast() {
        _toastMessage.value = null
    }
    
    fun clearNavigationEvent() {
        _navigationEvent.value = null
    }
    
    fun onPriceFilterApplied(minPrice: Int, maxPrice: Int) {
        // 任务十六日志记录：价格筛选
        if (minPrice == 5000 && maxPrice == 8000) {
            TaskSixteenLogger.logPriceFilterApplied(context, minPrice, maxPrice)
        }
    }
    
    fun onCategoryFilterApplied(category: String) {
        // 任务十六日志记录：类别筛选
        if (category.contains("手机")) {
            TaskSixteenLogger.logCategoryFilterApplied(context, category)
        }
    }
    
    // SearchContract.View implementations
    override fun showSuggestions(suggestions: List<String>) {
        _suggestions.value = suggestions
    }
    
    override fun showToast(message: String) {
        _toastMessage.value = message
    }
    
    override fun navigateToSearchResult(keyword: String) {
        _navigationEvent.value = keyword
    }
    
    override fun showLoading(isLoading: Boolean) {
        _isLoading.value = isLoading
    }
}

class SearchViewModelFactory(
    private val repository: DataRepository,
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SearchViewModel(repository, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}