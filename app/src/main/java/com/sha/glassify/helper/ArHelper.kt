package com.sha.glassify.helper

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.widget.Toast
import com.google.ar.core.ArCoreApk
import com.google.ar.core.AugmentedFace
import com.google.ar.core.TrackingState
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.Renderable
import com.google.ar.sceneform.ux.AugmentedFaceNode
import com.sha.glassify.FaceARFragment


class ArHelper(val context: Context) {
    private var currentRenderableId: Int = 0
    private var modelRenderable: ModelRenderable? = null
    private val faceNodeMap = HashMap<AugmentedFace, AugmentedFaceNode>()
    val MIN_OPENGL_VERSION: Double = 3.0

    fun loadModel(id: Int) {
        if (currentRenderableId == id) return

        currentRenderableId = id
        removeCurrent()
        modelRenderable = null

        ModelRenderable.builder()
            .setSource(context, id)
            .build()
            .thenAccept { model ->
                model.apply {
                    isShadowCaster = false
                    isShadowReceiver = false
                }
                modelRenderable = model
            }
    }

    fun setupScene(arFragment: FaceARFragment) {
        val sceneView = arFragment.arSceneView
        sceneView.cameraStreamRenderPriority = Renderable.RENDER_PRIORITY_FIRST

        val scene = sceneView.scene

        scene.addOnUpdateListener {

            sceneView.session
                ?.getAllTrackables(AugmentedFace::class.java)
                ?.forEach { face ->
                    if (modelRenderable == null) return@forEach
                    if (faceNodeMap.containsKey(face)) return@forEach

                    val faceNode = AugmentedFaceNode(face)
                    faceNode.apply {
                        setParent(scene)
                        faceRegionsRenderable = modelRenderable
                    }
                    faceNodeMap[face] = faceNode
                }

            val iterator = faceNodeMap.entries.iterator()

            while (iterator.hasNext()) {
                val entry = iterator.next()
                if (entry.key.trackingState == TrackingState.STOPPED) {
                    entry.value.setParent(null)
                    iterator.remove()
                }
            }
        }
//        setupScale(sceneView, arFragment)
    }

    private fun removeCurrent() {
        val iterator = faceNodeMap.entries.iterator()

        while (iterator.hasNext()) {
            val entry = iterator.next()
            entry.value.setParent(null)
            iterator.remove()
        }
    }

    fun isDeviceSupported(activity: Activity): Boolean {
        if (ArCoreApk.getInstance().checkAvailability(activity) == ArCoreApk.Availability.UNSUPPORTED_DEVICE_NOT_CAPABLE) {
            Toast.makeText(activity, "Augmented Faces requires ArCore", Toast.LENGTH_LONG).show()
            activity.finish()
            return false
        }

        val config = activity.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

        if (config.deviceConfigurationInfo.glEsVersion.toDouble() < MIN_OPENGL_VERSION) {
            Toast.makeText(activity, "Sceneform requires OpenGL ES 3.0 or later", Toast.LENGTH_LONG).show()
            activity.finish()
            return false
        }

        return true
    }

    fun scale(scale: Int) {
        val actualScale: Float = (scale + 100) /100f
        faceNodeMap.forEach{
            it.value.localScale = Vector3(actualScale, actualScale, actualScale)
        }
    }

}