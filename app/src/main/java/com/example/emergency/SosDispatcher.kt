package com.example.emergency

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioManager
import android.media.ToneGenerator
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.location.UserLocation
import com.example.model.BatteryState
import com.example.model.EmergencyContact
import java.io.File
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SosDispatcher(private val context: Context) {

    private var toneGenerator: ToneGenerator? = null
    private var isAlarmPlaying = false

    fun formatSosMessage(
        userName: String,
        location: UserLocation,
        battery: BatteryState,
        pairingCode: String = "",
        hasLiveAudioRecording: Boolean = true,
        customEmergencyNote: String = ""
    ): String {
        val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm:ss a", Locale.getDefault())
        val timeStr = dateFormat.format(Date(location.timestamp))

        val batteryInfo = battery.getTelemetrySummary()
        val mapsUrl = location.toGoogleMapsUrl()

        val builder = StringBuilder()
        builder.append("🚨 *HELP ME! EMERGENCY SOS ALERT* 🚨\n\n")
        builder.append("⚠️ *URGENT: I AM IN DANGER AND NEED IMMEDIATE HELP!*\n\n")

        if (customEmergencyNote.isNotBlank()) {
            builder.append("💬 *Distress Note:* $customEmergencyNote\n\n")
        }

        builder.append("📍 *Live GPS Google Maps Location:*\n$mapsUrl\n\n")
        builder.append("📌 *GPS Coordinates:* ${location.toFormattedCoordinates()} (Accuracy: ±${location.accuracy.toInt()}m)\n")
        builder.append("🏢 *Address/Landmark:* ${location.address}\n")
        builder.append("🔋 *Device Battery:* $batteryInfo\n")
        builder.append("🕒 *Alert Timestamp:* $timeStr\n")
        builder.append("👤 *Sender:* ${if (userName.isBlank()) "Circle Member" else userName}\n")

        if (pairingCode.isNotBlank()) {
            builder.append("🔗 *Safety Circle Code:* $pairingCode\n")
        }

        if (hasLiveAudioRecording) {
            builder.append("🎙️ *Audio Evidence:* Live audio distress recording captured on device.\n")
        }

        builder.append("\n🚨 *ACTION REQUIRED:* Call immediately or alert Nagpur Police (112) / Women Helpline (1091)!")

        return builder.toString()
    }

    fun formatSmsHelpMessage(
        userName: String,
        location: UserLocation,
        battery: BatteryState,
        customNote: String = ""
    ): String {
        val mapsUrl = location.toGoogleMapsUrl()
        val sender = if (userName.isBlank()) "Circle Member" else userName
        val notePart = if (customNote.isNotBlank()) " Note: $customNote." else ""
        return "HELP ME! 🚨 I am in danger and need immediate help!$notePart Live GPS: $mapsUrl | At: ${location.address} | Battery: ${battery.level}% | From: $sender"
    }

    /**
     * Sends direct SMS in the background using Android SmsManager.
     * Fallback opens SMS application if permission is missing or direct sending fails.
     */
    fun sendDirectSms(phoneNumber: String, message: String): Boolean {
        val cleanPhone = cleanPhoneNumber(phoneNumber)
        if (cleanPhone.isBlank()) return false

        val hasSmsPermission = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.SEND_SMS
        ) == PackageManager.PERMISSION_GRANTED

        if (hasSmsPermission) {
            try {
                val smsManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    context.getSystemService(SmsManager::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    SmsManager.getDefault()
                }

                if (message.length > 160) {
                    val parts = smsManager.divideMessage(message)
                    smsManager.sendMultipartTextMessage(cleanPhone, null, parts, null, null)
                } else {
                    smsManager.sendTextMessage(cleanPhone, null, message, null, null)
                }
                Log.d("SosDispatcher", "Direct SMS successfully dispatched to $cleanPhone")
                return true
            } catch (e: Exception) {
                Log.e("SosDispatcher", "Direct SMS failed: ${e.message}, falling back to intent", e)
            }
        }

        // Fallback to SMS Intent
        dispatchSms(cleanPhone, message)
        return false
    }

    fun dispatchSosToWhatsApp(
        contact: EmergencyContact,
        message: String,
        audioFilePath: String? = null
    ): Boolean {
        return dispatchToWhatsAppNumber(contact.phoneNumber, message, audioFilePath)
    }

    fun dispatchToWhatsAppNumber(
        phoneNumber: String,
        message: String,
        audioFilePath: String? = null
    ): Boolean {
        val cleanPhone = cleanPhoneNumber(phoneNumber)
        
        // If there's an audio recording, try sharing with audio attachment first
        if (!audioFilePath.isNullOrBlank()) {
            val audioFile = File(audioFilePath)
            if (audioFile.exists() && audioFile.length() > 0) {
                try {
                    val contentUri: Uri = FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.fileprovider",
                        audioFile
                    )
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "audio/*"
                        putExtra(Intent.EXTRA_STREAM, contentUri)
                        putExtra(Intent.EXTRA_TEXT, message)
                        if (cleanPhone.isNotBlank() && cleanPhone != "112" && cleanPhone != "100" && cleanPhone != "1091") {
                            putExtra("jid", "$cleanPhone@s.whatsapp.net")
                        }
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    shareIntent.setPackage("com.whatsapp")
                    context.startActivity(shareIntent)
                    return true
                } catch (_: Exception) {
                    // fallback to text dispatch below
                }
            }
        }

        return try {
            val encodedMessage = URLEncoder.encode(message, "UTF-8")
            val whatsappUrl = if (cleanPhone.isNotBlank() && cleanPhone != "112" && cleanPhone != "100" && cleanPhone != "1091") {
                "https://api.whatsapp.com/send?phone=$cleanPhone&text=$encodedMessage"
            } else {
                "https://api.whatsapp.com/send?text=$encodedMessage"
            }

            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(whatsappUrl)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }

            context.startActivity(intent)
            true
        } catch (e: Exception) {
            // Fallback: Copy to clipboard and open standard share
            copyToClipboard("SOS Emergency Message", message)
            try {
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, message)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(Intent.createChooser(shareIntent, "Send SOS Alert via...").apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                })
                true
            } catch (_: Exception) {
                false
            }
        }
    }

    fun dispatchSms(phoneNumber: String, message: String) {
        try {
            val cleanPhone = cleanPhoneNumber(phoneNumber)
            val uri = Uri.parse("smsto:$cleanPhone")
            val intent = Intent(Intent.ACTION_SENDTO, uri).apply {
                putExtra("sms_body", message)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Could not open SMS: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun callEmergencyNumber(phoneNumber: String = "112") {
        try {
            val cleanPhone = cleanPhoneNumber(phoneNumber)
            val hasCallPermission = ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.CALL_PHONE
            ) == PackageManager.PERMISSION_GRANTED

            val intent = if (hasCallPermission) {
                Intent(Intent.ACTION_CALL).apply {
                    data = Uri.parse("tel:$cleanPhone")
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
            } else {
                Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:$cleanPhone")
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            dialEmergency(phoneNumber)
        }
    }

    fun dialEmergency(phoneNumber: String = "112") {
        try {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$phoneNumber")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Dialer error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun openInGoogleMaps(lat: Double, lng: Double, label: String = "Emergency Location") {
        try {
            val uri = Uri.parse("geo:$lat,$lng?q=$lat,$lng($label)")
            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                setPackage("com.google.android.apps.maps")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            // Fallback to browser Google Maps
            val webUri = Uri.parse("https://maps.google.com/?q=$lat,$lng")
            val webIntent = Intent(Intent.ACTION_VIEW, webUri).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(webIntent)
        }
    }

    fun startEmergencyAlarm() {
        if (isAlarmPlaying) return
        isAlarmPlaying = true

        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_ALARM, 100)
            toneGenerator?.startTone(ToneGenerator.TONE_CDMA_EMERGENCY_RINGBACK, 3000)
        } catch (_: Exception) {}

        triggerSosVibration()
    }

    fun stopEmergencyAlarm() {
        isAlarmPlaying = false
        try {
            toneGenerator?.stopTone()
            toneGenerator?.release()
            toneGenerator = null
        } catch (_: Exception) {}
    }

    private fun triggerSosVibration() {
        try {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }

            if (vibrator?.hasVibrator() == true) {
                // SOS pattern: 3 short, 3 long, 3 short
                val timings = longArrayOf(0, 200, 100, 200, 100, 200, 300, 500, 100, 500, 100, 500, 300, 200, 100, 200, 100, 200)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createWaveform(timings, -1))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(timings, -1)
                }
            }
        } catch (_: Exception) {}
    }

    fun copyToClipboard(label: String, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard?.setPrimaryClip(clip)
    }

    fun cleanPhoneNumber(phone: String): String {
        var clean = phone.replace(Regex("[^0-9+]"), "")
        if (!clean.startsWith("+") && clean.length == 10) {
            clean = "+91$clean" // Default to India prefix for 10-digit mobile
        }
        return clean
    }
}
