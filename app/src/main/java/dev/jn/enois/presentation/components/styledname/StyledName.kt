package dev.jn.enois.presentation.components.styledname

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import dev.jn.enois.domain.model.AuthorNameStyle

@Composable
fun StyledName(
    name: String,
    style: AuthorNameStyle?,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.titleSmall,
    color: Color = MaterialTheme.colorScheme.onSurface,
) {
    if (style == null) {
        Text(name, modifier, color = color, style = textStyle, maxLines = 1, overflow = TextOverflow.Ellipsis)
        return
    }

    val art = style.nameplate?.let { Nameplates[it] }
    if (art == null) {
        PaintedName(name, style, textStyle, fallbackColor = color, modifier = modifier)
    } else {
        Nameplate(art, modifier) {
            PaintedName(name, style, textStyle, fallbackColor = color, plateInk = art.ink)
        }
    }
}