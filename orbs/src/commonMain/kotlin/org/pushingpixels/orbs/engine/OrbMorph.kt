package org.pushingpixels.orbs.engine

import kotlin.math.*

// This is a modified version of the original source code by Jakub Antalik
// available under the MIT license. See the full license under resources/Orbs.license.

/** Morph: a dotted outline cycling circle → triangle → square — the "shaping" state. */
public fun frameMorph(size: Double, t: Double, o: ModeOpts): OrbFrame {
    val K = 3
    val HOLD = 1.4
    val MORPH = 0.9
    val SEG = HOLD + MORPH
    val tc = t % (SEG * K)
    val k = (tc / SEG).toInt()
    val local = tc - k * SEG
    val m = if (local > HOLD) smoothE((local - HOLD) / MORPH) else 0.0
    val sprd = o["spread"] ?: 1.0

    val pA = cyclePaths[k]
    val pB = cyclePaths[(k + 1) % K]
    val M = 160
    val pts = mutableListOf<Pair<Double, Double>>()
    for (i in 0 until M) {
        val f = i / M.toDouble()
        val a = pA(f)
        val b = pB(f)
        pts.add(((a.first + (b.first - a.first) * m) * sprd) to ((a.second + (b.second - a.second) * m) * sprd))
    }
    val L = mutableListOf<Double>()
    var total = 0.0
    for (i in 0 until M) {
        val a = pts[i]
        val b = pts[(i + 1) % M]
        val l = hypot(b.first - a.first, b.second - a.second)
        L.add(l)
        total += l
    }

    val n = morphN(o["iconD"] ?: 1.0)
    val re = (o["rDot"] ?: 0.021) * 1.35 * sprd
    val pulse = 1 + 0.02 * sin(local * 3.1)

    val dots = mutableListOf<Dot>()
    val c2 = size / 2
    var seg = 0
    var acc = 0.0
    for (k2 in 0 until n) {
        val target = (k2 / n.toDouble()) * total
        while (acc + L[seg] < target && seg < M - 1) {
            acc += L[seg]
            seg++
        }
        val a = pts[seg]
        val b = pts[(seg + 1) % M]
        val f = if (L[seg] != 0.0) minOf(1.0, (target - acc) / L[seg]) else 0.0
        val x = (a.first + (b.first - a.first) * f) * pulse
        val y = (a.second + (b.second - a.second) * f) * pulse
        dots.add(Dot(
            x = c2 + x * size,
            y = c2 + y * size,
            z = 0.0,
            r = maxOf(0.35, re * size),
            white = 0.1
        ))
    }
    return finalizeFrame(dots, emptyList(), o["rMin"])
}

private fun smoothE(x: Double): Double = x * x * (3 - 2 * x)

private fun morphN(d: Double): Int = maxOf(6, (34 * d).toInt())

private fun polyPath(verts: Array<Pair<Double, Double>>): (Double) -> Pair<Double, Double> {
    val V = verts.size
    val L = mutableListOf<Double>()
    var total = 0.0
    for (i in 0 until V) {
        val a = verts[i]
        val b = verts[(i + 1) % V]
        val l = hypot(b.first - a.first, b.second - a.second)
        L.add(l)
        total += l
    }
    return { f: Double ->
        var target = f * total
        var i = 0
        while (target > L[i] && i < V - 1) {
            target -= L[i]
            i++
        }
        val a = verts[i]
        val b = verts[(i + 1) % V]
        val ff = if (L[i] != 0.0) minOf(1.0, target / L[i]) else 0.0
        (a.first + (b.first - a.first) * ff) to (a.second + (b.second - a.second) * ff)
    }
}

private val circlePath: (Double) -> Pair<Double, Double> = { f ->
    val a = -PI / 2 + f * 2 * PI
    (cos(a) * 0.24) to (sin(a) * 0.24)
}

private val trianglePath = polyPath(arrayOf(
    0.0 to -0.26,
    0.24 to 0.16,
    -0.24 to 0.16
))

private val squarePath = polyPath(arrayOf(
    0.0 to -0.2,
    0.2 to -0.2,
    0.2 to 0.2,
    -0.2 to 0.2,
    -0.2 to -0.2
))

private val cyclePaths = arrayOf(circlePath, trianglePath, squarePath)
