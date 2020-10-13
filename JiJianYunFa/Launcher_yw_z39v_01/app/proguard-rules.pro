-ignorewarnings                     # 忽略警告，避免打包时某些警告出现
-optimizationpasses 5               # 指定代码的压缩级别
-dontusemixedcaseclassnames         # 是否使用大小写混合
-dontskipnonpubliclibraryclasses    # 是否混淆第三方jar
-dontskipnonpubliclibraryclassmembers
-dontoptimize
-dontwarn
-dontpreverify                      # 混淆时是否做预校验
-verbose                            # 混淆时是否记录日志
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*        # 混淆时所采用的算法
-dontwarn com.lzkj.aidl.**
-keep class com.lzkj.aidl.** { *; }

-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.support.v4.**
-keep public class com.android.vending.licensing.ILicensingService

# For native methods, see http://proguard.sourceforge.net/manual/examples.html#native
-keepclasseswithmembernames class * {
    native <methods>;
}

# keep setters in Views so that animations can still work.
# see http://proguard.sourceforge.net/manual/examples.html#beans
-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
}

# We want to keep methods in Activity that could be used in the XML attribute onClick
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

# For enumeration classes, see http://proguard.sourceforge.net/manual/examples.html#enumerations
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keepclassmembers class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator CREATOR;
}

-keepclassmembers class **.R$* {
    public static <fields>;
}

###############################################################
#第三方的jar不混淆
###############################################################
#-libraryjars libs/android-support-v4.jar #混淆时不混淆android-support-v4.jar
-dontwarn android.support.**

#混淆时不混淆adpress-core.jar
#-libraryjars libs/iadpress-core-0.0.1-20160630.034415-11.jar
-dontwarn com.baize.adpress.core.**
-keep class com.baize.adpress.core.** {*;}

#-libraryjars libs/core.jar
-dontwarn com.google.zxing.**
-keep class com.google.zxing.** { *; }

#-libraryjars libs/mall_client_core.jar #混淆时不混淆mall_client_core.jar
-dontwarn com.lzmr.client.core.**
-keep class com.lzmr.client.core.** { *; }

#混淆时不混淆baize-android-utils1.0.6.jar
#-libraryjars libs/baize-android-utils1.0.6.jar
-dontwarn com.lzkj.baize_android.utils.**
-keep class com.lzkj.baize_android.utils.** {*;}

-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,LocalVariable*Table,*Annotation*,Synthetic,EnclosingMethod
