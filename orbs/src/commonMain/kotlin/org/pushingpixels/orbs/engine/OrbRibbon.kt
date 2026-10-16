package org.pushingpixels.orbs.engine

import kotlin.math.*

// This is a modified version of the original source code by Jakub Antalik
// available under the MIT license. See the full license under resources/Orbs.license.

/**
 * Ribbon: an undulating sash of parallel strands rides a great circle — the "composing" state.
 * The same painter also drives "breathing" (ring), via the `faceOn` flag.
 */
public fun frameRibbon(size: Double, t: Double, o: ModeOpts): OrbFrame {
    val cx = size / 2
    val cy = size / 2
    val R = (size / 2) * 0.78
    val spin = o["spin"] ?: 1.0
    val camTilt = 0.3
    val pt = makeProj(t * 0.1 * spin, camTilt, cx, cy, 1.0)
    val rs = radiusScale(size, o["rsPow"] ?: 0.6)

    val dots = mutableListOf<Dot>()
    val ghostN = (o["ghostN"] ?: 150).toInt()
    for (i in 0 until ghostN) {
        val d = fibDir(i, ghostN)
        val (px, py, z) = pt(d.first * R, d.second * R, d.third * R)
        val depth = (z / R + 1) / 2
        dots.add(Dot(x = px, y = py, z = z, r = 0.8 * rs, white = 0.78, a = 0.1 + 0.22 * depth))
    }

    val ya = t * 0.24 * spin
    val faceOn = o["faceOn"] != null
    val ta = if (faceOn) -camTilt else 0.55 + 0.3 * sin(t * 0.18) * spin
    val ux = cos(ya)
    val uy = 0.0
    val uz = sin(ya)
    val vx = -uz * sin(ta)
    val vy = cos(ta)
    val vz = ux * sin(ta)
    val nx = uy * vz - uz * vy
    val ny = uz * vx - ux * vz
    val nz = ux * vy - uy * vx

    val wobAmp = 0.23 * (o["wobMul"] ?: 1.0)
    val baseR = if (faceOn) R / (1 + 0.85 * wobAmp) else R

    val baseLanes = (o["lanes"] ?: 5).toInt()
    val segs = (o["segs"] ?: 88).toInt()
    val lanes = maxOf(1, (baseLanes * (o["bandMul"] ?: 1.0)).toInt())
    for (w in 0 until lanes) {
        val laneOff = (w - (lanes - 1) / 2.0) * 0.075
        val edge = abs(w - (lanes - 1) / 2.0) / maxOf(1.0, (lanes - 1) / 2.0)
        for (k in 0 until segs) {
            val a = (k / segs.toDouble()) * 2 * PI
            val wob = (0.16 * sin(a * 3 - t * 1.7 + w * 0.22) + 0.07 * sin(a * 5 + t * 1.1)) * (o["wobMul"] ?: 1.0)
            val radial = if (faceOn) 1 + wob else 1.0
            val off = if (faceOn) laneOff else laneOff + wob
            val x = ux * cos(a) + vx * sin(a) + nx * off
            val y = uy * cos(a) + vy * sin(a) + ny * off
            val z = uz * cos(a) + vz * sin(a) + nz * off
            val l = sqrt(x * x + y * y + z * z)
            val rr = baseR * radial
            val (px, py, zr) = pt(x / l * rr, y / l * rr, z / l * rr)
            val depth = (zr / R + 1) / 2
            dots.add(Dot(
                x = px, y = py, z = zr,
                r = ((o["rBase"] ?: 1.1) + (o["rDepth"] ?: 1.7) * depth) * (1 - 0.25 * edge) * rs,
                white = 0.52 - 0.44 * depth + 0.18 * edge,
                a = 0.4 + 0.6 * depth
            ))
        }
    }
    return finalizeFrame(dots, emptyList(), o["rMin"])
}
