package com.cina.redmifitness.feature.olahraga

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices

@Composable
fun OlahragaScreen(
    onCariRute: (
        String,
        String,
        String,
        Double,
        Double
    ) -> Unit
) {

    val context = LocalContext.current

    // ==========================================
    // LOCATION CLIENT
    // ==========================================

    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    // Menyimpan latitude dan longitude
    var latitude by remember {
        mutableStateOf<Double?>(null)
    }

    var longitude by remember {
        mutableStateOf<Double?>(null)
    }

    // ==========================================
    // LOCATION PERMISSION
    // ==========================================

    val locationPermissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->

            val fineLocationGranted =
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true

            val coarseLocationGranted =
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

            if (fineLocationGranted || coarseLocationGranted) {

                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location ->

                        if (location != null) {
                            latitude = location.latitude
                            longitude = location.longitude
                        }
                    }
            }
        }

    // ==========================================
    // GET CURRENT LOCATION
    // ==========================================

    fun getCurrentLocation() {

        val fineGranted =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        val coarseGranted =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        if (fineGranted || coarseGranted) {

            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->

                    if (location != null) {
                        latitude = location.latitude
                        longitude = location.longitude
                    }
                }

        } else {

            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    // ==========================================
    // REQUEST LOCATION WHEN SCREEN OPENS
    // ==========================================

    LaunchedEffect(Unit) {
        getCurrentLocation()
    }

    // ==========================================
    // SPORTS STATE
    // ==========================================

    var selectedActivity by remember {
        mutableStateOf("Lari")
    }

    var selectedLevel by remember {
        mutableStateOf("Sedang")
    }

    var selectedDistance by remember {
        mutableStateOf("5 km")
    }

    // ==========================================
    // DATA
    // ==========================================

    val activities = listOf(
        "Lari",
        "Sepeda",
        "Jalan"
    )

    val levels = listOf(
        "Sedang",
        "Sulit",
        "Terampil"
    )

    val distances = listOf(
        "3 km",
        "5 km",
        "7 km",
        "10 km"
    )

    // ==========================================
    // MAIN SCREEN
    // ==========================================

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.background
            )
            .verticalScroll(
                rememberScrollState()
            )
            .padding(
                horizontal = 20.dp
            )
    ) {

        // ======================================
        // HEADER
        // ======================================

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Text(
            text = "Mulai Olahraga",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        Text(
            text = "Pilih aktivitas dan targetmu hari ini.",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        // ======================================
        // JENIS OLAHRAGA
        // ======================================

        Text(
            text = "Jenis Olahraga",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            items(activities) { activity ->

                ActivityCard(
                    title = activity,
                    selected = selectedActivity == activity,
                    onClick = {
                        selectedActivity = activity
                    }
                )
            }
        }

        Spacer(
            modifier = Modifier.height(28.dp)
        )

        // ======================================
        // LEVEL
        // ======================================

        Text(
            text = "Level",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            levels.forEach { level ->

                LevelButton(
                    title = level,
                    selected = selectedLevel == level,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        selectedLevel = level
                    }
                )
            }
        }

        Spacer(
            modifier = Modifier.height(28.dp)
        )

        // ======================================
        // TARGET JARAK
        // ======================================

        Text(
            text = "Target Jarak",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            items(distances) { distance ->

                DistanceButton(
                    distance = distance,
                    selected = selectedDistance == distance,
                    onClick = {
                        selectedDistance = distance
                    }
                )
            }
        }

        Spacer(
            modifier = Modifier.height(28.dp)
        )

        // ======================================
        // LOKASI GPS
        // ======================================

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor =
                    MaterialTheme.colorScheme.surfaceVariant
            )
        ) {

            Column(
                modifier = Modifier.padding(20.dp)
            ) {

                Text(
                    text = "Lokasi Saya",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                if (
                    latitude != null &&
                    longitude != null
                ) {

                    Text(
                        text = "Latitude  : %.6f".format(
                            latitude
                        )
                    )

                    Text(
                        text = "Longitude : %.6f".format(
                            longitude
                        )
                    )

                } else {

                    Text(
                        text = "Sedang mengambil lokasi GPS..."
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        // ======================================
        // RENCANA OLAHRAGA
        // ======================================

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor =
                    MaterialTheme.colorScheme.surfaceVariant
            )
        ) {

            Column(
                modifier = Modifier.padding(20.dp)
            ) {

                Text(
                    text = "Rencana Olahraga",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(14.dp)
                )

                InfoRow(
                    label = "Aktivitas",
                    value = selectedActivity
                )

                InfoRow(
                    label = "Level",
                    value = selectedLevel
                )

                InfoRow(
                    label = "Target",
                    value = selectedDistance
                )
            }
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        // ======================================
        // GPS STATUS
        // ======================================

        Text(
            text =
                if (
                    latitude != null &&
                    longitude != null
                ) {

                    "GPS: %.6f, %.6f".format(
                        latitude,
                        longitude
                    )

                } else {

                    "GPS belum tersedia"
                },

            style = MaterialTheme.typography.bodySmall
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        // ======================================
        // BUTTON CARI RUTE
        // ======================================

        Button(
            onClick = {

                if (
                    latitude != null &&
                    longitude != null
                ) {

                    onCariRute(
                        selectedActivity,
                        selectedLevel,
                        selectedDistance,
                        latitude!!,
                        longitude!!
                    )
                }
            },

            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),

            shape = RoundedCornerShape(16.dp),

            colors = ButtonDefaults.buttonColors()
        ) {

            Text(
                text = "Cari Rute",
                fontWeight = FontWeight.Bold
            )
        }

        // Jarak dari tombol ke bagian bawah
        Spacer(
            modifier = Modifier.height(24.dp)
        )
    }
}

// ==================================================
// ACTIVITY CARD
// ==================================================

@Composable
private fun ActivityCard(
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .width(110.dp)
            .height(90.dp)
            .clickable {
                onClick()
            },

        shape = RoundedCornerShape(18.dp),

        colors = CardDefaults.cardColors(
            containerColor =
                if (selected) {

                    MaterialTheme.colorScheme.primary

                } else {

                    MaterialTheme.colorScheme.surfaceVariant
                }
        )
    ) {

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {

            Text(
                text = title,
                fontWeight = FontWeight.Bold,

                color =
                    if (selected) {

                        MaterialTheme.colorScheme.onPrimary

                    } else {

                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
            )
        }
    }
}

// ==================================================
// LEVEL BUTTON
// ==================================================

@Composable
private fun LevelButton(
    title: String,
    selected: Boolean,
    modifier: Modifier,
    onClick: () -> Unit
) {

    if (selected) {

        Button(
            onClick = onClick,

            modifier = modifier
                .height(48.dp),

            shape = RoundedCornerShape(14.dp)
        ) {

            Text(
                text = title,
                fontWeight = FontWeight.SemiBold
            )
        }

    } else {

        OutlinedButton(
            onClick = onClick,

            modifier = modifier
                .height(48.dp),

            shape = RoundedCornerShape(14.dp)
        ) {

            Text(
                text = title
            )
        }
    }
}

// ==================================================
// DISTANCE BUTTON
// ==================================================

@Composable
private fun DistanceButton(
    distance: String,
    selected: Boolean,
    onClick: () -> Unit
) {

    if (selected) {

        Button(
            onClick = onClick,
            shape = RoundedCornerShape(14.dp)
        ) {

            Text(
                text = distance
            )
        }

    } else {

        OutlinedButton(
            onClick = onClick,
            shape = RoundedCornerShape(14.dp)
        ) {

            Text(
                text = distance
            )
        }
    }
}

// ==================================================
// INFO ROW
// ==================================================

@Composable
private fun InfoRow(
    label: String,
    value: String
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                vertical = 5.dp
            ),

        horizontalArrangement =
            Arrangement.SpaceBetween
    ) {

        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium
        )

        Text(
            text = value,
            fontWeight = FontWeight.SemiBold
        )
    }
}