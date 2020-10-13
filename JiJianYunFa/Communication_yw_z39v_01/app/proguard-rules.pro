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


#########################################
# 第三方jar包不混淆
#########################################
#不混淆bo
-dontwarn com.lzkj.aidlservice.bo.**
-keep class com.lzkj.aidlservice.bo.** { *; }

#-libraryjars libs/android-support-v4.jar
#-dontwarn android.support.**
#-keep class android.support.** { *; }

#-dontwarn android.app.smdt.**
#-keep class android.app.smdt.** { *; }

#-libraryjars libs/smdt_api.jar
#-dontwarn android.app.smdt.**
#-keep class android.app.smdt.** { *; }

#混淆时不混淆adpress-core.jar
#-libraryjars libs/adpress-core-0.0.3-yw-SNAPSHOT.jar
#-libraryjars libs/iadpress-core-0.0.1-20160830.092317-15.jar
-dontwarn com.baize.adpress.core.**
-keep class com.baize.adpress.core.** { *; }

#混淆时不混淆commons-codec-1.9.jar
#-libraryjars libs/commons-codec-1.9.jar
-dontwarn com.apache.commons.codec.**  
-keep class com.apache.commons.codec.** { *; }

#混淆时不混淆commons-lang-2.6.jar
#-libraryjars libs/commons-lang-2.6.jar
-dontwarn com.apache.commons.lang.**  
-keep class com.apache.commons.lang.** { *; }

#混淆时不混淆commons-pool-1.5.5.jar
#-libraryjars libs/commons-pool-1.5.5.jar
-dontwarn com.apache.commons.pool.**  
-keep class com.apache.commons.pool.** { *; }

#混淆时不混淆download_bo_0.2.jar
#-libraryjars libs/download_bo_0.2.jar
-dontwarn com.lzkj.downloadservice.bean.**
-keep class com.lzkj.downloadservice.bean.** {*;}

#fastjson不混淆
#-libraryjars libs/fastjson-1.1.36.jar
-dontwarn com.alibaba.fastjson.**
-dontskipnonpubliclibraryclassmembers
-dontskipnonpubliclibraryclasses
-keep class com.alibaba.fastjson.** { *; }

#混淆时不混淆GetuiExt-2.0.3.jar
#-libraryjars libs/GetuiExt-2.0.3.jar
#-dontwarn com.igexin.getuiext.**  
#-keep class assets.** { *; }
#-keep class com.igexin.getuiext.** { *; }

#混淆时不混淆GetuiSdk2.4.1.0.jar
#-libraryjars libs/GetuiSdk2.4.1.0.jar
-dontwarn com.igexin.**  
#-keep class assets.** { *; }
-keep class com.igexin.** { *; }

#混淆时不混淆gson-2.2.4.jar#######################
#-libraryjars libs/gson-2.2.4.jar
#-dontwarn com.google.gson.**  
#-keep class com.google.gson.** { *; }
#############################################
#混淆时不混淆guard_service1.0.jar
#-libraryjars libs/guard_service1.0.jar
-dontwarn com.lzkj.guardservice.**  
-keep class com.lzkj.guardservice.** { *; }

#混淆时不混淆jpush-sdk-release1.7.4.jar
#-libraryjars libs/jpush-sdk-release1.7.4.jar
-dontwarn cn.jpush.android.**  
-keep class assets.** { *; }
-keep class cn.jpush.android.** { *; }

#混淆时不混淆locSDK_6.13.jar
#-libraryjars libs/locSDK_6.13.jar
-dontwarn com.baidu.location.**  
-keep class com.baidu.location.** { *; } 

#混淆时不混淆lz-core-0.0.1.jar
#-libraryjars libs/lz-core-0.0.1.jar
-dontwarn com.baize.lz.core.**  
-keep class com.baize.lz.core.** { *; }

#混淆时不混淆mall_client_core.jar
#-libraryjars libs/mall_client_core.jar
-dontwarn com.lzmr.client.core.**  
-keep class com.lzmr.client.core.** { *; }

#混淆时不混淆mallapp-core-0.0.1.jar
#-libraryjars libs/mallapp-core-0.0.1.jar
-dontwarn com.baize.mallapp.core.**  
-keep class com.baize.mallapp.core.** { *; }

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
