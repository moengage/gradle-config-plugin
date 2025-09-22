# Android Library Gradle Config Plugin

This plugin helps you quickly set up an Android Library module. This plugin would configure all the required plugins for building and publishing a plugin.
This plugin also adds in the required dependencies to write and run unit tests.

## Included Plugins

- Android Library Plugin
- Kotlin Android Plugin
- Library Publishing Plugin

## Included Configuration

- Minimum SDK Version
- Compile SDK Version
- Target SDK Version (for running Android Tests)
- Kotlin Options
  - Explicit API mode
  - JVM Target
- Single variant release
- Release Build configuration, includes pro-guard file path.
- Unit testing
  - JUnit5 Flags
  - JUnit5 dependencies