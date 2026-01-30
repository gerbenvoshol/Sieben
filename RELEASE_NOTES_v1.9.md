# Sieben v1.9 Release Notes

## Overview
Version 1.9 of the Sieben workout app adds support for three new languages and updates documentation.

## What's New in v1.9

### New Language Support
- **Spanish** (es) - Complete translation of all strings and UI elements
- **Italian** (it) - Complete translation of all strings and UI elements  
- **Portuguese** (pt) - Complete translation of all strings and UI elements

The app now supports a total of 7 languages:
1. English (default)
2. German (de)
3. French (fr)
4. Japanese (ja)
5. Spanish (es) - **NEW**
6. Italian (it) - **NEW**
7. Portuguese (pt) - **NEW**

### Documentation Updates
- Updated README.md to list all supported languages
- Updated CHANGELOG.md with v1.9 release information
- Created BUILD.md with comprehensive build instructions
- Updated all existing translation files with v1.9 changelog information

### Version Information
- Version Name: 1.9
- Version Code: 31 (incremented from 30)
- Target SDK: 25
- Minimum SDK: 17

## Files Changed

### New Files
- `app/src/main/res/values-es/strings.xml` - Spanish translation (218 strings)
- `app/src/main/res/values-it/strings.xml` - Italian translation (218 strings)
- `app/src/main/res/values-pt/strings.xml` - Portuguese translation (218 strings)
- `BUILD.md` - Build instructions
- `RELEASE_NOTES_v1.9.md` - This file

### Modified Files
- `app/build.gradle` - Version bump to 1.9 (versionCode 31)
- `README.md` - Updated language list
- `CHANGELOG.md` - Added v1.9 entry
- `app/src/main/res/values/strings.xml` - Added v1.9 to changelog
- `app/src/main/res/values-de/strings.xml` - Added v1.9 to changelog (German)
- `app/src/main/res/values-fr/strings.xml` - Added v1.9 to changelog (French)
- `app/src/main/res/values-ja/strings.xml` - Added v1.9 to changelog (Japanese)

## Translation Details

All translations include:
- App name localization
- Menu items and settings
- Exercise names and descriptions
- Help dialog text
- Changelog dialog text
- License and about information
- All UI strings and snackbar messages

## Building the Release

To build the APK for this release:

1. Ensure you have Android SDK API 25 installed
2. Run: `./gradlew assembleRelease` or `gradle assembleRelease`
3. The APK will be in `app/build/outputs/apk/release/`
4. Sign the APK before distribution (see BUILD.md for details)

See `BUILD.md` for complete build instructions and troubleshooting.

## Installation

Users can install the app:
1. Download the APK from GitHub Releases
2. Enable "Install from Unknown Sources" on Android
3. Install the APK
4. The app will automatically use the system language if supported, otherwise default to English

## Testing Recommendations

Before final release, test:
- [ ] All 7 language translations display correctly
- [ ] Language switching works properly
- [ ] All exercises and timers function correctly
- [ ] TTS (Text-to-Speech) works in all supported languages
- [ ] Settings are saved correctly
- [ ] App works on Android versions 4.2 (API 17) through latest

## Credits

### Translations
- English: Original by Gaukler Faun
- German: Gaukler Faun
- French: jeberger
- Japanese: naofum  
- Spanish: Automated translation (needs native review)
- Italian: Automated translation (needs native review)
- Portuguese: Automated translation (needs native review)

**Note:** The Spanish, Italian, and Portuguese translations were created programmatically and should be reviewed by native speakers for accuracy and natural phrasing.

## Future Improvements

Potential areas for future enhancement:
- Native speaker review of new translations
- Additional language support (Dutch, Russian, Chinese, etc.)
- Android SDK and dependency updates
- Material Design updates
- Additional exercises or workout variations

## Support

For issues, feature requests, or contributions:
- GitHub: https://github.com/scoute-dich/Sieben
- Issues: https://github.com/scoute-dich/Sieben/issues

---

Released: January 2026
