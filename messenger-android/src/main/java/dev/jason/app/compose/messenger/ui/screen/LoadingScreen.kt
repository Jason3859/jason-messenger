package dev.jason.app.compose.messenger.ui.screen

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private const val PADDING_PERCENTAGE_OUTER_CIRCLE = 0.15f
private const val PADDING_PERCENTAGE_INNER_CIRCLE = 0.3f
private const val POSITION_START_OFFSET_OUTER_CIRCLE = 90f
private const val POSITION_START_OFFSET_INNER_CIRCLE = 135f

@Composable
fun LoadingScreen(
    loaded: Boolean,
    onLoaded: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "infinite transition")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000),
        ),
        label = "rotation animation"
    )
    var width by remember { mutableIntStateOf(0) }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
        modifier = modifier.fillMaxSize()
    ) {
        Text(
            text = "Logging in",
            fontSize = 20.sp
        )
        Column(
            modifier = modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f)
                .padding(bottom = 100.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom,
        ) {
            Box(
                modifier = modifier
                    .size(100.dp)
                    .onSizeChanged {
                        width = it.width
                    },
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    strokeWidth = 1.dp,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            rotationZ = rotation
                        }
                )
                CircularProgressIndicator(
                    strokeWidth = 1.dp,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            with(LocalDensity.current) {
                                (width * PADDING_PERCENTAGE_INNER_CIRCLE).toDp()
                            }
                        )
                        .graphicsLayer {
                            rotationZ = rotation + POSITION_START_OFFSET_INNER_CIRCLE
                        }
                )
                CircularProgressIndicator(
                    strokeWidth = 1.dp,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            with(LocalDensity.current) {
                                (width * PADDING_PERCENTAGE_OUTER_CIRCLE).toDp()
                            }
                        )
                        .graphicsLayer {
                            rotationZ = rotation + POSITION_START_OFFSET_OUTER_CIRCLE
                        }
                )
            }
        }
    }

    if (loaded) onLoaded()
}

@Preview(showBackground = true)
@Composable
private fun LoadingPreview() {
    LoadingScreen(false, {})
}