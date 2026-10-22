package dev.jn.enois.data.mapper

import dev.jn.enois.data.remote.dto.media.MediaDto
import dev.jn.enois.domain.model.Media

fun MediaDto.toDomain() = Media(
    id = id,
    fileName = fileName,
    mimeType = mimeType,
    size = size,
    purpose = purpose,
    status = status,
    uploadUrl = uploadUrl,
    cdnUrl = cdnUrl,
    createdAt = createdAt
)