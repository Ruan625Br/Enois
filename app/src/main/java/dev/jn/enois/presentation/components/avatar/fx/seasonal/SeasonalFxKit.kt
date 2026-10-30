package dev.jn.enois.presentation.components.avatar.fx.seasonal


import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.DrawTransform
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.util.lerp
import dev.jn.enois.presentation.components.avatar.fx.CssEaseInOut
import dev.jn.enois.presentation.components.avatar.fx.CssEaseOut
import dev.jn.enois.presentation.components.avatar.fx.SeededRandom
import dev.jn.enois.presentation.components.avatar.fx.cycle
import dev.jn.enois.presentation.components.avatar.fx.kf
import dev.jn.enois.presentation.components.avatar.fx.polar
import dev.jn.enois.presentation.components.avatar.fx.r2
import dev.jn.enois.presentation.components.avatar.fx.rgb
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

/*
 * Porte do frame-kit.tsx + dos keyframes `cos-*` do styles.css.
 *
 * Modelo: cada "elemento JSX" vira um FxNode (lambda que desenha). Os geradores (seededRandom)
 * rodam UMA vez, quando o nó é construído, na mesma ordem do TS; o desenho por frame só lê `t`.
 * A ordem dos nós = ordem de pintura do JSX.
 */

internal typealias FxNode = DrawScope.(SeasonalCtx) -> Unit

/** `var(--primary)` do CSS: resolvido em desenho para o primário do tema do app. */
internal val PrimaryColor: Color = Color.Unspecified

/**
 * Estado compartilhado de um desenho (um por AvatarFrameFx, reaproveitado a cada frame).
 * `alpha` é a opacidade de grupo acumulada (os `opacity` de <g> e das animações cos-*).
 */
internal class SeasonalCtx(val textMeasurer: TextMeasurer) {
    var t = 0f
    var still = false
    var primary: Color = Color.Unspecified
    var alpha = 1f
    val textCache = HashMap<String, TextLayoutResult>()

    // saída de solve() (transform + opacidade da animação corrente)
    @JvmField var tx = 0f
    @JvmField var ty = 0f
    @JvmField var rot = 0f
    @JvmField var sx = 1f
    @JvmField var sy = 1f
    @JvmField var op = 1f

    fun col(c: Color): Color = if (c == Color.Unspecified) primary else c
}

// ---------------------------------------------------------------------------------------------
// Animações `cos-anim cos-<nome>`
// ---------------------------------------------------------------------------------------------

internal val CssEaseIn = CubicBezierEasing(0.42f, 0f, 1f, 1f)

internal enum class CosAnim(val key: String, val duration: Float) {
    SPIN("spin", 8f),
    SPIN_SLOW("spin-slow", 22f),
    SPIN_FAST("spin-fast", 4.5f),
    SPIN_REV("spin-rev", 14f),
    FLICK("flick", 0.7f),
    RIPPLE("ripple", 3.6f),
    BLINK("blink", 2.4f),
    FLAP("flap", 0.5f),
    SWAY("sway", 2.6f),
    MELT("melt", 4f),
    DRIFT("drift", 5f),
    TWINKLE("twinkle", 1.8f),
    RISE("rise", 4f),
    POP("pop", 2.4f),
    BOB("bob", 2.2f),
    ROCK("rock", 3f),
    WOBBLE("wobble", 0.6f),
    SHINE("shine", 2.4f),
    WING("wing", 1.8f);

    companion object {
        fun of(key: String): CosAnim? = entries.firstOrNull { it.key == key }
    }
}

/** `alternate`: 0→1→0 em 2×duração. O easing é aplicado depois (todos os alternate são ease-in-out). */
private fun alt(t: Float, dur: Float, delay: Float): Float {
    val x = cycle(t, dur * 2f, delay)
    return if (x < 0.5f) x * 2f else 2f - 2f * x
}

internal fun SeasonalCtx.solve(a: CosAnim, delay: Float, dur: Float?) {
    val d = dur ?: a.duration
    tx = 0f; ty = 0f; rot = 0f; sx = 1f; sy = 1f; op = 1f
    when (a) {
        CosAnim.SPIN, CosAnim.SPIN_SLOW, CosAnim.SPIN_FAST -> rot = 360f * cycle(t, d, delay)
        CosAnim.SPIN_REV -> rot = -360f * cycle(t, d, delay)

        // cos-flick .7s ease-in-out alternate: scale(1,.85) -> scale(.92,1.12)
        CosAnim.FLICK -> {
            val k = CssEaseInOut.transform(alt(t, d, delay))
            sx = lerp(1f, 0.92f, k); sy = lerp(0.85f, 1.12f, k)
        }

        // cos-ripple 3.6s ease-out: scale 1 -> 1.5, opacity .9 -> 0
        CosAnim.RIPPLE -> {
            val k = CssEaseOut.transform(cycle(t, d, delay))
            sx = lerp(1f, 1.5f, k); sy = sx; op = lerp(0.9f, 0f, k)
        }

        // cos-blink 2.4s ease-in-out: .35 -> 1 -> .35
        CosAnim.BLINK -> op = kf(cycle(t, d, delay), CssEaseInOut, 0f to 0.35f, 0.5f to 1f, 1f to 0.35f)

        // cos-flap .5s alternate: scaleY 1 -> .35
        CosAnim.FLAP -> sy = lerp(1f, 0.35f, CssEaseInOut.transform(alt(t, d, delay)))

        CosAnim.SWAY -> rot = lerp(-8f, 8f, CssEaseInOut.transform(alt(t, d, delay)))

        // cos-melt 4s linear
        CosAnim.MELT -> {
            val p = cycle(t, d, delay)
            ty = 78f * p
            sx = lerp(1f, 0.6f, p); sy = sx
            op = kf(p, LinearEasing, 0f to 0f, 0.15f to 1f, 0.7f to 0.9f, 1f to 0f)
        }

        // cos-drift 5s ease-in: translate(-18,80) rotate(220) (transform 0%->100%), opacity por trechos
        CosAnim.DRIFT -> {
            val p = cycle(t, d, delay)
            val m = CssEaseIn.transform(p)
            tx = -18f * m; ty = 80f * m; rot = 220f * m
            op = kf(p, CssEaseIn, 0f to 0f, 0.15f to 1f, 1f to 0f)
        }

        CosAnim.TWINKLE -> {
            val k = CssEaseInOut.transform(alt(t, d, delay))
            rot = 90f * k
            sx = lerp(1f, 0.6f, k); sy = sx
        }

        // cos-rise 4s ease-out: translateY(0 -> -60) scale(.6 -> 1.1); opacity 0, 1 (20%), 0
        CosAnim.RISE -> {
            val p = cycle(t, d, delay)
            val m = CssEaseOut.transform(p)
            ty = -60f * m
            sx = lerp(0.6f, 1.1f, m); sy = sx
            op = kf(p, CssEaseOut, 0f to 0f, 0.2f to 1f, 1f to 0f)
        }

        // cos-pop 2.4s ease-out: scale .1 -> 1 (60%) -> 1.15; opacity 0, 1 (15%), .9 (60%), 0
        CosAnim.POP -> {
            val p = cycle(t, d, delay)
            sx = kf(p, CssEaseOut, 0f to 0.1f, 0.6f to 1f, 1f to 1.15f); sy = sx
            op = kf(p, CssEaseOut, 0f to 0f, 0.15f to 1f, 0.6f to 0.9f, 1f to 0f)
        }

        CosAnim.BOB -> ty = lerp(-3f, 3f, CssEaseInOut.transform(alt(t, d, delay)))
        CosAnim.ROCK -> rot = lerp(-35f, 35f, CssEaseInOut.transform(alt(t, d, delay)))
        CosAnim.WOBBLE -> rot = lerp(-10f, 10f, CssEaseInOut.transform(alt(t, d, delay)))

        // cos-shine 2.4s ease-in-out: scale .85 -> 1.12 (50%) -> .85
        CosAnim.SHINE -> {
            sx = kf(cycle(t, d, delay), CssEaseInOut, 0f to 0.85f, 0.5f to 1.12f, 1f to 0.85f); sy = sx
        }

        CosAnim.WING -> rot = lerp(-4f, 12f, CssEaseInOut.transform(alt(t, d, delay)))
    }
}

/**
 * `<g class="cos-anim cos-x" style="animation-delay/duration">`: transform + opacidade da animação em
 * volta de `body`. Em `still` (reduced-motion / lite) o CSS faz `animation: none`: estado-base.
 * transform-origin 0 0 = origem local (o translate/rotate/scale dos pais já está aplicado).
 */
internal inline fun DrawScope.withAnim(
    c: SeasonalCtx,
    a: CosAnim?,
    delay: Float,
    duration: Float?,
    body: DrawScope.() -> Unit,
) {
    if (a == null || c.still) {
        body()
        return
    }
    c.solve(a, delay, duration)
    val tx = c.tx
    val ty = c.ty
    val rot = c.rot
    val sx = c.sx
    val sy = c.sy
    val prev = c.alpha
    c.alpha = prev * c.op
    withTransform({
        translate(tx, ty)
        rotate(rot, Offset.Zero)
        scale(sx, sy, Offset.Zero)
    }) { body() }
    c.alpha = prev
}

// ---------------------------------------------------------------------------------------------
// Utilidades
// ---------------------------------------------------------------------------------------------

/** `round()` do TS (meio para cima, 2 casas). */
internal fun rf(n: Float): Float = r2(n.toDouble()).toFloat()

internal fun dash(vararg v: Float): PathEffect = PathEffect.dashPathEffect(v, 0f)

private fun dashOf(s: String): PathEffect? {
    if (s.isEmpty()) return null
    var v = s.trim().split(Regex("\\s+")).map { it.toFloat() }.toFloatArray()
    if (v.size % 2 == 1) v += v
    return PathEffect.dashPathEffect(v, 0f)
}

/** `arc(r, a1, a2)` do kit: arco horário de a1 a a2 (0° no topo). Compose mede 0° à direita: -90. */
internal fun arcPath(r: Float, a1: Float, a2: Float): Path = Path().apply {
    arcTo(Rect(-r, -r, r, r), a1 - 90f, a2 - a1, true)
}

/** `flame(w, h)` do kit (a base fica na origem, a ponta em -h). */
internal fun flamePath(w: Float, h: Float): Path = Path().apply {
    moveTo(-w, 0f)
    cubicTo(-w, rf(-h * 0.45f), rf(-w * 0.15f), rf(-h * 0.55f), 0f, -h)
    cubicTo(rf(w * 0.15f), rf(-h * 0.55f), w, rf(-h * 0.45f), w, 0f)
    cubicTo(rf(w * 0.55f), rf(w * 0.9f), rf(-w * 0.55f), rf(w * 0.9f), -w, 0f)
    close()
}

internal fun polarWavePath(r: Float, amp: Float, freq: Float, phase: Float): Path = Path().apply {
    for (i in 0..120) {
        val t = i * 3
        val rr = r + amp.toDouble() * sin((t * freq.toDouble() + phase) * PI / 180.0)
        val o = polar(rr, t.toDouble())
        if (i == 0) moveTo(o.x, o.y) else lineTo(o.x, o.y)
    }
    close()
}

// ---------------------------------------------------------------------------------------------
// Primitivas SVG (<g>, <circle>, <ellipse>, <rect>, <path>)
// ---------------------------------------------------------------------------------------------

internal fun group(vararg nodes: FxNode): FxNode = { c -> for (n in nodes) n(this, c) }

internal fun group(nodes: List<FxNode>): FxNode = { c -> for (n in nodes) n(this, c) }

/** `<g transform="...">`: o bloco monta o transform na mesma ordem do atributo SVG. */
internal fun xform(transform: DrawTransform.() -> Unit, vararg nodes: FxNode): FxNode = { c ->
    withTransform(transform) { for (n in nodes) n(this, c) }
}

internal fun at(x: Float, y: Float, rot: Float? = null, vararg nodes: FxNode): FxNode = { c ->
    withTransform({
        translate(x, y)
        if (rot != null) rotate(rot, Offset.Zero)
    }) { for (n in nodes) n(this, c) }
}

/** `<g {...anim(cls, delay, duration)}>`. */
internal fun animated(cls: String, delay: Float = 0f, duration: Float? = null, vararg nodes: FxNode): FxNode {
    val a = CosAnim.of(cls)
    return { c -> withAnim(c, a, delay, duration) { for (n in nodes) n(this, c) } }
}

internal fun circle(
    cx: Float, cy: Float, r: Float,
    fill: Color? = null, stroke: Color? = null, sw: Float = 1f, opacity: Float = 1f,
    anim: String = "", delay: Float = 0f, duration: Float? = null,
): FxNode {
    val a = if (anim.isEmpty()) null else CosAnim.of(anim)
    val center = Offset(cx, cy)
    return { c ->
        withAnim(c, a, delay, duration) {
            val al = c.alpha * opacity
            if (fill != null) drawCircle(c.col(fill), r, center, al)
            if (stroke != null) drawCircle(c.col(stroke), r, center, al, Stroke(sw))
        }
    }
}

internal fun ellipse(
    cx: Float, cy: Float, rx: Float, ry: Float, fill: Color, opacity: Float = 1f,
): FxNode = { c ->
    drawOval(c.col(fill), Offset(cx - rx, cy - ry), Size(rx * 2f, ry * 2f), c.alpha * opacity)
}

internal fun rect(
    x: Float, y: Float, w: Float, h: Float,
    fill: Color? = null, stroke: Color? = null, sw: Float = 1f, rx: Float = 0f,
    anim: String = "", delay: Float = 0f, duration: Float? = null,
): FxNode {
    val a = if (anim.isEmpty()) null else CosAnim.of(anim)
    return { c ->
        withAnim(c, a, delay, duration) {
            val tl = Offset(x, y)
            val sz = Size(w, h)
            val cr = CornerRadius(rx, rx)
            if (fill != null) {
                if (rx > 0f) drawRoundRect(c.col(fill), tl, sz, cr, alpha = c.alpha)
                else drawRect(c.col(fill), tl, sz, c.alpha)
            }
            if (stroke != null) {
                if (rx > 0f) drawRoundRect(c.col(stroke), tl, sz, cr, Stroke(sw), c.alpha)
                else drawRect(c.col(stroke), tl, sz, c.alpha, Stroke(sw))
            }
        }
    }
}

internal fun pathNode(
    path: Path,
    fill: Color? = null, stroke: Color? = null, sw: Float = 1f,
    cap: StrokeCap = StrokeCap.Butt, join: StrokeJoin = StrokeJoin.Miter,
    opacity: Float = 1f, dash: PathEffect? = null,
    anim: String = "", delay: Float = 0f, duration: Float? = null,
): FxNode {
    val a = if (anim.isEmpty()) null else CosAnim.of(anim)
    return { c ->
        withAnim(c, a, delay, duration) {
            val al = c.alpha * opacity
            if (fill != null) drawPath(path, c.col(fill), al)
            if (stroke != null) drawPath(path, c.col(stroke), al, Stroke(sw, cap = cap, join = join, pathEffect = dash))
        }
    }
}

// ---------------------------------------------------------------------------------------------
// frame-kit.tsx
// ---------------------------------------------------------------------------------------------

/** Ícone (COSMETIC_ICONS) no ponto, girado e escalado; `cls` anima em torno do centro do ícone. */
internal fun place(
    key: String,
    x: Float, y: Float,
    s: Float = 1f, rot: Float = 0f,
    cls: String = "", delay: Float = 0f, duration: Float? = null,
): FxNode {
    val icon = cosmeticIcon(key)
    val a = if (cls.isEmpty()) null else CosAnim.of(cls)
    val px = rf(x)
    val py = rf(y)
    val pr = rf(rot)
    return { c ->
        if (icon != null) {
            withTransform({
                translate(px, py)
                rotate(pr, Offset.Zero)
                scale(s, s, Offset.Zero)
            }) {
                // a animação fica DENTRO do scale: translateY(78px) do melt vale 78 * s
                withAnim(c, a, delay, duration) { icon(this, c) }
            }
        }
    }
}

/** Ícone sobre o círculo de raio `r` no ângulo `a`. */
internal fun perch(
    key: String, a: Float, r: Float, s: Float = 1f, rot: Float = 0f,
    cls: String = "", delay: Float = 0f,
): FxNode {
    val o = polar(r.toDouble(), a.toDouble())
    return place(key, o.x, o.y, s, rot, cls, delay)
}

/** Ícone usado como chapéu: a borda de baixo apoia no topo do avatar. */
internal fun hat(key: String, s: Float, bottom: Float = 8.5f): FxNode =
    place(key, 0f, -38f - bottom * s, s)

internal fun orbit(
    keys: List<String>, n: Int, r: Float, s: Float,
    spin: String = "spin",
    arc: Pair<Float, Float>? = null,
    rotate: Boolean = true,
    rotOff: Float = 0f,
    each: String = "",
    stagger: Float = 0.2f,
    jitter: Float = 0f,
    seed: Int = 1,
): FxNode {
    val rnd = SeededRandom(seed)
    val items = List(n) { i ->
        val a: Double = if (arc != null) {
            arc.first + (if (n == 1) 0.0 else i * (arc.second - arc.first).toDouble() / (n - 1))
        } else {
            i * 360.0 / n
        }
        val jit = if (jitter != 0f) (rnd.next() - 0.5) * jitter else 0.0
        val o = polar(r + jit, a)
        place(keys[i % keys.size], o.x, o.y, s, if (rotate) (a + rotOff).toFloat() else 0f, each, -i * stagger)
    }
    return if (spin.isNotEmpty()) animated(spin, 0f, null, group(items)) else group(items)
}

/** `<circle fill="none" stroke=c stroke-linecap="round" stroke-dasharray=dash>` */
internal fun ring(c: Color, w: Float = 1.5f, dash: String = "", r: Float = 54f): FxNode {
    val effect = dashOf(dash)
    return { ctx ->
        drawCircle(
            ctx.col(c), r, Offset.Zero, ctx.alpha,
            Stroke(w, cap = StrokeCap.Round, pathEffect = effect),
        )
    }
}

internal fun wave(
    c: Color, r: Float = 60f, amp: Float = 5f, freq: Float = 5f, phase: Float = 0f, w: Float = 2.2f,
): FxNode = pathNode(polarWavePath(r, amp, freq, phase), stroke = c, sw = w, cap = StrokeCap.Round)

internal fun ripples(c: Color, r: Float = 52f): FxNode =
    group(listOf(0f, -1.2f, -2.4f).map { d -> circle(0f, 0f, r, stroke = c, sw = 1.4f, anim = "ripple", delay = d) })

/** Ícones caindo da borda de cima. */
internal fun fallers(
    keys: List<String>, n: Int, seed: Int, s: Float,
    cls: String = "melt", spread: Float = 140f,
): FxNode {
    val rnd = SeededRandom(seed)
    val items = List(n) { i ->
        val x = (i.toDouble() / max(1, n - 1) - 0.5) * spread + (rnd.next() - 0.5) * 8
        val y = -74 + rnd.next() * 6
        val delay = -rnd.next() * 4
        val dur = 3.4 + rnd.next() * 1.8
        place(keys[i % keys.size], x.toFloat(), y.toFloat(), s, 0f, cls, delay.toFloat(), dur.toFloat())
    }
    return group(items)
}

/** Ícones subindo de uma caixa (fumaça, bolhas). */
internal fun risers(
    keys: List<String>, n: Int, seed: Int, s: Float,
    x: Pair<Float, Float> = -60f to 60f,
    y: Pair<Float, Float> = 44f to 62f,
): FxNode {
    val rnd = SeededRandom(seed)
    val items = List(n) { i ->
        val px = x.first + rnd.next() * (x.second - x.first)
        val py = y.first + rnd.next() * (y.second - y.first)
        val delay = -rnd.next() * 4
        val dur = 3.2 + rnd.next() * 1.6
        place(keys[i % keys.size], px.toFloat(), py.toFloat(), s, 0f, "rise", delay.toFloat(), dur.toFloat())
    }
    return group(items)
}

/** Ícones surgindo e sumindo em volta (fogos, estrelinhas). */
internal fun bursts(
    keys: List<String>, n: Int, seed: Int, s: Float,
    range: Pair<Float, Float> = 0f to 360f,
    rr: Pair<Float, Float> = 58f to 70f,
): FxNode {
    val rnd = SeededRandom(seed)
    val items = List(n) { i ->
        val radius = rr.first + rnd.next() * (rr.second - rr.first)
        val angle = range.first + rnd.next() * (range.second - range.first)
        val o = polar(radius, angle)
        val scale = s * (0.8 + rnd.next() * 0.5)
        val delay = -rnd.next() * 2.4
        val dur = 2 + rnd.next()
        place(keys[i % keys.size], o.x, o.y, scale.toFloat(), 0f, "pop", delay.toFloat(), dur.toFloat())
    }
    return group(items)
}

/** Chamas tremulando sobre o círculo, mais altas no topo. */
internal fun flames(
    n: Int, r: Float, range: Pair<Float, Float>, colors: List<Color>, seed: Int = 11,
): FxNode {
    val rnd = SeededRandom(seed)
    val items = List(n) { i ->
        val a = range.first + i * (range.second - range.first).toDouble() / (n - 1)
        val o = polar(r.toDouble(), a)
        val top = (1 + cos(a * PI / 180.0)) / 2
        val delay = -rnd.next()
        val duration = 0.5 + rnd.next() * 0.4
        val h = 8 + 10 * top + rnd.next() * 5
        at(
            o.x, o.y, rf(a.toFloat()),
            pathNode(
                flamePath(3.4f, h.toFloat()), fill = colors[i % colors.size],
                anim = "flick", delay = delay.toFloat(), duration = duration.toFloat(),
            ),
        )
    }
    return group(items)
}

/** Texto girando em círculo (cada letra tombada sobre o raio; textLength em `spacing`). */
internal fun textRing(
    text: String, color: Color,
    size: Float = 7.2f,
    family: FontFamily = FontFamily.Monospace,
    r: Float = 61f,
    spin: String = "spin-slow",
): FxNode {
    val a = CosAnim.of(spin)
    val length = (2 * PI * r - 12).toFloat()
    return { c ->
        withAnim(c, a, 0f, null) { drawRingText(c, text, color, size, family, r, length) }
    }
}

private fun DrawScope.drawRingText(
    c: SeasonalCtx, text: String, color: Color, fontPx: Float, family: FontFamily, r: Float, length: Float,
) {
    fun layoutOf(ch: Char): TextLayoutResult =
        c.textCache.getOrPut("${family.hashCode()}|$fontPx|$ch") {
            // 1 unidade do SVG = 1 px do canvas já escalado: toSp() desfaz densidade e fontScale
            val style = TextStyle(fontFamily = family, fontSize = fontPx.toSp(), fontWeight = FontWeight.Bold)
            c.textMeasurer.measure(ch.toString(), style)
        }

    fun advance(l: TextLayoutResult) = l.getLineRight(0) - l.getLineLeft(0)

    var natural = 0f
    for (ch in text) natural += advance(layoutOf(ch))
    val extra = if (text.length > 1) (length - natural) / (text.length - 1) else 0f

    val col = c.col(color)
    var x = 0f
    for (ch in text) {
        val l = layoutOf(ch)
        val w = advance(l)
        val deg = (x + w / 2f) / r * 180f / PI.toFloat()
        val p = polar(r.toDouble(), deg.toDouble())
        withTransform({
            translate(p.x, p.y)
            rotate(deg, Offset.Zero)
        }) {
            drawText(l, color = col, topLeft = Offset(-w / 2f, -l.firstBaseline), alpha = c.alpha)
        }
        x += w + extra
    }
}

/** Fio de luzes de Natal piscando fora de compasso. */
internal fun lights(n: Int, colors: List<Color>, r: Float = 56f): FxNode {
    val bulbs = List(n) { i ->
        val a = i * 360.0 / n
        val col = colors[i % colors.size]
        val o = polar(r.toDouble(), a)
        at(
            o.x, o.y, a.toFloat(),
            rect(-1.6f, -1.2f, 3.2f, 2.6f, fill = rgb(0x14532D), rx = 0.6f),
            animated(
                "blink", -(i % 4) * 0.6f, null,
                ellipse(0f, -6f, 5f, 6.5f, col, opacity = 0.28f),
                ellipse(0f, -5.4f, 2.7f, 4f, col),
            ),
        )
    }
    return group(
        pathNode(polarWavePath(r, 1.4f, 16f, 0f), stroke = rgb(0x166534), sw = 1.4f),
        group(bulbs),
    )
}

internal fun pixelRing(colors: List<Color>): FxNode {
    val pixels = List(40) { i ->
        val a = i * 9.0
        val o = polar(56.0, a)
        at(
            o.x, o.y, a.toFloat(),
            rect(-2.6f, -2.6f, 5.2f, 5.2f, fill = colors[i % colors.size], anim = "blink", delay = -(i % 5) * 0.4f),
        )
    }
    return group(pixels)
}

internal fun arcsRing(
    parts: List<Triple<Color, Float, Float>>, r: Float = 55f, w: Float = 3.4f,
): FxNode = group(parts.map { (c, a, b) ->
    pathNode(arcPath(r, a, b), stroke = c, sw = w, cap = StrokeCap.Round)
})

/** Duas asas espelhadas atrás do avatar, batendo devagar. */
internal fun wings(art: FxNode, x: Float = 36f, y: Float = -14f): FxNode = group(
    at(-x, y, null, animated("wing", 0f, null, art)),
    xform({ translate(x, y); scale(-1f, 1f, Offset.Zero) }, animated("wing", 0f, null, art)),
)

/** O sol de verão no ângulo `a` do círculo de raio `r`, girando devagar. */
internal fun sun(r: Float, a: Float): FxNode {
    val o = polar(r.toDouble(), a.toDouble())
    val icon = place("sol", 0f, 0f)
    return at(o.x, o.y, null, animated("spin-slow", 0f, null, icon))
}