package com.example.face

import android.content.Context
import android.net.Uri
import com.google.api.client.http.FileContent
import com.google.api.client.http.HttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import com.google.api.services.drive.model.Permission
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential.Builder
import java.io.File as JavaFile

class UploadUtils {

    private val HTTP_TRANSPORT: HttpTransport = NetHttpTransport()
    private val JSON_FACTORY: JsonFactory = GsonFactory.getDefaultInstance()
    private val SCOPES = listOf("https://www.googleapis.com/auth/drive.file")

    private var googleSignInClient: GoogleSignInClient? = null

    fun initializeGoogleSignInClient(context: Context) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestScopes(*SCOPES.toTypedArray())
            .build()

        googleSignInClient = GoogleSignIn.getClient(context, gso)
    }

    fun getCredential(account: GoogleSignInAccount): GoogleCredential {
        return GoogleCredential.Builder()
            .setTransport(HTTP_TRANSPORT)
            .setJsonFactory(JSON_FACTORY)
            .setServiceAccountPrivateKey(account.account?.privateKey)
            .setServiceAccountScopes(SCOPES)
            .build()
    }

    fun uploadPhoto(
        uri: Uri,
        folderId: String,
        context: Context,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        val account = GoogleSignIn.getLastSignedInAccount(context)
        if (account != null) {
            val credential = getCredential(account)
            val driveService = Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName("Your Application Name")
                .build()

            val fileMetadata = File().apply {
                parents = listOf(folderId)
                name = uri.lastPathSegment ?: "Unknown"
            }

            val file = getFileFromUri(context, uri)
            val fileContent = FileContent("image/jpeg", file)

            try {
                val request = driveService.files().create(fileMetadata, fileContent)
                    .setFields("id")
                val uploadedFile = request.execute()

                // Add permissions if needed
                val permission = Permission().apply {
                    type = "anyone"
                    role = "reader"
                }
                driveService.permissions().create(uploadedFile.id, permission).execute()

                onSuccess()
            } catch (e: Exception) {
                e.printStackTrace()
                onFailure()
            }
        } else {
            onFailure()
        }
    }

    private fun getFileFromUri(context: Context, uri: Uri): JavaFile {
        val file = JavaFile(context.cacheDir, "temp_image.jpg")
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            file.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        return file
    }
}
