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
            useKotlinTest("2.3.20")
        }

        // Create a new test suite
        val functionalTest by registering(JvmTestSuite::class) {
            // Use Kotlin Test test framework
            useKotlinTest("2.3.20")

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

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xexplicit-api=strict")
    }
}

gradlePlugin {
    website = "https://github.com/moengage/gradle-config-plugin"
    vcsUrl = "https://github.com/moengage/gradle-config-plugin"

    val pluginName = project.findProperty("NAME") as String
    val pluginDescription = project.findProperty("DESCRIPTION") as String

    plugins {
        register("androidModuleConfigPlugin") {
            id = "com.moengage.android.library.config.plugin"
            implementationClass = "com.moengage.gradle.android.library.plugin.AndroidModuleConfigPlugin"
            displayName = pluginName
            description = pluginDescription
            tags = listOf("MoEngage", "Gradle Config")
        }
        register("hybridModuleConfigPlugin") {
            id = "com.moengage.android.hybrid.module.config.plugin"
            implementationClass = "com.moengage.gradle.android.library.plugin.HybridModuleConfigPlugin"
            displayName = pluginName
            description = pluginDescription
            tags = listOf("MoEngage", "Gradle Config")
        }
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

// Local publish: signing disabled. Restore before pushing upstream.
// signing {
//     val signingKeyId = project.findProperty("signingInMemoryKeyId") as String
//     val signingKey = (project.findProperty("signingInMemoryKey") as String)
//     val signingPassword = project.findProperty("signingInMemoryKeyPassword") as String
//
//     useInMemoryPgpKeys(signingKeyId, signingKey, signingPassword)
//     sign(configurations.runtimeElements.get())
// }