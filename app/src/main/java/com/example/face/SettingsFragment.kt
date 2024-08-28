package com.example.face

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class SettingsFragment : Fragment() {

    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_PERMISSION_CODE = 100
    private var imageView: ImageView? = null
    private var nameTextView: TextView? = null
    private var emailTextView: TextView? = null
    private var phoneTextView: TextView? = null
    private var profilePhotoUrl: String? = null
    private var authenticatorLink: String? = null
    private val uploadUtils = UploadUtils()
    private lateinit var uploadButton: Button

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        val cameraPermission = permissions[Manifest.permission.CAMERA] ?: false
        val storagePermission = permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE] ?: false

        if (cameraPermission && storagePermission) {
            openPhotoSourceDialog()
        } else {
            Toast.makeText(requireContext(), "Permissions required", Toast.LENGTH_SHORT).show()
        }
    }

    private val captureImageResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageBitmap = result.data?.extras?.get("data") as Bitmap
            uploadPhotoToDrive(imageBitmap)
        }
    }

    private val pickImageResult = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            val bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, uri)
            uploadPhotoToDrive(bitmap)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        // Retrieve user data from arguments
        val name = arguments?.getString("firstName") ?: "User"
        val email = arguments?.getString("email") ?: "No Email"
        val phoneNumber = arguments?.getString("mobileNumber") ?: "No Phone Number"
        profilePhotoUrl = arguments?.getString("profileLink")
        authenticatorLink = arguments?.getString("authenticatorLink")

        // Find UI elements
        imageView = view.findViewById(R.id.profileImageView)
        nameTextView = view.findViewById(R.id.nameTextView)
        emailTextView = view.findViewById(R.id.emailTextView)
        phoneTextView = view.findViewById(R.id.phoneTextView)
        uploadButton = view.findViewById(R.id.uploadButton)

        // Set the user information
        nameTextView?.text = "Name: $name"
        emailTextView?.text = "Email: $email"
        phoneTextView?.text = "Phone: $phoneNumber"

        // Load the profile photo if URL is provided
        if (!profilePhotoUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(profilePhotoUrl)
                .apply(RequestOptions.circleCropTransform())
                .into(imageView!!)
        } else {
            imageView?.setImageResource(R.drawable.default_profile_photo)
        }

        // Set the upload button click listener
        uploadButton.setOnClickListener {
            requestPermissionLauncher.launch(arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE))
        }

        return view
    }

    private fun openPhotoSourceDialog() {
        val options = arrayOf("Take Photo", "Choose from Gallery")
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Choose Photo Source")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> dispatchTakePictureIntent()
                    1 -> pickImageFromGallery()
                }
            }
            .show()
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        captureImageResult.launch(takePictureIntent)
    }

    private fun pickImageFromGallery() {
        pickImageResult.launch("image/*")
    }

    private fun uploadPhotoToDrive(bitmap: Bitmap) {
        val imageUri = getImageUri(bitmap)
        imageUri?.let { uri ->
            val folderId = getFolderIdFromLink(authenticatorLink ?: "")
            if (folderId != null) {
                uploadUtils.uploadPhoto(
                    uri,
                    folderId,
                    requireContext(),
                    onSuccess = {
                        Toast.makeText(requireContext(), "Photo uploaded successfully", Toast.LENGTH_SHORT).show()
                    },
                    onFailure = {
                        Toast.makeText(requireContext(), "Failed to upload photo", Toast.LENGTH_SHORT).show()
                    }
                )
            } else {
                Toast.makeText(requireContext(), "Invalid folder link", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getImageUri(bitmap: Bitmap): Uri? {
        val path = MediaStore.Images.Media.insertImage(
            requireContext().contentResolver,
            bitmap,
            "Title",
            null
        )
        return Uri.parse(path)
    }

    private fun getFolderIdFromLink(link: String): String? {
        val regex = "folders/([^?&]+)".toRegex()
        val matchResult = regex.find(link)
        return matchResult?.groupValues?.get(1)
    }
}
