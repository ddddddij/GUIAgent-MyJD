package com.example.MyJD.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.MyJD.model.MeTabData
import com.example.MyJD.repository.DataRepository
import com.google.gson.JsonObject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MeViewModel(private val repository: DataRepository) : ViewModel() {
    
    private val _meTabData = MutableStateFlow<MeTabData?>(null)
    val meTabData: StateFlow<MeTabData?> = _meTabData.asStateFlow()
    
    private val _userProfile = MutableStateFlow<JsonObject?>(null)
    val userProfile: StateFlow<JsonObject?> = _userProfile.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val meData = repository.loadMeTabData()
                val profileData = repository.loadUserProfile()
                
                _meTabData.value = meData
                _userProfile.value = profileData
            } catch (e: Exception) {
                // Handle error silently for now
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getUserDisplayName(): String {
        return _userProfile.value?.get("nickname")?.asString 
            ?: _userProfile.value?.get("username")?.asString 
            ?: "Ddddddjy"
    }

    fun getUserMemberLevel(): String {
        return _userProfile.value?.get("memberLevel")?.asString ?: "铜牌会员"
    }

    fun getUserAvatar(): String {
        return "👤" // Using emoji as placeholder
    }

    fun hasStudentBenefit(): Boolean {
        // Mock student benefit status
        return true
    }

    fun getPlusStatus(): String {
        return "PLUS会员 >"
    }

    fun getRedPacketStatus(): String {
        return "您有红包未领取"
    }
}