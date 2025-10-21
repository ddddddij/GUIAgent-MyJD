package com.example.MyJD.presenter

interface SearchContract {
    
    interface View {
        fun showSuggestions(suggestions: List<String>)
        fun showToast(message: String)
        fun navigateToSearchResult(keyword: String)
        fun showLoading(isLoading: Boolean)
    }
    
    interface Presenter {
        fun attach(view: View)
        fun detach()
        fun loadSuggestions(query: String = "")
        fun onSuggestionClicked(suggestion: String)
        fun onSearchClicked(keyword: String)
        fun highlightKeyword(text: String, keyword: String): String
    }
}

data class SearchSuggestion(
    val text: String,
    val hasAIRecommendation: Boolean = false
)