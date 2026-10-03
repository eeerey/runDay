package com.cina.redmifitness.ui.components

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import org.maplibre.android.MapLibre
import org.maplibre.android.maps.MapView

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun MapViewComposable() {

    AndroidView(
        factory = { context ->

            MapLibre.getInstance(context)

            MapView(context).apply {

                getMapAsync { map ->

                    map.setStyle(
                        "https://demotiles.maplibre.org/style.json"
                    )
                }
            }
        }
    )
}