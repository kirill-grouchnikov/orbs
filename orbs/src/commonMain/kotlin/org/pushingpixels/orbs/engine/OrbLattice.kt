package org.pushingpixels.orbs.engine

import kotlin.math.*

// This is a modified version of the original source code by Jakub Antalik
// available under the MIT license. See the full license under resources/Orbs.license.

// ─── Shared solver heartbeat (rubik) ──────────────────────────────────────────

private data class Move(val axis: Int, val lo: Double, val hi: Double, val ang: Double)

private data class SolveState(val amount: List<Double>, val active: Int)

private fun solveCycle(time: Double, count: Int, slotDur: Double, rest: Double): SolveState {
    val cyc = 2 * count * slotDur + rest
    val tc = time % cyc
    val amount = MutableList(count) { 0.0 }
    var active = -1
    if (tc < 2 * count * slotDur) {
        val slot = (tc / slotDur).toInt()
        val p = (tc - slot * slotDur) / slotDur
        val cl = minOf(1.0, p / 0.7)
        val ep = 1 - (1 - cl).pow(3)
        if (slot < count) {
            for (i in 0 until slot) amount[i] = 1.0
            amount[slot] = ep
            active = slot
        } else {
            val u = 2 * count - 1 - slot
            for (i in 0 until u) amount[i] = 1.0
            amount[u] = 1 - ep
            active = u
        }
    }
    return SolveState(amount, active)
}

private fun applyMoves(
    pt3: Triple<Double, Double, Double>,
    moves: List<Move>,
    sc: SolveState
): Quad<Double, Double, Double, Boolean> {
    var (x, y, z) = pt3
    var inActive = false
    for (i in moves.indices) {
        if (sc.amount[i] <= 0) continue
        val mv = moves[i]
        val coord = when (mv.axis) { 0 -> x; 1 -> y; 2 -> z; else -> 0.0 }
        if (coord < mv.lo || coord >= mv.hi) continue
        if (i == sc.active) inActive = true
        val a = mv.ang * sc.amount[i]
        val ca = cos(a)
        val sa = sin(a)
        if (mv.axis == 0) {
            val y2 = y * ca - z * sa
            z = y * sa + z * ca
            y = y2
        } else if (mv.axis == 1) {
            val x2 = x * ca + z * sa
            z = -x * sa + z * ca
            x = x2
        } else {
            val x2 = x * ca - y * sa
            y = x * sa + y * ca
            x = x2
        }
    }
    return Quad(x, y, z, inActive)
}

private fun makeMoves(count: Int): List<Move> {
    val moves = mutableListOf<Move>()
    for (i in 0 until count) {
        val axis = minOf(2, (hashD(i.toDouble(), 2.3) * 3).toInt())
        val lo = -1.0 + 0.5 * minOf(3, (hashD(i.toDouble(), 5.9) * 4).toInt()).toDouble()
        val dir = if (hashD(i.toDouble(), 7.7) < 0.5) 1 else -1
        moves.add(Move(axis, lo, lo + 0.5, (dir * PI) / 2))
    }
    return moves
}

/** Helper to hold 4 values. */
private data class Quad<A, B, C, D>(val a: A, val b: B, val c: C, val d: D)

// ─── Globe: lat/long field, a scan meridian sweeps — searching ────────────────

public fun frameGlobe(size: Double, t: Double, o: ModeOpts): OrbFrame {
    val spin = 0.5
    val cx = size / 2
    val cy = size / 2
    val radius = (size / 2) * 0.82
    val tilt = 0.4 + 0.06 * sin(t * 0.35)
    val pt = makeProj(t * spin, tilt, cx, cy, radius)
    val scan = t * (spin + (1.7 - spin) * (o["scanMul"] ?: 1.0))
    val rs = radiusScale(size, o["rsPow"] ?: 0.6)
    val dimBase = o["dimBase"] ?: 1.0

    val dots = mutableListOf<Dot>()
    val latRings = (o["latRings"] ?: 17).toInt()
    val lonDensity = (o["lonDensity"] ?: 44).toInt()
    for (li in 0..latRings) {
        val lat = -PI / 2 + (li / latRings.toDouble()) * PI
        val cosLat = cos(lat)
        val sinLat = sin(lat)
        val lonCount = maxOf(1, (abs(cosLat) * lonDensity).toInt())
        for (lj in 0 until lonCount) {
            val lon = (lj / lonCount.toDouble()) * 2 * PI
            val (px, py, z) = pt(cosLat * cos(lon), sinLat, cosLat * sin(lon))
            val depth = (z + 1) / 2
            val d = angleDelta(lon + t * spin, scan)
            val boost = exp(-(d * d) / 0.18) * maxOf(0.0, z)
            dots.add(Dot(
                x = px, y = py, z = z,
                r = ((o["rBase"] ?: 0.6) + (o["rDepth"] ?: 1.7) * depth + (o["rBoost"] ?: 1.0) * boost) * rs,
                white = (o["inkFar"] ?: 0.62) - (o["inkSpan"] ?: 0.54) * depth,
                a = dimBase + (1 - dimBase) * minOf(1.0, boost)
            ))
        }
    }
    return finalizeFrame(dots, emptyList(), o["rMin"])
}

// ─── Rubik: bands twist in quarter turns, scramble → solve — solving ──────────

public fun frameRubik(size: Double, t: Double, o: ModeOpts): OrbFrame {
    val cx = size / 2
    val cy = size / 2
    val R = (size / 2) * 0.82
    val pt = makeProj(t * 0.55, 0.35 + 0.1 * sin(t * 0.9), cx, cy, R)
    val rs = radiusScale(size, o["rsPow"] ?: 0.6)
    val moveCount = (o["moveCount"] ?: 14).toInt()
    val moves = makeMoves(moveCount)
    val sc = solveCycle(t, moveCount, 0.42, 1.2)

    val dots = mutableListOf<Dot>()
    val latRings = (o["latRings"] ?: 15).toInt()
    val lonDensity = (o["lonDensity"] ?: 40).toInt()
    for (li in 0..latRings) {
        val lat = -PI / 2 + (li / latRings.toDouble()) * PI
        val cosLat = cos(lat)
        val sinLat = sin(lat)
        val lonCount = maxOf(1, (abs(cosLat) * lonDensity).toInt())
        for (lj in 0 until lonCount) {
            val lon = (lj / lonCount.toDouble()) * 2 * PI
            val result = applyMoves(
                Triple(cosLat * cos(lon), sinLat, cosLat * sin(lon)), moves, sc
            )
            val (px, py, zr) = pt(result.a, result.b, result.c)
            val depth = (zr + 1) / 2
            val inActive = result.d
            dots.add(Dot(
                x = px, y = py, z = zr,
                r = ((o["rBase"] ?: 0.6) + (o["rDepth"] ?: 1.7) * depth + (if (inActive) (o["rActive"] ?: 0.3) else 0.0)) * rs,
                white = (o["inkFar"] ?: 0.62) - (o["inkSpan"] ?: 0.54) * depth - (if (inActive) 0.14 else 0.0)
            ))
        }
    }
    return finalizeFrame(dots, emptyList(), o["rMin"])
}

// ─── Wave: a waveform rolls through the rings — listening ─────────────────────

public fun frameWave(size: Double, t: Double, o: ModeOpts): OrbFrame {
    val cx = size / 2
    val cy = size / 2
    val R = (size / 2) * 0.874
    val pt = makeProj(t * 0.18, 0.38, cx, cy, 1.0)
    val rs = radiusScale(size, o["rsPow"] ?: 0.6)

    val dots = mutableListOf<Dot>()
    val rings = (o["rings"] ?: 15).toInt()
    val lonDensity = (o["lonDensity"] ?: 40).toInt()
    for (ri in 0..rings) {
        val lat = -PI / 2 + (ri / rings.toDouble()) * PI
        val cosLat = cos(lat)
        val sinLat = sin(lat)
        val w = 0.62 * sin(t * 2.1 - ri * 0.52) + 0.38 * sin(t * 1.27 + ri * 0.83)
        val rr = R * (0.88 + 0.105 * w)
        val lonCount = maxOf(1, (abs(cosLat) * lonDensity).toInt())
        for (lj in 0 until lonCount) {
            val lon = (lj / lonCount.toDouble()) * 2 * PI
            val (px, py, z) = pt(cosLat * cos(lon) * rr, sinLat * rr, cosLat * sin(lon) * rr)
            val depth = (z / R + 1) / 2
            val crest = maxOf(0.0, w)
            dots.add(Dot(
                x = px, y = py, z = z,
                r = ((o["rBase"] ?: 0.6) + (o["rDepth"] ?: 1.7) * depth) * (1 + 0.4 * crest) * rs,
                white = 0.66 - 0.56 * depth - 0.1 * crest
            ))
        }
    }
    return finalizeFrame(dots, emptyList(), o["rMin"])
}
