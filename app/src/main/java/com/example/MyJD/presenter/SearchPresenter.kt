package com.example.MyJD.presenter

class SearchPresenter : SearchContract.Presenter {
    
    private var view: SearchContract.View? = null
    
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
    
    override fun attach(view: SearchContract.View) {
        this.view = view
    }
    
    override fun detach() {
        this.view = null
    }
    
    override fun loadSuggestions(query: String) {
        view?.showLoading(true)
        
        // 模拟加载延迟
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            view?.showSuggestions(searchSuggestions)
            view?.showLoading(false)
        }, 300)
    }
    
    override fun onSuggestionClicked(suggestion: String) {
        view?.showToast("正在搜索 $defaultKeyword…")
        // 延迟一下再跳转，让用户看到Toast
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            view?.navigateToSearchResult(defaultKeyword)
        }, 500)
    }
    
    override fun onSearchClicked(keyword: String) {
        val searchKeyword = if (keyword.isBlank()) defaultKeyword else keyword
        view?.showToast("正在搜索 $searchKeyword…")
        // 延迟一下再跳转，让用户看到Toast
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            view?.navigateToSearchResult(searchKeyword)
        }, 500)
    }
    
    override fun highlightKeyword(text: String, keyword: String): String {
        // 这个方法将在UI层使用，用于高亮关键字
        // 返回原始文本，高亮处理在UI层进行
        return text
    }
}