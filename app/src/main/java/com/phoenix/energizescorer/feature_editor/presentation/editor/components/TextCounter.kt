package com.phoenix.energizescorer.feature_editor.presentation.editor.components

import android.view.HapticFeedbackConstants
import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.phoenix.energizescorer.R
import com.phoenix.energizescorer.feature_editor.presentation.util.MeasureView


@Composable
fun TextCounter (
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(top = 8.dp, start = 16.dp, end = 16.dp),
    text: String,
    textStyle: TextStyle = MaterialTheme.typography.titleLarge,
    counter: Int,
    enabled: Boolean,
    onClick: (add: Int) -> Unit
) {
    Measure(
        modifier = modifier.padding(paddingValues),
        counter = counter,
        textStyle = textStyle,
        text = text,
        enabled = enabled
    ) { modifier1, modifier2 ->
        Text(modifier = modifier1, text = text, style = textStyle)
        Counter(
            modifier = modifier2,
            counter = counter,
            textStyle = textStyle,
            onClick = onClick,
            enabled = enabled,
        )
    }
}

@Composable
private fun Measure (
    modifier: Modifier,
    counter: Int,
    textStyle: TextStyle,
    text: String,
    enabled: Boolean,
    content: @Composable (modifier1: Modifier, modifier2: Modifier) -> Unit,
) {
    MeasureView(
        modifier = modifier,
        viewToMeasure = {
            Row {
                Text(modifier = Modifier.width(IntrinsicSize.Min), text = text, style = textStyle)
                Counter(
                    counter = counter,
                    textStyle = textStyle,
                    onClick = {},
                    enabled = enabled
                )
            }
        },
    ) { maxWidth, measuredWidth, _ ->
        val vertical = maxWidth < measuredWidth

        if (vertical) {
            Column(Modifier.fillMaxWidth()) {
                content(Modifier, Modifier.align(Alignment.End))
            }
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                content(Modifier.weight(1f), Modifier)
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
internal fun Counter (
    modifier: Modifier = Modifier,
    counter: Int,
    textStyle: TextStyle,
    onClick: (add: Int) -> Unit,
    enabled: Boolean,
) {
    val minus = painterResource(id = R.drawable.ic_baseline_minus_24)
    val view = LocalView.current

    Row {
        Text (
            modifier = modifier
                .widthIn(max = 66.dp)
                .height(50.dp)
                .fillMaxWidth()
                .wrapContentHeight(),
            text = "$counter",
            style = textStyle.copy(fontFeatureSettings = "tnum"),
            textAlign = TextAlign.Center
        )

        AnimatedContent(
            targetState = enabled,
            transitionSpec = {
                (slideInHorizontally(initialOffsetX = { it }) with
                        slideOutHorizontally(targetOffsetX = { it }))
                    .using(
                        SizeTransform(
                            clip = false,
                            sizeAnimationSpec = { _, _ ->
                                spring(
                                    stiffness = Spring.StiffnessMediumLow,
                                    visibilityThreshold = IntSize.VisibilityThreshold
                                )
                            }
                        )
                    )
            }
        ) { targetState ->
            if (targetState) {
                Row {
                    Box(
                        modifier = Modifier
                            .widthIn(max = 68.dp)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        FilledIconButton(
                            modifier = Modifier.size(50.dp),
                            onClick = {
                                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                onClick(-1)
                            },
                            enabled = counter > 0
                        ) {
                            Icon(minus , contentDescription = "Minus")
                        }
                    }
                    Box(
                        modifier = Modifier
                            .widthIn(max = 66.dp)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        FilledIconButton(
                            modifier = Modifier.size(50.dp),
                            onClick = {
                                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                onClick(1)
                            }
                        ) {
                            Icon(Icons.Filled.Add, contentDescription = "Add")
                        }
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(50.dp))
            }
        }
    }
}