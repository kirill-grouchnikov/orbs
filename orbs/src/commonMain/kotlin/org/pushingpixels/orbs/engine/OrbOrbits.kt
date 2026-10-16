package org.pushingpixels.orbs.engine

import kotlin.math.*

// This is a modified version of the original source code by Jakub Antalik
// available under the MIT license. See the full license under resources/Orbs.license.

/** Orbits: particles on tilted orbits — the "working" state. */
public fun frameOrbits(size: Double, t: Double, o: ModeOpts): OrbFrame {
    val cx = size / 2
    val cy = size / 2
    val R = (size / 2) * 0.82
    val pt = makeProj(t * 0.12, 0.3, cx, cy, 1.0)
    val rs = radiusScale(size, o["rsPow"] ?: 0.6)

    val dots = mutableListOf<Dot>()
    val orbitN = (o["orbitN"] ?: 12).toInt()
    val ghostN = (o["ghostN"] ?: 40).toInt()
    val particles = (o["particles"] ?: 3).toInt()

    for (orb in 0 until orbitN) {
        val h1 = hashD(orb.toDouble(), 1.7)
        val h2 = hashD(orb.toDouble(), 5.2)
        val h3 = hashD(orb.toDouble(), 8.9)
        val ro = R * (0.45 + 0.52 * h1)
        val th = h1 * 2 * PI
        val phi = acos(2 * h2 - 1)
        val nx = sin(phi) * cos(th)
        val ny = cos(phi)
        val nz = sin(phi) * sin(th)
        var ux = -ny
        var uy = nx
        val uz = 0.0
        val ul = maxOf(1e-6, sqrt(ux * ux + uy * uy))
        ux /= ul
        uy /= ul
        val vx = ny * uz - nz * uy
        val vy = nz * ux - nx * uz
        val vz = nx * uy - ny * ux
        val speed = (0.25 + 0.55 * h3) * (if (h3 > 0.5) 1 else -1)

        for (k in 0 until ghostN) {
            val a = (k / ghostN.toDouble()) * 2 * PI
            val (px, py, z) = pt(
                (ux * cos(a) + vx * sin(a)) * ro,
                (uy * cos(a) + vy * sin(a)) * ro,
                (uz * cos(a) + vz * sin(a)) * ro
            )
            val depth = (z / ro + 1) / 2
            dots.add(Dot(
                x = px, y = py, z = z,
                r = (o["ghostR"] ?: 0.9) * rs,
                white = 0.72,
                a = (o["ghostA"] ?: 0.5) * (0.4 + 0.6 * depth)
            ))
        }
        for (m in 0 until particles) {
            val a = t * speed + (m / particles.toDouble()) * 2 * PI + h2 * 6
            val (px, py, z) = pt(
                (ux * cos(a) + vx * sin(a)) * ro,
                (uy * cos(a) + vy * sin(a)) * ro,
                (uz * cos(a) + vz * sin(a)) * ro
            )
            val depth = (z / ro + 1) / 2
            dots.add(Dot(
                x = px, y = py, z = z,
                r = ((o["partR"] ?: 1.2) + (o["partRDepth"] ?: 1.6) * depth) * rs,
                white = 0.3 - 0.22 * depth
            ))
        }
    }
    return finalizeFrame(dots, emptyList(), o["rMin"])
}
