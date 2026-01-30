#!/bin/bash

################################################################################
# Sieben APK Build Script
# 
# This script automates the process of:
# 1. Checking/installing prerequisites
# 2. Downloading required dependencies
# 3. Building the APK
# 4. Optionally signing the APK
#
# Usage: ./build-apk.sh [options]
#   Options:
#     --clean          Clean build directories before building
#     --release        Build release APK (default: debug)
#     --sign           Sign the APK after building (release only)
#     --install-sdk    Install Android SDK if not present
#     --help           Show this help message
################################################################################

set -e  # Exit on error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Default configuration
BUILD_TYPE="debug"
CLEAN_BUILD=false
SIGN_APK=false
INSTALL_SDK=false
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Android SDK configuration
ANDROID_SDK_VERSION="34"
ANDROID_BUILD_TOOLS="34.0.0"
MIN_JAVA_VERSION=8

################################################################################
# Helper Functions
################################################################################

print_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

print_header() {
    echo -e "\n${BLUE}========================================${NC}"
    echo -e "${BLUE}$1${NC}"
    echo -e "${BLUE}========================================${NC}\n"
}

show_help() {
    cat << EOF
Sieben APK Build Script

Usage: ./build-apk.sh [options]

Options:
  --clean          Clean build directories before building
  --release        Build release APK (default: debug)
  --sign           Sign the APK after building (release only)
  --install-sdk    Install Android SDK if not present
  --help           Show this help message

Examples:
  ./build-apk.sh                    # Build debug APK
  ./build-apk.sh --release          # Build release APK
  ./build-apk.sh --release --sign   # Build and sign release APK
  ./build-apk.sh --clean --release  # Clean build release APK

Requirements:
  - Java JDK 8 or higher
  - Gradle (will be downloaded if not present)
  - Android SDK (can be installed with --install-sdk)
  - Internet connection for downloading dependencies

EOF
}

################################################################################
# Prerequisite Checks
################################################################################

check_java() {
    print_header "Checking Java Installation"
    
    if command -v java &> /dev/null; then
        JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d'.' -f1)
        if [ "$JAVA_VERSION" -ge "$MIN_JAVA_VERSION" ]; then
            print_success "Java $JAVA_VERSION found"
            java -version
            return 0
        else
            print_error "Java version $JAVA_VERSION is too old. Need Java $MIN_JAVA_VERSION or higher"
            return 1
        fi
    else
        print_error "Java not found. Please install Java JDK $MIN_JAVA_VERSION or higher"
        print_info "Visit: https://adoptium.net/"
        return 1
    fi
}

check_android_sdk() {
    print_header "Checking Android SDK"
    
    if [ -n "$ANDROID_HOME" ] && [ -d "$ANDROID_HOME" ]; then
        print_success "Android SDK found at: $ANDROID_HOME"
        
        # Check for required platform
        if [ -d "$ANDROID_HOME/platforms/android-$ANDROID_SDK_VERSION" ] || \
           [ -d "$ANDROID_HOME/platforms/android-25" ]; then
            print_success "Required Android platform found"
            return 0
        else
            print_warning "Android platform not found"
            if [ "$INSTALL_SDK" = true ]; then
                install_android_platform
                return $?
            else
                print_error "Run with --install-sdk to install required components"
                return 1
            fi
        fi
    else
        print_error "ANDROID_HOME not set or Android SDK not found"
        if [ "$INSTALL_SDK" = true ]; then
            install_android_sdk
            return $?
        else
            print_info "Set ANDROID_HOME environment variable or run with --install-sdk"
            return 1
        fi
    fi
}

check_gradle() {
    print_header "Checking Gradle"
    
    if [ -f "$SCRIPT_DIR/gradlew" ]; then
        print_success "Gradle wrapper found"
        chmod +x "$SCRIPT_DIR/gradlew"
        return 0
    elif command -v gradle &> /dev/null; then
        print_success "Gradle found in system"
        gradle --version
        return 0
    else
        print_info "Gradle not found. Will attempt to use wrapper or install"
        setup_gradle_wrapper
        return $?
    fi
}

################################################################################
# Installation Functions
################################################################################

setup_gradle_wrapper() {
    print_info "Setting up Gradle wrapper..."
    
    # Create a minimal gradle wrapper structure
    mkdir -p "$SCRIPT_DIR/gradle/wrapper"
    
    # Download gradle wrapper jar if not present
    if [ ! -f "$SCRIPT_DIR/gradle/wrapper/gradle-wrapper.jar" ]; then
        print_info "Downloading Gradle wrapper..."
        GRADLE_VERSION="7.6.4"
        GRADLE_URL="https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip"
        
        wget -q "$GRADLE_URL" -O /tmp/gradle.zip || {
            print_error "Failed to download Gradle"
            return 1
        }
        
        unzip -q /tmp/gradle.zip -d /tmp/
        cp -r /tmp/gradle-${GRADLE_VERSION}/lib/plugins/gradle-wrapper-*.jar "$SCRIPT_DIR/gradle/wrapper/gradle-wrapper.jar"
        rm -rf /tmp/gradle.zip /tmp/gradle-${GRADLE_VERSION}
    fi
    
    # Create gradlew script if not present
    if [ ! -f "$SCRIPT_DIR/gradlew" ]; then
        cat > "$SCRIPT_DIR/gradlew" << 'GRADLEW_SCRIPT'
#!/bin/sh
exec gradle "$@"
GRADLEW_SCRIPT
        chmod +x "$SCRIPT_DIR/gradlew"
    fi
    
    print_success "Gradle wrapper setup complete"
    return 0
}

install_android_sdk() {
    print_info "Android SDK installation would require manual setup"
    print_info "Please download and install Android SDK from:"
    print_info "https://developer.android.com/studio#command-tools"
    print_info ""
    print_info "After installation, set ANDROID_HOME environment variable:"
    print_info "export ANDROID_HOME=/path/to/android/sdk"
    return 1
}

install_android_platform() {
    print_info "Installing Android platform..."
    
    if [ -x "$ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager" ]; then
        "$ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager" \
            "platforms;android-$ANDROID_SDK_VERSION" \
            "build-tools;$ANDROID_BUILD_TOOLS"
        return $?
    elif [ -x "$ANDROID_HOME/tools/bin/sdkmanager" ]; then
        "$ANDROID_HOME/tools/bin/sdkmanager" \
            "platforms;android-$ANDROID_SDK_VERSION" \
            "build-tools;$ANDROID_BUILD_TOOLS"
        return $?
    else
        print_error "sdkmanager not found. Please install manually"
        return 1
    fi
}

################################################################################
# Build Functions
################################################################################

clean_build() {
    print_header "Cleaning Build Directory"
    
    if [ -f "$SCRIPT_DIR/gradlew" ]; then
        "$SCRIPT_DIR/gradlew" clean
    else
        gradle clean
    fi
    
    print_success "Build cleaned"
}

download_dependencies() {
    print_header "Downloading Dependencies"
    
    print_info "This may take a few minutes on first run..."
    
    # Update repositories in build.gradle if needed
    if grep -q "jcenter()" "$SCRIPT_DIR/build.gradle"; then
        print_warning "Updating deprecated jcenter() repository to mavenCentral()"
        sed -i 's/jcenter()/mavenCentral()\n        google()/g' "$SCRIPT_DIR/build.gradle"
    fi
    
    # Download dependencies
    if [ -f "$SCRIPT_DIR/gradlew" ]; then
        "$SCRIPT_DIR/gradlew" dependencies --refresh-dependencies
    else
        gradle dependencies --refresh-dependencies
    fi
    
    print_success "Dependencies downloaded"
}

build_apk() {
    print_header "Building APK ($BUILD_TYPE)"
    
    cd "$SCRIPT_DIR"
    
    if [ "$BUILD_TYPE" = "release" ]; then
        if [ -f "$SCRIPT_DIR/gradlew" ]; then
            ./gradlew assembleRelease
        else
            gradle assembleRelease
        fi
        
        APK_PATH="$SCRIPT_DIR/app/build/outputs/apk/release/app-release-unsigned.apk"
        
        if [ -f "$APK_PATH" ]; then
            print_success "Release APK built successfully!"
            print_info "Location: $APK_PATH"
            
            # Show APK info
            if command -v aapt &> /dev/null; then
                print_info "\nAPK Information:"
                aapt dump badging "$APK_PATH" | grep -E "package:|sdkVersion:|targetSdkVersion:"
            fi
        else
            print_error "APK not found at expected location"
            return 1
        fi
    else
        if [ -f "$SCRIPT_DIR/gradlew" ]; then
            ./gradlew assembleDebug
        else
            gradle assembleDebug
        fi
        
        APK_PATH="$SCRIPT_DIR/app/build/outputs/apk/debug/app-debug.apk"
        
        if [ -f "$APK_PATH" ]; then
            print_success "Debug APK built successfully!"
            print_info "Location: $APK_PATH"
        else
            print_error "APK not found at expected location"
            return 1
        fi
    fi
    
    return 0
}

sign_apk() {
    print_header "Signing APK"
    
    if [ "$BUILD_TYPE" != "release" ]; then
        print_warning "Can only sign release APKs. Skipping..."
        return 0
    fi
    
    APK_PATH="$SCRIPT_DIR/app/build/outputs/apk/release/app-release-unsigned.apk"
    KEYSTORE_PATH="$SCRIPT_DIR/sieben-release-key.keystore"
    SIGNED_APK_PATH="$SCRIPT_DIR/app/build/outputs/apk/release/sieben-v1.9-release.apk"
    
    if [ ! -f "$KEYSTORE_PATH" ]; then
        print_info "Keystore not found. Creating new keystore..."
        print_warning "Please enter keystore information:"
        
        keytool -genkey -v \
            -keystore "$KEYSTORE_PATH" \
            -alias sieben \
            -keyalg RSA \
            -keysize 2048 \
            -validity 10000
    fi
    
    print_info "Signing APK..."
    jarsigner -verbose \
        -sigalg SHA1withRSA \
        -digestalg SHA1 \
        -keystore "$KEYSTORE_PATH" \
        "$APK_PATH" \
        sieben
    
    print_info "Aligning APK..."
    if command -v zipalign &> /dev/null; then
        zipalign -v 4 "$APK_PATH" "$SIGNED_APK_PATH"
        print_success "APK signed and aligned successfully!"
        print_info "Location: $SIGNED_APK_PATH"
    else
        print_warning "zipalign not found. APK is signed but not aligned"
        print_info "Install Android SDK build-tools to use zipalign"
    fi
    
    return 0
}

################################################################################
# Main Script
################################################################################

main() {
    print_header "Sieben APK Build Script"
    
    # Parse command line arguments
    while [[ $# -gt 0 ]]; do
        case $1 in
            --clean)
                CLEAN_BUILD=true
                shift
                ;;
            --release)
                BUILD_TYPE="release"
                shift
                ;;
            --sign)
                SIGN_APK=true
                shift
                ;;
            --install-sdk)
                INSTALL_SDK=true
                shift
                ;;
            --help)
                show_help
                exit 0
                ;;
            *)
                print_error "Unknown option: $1"
                show_help
                exit 1
                ;;
        esac
    done
    
    # Check prerequisites
    check_java || exit 1
    check_android_sdk || exit 1
    check_gradle || exit 1
    
    # Clean if requested
    if [ "$CLEAN_BUILD" = true ]; then
        clean_build || exit 1
    fi
    
    # Download dependencies
    download_dependencies || {
        print_warning "Failed to download some dependencies. Continuing anyway..."
    }
    
    # Build APK
    build_apk || exit 1
    
    # Sign if requested
    if [ "$SIGN_APK" = true ]; then
        sign_apk || exit 1
    fi
    
    print_header "Build Complete!"
    print_success "Your APK is ready for testing or distribution"
    
    if [ "$BUILD_TYPE" = "release" ] && [ "$SIGN_APK" != true ]; then
        print_info "\nNote: Release APK is unsigned. Use --sign to sign it for distribution"
    fi
}

# Run main function
main "$@"
