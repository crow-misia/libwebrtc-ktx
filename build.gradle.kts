// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    kotlin("android") apply false
}

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:_")
        classpath("org.jetbrains.dokka:dokka-gradle-plugin:_")

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle.kts files
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
}

val clean by tasks.creating(Delete::class) {
    group = "build"
    delete(rootProject.buildDir)
}
