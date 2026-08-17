package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.location.UserLocation
import com.example.map.OfflineMapCanvas
import com.example.model.SafeZone
import com.example.ui.theme.*

@Composable
fun OfflineMapScreen(
    userLocation: UserLocation,
    safeZones: List<SafeZone>,
    selectedZone: SafeZone?,
    onSelectZone: (SafeZone?) -> Unit,
    onOpenGoogleMaps: (Double, Double, String) -> Unit,
    onDialNumber: (String) -> Unit,
    onShareLocationToPolice: (SafeZone) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf("All") }
    val categories = listOf("All", "Police Station", "Hospital", "Metro Station")

    val filteredZones = remember(safeZones, selectedCategory) {
        if (selectedCategory == "All") safeZones else safeZones.filter { it.category == selectedCategory }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Life360DarkBg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Map,
                        contentDescription = null,
                        tint = Life360PurpleLight,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "LIVE FAMILY MAP",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                }
                Text(
                    text = "Real-Time GPS • Safe Places & Emergency Spots",
                    color = Life360TextSecondary,
                    fontSize = 11.sp
                )
            }

            // Google Maps App Trigger
            Button(
                onClick = {
                    onOpenGoogleMaps(
                        userLocation.latitude,
                        userLocation.longitude,
                        "My Live Location"
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = Life360Purple),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.OpenInNew,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Google Maps", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Offline Vector Map Canvas View (Takes upper portion)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, Life360DarkBorder, RoundedCornerShape(20.dp))
        ) {
            OfflineMapCanvas(
                userLocation = userLocation,
                safeZones = filteredZones,
                selectedZone = selectedZone,
                onSelectZone = onSelectZone,
                onOpenGoogleMaps = onOpenGoogleMaps
            )
        }

        // Category Filter Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(categories) { category ->
                val isSelected = selectedCategory == category
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (isSelected) Life360Purple else Life360DarkSurface,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSelected) Life360PurpleLight else Life360DarkBorder
                    ),
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { selectedCategory = category }
                ) {
                    Text(
                        text = if (category == "All") "All Places (${safeZones.size})" else category,
                        color = if (isSelected) Color.White else Life360TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // Safe Zones & Police Stations List
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredZones) { zone ->
                val isSelected = selectedZone?.id == zone.id
                SafeZoneCard(
                    zone = zone,
                    isSelected = isSelected,
                    onSelect = { onSelectZone(if (isSelected) null else zone) },
                    onOpenMaps = { onOpenGoogleMaps(zone.latitude, zone.longitude, zone.name) },
                    onCall = { onDialNumber(zone.phoneNumber) },
                    onShareWhatsApp = { onShareLocationToPolice(zone) }
                )
            }
        }
    }
}

@Composable
private fun SafeZoneCard(
    zone: SafeZone,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onOpenMaps: () -> Unit,
    onCall: () -> Unit,
    onShareWhatsApp: () -> Unit
) {
    val categoryColor = when (zone.category) {
        "Police Station" -> Life360Blue
        "Hospital" -> Life360Green
        "Metro Station" -> Life360PurpleLight
        else -> Life360Amber
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(
                1.dp,
                if (isSelected) categoryColor else Life360DarkBorder,
                RoundedCornerShape(16.dp)
            )
            .clickable { onSelect() }
            .testTag("safe_zone_card_${zone.id}"),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Life360DarkSurfaceElevated else Life360DarkSurface
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(categoryColor.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (zone.category) {
                                "Police Station" -> Icons.Default.LocalPolice
                                "Hospital" -> Icons.Default.LocalHospital
                                else -> Icons.Default.Subway
                            },
                            contentDescription = null,
                            tint = categoryColor,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = zone.name,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = zone.address,
                            color = Life360TextSecondary,
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = categoryColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = if (zone.is24Hours) "24x7 OPEN" else "OPEN",
                        color = categoryColor,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            // Quick Actions Strip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Focus / Target on Map
                OutlinedButton(
                    onClick = onSelect,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(vertical = 4.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = if (isSelected) Life360Amber else Life360TextPrimary
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Life360DarkBorder)
                ) {
                    Icon(
                        imageVector = Icons.Default.CenterFocusStrong,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isSelected) "Focused" else "Radar Pin", fontSize = 10.sp)
                }

                // Open in Google Maps
                Button(
                    onClick = onOpenMaps,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Life360DarkSurfaceElevated),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Directions,
                        contentDescription = "Directions",
                        tint = Life360PurpleLight,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Directions", fontSize = 10.sp, color = Color.White)
                }

                // Call Station
                Button(
                    onClick = onCall,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Life360Green),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = "Call",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Call", fontSize = 10.sp, color = Color.White)
                }
            }
        }
    }
}

