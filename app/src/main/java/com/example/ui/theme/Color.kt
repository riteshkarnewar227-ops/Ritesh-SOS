package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Authentic Life360 Brand Palette (intl.life360.com) - Light Mode Optimized
val Life360Purple = Color(0xFF5C24FF)           // Signature Life360 Electric Violet
val Life360PurpleDark = Color(0xFF4318FF)       // Life360 Royal Deep Purple
val Life360PurpleLight = Color(0xFF7044FF)      // Life360 Vivid Lilac
val Life360PurpleAccent = Color(0xFF8C66FF)     // Life360 Bright Violet Accent
val Life360PurpleGlow = Color(0x265C24FF)       // Life360 Glow Alpha
val Life360PurpleBg = Color(0xFFF1EDFF)         // Light Violet Badge / Pill Background

// Life360 Status Colors (High-Contrast for Crisp Light Backgrounds)
val Life360Green = Color(0xFF00A86B)            // Life360 Connected / Safe Emerald Mint
val Life360GreenDark = Color(0xFF008756)        // Deep Mint
val Life360GreenBg = Color(0xFFE6F9F2)          // Soft Green Pill Background
val Life360Red = Color(0xFFE6203D)              // Life360 SOS Coral Red
val Life360RedDark = Color(0xFFBF142D)          // Deep Coral
val Life360RedBg = Color(0xFFFFECEF)            // Soft Red Pill Background
val Life360Amber = Color(0xFFD97706)            // Life360 Warning / Speed Amber
val Life360AmberBg = Color(0xFFFEF3C7)          // Soft Amber Pill Background
val Life360Orange = Color(0xFFEA580C)           // Life360 Orange Warning
val Life360Blue = Color(0xFF0284C7)             // Life360 Police & Info Blue
val Life360BlueBg = Color(0xFFE0F2FE)           // Soft Blue Pill Background
val Life360Indigo = Color(0xFF4B23D1)           // Life360 Deep Indigo
val Life360Pink = Color(0xFFE11D48)             // Life360 Highlight Pink

// Life360 Light Canvas & Surfaces (Clean, Modern, Airy Canvas)
val Life360LightBg = Color(0xFFF7F5FC)           // Subtle Warm Lilac-White Background
val Life360LightSurface = Color(0xFFFFFFFF)      // Pure White Card Surface
val Life360LightSurfaceElevated = Color(0xFFF2EEFA) // Elevated Card / Modal Background
val Life360LightBorder = Color(0xFFE4DCF4)       // Clean Light Border
val Life360LightBorderLight = Color(0xFFD2C5EC)  // Focused Border

// Main Canvas Tokens (Light Theme First)
val Life360DarkBg = Life360LightBg               // Light Background Canvas
val Life360DarkSurface = Life360LightSurface     // Pure White Surface Card
val Life360DarkSurfaceElevated = Life360LightSurfaceElevated // Soft Elevated Card
val Life360DarkBorder = Life360LightBorder       // Light Border
val Life360DarkBorderLight = Life360LightBorderLight

// Typography Colors (Deep, Crisp & High-Contrast on White Background)
val Life360TextPrimary = Color(0xFF1B1139)       // Deep Violet Black (Maximum Readability)
val Life360TextSecondary = Color(0xFF645781)     // Refined Slate Violet
val Life360TextMuted = Color(0xFF968AA9)         // Subtle Meta Text

// Backward Compatibility Aliases
val SafetyRed = Life360Red
val SafetyRedDark = Life360RedDark
val SafetyRedBright = Color(0xFFFF3B53)
val SafetyCrimson = Color(0xFF8A001A)

val WarningAmber = Life360Amber
val WarningAmberLight = Color(0xFFF59E0B)

val SafeGreen = Life360Green
val SafeGreenDark = Life360GreenDark

val PoliceNavy = Color(0xFF0F172A)
val PoliceNavyLight = Color(0xFF1E293B)
val SlateBlue = Life360PurpleDark
val CyanAccent = Life360PurpleLight

val DarkSurface = Life360LightSurface
val DarkSurfaceCard = Life360LightSurfaceElevated
val DarkSurfaceBorder = Life360LightBorder

val TextPrimaryDark = Life360TextPrimary
val TextSecondaryDark = Life360TextSecondary
val TextTertiaryDark = Life360TextMuted

val LightBackground = Life360LightBg
val LightSurface = Life360LightSurface
val LightSurfaceVariant = Life360LightSurfaceElevated
val LightBorder = Life360LightBorder
val TextPrimaryLight = Life360TextPrimary
val TextSecondaryLight = Life360TextSecondary

