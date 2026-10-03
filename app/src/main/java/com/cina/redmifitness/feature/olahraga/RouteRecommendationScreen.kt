package com.cina.redmifitness.feature.olahraga

import android.os.Bundle

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding

import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

import org.maplibre.android.MapLibre
import org.maplibre.android.annotations.MarkerOptions
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.geometry.LatLngBounds
import org.maplibre.android.maps.MapView

import org.maplibre.android.style.layers.LineLayer
import org.maplibre.android.style.layers.PropertyFactory.lineColor
import org.maplibre.android.style.layers.PropertyFactory.lineWidth
import org.maplibre.android.style.sources.GeoJsonSource

import org.maplibre.geojson.Feature
import org.maplibre.geojson.LineString
import org.maplibre.geojson.Point


@Composable
fun RouteRecommendationScreen(
    activity: String,
    level: String,
    distance: String,
    latitude: Double?,
    longitude: Double?
) {

    val context = LocalContext.current

    // =====================================================
    // INISIALISASI MAPLIBRE
    // =====================================================

    remember {
        MapLibre.getInstance(context)
    }

    // =====================================================
    // MAPTILER API KEY
    // =====================================================

    val mapTilerKey =
        "jfQuDc8h0v3kJyjHsR2W"

    // =====================================================
    // STATE ROUTING
    // =====================================================

    var routeResult by remember {
        mutableStateOf<RouteResult?>(null)
    }

    var routingLoading by remember {
        mutableStateOf(false)
    }

    var routingError by remember {
        mutableStateOf<String?>(null)
    }

    // =====================================================
    // STATE MAP
    // =====================================================

    var mapView by remember {
        mutableStateOf<MapView?>(null)
    }

    var mapReady by remember {
        mutableStateOf(false)
    }

    // =====================================================
    // REQUEST ROUTE
    // =====================================================

    LaunchedEffect(
        latitude,
        longitude,
        activity,
        distance
    ) {

        if (
            latitude == null ||
            longitude == null
        ) {
            return@LaunchedEffect
        }

        routingLoading = true
        routingError = null
        routeResult = null

        try {

            // =================================================
            // AMBIL JARAK
            // =================================================

            val distanceKm =
                distance
                    .replace("km", "")
                    .trim()
                    .toDoubleOrNull()
                    ?: 5.0

            // =================================================
            // DESTINATION SEMENTARA
            //
            // Ini masih digunakan untuk TEST routing.
            // Nanti dapat diganti dengan circular route.
            // =================================================

            val delta =
                (distanceKm / 111.0) * 0.5

            val destinationLatitude =
                latitude + delta

            val destinationLongitude =
                longitude + delta

            // =================================================
            // REQUEST ROUTING
            // =================================================

            val result =
                RoutingService.getRoute(
                    startLatitude = latitude,
                    startLongitude = longitude,
                    endLatitude = destinationLatitude,
                    endLongitude = destinationLongitude,
                    activity = activity
                )

            // =================================================
            // HASIL ROUTING
            // =================================================

            result
                .onSuccess { route ->

                    routeResult = route
                }
                .onFailure { error ->

                    routingError =
                        error.message
                            ?: "Gagal mendapatkan rute"
                }

        } catch (e: Exception) {

            routingError =
                e.message
                    ?: "Terjadi kesalahan routing"

        } finally {

            routingLoading = false
        }
    }

    // =====================================================
    // UI
    // =====================================================

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),

        verticalArrangement =
            Arrangement.Top
    ) {

        // =================================================
        // HEADER
        // =================================================

        Text(
            text = "Rekomendasi Rute",
            style =
                MaterialTheme.typography.headlineMedium
        )

        Spacer(
            modifier =
                Modifier.height(8.dp)
        )

        Text(
            text =
                "Rute yang sesuai dengan pilihan olahraga kamu.",
            style =
                MaterialTheme.typography.bodyMedium
        )

        Spacer(
            modifier =
                Modifier.height(20.dp)
        )

        // =================================================
        // MAP
        // =================================================

        AndroidView(

            modifier = Modifier
                .fillMaxWidth()
                .height(350.dp),

            factory = { mapContext ->

                MapView(mapContext).also { view ->

                    // Simpan MapView
                    mapView = view

                    // Lifecycle MapView
                    view.onCreate(Bundle())

                    // Ambil MapLibre Map
                    view.getMapAsync { map ->

                        // =================================================
                        // MAPTILER STYLE
                        // =================================================

                        val styleUrl =
                            "https://api.maptiler.com/maps/streets-v4/style.json?key=$mapTilerKey"

                        map.setStyle(styleUrl) {

                            // Map siap
                            mapReady = true

                            // =================================================
                            // MARKER USER
                            // =================================================

                            if (
                                latitude != null &&
                                longitude != null
                            ) {

                                val userLocation =
                                    LatLng(
                                        latitude,
                                        longitude
                                    )

                                map.addMarker(

                                    MarkerOptions()
                                        .position(
                                            userLocation
                                        )
                                        .title(
                                            "Lokasi Saya"
                                        )
                                        .snippet(
                                            "Lat: %.6f, Lng: %.6f"
                                                .format(
                                                    latitude,
                                                    longitude
                                                )
                                        )
                                )

                                // =================================================
                                // KAMERA AWAL
                                // =================================================

                                map.cameraPosition =
                                    CameraPosition.Builder()
                                        .target(
                                            userLocation
                                        )
                                        .zoom(15.5)
                                        .tilt(30.0)
                                        .bearing(0.0)
                                        .build()
                            }
                        }
                    }
                }
            }
        )

        // =================================================
        // GAMBAR ROUTE
        // =================================================

        LaunchedEffect(
            routeResult,
            mapReady
        ) {

            val route =
                routeResult
                    ?: return@LaunchedEffect

            val view =
                mapView
                    ?: return@LaunchedEffect

            if (
                route.coordinates.isEmpty()
            ) {
                return@LaunchedEffect
            }

            // Tunggu MapLibre siap
            view.getMapAsync { map ->

                val style =
                    map.style
                        ?: return@getMapAsync

                // =================================================
                // KONVERSI KOORDINAT
                //
                // RouteResult menggunakan:
                // Pair(latitude, longitude)
                //
                // MapLibre Point menggunakan:
                // longitude, latitude
                // =================================================

                val points =
                    route.coordinates.map {

                        Point.fromLngLat(
                            it.second,
                            it.first
                        )
                    }

                // =================================================
                // LINE STRING
                // =================================================

                val lineString =
                    LineString.fromLngLats(
                        points
                    )

                // =================================================
                // GEOJSON FEATURE
                // =================================================

                val feature =
                    Feature.fromGeometry(
                        lineString
                    )

                // =================================================
                // HAPUS ROUTE LAMA
                // =================================================

                if (
                    style.getLayer("route-layer") != null
                ) {

                    style.removeLayer(
                        "route-layer"
                    )
                }

                if (
                    style.getSource("route-source") != null
                ) {

                    style.removeSource(
                        "route-source"
                    )
                }

                // =================================================
                // GEOJSON SOURCE
                // =================================================

                val source =
                    GeoJsonSource(
                        "route-source",
                        feature
                    )

                style.addSource(
                    source
                )

                // =================================================
                // LINE LAYER
                // =================================================

                val lineLayer =
                    LineLayer(
                        "route-layer",
                        "route-source"
                    )

                lineLayer.setProperties(

                    lineColor(
                        "#1976D2"
                    ),

                    lineWidth(
                        6f
                    )
                )

                style.addLayer(
                    lineLayer
                )

                // =================================================
                // FIT CAMERA KE SELURUH ROUTE
                // =================================================

                try {

                    val boundsBuilder =
                        LatLngBounds.Builder()

                    route.coordinates.forEach {

                        boundsBuilder.include(
                            LatLng(
                                it.first,
                                it.second
                            )
                        )
                    }

                    val bounds =
                        boundsBuilder.build()

                    map.animateCamera(

                        CameraUpdateFactory
                            .newLatLngBounds(
                                bounds,
                                70
                            )
                    )

                } catch (_: Exception) {

                    // Abaikan jika bounds belum valid.
                }
            }
        }

        Spacer(
            modifier =
                Modifier.height(12.dp)
        )

        // =================================================
        // STATUS ROUTING
        // =================================================

        if (routingLoading) {

            Text(
                text =
                    "Sedang mencari rute..."
            )
        }

        routingError?.let { error ->

            Text(
                text =
                    "Routing error:\n$error"
            )
        }

        Spacer(
            modifier =
                Modifier.height(12.dp)
        )

        // =================================================
        // DETAIL RUTE
        // =================================================

        Card(
            modifier =
                Modifier.fillMaxWidth()
        ) {

            Column(
                modifier =
                    Modifier.padding(18.dp)
            ) {

                Text(
                    text =
                        "Detail Rute",

                    style =
                        MaterialTheme.typography.titleLarge
                )

                Spacer(
                    modifier =
                        Modifier.height(12.dp)
                )

                // =================================================
                // JARAK DAN DURASI
                // =================================================

                val route =
                    routeResult

                if (route != null) {

                    val distanceKm =
                        route.distanceMeters /
                                1000.0

                    val durationMinutes =
                        route.durationSeconds /
                                60.0

                    Text(
                        text =
                            "Jarak              %.2f km"
                                .format(
                                    distanceKm
                                )
                    )

                    Text(
                        text =
                            "Estimasi waktu     %.0f menit"
                                .format(
                                    durationMinutes
                                )
                    )

                } else {

                    Text(
                        text =
                            "Jarak              $distance"
                    )

                    Text(
                        text =
                            "Estimasi waktu     -"
                    )
                }

                // =================================================
                // AKTIVITAS
                // =================================================

                Text(
                    text =
                        "Aktivitas           $activity"
                )

                // =================================================
                // LEVEL
                // =================================================

                Text(
                    text =
                        "Level               $level"
                )

                Spacer(
                    modifier =
                        Modifier.height(12.dp)
                )

                // =================================================
                // GPS USER
                // =================================================

                if (
                    latitude != null &&
                    longitude != null
                ) {

                    Text(
                        text =
                            "Lokasi Saya",

                        style =
                            MaterialTheme.typography.titleMedium
                    )

                    Spacer(
                        modifier =
                            Modifier.height(4.dp)
                    )

                    Text(
                        text =
                            "Latitude  : %.6f"
                                .format(
                                    latitude
                                )
                    )

                    Text(
                        text =
                            "Longitude : %.6f"
                                .format(
                                    longitude
                                )
                    )

                } else {

                    Text(
                        text =
                            "Lokasi GPS belum tersedia"
                    )
                }
            }
        }

        Spacer(
            modifier =
                Modifier.height(20.dp)
        )

        // =================================================
        // BUTTON MULAI
        // =================================================

        Button(

            onClick = {

                // Nanti:
                //
                // 1. Cek smartwatch
                // 2. Mulai tracking
                // 3. GPS realtime
                // 4. Hitung pace
                // 5. Hitung jarak
                // 6. Tampilkan heart rate

            },

            modifier =
                Modifier.fillMaxWidth(),

            enabled =
                routeResult != null &&
                        !routingLoading
        ) {

            Text(
                text =
                    "Pilih Rute & Mulai"
            )
        }

        Spacer(
            modifier =
                Modifier.height(20.dp)
        )
    }
}