# Building Sieben APK

This document describes how to build the Sieben Android application APK.

## Quick Start - Automated Build Script (Recommended)

We provide an automated build script that handles all prerequisites and builds the APK:

```bash
# Make script executable (first time only)
chmod +x build-apk.sh

# Build debug APK
./build-apk.sh

# Build release APK
./build-apk.sh --release

# Build and sign release APK
./build-apk.sh --release --sign

# Clean build
./build-apk.sh --clean --release
```

The script will:
- Check for Java, Android SDK, and Gradle
- Download missing dependencies automatically
- Build the APK
- Optionally sign the APK for distribution

For more options, run: `./build-apk.sh --help`

## Manual Build Instructions

### Prerequisites

- Android SDK with API level 25 or higher
- Build Tools version 25.0.0 or higher  
- Gradle 2.2.2 or higher
- Java Development Kit (JDK) 8 or higher

### Using Gradle (Recommended)

1. **Clean the project:**
   ```bash
   ./gradlew clean
   ```

2. **Build debug APK:**
   ```bash
   ./gradlew assembleDebug
   ```
   The APK will be located at: `app/build/outputs/apk/debug/app-debug.apk`

3. **Build release APK:**
   ```bash
   ./gradlew assembleRelease
   ```
   The APK will be located at: `app/build/outputs/apk/release/app-release-unsigned.apk`

4. **Sign the release APK** (Optional, for distribution):
   ```bash
   keytool -genkey -v -keystore sieben-release-key.keystore -alias sieben -keyalg RSA -keysize 2048 -validity 10000
   jarsigner -verbose -sigalg SHA1withRSA -digestalg SHA1 -keystore sieben-release-key.keystore app/build/outputs/apk/release/app-release-unsigned.apk sieben
   zipalign -v 4 app/build/outputs/apk/release/app-release-unsigned.apk app/build/outputs/apk/release/sieben-v1.9-release.apk
   ```

### Using Android Studio

1. Open the project in Android Studio
2. Select **Build > Build Bundle(s) / APK(s) > Build APK(s)**
3. The APK will be generated in `app/build/outputs/apk/`

## Troubleshooting

### Gradle issues
If you encounter Gradle build errors, ensure that:
- Your `ANDROID_HOME` environment variable is set correctly
- You have the correct Android SDK platforms and build tools installed
- Internet connection is available for dependency downloads

### JCenter deprecation
If you see errors related to JCenter being unavailable, update the `build.gradle` files to use `mavenCentral()` instead of `jcenter()`.

## Release Checklist

Before creating a release:
- [ ] Update version code and version name in `app/build.gradle`
- [ ] Update `CHANGELOG.md` with new changes
- [ ] Update `README.md` if necessary
- [ ] Test the app on different Android versions
- [ ] Build and test the release APK
- [ ] Sign the APK for distribution
- [ ] Create a GitHub release with the signed APK
- [ ] Update F-Droid metadata if publishing there

## Version 1.9 Release Notes

This version includes:
- Spanish translation (values-es)
- Italian translation (values-it)
- Portuguese translation (values-pt)
- Updated documentation
- Version bump from 1.8 (versionCode 30) to 1.9 (versionCode 31)
