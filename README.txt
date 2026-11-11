WarGame - LibGDX Android starter project

Modules:
- app: Android launcher and Android packaging
- core: shared LibGDX game code

Versions:
- Gradle Wrapper: 8.7
- Android Gradle Plugin: 8.6.1
- compileSdk: 34
- Java: 17
- LibGDX: 1.13.1

IMPORTANT - AndroidIDE wrapper files:
This archive includes gradle/wrapper/gradle-wrapper.properties, but not the
binary gradle-wrapper.jar or gradlew launch scripts. To preserve the known
working AndroidIDE setup, copy these three files from your already-working
WarGame-AndroidIDE project into this project:
- gradlew
- gradlew.bat
- gradle/wrapper/gradle-wrapper.jar

The project uses AndroidIDE's AAPT2 override in gradle.properties. If your
AndroidIDE installation uses a different path, update that property or pass:
-Pandroid.aapt2FromMavenOverride=$HOME/.androidide/aapt2

Build from the project root:
bash gradlew -Pandroid.aapt2FromMavenOverride=$HOME/.androidide/aapt2 assembleDebug

APK:
app/build/outputs/apk/debug/app-debug.apk
