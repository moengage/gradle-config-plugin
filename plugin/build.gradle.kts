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

dependencies {
    compileOnly(libs.android.tools.build.gradle)
    compileOnly(libs.kotlin.gradle.plugin)
}

signing {
    val signingKeyId = project.findProperty("signingInMemoryKeyId") as String
    val signingKey = (project.findProperty("signingInMemoryKey") as String)
    val signingPassword = project.findProperty("signingInMemoryKeyPassword") as String

    useInMemoryPgpKeys(signingKeyId, signingKey, signingPassword)
    sign(configurations.runtimeElements.get())
}