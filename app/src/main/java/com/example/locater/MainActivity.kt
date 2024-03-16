package com.example.locater

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.locater.ui.theme.LocaterTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LocaterTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Surface(
                        modifier = Modifier.fillMaxSize().padding(innerPadding),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        // State variables to manage location information and permission result text
                        var locationText by remember { mutableStateOf("No location obtained, You Might Disabled Location.") }
                        var showPermissionResultText by remember { mutableStateOf(false) }
                        var permissionResultText by remember { mutableStateOf("Permission Granted...") }
                        // State variable to start Locating
                        var startLocating by remember { mutableStateOf(false) }

                        if (startLocating) {
                            // Request location permission using a Compose function
                            RequestLocationPermission(
                                onPermissionGranted = {
                                    // Callback when permission is granted
                                    showPermissionResultText = true
                                    // Get the last known user location
                                    getLastUserLocation(context = this,
                                        onGetLastLocationSuccess = {
                                            locationText =
                                                "Location using LAST-LOCATION: LATITUDE: ${it.first}, LONGITUDE: ${it.second} \n " +
                                                        "${getReadableLocation(it.first, it.second, this)}"
                                        },
                                        onGetLastLocationFailed = { exception ->
                                            showPermissionResultText = true
                                            locationText =
                                                exception.localizedMessage
                                                    ?: "Error Getting Last Location"
                                        },
                                        onGetLastLocationIsNull = {
                                            // Get the current user location
                                            getCurrentLocation(context = this,
                                                onGetCurrentLocationSuccess = {
                                                    locationText =
                                                        "Location using CURRENT-LOCATION: LATITUDE: ${it.first}, LONGITUDE: ${it.second} \n " +
                                                                "${getReadableLocation(it.first, it.second, this)}"
                                                },
                                                onGetCurrentLocationFailed = {
                                                    showPermissionResultText = true
                                                    locationText =
                                                        it.localizedMessage
                                                            ?: "Error Getting Current Location"
                                                }
                                            )
                                        }
                                    )
                                },
                                onPermissionDenied = {
                                    // Callback when permission is denied
                                    showPermissionResultText = true
                                    permissionResultText = "Permission is Denied."
                                },
                                onPermissionsRevoked = {
                                    // Callback when permission is revoked
                                    showPermissionResultText = true
                                    permissionResultText = "Permission is Revoked."
                                }
                            )
                        }

                        // Main UI
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Button(
                                onClick = {
                                    startLocating = !startLocating
                                },
                                Modifier
                                    .fillMaxWidth()
                                    .height(64.dp)
                            ) {
                                Text(text = "Start Locating 'Double Click to Relocate'")
                            }
                            // Display a message indicating the permission request process
                            Text(
                                text = "Requesting location permission...",
                                textAlign = TextAlign.Center
                            )

                            // Display permission result and location information if available
                            if (showPermissionResultText) {
                                Text(text = permissionResultText, textAlign = TextAlign.Center)
                                Text(text = locationText, textAlign = TextAlign.Center)
                            }
                        }
                    }
                }
            }
        }
    }
}

