package com.example.map

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.location.UserLocation
import com.example.model.SafeZone
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalTextApi::class)
@Composable
fun OfflineMapCanvas(
    userLocation: UserLocation,
    safeZones: List<SafeZone>,
    selectedZone: SafeZone?,
    onSelectZone: (SafeZone?) -> Unit,
    onOpenGoogleMaps: (Double, Double, String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Zoom and pan state
    var zoomScale by remember { mutableFloatStateOf(12000f) } // pixels per degree
    var centerLat by remember { mutableDoubleStateOf(userLocation.latitude) }
    var centerLng by remember { mutableDoubleStateOf(userLocation.longitude) }

    // Follow user location when it first loads
    LaunchedEffect(userLocation.latitude, userLocation.longitude) {
        if (centerLat == 21.1458 && centerLng == 79.0882) {
            centerLat = userLocation.latitude
            centerLng = userLocation.longitude
        }
    }

    // Pulse animation for radar ring
    val infiniteTransition = rememberInfiniteTransition(label = "radar")
    val pulseRadius by infiniteTransition.animateFloat(
        initialValue = 10f,
        targetValue = 65f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "alpha"
    )

    val textMeasurer = rememberTextMeasurer()

    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF0F172A))
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    zoomScale = (zoomScale * zoom).coerceIn(4000f, 45000f)
                    // Convert pixel pan back to lat/lng degrees
                    centerLng -= (pan.x / zoomScale)
                    centerLat += (pan.y / zoomScale)
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val canvasCenterX = width / 2f
            val canvasCenterY = height / 2f

            // Helper to project lat/lng to screen coordinates
            fun project(lat: Double, lng: Double): Offset {
                val x = canvasCenterX + ((lng - centerLng) * zoomScale).toFloat()
                val y = canvasCenterY - ((lat - centerLat) * zoomScale).toFloat()
                return Offset(x, y)
            }

            // 1. Draw Tactical Grid & Coordinate Lines
            val gridColor = Color(0xFF1E293B)
            val gridStep = 40.dp.toPx()
            var gx = (canvasCenterX % gridStep)
            while (gx < width) {
                drawLine(
                    color = gridColor,
                    start = Offset(gx, 0f),
                    end = Offset(gx, height),
                    strokeWidth = 1f
                )
                gx += gridStep
            }
            var gy = (canvasCenterY % gridStep)
            while (gy < height) {
                drawLine(
                    color = gridColor,
                    start = Offset(0f, gy),
                    end = Offset(width, gy),
                    strokeWidth = 1f
                )
                gy += gridStep
            }

            // 2. Draw Concentric Range Rings (Offline Distance Gauge)
            val rangeColor = Color(0x3338BDF8)
            val rings = listOf(50.dp.toPx(), 100.dp.toPx(), 170.dp.toPx())
            rings.forEach { r ->
                drawCircle(
                    color = rangeColor,
                    radius = r,
                    center = project(userLocation.latitude, userLocation.longitude),
                    style = Stroke(
                        width = 1.5f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                    )
                )
            }

            val userPos = project(userLocation.latitude, userLocation.longitude)

            // 3. Draw Connecting Line to Nearest / Selected Safe Zone
            selectedZone?.let { zone ->
                val zonePos = project(zone.latitude, zone.longitude)
                drawLine(
                    color = Color(0xFFF59E0B),
                    start = userPos,
                    end = zonePos,
                    strokeWidth = 2.5f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 10f), 0f)
                )
            }

            // 4. Draw Safe Zones & Police Stations
            safeZones.forEach { zone ->
                val pos = project(zone.latitude, zone.longitude)
                val isSelected = selectedZone?.id == zone.id

                val markerColor = when (zone.category) {
                    "Police Station" -> Color(0xFFEF4444) // Red
                    "Hospital" -> Color(0xFF10B981) // Green
                    "Metro Station" -> Color(0xFF3B82F6) // Blue
                    else -> Color(0xFFF59E0B) // Amber
                }

                // Marker outer glow if selected
                if (isSelected) {
                    drawCircle(
                        color = markerColor.copy(alpha = 0.35f),
                        radius = 24.dp.toPx(),
                        center = pos
                    )
                }

                // Marker Base
                drawCircle(
                    color = Color(0xFF0F172A),
                    radius = 12.dp.toPx(),
                    center = pos
                )
                drawCircle(
                    color = markerColor,
                    radius = 9.dp.toPx(),
                    center = pos
                )
                drawCircle(
                    color = Color.White,
                    radius = 4.dp.toPx(),
                    center = pos
                )

                // Label for safe zone
                val labelText = zone.name.replace("Police Station", "PS").take(18)
                val textLayout = textMeasurer.measure(
                    text = AnnotatedString(labelText),
                    style = TextStyle(
                        color = if (isSelected) Color(0xFFFDE047) else Color(0xFFE2E8F0),
                        fontSize = 10.sp,
                        background = Color(0xCC0F172A)
                    )
                )
                drawText(
                    textLayoutResult = textLayout,
                    topLeft = Offset(pos.x - (textLayout.size.width / 2f), pos.y + 14.dp.toPx())
                )
            }

            // 5. Draw User Location Radar & Beacon
            // Pulsing radar ring
            drawCircle(
                color = Color(0xFF38BDF8).copy(alpha = pulseAlpha),
                radius = pulseRadius * (zoomScale / 10000f).coerceIn(0.8f, 2.5f),
                center = userPos
            )
            // Outer halo
            drawCircle(
                color = Color(0x550284C7),
                radius = 16.dp.toPx(),
                center = userPos
            )
            // Core beacon
            drawCircle(
                color = Color(0xFF0284C7),
                radius = 9.dp.toPx(),
                center = userPos
            )
            drawCircle(
                color = Color.White,
                radius = 4.dp.toPx(),
                center = userPos
            )

            // Accuracy label
            val accText = "GPS ±${userLocation.accuracy.toInt()}m"
            val accLayout = textMeasurer.measure(
                text = AnnotatedString(accText),
                style = TextStyle(
                    color = Color(0xFF38BDF8),
                    fontSize = 9.sp,
                    background = Color(0xBB0B132B)
                )
            )
            drawText(
                textLayoutResult = accLayout,
                topLeft = Offset(userPos.x - (accLayout.size.width / 2f), userPos.y - 20.dp.toPx())
            )
        }

        // Top Status Header Overlay
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(12.dp)
                .background(Color(0xD90F172A), RoundedCornerShape(10.dp))
                .border(1.dp, Color(0xFF334155), RoundedCornerShape(10.dp))
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(Color(0xFF22C55E), CircleShape)
            )
            Text(
                text = "OFFLINE TACTICAL MAP • NAGPUR",
                color = Color(0xFFF1F5F9),
                style = MaterialTheme.typography.labelSmall,
                fontSize = 10.sp
            )
        }

        // Floating Map Controls (Right Side)
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Recenter on User
            SmallFloatingActionButton(
                onClick = {
                    centerLat = userLocation.latitude
                    centerLng = userLocation.longitude
                    zoomScale = 14000f
                },
                containerColor = Color(0xFF1E293B),
                contentColor = Color(0xFF38BDF8)
            ) {
                Icon(Icons.Default.MyLocation, contentDescription = "Center on me")
            }

            // Zoom In
            SmallFloatingActionButton(
                onClick = { zoomScale = (zoomScale * 1.35f).coerceAtMost(45000f) },
                containerColor = Color(0xFF1E293B),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Zoom in")
            }

            // Zoom Out
            SmallFloatingActionButton(
                onClick = { zoomScale = (zoomScale / 1.35f).coerceAtLeast(4000f) },
                containerColor = Color(0xFF1E293B),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Remove, contentDescription = "Zoom out")
            }

            // Open in Google Maps directly
            SmallFloatingActionButton(
                onClick = {
                    onOpenGoogleMaps(
                        userLocation.latitude,
                        userLocation.longitude,
                        "My Live Location"
                    )
                },
                containerColor = Color(0xFFDC2626),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Map, contentDescription = "Open in Google Maps")
            }
        }

        // Bottom Map Legend
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(12.dp)
                .background(Color(0xD90F172A), RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LegendItem(color = Color(0xFFEF4444), label = "Police")
            LegendItem(color = Color(0xFF10B981), label = "Hospital")
            LegendItem(color = Color(0xFF38BDF8), label = "You")
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .background(color, CircleShape)
        )
        Text(
            text = label,
            color = Color(0xFF94A3B8),
            fontSize = 9.sp
        )
    }
}
