package com.example.face

import android.app.AlertDialog
import android.app.DownloadManager
import android.content.DialogInterface
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Retrieve arguments passed from Afterlogin activity
        val name = arguments?.getString("firstName") ?: "Unknown"
        val unknownLink = arguments?.getString("unknownLink") ?: ""

        // Display the welcome message
        val welcomeTextView: TextView = view.findViewById(R.id.welcomeTextView)
        welcomeTextView.text = "Welcome $name"

        // Initializing the WebView
        val wv: WebView = view.findViewById(R.id.webview)

        // Check if the unknownLink is provided
        if (unknownLink.isNotEmpty()) {
            // Load the URL in WebView
            wv.loadUrl(unknownLink)
        } else {
            // Show error message
            wv.visibility = View.GONE
            val errorTextView: TextView = view.findViewById(R.id.errorTextView)
            errorTextView.visibility = View.VISIBLE
            errorTextView.text = "Error: No valid link provided."
        }

        wv.webViewClient = Client()
        val ws: WebSettings = wv.settings

        // Enabling JavaScript
        ws.javaScriptEnabled = true
        wv.settings.javaScriptCanOpenWindowsAutomatically = true
        wv.clearCache(true)
        wv.clearHistory()

        // Download Manager to handle downloads
        wv.setDownloadListener { url, _, _, _, _ ->
            val req = DownloadManager.Request(Uri.parse(url))
            req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            val dm = activity?.getSystemService(AppCompatActivity.DOWNLOAD_SERVICE) as DownloadManager
            dm.enqueue(req)
            Toast.makeText(activity, "Downloading....", Toast.LENGTH_SHORT).show()
        }

        return view
    }

    private inner class Client : WebViewClient() {
        // On page started, start loading the URL
        override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
        }

        // Load the URL of our drive
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            view.loadUrl(url)
            return true
        }

        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
        }

        override fun onReceivedError(
            webView: WebView,
            errorCode: Int,
            description: String,
            failingUrl: String
        ) {
            // Stop loading
            try {
                webView.stopLoading()
            } catch (e: Exception) {
                // Handle exception if needed
            }

            if (webView.canGoBack()) {
                webView.goBack()
            }

            // Show error message
            webView.loadUrl("about:blank")
            val alertDialog = AlertDialog.Builder(activity).create()
            alertDialog.setTitle("Error")
            alertDialog.setMessage("Check your internet connection and try again.")
            alertDialog.setButton(
                DialogInterface.BUTTON_POSITIVE,
                "Try Again"
            ) { _, _ ->
                activity?.let { act ->
                    act.finish()
                    startActivity(act.intent)
                }
            }

            alertDialog.show()
            super.onReceivedError(webView, errorCode, description, failingUrl)
        }
    }
}
