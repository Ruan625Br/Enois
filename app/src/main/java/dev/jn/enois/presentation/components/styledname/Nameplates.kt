package dev.jn.enois.presentation.components.styledname

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color

@Stable
internal data class NameplateArt(
    val background: PlateBackground,
    val ink: Color,
    val icons: List<String>,
)

object Nameplates {
    internal val all: Map<String, NameplateArt> = mapOf(
        "placa-mata" to NameplateArt(
            HGradient(
                0f to Color(0xFF14532D),
                0.6f to Color(0xFF166534),
                1f to Color(0xFF15803D),
            ),
            Color(0xFFF0FDF4),
            listOf("folha", "carapuca"),
        ),

        "placa-teia" to NameplateArt(
            hGradient(
                listOf(
                    Color(0xFF2E1065),
                    Color(0xFF4C1D95),
                )
            ),
            Color(0xFFF5F3FF),
            listOf("teia", "morcego"),
        ),

        "placa-correnteza" to NameplateArt(
            HGradient(
                0f to Color(0xFF0E7490),
                0.6f to Color(0xFF0891B2),
                1f to Color(0xFF14B8A6),
            ),
            Color(0xFFECFEFF),
            listOf("onda", "boto"),
        ),

        "placa-fogo" to NameplateArt(
            HGradient(
                0f to Color(0xFF7C2D12),
                0.6f to Color(0xFFC2410C),
                1f to Color(0xFFF59E0B),
            ),
            Color(0xFFFFF7ED),
            listOf("chama"),
        ),

        "placa-embrulho" to NameplateArt(
            DiagonalStripes(
                Color(0xFFB91C1C),
                Color(0xFFDC2626),
                20f,
            ),
            Color.White,
            listOf("estrela", "presente"),
        ),

        "placa-pinheiro" to NameplateArt(
            hGradient(
                listOf(
                    Color(0xFF14532D),
                    Color(0xFF166534),
                )
            ),
            Color(0xFFFEFCE8),
            listOf("sino", "guirlanda"),
        ),

        "placa-ceu-de-fogos" to NameplateArt(
            hGradient(
                listOf(
                    Color(0xFF0F172A),
                    Color(0xFF1E3A8A),
                )
            ),
            Color(0xFFFEFCE8),
            listOf("fogos", "fogosVerde"),
        ),

        "placa-confete" to NameplateArt(
            hGradient(
                listOf(
                    Color(0xFF6D28D9),
                    Color(0xFFC026D3),
                )
            ),
            Color(0xFFFDF4FF),
            listOf("confete", "mascara"),
        ),

        "placa-frevo" to NameplateArt(
            hGradient(
                listOf(
                    Color(0xFFEF4444),
                    Color(0xFFFACC15),
                    Color(0xFF22C55E),
                    Color(0xFF3B82F6),
                    Color(0xFFA855F7),
                )
            ),
            Color(0xFF1E1B4B),
            listOf("sombrinha"),
        ),

        "placa-chita" to NameplateArt(
            Chita,
            Color(0xFFFFF7ED),
            listOf("florAzul", "florAmarela"),
        ),

        "placa-rosa" to NameplateArt(
            hGradient(
                listOf(
                    Color(0xFFFCE7F3),
                    Color(0xFFFBCFE8),
                )
            ),
            Color(0xFF9D174D),
            listOf("coracao", "carta"),
        ),

        "placa-cerrado" to NameplateArt(
            HGradient(
                0f to Color(0xFFA16207),
                0.5f to Color(0xFFCA8A04),
                1f to Color(0xFFFDE047),
            ),
            Color(0xFF1C1917),
            listOf("ipe", "arara"),
        ),

        "placa-terminal" to NameplateArt(
            hGradient(
                listOf(
                    Color(0xFF020617),
                    Color(0xFF0F172A),
                )
            ),
            Color(0xFF4ADE80),
            listOf("cursor", "cafe"),
        ),

        "placa-grade" to NameplateArt(
            Ruled(
                Color(0xFFF5F3FF),
                Color(0xFF6746F5).copy(alpha = 0.18f),
                horizontal = true,
                vertical = true,
            ),
            Color(0xFF3B1FA8),
            listOf("chip", "merge"),
        ),

        "placa-caderno" to NameplateArt(
            Ruled(
                Color.White,
                Color(0xFFBFDBFE),
                horizontal = true,
                vertical = false,
            ),
            Color(0xFF1E3A8A),
            listOf("pipa", "pirulito"),
        ),
    )

    internal operator fun get(id: String): NameplateArt? = all[id]
}