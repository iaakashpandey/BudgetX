plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.devtools.ksp") version "2.1.0-1.0.29"
    kotlin("plugin.serialization") version "2.0.21"
    id("kotlin-parcelize")
    alias(libs.plugins.google.gms.google.services)

}

android {
    namespace = "com.example.budgetx"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.budgetx"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures {
        viewBinding = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            versionNameSuffix = "1"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //Room
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    annotationProcessor("androidx.room:room-compiler:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")

    //Navigation Fragment
    implementation("androidx.navigation:navigation-fragment:2.8.5")
    implementation("androidx.navigation:navigation-ui:2.8.5")

    //ViewModel and LiveData
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.7")

    //RecyclerView
    implementation("androidx.recyclerview:recyclerview:1.4.0")
    // For control over item selection of both touch and mouse driven selection
    implementation("androidx.recyclerview:recyclerview-selection:1.1.0")

    //Glide for image Loading
    implementation ("com.github.bumptech.glide:glide:4.16.0")

    //ViewPager
    implementation("androidx.viewpager2:viewpager2:1.1.0")

    //DotIndicator
    implementation("com.tbuonomo:dotsindicator:5.1.0")

    //CircularImageView <com.makeramen.roundedimageview.RoundedImageView
    implementation ("com.makeramen:roundedimageview:2.3.0")

    //CircularImageView <de.hdodenhof.circleimageview.CircleImageView
    implementation ("de.hdodenhof:circleimageview:3.1.0")

    //Scalable size unit(support for different screen size)
    implementation ("com.intuit.sdp:sdp-android:1.1.1")
    implementation ("com.intuit.ssp:ssp-android:1.1.1")

    //Splash screen
    implementation("androidx.core:core-splashscreen:1.0.1")

    //Gson
    implementation ("com.google.code.gson:gson:2.11.0")

    //Chart
    implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0"){
       exclude("de.hdodenhof.circleimageview")
    }

    //WorkManager library for scheduling tasks Notification
    implementation ("androidx.work:work-runtime-ktx:2.10.0")

    implementation ("it.xabaras.android:recyclerview-swipedecorator:1.4")

    implementation ("com.google.android.gms:play-services-auth:21.3.0")
    implementation ("com.google.firebase:firebase-auth:23.1.0")


    implementation ("androidx.credentials:credentials:1.3.0")
}