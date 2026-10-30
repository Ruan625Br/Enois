package dev.jn.enois.presentation.components.avatar.fx.seasonal

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontFamily
import dev.jn.enois.presentation.components.avatar.fx.SeededRandom
import dev.jn.enois.presentation.components.avatar.fx.polar
import dev.jn.enois.presentation.components.avatar.fx.r2
import dev.jn.enois.presentation.components.avatar.fx.rgb
import kotlin.math.sin


private fun k(hex: Int, a: Float = 1f): Color =
    rgb(hex, a)

private val FRAME_ICON_KEYS = HashMap<String, Set<String>>()

/** Constrói o nó sob demanda e guarda quais ícones ele usa (para pré-carregar os SVGs). */
private fun art(id: String, build: () -> FxNode): Pair<String, Lazy<FxNode>> = id to lazy {
    val keys = LinkedHashSet<String>()
    val prev = IconRecorder.current
    IconRecorder.current = keys
    try {
        build()
    } finally {
        IconRecorder.current = prev
        FRAME_ICON_KEYS[id] = keys
    }
}

/** Chaves de ícone (assets/icons/<chave>.svg) que a moldura `id` desenha. */
internal fun seasonalIconKeys(id: String): Set<String> {
    SEASONAL_FRAME_ART[id]?.value
    return FRAME_ICON_KEYS[id].orEmpty()
}

/** Id da web (`moldura-xxx`) -> desenho. */
private val SEASONAL_FRAME_ART: Map<String, Lazy<FxNode>> by lazy {
    (saci() + halloween() + folclore() + natal() + reveillon() + carnaval() + junina() +
            namorados() + flor() + dev() + criancas()).toMap()
}

internal fun DrawScope.drawSeasonal(id: String, c: SeasonalCtx) {
    SEASONAL_FRAME_ART[id]?.value?.invoke(this, c)
}

// ===== saci.tsx =============================================================================

private class Whirl(val r: Float, val a: Float, val b: Float, val color: Color, val w: Float)

/** Arcos de vento tracejados girando: o redemoinho do Saci. */
private fun whirl(vararg arcs: Whirl): FxNode = animated(
    "spin", 0f, null,
    group(arcs.map {
        pathNode(
            arcPath(it.r, it.a, it.b), stroke = it.color, sw = it.w, cap = StrokeCap.Round,
            dash = dash(it.w * 6f, it.w * 2.5f),
        )
    }),
)

private fun saci() = listOf(
    art("moldura-redemoinho") {
        group(
            whirl(
                Whirl(56f, 0f, 210f, k(0x16A34A), 2.4f),
                Whirl(62f, 120f, 320f, k(0x65A30D), 1.7f),
                Whirl(68f, 240f, 400f, k(0x15803D), 1.2f),
            ),
            orbit(
                listOf("folha", "folhaSeca"),
                4,
                63f,
                0.55f,
                spin = "spin-fast",
                jitter = 10f,
                seed = 4
            ),
            perch("carapuca", 36f, 56f, 1.25f, 34f),
        )
    },
    art("moldura-carapuca") {
        group(ring(k(0xDC2626), 1.4f, "2 5"), hat("carapuca", 2.3f))
    },
    art("moldura-cachimbo") {
        group(
            perch("cachimbo", 128f, 58f, 1.7f),
            risers(listOf("fumaca"), 5, 9, 0.8f, 36f to 56f, 24f to 40f),
        )
    },
    art("moldura-folhas") {
        orbit(
            listOf("folha", "folhaSeca", "folhaEscura"),
            11,
            62f,
            0.65f,
            spin = "spin-fast",
            jitter = 10f,
            seed = 6
        )
    },
    art("moldura-mata") {
        orbit(
            listOf("folhaEscura", "folha"),
            22,
            56f,
            0.7f,
            spin = "",
            each = "sway",
            stagger = 0.15f
        )
    },
    art("moldura-saci-completo") {
        group(
            whirl(
                Whirl(60f, 0f, 210f, k(0x16A34A), 2.2f),
                Whirl(67f, 150f, 350f, k(0x65A30D), 1.5f),
            ),
            hat("carapuca", 2.1f),
            perch("cachimbo", 128f, 58f, 1.5f),
            risers(listOf("fumaca"), 4, 3, 0.7f, 36f to 52f, 24f to 38f),
        )
    },
)

// ===== halloween.tsx ========================================================================

private val BAT_WING: FxNode = group(
    pathNode(
        Path().apply {
            moveTo(0f, -2f)
            cubicTo(-14f, -24f, -30f, -42f, -44f, -46f)
            cubicTo(-40f, -34f, -40f, -26f, -42f, -18f)
            cubicTo(-34f, -22f, -28f, -20f, -24f, -14f)
            cubicTo(-22f, -20f, -16f, -20f, -12f, -14f)
            cubicTo(-12f, -10f, -6f, -6f, 0f, 2f)
            close()
        },
        fill = k(0x1E1B4B), stroke = k(0xA78BFA), sw = 1.2f, join = StrokeJoin.Round,
    ),
    pathNode(
        Path().apply {
            moveTo(-4f, -4f)
            cubicTo(-14f, -20f, -26f, -34f, -38f, -40f)
        },
        stroke = k(0xA78BFA), sw = 0.7f, opacity = 0.7f,
    ),
)

private val HORN: FxNode = pathNode(
    Path().apply {
        moveTo(0f, 0f)
        cubicTo(-3f, -8f, 0f, -16f, 7f, -21f)
        cubicTo(5f, -13f, 8f, -7f, 9f, 0f)
        close()
    },
    fill = k(0xDC2626), stroke = k(0x7F1D1D), sw = 1f,
)

private fun halloween() = listOf(
    art("moldura-asas-morcego") { wings(BAT_WING) },
    art("moldura-chifres") {
        group(
            xform({ translate(-24f, -42f); rotate(-15f, Offset.Zero) }, HORN),
            xform(
                { translate(24f, -42f); scale(-1f, 1f, Offset.Zero); rotate(-15f, Offset.Zero) },
                HORN
            ),
        )
    },
    art("moldura-abobora") {
        group(
            ring(k(0x7C3AED), 1.6f, "1 4"),
            orbit(listOf("morcego"), 3, 67f, 1f, each = "flap", stagger = 0.17f),
            perch("abobora", 140f, 56f, 1.4f, -12f),
        )
    },
    art("moldura-teia") {
        group(
            perch("teia", 45f, 62f, 2.3f),
            animated(
                "bob", 0f, null,
                pathNode(
                    Path().apply { moveTo(66f, -36f); lineTo(66f, 2f) },
                    stroke = k(0x94A3B8), sw = 0.7f,
                ),
                place("aranha", 66f, 6f, 0.9f),
            ),
        )
    },
    art("moldura-fantasmas") {
        orbit(
            listOf("fantasma"),
            3,
            64f,
            1f,
            spin = "spin-slow",
            rotate = false,
            each = "bob",
            stagger = 0.6f
        )
    },
    art("moldura-caldeirao") {
        group(
            perch("caldeirao", 220f, 60f, 1.6f),
            risers(listOf("bolhaVerde"), 6, 21, 0.9f, -62f to -30f, 30f to 44f),
        )
    },
    art("moldura-lua") {
        group(
            perch("luaCheia", 40f, 56f, 2.6f),
            orbit(listOf("morcego"), 2, 68f, 0.9f, spin = "spin-slow", each = "flap"),
        )
    },
    art("moldura-velas") {
        orbit(
            listOf("vela"), 7, 60f, 0.95f,
            spin = "", arc = 115f to 245f, rotate = false, each = "blink", stagger = 0.4f,
        )
    },
)

// ===== folclore.tsx =========================================================================

/** Os pés virados para trás do Curupira andando em volta, com um fogo em cima. */
private fun curupira(): FxNode {
    val feet = List(10) { i ->
        perch("pe", i * 36f, 62f + if (i % 2 == 1) 3.5f else -3.5f, 1f, i * 36f - 90f)
    }
    val fire = listOf(-28f, -14f, 0f, 14f, 28f).mapIndexed { i, a ->
        val o = polar(
            46.0,
            a.toDouble()
        )
        at(
            o.x, o.y, a,
            pathNode(
                flamePath(5f, 14f + if (i == 2) 6f else 0f),
                fill = if (i % 2 == 1) k(0xF97316) else k(0xDC2626),
                anim = "flick", delay = -i * 0.13f,
            ),
        )
    }
    return group(animated("spin-slow", 0f, null, group(feet)), group(fire))
}

/** A cobra de fogo: cada bolinha é o corpo um pouco mais atrás. */
private fun boitata(): FxNode {
    val body = List(26) { kk ->
        val o = polar(
            60.0 + sin(kk * 0.8) * 2.6,
            -kk * 8.5
        )
        circle(
            o.x, o.y,
            r2(6.4 - kk * 0.2)
                .toFloat(),
            fill = Color.hsl(42f - kk * 1.4f, 0.96f, (58f - kk * 0.8f) / 100f),
            opacity = r2(1.0 - kk / 30.0)
                .toFloat(),
        )
    }.reversed()
    val h = polar(60.0, 4.0)
    return animated(
        "spin", 0f, null,
        group(body),
        at(
            h.x, h.y, 94f,
            ellipse(
                0f,
                0f,
                6f,
                8f,
                k(0xF97316)
            ),
            circle(-2.6f, -3f, 1.7f, fill = k(0xFFFBEB)),
            circle(2.6f, -3f, 1.7f, fill = k(0xFFFBEB)),
        ),
    )
}

private fun iara(): FxNode {
    val scales = ArrayList<FxNode>()
    for (row in 0..1) {
        var a = 112 + row * 7
        while (a <= 248) {
            scales.add(perch("escama", a.toFloat(), 55f + row * 6f, 1f, a.toFloat()))
            a += 14
        }
    }
    val p = polar(57.0, 150.0)
    return group(
        ripples(k(0x06B6D4)),
        group(scales),
        circle(p.x, p.y, 3.4f, fill = k(0xF0FDFA), stroke = k(0x99F6E4)),
    )
}

private fun folclore() = listOf(
    art("moldura-curupira") { curupira() },
    art("moldura-boitata") { boitata() },
    art("moldura-iara") { iara() },
    art("moldura-mula") {
        group(
            flames(28, 50f, 0f to 345f, listOf(k(0xF97316), k(0xEF4444), k(0xFACC15)), 31),
            orbit(listOf("ferradura"), 3, 68f, 0.8f, spin = "spin-fast"),
        )
    },
    art("moldura-boto") {
        group(
            ripples(k(0xF9A8D4)),
            hat("chapeu", 2.2f, 6.6f),
            animated("rock", 0f, null, perch("boto", 90f, 66f, 1.3f, 90f)),
        )
    },
    art("moldura-vitoria") {
        orbit(
            listOf("vitoria"),
            8,
            61f,
            0.85f,
            spin = "",
            rotate = true,
            each = "bob",
            stagger = 0.3f
        )
    },
    art("moldura-lobisomem") {
        group(
            ring(k(0x64748B), 1.2f, "3 4"),
            perch("luaCheia", -40f, 58f, 2.1f),
            perch("garras", 130f, 60f, 1.9f, 130f),
        )
    },
)

// ===== natal.tsx ============================================================================

private fun natal() = listOf(
    art("moldura-pisca") {
        lights(16, listOf(k(0xEF4444), k(0xFACC15), k(0x22C55E), k(0x3B82F6)))
    },
    art("moldura-neve") {
        group(
            ring(k(0x7DD3FC), 1.5f, "2 5", 53.5f),
            fallers(listOf("floco"), 10, 35, 0.45f),
            sun(60f, 42f),
        )
    },
    art("moldura-guirlanda") {
        group(
            orbit(listOf("folhaEscura", "folha"), 24, 56f, 0.6f, spin = ""),
            group(List(8) { i ->
                val o = polar(
                    58.0,
                    22.0 + i * 45
                )
                circle(o.x, o.y, 2.4f, fill = k(0xDC2626))
            }),
            perch("laco", 180f, 58f, 1.5f),
        )
    },
    art("moldura-presentes") {
        group(
            perch("presente", 205f, 60f, 1.3f),
            perch("presenteAzul", 182f, 64f, 1.1f),
            perch("presente", 160f, 62f, 1f),
            perch("presenteAzul", 140f, 58f, 0.9f),
        )
    },
    art("moldura-estrela") {
        group(
            place("estrela", 0f, -64f, 1.5f, 0f, "shine"),
            orbit(
                listOf("brilhoDourado"),
                6,
                62f,
                0.45f,
                spin = "spin-slow",
                each = "twinkle",
                arc = 40f to 320f
            ),
        )
    },
    art("moldura-noel") {
        group(
            hat("gorro", 2.3f, 9f),
            perch("coqueiro", 232f, 62f, 1.9f),
            perch("sol", 128f, 62f, 1.3f),
        )
    },
)

// ===== reveillon.tsx ========================================================================

private fun reveillon() = listOf(
    art("moldura-fogos") { bursts(listOf("fogos", "fogosVerde"), 7, 41, 1.1f) },
    art("moldura-sete-ondas") {
        animated(
            "spin-slow", 0f, null,
            wave(k(0x38BDF8), r = 56f, amp = 3f, freq = 7f),
            wave(k(0x7DD3FC), r = 62f, amp = 3f, freq = 7f, phase = 90f),
            wave(k(0x0EA5E9), r = 68f, amp = 3f, freq = 7f, phase = 180f, w = 1.6f),
        )
    },
    art("moldura-meia-noite") {
        group(
            place("relogio", 0f, -62f, 1.5f),
            orbit(
                listOf("brilhoDourado"),
                8,
                62f,
                0.45f,
                spin = "spin-slow",
                each = "twinkle",
                arc = 35f to 325f
            ),
        )
    },
    art("moldura-uvas") { orbit(listOf("uva"), 12, 61f, 0.55f, spin = "spin-slow") },
)

// ===== carnaval.tsx =========================================================================

private val CONFETTI = listOf(0xEC4899, 0xF59E0B, 0x22C55E, 0x3B82F6, 0xA855F7).map { k(it) }

/** Três serpentinas girando e confete solto cintilando. */
private fun serpentina(): FxNode {
    val rnd = SeededRandom(7)
    val confetti = List(16) { i ->
        val p = polar(
            54 + rnd.next() * 20,
            rnd.next() * 360
        )
        val turn = r2(rnd.next() * 180)
            .toFloat()
        val delay = -rnd.next() * 2
        at(
            p.x, p.y, turn,
            rect(
                -1.4f,
                -3f,
                2.8f,
                6f,
                fill = CONFETTI[i % 5],
                rx = 0.8f,
                anim = "twinkle",
                delay = delay.toFloat()
            ),
        )
    }
    return group(
        animated(
            "spin-slow", 0f, null,
            group(listOf(0xEC4899, 0xF59E0B, 0x22C55E).mapIndexed { i, c ->
                wave(
                    k(c),
                    phase = i * 120f
                )
            }),
        ),
        group(confetti),
    )
}

/** O avatar vira o couro do pandeiro; os platinelas giram em volta. */
private fun pandeiro(): FxNode = group(
    ring(k(0xB45309), 5f, "", 54f),
    animated(
        "spin-slow", 0f, null,
        group(List(8) { i ->
            val o = polar(
                54.0,
                i * 45.0
            )
            at(
                o.x, o.y, i * 45f,
                rect(-4f, -2f, 8f, 4f, fill = k(0x78350F), rx = 1f),
                circle(-2.2f, 0f, 2.1f, fill = k(0xFCD34D), stroke = k(0x92400E), sw = 0.6f),
                circle(2.2f, 0f, 2.1f, fill = k(0xFCD34D), stroke = k(0x92400E), sw = 0.6f),
            )
        }),
    ),
)

private fun carnaval() = listOf(
    art("moldura-serpentina") { serpentina() },
    art("moldura-confete") {
        group(
            ring(k(0xEC4899), 1.4f, "1 5"),
            fallers(listOf("confete"), 12, 51, 0.6f, "drift"),
        )
    },
    art("moldura-plumas") {
        orbit(
            listOf("pluma", "plumaAzul", "plumaAmarela"), 11, 60f, 1.35f,
            spin = "", arc = -85f to 85f, rotOff = 45f, each = "sway", stagger = 0.25f,
        )
    },
    art("moldura-frevo") {
        orbit(listOf("sombrinha"), 4, 64f, 0.95f, spin = "spin", each = "sway", stagger = 0.5f)
    },
    art("moldura-purpurina") {
        orbit(
            listOf("brilho", "brilhoDourado"), 14, 62f, 0.45f,
            spin = "spin-slow", each = "twinkle", jitter = 14f, seed = 9, stagger = 0.3f,
        )
    },
    art("moldura-pandeiro") { pandeiro() },
)

// ===== junina.tsx ===========================================================================

private val FLAG_COLORS = listOf(0xEF4444, 0xF59E0B, 0x10B981, 0x3B82F6, 0xA855F7, 0xEC4899).map {
    k(
        it
    )
}

private val FLAG_PATH = Path().apply {
    moveTo(-5f, 0f); lineTo(5f, 0f); lineTo(5f, -11f); lineTo(0f, -7.5f); lineTo(-5f, -11f); close()
}

/** Um varal de bandeirinhas sobre a cabeça e a fogueira embaixo. */
private fun bandeirinhas(): FxNode {
    val flags = List(13) { i ->
        val a = -78f + 13f * i
        val o = polar(
            60.0,
            a.toDouble()
        )
        at(
            o.x, o.y, a,
            animated("sway", -i * 0.35f, null, pathNode(FLAG_PATH, fill = FLAG_COLORS[i % 6])),
        )
    }
    return group(
        pathNode(arcPath(60f, -84f, 84f), stroke = k(0xA16207), sw = 1f),
        group(flags),
        place("fogueira", 0f, 64f, 1.3f),
    )
}

private fun junina() = listOf(
    art("moldura-bandeirinhas") { bandeirinhas() },
    art("moldura-fogueira") {
        group(
            flames(14, 50f, 110f to 250f, listOf(k(0xF97316), k(0xFACC15), k(0xEF4444)), 17),
            place("fogueira", 0f, 66f, 1.2f),
        )
    },
    art("moldura-chapeu-palha") {
        group(ring(k(0xEAB308), 1.4f, "4 3"), hat("chapeuPalha", 2.6f, 6f))
    },
    art("moldura-milharal") {
        orbit(listOf("milho"), 12, 60f, 0.75f, spin = "", each = "sway", stagger = 0.2f)
    },
    art("moldura-chita") {
        orbit(listOf("florRosa", "florAzul", "florAmarela"), 12, 58f, 0.65f, spin = "spin-slow")
    },
    art("moldura-estrelinha") { bursts(listOf("estrelinha"), 7, 71, 1f, 90f to 270f) },
)

// ===== namorados.tsx ========================================================================

private fun namorados() = listOf(
    art("moldura-coracoes") {
        orbit(
            listOf("coracao"),
            6,
            62f,
            0.7f,
            spin = "spin",
            each = "shine",
            stagger = 0.4f,
            rotate = false
        )
    },
    art("moldura-cartas") {
        orbit(
            listOf("carta"),
            3,
            65f,
            0.8f,
            spin = "spin-slow",
            each = "bob",
            stagger = 0.7f,
            rotate = false
        )
    },
    art("moldura-laco") {
        group(
            wave(k(0xF472B6), r = 56f, amp = 2f, freq = 9f, w = 3f),
            perch("laco", 0f, 58f, 1.4f),
        )
    },
)

// ===== flor.tsx =============================================================================

private val ARARA_WING: FxNode = group(
    listOf(
        Triple(0xDC2626, -104f, 21f),
        Triple(0xFACC15, -86f, 24f),
        Triple(0x2563EB, -68f, 26f),
        Triple(0xDC2626, -50f, 24f),
        Triple(0x2563EB, -32f, 19f),
    ).map { (c, a, l) ->
        xform(
            { rotate(a, Offset.Zero) },
            pathNode(
                Path().apply { addOval(Rect(-5f, -2f * l, 5f, 0f)) },
                fill = k(c), stroke = k(0x000000, 0.18f), sw = 0.8f,
            ),
        )
    },
)

private val IPE_TWIG = Path().apply { moveTo(-14f, 6f); quadraticTo(0f, -2f, 14f, 6f) }
private val IPE_TWIG_SMALL = Path().apply { moveTo(-12f, 6f); quadraticTo(0f, -2f, 12f, 6f) }

/** Um cacho de ipê num galho, no ângulo `a` do círculo. */
private fun ipeCluster(a: Float): FxNode {
    val o =
        polar(58.0, a.toDouble())
    return at(
        o.x, o.y, a,
        pathNode(IPE_TWIG, stroke = k(0x78350F), sw = 1.6f),
        place("ipe", -9f, 0f, 0.62f, 10f),
        place("ipe", 0f, -5f, 0.78f),
        place("ipe", 9f, 0f, 0.58f, -12f),
    )
}

/** Florada: recompensa de evento que ganha um cacho de ipê por semana de setembro (stage 0..6). */
private fun florada(stage: Int = 6): FxNode =
    group(listOf(-40f, 140f, 40f, 220f, -100f, 100f).take(stage).map { a ->
        val o = polar(
            58.0,
            a.toDouble()
        )
        at(
            o.x, o.y, a,
            pathNode(IPE_TWIG_SMALL, stroke = k(0x78350F), sw = 1.6f),
            place("ipe", -7f, 0f, 0.55f),
            place("ipe", 0f, -4f, 0.7f),
            place("ipe", 7f, 0f, 0.5f),
        )
    })

private fun flor() = listOf(
    art("moldura-asas-arara") { wings(ARARA_WING, 38f, -8f) },
    art("moldura-coroa-flores") {
        group(
            orbit(listOf("folha", "folhaEscura"), 9, 47f, 0.55f, spin = "", arc = -80f to 80f),
            orbit(
                listOf("florRosa", "florAmarela", "florAzul"), 7, 50f, 0.6f,
                spin = "", arc = -66f to 66f, rotate = false, each = "bob", stagger = 0.3f,
            ),
        )
    },
    art("moldura-ipe") {
        group(
            ipeCluster(-40f),
            ipeCluster(140f),
            fallers(listOf("ipe"), 5, 81, 0.25f, "drift", 120f),
        )
    },
    art("moldura-arara") {
        group(
            arcsRing(
                listOf(
                    Triple(k(0xDC2626), 200f, 300f),
                    Triple(k(0x2563EB), 300f, 380f),
                    Triple(k(0xFACC15), 20f, 70f),
                ),
                55f, 3f,
            ),
            perch("arara", 62f, 60f, 1.9f, 0f, "bob"),
        )
    },
    art("moldura-tucano") {
        group(
            orbit(listOf("folhaEscura"), 6, 58f, 0.7f, spin = "", arc = -110f to -10f),
            perch("tucano", -52f, 60f, 1.9f, 0f, "bob"),
        )
    },
    art("moldura-beija-flor") {
        group(
            orbit(listOf("florRosa", "florAmarela"), 4, 58f, 0.55f, spin = "", arc = 110f to 250f),
            orbit(listOf("beijaflor"), 1, 66f, 1.3f, spin = "spin-fast", rotOff = 0f),
        )
    },
    art("moldura-mandacaru") {
        group(
            perch("mandacaru", 225f, 60f, 2f),
            perch("mandacaru", 140f, 62f, 1.5f),
            sun(62f, 40f),
        )
    },
    art("moldura-florada") { florada() },
)

// ===== dev.tsx ==============================================================================

private const val TERMINAL_PROMPT = "~/enois \$ git push origin main && echo deploy ·"

/** Um headset com microfone e a luz verde de "ao vivo". */
private fun fone(): FxNode = group(
    pathNode(arcPath(53f, -72f, 72f), stroke = k(0x334155), sw = 5f, cap = StrokeCap.Round),
    group(listOf(-80f, 80f).map { a ->
        val o = polar(
            52.0,
            a.toDouble()
        )
        rect(
            rf(o.x - 6f), rf(o.y - 11f), 12f, 22f,
            fill = PrimaryColor, stroke = k(0x1E293B), sw = 1.5f, rx = 5f,
        )
    }),
    pathNode(
        Path().apply {
            moveTo(-50f, 8f)
            cubicTo(-48f, 26f, -36f, 36f, -18f, 38f)
        },
        stroke = k(0x334155), sw = 2.5f, cap = StrokeCap.Round,
    ),
    circle(-16f, 38f, 3.4f, fill = k(0x1E293B)),
    circle(40f, -52f, 3.5f, fill = k(0x22C55E), anim = "blink"),
)

private fun dev() = listOf(
    art("moldura-fone") { fone() },
    art("moldura-0x100") {
        group(
            ring(PrimaryColor, 1f, "1 3"),
            textRing("100000000 · 0x100 · 256 · 100000000 · 0x100 · 256 ·", PrimaryColor),
        )
    },
    art("moldura-terminal") {
        group(
            ring(k(0x0F172A), 9f, "", 61f),
            textRing(TERMINAL_PROMPT, k(0x4ADE80), 6.4f),
            rect(-3f, -72f, 6f, 7f, fill = k(0x4ADE80), anim = "blink"),
        )
    },
    art("moldura-toalha") {
        group(
            // Manrope não é fonte do app Compose: cai na fonte padrão (diferença de largura é absorvida
            // pelo textLength)
            textRing(
                "não entre em pânico · 42 · não entre em pânico · 42 ·", k(0x0284C7),
                6.6f, FontFamily.Default, 62f, "spin-rev",
            ),
            perch("toalha", 135f, 56f, 1.6f, 20f),
        )
    },
    art("moldura-d20") {
        orbit(listOf("d20"), 3, 64f, 0.95f, spin = "spin", each = "twinkle", stagger = 0.5f)
    },
    art("moldura-pixel") {
        pixelRing(listOf(k(0xA78BFA), k(0x6746F5), k(0x22D3EE), k(0xF472B6), k(0xFACC15)))
    },
    art("moldura-bugs") {
        orbit(listOf("bug"), 6, 61f, 0.65f, spin = "spin-slow", rotOff = 90f)
    },
)

// ===== criancas.tsx =========================================================================

private fun criancas() = listOf(
    art("moldura-pipas") {
        orbit(
            listOf("pipa", "pipaAzul", "pipaAmarela"), 3, 64f, 1f,
            spin = "spin-slow", rotate = false, each = "sway", stagger = 0.6f,
        )
    },
    art("moldura-piao") {
        group(ring(k(0xF59E0B), 1.2f, "2 4"), place("piao", 0f, 64f, 1.5f, 0f, "wobble"))
    },
    art("moldura-bolhas") { risers(listOf("bolha"), 8, 111, 0.9f, -66f to 66f, 40f to 64f) },
    art("moldura-gude") {
        orbit(listOf("gude", "gudeVerde", "gudeVermelha"), 6, 60f, 0.6f, spin = "spin")
    },
    art("moldura-figurinhas") {
        orbit(listOf("figurinha"), 5, 64f, 0.8f, spin = "spin-slow", each = "bob", stagger = 0.5f)
    },
)