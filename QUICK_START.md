# Sieben v1.9 - Quick Start Guide

## For Users

### Download & Install
1. Download the APK from the [Releases](https://github.com/gerbenvoshol/Sieben/releases) page
2. Enable "Install from Unknown Sources" on your Android device
3. Install the APK
4. Choose your language in system settings (app will auto-detect)

### Supported Languages (9)
🇬🇧 English | 🇩🇪 German | 🇫🇷 French | 🇯🇵 Japanese  
🇪🇸 Spanish | 🇮🇹 Italian | 🇵🇹 Portuguese | 🇳🇱 Dutch | 🇷🇺 Russian

---

## For Developers

### Quick Build (Automated)
```bash
# Clone the repository
git clone https://github.com/gerbenvoshol/Sieben.git
cd Sieben

# Build release APK
chmod +x build-apk.sh
./build-apk.sh --release
```

The APK will be at: `app/build/outputs/apk/release/app-release-unsigned.apk`

### Build Options
```bash
./build-apk.sh              # Debug APK
./build-apk.sh --release    # Release APK
./build-apk.sh --release --sign  # Signed APK
./build-apk.sh --clean --release # Clean build
./build-apk.sh --help       # Show all options
```

### Requirements
- Java JDK 8+
- Android SDK (will guide installation)
- Internet connection (first build only)

---

## Features

### Workout
- 12 exercises in 7 minutes
- Customizable exercise duration (30/45/60 seconds)
- Voice guidance (TTS)
- Pause/resume support
- Swipe gestures for navigation

### Technical
- Supports Android 4.2+ (API 17+)
- Lightweight (<2MB)
- No ads, no tracking
- Open source (GPL3)
- Offline-capable

---

## Documentation

- **BUILD.md** - Detailed build instructions
- **BUILD_SCRIPT_DOCS.md** - Build script documentation
- **FINAL_SUMMARY.md** - Complete project summary
- **RELEASE_NOTES_v1.9.md** - Version 1.9 release notes

---

## Support

- **Issues:** https://github.com/gerbenvoshol/Sieben/issues
- **F-Droid:** https://f-droid.org/repository/browse/?fdid=de.baumann.sieben

---

## License

GNU General Public License v3.0 - See LICENSE.md

---

**Made with ❤️ by the Sieben community**
