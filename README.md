# SkillTracker

A personal skill tracking and visualization Android app built with Jetpack Compose.

## Features

- **Skill Management**: Create, edit, and delete skills with custom colors
- **Value Tracking**: Adjust skill values with +1/+5/+10 and -1/-5/-10 controls
- **Growth History**: Every value change is recorded with timestamps
- **Charts**: Bar chart, radar chart, and trend line chart using MPAndroidChart
- **Long-press Info**: Long-press any chart point to see skill details in a dialog
- **Export/Import**: Export all data to JSON (SAF), import from JSON file
- **Dark Mode**: Full Material 3 dark mode support
- **Tablet Layout**: Responsive layout for larger screens
- **Sample Data**: Auto-generated on first launch (Java, System Design, Architecture, English)

## Tech Stack

- Kotlin
- Jetpack Compose (Material 3)
- MVVM Architecture
- Room Database
- Kotlin Coroutines & Flow
- Gson (JSON serialization)
- MPAndroidChart (charts via AndroidView)
- Navigation Compose

## Project Structure

```
app/src/main/java/com/skilltracker/
├── domain/model/          # Skill, SkillHistory data classes
├── domain/repository/     # SkillRepository interface
├── data/database/         # Room entities, DAOs, AppDatabase
├── data/repository/       # SkillRepositoryImpl
├── viewmodel/             # HomeViewModel, DetailViewModel, AddEditViewModel
├── ui/theme/              # Material 3 theme
├── ui/charts/             # BarChart, RadarChart, TrendChart (MPAndroidChart)
├── ui/components/         # ColorPicker, SkillInfoDialog, ValueControls
├── ui/navigation/         # NavGraph
├── ui/home/               # HomeScreen
├── ui/detail/             # DetailScreen
├── ui/addedit/            # AddEditSkillScreen
├── export/                # ExportManager, ImportManager, ExportData
├── SkillTrackerApp.kt     # Application class with seed data
└── MainActivity.kt        # Entry point
```

## Build with GitHub Actions

This project is designed to build entirely via GitHub Actions with no local Android SDK required.

### Automatic Builds

1. Push to `main` or `master` branch triggers a Debug APK build
2. The APK is uploaded as a build artifact
3. Create a release tag (e.g., `git tag v1.0.0 && git push --tags`) to trigger a GitHub Release with the APK attached

### Downloading APK

1. Go to the repository's **Actions** tab
2. Click on the latest workflow run
3. Download the `skilltracker-debug` artifact from the **Artifacts** section
4. For releases, go to the **Releases** page and download the APK directly

## Export & Import

### Export

1. Tap the **Share** icon in the top-right of the home screen
2. Choose a save location in the system file picker
3. A `SkillTracker.json` file will be saved with all your skills

### Import

1. Tap the **File Open** icon in the top-right of the home screen
2. Select a `SkillTracker.json` file
3. All skills from the file will be added to your database

### JSON Format

```json
{
  "version": 1,
  "skills": [
    {
      "id": "1",
      "name": "Java",
      "value": 75,
      "color": 4294946048,
      "strengths": ["OOP", "Collections"],
      "improvements": ["Streams API", "Records"]
    }
  ]
}
```

## Local Development

If you want to build locally:

1. Install JDK 17
2. Install Android SDK (API 34)
3. Generate Gradle wrapper: `gradle wrapper`
4. Build: `./gradlew assembleDebug`

## License

MIT
