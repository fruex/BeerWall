# Add project specific ProGuard rules here.
# You can use the syntax argument in the 'proguard' configuration of your build.gradle file.

-keep class com.fruex.beerwall.** { *; }
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes Exceptions
-keepattributes InnerClasses
-keepattributes SourceFile,LineNumberTable

# Compose
-keep class androidx.compose.** { *; }

# Serialization
-keepclassmembers class * {
    @kotlinx.serialization.Serializable <init>(...);
}

# Ktor
-keep class io.ktor.** { *; }
