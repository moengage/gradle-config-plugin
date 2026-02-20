package com.moengage.gradle.android.library.plugin.configuration.static

import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Project

/**
 * Configures Maven publishing for Android library modules by enabling a
 * single "release" variant with an attached sources JAR.
 *
 * This configuration delegates to the Android Gradle Plugin's publishing
 * DSL and is intended to be applied uniformly across all SDK libraries.
 *
 * @author Abhishek Kumar
 */
internal abstract class PublishingConfiguration : StaticConfig {

    override fun apply(
        project: Project,
        libraryExtension: LibraryExtension
    ) {
        with(libraryExtension) {
            publishing {
                singleVariant("release") {
                    withSourcesJar()
                }
            }
        }
    }
}