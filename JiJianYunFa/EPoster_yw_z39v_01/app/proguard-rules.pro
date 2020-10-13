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

-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
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
################

#-keep @com.facebook.common.internal.DoNotStrip class *
#-keepclassmembers class * {
#@com.facebook.common.internal.DoNotStrip *;
#}
####
#混淆时不混淆picasso-2.5.2.jar
#-libraryjars libs/glide-3.7.0.jar
-dontwarn com.bumptech.glide.**  
-keep class com.bumptech.glide.** { *; } 

-dontwarn fakeawt.**
-keep class fakeawt.** { *; }

-dontwarn magick.**
-keep class magick.** { *; }

#-libraryjars libs/guard_service1.0.jar #混淆时不混淆guard_service1.0.jar
#-libraryjars libs/ApiStoreSDK1.0.4.jar
#-libraryjars libs/universal-image-loader-1.9.2.jar #混淆时不混淆universal-image-loader.jar
-dontwarn android.support.**

#混淆时不混淆download_bo_0.2.jar
#-libraryjars libs/download_bo_0.2.jar
-dontwarn com.lzkj.downloadservice.bean.**
-keep class com.lzkj.downloadservice.bean.** {*;}


#混淆时不混淆picasso-2.5.2.jar
#-libraryjars libs/picasso-2.5.2.jar
#-dontwarn com.squareup.picasso.**  
#-keep class com.squareup.picasso.** { *; } 

#混淆时不混淆locSDK_6.13.jar
#-libraryjars libs/locSDK_6.13.jar
-dontwarn com.baidu.location.**  
-keep class com.baidu.location.** { *; } 

#混淆时不混淆adpress-core.jar
#-libraryjars libs/adpress-core-0.0.3-yw-SNAPSHOT.jar
-dontwarn com.baize.adpress.core.**
-keep class com.baize.adpress.core.** {*;}

#混淆时不混淆yqhd-log-socket-1.0.jar
#-libraryjars libs/yqhd-log-socket-1.0.jar
-dontwarn com.yqhd.socket.**
-keep class com.yqhd.socket.** {*;}

#混淆时不混淆tanzhen.jar
-dontwarn cn.doogi.testtanzhen2.**
-keep class cn.doogi.testtanzhen2.** {*;}

#fastjson不混淆
#-libraryjars libs/fastjson-1.1.36.jar
-dontwarn com.alibaba.fastjson.**
-dontskipnonpubliclibraryclassmembers
-dontskipnonpubliclibraryclasses
-keep class com.alibaba.fastjson.** { *; }

-keepclassmembers class * {
public <methods>;
}

-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
    public <fields>;
}

-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,LocalVariable*Table,*Annotation*,Synthetic,EnclosingMethod