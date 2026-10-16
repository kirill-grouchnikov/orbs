package org.pushingpixels.orbs.engine

import kotlin.math.*

// This is a modified version of the original source code by Jakub Antalik
// available under the MIT license. See the full license under resources/Orbs.license.

// ─── Core math utilities ───────────────────────────────────────────────────────

/** Linear interpolation. */
public fun lerp(a: Double, b: Double, f: Double): Double = a + (b - a) * f

/** Fractional part. */
public fun frac(x: Double): Double = x - floor(x)

/** Deterministic hash in [0, 1). */
public fun hashD(a: Double, b: Double): Double {
    val h = sin(a * 12.9898 + b * 78.233) * 43758.5453
    return h - floor(h)
}

/** Value noise on a 2D lattice — smooth, deterministic, cheap. */
public fun vnoise(x: Double, y: Double): Double {
    val xi = floor(x).toInt()
    val yi = floor(y).toInt()
    var fx = x - xi
    var fy = y - yi
    fx = fx * fx * (3 - 2 * fx)
    fy = fy * fy * (3 - 2 * fy)
    val a = hashD(xi.toDouble(), yi.toDouble())
    val b = hashD((xi + 1).toDouble(), yi.toDouble())
    val c = hashD(xi.toDouble(), (yi + 1).toDouble())
    val d = hashD((xi + 1).toDouble(), (yi + 1).toDouble())
    return a + (b - a) * fx + (c - a) * fy + (a - b - c + d) * fx * fy
}

/** Stable directions on a unit sphere (Fibonacci lattice). */
public fun fibDir(i: Int, n: Int): Triple<Double, Double, Double> {
    val golden = PI * (3 - sqrt(5.0))
    val y = 1 - (2 * (i + 0.5)) / n
    val rad = sqrt(1 - y * y)
    val a = i * golden
    return Triple(rad * cos(a), y, rad * sin(a))
}

/** Shortest signed angular distance, wrapped to (-π, π]. */
public fun angleDelta(a: Double, b: Double): Double = atan2(sin(a - b), cos(a - b))

/** 3D point type alias. */
public typealias Point3D = Triple<Double, Double, Double>

/** Orthographic projector: (x, y, z) → (screenX, screenY, depthZ). */
public typealias Projector = (x: Double, y: Double, z: Double) -> Triple<Double, Double, Double>

/** Shared spin + tilt + orthographic projection. */
public fun makeProj(
    yaw: Double, tilt: Double, cx: Double, cy: Double, scale: Double
): Projector {
    val st = sin(tilt)
    val ct = cos(tilt)
    val sy = sin(yaw)
    val cyw = cos(yaw)
    return { x: Double, y: Double, z: Double ->
        val x1 = x * cyw + z * sy
        val z1 = -x * sy + z * cyw
        val y1 = y * ct - z1 * st
        val z2 = y * st + z1 * ct
        Triple(cx + x1 * scale, cy - y1 * scale, z2)
    }
}

/** Dot radii were tuned for a 300pt frame; sub-linear scaling keeps small spinners legible. */
public fun radiusScale(size: Double, pow: Double): Double = (size / 300.0).pow(pow)

// ─── Frame finalization ────────────────────────────────────────────────────────

/**
 * Turn raw mode output into a finished frame: drop invisible marks, clamp
 * radii to the mode's floor, and z-sort far→near into draw order.
 */
public fun finalizeFrame(dots: List<Dot>, lines: List<Line>, rMin: Double? = null): OrbFrame {
    val rFloor = rMin ?: 0.3
    val visible = dots.filter { d -> d.a >= 0.02 }.map { d ->
        d.copy(r = maxOf(rFloor, d.r))
    }.sortedBy { it.z }
    return OrbFrame(visible, lines.filter { l -> l.a >= 0.02 })
}
