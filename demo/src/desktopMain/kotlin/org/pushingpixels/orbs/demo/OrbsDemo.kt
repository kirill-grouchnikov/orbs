/*
 * Copyright 2020-2026 Aurora, Kirill Grouchnikov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.pushingpixels.orbs.demo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import org.pushingpixels.orbs.OrbState
import org.pushingpixels.orbs.ThinkingOrb

fun main() = application {
    val state = rememberWindowState(
        placement = WindowPlacement.Floating,
        position = WindowPosition.Aligned(Alignment.Center),
        size = DpSize(640.dp, 560.dp)
    )

    Window(
        title = "Loaders Demo",
        state = state,
        onCloseRequest = ::exitApplication
    ) {
        LazyVerticalGrid(
            modifier = Modifier.fillMaxSize().background(Color(0xFF171717)),
            columns = GridCells.Adaptive(minSize = 100.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 24.dp),
        ) {
            item { OrbsItem("Working") { ThinkingOrb(state = OrbState.Working) } }
            item { OrbsItem("Searching") { ThinkingOrb(state = OrbState.Searching) } }
            item { OrbsItem("Solving") { ThinkingOrb(state = OrbState.Solving) } }
            item { OrbsItem("Listening") { ThinkingOrb(state = OrbState.Listening) } }
            item { OrbsItem("Connecting") { ThinkingOrb(state = OrbState.Connecting) } }
            item { OrbsItem("Weaving") { ThinkingOrb(state = OrbState.Weaving) } }
            item { OrbsItem("Composing") { ThinkingOrb(state = OrbState.Composing) } }
            item { OrbsItem("Breathing") { ThinkingOrb(state = OrbState.Breathing) } }
            item { OrbsItem("Shaping") { ThinkingOrb(state = OrbState.Shaping) } }
        }
    }
}

@Composable
fun OrbsItem(name: String, content: @Composable () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        content()
        BasicText(text = name, style = TextStyle(color = Color.White))
    }
}

