package dev.jn.enois.data.mapper

import dev.jn.enois.data.remote.dto.profile.AuthorNameStyleDto
import dev.jn.enois.domain.model.AuthorNameStyle
import dev.jn.enois.domain.model.FreeProfileNameTheme
import dev.jn.enois.domain.model.ProfileNameFont
import dev.jn.enois.domain.model.ProfileNameTheme
import dev.jn.enois.domain.model.SeasonalNameTheme

fun AuthorNameStyleDto.toDomain(): AuthorNameStyle =
    AuthorNameStyle(
        theme = theme?.toProfileNameTheme(),
        font = ProfileNameFont.fromName(font),
        color = color,
        nameplate = nameplate
    )

private fun String.toProfileNameTheme(): ProfileNameTheme? {
    return SeasonalNameTheme.fromId(this)
        ?: FreeProfileNameTheme.fromName(this)
}