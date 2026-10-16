package sample.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import org.pushingpixels.orbs.OrbState
import org.pushingpixels.orbs.ThinkingOrb

@Composable
fun App() {
    LazyVerticalGrid(
        modifier = Modifier.fillMaxSize().background(Color(0xFF171717))
            .wrapContentWidth(Alignment.CenterHorizontally),
        columns = GridCells.Adaptive(minSize = 100.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 48.dp),
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


@Composable
fun OrbsItem(name: String, content: @Composable () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        content()
        BasicText(text = name, style = TextStyle(color = Color(0xFFE0E0E0)))
    }
}
