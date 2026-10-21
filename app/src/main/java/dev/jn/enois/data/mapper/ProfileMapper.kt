package dev.jn.enois.data.mapper

import dev.jn.enois.data.remote.dto.profile.UserProfileDto
import dev.jn.enois.domain.model.UserProfile

fun UserProfileDto.toDomain() = UserProfile(
    id = id,
    username = username,
    name = name,
    headline = headline,
    email = email,
    company = company,
    location = location,
    avatar = avatar,
    banner = banner,
    badge = badge,
    bio = bio,
    bioHtml = bioHtml,
    bannerHtml = bannerHtml,
    skills = skills,
    goals = goals,
    availability = availability,
    portfolio = portfolio,
    externalLinks = externalLinks,
    openToWork = openToWork,
    showRanking = showRanking,
    isMentor = isMentor,
    connectionsCount = connectionsCount,
    followersCount = followersCount,
    reputation = reputation,
    nickname = nickname,
    birthDate = birthDate,
    socialName = socialName,
    pronouns = pronouns,
    aka = aka,
    avatarFrame = avatarFrame,
    resumeUrl = resumeUrl,
    resumeName = resumeName,
    badges = badges
)