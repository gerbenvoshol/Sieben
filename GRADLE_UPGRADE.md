# Gradle Upgrade Documentation

## Problem Statement

The project was experiencing a Gradle compatibility error:
```
Unable to find method 'void org.gradle.api.internal.DefaultDomainObjectSet.<init>(java.lang.Class)'
```

This error occurs when there is a version mismatch between Gradle and the Android Gradle Plugin (AGP). The `DefaultDomainObjectSet` constructor signature changed in newer Gradle versions, making old AGP versions incompatible with newer Gradle versions.

## Root Cause

- **Previous Configuration:**
  - Gradle: 4.10.3 (released December 2018)
  - Android Gradle Plugin (AGP): 2.3.0 (released March 2017)
  - compileSdkVersion: 25 (Android 7.1, released October 2016)

- **Issue:** AGP 2.3.0 uses internal Gradle APIs (`DefaultDomainObjectSet`) that were changed/removed in Gradle 5.0+. This makes it impossible to use modern Gradle versions with very old AGP versions.

## Solution Applied

### 1. Gradle Wrapper Upgrade
**File:** `gradle/wrapper/gradle-wrapper.properties`

- **Before:** `gradle-4.10.3-bin.zip`
- **After:** `gradle-7.6.4-bin.zip`
- **Reasoning:** Gradle 7.6.4 is a stable LTS version compatible with AGP 7.4.x and provides modern build features while maintaining good stability.

### 2. Android Gradle Plugin Upgrade
**File:** `build.gradle`

- **Before:** `classpath 'com.android.tools.build:gradle:2.3.0'`
- **After:** `classpath 'com.android.tools.build:gradle:7.4.2'`
- **Reasoning:** AGP 7.4.2 is compatible with Gradle 7.5-7.6 and resolves the `DefaultDomainObjectSet` issue.

### 3. Android SDK Version Updates
**File:** `app/build.gradle`

- **compileSdkVersion:** 25 → 34 (Android 14)
- **targetSdkVersion:** 25 → 34 (Android 14)
- **Added:** `namespace 'de.baumann.sieben'` (required for AGP 7.0+)
- **Removed:** `buildToolsVersion '25.0.2'` (no longer required, auto-selected by AGP)
- **Removed:** `return void` (unnecessary statement)

### 4. Dependency Configuration Updates
**File:** `app/build.gradle`

- **Before:** `compile`, `testCompile`
- **After:** `implementation`, `testImplementation`
- **Reasoning:** The `compile` configuration was deprecated in Gradle 3.0 and removed in Gradle 7.0. Modern configurations provide better dependency resolution and build performance.

- **JUnit Update:** `junit:junit:4.12` → `junit:junit:4.13.2`

### 5. Gradle Properties
**File:** `gradle.properties`

Added:
```properties
android.useAndroidX=false
android.enableJetifier=false
```

**Reasoning:** Explicitly declares that the project is not using AndroidX (it still uses legacy support libraries). This prevents AGP from attempting AndroidX migration.

## Compatibility Matrix

| Component | Old Version | New Version | Compatibility |
|-----------|-------------|-------------|---------------|
| Gradle | 4.10.3 | 7.6.4 | ✅ AGP 7.4.2 requires Gradle 7.5+ |
| AGP | 2.3.0 | 7.4.2 | ✅ Fixes DefaultDomainObjectSet issue |
| compileSdk | 25 | 34 | ✅ Modern API support |
| Java | 8+ | 8+ | ✅ Compatible |

## Verification Steps

To verify the upgrade works correctly:

```bash
# 1. Check Gradle version
./gradlew --version

# Expected output: Gradle 7.6.4

# 2. Clean the project
./gradlew clean

# 3. Build the project
./gradlew assembleDebug

# 4. Run tests
./gradlew test

# 5. Generate release APK
./gradlew assembleRelease
```

## Known Limitations

1. **Support Library:** The project still uses the legacy Android Support Library (25.3.1) instead of AndroidX. While this works with AGP 7.4.2, it's recommended to migrate to AndroidX for long-term maintenance.

2. **compileSdkVersion:** Updated to 34, but the support library versions remain at 25.3.1. This combination works but is not ideal. Consider upgrading to AndroidX equivalents:
   - `appcompat-v7:25.3.1` → `androidx.appcompat:appcompat:1.6.1`
   - `design:25.3.1` → `com.google.android.material:material:1.11.0`
   - `cardview-v7:25.3.1` → `androidx.cardview:cardview:1.0.0`

3. **minSdkVersion 17:** Still targets Android 4.2 (Jelly Bean). While this maximizes device compatibility, consider raising this to at least 21 (Android 5.0) to simplify development.

## Further Upgrade Path (Optional)

For even more modern configuration, consider:

1. **Upgrade to AGP 8.x:**
   - Requires Gradle 8.0+
   - Requires Java 17+
   - Provides additional features and optimizations

2. **Migrate to AndroidX:**
   - Required for accessing newer Android features
   - Better long-term support
   - More active development

3. **Update third-party dependencies:**
   - Some dependencies may have updates or AndroidX versions
   - Check for newer versions of AndroidOnboarder and material-about-library

4. **Consider Kotlin DSL:**
   - Modern Gradle projects use `build.gradle.kts` instead of `build.gradle`
   - Provides better IDE support and type safety

## Migration Script

For automated migration, you can use:

```bash
# Update Gradle wrapper
./gradlew wrapper --gradle-version=7.6.4

# Clean build
./gradlew clean

# Rebuild
./gradlew build
```

## Gradle 9.0 Compatibility Updates (2026)

### Additional Deprecated Features Fixed

To ensure compatibility with Gradle 9.0, the following deprecated features were updated:

#### 1. Replaced `lintOptions` with `lint`
**File:** `app/build.gradle`

- **Before:** `lintOptions { ... }`
- **After:** `lint { ... }`
- **Reasoning:** The `lintOptions` DSL was deprecated in AGP 7.0 and replaced with `lint` for consistency and future compatibility.

#### 2. Migrated Repository Declarations to `settings.gradle`
**Files:** `build.gradle`, `settings.gradle`

- **Before:** Repository declarations in `allprojects` block in `build.gradle`
- **After:** Repository declarations moved to `dependencyResolutionManagement` in `settings.gradle`
- **Reasoning:** The `allprojects` block for repository configuration is deprecated in Gradle 7+ and will be removed in Gradle 9.0. The modern approach uses `dependencyResolutionManagement` in `settings.gradle` with `FAIL_ON_PROJECT_REPOS` mode to centralize repository management and improve build performance.

**Changes in `settings.gradle`:**
```gradle
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri('https://jitpack.io') }
    }
}
```

**Removed from `build.gradle`:**
```gradle
allprojects {
    repositories {
        google()
        mavenCentral()
        maven { url = 'https://jitpack.io' }
    }
}
```

These updates ensure the project is ready for Gradle 9.0 while maintaining compatibility with Gradle 7.6.4.

## References

- [Android Gradle Plugin Release Notes](https://developer.android.com/studio/releases/gradle-plugin)
- [Gradle Release Notes](https://docs.gradle.org/current/release-notes.html)
- [AGP/Gradle Compatibility Matrix](https://developer.android.com/studio/releases/gradle-plugin#updating-gradle)
- [Migrating to Android Plugin for Gradle 7.0](https://developer.android.com/studio/releases/gradle-plugin#7-0-0)
- [Gradle 9.0 Deprecations](https://docs.gradle.org/current/userguide/upgrading_version_8.html)
