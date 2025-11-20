// build.gradle.kts (Module: app)

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)  // Use alias aqui também
    alias(libs.plugins.hilt) // Use alias aqui também
}

android {
    namespace = "com.akda.od2"
    compileSdk = 35 // Recomendo baixar para 35. O 36 ainda é preview e pode bugar o Hilt/Room.

    defaultConfig {
        applicationId = "com.akda.od2"
        minSdk = 28
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    // ... (resto das configurações de build types e compileOptions mantêm igual)
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    // --- Android Core ---
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.splashscreen) // A que adicionamos agora pouco

    // --- COMPOSE (A parte que está falhando) ---
    // O BOM controla as versões das libs do Compose abaixo
    implementation(platform(libs.androidx.compose.bom))

    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    // Ferramentas de Debug do Compose
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // --- NAVEGAÇÃO ---
    // Se você estiver usando Navigation Compose, adicione isso ao TOML e aqui.
    // Se não estiver no TOML, comente a linha abaixo por enquanto:
    // implementation("androidx.navigation:navigation-compose:2.7.7")

    // --- ROOM (Banco de dados) ---
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // --- HILT (Injeção de Dependência) ---
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // --- OUTROS ---
    implementation(libs.transport.runtime)
    implementation("com.google.code.gson:gson:2.10.1")

    // --- TESTES ---
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    implementation(libs.androidx.navigation.compose)
}