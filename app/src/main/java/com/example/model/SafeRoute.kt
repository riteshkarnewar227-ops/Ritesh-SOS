package com.example.model

enum class SafetyLevel(val label: String, val description: String) {
    HIGH("High Safety", "Well-illuminated arterial road with frequent police patrols & high footfall"),
    MODERATE("Moderate Safety", "Generally safe during day; caution advised late at night in certain stretches"),
    LOW_SAFETY("Low Safety / High Caution", "Poor lighting, isolated stretches, or narrow unmonitored alleys")
}

enum class ChallengeSeverity {
    HIGH,
    MEDIUM,
    LOW
}

data class RouteChallenge(
    val id: String,
    val title: String,
    val severity: ChallengeSeverity,
    val stretchDescription: String,
    val timeSpan: String,
    val riskFactor: String,
    val recommendedSafetyAction: String
)

data class SafeHavenSummary(
    val name: String,
    val type: String, // Police Station, 24x7 Hospital, Metro Station, Women Help Booth
    val distanceAlongRoute: String,
    val contactNumber: String,
    val is24x7: Boolean = true
)

data class RouteWaypoint(
    val label: String,
    val latitude: Double,
    val longitude: Double,
    val isSafetySpot: Boolean = false,
    val spotType: String? = null
)

data class SafeRouteAnalysis(
    val id: String,
    val title: String,
    val origin: String,
    val destination: String,
    val distanceKm: Double,
    val estimatedDurationMinutes: Int,
    val daySafetyScore: Int, // 0 - 100
    val nightSafetyScore: Int, // 0 - 100
    val safetyLevel: SafetyLevel,
    val streetLightingQuality: String, // e.g. "95% Bright LED Lit"
    val policePatrolFrequency: String, // e.g. "PCR Van every 15-20 min"
    val cctvCoverage: String, // e.g. "Smart City 24x7 Monitored"
    val challenges: List<RouteChallenge>,
    val safeHavens: List<SafeHavenSummary>,
    val safetyTips: List<String>,
    val waypoints: List<RouteWaypoint> = emptyList()
)
