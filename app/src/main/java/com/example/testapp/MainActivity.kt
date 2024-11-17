package com.example.testapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.ar.core.AugmentedFace
import com.google.ar.core.TrackingState
import com.google.ar.sceneform.FrameTime
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.Renderable
import com.google.ar.sceneform.rendering.Texture
import com.google.ar.sceneform.ux.AugmentedFaceNode
import com.google.ar.sceneform.ux.ArFragment
import java.util.*

class MainActivity : AppCompatActivity() {
    private var modelRenderable: ModelRenderable? = null
    private var texture: Texture? = null
    private val faceNodeMap = HashMap<AugmentedFace, AugmentedFaceNode>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val customArFragment = supportFragmentManager.findFragmentById(R.id.arFragment) as? ArFragment
            ?: run {
                Toast.makeText(this, "Error: Custom AR Fragment not found", Toast.LENGTH_SHORT).show()
                return
            }

        // Load the face regions renderable
        ModelRenderable.builder()
            .setSource(this, R.raw.fox_face) // Ensure fox_face.sfb is correctly placed in res/raw
            .build()
            .thenAccept { renderable ->
                modelRenderable = renderable.apply {
                    isShadowCaster = false
                    isShadowReceiver = false
                }
            }
            .exceptionally { throwable ->
                Toast.makeText(this, "Error loading model: ${throwable.message}", Toast.LENGTH_SHORT).show()
                null
            }

        // Load the face mesh texture
        Texture.builder()
            .setSource(this, R.drawable.fox_face_mesh_texture) // Ensure this file is in res/drawable
            .build()
            .thenAccept { loadedTexture -> texture = loadedTexture }
            .exceptionally { throwable ->
                Toast.makeText(this, "Error loading texture: ${throwable.message}", Toast.LENGTH_SHORT).show()
                null
            }

        // Set render priority
        customArFragment.arSceneView.cameraStreamRenderPriority = Renderable.RENDER_PRIORITY_FIRST

        // Add update listener to the scene
        customArFragment.arSceneView.scene.addOnUpdateListener { frameTime: FrameTime? ->
            if (modelRenderable == null || texture == null) return@addOnUpdateListener

            val frame = customArFragment.arSceneView.arFrame ?: return@addOnUpdateListener
            val augmentedFaces = frame.getUpdatedTrackables(AugmentedFace::class.java)

            // Process each detected face
            for (augmentedFace in augmentedFaces) {
                if (!faceNodeMap.containsKey(augmentedFace)) {
                    // Create a new AugmentedFaceNode
                    val faceNode = AugmentedFaceNode(augmentedFace).apply {
                        setParent(customArFragment.arSceneView.scene)
                        faceRegionsRenderable = modelRenderable
                        faceMeshTexture = texture
                    }
                    faceNodeMap[augmentedFace] = faceNode
                }
            }

            // Remove nodes for faces that are no longer tracked
            val iterator = faceNodeMap.entries.iterator()
            while (iterator.hasNext()) {
                val (face, node) = iterator.next()
                if (face.trackingState == TrackingState.STOPPED) {
                    node.setParent(null)
                    iterator.remove()
                }
            }
        }
    }
}
