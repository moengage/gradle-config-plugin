# MoEngage Android Gradle Config Plugin

| Plugin ID | Latest Version |
|-----------|----------------|
| `com.moengage.android.library.config.plugin` | [![Gradle Plugin Portal](https://img.shields.io/gradle-plugin-portal/v/com.moengage.android.library.config.plugin)](https://plugins.gradle.org/plugin/com.moengage.android.library.config.plugin) |

---

## 1. Overview

`com.moengage.android.library.config.plugin` is a Gradle plugin for **Android library modules** that standardises:

- Android + Kotlin configuration
- SDK versions and compile options
- Lint and test options
- Default build types
- Maven publishing
- Optional plugins (Kotlin serialization, Dokka, release publishing)
- Product flavours, build features, BuildConfig fields, and instrumentation runner

The goal is to remove duplication across multiple modules and provide a **simple, declarative DSL** so each module only specifies what is different.

---

## 2. Getting started

### 2.1. Declare the plugin in your version catalog (recommended)

In your **root** project, add the plugin to `gradle/libs.versions.toml`:

```toml
[plugins]
plugin-native-module-config = { id = "com.moengage.android.library.config.plugin", version = "pluginVersion" }
```

> Replace `pluginVersion` with the latest version from the Gradle Plugin Portal badge above.

---

### 2.2. Apply the plugin in your module (Android library)

In the **module-level** `build.gradle.kts` or `build.gradle` of your Android library, apply the plugin using the version catalog alias.

<details>
<summary><code>build.gradle.kts</code> (Kotlin DSL)</summary>

```kotlin
plugins {
    alias(libs.plugins.plugin.native.module.config)
}
```
</details>

<details>
<summary><code>build.gradle</code> (Groovy)</summary>

```groovy
plugins {
    alias(libs.plugins.plugin.native.module.config)
}
```
</details>

> Note: No `buildscript` classpath entry is required when using the plugins DSL with the version catalog.

The plugin will then apply common static configuration automatically and expose the `moduleConfig.configure` extension for dynamic configuration.

---

## 3. Static configuration (applied automatically)

When the plugin is applied to an Android library module, it configures the underlying `com.android.library` extension with same defaults.

> You **do not** need to configure these unless you want to override them directly using the `android {}` block.

### 3.1. SDK versions

Configured by `SDKVersionConfiguration`:

- `compileSdk` – set to the compile SDK version
- `minSdk` – default minimum supported Android version
- `targetSdk` – target Android version 

### 3.2. Java/Kotlin compile options

Configured by `CompileOptionConfiguration` and `KotlinOptionConfiguration`:

- Java source/target compatibility (e.g., `JavaVersion.VERSION_1_8`)
- Kotlin JVM target aligned with Java version
- Common Kotlin compiler flags used across SDK modules

### 3.3. Build types

Configured by `DebugBuildTypeConfiguration`:

- Ensures a `debug` build type exists
- Applies common debug settings for test case coverage

Additional build types can be added or customised using dynamic configuration (see section **4.4**).

### 3.4. Lint configuration

Configured by `LintConfiguration`:

- Common lint options shared across SDK modules (e.g., warnings as errors, baseline handling)

### 3.5. Test options

Configured by `TestOptionsConfiguration`:

- Standard unit and instrumentation test options

### 3.6. Publishing

Configured by `PublishingConfiguration`:

- Sets up Maven publishing for the library artifact using MoEngage’s internal conventions

---

## 4. Dynamic configuration via `nativeModuleConfig`

For per-module customisation, use the `nativeModuleConfig` extension provided by the plugin.

```kotlin
moduleConfig.configure {
    // configure plugins, flavours, build features, build types, instrumentation runner
}
```

Below are all available configuration blocks with **default values** and **possible values**.

> Type names are shown in Kotlin for clarity. Groovy usage is similar (no explicit `.set(...)` for simple assignments).

---

### 4.1. Optional plugins configuration

Controls optional Gradle plugins applied to the module.

- Backed by: `PluginConfiguration`

#### Properties

- `kotlinSerializationEnabled: Property<Boolean>`  
  - **Default:** `true`  
  - **When true:** applies `org.jetbrains.kotlin.plugin.serialization`  
  - **Possible values:** `true` or `false`

- `releasePluginEnabled: Property<Boolean>`  
  - **Default:** `true`  
  - **When true:** applies `com.moengage.plugin.maven.publish`  
  - **Possible values:** `true` or `false`

- `dokkaPluginEnabled: Property<Boolean>`  
  - **Default:** `true`  
  - **When true:** applies `org.jetbrains.dokka`  
  - **Possible values:** `true` or `false`

#### Example

```kotlin
moduleConfig.configure {
    plugins {
        kotlinSerializationEnabled.set(true)
        releasePluginEnabled.set(true)
        dokkaPluginEnabled.set(false)
    }
}
```

---

### 4.2. Product flavour configuration

Defines flavour dimension and flavours for the Android library.

- Backed by: `ProductFlavorConfiguration`

#### Properties

- `dimension: Property<String>`  
  - **Default:** not set (no flavour dimension is added)  
  - **Possible values:** any non-empty string (e.g., `"environment"`, `"tier"`)

- `flavor(name: String, minSdk: Int? = null, missingDimensionStrategy: Pair<String, String>? = null)`  
  - Registers a product flavour.
  - `name` – flavour name (e.g., `"dev"`, `"prod"`)  
  - `minSdk` – optional per-flavour `minSdk` override  
  - `missingDimensionStrategy` – optional pair of `(dimensionName, defaultFlavor)`

#### Defaults

- If no flavours are added, the module will behave as a single-flavour module (default Android behaviour).

#### Possible values

- Any number of flavours (e.g., `dev`, `staging`, `prod`)
- Any valid `minSdk` integer >= module `minSdk`

#### Example

```kotlin
moduleConfig.configure {
    productFlavour {
        dimension.set("environment")

        flavor(
            name = "dev",
            minSdk = 23,
            missingDimensionStrategy = "environment" to "dev"
        )

        flavor(
            name = "prod",
            minSdk = 23
        )
    }
}
```

---

### 4.3. Build features / BuildConfig configuration

Controls whether `BuildConfig` is generated and lets you declare custom `BuildConfig` fields.

- Backed by: `BuildFeatureConfiguration`

#### Properties

- `buildConfigField(type: String, name: String, value: String)`  
  - Adds a `BuildConfig` field on `defaultConfig`.
  - `type` – Type of `BuildConfigType`
  - `name` – constant name (e.g., `"SDK_NAME"`)  
  - `value` – **raw** value as it should appear in code (e.g., `"\"MoEngageSDK\""` for a string)

#### Defaults

- No additional `BuildConfig` fields are added by default.

#### Example

```kotlin
moduleConfig.configure {
    buildFeature {
        buildConfigField(BuildConfigType.STRING, "SDK_NAME", "\"MoEngageSampleSDK\"")
        buildConfigField(BuildConfigType.BOOLEAN, "FEATURE_FLAG_ENABLED", "true")
    }
}
```

---

### 4.4. Build types configuration

Configures or creates build types like `release`, `debug`, or custom ones.

- Backed by: `BuildTypeConfiguration` and `BuildTypeConfig`

#### `BuildTypeConfig` properties

- `name: String`  
  - Build type name (e.g., `"release"`, `"debug"`, `"benchmark"`).  
  - **Possible values:** any valid Gradle build type name

- `kind: BuildTypeKind`  
  - How the build type should be handled.  
  - **Possible values:**
    - `BuildTypeKind.REGISTER` – uses existing build type or registers it if missing
    - `BuildTypeKind.CREATE` – creates a new build type explicitly

- `isMinifyEnabled: Boolean`  
  - Controls code shrinking (R8/ProGuard).  
  - **Default:** `false` (if not set in your own configuration)  
  - **Possible values:** `true` or `false`

- `isShrinkResources: Boolean`  
  - Controls resource shrinking.  
  - **Default:** `false` (if not set in your own configuration)  
  - **Possible values:** `true` or `false`

- `proguardFiles: List<String>`  
  - List of ProGuard/R8 file names.  
  - The plugin will map:
    - Defaults (e.g., `"proguard-android.txt"`) using `getDefaultProguardFile`
    - Custom files via `project.file(...)`

#### Defaults

- A default `debug` build type is configured by the static configuration.
- No extra build types are added by `nativeModuleConfig` unless you define them.

#### Example

```kotlin
import com.moengage.gradle.android.library.plugin.configs.BuildTypeConfig
import com.moengage.gradle.android.library.plugin.configs.BuildTypeKind

moduleConfig.configure {
    buildTypes {
        buildTypes.set(
            listOf(
                BuildTypeConfig(
                    name = "release",
                    kind = BuildTypeKind.REGISTER,
                    isMinifyEnabled = false,
                    isShrinkResources = false,
                    proguardFiles = listOf(
                        "proguard-android.txt",
                        "proguard-rules.pro"
                    )
                )
            )
        )
    }
}
```

---

### 4.5. Instrumentation runner configuration

Configures the default Android instrumentation test runner.

- Backed by: `InstrumentationRunnerConfiguration`

#### Properties

- `useDefaultRunner: Property<Boolean>`  
  - **Default:** `true`  
  - **When true:** sets `defaultConfig.testInstrumentationRunner` to the plugin's `DEFAULT_ANDROID_RUNNER` (typically `"androidx.test.runner.AndroidJUnitRunner"`).  
  - **Possible values:** `true` or `false`

#### Example

```kotlin
moduleConfig.configure {
    instrumentationRunner {
        useDefaultRunner.set(true)
    }
}
```

This results in:

```kotlin
android {
    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}
```

---

## 5. Full example

```kotlin
plugins {
    id("com.moengage.android.library.config.plugin")
}

nativeModuleConfig {
    // Include/Exclude plugins 
    plugins {
        kotlinSerializationEnabled.set(true)
        releasePluginEnabled.set(true)
        dokkaPluginEnabled.set(false)
    }

    // Product flavours
    productFlavour {
        dimension.set("environment")
        flavor("dev", minSdk = 23)
        flavor("prod", minSdk = 23)
    }

    // Build features / BuildConfig
    buildFeature {
        buildConfigField(BuildConfigType.STRING, "SDK_NAME", "\"MoEngageExampleSDK\"")
    }

    // Build types
    buildTypes {
        buildTypes.set(
            listOf(
                BuildTypeConfig(
                    name = "release",
                    kind = BuildTypeKind.REGISTER,
                    isMinifyEnabled = false,
                    isShrinkResources = false,
                    proguardFiles = listOf(
                        "proguard-android.txt",
                        "proguard-rules.pro"
                    )
                )
            )
        )
    }

    // Exclude Instrumentation runner
    instrumentationRunner {
        useDefaultRunner.set(false)
    }
}
```

After configuring, **sync the project** and run your usual Gradle tasks (e.g., `assemble`, `test`).
