# Build Script Documentation

## Overview

The `build-apk.sh` script is a comprehensive, automated build tool for the Sieben Android application. It handles prerequisite checking, dependency management, and APK building with minimal user intervention.

## Features

- ✅ **Automatic Prerequisite Checking**
  - Java JDK version validation
  - Android SDK detection and validation
  - Gradle availability check

- ✅ **Dependency Management**
  - Automatic dependency download
  - Repository configuration updates (jcenter → mavenCentral)
  - Gradle wrapper setup if needed

- ✅ **Flexible Build Options**
  - Debug and release builds
  - Clean build support
  - APK signing capability
  - SDK installation assistance

- ✅ **User-Friendly Interface**
  - Colored output for better readability
  - Progress indicators
  - Comprehensive error messages
  - Detailed help documentation

## Usage

### Basic Commands

```bash
# Make script executable (first time only)
chmod +x build-apk.sh

# Build debug APK (default)
./build-apk.sh

# Build release APK
./build-apk.sh --release

# Build and sign release APK
./build-apk.sh --release --sign

# Clean build
./build-apk.sh --clean --release

# Show help
./build-apk.sh --help
```

### Command Options

| Option | Description |
|--------|-------------|
| `--clean` | Clean build directories before building |
| `--release` | Build release APK (default: debug) |
| `--sign` | Sign the APK after building (release only) |
| `--install-sdk` | Attempt to install Android SDK components |
| `--help` | Show help message |

## Prerequisites

The script will check for and help you install:

1. **Java JDK 8 or higher**
   - Required for compiling Android applications
   - Download from: https://adoptium.net/

2. **Android SDK**
   - Required platforms: API 25 or API 34
   - Build tools: 34.0.0 or compatible
   - Can be installed via script with `--install-sdk`

3. **Gradle**
   - Version 2.2.2 or higher
   - Script can set up Gradle wrapper automatically

## Build Process

The script follows this process:

1. **Prerequisite Checks**
   ```
   [INFO] Checking Java Installation
   [SUCCESS] Java 17 found
   [INFO] Checking Android SDK
   [SUCCESS] Android SDK found at: /path/to/sdk
   [INFO] Checking Gradle
   [SUCCESS] Gradle wrapper found
   ```

2. **Clean Build (if requested)**
   ```
   [INFO] Cleaning Build Directory
   [SUCCESS] Build cleaned
   ```

3. **Download Dependencies**
   ```
   [INFO] Downloading Dependencies
   [INFO] This may take a few minutes on first run...
   [SUCCESS] Dependencies downloaded
   ```

4. **Build APK**
   ```
   [INFO] Building APK (release)
   [SUCCESS] Release APK built successfully!
   [INFO] Location: app/build/outputs/apk/release/app-release-unsigned.apk
   ```

5. **Sign APK (if requested)**
   ```
   [INFO] Signing APK...
   [INFO] Aligning APK...
   [SUCCESS] APK signed and aligned successfully!
   [INFO] Location: app/build/outputs/apk/release/sieben-v1.9-release.apk
   ```

## Output Locations

- **Debug APK**: `app/build/outputs/apk/debug/app-debug.apk`
- **Release APK (unsigned)**: `app/build/outputs/apk/release/app-release-unsigned.apk`
- **Release APK (signed)**: `app/build/outputs/apk/release/sieben-v1.9-release.apk`

## Signing Configuration

When signing for the first time, you'll be prompted to create a keystore:

```bash
./build-apk.sh --release --sign
```

The script will:
1. Create a new keystore at `sieben-release-key.keystore`
2. Prompt for keystore password and certificate information
3. Sign the APK with the generated key
4. Align the APK with zipalign (if available)

**Important**: Keep your keystore file safe! You'll need it for all future releases.

## Troubleshooting

### Java Not Found
```
[ERROR] Java not found. Please install Java JDK 8 or higher
[INFO] Visit: https://adoptium.net/
```
**Solution**: Install Java JDK from the provided link and ensure it's in your PATH.

### Android SDK Not Found
```
[ERROR] ANDROID_HOME not set or Android SDK not found
[INFO] Set ANDROID_HOME environment variable or run with --install-sdk
```
**Solution**: 
- Set ANDROID_HOME: `export ANDROID_HOME=/path/to/android/sdk`
- Or download from: https://developer.android.com/studio#command-tools

### Gradle Issues
```
[ERROR] Gradle not found
```
**Solution**: The script will attempt to set up Gradle wrapper automatically. If that fails, install Gradle manually.

### Dependency Download Failures
```
[WARNING] Failed to download some dependencies. Continuing anyway...
```
**Solution**: 
- Check internet connection
- Ensure firewall allows access to maven repositories
- Update build.gradle repositories if needed

## Advanced Usage

### Custom Android SDK Location
```bash
export ANDROID_HOME=/custom/path/to/sdk
./build-apk.sh --release
```

### Building with Specific Gradle Version
```bash
gradle wrapper --gradle-version 7.6.4
./gradlew assembleRelease
```

### Verifying APK
```bash
# After building
aapt dump badging app/build/outputs/apk/release/app-release-unsigned.apk
```

## Continuous Integration

The script can be used in CI/CD pipelines:

```yaml
# GitHub Actions example
- name: Build APK
  run: |
    chmod +x build-apk.sh
    ./build-apk.sh --release
    
- name: Upload APK
  uses: actions/upload-artifact@v2
  with:
    name: app-release
    path: app/build/outputs/apk/release/*.apk
```

## Script Maintenance

The script includes:
- Error handling with `set -e`
- Colored output for readability
- Comprehensive logging
- Modular function design
- Extensive comments

To modify the script:
1. Open `build-apk.sh` in a text editor
2. Modify configuration variables at the top
3. Add/modify functions as needed
4. Test thoroughly before committing

## Security Notes

- Never commit your keystore file to version control
- Keep keystore passwords secure
- Use strong passwords for release keys
- Backup your keystore in a secure location
- Consider using Android App Bundle (.aab) for Play Store

## Support

For issues with the build script:
1. Run with verbose output: `bash -x build-apk.sh`
2. Check logs for error messages
3. Verify prerequisites are installed
4. Consult BUILD.md for manual build instructions
5. Open an issue on GitHub if problem persists

## Version History

- **v1.0** (Jan 2026) - Initial release with automated build capabilities

---

**Note**: This script is designed for Sieben v1.9. Future versions may require updates to SDK versions or build configurations.
