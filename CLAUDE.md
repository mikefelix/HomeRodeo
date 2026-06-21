# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run

```bash
# Build debug APK
./gradlew assembleDebug

# Run unit tests
./gradlew test

# Run instrumented tests (requires connected device/emulator)
./gradlew connectedAndroidTest

# Run a single unit test class
./gradlew test --tests "com.example.homerodeo.ExampleUnitTest"

# Install and run on connected device
./gradlew installDebug
```

## Architecture

This is an early-stage Android app using Jetpack Compose with a bottom navigation shell.

- **`RodeoActivity`** — the main entry point. Extends `BottomTabsActivity`, overrides `Content()` to render `HomeRodeoApp`.
- **`BottomTabsActivity`** — abstract base `ComponentActivity` that wraps content in `HomeRodeoTheme` and calls `Content()`. Concrete activities extend this and provide a `TabsViewModel`.
- **`TabsViewModel`** — holds the tab list (`Tab` data class with label + icon drawable) and tracks `currentDestination` via Compose state.
- **`HomeRodeoApp`** — top-level composable using `NavigationSuiteScaffold` (from `material3.adaptive`) to render adaptive bottom/side/rail navigation from the tab list.

The tab items (Home, Favorites, Profile) are defined in `TabsViewModel.tabs` and backed by vector drawables in `res/drawable/`.

## Tech Stack

- Kotlin + Jetpack Compose
- Material3 with `NavigationSuiteScaffold` for adaptive navigation
- `minSdk = 33`, `compileSdk = 36`
- ViewModel via `androidx.lifecycle.viewmodel.compose`
