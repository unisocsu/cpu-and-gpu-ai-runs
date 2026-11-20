# This file is used to configure ProGuard for code optimization and obfuscation.
# For this simple game, we don't need to keep any specific classes.
-keepattributes *Annotation*
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}
