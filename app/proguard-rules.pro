# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/Pyxis/Library/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
-keep,allowobfuscation @interface com.facebook.common.internal.DoNotStrip
# Do not strip any method/class that is annotated with @DoNotStrip
-keep @com.facebook.common.internal.DoNotStrip class *
-keepclassmembers class * {
    @com.facebook.common.internal.DoNotStrip *;
}
# Keep native methods
-keepclassmembers class * {
    native <methods>;
}
-dontwarn okio.**
-dontwarn com.squareup.okhttp.**
-dontwarn javax.annotation.**
-dontwarn com.android.volley.toolbox.**
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
}
-dontwarn twitter4j.**
-dontwarn com.fasterxml.**
-keep class twitter4j.conf.PropertyConfigurationFactory
-keep class twitter4j.** { *; }
-dontwarn com.squareup.picasso.**
-keepnames class com.fasterxml.jackson.** { *; }
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }
-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}
-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}
-keep class org.eclipse.mat.** { *; }
-keep class com.squareup.leakcanary.** { *; }
-dontwarn sun.misc.Unsafe
-keep class com.sun.**
-keep class org.nustaq.**
-keep class org.objenesis.**
-keep class sun.misc.**
-keep class sun.reflect.**
-keep class sun.nio.**
-dontwarn com.sun.**
-dontwarn org.nustaq.**
-dontwarn org.objenesis.**
-dontwarn sun.misc.**
-dontwarn sun.reflect.**
-dontwarn sun.nio.**
-dontwarn javassist.util.Hotswapper
-dontwarn java.applet.Applet
-dontwarn javassist.tools.rmi.**
-dontwarn com.sun.jdi.**
-dontwarn com.google.android.gms.**
-dontwarn org.apache.http.**
-keep class org.lucasr.twowayview.** { *; }
-keepattributes *Annotation*
-keepclassmembers class ** {
    @com.squareup.otto.Subscribe public *;
    @com.squareup.otto.Produce public *;
}
# Fabric Proguard
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.Exception
-keepnames class * implements java.io.Serializable
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
-keep class moe.palette.twitter2.module.narae.**
-keep class org.nustaq.serialization.**
# Needed to keep generic types and @Key annotations accessed via reflection
-keepattributes Signature,RuntimeVisibleAnnotations,AnnotationDefault
-keepclasseswithmembers class * {
  @com.google.api.client.util.Key <fields>;
}
-keepclasseswithmembers class * {
  @com.google.api.client.util.Value <fields>;
}
-keepnames class com.google.api.client.http.HttpTransport
# Needed by google-http-client-android when linking against an older platform version
-dontwarn com.google.api.client.extensions.android.**
# Needed by google-api-client-android when linking against an older platform version
-dontwarn com.google.api.client.googleapis.extensions.android.**
# Do not obfuscate but allow shrinking of android-oauth-client
-keepnames class com.wuman.android.auth.** { *; }