package dev.jn.enois.data.mapper

import dev.jn.enois.core.util.parseIsoMillis
import dev.jn.enois.data.remote.dto.message.ConversationDto
import dev.jn.enois.data.remote.dto.message.CursorPageDto
import dev.jn.enois.data.remote.dto.message.MessageDto
import dev.jn.enois.domain.model.Conversation
import dev.jn.enois.domain.model.ConversationKind
import dev.jn.enois.domain.model.CursorPage
import dev.jn.enois.domain.model.Message

fun ConversationDto.toDomain() = Conversation(
    id = id,
    kind = runCatching { ConversationKind.valueOf(kind) }.getOrDefault(ConversationKind.DIRECT),
    title = title,
    participantIds = participantIds,
    lastMessage = lastMessage,
    time = time,
    unread = unread,
    otherLastReadAt = otherLastReadAt,
    otherLastReadMillis = parseIsoMillis(otherLastReadAt),
    lastMessageE2e = lastMessageE2e,
    lastMessageIv = lastMessageIv,
)

fun MessageDto.toDomain() = Message(
    id = id,
    conversationId = conversationId,
    senderId = from,
    text = text,
    mediaIds = mediaIds,
    mediaSpoiler = mediaSpoiler,
    time = time,
    timeMillis = parseIsoMillis(time) ?: 0L,
    reactions = reactions,
    myReaction = myReaction,
    isE2e = e2e,
    iv = iv,
)

@JvmName("conversationListToDomain")
fun List<ConversationDto>.toDomain(): List<Conversation> =
    map { it.toDomain() }

@JvmName("messageListToDomain")
fun List<MessageDto>.toDomain(): List<Message> =
    map { it.toDomain() }

fun <T, R> CursorPageDto<T>.toDomain(
    mapper: (T) -> R
) = CursorPage(
    items = items.map(mapper),
    nextCursor = nextCursor,
)