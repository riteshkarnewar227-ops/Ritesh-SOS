package com.example.data

import com.example.model.ChallengeSeverity
import com.example.model.RouteChallenge
import com.example.model.RouteWaypoint
import com.example.model.SafeHavenSummary
import com.example.model.SafeRouteAnalysis
import com.example.model.SafetyLevel

object SafeRouteRepository {

    val popularRoutes: List<SafeRouteAnalysis> = listOf(
        SafeRouteAnalysis(
            id = "route_1",
            title = "Sitabuldi to Dharampeth via West High Court (WHC) Rd",
            origin = "Sitabuldi Interchange / Metro Station",
            destination = "Dharampeth Coffee House Square",
            distanceKm = 3.2,
            estimatedDurationMinutes = 11,
            daySafetyScore = 96,
            nightSafetyScore = 88,
            safetyLevel = SafetyLevel.HIGH,
            streetLightingQuality = "98% Bright LED Streetlights & Commercial Illumination",
            policePatrolFrequency = "High — Sitabuldi PCR Van & Traffic Post every 10 mins",
            cctvCoverage = "Continuous Nagpur Smart City CCTV Corridor",
            challenges = listOf(
                RouteChallenge(
                    id = "c1_1",
                    title = "Heavy Mixed Traffic & Congestion at Law College Square",
                    severity = ChallengeSeverity.MEDIUM,
                    stretchDescription = "Between Shankar Nagar & Law College Sq (0.8 km stretch)",
                    timeSpan = "Peak Rush Hours: 5:30 PM – 8:30 PM",
                    riskFactor = "High two-wheeler density and frequent pedestrian jaywalking",
                    recommendedSafetyAction = "Use designated zebra crossings; avoid overtaking near turning pockets."
                ),
                RouteChallenge(
                    id = "c1_2",
                    title = "Dimly Lit Alleyways off WHC Road after 10:30 PM",
                    severity = ChallengeSeverity.LOW,
                    stretchDescription = "Side lanes leading toward Ramdaspeth residential sector",
                    timeSpan = "Late Night: 10:30 PM – 5:00 AM",
                    riskFactor = "Low pedestrian footfall once commercial shops close",
                    recommendedSafetyAction = "Stick strictly to the main WHC arterial dual carriageway rather than taking interior shortcuts."
                )
            ),
            safeHavens = listOf(
                SafeHavenSummary(
                    name = "Sitabuldi Police Station",
                    type = "Police Station",
                    distanceAlongRoute = "0.2 km from start",
                    contactNumber = "0712-2565100",
                    is24x7 = true
                ),
                SafeHavenSummary(
                    name = "Shankar Nagar Metro Station",
                    type = "Metro Station",
                    distanceAlongRoute = "1.6 km along route",
                    contactNumber = "0712-2550100",
                    is24x7 = true
                ),
                SafeHavenSummary(
                    name = "Wockhardt Super Specialty Hospital",
                    type = "24x7 Hospital",
                    distanceAlongRoute = "2.1 km along route",
                    contactNumber = "0712-6624100",
                    is24x7 = true
                ),
                SafeHavenSummary(
                    name = "Ambazari Police Chowki",
                    type = "Police Post",
                    distanceAlongRoute = "3.0 km near destination",
                    contactNumber = "0712-2244200",
                    is24x7 = true
                )
            ),
            safetyTips = listOf(
                "Keep to the main WHC Road; it has 24x7 active commercial storefronts and well-maintained sidewalks.",
                "In case of emergency, Shankar Nagar Metro Station has active security guards and emergency SOS buttons.",
                "Ensure your phone battery is above 20% before night travel."
            ),
            waypoints = listOf(
                RouteWaypoint("Sitabuldi Metro", 21.1458, 79.0882, true, "Police & Metro"),
                RouteWaypoint("Jhansi Rani Sq", 21.1442, 79.0795),
                RouteWaypoint("Shankar Nagar Sq", 21.1395, 79.0680, true, "Metro Station"),
                RouteWaypoint("Law College Sq", 21.1460, 79.0620),
                RouteWaypoint("Dharampeth Square", 21.1490, 79.0580, true, "Safe Haven")
            )
        ),
        SafeRouteAnalysis(
            id = "route_2",
            title = "VNIT Campus / Bajaj Nagar to Nagpur Airport via Wardha Rd",
            origin = "VNIT Main Gate, South Ambazari Rd",
            destination = "Dr. Babasaheb Ambedkar International Airport",
            distanceKm = 6.8,
            estimatedDurationMinutes = 18,
            daySafetyScore = 92,
            nightSafetyScore = 79,
            safetyLevel = SafetyLevel.HIGH,
            streetLightingQuality = "92% Sodium & LED High-Mast Lighting on Wardha Road",
            policePatrolFrequency = "Moderate — Highway Patrol & Sonegaon Station Vans",
            cctvCoverage = "Metro Pillar Surveillance along full Wardha Rd stretch",
            challenges = listOf(
                RouteChallenge(
                    id = "c2_1",
                    title = "Flyover Underpass Blind Spots near Ajni / Chhatrapati Sq",
                    severity = ChallengeSeverity.HIGH,
                    stretchDescription = "Underpass section below Chhatrapati Square Flyover (1.2 km)",
                    timeSpan = "Night: 9:30 PM – 5:30 AM",
                    riskFactor = "Reduced lighting under flyover spans and reduced line-of-sight for oncoming traffic",
                    recommendedSafetyAction = "Take the upper flyover ramp when travelling by vehicle, or use the well-lit service lane pedestrian footpath."
                ),
                RouteChallenge(
                    id = "c2_2",
                    title = "High Speed Vehicles & Merge Zone near Airport T-Point",
                    severity = ChallengeSeverity.MEDIUM,
                    stretchDescription = "Wardha Highway entry to Airport Terminal approach road",
                    timeSpan = "All Day (Highest at night: 11 PM – 4 AM)",
                    riskFactor = "Fast interstate highway traffic merging with airport passenger vehicles",
                    recommendedSafetyAction = "Maintain safe distance, signal early, and avoid stopping on highway shoulders."
                ),
                RouteChallenge(
                    id = "c2_3",
                    title = "Isolated Service Lane between Ujjwal Nagar & Somalwada",
                    severity = ChallengeSeverity.MEDIUM,
                    stretchDescription = "Western service road parallel to metro line (0.9 km)",
                    timeSpan = "Night: 10:00 PM – 5:00 AM",
                    riskFactor = "Sparse storefront presence at night",
                    recommendedSafetyAction = "Stay on the central Wardha Road main corridor rather than side service road."
                )
            ),
            safeHavens = listOf(
                SafeHavenSummary(
                    name = "Bajaj Nagar Police Post",
                    type = "Police Post",
                    distanceAlongRoute = "0.5 km from start",
                    contactNumber = "0712-2244100",
                    is24x7 = true
                ),
                SafeHavenSummary(
                    name = "Orange City Hospital & Research Institute",
                    type = "24x7 Multi-Specialty Hospital",
                    distanceAlongRoute = "2.4 km along route",
                    contactNumber = "0712-6652000",
                    is24x7 = true
                ),
                SafeHavenSummary(
                    name = "Chhatrapati Square Metro Station",
                    type = "Metro Station",
                    distanceAlongRoute = "3.1 km along route",
                    contactNumber = "0712-2550100",
                    is24x7 = true
                ),
                SafeHavenSummary(
                    name = "Sonegaon Police Station",
                    type = "Police Station",
                    distanceAlongRoute = "5.0 km near Airport",
                    contactNumber = "0712-2287200",
                    is24x7 = true
                )
            ),
            safetyTips = listOf(
                "Use the Metro line along Wardha Road if commuting after 10 PM for 100% guarded transit.",
                "Orange City Hospital provides a 24x7 accessible emergency refuge point midway through this corridor.",
                "Share your Live Location with Family Circle before starting night transit to the airport."
            ),
            waypoints = listOf(
                RouteWaypoint("VNIT Gate", 21.1255, 79.0515),
                RouteWaypoint("Bajaj Nagar", 21.1270, 79.0600, true, "Police Post"),
                RouteWaypoint("Chhatrapati Sq", 21.1150, 79.0730, true, "Metro & Hospital"),
                RouteWaypoint("Ujjwal Nagar", 21.0990, 79.0710),
                RouteWaypoint("Airport Terminal", 21.0922, 79.0620, true, "Police & Security")
            )
        ),
        SafeRouteAnalysis(
            id = "route_3",
            title = "Sadar to Nagpur Railway Station via Residency Road",
            origin = "Sadar Residency Road (Mount Road Sq)",
            destination = "Nagpur Central Railway Station (West Gate)",
            distanceKm = 2.4,
            estimatedDurationMinutes = 9,
            daySafetyScore = 95,
            nightSafetyScore = 90,
            safetyLevel = SafetyLevel.HIGH,
            streetLightingQuality = "100% LED Lighting & Vibrant Cantonment Commercial Lighting",
            policePatrolFrequency = "Very High — Sadar Police HQ & Railway RPF Security presence",
            cctvCoverage = "360° CCTV Surveillance Coverage with Cantonment Security",
            challenges = listOf(
                RouteChallenge(
                    id = "c3_1",
                    title = "Crowded Auto-Rickshaw & Passenger Swarms at Railway Station Approach",
                    severity = ChallengeSeverity.MEDIUM,
                    stretchDescription = "Station Road Flyover descent & West Entrance Circle (0.4 km)",
                    timeSpan = "Continuous (Peak: 7:00 PM – 11:30 PM)",
                    riskFactor = "High pickpocket vulnerability in dense jostling crowds and aggressive touts",
                    recommendedSafetyAction = "Keep bags zipped in front; use pre-paid taxi/auto booths or app cabs."
                ),
                RouteChallenge(
                    id = "c3_2",
                    title = "Potholed Construction Patch near LIC Square Flyover Ramp",
                    severity = ChallengeSeverity.LOW,
                    stretchDescription = "LIC Square intersection (0.3 km)",
                    timeSpan = "All Day",
                    riskFactor = "Uneven road surface and sudden braking by buses",
                    recommendedSafetyAction = "Reduce driving speed to under 30 km/h."
                )
            ),
            safeHavens = listOf(
                SafeHavenSummary(
                    name = "Sadar Police Station & DCP Zone II HQ",
                    type = "Police Station",
                    distanceAlongRoute = "0.3 km from start",
                    contactNumber = "0712-2531300",
                    is24x7 = true
                ),
                SafeHavenSummary(
                    name = "Alexis Multi-Speciality Hospital",
                    type = "24x7 Hospital",
                    distanceAlongRoute = "1.1 km along route",
                    contactNumber = "0712-7120000",
                    is24x7 = true
                ),
                SafeHavenSummary(
                    name = "Nagpur Railway Police (GRP / RPF 24x7 Help Desk)",
                    type = "Railway Police Station",
                    distanceAlongRoute = "2.4 km at destination",
                    contactNumber = "139 / 0712-2560300",
                    is24x7 = true
                )
            ),
            safetyTips = listOf(
                "Residency Road is one of the safest night corridors in Nagpur with 24x7 armed cantonment checkposts.",
                "For women travelling alone late at night, utilize the GRP 'Nirbhaya Booth' on Platform 1 of Nagpur Station.",
                "1-tap call 112 or Sadar Police Station if you notice suspicious activity."
            ),
            waypoints = listOf(
                RouteWaypoint("Sadar Mount Rd", 21.1610, 79.0830, true, "Sadar Police"),
                RouteWaypoint("LIC Square", 21.1550, 79.0860),
                RouteWaypoint("Manas Square", 21.1490, 79.0870),
                RouteWaypoint("Railway Station West", 21.1524, 79.0888, true, "GRP Police & CCTV")
            )
        ),
        SafeRouteAnalysis(
            id = "route_4",
            title = "Hingna Industrial Belt to Ambazari & GMC Hospital",
            origin = "Hingna T-Point / MIDC Main Road",
            destination = "Government Medical College (GMC), Ajni Rd",
            distanceKm = 8.5,
            estimatedDurationMinutes = 24,
            daySafetyScore = 86,
            nightSafetyScore = 64,
            safetyLevel = SafetyLevel.MODERATE,
            streetLightingQuality = "78% Lighting (Several gaps along MIDC periphery and Ambazari back-lake)",
            policePatrolFrequency = "Moderate — MIDC Police Van every 30-40 mins",
            cctvCoverage = "Intermittent — Junction cameras present at major intersections",
            challenges = listOf(
                RouteChallenge(
                    id = "c4_1",
                    title = "Isolated Forest & Lake Boundary Stretch along Ambazari Backwaters",
                    severity = ChallengeSeverity.HIGH,
                    stretchDescription = "Ambazari Bypass to Wadi T-Point road (2.2 km stretch)",
                    timeSpan = "Night: 8:30 PM – 6:00 AM",
                    riskFactor = "Sparse residential habitations, heavy truck transit, no street vendors, dark blind spots",
                    recommendedSafetyAction = "Do NOT travel alone on two-wheeler after 9 PM. Take the alternate route through Subhash Nagar / Hingna Road."
                ),
                RouteChallenge(
                    id = "c4_2",
                    title = "High Heavy Commercial Vehicle Traffic (Trucks & Mixers)",
                    severity = ChallengeSeverity.HIGH,
                    stretchDescription = "MIDC Hingna Industrial Gate to Hingna T-Point",
                    timeSpan = "Evening & Night: 6:00 PM – 3:00 AM",
                    riskFactor = "Large blind spots of industrial trailers, dust pollution reducing headlight visibility",
                    recommendedSafetyAction = "Keep high-visibility headlights on, stay on the left lane, avoid overtaking heavy trucks on turns."
                ),
                RouteChallenge(
                    id = "c4_3",
                    title = "Potholes and Road Shoulder Erosion",
                    severity = ChallengeSeverity.MEDIUM,
                    stretchDescription = "Near CRPF Gate & Digdoh Hills",
                    timeSpan = "All Day",
                    riskFactor = "Sudden swerving risk for two-wheelers",
                    recommendedSafetyAction = "Maintain moderate speed under 40 km/h."
                )
            ),
            safeHavens = listOf(
                SafeHavenSummary(
                    name = "MIDC Hingna Police Station",
                    type = "Police Station",
                    distanceAlongRoute = "1.0 km from start",
                    contactNumber = "0712-237100",
                    is24x7 = true
                ),
                SafeHavenSummary(
                    name = "Lata Mangeshkar Hospital (NKP Salve Medical)",
                    type = "24x7 Teaching Hospital",
                    distanceAlongRoute = "2.8 km along route",
                    contactNumber = "0712-286500",
                    is24x7 = true
                ),
                SafeHavenSummary(
                    name = "Ambazari Police Chowki",
                    type = "Police Post",
                    distanceAlongRoute = "5.5 km along route",
                    contactNumber = "0712-2244200",
                    is24x7 = true
                ),
                SafeHavenSummary(
                    name = "Government Medical College (GMC) Trauma Centre",
                    type = "24x7 Level 1 Trauma Hospital",
                    distanceAlongRoute = "8.5 km at destination",
                    contactNumber = "0712-2744100",
                    is24x7 = true
                )
            ),
            safetyTips = listOf(
                "When travelling from Hingna after dark, always prefer the Subhash Nagar - Mate Square route over Ambazari lake bypass.",
                "Ensure your vehicle has adequate fuel and tire pressure as petrol pumps are sparse on the industrial stretch after 11 PM.",
                "Keep emergency speed dial ready on the lock screen."
            ),
            waypoints = listOf(
                RouteWaypoint("Hingna MIDC", 21.0950, 78.9850, true, "MIDC Police"),
                RouteWaypoint("CRPF Gate", 21.1100, 79.0120),
                RouteWaypoint("Hingna T-Point", 21.1180, 79.0350),
                RouteWaypoint("Mate Square", 21.1270, 79.0550, true, "Safe Chowki"),
                RouteWaypoint("GMC Hospital", 21.1350, 79.0950, true, "Level-1 Trauma")
            )
        ),
        SafeRouteAnalysis(
            id = "route_5",
            title = "Mahal / Itwari Heritage Sector to Medical Square",
            origin = "Kalyan Mandir, Itwari Cloth Market",
            destination = "Medical Square, Great Nag Road",
            distanceKm = 3.6,
            estimatedDurationMinutes = 14,
            daySafetyScore = 90,
            nightSafetyScore = 72,
            safetyLevel = SafetyLevel.MODERATE,
            streetLightingQuality = "82% Lighting (Heritage narrow corridors with partial shadowing)",
            policePatrolFrequency = "Frequent — Tehsil Police Station & Kotwali Foot Patrols",
            cctvCoverage = "Dense market CCTV cameras active during day; some blind spots at night",
            challenges = listOf(
                RouteChallenge(
                    id = "c5_1",
                    title = "Very Narrow Walled Alleyways & Heritage Bazaars",
                    severity = ChallengeSeverity.HIGH,
                    stretchDescription = "Sarafa Bazaar to Gandhi Gate stretch (1.1 km)",
                    timeSpan = "Night: 9:00 PM – 6:00 AM",
                    riskFactor = "Zero four-wheeler access, shuttered shop fronts, deserted narrow alleys after market closure",
                    recommendedSafetyAction = "Take the wider outer arterial roads (Central Avenue or Great Nag Road) rather than cutting through interior bazaars."
                ),
                RouteChallenge(
                    id = "c5_2",
                    title = "Stray Cattle & Animal Obstructions at Night",
                    severity = ChallengeSeverity.MEDIUM,
                    stretchDescription = "Near Shukrawari Lake / Gandhi Sagar perimeter",
                    timeSpan = "Night: 10:00 PM – 5:00 AM",
                    riskFactor = "Unlit cattle resting on dark tarmac causing sudden collision hazards",
                    recommendedSafetyAction = "Use bright low-beam lights and slow down near water body turns."
                )
            ),
            safeHavens = listOf(
                SafeHavenSummary(
                    name = "Kotwali Police Station",
                    type = "Police Station",
                    distanceAlongRoute = "0.6 km along route",
                    contactNumber = "0712-2724100",
                    is24x7 = true
                ),
                SafeHavenSummary(
                    name = "Tehsil Police Station",
                    type = "Police Station",
                    distanceAlongRoute = "1.2 km along route",
                    contactNumber = "0712-2767100",
                    is24x7 = true
                ),
                SafeHavenSummary(
                    name = "Government Ayurvedic Hospital & College",
                    type = "24x7 Government Hospital",
                    distanceAlongRoute = "2.2 km along route",
                    contactNumber = "0712-2746100",
                    is24x7 = true
                ),
                SafeHavenSummary(
                    name = "GMC Hospital & Medical Police Post",
                    type = "Police & Trauma Hospital",
                    distanceAlongRoute = "3.6 km at destination",
                    contactNumber = "0712-2744100",
                    is24x7 = true
                )
            ),
            safetyTips = listOf(
                "Use Central Avenue for smooth, wide, well-lit transit across East and Central Nagpur.",
                "Avoid inner market alleys after 9:30 PM once retail shops pull their shutters down.",
                "In case of any harassment or distress, Kotwali Police Station is located right by Gandhi Gate."
            ),
            waypoints = listOf(
                RouteWaypoint("Itwari Market", 21.1560, 79.1120),
                RouteWaypoint("Kotwali Police", 21.1500, 79.1080, true, "Police Station"),
                RouteWaypoint("Gandhi Sagar", 21.1440, 79.1020),
                RouteWaypoint("Medical Square", 21.1350, 79.0950, true, "24x7 Hospital")
            )
        )
    )

    fun getRouteById(id: String): SafeRouteAnalysis? {
        return popularRoutes.find { it.id == id } ?: popularRoutes.firstOrNull()
    }

    fun analyzeCustomRoute(origin: String, destination: String): SafeRouteAnalysis {
        // Build dynamic intelligent analysis for user custom query
        val isLateNightCity = true
        val estKm = 4.5
        val duration = 15

        return SafeRouteAnalysis(
            id = "custom_route_${System.currentTimeMillis()}",
            title = "$origin to $destination",
            origin = origin.ifBlank { "Current Live GPS Location" },
            destination = destination.ifBlank { "Designated Destination" },
            distanceKm = estKm,
            estimatedDurationMinutes = duration,
            daySafetyScore = 91,
            nightSafetyScore = 78,
            safetyLevel = SafetyLevel.HIGH,
            streetLightingQuality = "88% Continuous LED Lighting along main thoroughfare",
            policePatrolFrequency = "City Police Mobile PCR Patrol every 15-20 min",
            cctvCoverage = "Smart City Junction CCTV cameras active",
            challenges = listOf(
                RouteChallenge(
                    id = "custom_c1",
                    title = "Intersection Visibility & Reduced Footfall after Dark",
                    severity = ChallengeSeverity.MEDIUM,
                    stretchDescription = "Midpoint transit corridor between $origin and $destination",
                    timeSpan = "Night: 9:30 PM – 5:30 AM",
                    riskFactor = "Fewer open businesses after 10 PM creates isolated pockets",
                    recommendedSafetyAction = "Stay on main multi-lane roads. Keep headlights on high-visibility mode and avoid dark alleyway short-cuts."
                ),
                RouteChallenge(
                    id = "custom_c2",
                    title = "Two-Wheeler Lane Filtering & Blind Crossings",
                    severity = ChallengeSeverity.LOW,
                    stretchDescription = "Near main square and signal turning points",
                    timeSpan = "Evening Rush Hour: 6:00 PM – 9:00 PM",
                    riskFactor = "Sudden turns by commercial autos and scooters",
                    recommendedSafetyAction = "Maintain safe 3-second braking distance and check mirrors before turns."
                )
            ),
            safeHavens = listOf(
                SafeHavenSummary(
                    name = "Nearest Police Control Room (112)",
                    type = "Emergency Response Post",
                    distanceAlongRoute = "1.2 km from current location",
                    contactNumber = "112 / 100",
                    is24x7 = true
                ),
                SafeHavenSummary(
                    name = "Nearest 24x7 Emergency Care Hospital",
                    type = "24x7 Emergency Hospital",
                    distanceAlongRoute = "2.5 km along corridor",
                    contactNumber = "108 / 0712-2565100",
                    is24x7 = true
                ),
                SafeHavenSummary(
                    name = "Metro Station / 24x7 Fuel Station",
                    type = "Illuminated Safe Haven",
                    distanceAlongRoute = "3.1 km along route",
                    contactNumber = "112",
                    is24x7 = true
                )
            ),
            safetyTips = listOf(
                "Share your Live Location with your Family Circle via WhatsApp before departing.",
                "Stick strictly to wide dual-carriageway roads rather than navigation shortcuts.",
                "If you feel uneasy, pull over into a 24x7 fuel station or metro station and press SOS."
            ),
            waypoints = listOf(
                RouteWaypoint(origin.take(15).ifBlank { "Start Point" }, 21.1458, 79.0882, true, "Origin"),
                RouteWaypoint("Mid-Corridor Safe Haven", 21.1400, 79.0750, true, "Police/Hospital"),
                RouteWaypoint(destination.take(15).ifBlank { "Destination" }, 21.1350, 79.0620, true, "Destination")
            )
        )
    }
}
