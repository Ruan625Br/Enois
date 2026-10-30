package dev.jn.enois.domain.model

data class AccountMenuProfile(
    val name: String,
    val email: String?,
    val avatar: String?,
    val avatarFrame: AvatarFrameType?
)

fun UserProfile.asAccountMenuProfile() = AccountMenuProfile(
    name = name,
    email = email,
    avatar = avatar,
    avatarFrame = AvatarFrameType.fromFxName(avatarFrame)
)