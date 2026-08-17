package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.location.UserLocation
import com.example.model.SafeZone
import com.example.ui.theme.*

@Composable
fun Life360MapCard(
    userLocation: UserLocation,
    safeZones: List<SafeZone> = emptyList(),
    onOpenFullMap: () -> Unit,
    onOpenGoogleMaps: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "map_radar_pulse")
    val radarPulse by infiniteTransition.animateFloat(
        initialValue = 10f,
        targetValue = 65f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radar_pulse"
    )
    val radarAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radar_alpha"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .border(1.dp, Life360DarkBorder, RoundedCornerShape(24.dp))
            .testTag("life360_map_card"),
        colors = CardDefaults.cardColors(
            containerColor = Life360DarkSurface
        )
    ) {
        Column {
            // Simulated Vector Map View with radar and pins
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color(0xFF0F1527))
                    .clickable { onOpenFullMap() }
            ) {
                // Vector grid lines & stylized streets
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    // Grid streets
                    val streetColor = Color(0xFF1E2846)
                    val highwayColor = Color(0xFF2E3D66)

                    drawLine(streetColor, Offset(0f, h * 0.35f), Offset(w, h * 0.35f), strokeWidth = 6f)
                    drawLine(highwayColor, Offset(0f, h * 0.65f), Offset(w, h * 0.65f), strokeWidth = 10f)
                    drawLine(streetColor, Offset(w * 0.3f, 0f), Offset(w * 0.3f, h), strokeWidth = 6f)
                    drawLine(highwayColor, Offset(w * 0.7f, 0f), Offset(w * 0.7f, h), strokeWidth = 10f)

                    // Diagonal ring road (Nagpur Wardha/Amravati Rd simulation)
                    val path = Path().apply {
                        moveTo(0f, h * 0.8f)
                        cubicTo(w * 0.4f, h * 0.7f, w * 0.6f, h * 0.2f, w, h * 0.1f)
                    }
                    drawPath(path, color = Color(0xFF3F51B5).copy(alpha = 0.4f), style = Stroke(width = 8f))

                    // Safe zones pins representation
                    drawCircle(color = Color(0xFF1E90FF), radius = 8f, center = Offset(w * 0.25f, h * 0.4f))
                    drawCircle(color = Color(0xFF00D287), radius = 8f, center = Offset(w * 0.8f, h * 0.3f))
                    drawCircle(color = Color(0xFFFF4757), radius = 8f, center = Offset(w * 0.75f, h * 0.75f))

                    // Center User Pin Radar Pulse
                    val center = Offset(w * 0.5f, h * 0.52f)
                    drawCircle(
                        color = Life360Purple.copy(alpha = radarAlpha),
                        radius = radarPulse,
                        center = center
                    )
                    drawCircle(
                        color = Life360Purple,
                        radius = 12f,
                        center = center
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 5f,
                        center = center
                    )
                }

                // Top Left Live Status Pill
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Life360DarkBg.copy(alpha = 0.85f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Life360DarkBorder),
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopStart)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(Life360Green, CircleShape)
                        )
                        Text(
                            text = "LIVE LOCATION",
                            color = Life360Green,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                // Top Right Map Actions (Fullscreen & Google Maps)
                Row(
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopEnd),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    IconButton(
                        onClick = onOpenGoogleMaps,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Life360DarkBg.copy(alpha = 0.85f))
                            .border(1.dp, Life360DarkBorder, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.OpenInNew,
                            contentDescription = "Open in Google Maps",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    IconButton(
                        onClick = onOpenFullMap,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Life360DarkBg.copy(alpha = 0.85f))
                            .border(1.dp, Life360DarkBorder, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Layers,
                            contentDescription = "Full Map & Safe Places",
                            tint = Life360PurpleLight,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Location Info & Driving/Speed Telemetry Footer (Life360 Style)
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = userLocation.address,
                            color = Life360TextPrimary,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${userLocation.toFormattedCoordinates()} • Accuracy ±${userLocation.accuracy.toInt()}m",
                            color = Life360TextSecondary,
                            fontSize = 11.sp
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Life360PurpleBg,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Life360Purple.copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = "0 km/h • Still",
                            color = Life360PurpleDark,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}
