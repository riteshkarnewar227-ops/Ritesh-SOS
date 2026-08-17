package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.emergency.AudioRecordingState
import com.example.ui.theme.*

@Composable
fun AudioDistressCard(
    audioState: AudioRecordingState,
    onStartRecording: () -> Unit,
    onStopRecording: () -> Unit,
    onPlayRecording: () -> Unit,
    onStopPlayback: () -> Unit,
    onShareWhatsApp: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isRecording = audioState.isRecording
    val isPlaying = audioState.isPlaying
    val hasRecording = !audioState.lastRecordedFilePath.isNullOrBlank()

    // Pulse animation for recording badge
    val infiniteTransition = rememberInfiniteTransition(label = "audio_pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(
                1.dp,
                if (isRecording) Life360Red else Life360DarkBorder,
                RoundedCornerShape(20.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = if (isRecording) Color(0xFF260D1A) else Life360DarkSurface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(
                                if (isRecording) Life360Red.copy(alpha = pulseAlpha) else Life360PurpleDark.copy(alpha = 0.3f),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isRecording) Icons.Default.Mic else Icons.Default.MicNone,
                            contentDescription = null,
                            tint = if (isRecording) Color.White else Life360PurpleLight,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Column {
                        Text(
                            text = if (isRecording) "🔴 LIVE AUDIO DISTRESS RECORDING" else "DISTRESS AUDIO EVIDENCE",
                            color = if (isRecording) Life360Red else Life360TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = if (isRecording) "Capturing audio evidence for Parent & Police..." else "Auto-captures during SOS or record manually",
                            color = Life360TextSecondary,
                            fontSize = 10.sp
                        )
                    }
                }

                if (isRecording) {
                    val minutes = audioState.recordingDurationSeconds / 60
                    val seconds = audioState.recordingDurationSeconds % 60
                    Surface(
                        color = Life360Red,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = String.format("%02d:%02d", minutes, seconds),
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Audio Waveform Visualization
            if (isRecording) {
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp)
                        .background(Color(0xFFFFEEEE), RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    val barCount = 20
                    val barWidth = size.width / (barCount * 1.5f)
                    val spacing = barWidth * 0.5f

                    for (i in 0 until barCount) {
                        val factor = ((i * 7 + (audioState.recordingDurationSeconds * 3)) % 10) / 10f
                        val barHeight = (size.height * 0.2f) + (size.height * 0.7f * factor * audioState.amplitude.coerceIn(0.3f, 1f))
                        val left = i * (barWidth + spacing)
                        val top = (size.height - barHeight) / 2f

                        drawRoundRect(
                            color = Life360Red,
                            topLeft = Offset(left, top),
                            size = Size(barWidth, barHeight),
                            cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
                        )
                    }
                }
            }

            // Controls Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isRecording) {
                    Button(
                        onClick = onStopRecording,
                        colors = ButtonDefaults.buttonColors(containerColor = Life360Red),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .testTag("stop_audio_recording_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Stop,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Stop & Save Audio", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    OutlinedButton(
                        onClick = onStartRecording,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Life360Purple),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Life360Purple),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .testTag("start_audio_recording_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = null,
                            tint = Life360Purple,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Record Distress Note", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                if (hasRecording && !isRecording) {
                    // Play / Stop Playback Button
                    Button(
                        onClick = {
                            if (isPlaying) onStopPlayback() else onPlayRecording()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Life360PurpleBg),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .height(40.dp)
                            .testTag("play_audio_recording_button")
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = if (isPlaying) Life360Amber else Life360PurpleDark,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isPlaying) "Playing" else "Preview",
                            color = Life360PurpleDark,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Share on WhatsApp Button
                    IconButton(
                        onClick = onShareWhatsApp,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Life360Green)
                            .testTag("share_audio_whatsapp_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share Audio",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

