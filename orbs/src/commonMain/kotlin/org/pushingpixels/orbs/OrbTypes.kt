package org.pushingpixels.orbs

// This is a modified version of the original source code by Jakub Antalik
// available under the MIT license. See the full license under resources/Orbs.license.

/**
 * The nine shipped states — each a hand-tuned animation:
 * - `Working`    — particles on tilted orbits
 * - `Searching`  — a scan meridian sweeps a dotted globe
 * - `Solving`    — bands scramble in quarter turns, then click back
 * - `Listening`  — a waveform rolls through latitude rings
 * - `Connecting` — a constellation wires itself, packets running the edges
 * - `Weaving`    — three strands plait around the sphere
 * - `Composing`  — an undulating multi-band sash
 * - `Breathing`  — a face-on ring slowly morphing
 * - `Shaping`    — a dotted outline morphs circle → triangle → square
 */
public enum class OrbState {
    Working,
    Searching,
    Solving,
    Listening,
    Connecting,
    Weaving,
    Composing,
    Breathing,
    Shaping
}

/**
 * Rendered size in pixels. Exactly two tuned presets ship:
 * 64 (chat-avatar scale) and 20 (inline-text scale). Each size carries
 * its own dot count, dot size and speed tuning — they are separate
 * designs, not a scale factor.
 */
public enum class OrbSize(public val value: Int) {
    Large(64),
    Small(20)
}
