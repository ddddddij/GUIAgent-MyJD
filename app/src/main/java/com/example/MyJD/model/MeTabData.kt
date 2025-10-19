package com.example.MyJD.model

data class MemberBenefit(
    val id: String,
    val name: String,
    val value: String,
    val description: String,
    val iconEmoji: String
)

data class PromoBannerItem(
    val id: String,
    val title: String,
    val subtitle: String?,
    val backgroundColor: String,
    val textColor: String,
    val iconEmoji: String?
)

data class MeTabOrderStatus(
    val id: String,
    val name: String,
    val iconEmoji: String,
    val count: Int = 0
)

data class AssetItem(
    val id: String,
    val name: String,
    val value: String,
    val unit: String,
    val description: String?,
    val iconEmoji: String
)

data class ServiceItem(
    val id: String,
    val name: String,
    val description: String,
    val iconEmoji: String,
    val badgeText: String?
)

data class InteractionItem(
    val id: String,
    val name: String,
    val description: String,
    val iconEmoji: String,
    val badgeText: String?
)

data class QuickAction(
    val id: String,
    val name: String,
    val iconEmoji: String,
    val route: String
)

data class UserStats(
    val footprint: Int,
    val favorites: Int,
    val following: Int,
    val grass: Int
)

data class MeTabData(
    val memberBenefits: List<MemberBenefit>,
    val promoBanners: List<PromoBannerItem>,
    val orderStatuses: List<MeTabOrderStatus>,
    val assetItems: List<AssetItem>,
    val serviceItems: List<ServiceItem>,
    val interactionItems: List<InteractionItem>,
    val quickActions: List<QuickAction>,
    val userStats: UserStats
)