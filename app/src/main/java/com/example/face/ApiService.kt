package com.example.face
import retrofit2.http.GET
import retrofit2.http.Path
// Ensure this class is public or has the same visibility as the function returning it
public data class Image(
    val url: String,
    val description: String
)

public interface ApiService {

    // This function is public, so the return type 'Image' must also be public
    @GET("images/{id}")
    suspend fun getImage(@Path("id") id: String): Image
}

