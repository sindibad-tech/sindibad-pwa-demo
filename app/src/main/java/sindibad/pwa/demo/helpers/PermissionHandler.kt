package sindibad.pwa.demo.helpers

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

class PermissionHandler(private val activity: Activity) {
    interface PermissionCallback {
        fun onPermissionGranted(permission: String)
        fun onPermissionDenied(permission: String)
    }

    fun requestPermissions(
        permissions: List<String>,
        callback: PermissionCallback
    ) {
        val permissionsToRequest = mutableListOf<String>()

        permissions.forEach { permission ->
            val permissionStatus = ActivityCompat.checkSelfPermission(activity, permission)
            if (permissionStatus != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission)
            }
        }

        if (permissionsToRequest.isEmpty()) {
            permissions.forEach { permission ->
                callback.onPermissionGranted(permission)
            }
        } else {
            ActivityCompat.requestPermissions(
                activity,
                permissionsToRequest.toTypedArray(),
                PERMISSION_REQUEST_CODE
            )
        }
    }

    // It will be used when we need to do something based on the result of the permission
    fun handlePermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
        callback: PermissionCallback
    ) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            permissions.forEachIndexed { index, permission ->
                if (grantResults[index] == PackageManager.PERMISSION_GRANTED) {
                    callback.onPermissionGranted(permission)
                } else {
                    callback.onPermissionDenied(permission)
                }
            }
        }
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 123
    }
}
