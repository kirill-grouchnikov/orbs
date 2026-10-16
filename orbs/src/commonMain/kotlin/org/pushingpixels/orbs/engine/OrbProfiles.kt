package org.pushingpixels.orbs.engine

import kotlin.math.*

// This is a modified version of the original source code by Jakub Antalik
// available under the MIT license. See the full license under resources/Orbs.license.

// ─── Density profiles + the multiplier machinery that scales them ──────────────

/** Base (fine) profiles per mode, before preset multipliers. */
public val BASE_PROFILES: Map<String, ModeOpts> = mapOf(
    "globe" to mapOf(
        "latRings" to 17.0, "lonDensity" to 44.0, "rBase" to 0.6, "rDepth" to 1.7,
        "rBoost" to 1.0, "inkFar" to 0.62, "inkSpan" to 0.54, "rsPow" to 0.6, "rMin" to 0.3
    ),
    "orbits" to mapOf(
        "orbitN" to 12.0, "ghostN" to 40.0, "ghostR" to 0.9, "ghostA" to 0.5,
        "particles" to 3.0, "partR" to 1.2, "partRDepth" to 1.6, "rsPow" to 0.6, "rMin" to 0.3
    ),
    "rubik" to mapOf(
        "latRings" to 15.0, "lonDensity" to 40.0, "moveCount" to 14.0,
        "rBase" to 0.6, "rDepth" to 1.7, "rActive" to 0.3,
        "inkFar" to 0.62, "inkSpan" to 0.54, "rsPow" to 0.6, "rMin" to 0.3
    ),
    "wave" to mapOf(
        "rings" to 15.0, "lonDensity" to 40.0, "rBase" to 0.6, "rDepth" to 1.7,
        "rsPow" to 0.6, "rMin" to 0.3
    ),
    "web" to mapOf(
        "nodeN" to 30.0, "thr" to 0.72, "signals" to 5.0,
        "nodeR" to 1.4, "nodeRDepth" to 1.8, "lineW" to 0.8, "rsPow" to 0.6, "rMin" to 0.3
    ),
    "braid" to mapOf(
        "strandN" to 52.0, "turns" to 3.0, "ghostN" to 150.0,
        "rBase" to 1.2, "rDepth" to 1.8, "rsPow" to 0.6, "rMin" to 0.3
    ),
    "ribbon" to mapOf(
        "lanes" to 5.0, "segs" to 88.0, "ghostN" to 150.0,
        "rBase" to 1.1, "rDepth" to 1.7, "rsPow" to 0.6, "rMin" to 0.3
    ),
    "ring" to mapOf(
        "lanes" to 5.0, "segs" to 88.0, "ghostN" to 0.0, "faceOn" to 1.0,
        "rBase" to 1.1, "rDepth" to 1.7, "rsPow" to 0.6, "rMin" to 0.3
    ),
    "morph" to mapOf(
        "rDot" to 0.021, "iconD" to 1.0, "rMin" to 0.25
    )
)

private val COUNT_PAIRS: List<Pair<String, String>> = listOf(
    "latRings" to "lonDensity",
    "rings" to "lonDensity",
    "lanes" to "segs"
)
private val COUNT_KEYS = listOf("orbitN", "ghostN", "nodeN", "strandN", "signals")
private val ICON_DENSITY_KEYS = listOf("iconD")
private val RADIUS_KEYS = listOf(
    "rBase", "rDepth", "rActive", "rDot", "ghostR", "partR", "partRDepth", "nodeR", "nodeRDepth"
)

/** Scale dot counts for a smaller/larger preset. */
public fun scaleCounts(opts: ModeOpts, scale: Double): ModeOpts {
    val out = opts.toMutableMap()
    val done = mutableSetOf<String>()
    val rt = sqrt(scale)
    for ((a, b) in COUNT_PAIRS) {
        val va = out[a]
        val vb = out[b]
        if (va != null && vb != null && a !in done && b !in done) {
            out[a] = maxOf(2, round(va * rt).toInt()).toDouble()
            out[b] = maxOf(2, round(vb * rt).toInt()).toDouble()
            done.add(a)
            done.add(b)
        }
    }
    for (k in COUNT_KEYS) {
        val v = out[k]
        if (v != null && v != 0.0 && k !in done) {
            out[k] = maxOf(1, round(v * scale).toInt()).toDouble()
        }
    }
    for (k in ICON_DENSITY_KEYS) {
        val v = out[k]
        if (v != null) out[k] = maxOf(0.02, v * scale)
    }
    return out
}

/** Scale dot radii for a smaller/larger preset. */
public fun scaleRadii(opts: ModeOpts, scale: Double): ModeOpts {
    val out = opts.toMutableMap()
    for (k in RADIUS_KEYS) {
        val v = out[k]
        if (v != null) out[k] = v * scale
    }
    out["rSizeMul"] = (out["rSizeMul"] ?: 1.0) * scale
    return out
}
