package com.moengage.gradle.android.library.plugin.configs

/**
 * @author Umang Chamaria
 * Date: 19/09/25
 */

const val DEPENDENCY_TYPE_TEST_IMPLEMENTATION = "testImplementation"

object TestDependencies {
    const val JSON_ASSERT = "org.skyscreamer:jsonassert:1.5.3"
    const val JUNIT_5_PLATFORM = "org.junit.platform:junit-platform-commons:1.11.4"
    const val JUNIT_5_JUPITER = "org.junit.jupiter:junit-jupiter:5.11.4"
}

object CommonDependencies {
    const val KOTLIN_STD_LIB = "org.jetbrains.kotlin:kotlin-stdlib:$KOTLIN_VERSION"
    const val KOTLIN_SERIALIZATION = "org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3"
}