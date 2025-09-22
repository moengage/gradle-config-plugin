package com.moengage.gradle.android.library.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import com.android.build.api.dsl.LibraryExtension
import com.moengage.gradle.android.library.plugin.configs.*
import org.gradle.api.Action
import org.gradle.api.JavaVersion
import org.gradle.api.internal.artifacts.dependencies.DefaultMutableVersionConstraint
import org.gradle.api.internal.artifacts.dependencies.DefaultPluginDependency
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions


/**
 * @author Umang Chamaria
 * Date: 19/09/25
 */
class AndroidLibraryConfigPlugin : Plugin<Project> {
    override fun apply(project: Project): Unit = with(project) {
        configurePlugins()
        addTestDependencies()
        extensions.configure<LibraryExtension> {
            addSdkAndConfigs()
            addBuildTypes()
            addCompileOptions()
            addTestOptions()
            addPublishing()
            addBuildFeatures()
            addLintOptions()
            addKotlinOptions()
        }
    }

    private fun Project.configurePlugins() {
        val androidLibrary = DefaultPluginDependency(
            PLUGIN_ID_ANDROID_LIBRARY, DefaultMutableVersionConstraint(
                VERSION_ANDROID_PLUGIN
            ))
        plugins.apply(androidLibrary.pluginId)
        val kotlinPlugin =
            DefaultPluginDependency(PLUGIN_ID_KOTLIN_ANDROID, DefaultMutableVersionConstraint(KOTLIN_VERSION))
        plugins.apply(kotlinPlugin.pluginId)
        val mavenPlugin = DefaultPluginDependency(
            PLUGIN_ID_MAVEN_PUBLISH, DefaultMutableVersionConstraint(
                VERSION_MAVEN_PLUGIN
            ))
        plugins.apply(mavenPlugin.pluginId)
    }

    private fun LibraryExtension.addSdkAndConfigs() {
        compileSdk = ANDROID_BUILD_CONFIG_COMPILE_SDK_VERSION
        defaultConfig {
            minSdk = ANDROID_BUILD_CONFIG_MINIMUM_SDK_VERSION
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
            consumerProguardFiles("consumer-rules.pro")
        }
    }

    private fun LibraryExtension.addCompileOptions() {
        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_1_8
            targetCompatibility = JavaVersion.VERSION_1_8
        }
    }

    private fun LibraryExtension.addPublishing() {
        publishing {
            singleVariant("release") {
                withSourcesJar()
            }
        }
    }

    private fun LibraryExtension.addBuildTypes() {
        buildTypes {
            getByName("release") {
                isMinifyEnabled = false
                proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
                )
            }
        }
    }

    private fun LibraryExtension.addTestOptions() {
        testOptions {
            targetSdk = ANDROID_BUILD_CONFIG_TARGET_SDK_VERSION
            unitTests {
                all {
                    it.useJUnitPlatform()
                }
            }
        }
    }

    private fun LibraryExtension.addBuildFeatures() {
        buildFeatures {
            buildConfig = true
        }
    }

    private fun LibraryExtension.addLintOptions() {
        lint {
            targetSdk = ANDROID_BUILD_CONFIG_TARGET_SDK_VERSION
        }
    }

    private fun LibraryExtension.addKotlinOptions() {
        val configure = Action<KotlinJvmOptions> {
            jvmTarget = "1.8"
            freeCompilerArgs = freeCompilerArgs + "-Xexplicit-api=strict"
        }
        (this as org.gradle.api.plugins.ExtensionAware).extensions.configure("kotlinOptions", configure)
    }

    private fun Project.addTestDependencies() {
        dependencies {
            add(DEPENDENCY_TYPE_TEST_IMPLEMENTATION, CommonDependencies.KOTLIN_STD_LIB)
            add(DEPENDENCY_TYPE_TEST_IMPLEMENTATION, TestDependencies.JSON_ASSERT)
            add(DEPENDENCY_TYPE_TEST_IMPLEMENTATION, TestDependencies.JUNIT_5_PLATFORM)
            add(DEPENDENCY_TYPE_TEST_IMPLEMENTATION, TestDependencies.JUNIT_5_JUPITER)
        }
    }
}