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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.isDigitsOnly
import kotlinx.coroutines.CoroutineScope

@Composable
fun CountdownTimerLayout() {
    var isStart: Boolean by remember { mutableStateOf(false) }
    var currentNum by remember { mutableStateOf(5) }
    var currentNumStr by remember { mutableStateOf("5") }
    val textAlpha by remember { mutableStateOf(1f) }
    var textEditEnabled by remember { mutableStateOf(true) }
    var startBtnEnabled by remember { mutableStateOf(true) }
    val textScale = remember { Animatable(1f) }
    lateinit var coroutineScope: CoroutineScope
    val fontSize = 50.sp
    LaunchedEffect(isStart) {
        if (isStart) {
            while (currentNum >= 1) {
                textScale.animateTo(2f, animationSpec = tween(500))
                textScale.animateTo(if (currentNum != 1) 0f else 1f, animationSpec = tween(500))
                currentNum--
                currentNumStr = currentNum.toString()
                when (currentNum) {
                    1 -> {
                        currentNumStr = currentNumStr + "🎉️"
                    }
                }
            }
            currentNum = 1
            currentNumStr = currentNum.toString()
            startBtnEnabled = true
            textEditEnabled = true
            isStart = false
        }
    }
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Box(modifier = Modifier.size(300.dp), contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawRoundRect(
                    Color.Black,
                    cornerRadius = CornerRadius(100f, 100f),
                    style = Stroke(width = 20.dp.value)
                )
            }
            BasicTextField(
                value = "$currentNumStr",
                onValueChange = {
                    if ((it.isDigitsOnly() || it.isEmpty())&& it.length <= 4) {
                        currentNumStr = it
                        currentNum = it.toIntOrNull() ?: 1
                    }
                },
                textStyle = TextStyle(
                    fontSize = fontSize,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.graphicsLayer(
                    scaleX = textScale.value,
                    scaleY = textScale.value,
                    alpha = textAlpha
                ),
                enabled = textEditEnabled
            )
            Button(
                onClick = {
                    isStart = !isStart
                    startBtnEnabled = false
                    textEditEnabled = false
                },
                enabled = startBtnEnabled,
                modifier = Modifier.align(Alignment.BottomCenter).padding(30.dp)
            ) {
                Text("Countdown Start!")
            }
        }
    }
}
