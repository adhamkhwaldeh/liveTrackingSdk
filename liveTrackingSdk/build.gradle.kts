import org.jetbrains.dokka.gradle.DokkaTaskPartial
import java.net.URI
import org.gradle.plugins.signing.Sign

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)

    id("org.jetbrains.dokka") version "2.0.0"
//    id("org.jreleaser.gradle.plugin") version "0.1.0"
    id("org.jreleaser") version "1.19.0"
    id("maven-publish")
    id("signing")
}
// THIS IS THE MOST IMPORTANT PART FOR THIS ERROR
// Or your intended release version, e.g., "0.1.2"
group = "io.github.adhamkhwaldeh"
version = "1.0.7"

android {
    namespace = "com.kerberos.livetrackingsdk"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
        version = "1.0.7"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
//        compose = true
        aidl = true // Ensure this is true or remove the line (as true is default)
        buildConfig = true // Or remove this line
        dataBinding = true

        // for view binding:
        viewBinding = true
    }

}

jreleaser {
    release {
        github {
            enabled.set(false) // 🔴 disable GitHub release
        }
    }

    project {
        // Only set version here if you explicitly want to override Gradle's project.version for JReleaser
        // If not, remove the line below or comment it out.
        // version = "1.0.7"

        name = "liveTrackingSdk" // Usually the artifactId
        description = "A Live Tracking SDK for Android."
        longDescription =
            "A longer description of the Live Tracking SDK for Android, detailing its features and benefits."
        website = "https://github.com/adhamkhwaldeh/liveTrackingSdk" // Your project's website/repo
        authors = listOf("Adham Khwaldeh <adhamkhwaldeh@gmail.com>")
        license = "Apache-2.0" // SPDX identifier
        version = "1.0.7"
//        groupId = "io.github.adhamkhwaldeh"
//        artifactId = "livetrackingsdk"
//        version = "1.0.7"
        // java.groupId = "io.github.adhamkhwaldeh" // JReleaser usually infers this from project.group
        // java.artifactId = "livetrackingsdk" // JReleaser usually infers this
    }
//    deploy {
//        maven {
//            nexus2 {
//                register("sonatype") {
//                    active.set(org.jreleaser.model.Active.ALWAYS)
//                    url.set("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
//                    snapshotUrl.set("https://s01.oss.sonatype.org/content/repositories/snapshots/")
//                    username.set(providers.environmentVariable("JRELEASER_SONATYPE_USERNAME"))
//                    password.set(providers.environmentVariable("JRELEASER_SONATYPE_PASSWORD"))
//                }
//            }
//        }
//    }
    // ... other JReleaser configurations (release, signing, packagers, etc.)
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.play.services.location)
    implementation(libs.play.services.maps)
    implementation(libs.timber)

    implementation(libs.androidx.datastore.preferences) // Or the latest version
    implementation(libs.androidx.lifecycle.runtime.ktx) // Often used with DataStore for coroutine scopes

}

tasks.withType<DokkaTaskPartial>().configureEach { // Use DokkaTaskPartial for module-level config
    moduleName.set("LiveTrackingSdk") // Sets the module name

    // Configuration for specific output formats (like HTML)
    // This part of your original config seems to be for an older Dokka version's direct `dokkaPublications`
    // In modern Dokka (1.4+), you usually configure the output directory on the main task
    // or on specific format tasks like dokkaHtml.
    // If 'dokkaPublications.html' is from a very specific Dokka version or a custom setup,
    // this might need adjustment based on that version's DSL.

    // For standard HTML output configuration (Dokka 1.4+):
    // This would typically be on the DokkaTask itself, not DokkaTaskPartial,
    // or handled by the dokkaHtml task specifically.
    // If you are configuring the main HTML output for the module:
    // outputDirectory.set(layout.buildDirectory.dir("dokkaDir/html")) // Example for HTML output

    dokkaSourceSets.named("main") { // Configure the 'main' source set
        // Set the actual Kotlin/Java dirs for sourceRoots only once to avoid duplication
        // and potential issues.
        sourceRoots.setFrom(files("src/main/java")) // Use setFrom to replace existing, or from() to add
        // If you also have Kotlin sources:
        // sourceRoots.from(files("src/main/kotlin"))


        includes.from("README.md")
        skipEmptyPackages.set(true)
        // includeNonPublic.set(false) // Uncomment if needed, default is false (only public/protected)

        sourceLink {
            // localDirectory.set(file("src/main/java")) // Set this if your sources are not at the project root relative to remoteUrl
            remoteUrl.set(URI("https://github.com/adhamkhwaldeh/WeatherSdk/tree/main/app/src/main/java").toURL())
            remoteLineSuffix.set("#L")
        }

        reportUndocumented.set(true)          // Warn about undocumented public APIs
        skipDeprecated.set(true)              // Exclude deprecated elements (default is true)
        // suppress.set(false) // 'suppress' is not a standard Dokka property.
        // Maybe you meant 'suppressInheritedMembers.set(false)' or similar?
        // Or it's a custom property from a plugin.
        // If you mean to include elements annotated with @suppress, that's usually default.

        // jdkVersion.set(17) // Set the JDK version for parsing Java sources
        // For Dokka 1.6.0+, this is auto-detected from compileJava.sourceCompatibility
        // or can be set if needed for specific cases.
    }
}

// If you need to specifically configure the HTML output directory for the 'dokkaHtml' task:
tasks.named<org.jetbrains.dokka.gradle.DokkaTask>("dokkaHtml") {
    outputDirectory.set(layout.buildDirectory.dir("dokkaDir")) // This matches your original outputDirectory
    moduleName.set("LiveTrackingSdk") // Sets the module name

    // Configuration for specific output formats (like HTML)
    // This part of your original config seems to be for an older Dokka version's direct `dokkaPublications`
    // In modern Dokka (1.4+), you usually configure the output directory on the main task
    // or on specific format tasks like dokkaHtml.
    // If 'dokkaPublications.html' is from a very specific Dokka version or a custom setup,
    // this might need adjustment based on that version's DSL.

    // For standard HTML output configuration (Dokka 1.4+):
    // This would typically be on the DokkaTask itself, not DokkaTaskPartial,
    // or handled by the dokkaHtml task specifically.
    // If you are configuring the main HTML output for the module:
    // outputDirectory.set(layout.buildDirectory.dir("dokkaDir/html")) // Example for HTML output

    dokkaSourceSets.named("main") { // Configure the 'main' source set
        // Set the actual Kotlin/Java dirs for sourceRoots only once to avoid duplication
        // and potential issues.
        sourceRoots.setFrom(files("src/main/java")) // Use setFrom to replace existing, or from() to add
        // If you also have Kotlin sources:
        // sourceRoots.from(files("src/main/kotlin"))


        includes.from("README.md")
        skipEmptyPackages.set(true)
        // includeNonPublic.set(false) // Uncomment if needed, default is false (only public/protected)

        sourceLink {
            // localDirectory.set(file("src/main/java")) // Set this if your sources are not at the project root relative to remoteUrl
            remoteUrl.set(URI("https://github.com/adhamkhwaldeh/WeatherSdk/tree/main/app/src/main/java").toURL())
            remoteLineSuffix.set("#L")
        }

        reportUndocumented.set(true)          // Warn about undocumented public APIs
        skipDeprecated.set(true)              // Exclude deprecated elements (default is true)
        // suppress.set(false) // 'suppress' is not a standard Dokka property.
        // Maybe you meant 'suppressInheritedMembers.set(false)' or similar?
        // Or it's a custom property from a plugin.
        // If you mean to include elements annotated with @suppress, that's usually default.

        // jdkVersion.set(17) // Set the JDK version for parsing Java sources
        // For Dokka 1.6.0+, this is auto-detected from compileJava.sourceCompatibility
        // or can be set if needed for specific cases.
    }
}
//tasks.named<org.jetbrains.dokka.gradle.DokkaTask>("dokkaPublicationsHtml") {
//    outputDirectory.set(layout.buildDirectory.dir("dokkaDir")) // This matches your original outputDirectory
//}


//afterEvaluate {
//    publishing {
//        publications {
//            create<MavenPublication>("release") {
//                from(components["release"])
//
//                groupId = "io.github.adhamkhwaldeh"
//                artifactId = "livetrackingsdk"
//                version = "1.0.7"
//
//                pom {
//                    name.set("Livetrackingsdk")
//                    description.set("A description of your library")
//                    url.set("https://github.com/adhamkhwaldeh/liveTrackingSdk")
//
//                    licenses {
//                        license {
//                            name.set("The Apache License, Version 2.0")
//                            url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
//                        }
//                    }
//                    developers {
//                        developer {
//                            id.set("adhamkhwaldeh")
//                            name.set("adham al khawalda")
//                            email.set("adhamkhwaldeh@gmail.com")
//                        }
//                    }
//                    scm {
//                        connection.set("scm:git:git://github.com/adhamkhwaldeh/liveTrackingSdk.git")
//                        developerConnection.set("scm:git:ssh://github.com/adhamkhwaldeh/liveTrackingSdk.git")
//                        url.set("https://github.com/adhamkhwaldeh/liveTrackingSdk")
//                    }
//                }
//            }
//        }
//
//        repositories {
//            maven {
//                name = "OSSRH"
////                // Use release or snapshot repo depending on version
////                val releasesRepoUrl =
////                    uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
////                val snapshotsRepoUrl =
////                    uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
////                url = if (version.toString()
////                        .endsWith("SNAPSHOT")
////                ) snapshotsRepoUrl else releasesRepoUrl
//
//                val releasesRepoUrl = uri("https://central.sonatype.com/api/v1/publish")
//                val snapshotsRepoUrl = uri("https://central.sonatype.com/api/v1/publish-snapshots")
//                url = if (version.toString()
//                        .endsWith("SNAPSHOT")
//                ) snapshotsRepoUrl else releasesRepoUrl
//
//                credentials {
//                    username = project.findProperty("sonatypeUsername") as String? ?: ""
//                    password = project.findProperty("sonatypePassword") as String? ?: ""
//                }
//            }
//        }
//
//
//    }
//}


afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                groupId = "io.github.adhamkhwaldeh"
                artifactId = "livetrackingsdk"
                version = "1.0.7"

                // Publish the release AAR
                artifact("$buildDir/outputs/aar/${project.name}-release.aar")

                pom {
                    name.set("LiveTrackingSDK")
                    description.set("Android SDK for live tracking")
                    url.set("https://github.com/adhamkhwaldeh/livetrackingsdk")

                    licenses {
                        license {
                            name.set("Apache-2.0")
                            url.set("http://www.apache.org/licenses/LICENSE-2.0")
                        }
                    }

                    developers {
                        developer {
                            id.set("adhamkhwaldeh")
                            name.set("Adham Khwaldeh")
                            email.set("adhamkhwaldeh@gmail.com")
                        }
                    }

                    scm {
                        connection.set("scm:git:git://github.com/adhamkhwaldeh/livetrackingsdk.git")
                        developerConnection.set("scm:git:ssh://github.com/adhamkhwaldeh/livetrackingsdk.git")
                        url.set("https://github.com/adhamkhwaldeh/livetrackingsdk")
                    }
                }
            }
        }

        repositories {
            maven {
                name = "OSSRH"
//                url = uri("https://central.sonatype.com/api/v1/publish")
                url = if (version.toString().endsWith("SNAPSHOT"))
                    uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
                else
                    uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")

//                url = if (version.toString().endsWith("SNAPSHOT"))
//                    uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
//                else
//                    uri("https://central.sonatype.com/api/v1/publish")

                credentials {
                    username = project.findProperty("sonatypeUsername") as String?
                    password = project.findProperty("sonatypePassword") as String?
                }
            }
        }
    }

    signing {
        useGpgCmd() // Use the installed gpg command
//        options {
//            commandLineArguments = listOf("--batch", "--pinentry-mode", "loopback")
//        }
        sign(publishing.publications["release"])
    }

    tasks.withType<Sign>().configureEach {
        enabled = false
    }
    tasks.named("signReleasePublication") {
        dependsOn("bundleReleaseAar")
    }
}