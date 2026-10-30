package dev.jn.enois.presentation.profile.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.jn.enois.domain.model.AccountMenuProfile
import dev.jn.enois.presentation.components.avatar.UserAvatar
import dev.jn.enois.presentation.components.styledname.StyledName

@Composable
fun AccountMenuHeader(
    profile: AccountMenuProfile,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        UserAvatar(
            url = profile.avatar.orEmpty(),
            userName = profile.name,
            frame = profile.avatarFrame,
            textStyle = MaterialTheme.typography.titleMedium,
            shape = CircleShape
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 48.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Column {
                StyledName(
                    name = profile.name,
                    style = null
                )

                Text(
                    text = profile.email.orEmpty(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}