package com.example.MyJD.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.MyJD.presenter.SearchContract
import com.example.MyJD.presenter.SearchPresenter

class SearchViewModel : ViewModel(), SearchContract.View {
    
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

class SearchViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SearchViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}