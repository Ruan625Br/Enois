package dev.jn.enois.presentation.components.avatar.fx.seasonal


import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import coil3.toBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/*
 * Ícones sazonais (COSMETIC_ICONS): os SVGs exportados em assets/icons/<chave>.svg (viewBox
 * -12 -12 24 24, centrados na origem, ±10 unidades). Cada um é rasterizado UMA vez por processo
 * (Coil + SvgDecoder, o mesmo que o Nameplate já usa) e desenhado com drawImage dentro dos
 * transforms do place(). Enquanto o bitmap não chega, o ícone simplesmente não aparece.
 */

/** Anota as chaves de ícone usadas por cada moldura enquanto o nó dela é construído. */
internal object IconRecorder {
    var current: MutableSet<String>? = null
}

internal object SeasonalIcons {
    /** 128 px para 24 unidades (~5,3 px/unidade): aguenta ícones escalados até ~2,6x. */
    private const val SIZE = 128

    // mutableStateMapOf: o desenho lê daqui, então um ícone que termina de carregar redesenha
    // a moldura mesmo com o relógio pausado (scroll).
    private val bitmaps = mutableStateMapOf<String, ImageBitmap>()
    private val loading = HashSet<String>()

    fun get(key: String): ImageBitmap? = bitmaps[key]

    suspend fun load(context: PlatformContext, keys: Set<String>) {
        val loader = SingletonImageLoader.get(context)
        for (key in keys) {
            if (key in bitmaps || !loading.add(key)) continue
            try {
                val request = ImageRequest.Builder(context)
                    .data("file:///android_asset/icons/$key.svg")
                    .size(SIZE, SIZE)
                    .build()
                val result = loader.execute(request)
                if (result is SuccessResult) {
                    val bmp = withContext(Dispatchers.Default) {
                        result.image.toBitmap(SIZE, SIZE).asImageBitmap()
                    }
                    bitmaps[key] = bmp
                }
            } finally {
                loading.remove(key) // se falhou, uma próxima chamada tenta de novo
            }
        }
    }
}

/** `icon(key)` do kit. */
internal fun cosmeticIcon(key: String): FxNode? {
    IconRecorder.current?.add(key)
    return { c ->
        SeasonalIcons.get(key)?.let { img ->
            drawImage(
                img,
                dstOffset = IntOffset(-12, -12),
                dstSize = IntSize(24, 24),
                alpha = c.alpha,
                filterQuality = FilterQuality.High,
            )
        }
    }
}