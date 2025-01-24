// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.1.0" apply false
    id("com.google.devtools.ksp") version "1.8.10-1.0.9" apply false // For room
    id("com.google.dagger.hilt.android") version "2.44" apply false // For hilt
    id("org.jetbrains.kotlin.android") version "1.8.10" apply false
}