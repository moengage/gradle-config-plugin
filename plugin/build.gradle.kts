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
        val test by getting(JvmTestSuite::class) {
            useKotlinTest("2.3.20")
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

dependencies {
    compileOnly(libs.android.tools.build.gradle)
    compileOnly(libs.kotlin.gradle.plugin)
}

signing {
    val rawKeyId = project.findProperty("signingInMemoryKeyId") as String
    val rawKey = project.findProperty("signingInMemoryKey") as String
    val rawPassword = project.findProperty("signingInMemoryKeyPassword") as String

    // Temporary diagnostics: prints only shapes of the values, never the values themselves.
    fun describe(label: String, value: String) = println(
        "[signing-diag] $label: length=${value.length}, lines=${value.lines().size}, " +
            "leadingWhitespace=${value.firstOrNull()?.isWhitespace() == true}, " +
            "trailingWhitespace=${value.lastOrNull()?.isWhitespace() == true}, " +
            "endsWithNewline=${value.endsWith("\n")}, hasCR=${value.contains('\r')}, " +
            "literalBackslashN=${value.contains("\\n")}, backslashes=${value.count { it == '\\' }}, " +
            "nonAscii=${value.count { it.code > 127 }}"
    )
    describe("keyId", rawKeyId)
    describe("key", rawKey)
    describe("password", rawPassword)
    println("[signing-diag] key starts with armor header=${rawKey.trimStart().startsWith("-----BEGIN PGP PRIVATE KEY BLOCK-----")}")

    val signingKeyId = rawKeyId.trim()
    val signingKey = rawKey.replace("\\n", "\n").replace("\r", "")
    val signingPassword = rawPassword.trimEnd('\n', '\r')

    useInMemoryPgpKeys(signingKeyId, signingKey, signingPassword)
    sign(publishing.publications)
}