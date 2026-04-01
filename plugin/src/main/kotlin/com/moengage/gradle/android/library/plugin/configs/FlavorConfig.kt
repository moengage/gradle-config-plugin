package com.moengage.gradle.android.library.plugin.configs

/**
 * Configuration for a single product flavor.
 *
 * @author Abhishek Kumar
 */
public data class FlavorConfig(
    /** Name of the flavor (e.g. dev, prod). */
    val name: String,
    /** Optional minSdk value to override for this flavor. */
    val minSdk: Int? = null,
    /** Optional missingDimensionStrategy pair of (dimension, requestedFlavor). */
    val missingDimensionStrategy: Pair<String, String>? = null
)