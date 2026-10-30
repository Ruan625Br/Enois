package dev.jn.enois.presentation.profile.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.jn.enois.domain.model.AccountMenuAction
import dev.jn.enois.domain.model.AccountMenuProfile

@Composable
fun AccountMenuContent(
    profile: AccountMenuProfile,
    actions: List<AccountMenuAction>,
    onAction: (AccountMenuAction) -> Unit,
    modifier: Modifier = Modifier
) {

    val regularActions = actions.filterNot {
        it.destructive
    }

    val destructiveActions = actions.filter {
        it.destructive
    }
    val border = BorderStroke(
        1.dp,
        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
    )

    Surface(
        modifier = modifier.width(240.dp),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceContainer,
        border = border,
        shadowElevation = 8.dp,
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(
                vertical = 12.dp
            )
        ) {
            AccountMenuHeader(
                modifier = Modifier
                    .padding(horizontal = 12.dp),
                profile = profile
            )


            Spacer(Modifier.height(12.dp))

            HorizontalDivider()

            Spacer(Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier
                    .heightIn(max = 450.dp)
            ) {
                items(
                    items = regularActions,
                    key = { it::class.simpleName.orEmpty() }
                ) { action ->

                    AccountMenuItem(
                        action = action,
                        onClick = { onAction(action) }
                    )
                }
            }

            HorizontalDivider()

            destructiveActions.forEach { action ->
                AccountMenuItem(
                    action = action,
                    onClick = { onAction(action) }
                )
            }

        }
    }
}

@Composable
private fun AccountMenuItem(
    action: AccountMenuAction,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.onSurface,
) {

    val contentColor = when {
        action.destructive -> MaterialTheme.colorScheme.error
        else -> tint
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp)
            .clickable(
                onClick = onClick
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            modifier = Modifier
                .padding(start = 12.dp)
                .size(18.dp),
            imageVector = action.icon,
            contentDescription = null,
            tint = contentColor
        )

        Text(
            text = action.title,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}