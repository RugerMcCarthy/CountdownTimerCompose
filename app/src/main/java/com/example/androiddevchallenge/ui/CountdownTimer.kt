/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.isFocused
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.isDigitsOnly
import com.example.androiddevchallenge.R
import com.example.androiddevchallenge.ui.theme.MyTheme
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch

@Composable
fun CountdownTimerLayout() {
    var isStart: Boolean by remember { mutableStateOf(false) }
    var currentNum by remember { mutableStateOf(5) }
    var currentNumStr by remember { mutableStateOf("5") }
    var textAlpha = remember { Animatable(1f) }
    var textOffset = remember { Animatable(0f) }
    var textEditEnabled by remember { mutableStateOf(true) }
    var startBtnEnabled by remember { mutableStateOf(true) }
    var textScale = remember { Animatable(1f) }
    var isFocus by remember { mutableStateOf(false) }
    var sweepAngle = remember { Animatable(0f) }
    var fontSize by remember { mutableStateOf(50.sp) }
    LaunchedEffect(isFocus) {
        if (isFocus) {
            textOffset.animateTo(-100.dp.value, animationSpec = tween(500))
        } else {
            textOffset.animateTo(0.dp.value, animationSpec = tween(500))
        }
    }
    LaunchedEffect(isStart) {
        if (isStart) {
            launch {
                sweepAngle.animateTo(360f, animationSpec = tween(currentNum * 1000))
            }
            while (currentNum >= 1) {
                var fadeOutTextScale = launch {
                    textScale.animateTo(2f, animationSpec = tween(500))
                }
                var fadeOutTextAlpha = launch {
                    textAlpha.animateTo(1f, animationSpec = tween(500))
                }
                var fadeOutAnim = listOf(fadeOutTextScale, fadeOutTextAlpha)
                fadeOutAnim.joinAll()
                var fadeInTextScale = launch {
                    textScale.animateTo(if (currentNum != 1) 0f else 1f, animationSpec = tween(500))
                }
                var fadeInTextAlpha = launch {
                    textAlpha.animateTo(if (currentNum != 1) 0f else 1f, animationSpec = tween(500))
                }
                var fadeInAnim = listOf(fadeInTextScale, fadeInTextAlpha)
                fadeInAnim.joinAll()
                currentNum--
                currentNumStr = currentNum.toString()
                when (currentNum) {
                    1 -> {
                        fontSize = 30.sp
                        currentNumStr = "Compose🎉️"
                    }
                }
            }
            currentNum = 1
            currentNumStr = "Compose🎉️"
            startBtnEnabled = true
            textEditEnabled = true
            isStart = false
        }
    }
    Box(
        Modifier
            .fillMaxSize()
            .offset(y = textOffset.value.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(modifier = Modifier.size(350.dp), contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    color = Color(0xFF1E7171),
                    center = Offset(drawContext.size.width / 2f, drawContext.size.height / 2f),
                    style = Stroke(width = 50.dp.value)
                )
                drawArc(
                    color = Color(0xFF3BDCCE),
                    startAngle = -90f,
                    sweepAngle = sweepAngle.value,
                    useCenter = false,
                    style = Stroke(width = 50.dp.value, cap = StrokeCap.Round)
                )
            }
            BasicTextField(
                value = "$currentNumStr",
                onValueChange = {
                    if ((it.isDigitsOnly() || it.isEmpty()) && it.length <= 4) {
                        currentNumStr = it
                        currentNum = it.toIntOrNull() ?: 1
                    }
                },
                textStyle = TextStyle(
                    fontSize = fontSize,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = Color.White
                ),
                modifier = Modifier
                    .graphicsLayer(
                        scaleX = textScale.value,
                        scaleY = textScale.value,
                        alpha = textAlpha.value,
                    )
                    .onFocusChanged {
                        isFocus = it.isFocused
                        if (it.isFocused) {
                            GlobalScope.launch {
                                sweepAngle.animateTo(0f, tween(1000))
                            }
                            if (currentNumStr == "Compose\uD83C\uDF89️") {
                                fontSize = 50.sp
                                currentNumStr = "1"
                            }
                        } else {
                            if (currentNumStr.isEmpty()) {
                                currentNumStr = "1"
                            }
                        }
                    },
                enabled = textEditEnabled
            )
            Button(
                onClick = {
                    isStart = !isStart
                    startBtnEnabled = false
                    textEditEnabled = false
                },
                enabled = startBtnEnabled,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(70.dp)
                    .size(50.dp)
                    .clip(RoundedCornerShape(25.dp))
            ) {
                if (!isStart) {
                    Image(
                        painter = painterResource(id = R.drawable.countdown_start),
                        contentDescription = "Start",
                        modifier = Modifier.padding(start = 3.dp)
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.countdown_stop),
                        contentDescription = "Top"
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun CountdownTimerPreview() {
    MyTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text("Countdown Timer")
                    }
                )
            },
            bottomBar = {
                CountdownTimerLayout()
            }
        ) {
        }
    }
}
