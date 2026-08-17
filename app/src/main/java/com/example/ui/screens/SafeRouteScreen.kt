package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.theme.*

@Composable
fun SafeRouteScreen(
    selectedRoute: SafeRouteAnalysis,
    allRoutes: List<SafeRouteAnalysis>,
    isNightMode: Boolean,
    onSelectRoute: (SafeRouteAnalysis) -> Unit,
    onToggleNightMode: (Boolean) -> Unit,
    onAnalyzeCustomRoute: (String, String) -> Unit,
    onStartNavigation: (String, String) -> Unit,
    onShareRoutePlan: (SafeRouteAnalysis, Boolean) -> Unit,
    onCallNumber: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showCustomSearch by remember { mutableStateOf(false) }
    var customOrigin by remember { mutableStateOf("") }
    var customDestination by remember { mutableStateOf("") }

    val safetyScore = if (isNightMode) selectedRoute.nightSafetyScore else selectedRoute.daySafetyScore
    val scoreColor by animateColorAsState(
        targetValue = when {
            safetyScore >= 85 -> Life360Green
            safetyScore >= 70 -> Life360Amber
            else -> Life360Red
        },
        label = "scoreColor"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Life360LightBg)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, Life360LightBorder, RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = Life360LightSurface)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .background(Life360PurpleBg, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AltRoute,
                                    contentDescription = null,
                                    tint = Life360Purple,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "SAFE ROUTE ADVISOR",
                                    color = Life360TextPrimary,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = "Safety Score, Hazards & Safe Havens",
                                    color = Life360TextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        // Day / Night Toggle Pill
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (isNightMode) Life360Indigo.copy(alpha = 0.12f) else Life360AmberBg,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isNightMode) Life360Indigo.copy(alpha = 0.4f) else Life360Amber.copy(alpha = 0.4f)
                            ),
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .clickable { onToggleNightMode(!isNightMode) }
                                .testTag("toggle_night_route")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = if (isNightMode) Icons.Default.NightlightRound else Icons.Default.WbSunny,
                                    contentDescription = null,
                                    tint = if (isNightMode) Life360Indigo else Life360Amber,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = if (isNightMode) "NIGHT (7PM-6AM)" else "DAY (6AM-7PM)",
                                    color = if (isNightMode) Life360Indigo else Life360Amber,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Text(
                        text = "Real-time municipal safety intelligence evaluates street lighting, police PCR patrols, blind spots, traffic bottlenecks, and 24x7 emergency safe havens.",
                        color = Life360TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )
                }
            }
        }

        // Popular Route Selector & Custom Search Button
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "POPULAR COMMUTER CORRIDORS",
                        color = Life360TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )

                    TextButton(
                        onClick = { showCustomSearch = !showCustomSearch },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Icon(
                            imageVector = if (showCustomSearch) Icons.Default.Close else Icons.Default.Search,
                            contentDescription = null,
                            tint = Life360Purple,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (showCustomSearch) "Hide Search" else "Custom Route",
                            color = Life360Purple,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Custom Route Search Box
                AnimatedVisibility(visible = showCustomSearch) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.dp, Life360Purple.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
                        colors = CardDefaults.cardColors(containerColor = Life360PurpleBg)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "Analyze Custom Route Safety:",
                                color = Life360TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )

                            OutlinedTextField(
                                value = customOrigin,
                                onValueChange = { customOrigin = it },
                                label = { Text("Starting Location / Origin") },
                                placeholder = { Text("e.g. Ramdaspeth, Wardha Road") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.TripOrigin,
                                        contentDescription = null,
                                        tint = Life360Green
                                    )
                                },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    focusedBorderColor = Life360Purple,
                                    unfocusedBorderColor = Life360LightBorder
                                )
                            )

                            OutlinedTextField(
                                value = customDestination,
                                onValueChange = { customDestination = it },
                                label = { Text("Destination Point") },
                                placeholder = { Text("e.g. VNIT Campus, Airport") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = Life360Red
                                    )
                                },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    focusedBorderColor = Life360Purple,
                                    unfocusedBorderColor = Life360LightBorder
                                ),
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                keyboardActions = KeyboardActions(onDone = {
                                    if (customOrigin.isNotBlank() && customDestination.isNotBlank()) {
                                        onAnalyzeCustomRoute(customOrigin, customDestination)
                                        showCustomSearch = false
                                    }
                                })
                            )

                            Button(
                                onClick = {
                                    if (customOrigin.isNotBlank() && customDestination.isNotBlank()) {
                                        onAnalyzeCustomRoute(customOrigin, customDestination)
                                        showCustomSearch = false
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Life360Purple),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Audit Route Safety & Challenges", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Preset Route Chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 2.dp)
                ) {
                    items(allRoutes) { route ->
                        val isSelected = route.id == selectedRoute.id
                        val chipScore = if (isNightMode) route.nightSafetyScore else route.daySafetyScore

                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (isSelected) Life360Purple else Life360LightSurface,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) Life360Purple else Life360LightBorder
                            ),
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { onSelectRoute(route) }
                                .testTag("route_chip_${route.id}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(
                                            if (isSelected) Color.White else if (chipScore >= 85) Life360Green else Life360Amber,
                                            CircleShape
                                        )
                                )
                                Column {
                                    Text(
                                        text = route.title.take(28) + if (route.title.length > 28) "…" else "",
                                        color = if (isSelected) Color.White else Life360TextPrimary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${route.distanceKm} km • Score: $chipScore%",
                                        color = if (isSelected) Color.White.copy(alpha = 0.85f) else Life360TextSecondary,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Active Route Safety Score & Overview Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, Life360LightBorder, RoundedCornerShape(20.dp))
                    .testTag("route_score_card"),
                colors = CardDefaults.cardColors(containerColor = Life360LightSurface)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = selectedRoute.title,
                                color = Life360TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                lineHeight = 22.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DirectionsCar,
                                    contentDescription = null,
                                    tint = Life360Purple,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "${selectedRoute.distanceKm} km • ~${selectedRoute.estimatedDurationMinutes} mins drive / transit",
                                    color = Life360TextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        // Circular Safety Score Dial
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(
                                    when {
                                        safetyScore >= 85 -> Life360GreenBg
                                        safetyScore >= 70 -> Life360AmberBg
                                        else -> Life360RedBg
                                    },
                                    CircleShape
                                )
                                .border(
                                    2.dp,
                                    scoreColor,
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "$safetyScore%",
                                    color = scoreColor,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    text = "SAFETY",
                                    color = scoreColor,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Divider(color = Life360LightBorder, thickness = 1.dp)

                    // Origin and Destination Row
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.TripOrigin,
                                contentDescription = null,
                                tint = Life360Green,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "From: ${selectedRoute.origin}",
                                color = Life360TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = Life360Red,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "To: ${selectedRoute.destination}",
                                color = Life360TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Key Security Metrics Grid
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Life360LightBg, RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        MetricPill(
                            icon = Icons.Default.LightMode,
                            label = "Street Lighting",
                            value = selectedRoute.streetLightingQuality.take(18) + "…"
                        )
                        MetricPill(
                            icon = Icons.Default.LocalPolice,
                            label = "Police Patrol",
                            value = selectedRoute.policePatrolFrequency.take(18) + "…"
                        )
                        MetricPill(
                            icon = Icons.Default.Videocam,
                            label = "CCTV Surveillance",
                            value = selectedRoute.cctvCoverage.take(18) + "…"
                        )
                    }

                    // Action Buttons (Start Google Maps & Share Safety Plan)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { onStartNavigation(selectedRoute.origin, selectedRoute.destination) },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Life360Purple),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Navigation,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Navigate Route", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { onShareRoutePlan(selectedRoute, isNightMode) },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF25D366)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF25D366)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = null,
                                tint = Color(0xFF25D366),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Share with Circle", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Section: Challenges & Hazards You May Face
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = Life360Amber,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "CHALLENGES & RISKS ON THIS ROUTE",
                            color = Life360TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Life360RedBg
                    ) {
                        Text(
                            text = "${selectedRoute.challenges.size} Identified",
                            color = Life360Red,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                if (selectedRoute.challenges.isEmpty()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp)),
                        colors = CardDefaults.cardColors(containerColor = Life360GreenBg)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Life360Green,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = "Zero high-risk hazard zones detected along this corridor. Optimal illuminated route.",
                                color = Life360GreenDark,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        selectedRoute.challenges.forEach { challenge ->
                            ChallengeCard(challenge = challenge, isNight = isNightMode)
                        }
                    }
                }
            }
        }

        // Section: Emergency Safe Havens along Corridor
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalHospital,
                            contentDescription = null,
                            tint = Life360Blue,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "24x7 SAFE HAVENS ALONG CORRIDOR",
                            color = Life360TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        )
                    }

                    Text(
                        text = "Immediate Refuge",
                        color = Life360TextSecondary,
                        fontSize = 11.sp
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    selectedRoute.safeHavens.forEach { haven ->
                        SafeHavenCard(haven = haven, onCall = { onCallNumber(haven.contactNumber) })
                    }
                }
            }
        }

        // Section: Route Safety Guidance
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, Life360LightBorder, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = Life360LightSurface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = Life360Purple,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "LIFE360 ROUTE ADVICE & PROTOCOL",
                            color = Life360TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    selectedRoute.safetyTips.forEach { tip ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text("•", color = Life360Purple, fontWeight = FontWeight.Black, fontSize = 14.sp)
                            Text(
                                text = tip,
                                color = Life360TextSecondary,
                                fontSize = 12.sp,
                                lineHeight = 17.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MetricPill(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Column(
        modifier = Modifier.width(100.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Life360Purple,
                modifier = Modifier.size(12.dp)
            )
            Text(
                text = label,
                color = Life360TextSecondary,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            text = value,
            color = Life360TextPrimary,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun ChallengeCard(
    challenge: RouteChallenge,
    isNight: Boolean
) {
    val badgeBg = when (challenge.severity) {
        ChallengeSeverity.HIGH -> Life360RedBg
        ChallengeSeverity.MEDIUM -> Life360AmberBg
        ChallengeSeverity.LOW -> Life360BlueBg
    }
    val badgeColor = when (challenge.severity) {
        ChallengeSeverity.HIGH -> Life360Red
        ChallengeSeverity.MEDIUM -> Life360Amber
        ChallengeSeverity.LOW -> Life360Blue
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, badgeColor.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
            .testTag("challenge_card_${challenge.id}"),
        colors = CardDefaults.cardColors(containerColor = Life360LightSurface)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(badgeBg, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (challenge.severity) {
                                ChallengeSeverity.HIGH -> Icons.Default.Dangerous
                                ChallengeSeverity.MEDIUM -> Icons.Default.Warning
                                ChallengeSeverity.LOW -> Icons.Default.Info
                            },
                            contentDescription = null,
                            tint = badgeColor,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Text(
                        text = challenge.title,
                        color = Life360TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 17.sp
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = badgeBg
                ) {
                    Text(
                        text = "${challenge.severity.name} RISK",
                        color = badgeColor,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            // Stretch details and timing
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "📍 ${challenge.stretchDescription}",
                    color = Life360TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Life360LightBg,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "⚠️ Risk Factor: ${challenge.riskFactor}",
                        color = Life360TextPrimary,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                    Text(
                        text = "🛡️ Safe Action: ${challenge.recommendedSafetyAction}",
                        color = Life360Purple,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 15.sp
                    )
                    Text(
                        text = "⏱️ Time window: ${challenge.timeSpan}",
                        color = Life360TextMuted,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}

@Composable
fun SafeHavenCard(
    haven: SafeHavenSummary,
    onCall: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, Life360LightBorder, RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(containerColor = Life360LightSurface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(
                            when {
                                haven.type.contains("Police", ignoreCase = true) -> Life360BlueBg
                                haven.type.contains("Hospital", ignoreCase = true) -> Life360RedBg
                                else -> Life360PurpleBg
                            },
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when {
                            haven.type.contains("Police", ignoreCase = true) -> Icons.Default.LocalPolice
                            haven.type.contains("Hospital", ignoreCase = true) -> Icons.Default.LocalHospital
                            haven.type.contains("Metro", ignoreCase = true) -> Icons.Default.DirectionsSubway
                            else -> Icons.Default.Security
                        },
                        contentDescription = null,
                        tint = when {
                            haven.type.contains("Police", ignoreCase = true) -> Life360Blue
                            haven.type.contains("Hospital", ignoreCase = true) -> Life360Red
                            else -> Life360Purple
                        },
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column {
                    Text(
                        text = haven.name,
                        color = Life360TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = haven.type,
                            color = Life360TextSecondary,
                            fontSize = 10.sp
                        )
                        Text(
                            text = "• ${haven.distanceAlongRoute}",
                            color = Life360Purple,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            IconButton(
                onClick = onCall,
                modifier = Modifier
                    .size(34.dp)
                    .background(Life360GreenBg, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = "Call Haven",
                    tint = Life360Green,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
