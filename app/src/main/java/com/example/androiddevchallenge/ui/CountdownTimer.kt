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

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.focus.isFocused
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.isDigitsOnly
import com.example.androiddevchallenge.ui.theme.MyTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch

val KeyboardShownKey = SemanticsPropertyKey<Boolean>("KeyboardShownKey")
var SemanticsPropertyReceiver.keyboardShownProperty by KeyboardShownKey

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
    val fontSize = 50.sp
    LaunchedEffect(isFocus) {
        if (isFocus) {
            textOffset.animateTo(-100.dp.value, animationSpec = tween(500))
        } else {
            textOffset.animateTo(0.dp.value, animationSpec = tween(500))
        }
    }
    LaunchedEffect(isStart) {
        if (isStart) {
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
    Box(Modifier.fillMaxSize().offset(y = textOffset.value.dp), contentAlignment = Alignment.Center) {
        Box(modifier = Modifier.size(300.dp), contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawRoundRect(
                    Color(0x1E7171),
                    cornerRadius = CornerRadius(100f, 100f),
                    style = Stroke(width = 20.dp.value)
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
                            if (currentNumStr == "Compose\uD83C\uDF89️") {
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
                    .padding(30.dp)
            ) {
                Text("Countdown Start!")
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