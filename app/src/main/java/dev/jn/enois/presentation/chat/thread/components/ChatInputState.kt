package dev.jn.enois.presentation.chat.thread.components

import android.net.Uri
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusRequester
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlin.collections.getOrNull
import kotlin.uuid.Uuid


@Composable
fun rememberChatInputState(
    textContent: String = "",
    loading: Boolean = false,
):ChatInputState {
    return remember(textContent, loading) {
        ChatInputState().apply {
            this.textContent.setTextAndPlaceCursorAtEnd(textContent)
            this.loading = loading
        }
    }
}

class ChatInputState {
    val textContent = TextFieldState()
    var messageContent: List<UIMessagePart>
        get() = pendingAttachments.map { it.part }
        set(value) {
            pendingAttachments = reconcileAttachments(
                previousAttachments = pendingAttachments,
                nextContent = value,
            )
        }
    var pendingAttachments by mutableStateOf(listOf<ChatInputAttachment>())
        private set

    var loading by mutableStateOf(false)

    val focusRequester = FocusRequester()


    fun addImages(uris: List<Uri>) {
        addAttachments(uris.map { uri -> UIMessagePart.Image(uri.toString()) })
    }

    fun addVideos(uris: List<Uri>) {
        addAttachments(uris.map { uri -> UIMessagePart.Video(uri.toString()) })
    }

    fun addAudios(uris: List<Uri>) {
        addAttachments(uris.map { uri -> UIMessagePart.Audio(uri.toString()) })
    }

    fun addFiles(uris: List<UIMessagePart.Document>) {
        addAttachments(uris)
    }

    fun replaceAttachment(instanceId: String, part: UIMessagePart): UIMessagePart? {
        val index = pendingAttachments.indexOfFirst { it.id == instanceId }
        if (index < 0) return null
        val previousPart = pendingAttachments[index].part
        pendingAttachments = pendingAttachments.toMutableList().apply {
            set(index, ChatInputAttachment(id = instanceId, part = part))
        }
        return previousPart
    }

    fun removeAttachment(instanceId: String): UIMessagePart? {
        val index = pendingAttachments.indexOfFirst { it.id == instanceId }
        if (index < 0) return null
        val removedPart = pendingAttachments[index].part
        pendingAttachments = pendingAttachments.toMutableList().apply {
            removeAt(index)
        }
        return removedPart
    }

    private fun addAttachments(parts: List<UIMessagePart>) {
        if (parts.isEmpty()) return
        pendingAttachments = pendingAttachments + parts.map { part ->
            ChatInputAttachment(id = newAttachmentInstanceId(), part = part)
        }
    }


    private fun reconcileAttachments(
        previousAttachments: List<ChatInputAttachment>,
        nextContent: List<UIMessagePart>,
    ): List<ChatInputAttachment> {
        val usedPreviousIndexes = mutableSetOf<Int>()
        return nextContent.mapIndexed { index, part ->
            if (previousAttachments.getOrNull(index)?.part == part) {
                usedPreviousIndexes += index
                previousAttachments[index].copy(part = part)
            } else {
                val matchedIndex = previousAttachments.indices.firstOrNull { previousIndex ->
                    previousIndex !in usedPreviousIndexes &&
                            previousAttachments[previousIndex].part == part
                }
                if (matchedIndex != null) {
                    usedPreviousIndexes += matchedIndex
                    previousAttachments[matchedIndex].copy(part = part)
                } else {
                    ChatInputAttachment(id = newAttachmentInstanceId(), part = part)
                }
            }
        }
    }

    private fun newAttachmentInstanceId(): String {
        return Uuid.random().toString()
    }
}

data class ChatInputAttachment(
    val id: String,
    val part: UIMessagePart,
)

sealed class UIMessagePart {
    abstract val priority: Int
    abstract val metadata: JsonObject?

    @Serializable
    data class Text(
        val text: String,
        override var metadata: JsonObject? = null
    ) : UIMessagePart() {
        override val priority: Int = 0
    }

    data class Image(
        val url: String,
        override var metadata: JsonObject? = null
    ) : UIMessagePart() {
        override val priority: Int = 1
    }

    data class Video(
        val url: String,
        override var metadata: JsonObject? = null
    ) : UIMessagePart() {
        override val priority: Int = 1
    }

    data class Audio(
        val url: String,
        override var metadata: JsonObject? = null
    ) : UIMessagePart() {
        override val priority: Int = 1
    }

    data class Document(
        val url: String,
        val fileName: String,
        val mime: String = "text/*",
        override var metadata: JsonObject? = null
    ) : UIMessagePart() {
        override val priority: Int = 1
    }
}