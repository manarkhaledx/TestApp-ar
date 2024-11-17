package com.example.testapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.Nullable
import com.google.ar.core.Config
import com.google.ar.core.Session
import com.google.ar.sceneform.ux.ArFragment
import java.util.EnumSet


class CustomArFragment : ArFragment() {
    override fun getSessionConfiguration(session: Session?): Config {
        val config: Config = Config(session)

        // Configure 3D Face Mesh
        config.setAugmentedFaceMode(Config.AugmentedFaceMode.MESH3D)
        arSceneView.setupSession(session)
        return config
    }

    override fun getSessionFeatures(): MutableSet<Session.Feature>? {
        // Configure Front Camera
        return EnumSet.of<Session.Feature>(Session.Feature.FRONT_CAMERA)
    }

    // Override to turn off planeDiscoveryController.
    // Plane traceable are not supported with the front camera.
    override fun onCreateView(
        inflater: LayoutInflater,
        @Nullable container: ViewGroup?,
        @Nullable savedInstanceState: Bundle?
    ): View? {
        val frameLayout =
            super.onCreateView(inflater, container, savedInstanceState) as FrameLayout?
        planeDiscoveryController.hide()
        planeDiscoveryController.setInstructionView(null)
        return frameLayout
    }
}