package com.example.face

import android.util.Log
import okhttp3.*
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class GoogleSheetsHelper(private val webAppUrl: String) {
    private val client = OkHttpClient()

    // Method to append a new row in Google Sheets (Registration)
    fun appendRow(values: Map<String, String>, callback: (Boolean) -> Unit) {
        val json = createJsonPayload("append", values)
        val requestBody = json.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        val request = Request.Builder()
            .url(webAppUrl)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("GoogleSheetsHelper", "Append request failed: ${e.message}")
                callback(false)
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!it.isSuccessful) {
                        Log.e("GoogleSheetsHelper", "Append response not successful: ${response.message}")
                        callback(false)
                    } else {
                        callback(true)
                    }
                }
            }
        })
    }

    // Method to verify login credentials
    fun verifyLogin(email: String, password: String, callback: (Boolean, Map<String, Any>?) -> Unit) {
        val json = createJsonPayload("verifyLogin", mapOf("email" to email, "password" to password))
        val requestBody = json.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        val request = Request.Builder()
            .url(webAppUrl)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("GoogleSheetsHelper", "Login request failed: ${e.message}")
                callback(false, null)
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!it.isSuccessful) {
                        Log.e("GoogleSheetsHelper", "Login response not successful: ${response.message}")
                        callback(false, null)
                    } else {
                        val responseBody = it.body?.string()
                        Log.d("GoogleSheetsHelper", "Login response body: $responseBody")
                        val responseJson = Gson().fromJson(responseBody, Map::class.java)
                        val status = responseJson["status"] as? String ?: "failure"
                        if (status == "success") {
                            val data = responseJson["data"] as? Map<String, Any>
                            callback(true, data)
                        } else {
                            callback(false, null)
                        }
                    }
                }
            }
        })
    }

    // Method to update specific fields in Google Sheets
    fun updateRow(email: String, updates: Map<String, Any>, callback: (Boolean) -> Unit) {
        val json = createJsonPayload("update", mapOf("email" to email) + updates)
        val requestBody = json.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        val request = Request.Builder()
            .url(webAppUrl)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("GoogleSheetsHelper", "Update request failed: ${e.message}")
                callback(false)
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!it.isSuccessful) {
                        Log.e("GoogleSheetsHelper", "Update response not successful: ${response.message}")
                        callback(false)
                    } else {
                        callback(true)
                    }
                }
            }
        })
    }

    // Method to update barcode image in Google Sheets
    fun updateBarcode(email: String, barcodeBase64: String, callback: (Boolean) -> Unit) {
        val json = createJsonPayload("updateBarcode", mapOf("email" to email, "barcodeImageBase64" to barcodeBase64))
        val requestBody = json.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        val request = Request.Builder()
            .url(webAppUrl)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("GoogleSheetsHelper", "Update barcode request failed: ${e.message}")
                callback(false)
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!it.isSuccessful) {
                        Log.e("GoogleSheetsHelper", "Update barcode response not successful: ${response.message}")
                        callback(false)
                    } else {
                        callback(true)
                    }
                }
            }
        })
    }

    // Method to create a JSON payload for the given action
    private fun createJsonPayload(action: String, values: Map<String, Any>): String {
        return when (action) {
            "append" -> {
                Gson().toJson(mapOf(
                    "action" to action,
                    "data" to values
                ))
            }
            "verifyLogin" -> {
                Gson().toJson(mapOf(
                    "action" to action,
                    "data" to mapOf(
                        "email" to values["email"],
                        "password" to values["password"]
                    )
                ))
            }
            "update" -> {
                Gson().toJson(mapOf(
                    "action" to action,
                    "data" to mapOf(
                        "email" to values["email"]
                    ) + values.filterKeys { it != "email" }
                ))
            }
            "updateBarcode" -> {
                Gson().toJson(mapOf(
                    "action" to action,
                    "data" to values
                ))
            }
            else -> {
                Gson().toJson(mapOf(
                    "action" to action
                ))
            }
        }
    }
}
