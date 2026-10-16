package org.pushingpixels.orbs.engine

import kotlin.math.*

// This is a modified version of the original source code by Jakub Antalik
// available under the MIT license. See the full license under resources/Orbs.license.

/** Braid: three strands plait around the sphere — the "weaving" state. */
public fun frameBraid(size: Double, t: Double, o: ModeOpts): OrbFrame {
    val cx = size / 2
    val cy = size / 2
    val R = (size / 2) * 0.76
    val pt = makeProj(t * 0.4, 0.3, cx, cy, 1.0)
    val rs = radiusScale(size, o["rsPow"] ?: 0.6)

    val dots = mutableListOf<Dot>()
    val ghostN = (o["ghostN"] ?: 150).toInt()
    for (i in 0 until ghostN) {
        val d = fibDir(i, ghostN)
        val (px, py, z) = pt(d.first * R, d.second * R, d.third * R)
        val depth = (z / R + 1) / 2
        dots.add(Dot(x = px, y = py, z = z, r = 0.8 * rs, white = 0.78, a = 0.1 + 0.22 * depth))
    }

    val strandN = (o["strandN"] ?: 52).toInt()
    val turns = o["turns"] ?: 3.0
    for (s in 0 until 3) {
        val phase = (s / 3.0) * 2 * PI
        for (i in 0 until strandN) {
            val u = (frac(i / strandN.toDouble() + t * 0.045) * 2 - 1) * 0.96
            val surf = sqrt(maxOf(0.0, 1 - u * u))
            val endFade = minOf(1.0, (1 - abs(u)) / 0.1)
            val a = u * PI * turns + phase
            val weave = 1 + 0.075 * sin(u * PI * turns * 2 + phase * 2 + t * 0.8)
            val rr = surf * R * weave
            val (px, py, zr) = pt(cos(a) * rr, u * R * weave, sin(a) * rr)
            val depth = (zr / R + 1) / 2
            dots.add(Dot(
                x = px, y = py, z = zr,
                r = ((o["rBase"] ?: 1.2) + (o["rDepth"] ?: 1.8) * depth) * rs,
                white = 0.55 - 0.45 * depth,
                a = endFade * (0.45 + 0.55 * depth)
            ))
        }
    }
    return finalizeFrame(dots, emptyList(), o["rMin"])
}
