package com.luahub.injector.ui

import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color

val NeonCyan = Color(0xFF00E5FF)
val NeonRed = Color(0xFFFF1744)
val CyberDark = Color(0xFF0B0E14)
val CyberCard = Color(0xFF161C26)
val TextWhite = Color(0xFFF5F5F5)

val LuahubColorScheme = darkColorScheme(
    primary = NeonCyan,
    background = CyberDark,
    surface = CyberCard,
    error = NeonRed,
    onPrimary = Color.Black,
    onBackground = TextWhite,
    onSurface = TextWhite
)
