package dev.jn.enois.data.remote.dto.profile

import kotlinx.serialization.Serializable

@Serializable
data class UserProfileDto(
    val id: String,
    val username: String,
    val name: String,
    val headline: String?,
    val email: String? = null,
    val company: String?,
    val location: String?,
    val avatar: String?,
    val banner: String?,
    val badge: String?,
    val bio: String?,
    val bioHtml: String?,
    val bannerHtml: String?,
    val skills: List<String>,
    val goals: List<String>,
    val availability: String?,
    val portfolio: String?,
    val externalLinks: List<String>,
    val openToWork: Boolean,
    val showRanking: Boolean = false,
    val isMentor: Boolean,
    val connectionsCount: Int?,
    val followersCount: Int?,
    val reputation: Int?,
    val nickname: String? = null,
    val birthDate: String? = null,
    val socialName: String? = null,
    val pronouns: String?,
    val aka: String?,
    val avatarFrame: String? = null,
    val resumeUrl: String? = null,
    val resumeName: String? = null,
    val badges: List<String>?
)
