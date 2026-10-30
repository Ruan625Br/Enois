package dev.jn.enois.domain.model

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.vector.ImageVector
import dev.jn.enois.ui.icons.AppIcons

@Stable
sealed interface AccountMenuAction {

    val title: String
    val icon: ImageVector
    val destructive: Boolean get() = false

    data object Profile : AccountMenuAction {
        override val title = "Ver perfil"
        override val icon = AppIcons.Person
    }

    data object CorporateProfile : AccountMenuAction {
        override val title = "Perfil profissional"
        override val icon = AppIcons.Person
    }

    data object Wallet : AccountMenuAction {
        override val title = "Carteira"
        override val icon = AppIcons.ElectricBolt
    }

    data object Marketplace : AccountMenuAction {
        override val title = "Marketplace"
        override val icon = AppIcons.Storefront
    }

    data object ChangeEmail : AccountMenuAction {
        override val title = "Alterar e-mail"
        override val icon = AppIcons.Mail
    }

    data object Support : AccountMenuAction {
        override val title = "Ajuda e suporte"
        override val icon = AppIcons.SupportAgent
    }

    data object Security : AccountMenuAction {
        override val title = "Segurança e verificação"
        override val icon = AppIcons.VerifiedUser
    }

    data object BlockedUsers : AccountMenuAction {
        override val title = "Usuários bloqueados"
        override val icon = AppIcons.RemoveModerator
    }

    data object Reports : AccountMenuAction {
        override val title = "Minhas denúncias"
        override val icon = AppIcons.Flag
    }

    data object Sanctions : AccountMenuAction {
        override val title = "Penalidades"
        override val icon = AppIcons.GppMaybe
    }

    data object ChangePassword : AccountMenuAction {
        override val title = "Trocar senha"
        override val icon = AppIcons.Key
    }

    data object LinkedAccounts : AccountMenuAction {
        override val title = "Contas vinculadas"
        override val icon = AppIcons.Link
    }

    data object HealthyUsage : AccountMenuAction {
        override val title = "Uso saudável"
        override val icon = AppIcons.HeartSmile
    }

    data object Guardian : AccountMenuAction {
        override val title = "Supervisão parental"
        override val icon = AppIcons.VerifiedUser
    }

    data object Moderator : AccountMenuAction {
        override val title = "Painel de moderador"
        override val icon = AppIcons.Hardware
    }

    data object SupportProject : AccountMenuAction {
        override val title = "Apoie o Ènois"
        override val icon = AppIcons.Favorite
    }

    data object DeleteAccount : AccountMenuAction {
        override val title = "Excluir conta"
        override val icon = AppIcons.Delete
        override val destructive = true
    }

    data object Logout : AccountMenuAction {
        override val title = "Sair"
        override val icon = AppIcons.Logout
        override val destructive = true
    }


    companion object {
        val all = listOf(
            Profile,
            CorporateProfile,
            Wallet,
            Marketplace,
            ChangeEmail,
            Support,
            Security,
            BlockedUsers,
            Reports,
            Sanctions,
            ChangePassword,
            LinkedAccounts,
            HealthyUsage,
            Guardian,
            Moderator,
            SupportProject,
            DeleteAccount,
            Logout
        )
    }
}