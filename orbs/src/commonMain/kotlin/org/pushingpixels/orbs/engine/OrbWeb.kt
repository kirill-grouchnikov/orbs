package org.pushingpixels.orbs.engine

import kotlin.math.*

// This is a modified version of the original source code by Jakub Antalik
// available under the MIT license. See the full license under resources/Orbs.license.

/** Web: a constellation wires itself — the "connecting" state. */
public fun frameWeb(size: Double, t: Double, o: ModeOpts): OrbFrame {
    val cx = size / 2
    val cy = size / 2
    val R = (size / 2) * 0.8 * (o["spread"] ?: 1.0)
    val pt = makeProj(t * 0.12, 0.32, cx, cy, R)
    val rs = radiusScale(size, o["rsPow"] ?: 0.6)

    val nodeN = (o["nodeN"] ?: 30).toInt()
    val thr = o["thr"] ?: 0.72
    val nodeR = o["nodeR"] ?: 1.4
    val nodeRDepth = o["nodeRDepth"] ?: 1.8

    val nodes = mutableListOf<Triple<Double, Double, Double>>()
    for (i in 0 until nodeN) {
        val d = fibDir(i, nodeN)
        val x = d.first + 0.3 * (vnoise(i * 0.31 + 9, t * 0.24) - 0.5) * 2
        val y = d.second + 0.3 * (vnoise(i * 0.53 + 27, t * 0.21) - 0.5) * 2
        val z = d.third + 0.3 * (vnoise(i * 0.77 + 55, t * 0.27) - 0.5) * 2
        val l = sqrt(x * x + y * y + z * z)
        nodes.add(Triple(x / l, y / l, z / l))
    }

    val lines = mutableListOf<Line>()
    val dots = mutableListOf<Dot>()

    for (i in 0 until nodeN) {
        for (j in i + 1 until nodeN) {
            val dx = nodes[i].first - nodes[j].first
            val dy = nodes[i].second - nodes[j].second
            val dz = nodes[i].third - nodes[j].third
            val dist = sqrt(dx * dx + dy * dy + dz * dz)
            if (dist >= thr) continue
            val (x1, y1, z1) = pt(nodes[i].first, nodes[i].second, nodes[i].third)
            val (x2, y2, z2) = pt(nodes[j].first, nodes[j].second, nodes[j].third)
            val depth = ((z1 + z2) / 2 + 1) / 2
            lines.add(Line(
                x1 = x1, y1 = y1, x2 = x2, y2 = y2,
                white = 0.42,
                a = (1 - dist / thr) * (0.3 + 0.55 * depth),
                w = maxOf(0.6, (o["lineW"] ?: 0.8) * rs)
            ))
        }
    }

    for (i in 0 until nodeN) {
        val (px, py, z) = pt(nodes[i].first, nodes[i].second, nodes[i].third)
        val depth = (z + 1) / 2
        val pulse = 1 + 0.25 * sin(t * 1.4 + i * 2.7)
        dots.add(Dot(
            x = px, y = py, z = z,
            r = (nodeR + nodeRDepth * depth) * pulse * rs,
            white = 0.55 - 0.45 * depth
        ))
    }

    val signals = (o["signals"] ?: 5).toInt()
    for (s in 0 until signals) {
        val seg = (t * 0.55 + s * 7.31).toInt()
        val a = (hashD(seg.toDouble(), s * 3.1 + 1.7) * nodeN).toInt()
        val b = (hashD(seg.toDouble(), s * 5.7 + 4.2) * nodeN).toInt()
        if (a == b) continue
        val f = frac(t * 0.55 + s * 7.31)
        val x = lerp(nodes[a].first, nodes[b].first, f)
        val y = lerp(nodes[a].second, nodes[b].second, f)
        val z = lerp(nodes[a].third, nodes[b].third, f)
        val l = maxOf(1e-6, sqrt(x * x + y * y + z * z))
        val (px, py, zr) = pt(x / l, y / l, z / l)
        val depth = (zr + 1) / 2
        dots.add(Dot(
            x = px, y = py, z = zr,
            r = (nodeR * 1.5 + nodeRDepth * depth) * rs,
            white = 0.05,
            a = 0.5 + 0.5 * depth
        ))
    }

    return finalizeFrame(dots, lines, o["rMin"])
}
