package dev.jn.enois.presentation.components.avatar.fx

import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.animation.core.LinearEasing
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import dev.jn.enois.R


internal class GalaxiaImages private constructor(
    val wisps: ImageBitmap,
    val clouds: ImageBitmap,
    val glintsA: ImageBitmap,
    val glintsB: ImageBitmap,
    val core: ImageBitmap,
) {
    companion object {
        @Volatile
        private var cached: GalaxiaImages? = null

        fun get(context: Context): GalaxiaImages =
            cached ?: synchronized(this) { cached ?: load(context).also { cached = it } }

        private fun load(context: Context): GalaxiaImages {
            val options = BitmapFactory.Options().apply { inScaled = false }
            fun decode(id: Int) =
                BitmapFactory.decodeResource(context.resources, id, options).asImageBitmap()
            return GalaxiaImages(
                wisps = decode(R.drawable.galaxia_wisps),
                clouds = decode(R.drawable.galaxia_clouds),
                glintsA = decode(R.drawable.galaxia_glints_a),
                glintsB = decode(R.drawable.galaxia_glints_b),
                core = decode(R.drawable.galaxia_core),
            )
        }
    }
}

@Composable
internal fun rememberGalaxiaImages(): GalaxiaImages {
    val appContext = LocalContext.current.applicationContext
    return remember { GalaxiaImages.get(appContext) }
}

private fun DrawScope.layer(img: ImageBitmap, alpha: Float = 1f) {
    drawImage(
        img,
        dstOffset = IntOffset(-80, -80),
        dstSize = IntSize(160, 160),
        alpha = alpha,
        filterQuality = FilterQuality.Medium,
    )
}

private fun DrawScope.glints(img: ImageBitmap, p: Float) {
    val rot = kf(p, LinearEasing, 0f to 0f, 0.5f to -180f, 1f to -360f)
    val sc = kf(p, LinearEasing, 0f to 1f, 0.5f to 1.018f, 1f to 1f)
    val op = kf(
        p, LinearEasing,
        0f to 0.35f, 0.125f to 0.85f, 0.25f to 0.45f, 0.375f to 1f, 0.5f to 0.4f,
        0.625f to 0.85f, 0.75f to 0.45f, 0.875f to 1f, 1f to 0.35f,
    )
    withTransform({
        rotate(rot, Offset.Zero)
        scale(sc, sc, Offset.Zero)
    }) { layer(img, op) }
}


internal fun DrawScope.drawGalaxia(t: Float, img: GalaxiaImages, still: Boolean = false) {
    rotate(if (still) 0f else -360f * cycle(t, 41f), Offset.Zero) { layer(img.wisps, 0.85f) }
    rotate(if (still) 0f else -360f * cycle(t, 18f), Offset.Zero) { layer(img.clouds) }

    if (still) {
        layer(img.glintsA, 0.7f)
        layer(img.glintsB, 0.7f)
    } else {
        glints(img.glintsA, cycle(t, 13f, -1f))
        glints(img.glintsB, cycle(t, 27f, -5.4f))
    }
    layer(img.core)
}