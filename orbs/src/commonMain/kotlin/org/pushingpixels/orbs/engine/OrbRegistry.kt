package org.pushingpixels.orbs.engine

// This is a modified version of the original source code by Jakub Antalik
// available under the MIT license. See the full license under resources/Orbs.license.

/** Mode key for the geometry builder. */
public enum class ModeKey(public val key: String) {
    Orbits("orbits"),
    Globe("globe"),
    Rubik("rubik"),
    Wave("wave"),
    Web("web"),
    Braid("braid"),
    Ribbon("ribbon"),
    Ring("ring"),
    Morph("morph")
}

/** Mode key → geometry builder. */
public val MODE_FRAMES: Map<ModeKey, ModeFrame> = mapOf(
    ModeKey.Orbits to ::frameOrbits,
    ModeKey.Globe to ::frameGlobe,
    ModeKey.Rubik to ::frameRubik,
    ModeKey.Wave to ::frameWave,
    ModeKey.Web to ::frameWeb,
    ModeKey.Braid to ::frameBraid,
    ModeKey.Ribbon to ::frameRibbon,
    ModeKey.Ring to ::frameRibbon,
    ModeKey.Morph to ::frameMorph
)
