// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.compose) apply false
    id("org.jreleaser") version "1.19.0"
    base // Add this line

    id("signing")
    id("maven-publish")
}
// Root build.gradle.kts
group = "io.github.adhamkhwaldeh"
version = "1.0.0"


//// In your root build.gradle.kts
//task("cleanAll") {
//    description = "Cleans all subprojects."
//    doLast {
//        subprojects.forEach { subproject ->
//            subproject.tasks.findByName("clean")?.let { cleanTask ->
//                println("Cleaning ${subproject.name}...")
//                // You can't directly execute another task like this in doLast.
//                // Instead, make this task depend on the subproject clean tasks.
//            }
//        }
//    }
//}

// Better way: Make cleanAll depend on subproject clean tasks
tasks.register("cleanAll") {
    description = "Cleans all subprojects that have a clean task."
    subprojects.forEach { subproject ->
        subproject.tasks.matching { it.name == "clean" }.forEach { cleanTask ->
            dependsOn(cleanTask)
        }
    }

}


// Then run:
// ./gradlew cleanAll