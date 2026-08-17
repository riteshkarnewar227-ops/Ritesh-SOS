package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SafetyCrimson
import com.example.ui.theme.SafetyRed
import com.example.ui.theme.SafetyRedBright

@Composable
fun SosPulsingButton(
    isCountingDown: Boolean,
    countdownSeconds: Int,
    isSosActive: Boolean,
    onTriggerSos: () -> Unit,
    onCancelCountdown: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "sos_pulse")

    val pulse1 by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse1"
    )
    val alpha1 by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "alpha1"
    )

    val pulse2 by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.55f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, delayMillis = 300, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse2"
    )
    val alpha2 by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, delayMillis = 300, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "alpha2"
    )

    Box(
        modifier = modifier
            .size(240.dp)
            .testTag("sos_button_container"),
        contentAlignment = Alignment.Center
    ) {
        // Outer pulsing wave rings
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2)
            val baseRadius = (size.minDimension / 2) * 0.65f

            // Outer wave 2
            drawCircle(
                color = SafetyRedBright.copy(alpha = alpha2),
                radius = baseRadius * pulse2,
                center = center
            )

            // Outer wave 1
            drawCircle(
                color = SafetyRed.copy(alpha = alpha1),
                radius = baseRadius * pulse1,
                center = center
            )

            // Outer border glow
            drawCircle(
                color = SafetyRedBright.copy(alpha = 0.3f),
                radius = baseRadius * 1.08f,
                center = center,
                style = Stroke(width = 3.dp.toPx())
            )
        }

        // Central Action Button
        val buttonGradient = if (isCountingDown || isSosActive) {
            Brush.radialGradient(
                listOf(Color(0xFFFF0055), SafetyCrimson, Color(0xFF370000))
            )
        } else {
            Brush.radialGradient(
                listOf(SafetyRedBright, SafetyRed, SafetyCrimson)
            )
        }

        Box(
            modifier = Modifier
                .size(160.dp)
                .shadow(24.dp, shape = CircleShape, ambientColor = SafetyRed, spotColor = SafetyRedBright)
                .clip(CircleShape)
                .background(buttonGradient)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    if (isCountingDown) {
                        onCancelCountdown()
                    } else {
                        onTriggerSos()
                    }
                }
                .testTag("sos_main_button"),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(12.dp)
            ) {
                if (isCountingDown) {
                    Text(
                        text = "$countdownSeconds",
                        color = Color.White,
                        fontSize = 44.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "TAP TO CANCEL",
                        color = Color(0xFFFFD8D8),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                } else if (isSosActive) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "SOS Active",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                    Text(
                        text = "SOS ACTIVE",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Broadcasting...",
                        color = Color(0xFFFFD8D8),
                        fontSize = 10.sp
                    )
                } else {
                    Text(
                        text = "EMERGENCY",
                        color = Color(0xFFFFD8D8),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "SOS",
                        color = Color.White,
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "SEND WHATSAPP",
                        color = Color(0xFFFFE0E0),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}
