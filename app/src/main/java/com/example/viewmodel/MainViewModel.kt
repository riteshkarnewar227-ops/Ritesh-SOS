package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.battery.BatteryMonitor
import com.example.data.AppDatabase
import com.example.data.SafeRouteRepository
import com.example.data.SosRepository
import com.example.emergency.AudioRecorder
import com.example.emergency.AudioRecordingState
import com.example.emergency.SosDispatcher
import com.example.location.LocationTracker
import com.example.location.UserLocation
import com.example.model.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class SosTriggerState(
    val isCountingDown: Boolean = false,
    val countdownSecondsRemaining: Int = 3,
    val isSosActive: Boolean = false,
    val lastDispatchedMessage: String = "",
    val showMultiContactDialog: Boolean = false,
    val pendingContactsToDispatch: List<EmergencyContact> = emptyList(),
    val isAlarmActive: Boolean = false,
    val lastRecordedAudioPath: String? = null
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application, viewModelScope)
    private val repository = SosRepository(database)
    val batteryMonitor = BatteryMonitor(application)
    val locationTracker = LocationTracker(application, viewModelScope)
    val sosDispatcher = SosDispatcher(application)
    val audioRecorder = AudioRecorder(application)

    val batteryState: StateFlow<BatteryState> = batteryMonitor.batteryState
    val userLocation: StateFlow<UserLocation> = locationTracker.currentLocation
    val audioState: StateFlow<AudioRecordingState> = audioRecorder.recordingState

    val userProfile: StateFlow<UserProfile?> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val contacts: StateFlow<List<EmergencyContact>> = repository.contacts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val sosHistory: StateFlow<List<SosHistory>> = repository.sosHistory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val safeZones: StateFlow<List<SafeZone>> = repository.safeZones
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _sosState = MutableStateFlow(SosTriggerState())
    val sosState: StateFlow<SosTriggerState> = _sosState.asStateFlow()

    private val _selectedSafeZone = MutableStateFlow<SafeZone?>(null)
    val selectedSafeZone: StateFlow<SafeZone?> = _selectedSafeZone.asStateFlow()

    // Safe Route Navigator & Safety Audit State
    val allSafeRoutes: List<SafeRouteAnalysis> = SafeRouteRepository.popularRoutes
    private val _selectedSafeRoute = MutableStateFlow<SafeRouteAnalysis>(SafeRouteRepository.popularRoutes.first())
    val selectedSafeRoute: StateFlow<SafeRouteAnalysis> = _selectedSafeRoute.asStateFlow()

    private val _isNightRouteAnalysis = MutableStateFlow<Boolean>(false)
    val isNightRouteAnalysis: StateFlow<Boolean> = _isNightRouteAnalysis.asStateFlow()

    // Temporary active role state for testing/switching mode in UI
    private val _currentActiveRole = MutableStateFlow<UserRole?>(null)
    val currentActiveRole: StateFlow<UserRole?> = _currentActiveRole.asStateFlow()

    private var countdownJob: Job? = null
    private var recordingTickerJob: Job? = null

    init {
        viewModelScope.launch {
            repository.ensureSeeded()
        }
        batteryMonitor.startListening()
        locationTracker.startLocationUpdates()
    }

    override fun onCleared() {
        super.onCleared()
        batteryMonitor.stopListening()
        locationTracker.stopLocationUpdates()
        sosDispatcher.stopEmergencyAlarm()
        audioRecorder.release()
        recordingTickerJob?.cancel()
    }

    fun completeSignUp(
        name: String,
        phone: String,
        role: UserRole,
        extraField: String = "",
        pairedPhone: String = ""
    ) {
        viewModelScope.launch {
            val generatedCode = "SUR-${(1000..9999).random()}"
            val profile = when (role) {
                UserRole.CHILD -> {
                    UserProfile(
                        name = name.trim(),
                        phoneNumber = phone.trim(),
                        role = role,
                        pairingCode = generatedCode,
                        pairedPersonPhone = pairedPhone.trim(),
                        pairedPersonName = if (extraField.isNotBlank()) extraField.trim() else "Guardian",
                        isRegistered = true
                    )
                }
                UserRole.PARENT -> {
                    UserProfile(
                        name = name.trim(),
                        phoneNumber = phone.trim(),
                        role = role,
                        pairingCode = generatedCode,
                        pairedWithCode = extraField.trim().uppercase(),
                        pairedPersonName = "Child / Family Member",
                        pairedPersonPhone = pairedPhone.trim(),
                        isRegistered = true
                    )
                }
                UserRole.POLICE -> {
                    UserProfile(
                        name = name.trim(),
                        phoneNumber = phone.trim(),
                        role = role,
                        pairingCode = generatedCode,
                        policeStationOrBadge = if (extraField.isNotBlank()) extraField.trim() else "Nagpur Police Central",
                        isRegistered = true
                    )
                }
            }
            repository.saveProfile(profile)
            _currentActiveRole.value = role

            // If child mode and guardian phone is provided, add/update in emergency contacts
            if (role == UserRole.CHILD && pairedPhone.isNotBlank()) {
                repository.saveContact(
                    EmergencyContact(
                        name = if (extraField.isNotBlank()) extraField.trim() else "Parent / Guardian",
                        phoneNumber = pairedPhone.trim(),
                        relationship = "Parent",
                        isWhatsAppEnabled = true,
                        isSmsEnabled = true,
                        isPrimary = true,
                        customNotes = "Linked Parent Contact"
                    )
                )
            }
        }
    }

    fun updateChildPairing(code: String, childName: String = "Child", childPhone: String = "") {
        viewModelScope.launch {
            val current = userProfile.value
            if (current != null) {
                val updated = current.copy(
                    pairedWithCode = code.trim().uppercase(),
                    pairedPersonName = childName.trim(),
                    pairedPersonPhone = childPhone.trim()
                )
                repository.saveProfile(updated)
            }
        }
    }

    fun switchRole(newRole: UserRole) {
        viewModelScope.launch {
            val current = userProfile.value
            if (current != null) {
                val updated = current.copy(role = newRole)
                repository.saveProfile(updated)
            }
            _currentActiveRole.value = newRole
        }
    }

    fun resetProfileToSignUp() {
        viewModelScope.launch {
            repository.clearProfile()
            _currentActiveRole.value = null
        }
    }

    fun setSelectedSafeZone(zone: SafeZone?) {
        _selectedSafeZone.value = zone
    }

    fun triggerEmergencySosWithCountdown(seconds: Int = 3, customNote: String = "") {
        if (seconds <= 0) {
            executeEmergencySosNow(customNote)
            return
        }

        countdownJob?.cancel()
        _sosState.update {
            it.copy(
                isCountingDown = true,
                countdownSecondsRemaining = seconds
            )
        }

        countdownJob = viewModelScope.launch {
            for (i in seconds downTo 1) {
                _sosState.update { it.copy(countdownSecondsRemaining = i) }
                delay(1000)
            }
            _sosState.update { it.copy(isCountingDown = false) }
            executeEmergencySosNow(customNote)
        }
    }

    fun cancelSosCountdown() {
        countdownJob?.cancel()
        countdownJob = null
        _sosState.update {
            it.copy(
                isCountingDown = false,
                countdownSecondsRemaining = 3
            )
        }
        sosDispatcher.stopEmergencyAlarm()
    }

    fun startDistressAudioRecording(): String? {
        val path = audioRecorder.startRecording()
        startAudioTicker()
        return path
    }

    fun stopDistressAudioRecording(): String? {
        recordingTickerJob?.cancel()
        val path = audioRecorder.stopRecording()
        _sosState.update { it.copy(lastRecordedAudioPath = path) }
        return path
    }

    private fun startAudioTicker() {
        recordingTickerJob?.cancel()
        recordingTickerJob = viewModelScope.launch {
            var elapsed = 0
            while (audioState.value.isRecording) {
                delay(1000)
                elapsed++
                audioRecorder.updateDuration(elapsed)
            }
        }
    }

    fun playAudioRecording(path: String? = null, onComplete: () -> Unit = {}) {
        audioRecorder.playRecording(path, onComplete)
    }

    fun stopAudioPlayback() {
        audioRecorder.stopPlayback()
    }

    fun shareDistressAudioViaWhatsApp(path: String? = null) {
        val profile = userProfile.value
        val caption = "🚨 EMERGENCY DISTRESS AUDIO RECORDING from ${profile?.name ?: "Citizen"} (Code: ${profile?.pairingCode ?: "SUR-SOS"})"
        audioRecorder.shareAudioToWhatsApp(path, caption)
    }

    fun executeEmergencySosNow(customNote: String = "") {
        val loc = userLocation.value
        val batt = batteryState.value
        val allContacts = contacts.value
        val profile = userProfile.value
        val name = profile?.name ?: "Circle Member in Distress"
        val pairingCode = profile?.pairingCode ?: ""

        // 1. Immediately Start Live Audio Recording
        val audioPath = startDistressAudioRecording()

        // 2. Start loud alarm sound and vibration
        sosDispatcher.startEmergencyAlarm()

        // 3. Format emergency SOS payload & SMS text
        val formattedMsg = sosDispatcher.formatSosMessage(
            userName = name,
            location = loc,
            battery = batt,
            pairingCode = pairingCode,
            hasLiveAudioRecording = true,
            customEmergencyNote = customNote
        )

        val smsHelpMsg = sosDispatcher.formatSmsHelpMessage(
            userName = name,
            location = loc,
            battery = batt,
            customNote = customNote
        )

        // 4. Identify Parent/Guardian and Nearest Police Station
        val enabledContacts = allContacts.filter { it.isWhatsAppEnabled }
        val primaryParent = enabledContacts.firstOrNull { it.relationship == "Parent" || it.isPrimary }
            ?: enabledContacts.firstOrNull()
        val parentPhone = primaryParent?.phoneNumber ?: profile?.pairedPersonPhone ?: ""

        // Calculate nearest police station from safeZones
        val policeZones = safeZones.value.filter { 
            it.category.contains("Police", ignoreCase = true) || it.name.contains("Police", ignoreCase = true)
        }
        val nearestPolice = policeZones.minByOrNull { zone ->
            val dLat = zone.latitude - loc.latitude
            val dLng = zone.longitude - loc.longitude
            dLat * dLat + dLng * dLng
        }
        val policePhone = nearestPolice?.phoneNumber ?: "+917122561222" // Sitabuldi Police Station / Nagpur Control Room

        // 5. AUTOMATICALLY SEND DIRECT SMS WITH "HELP ME!" AND LIVE LOCATION TO BOTH PARENT & NEAREST POLICE
        if (parentPhone.isNotBlank()) {
            sosDispatcher.sendDirectSms(parentPhone, smsHelpMsg)
        }
        if (policePhone.isNotBlank()) {
            sosDispatcher.sendDirectSms(policePhone, smsHelpMsg)
        }
        // Also ensure 112 is notified
        if (policePhone != "112" && policePhone != "+91112") {
            sosDispatcher.sendDirectSms("112", smsHelpMsg)
        }

        // 6. Dispatch to Parent/Guardian and Police via WhatsApp
        if (primaryParent != null) {
            sosDispatcher.dispatchSosToWhatsApp(primaryParent, formattedMsg, audioPath)
        } else if (parentPhone.isNotBlank()) {
            sosDispatcher.dispatchToWhatsAppNumber(parentPhone, formattedMsg, audioPath)
        } else {
            sosDispatcher.dispatchToWhatsAppNumber(policePhone, formattedMsg, audioPath)
        }

        // 7. Trigger immediate Emergency Phone Call to Parent or Police 112
        val callTarget = if (parentPhone.isNotBlank()) parentPhone else policePhone
        sosDispatcher.callEmergencyNumber(callTarget)

        // 8. Update UI State & Show multi-contact quick broadcast hub
        _sosState.update {
            it.copy(
                isSosActive = true,
                isAlarmActive = true,
                lastDispatchedMessage = formattedMsg,
                showMultiContactDialog = true,
                pendingContactsToDispatch = enabledContacts,
                lastRecordedAudioPath = audioPath
            )
        }

        // 9. Log to Room Database history
        viewModelScope.launch {
            val contactSummary = buildList {
                if (parentPhone.isNotBlank()) add("Parent/Guardian ($parentPhone)")
                if (nearestPolice != null) add("${nearestPolice.name} ($policePhone)") else add("Nagpur Police 112")
                addAll(enabledContacts.map { "${it.name} (${it.relationship})" })
            }.distinct().joinToString(", ")

            repository.logSosEvent(
                SosHistory(
                    latitude = loc.latitude,
                    longitude = loc.longitude,
                    accuracyMeters = loc.accuracy,
                    address = loc.address,
                    batteryPercent = batt.level,
                    isCharging = batt.isCharging,
                    contactsAlerted = contactSummary,
                    status = "AUTOMATIC_DISPATCH_SENT",
                    googleMapsUrl = loc.toGoogleMapsUrl(),
                    audioFilePath = audioPath ?: "",
                    hasAudioRecording = true,
                    senderName = name,
                    pairingCode = pairingCode
                )
            )
        }
    }

    fun dispatchToSpecificContactWhatsApp(contact: EmergencyContact) {
        val audioPath = _sosState.value.lastRecordedAudioPath ?: audioRecorder.recordingState.value.lastRecordedFilePath
        val msg = _sosState.value.lastDispatchedMessage.ifBlank {
            sosDispatcher.formatSosMessage(
                userName = userProfile.value?.name ?: "Citizen",
                location = userLocation.value,
                battery = batteryState.value,
                pairingCode = userProfile.value?.pairingCode ?: ""
            )
        }
        sosDispatcher.dispatchSosToWhatsApp(contact, msg, audioPath)
    }

    fun dispatchSmsToContact(contact: EmergencyContact) {
        val msg = _sosState.value.lastDispatchedMessage.ifBlank {
            sosDispatcher.formatSosMessage(
                userName = userProfile.value?.name ?: "Citizen",
                location = userLocation.value,
                battery = batteryState.value,
                pairingCode = userProfile.value?.pairingCode ?: ""
            )
        }
        sosDispatcher.dispatchSms(contact.phoneNumber, msg)
    }

    fun sendParentCheckInWhatsApp(childPhone: String, childName: String) {
        val checkInMsg = "👋 Hi $childName, this is your Parent/Guardian checking in from Nagpur Suraksha. Please confirm you are safe, or send your live GPS location if needed! 🛡️"
        sosDispatcher.dispatchToWhatsAppNumber(childPhone, checkInMsg)
    }

    fun sendPoliceDispatchWhatsApp(phone: String, incidentInfo: String) {
        val policeMsg = "🚨 *NAGPUR POLICE EMERGENCY DISPATCH ASSIGNMENT* 🚨\n\n$incidentInfo\n\n🚔 PCR Unit En Route."
        sosDispatcher.dispatchToWhatsAppNumber(phone, policeMsg)
    }

    fun dismissMultiContactDialog() {
        _sosState.update { it.copy(showMultiContactDialog = false) }
    }

    fun toggleAlarm() {
        val current = _sosState.value.isAlarmActive
        if (current) {
            sosDispatcher.stopEmergencyAlarm()
            _sosState.update { it.copy(isAlarmActive = false) }
        } else {
            sosDispatcher.startEmergencyAlarm()
            _sosState.update { it.copy(isAlarmActive = true) }
        }
    }

    fun stopAlarm() {
        sosDispatcher.stopEmergencyAlarm()
        stopDistressAudioRecording()
        _sosState.update { it.copy(isAlarmActive = false, isSosActive = false) }
    }

    fun dialPolice(number: String = "112") {
        sosDispatcher.dialEmergency(number)
    }

    fun callDirect(number: String) {
        sosDispatcher.callEmergencyNumber(number)
    }

    fun openGoogleMaps(lat: Double, lng: Double, label: String) {
        sosDispatcher.openInGoogleMaps(lat, lng, label)
    }

    fun copySosMessageToClipboard() {
        val msg = _sosState.value.lastDispatchedMessage.ifBlank {
            sosDispatcher.formatSosMessage(
                userName = userProfile.value?.name ?: "Citizen",
                location = userLocation.value,
                battery = batteryState.value,
                pairingCode = userProfile.value?.pairingCode ?: ""
            )
        }
        sosDispatcher.copyToClipboard("Nagpur Suraksha SOS", msg)
    }

    fun saveContact(contact: EmergencyContact) {
        viewModelScope.launch {
            repository.saveContact(contact)
        }
    }

    fun deleteContact(contact: EmergencyContact) {
        viewModelScope.launch {
            repository.deleteContact(contact)
        }
    }

    fun selectSafeRoute(route: SafeRouteAnalysis) {
        _selectedSafeRoute.value = route
    }

    fun toggleNightRouteAnalysis(isNight: Boolean) {
        _isNightRouteAnalysis.value = isNight
    }

    fun analyzeCustomRoute(origin: String, destination: String) {
        val analysis = SafeRouteRepository.analyzeCustomRoute(origin, destination)
        _selectedSafeRoute.value = analysis
    }

    fun shareSafeRouteWithCircle(route: SafeRouteAnalysis, isNight: Boolean) {
        val score = if (isNight) route.nightSafetyScore else route.daySafetyScore
        val timeLabel = if (isNight) "🌙 Night Travel Safety Audit" else "☀️ Daytime Travel Safety Audit"
        
        val challengesSummary = route.challenges.joinToString("\n") { 
            "⚠️ *${it.title}* (${it.severity.name} Risk)\n   • Stretch: ${it.stretchDescription}\n   • Challenge: ${it.riskFactor}\n   • Action: ${it.recommendedSafetyAction}"
        }

        val safeHavensSummary = route.safeHavens.joinToString("\n") {
            "🛡️ ${it.name} (${it.type}) - ${it.distanceAlongRoute} [Tel: ${it.contactNumber}]"
        }

        val text = buildString {
            append("🗺️ *LIFE360 SAFE ROUTE & SAFETY AUDIT* 🛡️\n")
            append("👤 Shared by: ${userProfile.value?.name ?: "Family Member"}\n")
            append("🛣️ Route: *${route.origin}* ➔ *${route.destination}*\n")
            append("📏 Distance: ${route.distanceKm} km (~${route.estimatedDurationMinutes} min)\n")
            append("📊 Mode: $timeLabel\n")
            append("🛡️ *Route Safety Score: $score/100* (${route.safetyLevel.label})\n\n")
            append("💡 *Illumination & Surveillance:*\n")
            append("• Streetlights: ${route.streetLightingQuality}\n")
            append("• Patrols: ${route.policePatrolFrequency}\n")
            append("• CCTV: ${route.cctvCoverage}\n\n")
            if (route.challenges.isNotEmpty()) {
                append("⚠️ *IDENTIFIED ROUTE CHALLENGES & RISKS:*\n")
                append(challengesSummary)
                append("\n\n")
            }
            if (route.safeHavens.isNotEmpty()) {
                append("🏥 *EMERGENCY SAFE HAVENS ALONG CORRIDOR:*\n")
                append(safeHavensSummary)
                append("\n\n")
            }
            append("📍 Live GPS Tracking Active via Life360 Suraksha")
        }

        val firstContactPhone = contacts.value.firstOrNull()?.phoneNumber ?: userProfile.value?.pairedPersonPhone ?: ""
        if (firstContactPhone.isNotBlank()) {
            sosDispatcher.dispatchToWhatsAppNumber(firstContactPhone, text)
        } else {
            sosDispatcher.copyToClipboard("Life360 Safe Route Plan", text)
        }
    }

    fun openRouteInGoogleMaps(origin: String, destination: String) {
        val query = "https://www.google.com/maps/dir/?api=1&origin=${java.net.URLEncoder.encode(origin, "UTF-8")}&destination=${java.net.URLEncoder.encode(destination, "UTF-8")}&travelmode=driving"
        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(query)).apply {
            flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK
        }
        try {
            getApplication<Application>().startActivity(intent)
        } catch (e: Exception) {
            // Fallback to coordinates if available
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }
}
