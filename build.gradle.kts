// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    val kotlin_version by extra("2.0.0-Beta5")
    dependencies {
        classpath ("com.google.gms:google-services:4.4.1")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version")
    }
    repositories {
        mavenCentral()
    }
}



plugins {
    id("com.android.application") version "8.2.0" apply false
    id ("com.android.library") version "7.4.2" apply false
    id("com.google.gms.google-services") version "4.4.1" apply false
}



