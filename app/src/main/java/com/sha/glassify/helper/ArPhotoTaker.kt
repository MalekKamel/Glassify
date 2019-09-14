package com.sha.glassify.helper

import android.content.Intent
import android.graphics.Bitmap
import android.os.Handler
import android.os.HandlerThread
import android.view.PixelCopy
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.google.android.material.snackbar.Snackbar
import com.google.ar.sceneform.ux.ArFragment
import java.io.File
import java.io.IOException

object ArPhotoTaker {

    fun take(arFragment: ArFragment, activity: AppCompatActivity) {
        val filename = StorageHelper.generateFilename()
        val view = arFragment.arSceneView

        // Create a bitmap the size of the scene view.
        val bitmap = Bitmap.createBitmap(
            view.width, view.height,
            Bitmap.Config.ARGB_8888
        )

        // Create a handler thread to offload the processing of the image.
        val handlerThread = HandlerThread("PixelCopier")
        handlerThread.start()
        // Make the request to copy.
        PixelCopy.request(view, bitmap, { copyResult ->

            if (copyResult != PixelCopy.SUCCESS) {
                val toast = Toast.makeText(
                    activity,
                    "Failed to copy Pixels: $copyResult", Toast.LENGTH_LONG
                )
                toast.show()
                return@request
            }

            try {
                StorageHelper.saveBitmapToDisk(bitmap, filename)
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(
                    activity,
                    e.toString(),
                    Toast.LENGTH_LONG
                ).show()
                return@request
            }

            val snackBar = Snackbar.make(
                activity.findViewById(android.R.id.content),
                "Photo saved", Snackbar.LENGTH_LONG
            )

            snackBar.setAction("Open in Photos") {
                val photoFile = File(filename)

                val photoURI = FileProvider.getUriForFile(
                    activity,
                    activity.packageName + ".ar.glassify.name.provider",
                    photoFile
                )
                val intent = Intent(Intent.ACTION_VIEW, photoURI)
                intent.setDataAndType(photoURI, "image/*")
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                activity.startActivity(intent)

            }
            snackBar.show()

            handlerThread.quitSafely()
        }, Handler(handlerThread.looper))
    }

}