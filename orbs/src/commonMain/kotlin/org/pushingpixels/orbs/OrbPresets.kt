package org.pushingpixels.orbs

import org.pushingpixels.orbs.engine.BASE_PROFILES
import org.pushingpixels.orbs.engine.ModeKey
import org.pushingpixels.orbs.engine.ModeOpts
import org.pushingpixels.orbs.engine.scaleCounts
import org.pushingpixels.orbs.engine.scaleRadii

// This is a modified version of the original source code by Jakub Antalik
// available under the MIT license. See the full license under resources/Orbs.license.

// ─── Presets: nine states × two sizes, baked from the tuning session ───────────

/** One preset tuning for a (state, size) pair. */
public data class Preset(
    val speed: Double,
    val count: Double,
    val size: Double,
    val extra: ModeOpts? = null
)

/** Resolved preset: mode key, effective speed, and fully-scaled draw options. */
public data class Resolved(
    val mode: ModeKey,
    val speed: Double,
    val opts: ModeOpts
)

/** Map from OrbState to its engine mode. */
public val STATE_TO_MODE: Map<OrbState, ModeKey> = mapOf(
    OrbState.Working to ModeKey.Orbits,
    OrbState.Searching to ModeKey.Globe,
    OrbState.Solving to ModeKey.Rubik,
    OrbState.Listening to ModeKey.Wave,
    OrbState.Connecting to ModeKey.Web,
    OrbState.Weaving to ModeKey.Braid,
    OrbState.Composing to ModeKey.Ribbon,
    OrbState.Breathing to ModeKey.Ring,
    OrbState.Shaping to ModeKey.Morph
)

/** Shipped presets per mode key and size. */
public val PRESETS: Map<ModeKey, Map<Int, Preset>> = mapOf(
    ModeKey.Orbits to mapOf(
        64 to Preset(speed = 1.885, count = 1.0, size = 1.0),
        20 to Preset(speed = 3.9, count = 0.238, size = 2.4)
    ),
    ModeKey.Globe to mapOf(
        64 to Preset(speed = 2.015, count = 0.42, size = 1.15, extra = mapOf("scanMul" to 4.08, "dimBase" to 0.45)),
        20 to Preset(speed = 2.665, count = 0.105, size = 1.75, extra = mapOf("scanMul" to 4.335, "dimBase" to 0.45))
    ),
    ModeKey.Rubik to mapOf(
        64 to Preset(speed = 1.82, count = 0.35, size = 1.05),
        20 to Preset(speed = 1.95, count = 0.088, size = 1.9)
    ),
    ModeKey.Wave to mapOf(
        64 to Preset(speed = 4.388, count = 0.341, size = 1.0),
        20 to Preset(speed = 3.998, count = 0.105, size = 1.6)
    ),
    ModeKey.Web to mapOf(
        64 to Preset(speed = 3.315, count = 1.35, size = 0.95),
        20 to Preset(speed = 6.63, count = 0.25, size = 1.52)
    ),
    ModeKey.Braid to mapOf(
        64 to Preset(speed = 1.625, count = 0.5, size = 1.0),
        20 to Preset(speed = 2.75, count = 0.1125, size = 1.36)
    ),
    ModeKey.Ribbon to mapOf(
        64 to Preset(speed = 2.34, count = 0.25, size = 0.85, extra = mapOf("spin" to 0.0, "bandMul" to 3.9, "wobMul" to 1.0)),
        20 to Preset(speed = 3.12, count = 0.051, size = 1.073, extra = mapOf("spin" to 0.0, "bandMul" to 4.94, "wobMul" to 1.0))
    ),
    ModeKey.Ring to mapOf(
        64 to Preset(speed = 3.24, count = 0.25, size = 0.956, extra = mapOf("spin" to 0.0, "bandMul" to 3.627, "wobMul" to 0.368)),
        20 to Preset(speed = 3.78, count = 0.028, size = 1.622, extra = mapOf("spin" to 0.0, "bandMul" to 3.968, "wobMul" to 0.565))
    ),
    ModeKey.Morph to mapOf(
        64 to Preset(speed = 2.405, count = 0.702, size = 0.395, extra = mapOf("spread" to 1.45)),
        20 to Preset(speed = 2.08, count = 0.53, size = 1.011, extra = mapOf("spread" to 1.45))
    )
)

private val cache = mutableMapOf<String, Resolved>()

/** Resolve a (state, size) pair to its mode + fully-scaled draw options. */
public fun resolvePreset(state: OrbState, size: Int): Resolved {
    val key = "${state.name}-$size"
    cache[key]?.let { return it }

    val mode = STATE_TO_MODE[state]!!
    val preset = PRESETS[mode]!![size]!!
    var opts: ModeOpts = BASE_PROFILES[mode.key]!!.toMap()
    if (preset.count != 1.0) opts = scaleCounts(opts, preset.count)
    if (preset.size != 1.0) opts = scaleRadii(opts, preset.size)
    preset.extra?.let { opts += it }

    val resolved = Resolved(mode, preset.speed, opts)
    cache[key] = resolved
    return resolved
}
