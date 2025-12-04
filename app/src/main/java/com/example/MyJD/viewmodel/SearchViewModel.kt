package com.example.MyJD.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.MyJD.repository.DataRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SearchUiState(
    val suggestions: List<String> = emptyList(),
    val toastMessage: String? = null,
    val isLoading: Boolean = false,
    val navigationEvent: SearchNavigationEvent? = null,
    val searchText: String = ""
)

sealed class SearchNavigationEvent {
    data class ToSearchResult(val keyword: String) : SearchNavigationEvent()
    object None : SearchNavigationEvent()
}

class SearchViewModel(
    private val repository: DataRepository, // Although not directly used by presenter, keep for future expansion
    private val context: Context // Context might be needed for Toast, but generally avoid in ViewModel
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val defaultKeyword = "iPhone 15"
    private val searchSuggestions = listOf(
        "iphone15promax",
        "iphone15",
        "iphone15pro", 
        "iphone15plus",
        "iphone15 256g",
        "iphone15pro max 国行原装全新",
        "iphone15promax 分期",
        "iphone15pm",
        "iphone15plus512g",
        "iphone15pro max 分期付款 24期",
        "iphone15pro max 苹果官方旗舰国行",
        "iphone15pro max 全新京东自营",
        "iphone15pro 全新未激活未拆封国行",
        "iphone15 512g",
        "iphone15pro512g",
        "iphone15pro max 苹果官方旗舰全新",
        "iphone15pro max 全新国行未激活",
        "iphone15promax 国行百亿补贴",
        "iphone15 手机 pro max"
    )

    init {
        loadSuggestions()
    }

    fun loadSuggestions(query: String = "") {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            delay(300) // Simulate network delay
            // In a real app, filter suggestions based on query
            _uiState.value = _uiState.value.copy(
                suggestions = searchSuggestions,
                isLoading = false
            )
        }
    }

    fun onSuggestionClicked(suggestion: String) {
        // As per presenter, it always navigates with defaultKeyword for suggestion clicks
        _uiState.value = _uiState.value.copy(
            toastMessage = "正在搜索 $defaultKeyword…",
            navigationEvent = SearchNavigationEvent.ToSearchResult(defaultKeyword)
        )
    }

    fun onSearchClicked(keyword: String) {
        val searchKeyword = if (keyword.isBlank()) defaultKeyword else keyword
        _uiState.value = _uiState.value.copy(
            toastMessage = "正在搜索 $searchKeyword…",
            navigationEvent = SearchNavigationEvent.ToSearchResult(searchKeyword)
        )
    }

    fun onSearchTextChange(newText: String) {
        _uiState.value = _uiState.value.copy(searchText = newText)
    }

    fun clearToast() {
        _uiState.value = _uiState.value.copy(toastMessage = null)
    }

    fun clearNavigationEvent() {
        _uiState.value = _uiState.value.copy(navigationEvent = SearchNavigationEvent.None)
    }
    
    // This function remains in ViewModel as it provides data, but actual highlighting will be in UI
    fun highlightKeyword(text: String, keyword: String): String {
        return text // Highlighting logic will be in UI layer
    }
}
