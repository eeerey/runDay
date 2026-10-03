package com.cina.redmifitness.feature.olahraga

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

data class RouteResult(
    val distanceMeters: Double,
    val durationSeconds: Double,
    val coordinates: List<Pair<Double, Double>>
)

object RoutingService {

    private const val API_KEY =
        "MASUKKAN_API_KEY_ORS_DI_SINI"

    suspend fun getRoute(
        startLatitude: Double,
        startLongitude: Double,
        endLatitude: Double,
        endLongitude: Double,
        activity: String
    ): Result<RouteResult> =
        withContext(Dispatchers.IO) {

            try {

                val profile =
                    when (activity.lowercase()) {

                        "lari",
                        "jalan" ->
                            "foot-walking"

                        "sepeda" ->
                            "cycling-regular"

                        else ->
                            "foot-walking"
                    }

                val urlString =
                    "https://api.openrouteservice.org/v2/directions/" +
                            "$profile" +
                            "?api_key=$API_KEY" +
                            "&start=$startLongitude,$startLatitude" +
                            "&end=$endLongitude,$endLatitude"

                val connection =
                    URL(urlString)
                        .openConnection()
                            as HttpURLConnection

                connection.requestMethod = "GET"
                connection.connectTimeout = 15000
                connection.readTimeout = 15000

                val responseCode =
                    connection.responseCode

                if (responseCode !in 200..299) {

                    return@withContext Result.failure(
                        Exception(
                            "Routing API error: HTTP $responseCode"
                        )
                    )
                }

                val response =
                    connection.inputStream
                        .bufferedReader()
                        .use {
                            it.readText()
                        }

                connection.disconnect()

                val json =
                    JSONObject(response)

                val feature =
                    json
                        .getJSONArray("features")
                        .getJSONObject(0)

                val properties =
                    feature.getJSONObject(
                        "properties"
                    )

                val summary =
                    properties.getJSONObject(
                        "summary"
                    )

                val distance =
                    summary.getDouble(
                        "distance"
                    )

                val duration =
                    summary.getDouble(
                        "duration"
                    )

                val geometry =
                    feature.getJSONObject(
                        "geometry"
                    )

                val coordinates =
                    geometry.getJSONArray(
                        "coordinates"
                    )

                val points =
                    mutableListOf<Pair<Double, Double>>()

                for (
                i in 0 until coordinates.length()
                ) {

                    val point =
                        coordinates.getJSONArray(i)

                    val longitude =
                        point.getDouble(0)

                    val latitude =
                        point.getDouble(1)

                    points.add(
                        Pair(
                            latitude,
                            longitude
                        )
                    )
                }

                Result.success(
                    RouteResult(
                        distanceMeters =
                            distance,

                        durationSeconds =
                            duration,

                        coordinates =
                            points
                    )
                )

            } catch (e: Exception) {

                Result.failure(e)
            }
        }
}