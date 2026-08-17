package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.model.UserRole
import com.example.ui.components.MultiContactSosDialog
import com.example.ui.components.ProfileDialog
import com.example.ui.screens.*
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SafetyRed
import com.example.viewmodel.MainViewModel

sealed class Screen(val route: String, val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Home : Screen("home", "Circle", Icons.Default.Groups)
    object SafeRoute : Screen("safe_route", "Safe Route", Icons.Default.AltRoute)
    object Map : Screen("map", "Places", Icons.Default.Place)
    object Battery : Screen("battery", "Battery", Icons.Default.BatteryChargingFull)
    object Contacts : Screen("contacts", "Family", Icons.Default.ContactPhone)
    object History : Screen("history", "Safety Logs", Icons.Default.History)
}

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme(darkTheme = false) {
                MainApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainApp(viewModel: MainViewModel) {
    val context = LocalContext.current
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Home.route

    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val batteryState by viewModel.batteryState.collectAsStateWithLifecycle()
    val userLocation by viewModel.userLocation.collectAsStateWithLifecycle()
    val sosState by viewModel.sosState.collectAsStateWithLifecycle()
    val audioState by viewModel.audioState.collectAsStateWithLifecycle()
    val contacts by viewModel.contacts.collectAsStateWithLifecycle()
    val safeZones by viewModel.safeZones.collectAsStateWithLifecycle()
    val sosHistory by viewModel.sosHistory.collectAsStateWithLifecycle()
    val selectedZone by viewModel.selectedSafeZone.collectAsStateWithLifecycle()

    var showProfileDialog by remember { mutableStateOf(false) }

    // Multi-Permission Launcher (Location, Audio Recording, SMS, Phone Call)
    val permissionsToRequest = remember {
        val list = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.SEND_SMS,
            Manifest.permission.CALL_PHONE
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            list.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        list.toTypedArray()
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) {
        viewModel.locationTracker.startLocationUpdates()
    }

    LaunchedEffect(Unit) {
        val needsPermission = permissionsToRequest.any {
            ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
        }
        if (needsPermission) {
            permissionLauncher.launch(permissionsToRequest)
        }
    }

    // If user is not yet registered, show the SignUp / Mode Selection screen
    if (userProfile == null || userProfile?.isRegistered != true) {
        SignUpScreen(
            onCompleteSignUp = { name, phone, role, passkey, extraField, pairedPhone ->
                viewModel.completeSignUp(name, phone, role, passkey, extraField, pairedPhone)
            }
        )
        return
    }

    val currentRole = userProfile?.role ?: UserRole.CHILD

    val items = listOf(
        Screen.Home,
        Screen.SafeRoute,
        Screen.Map,
        Screen.Contacts,
        Screen.History
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = com.example.ui.theme.Life360LightBg,
        bottomBar = {
            NavigationBar(
                containerColor = com.example.ui.theme.Life360LightSurface,
                contentColor = com.example.ui.theme.Life360TextPrimary,
                tonalElevation = 6.dp
            ) {
                items.forEach { screen ->
                    val isSelected = currentRoute == screen.route
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = screen.icon,
                                contentDescription = screen.title,
                                tint = if (isSelected) com.example.ui.theme.Life360Purple else com.example.ui.theme.Life360TextSecondary
                            )
                        },
                        label = {
                            Text(
                                text = screen.title,
                                color = if (isSelected) com.example.ui.theme.Life360Purple else com.example.ui.theme.Life360TextSecondary,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        selected = isSelected,
                        onClick = {
                            if (currentRoute != screen.route) {
                                navController.navigate(screen.route) {
                                    popUpTo(Screen.Home.route) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = com.example.ui.theme.Life360PurpleBg,
                            selectedIconColor = com.example.ui.theme.Life360Purple,
                            unselectedIconColor = com.example.ui.theme.Life360TextSecondary
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                when (currentRole) {
                    UserRole.CHILD -> {
                        ChildHomeScreen(
                            userProfile = userProfile,
                            batteryState = batteryState,
                            userLocation = userLocation,
                            sosState = sosState,
                            audioState = audioState,
                            onTriggerSos = { customNote ->
                                viewModel.triggerEmergencySosWithCountdown(seconds = 3, customNote = customNote)
                            },
                            onCancelCountdown = { viewModel.cancelSosCountdown() },
                            onOpenBatteryScreen = { navController.navigate(Screen.Battery.route) },
                            onOpenMapScreen = { navController.navigate(Screen.Map.route) },
                            onOpenSafeRouteScreen = { navController.navigate(Screen.SafeRoute.route) },
                            onOpenGoogleMapsUrl = {
                                viewModel.openGoogleMaps(userLocation.latitude, userLocation.longitude, "My Location")
                            },
                            onDialPolice = { num -> viewModel.dialPolice(num) },
                            onToggleAlarm = { viewModel.toggleAlarm() },
                            onStartAudioRecording = { viewModel.startDistressAudioRecording() },
                            onStopAudioRecording = { viewModel.stopDistressAudioRecording() },
                            onPlayAudioRecording = { viewModel.playAudioRecording() },
                            onStopAudioPlayback = { viewModel.stopAudioPlayback() },
                            onShareAudioWhatsApp = { viewModel.shareDistressAudioViaWhatsApp() },
                            onSharePairingCodeWhatsApp = { text ->
                                viewModel.sosDispatcher.dispatchToWhatsAppNumber("", text)
                            },
                            onOpenSwitchMode = { showProfileDialog = true }
                        )
                    }

                    UserRole.PARENT -> {
                        ParentHomeScreen(
                            userProfile = userProfile,
                            childLocation = userLocation,
                            childBattery = batteryState,
                            sosHistory = sosHistory,
                            safeZones = safeZones,
                            onUpdatePairingCode = { code, name, phone ->
                                viewModel.updateChildPairing(code, name, phone)
                            },
                            onCallChild = { phone -> viewModel.dialPolice(phone) },
                            onWhatsAppCheckIn = { phone, name ->
                                viewModel.sendParentCheckInWhatsApp(phone, name)
                            },
                            onOpenGoogleMaps = { lat, lng, label ->
                                viewModel.openGoogleMaps(lat, lng, label)
                            },
                            onForwardToPolice = { num -> viewModel.dialPolice(num) },
                            onPlayAudioRecording = { path -> viewModel.playAudioRecording(path) },
                            onOpenSwitchMode = { showProfileDialog = true }
                        )
                    }

                    UserRole.POLICE -> {
                        PoliceHomeScreen(
                            userProfile = userProfile,
                            currentLocation = userLocation,
                            sosHistory = sosHistory,
                            safeZones = safeZones,
                            onDispatchPcrVan = { incidentInfo, phone ->
                                viewModel.sendPoliceDispatchWhatsApp(phone, incidentInfo)
                            },
                            onCallCitizen = { phone -> viewModel.dialPolice(phone) },
                            onOpenGoogleMaps = { lat, lng, label ->
                                viewModel.openGoogleMaps(lat, lng, label)
                            },
                            onPlayAudioRecording = { path -> viewModel.playAudioRecording(path) },
                            onDialHelpline = { num -> viewModel.dialPolice(num) },
                            onOpenSwitchMode = { showProfileDialog = true }
                        )
                    }
                }
            }

            composable(Screen.SafeRoute.route) {
                val selectedRoute by viewModel.selectedSafeRoute.collectAsStateWithLifecycle()
                val isNightMode by viewModel.isNightRouteAnalysis.collectAsStateWithLifecycle()
                SafeRouteScreen(
                    selectedRoute = selectedRoute,
                    allRoutes = viewModel.allSafeRoutes,
                    isNightMode = isNightMode,
                    onSelectRoute = { route -> viewModel.selectSafeRoute(route) },
                    onToggleNightMode = { isNight -> viewModel.toggleNightRouteAnalysis(isNight) },
                    onAnalyzeCustomRoute = { origin, dest -> viewModel.analyzeCustomRoute(origin, dest) },
                    onStartNavigation = { origin, dest -> viewModel.openRouteInGoogleMaps(origin, dest) },
                    onShareRoutePlan = { route, isNight -> viewModel.shareSafeRouteWithCircle(route, isNight) },
                    onCallNumber = { num -> viewModel.dialPolice(num) }
                )
            }

            composable(Screen.Map.route) {
                OfflineMapScreen(
                    userLocation = userLocation,
                    safeZones = safeZones,
                    selectedZone = selectedZone,
                    onSelectZone = { zone -> viewModel.setSelectedSafeZone(zone) },
                    onOpenGoogleMaps = { lat, lng, label -> viewModel.openGoogleMaps(lat, lng, label) },
                    onDialNumber = { num -> viewModel.dialPolice(num) },
                    onShareLocationToPolice = { zone ->
                        viewModel.dispatchToSpecificContactWhatsApp(
                            com.example.model.EmergencyContact(
                                name = zone.name,
                                phoneNumber = zone.phoneNumber,
                                relationship = "Police"
                            )
                        )
                    }
                )
            }

            composable(Screen.Battery.route) {
                BatteryMonitorScreen(
                    batteryState = batteryState,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Contacts.route) {
                EmergencyContactsScreen(
                    contacts = contacts,
                    onSaveContact = { contact -> viewModel.saveContact(contact) },
                    onDeleteContact = { contact -> viewModel.deleteContact(contact) },
                    onTestWhatsApp = { contact -> viewModel.dispatchToSpecificContactWhatsApp(contact) }
                )
            }

            composable(Screen.History.route) {
                SosHistoryScreen(
                    historyList = sosHistory,
                    onOpenGoogleMaps = { lat, lng, label -> viewModel.openGoogleMaps(lat, lng, label) },
                    onClearHistory = { viewModel.clearHistory() }
                )
            }
        }

        // Multi-Contact WhatsApp Broadcast Dialog after SOS
        if (sosState.showMultiContactDialog) {
            MultiContactSosDialog(
                contacts = sosState.pendingContactsToDispatch,
                dispatchedMessage = sosState.lastDispatchedMessage,
                onDispatchWhatsApp = { contact -> viewModel.dispatchToSpecificContactWhatsApp(contact) },
                onDispatchSms = { contact -> viewModel.dispatchSmsToContact(contact) },
                onDialPolice = { num -> viewModel.dialPolice(num) },
                onCopyMessage = { viewModel.copySosMessageToClipboard() },
                onDismiss = { viewModel.dismissMultiContactDialog() },
                onStopAlarm = { viewModel.stopAlarm() }
            )
        }

        // Profile & Mode Switcher Dialog
        if (showProfileDialog) {
            ProfileDialog(
                userProfile = userProfile,
                onSwitchRole = { newRole -> viewModel.switchRole(newRole) },
                onResetProfile = { viewModel.resetProfileToSignUp() },
                onDismiss = { showProfileDialog = false }
            )
        }
    }
}
