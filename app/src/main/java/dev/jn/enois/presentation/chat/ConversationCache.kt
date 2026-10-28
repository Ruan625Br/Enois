package dev.jn.enois.presentation.chat

import dev.jn.enois.domain.model.Conversation

class ConversationCache {
    private val map = mutableMapOf<String, Conversation>()
    fun put(c: Conversation) { map[c.id] = c }
    fun get(id: String): Conversation? = map[id]
}