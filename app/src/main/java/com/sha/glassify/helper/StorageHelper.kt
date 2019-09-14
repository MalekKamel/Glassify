package com.sha.glassify.helper

import android.graphics.Bitmap
import android.os.Environment
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

object StorageHelper {

    fun generateFilename(): String {
        val date = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Date())
        return Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES
        ).toString() + File.separator + "Sceneform/" + date + "_screenshot.jpg"
    }

    @Throws(IOException::class)
    fun saveBitmapToDisk(bitmap: Bitmap, filename: String) {

        val out = File(filename)
        if (!out.parentFile.exists()) {
            out.parentFile.mkdirs()
        }
        try {
            FileOutputStream(filename).use { outputStream ->
                ByteArrayOutputStream().use { outputData ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputData)
                    outputData.writeTo(outputStream)
                    outputStream.flush()
                    outputStream.close()
                }
            }
        } catch (ex: IOException) {
            ex.printStackTrace()
            throw IOException("Failed to save bitmap to disk", ex)
        }

    }

}