import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.buildkonfig)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
            freeCompilerArgs.add("-Xexpect-actual-classes")
        }
    }
    
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
    
    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.credentials)
            implementation(libs.androidx.credentials.play)
            implementation(libs.googleid)
            implementation(libs.datastore)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.koin.android)
        }
        commonMain.dependencies {
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.androidx.navigation.compose)
            implementation(compose.materialIconsExtended)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.datetime)
            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor3)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.ktor.client.logging)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.koin.test)
        }
    }
}

android {
    namespace = "com.fruex.beerwall"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.fruex.beerwall"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = project.findProperty("versionName")?.toString() ?: "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

buildkonfig {
    packageName = "com.fruex.beerwall"

    defaultConfigs {
        buildConfigField(com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING, "BUILD_TYPE", "debug")
        buildConfigField(com.codingfeline.buildkonfig.compiler.FieldSpec.Type.BOOLEAN, "DEBUG", "true")
        buildConfigField(com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING, "BASE_URL", "http://api-debug.igibeer.pl:7000")
    }

    defaultConfigs("release") {
        buildConfigField(com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING, "BUILD_TYPE", "release")
        buildConfigField(com.codingfeline.buildkonfig.compiler.FieldSpec.Type.BOOLEAN, "DEBUG", "false")
        buildConfigField(com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING, "BASE_URL", "https://api.igibeer.pl")
    }
}

