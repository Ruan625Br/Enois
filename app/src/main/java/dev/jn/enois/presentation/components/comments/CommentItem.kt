package dev.jn.enois.presentation.components.comments

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.jn.enois.core.util.formatTime
import dev.jn.enois.domain.model.Comment
import dev.jn.enois.domain.util.FlatComment
import dev.jn.enois.presentation.components.avatar.UserAvatar
import dev.jn.enois.presentation.components.badge.UserBadges
import dev.jn.enois.presentation.components.richtext.CollapsibleRichText

@Composable
fun CommentItem(
    node: FlatComment,
    onReply: (Comment) -> Unit,
    onToggle: (String) -> Unit,
    lineColors: (Int) -> Color,
    modifier: Modifier = Modifier,
    bubble: Boolean = false,
) {
    val comment = node.comment
    val visualDepth = minOf(node.depth, MaxVisualDepth)
    val ownLine = node.hasChildren && !node.isCollapsed && node.depth < MaxVisualDepth
    val time = remember(comment.createdAt) { formatTime(comment.createdAt) }
    val scheme = MaterialTheme.colorScheme

    Box(modifier) {
        Column(Modifier.threadLines(node.depth, node.continues, ownLine, lineColors)) {
            Column(Modifier.padding(start = ThreadIndent * visualDepth, top = ItemTopPadding)) {

                Row {
                    UserAvatar(
                        url = comment.authorAvatar.orEmpty(),
                        userName = comment.authorName,
                        frame = comment.authorAvatarFrame,
                        size = CommentAvatarSize
                    )
                    Spacer(Modifier.width(AvatarGap))

                    Surface(
                        shape = if (bubble) MaterialTheme.shapes.large else MaterialTheme.shapes.small,
                        color = if (bubble) scheme.surfaceContainerHigh else Color.Transparent,
                        modifier = Modifier.weight(1f),
                    ) {
                        Column(
                            modifier = if (bubble) Modifier
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                            else Modifier,
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = comment.authorName,
                                    style = MaterialTheme.typography.titleSmall,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f, fill = false),
                                )
                                Text(
                                    "· $time",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = scheme.onSurfaceVariant
                                )
                                if (comment.pinned) Text(
                                    "📌",
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                            if (comment.authorBadges.isNotEmpty()) UserBadges(badges = comment.authorBadges)

                            CollapsibleRichText(
                                content = comment.content,
                                key = comment.id,
                                onMention = {},
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(ActionRowHeight),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        Modifier
                            .width(CommentAvatarSize)
                            .fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        if (node.hasChildren) {
                            Box(
                                Modifier
                                    .fillMaxSize()
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null,
                                        onClickLabel = if (node.isCollapsed) "Expandir respostas" else "Recolher respostas",
                                        role = Role.Button,
                                    ) { onToggle(comment.id) },
                                contentAlignment = Alignment.Center,
                            ) {
                                CollapseGlyph(
                                    collapsed = node.isCollapsed,
                                    color = scheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                    Spacer(Modifier.width(AvatarGap))

                    Text(
                        text = "Responder",
                        style = MaterialTheme.typography.labelMedium,
                        color = scheme.onSurfaceVariant,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onReply(comment) }
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                    )
                    if (node.isCollapsed) {
                        Text(
                            text = if (node.descendantCount == 1) "· 1 resposta" else "· ${node.descendantCount} respostas",
                            style = MaterialTheme.typography.labelMedium,
                            color = scheme.primary,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onToggle(comment.id) }
                                .padding(horizontal = 4.dp, vertical = 4.dp),
                        )
                    }
                }
            }
            Spacer(Modifier.height(ItemBottomPadding))
        }

        ThreadToggleAreas(
            depth = node.depth,
            ancestorIds = node.ancestorIds,
            continues = node.continues,
            ownId = comment.id.takeIf { ownLine },
            onToggle = onToggle,
        )
    }
}