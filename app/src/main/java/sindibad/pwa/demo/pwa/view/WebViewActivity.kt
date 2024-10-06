package sindibad.pwa.demo.pwa.view

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.webkit.CookieManager
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebView.setWebContentsDebuggingEnabled
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import sindibad.pwa.demo.R
import sindibad.pwa.demo.helpers.PermissionHandler
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WebViewActivity : AppCompatActivity(),
    PermissionHandler.PermissionCallback {
    private var webView: WebView? = null
    private var localFilePathCallback: ValueCallback<Array<Uri>>? = null
    private var localFileChooserParams: WebChromeClient.FileChooserParams? = null

    private var chooserLauncher: ActivityResultLauncher<Intent>? = null


    private var photoUri: Uri? = null
    private val cookieManager = CookieManager.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupWebView()

        chooserLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    try {
                        if (result.data?.data != null) {
                            handleImageUri(result.data?.data!!)
                        } else if (photoUri != null) {
                            handleImageUri(photoUri!!)
                        }
                    } catch (e: Exception) {
                        Log.w("WebViewActivity", e)
                    }
                } else {
                    localFilePathCallback?.onReceiveValue(null)
                }
            }

        webView?.loadUrl("sindibad.iq")
    }

    private fun handleImageUri(uri: Uri) {
        val imageBitmap = getBitmapFromUri(uri)
        if (imageBitmap != null) {
            localFilePathCallback?.onReceiveValue(
                arrayOf(uri)
            )
        } else {
            localFilePathCallback?.onReceiveValue(null)
        }
    }

    private fun getBitmapFromUri(uri: Uri): Bitmap? {
        return try {
            contentResolver.openInputStream(uri)?.use { inputStream ->
                BitmapFactory.decodeStream(inputStream)
            }
        } catch (e: Exception) {
            Log.e("WebViewActivity", e.message.orEmpty())
            null
        }
    }

    private fun setupWebView() {

        webView?.apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.allowFileAccess = true
            settings.databaseEnabled = true

            val cookieManager = CookieManager.getInstance()
            cookieManager.setAcceptCookie(true)
            cookieManager.setAcceptThirdPartyCookies(this, true)

            setWebContentsDebuggingEnabled(true)
        }

        webView?.webChromeClient = object : WebChromeClient() {
            override fun onShowFileChooser(
                webView: WebView?,
                filePathCallback: ValueCallback<Array<Uri>>?,
                fileChooserParams: FileChooserParams?
            ): Boolean {
                localFilePathCallback = filePathCallback
                localFileChooserParams = fileChooserParams

                try {
                    openImagePicker()
                } catch (e: Exception) {
                    localFilePathCallback = null
                    return false
                }
                return true
            }
        }
    }

    private fun openImagePicker() {
        PermissionHandler(this).requestPermissions(listOf(Manifest.permission.CAMERA), this)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PermissionHandler(this).handlePermissionsResult(
            requestCode,
            permissions,
            grantResults,
            this
        )
    }

    override fun onPermissionGranted(permission: String) {
        try {
            val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(
                Date()
            )

            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, "SINDIBAD_$timeStamp.png")
                put(MediaStore.Images.Media.MIME_TYPE, MIME_TYPE)
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            }

            photoUri = contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )

            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            }

            val galleryIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
                    type = INTENT_DATA_TYPE
                }

            val chooserIntent =
                Intent.createChooser(
                    galleryIntent,
                    "Select passport image"
                )
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(cameraIntent))

            chooserLauncher?.launch(chooserIntent)
        } catch (e: Exception) {
            Log.e("WebViewActivity", e.message.orEmpty())
        }
    }

    override fun onPermissionDenied(permission: String) {
        try {
            localFileChooserParams?.createIntent()?.let { chooserLauncher?.launch(it) }
        } catch (e: Exception) {
            Log.e("WebViewActivity", e.message.orEmpty())
        }
    }

    override fun onDestroy() {
        webView?.apply {
            loadUrl("about:blank")
            onPause()
            removeAllViews()
            destroy()
        }
        localFileChooserParams = null
        localFilePathCallback = null
        cookieManager.removeSessionCookies { }
        super.onDestroy()
    }

    companion object {
        private const val MIME_TYPE = "image/png"
        private const val INTENT_DATA_TYPE = "image/*"
    }
}
