package dev.jn.enois.presentation.components.comments

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import kotlin.io.path.Path

internal val CommentAvatarSize = 36.dp        // use o mesmo valor no avatar do CommentItem
internal val CommentItemVerticalPadding = 6.dp
internal val ReplyIndent = 46.dp              // avatar + espaçamento (36 + 10)

internal enum class ThreadLine { None, RootWithReplies, Reply, LastReply }

internal fun Modifier.threadLine(kind: ThreadLine, color: Color): Modifier =
    if (kind == ThreadLine.None) this else drawBehind {
        val stroke = 2.dp.toPx()
        val x = (CommentAvatarSize / 2).toPx()                      // centro do avatar da raiz
        val elbowY = (CommentItemVerticalPadding + CommentAvatarSize / 2).toPx()  // centro do avatar da resposta
        val radius = 12.dp.toPx()
        val elbowEndX = ReplyIndent.toPx() - 4.dp.toPx()

        when (kind) {
            ThreadLine.RootWithReplies -> {
                // sai de baixo do avatar da raiz e desce até o fim do item
                val startY = (CommentItemVerticalPadding + CommentAvatarSize + 4.dp).toPx()
                drawLine(color, Offset(x, startY), Offset(x, size.height), strokeWidth = stroke)
            }

            ThreadLine.Reply, ThreadLine.LastReply -> {
                // a última resposta para no cotovelo; as outras continuam descendo
                val lineEnd = if (kind == ThreadLine.LastReply) elbowY - radius else size.height
                drawLine(color, Offset(x, 0f), Offset(x, lineEnd), strokeWidth = stroke)

                val elbow = Path().apply {
                    moveTo(x, elbowY - radius)
                    quadraticTo(x, elbowY, x + radius, elbowY)
                    lineTo(elbowEndX, elbowY)
                }
                drawPath(elbow, color, style = Stroke(width = stroke, cap = StrokeCap.Round))
            }

            ThreadLine.None -> Unit
        }
    }