plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    `maven-publish`
    signing
    alias(libs.plugins.jvm)
    alias(libs.plugins.plugin.gradle.publish)
}

group = project.findProperty("GROUP") as String
version = project.findProperty("VERSION_NAME") as String

testing {
    suites {
        // Configure the built-in test suite
        val test by getting(JvmTestSuite::class) {
            // Use Kotlin Test test framework
            useKotlinTest("1.9.23")
        }

        // Create a new test suite
        val functionalTest by registering(JvmTestSuite::class) {
            // Use Kotlin Test test framework
            useKotlinTest("1.9.23")

            dependencies {
                // functionalTest test suite depends on the production code in tests
                implementation(project())
            }

            targets {
                all {
                    // This test suite should run after the built-in test suite has run its tests
                    testTask.configure { shouldRunAfter(test) } 
                }
            }
        }
    }
}

gradlePlugin {
    website = "https://github.com/moengage/gradle-config-plugin"
    vcsUrl = "https://github.com/moengage/gradle-config-plugin"
    val androidPluginConfiguration by plugins.creating {
        id = "com.moengage.android.library.config.plugin"
        implementationClass = "com.moengage.gradle.android.library.plugin.AndroidLibraryConfigPlugin"
        displayName = project.findProperty("NAME") as String
        description = project.findProperty("DESCRIPTION") as String
    }
}

gradlePlugin.testSourceSets.add(sourceSets["functionalTest"])

tasks.named<Task>("check") {
    // Include functionalTest as part of the check lifecycle
    dependsOn(testing.suites.named("functionalTest"))
}

dependencies {
    compileOnly(libs.android.tools.build.gradle)
    compileOnly(libs.kotlin.gradle.plugin)
}