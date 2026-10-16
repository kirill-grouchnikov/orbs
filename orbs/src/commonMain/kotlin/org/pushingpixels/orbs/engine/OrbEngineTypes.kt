package org.pushingpixels.orbs.engine

// This is a modified version of the original source code by Jakub Antalik
// available under the MIT license. See the full license under resources/Orbs.license.

/**
 * A single dot mark in the frame.
 * [white] is the ink value: 0 = darkest ink on paper. Mirrored on dark themes.
 * [a] is the alpha (opacity), defaults to 1.0.
 */
public data class Dot(
    val x: Double,
    val y: Double,
    val z: Double,
    val r: Double,
    val white: Double,
    val a: Double = 1.0
)

/**
 * A stroked edge between two projected points (used by the Connecting web).
 * [white] uses the same convention as [Dot.white].
 */
public data class Line(
    val x1: Double,
    val y1: Double,
    val x2: Double,
    val y2: Double,
    val white: Double,
    val a: Double = 1.0,
    val w: Double
)

/**
 * One rendered instant: a complete, final set of draw instructions.
 * [dots] is already z-sorted into draw order and radius-clamped; [lines]
 * are drawn first. Nothing here needs further interpretation.
 */
public data class OrbFrame(
    val dots: List<Dot>,
    val lines: List<Line>
)

/** Mode options — numeric tuning parameters per mode. */
public typealias ModeOpts = Map<String, Double>

/** Geometry builder: pure math over (size, t, opts), no rendering surface. */
public typealias ModeFrame = (size: Double, t: Double, opts: ModeOpts) -> OrbFrame
