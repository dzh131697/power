# MPAndroidChart
-keep class com.github.mikephil.charting.** { *; }

# Gson
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.skilltracker.export.** { *; }
-keep class com.skilltracker.domain.model.** { *; }

# Room
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**
