import org.gradle.api.JavaVersion

object Build {
    const val COMPILE_SDK = 35
    const val MIN_SDK = 21
    val jvmTarget = JavaVersion.VERSION_1_8
}
