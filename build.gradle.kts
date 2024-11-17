// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
}
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        // Add the Sceneform plugin classpath
        classpath("com.google.ar.sceneform:plugin:1.17.1")
    }
}