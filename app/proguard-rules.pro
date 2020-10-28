# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
#移除log代码
-assumenosideeffects class android.util.Log {
    public static *** v(...);
    public static *** i(...);
    public static *** d(...);
    public static *** w(...);
    public static *** e(...);
}
# 保留系统基本组件
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View

-keep public class com.example.autoclick.** {*;}


# 保持support下的所有类及其内部类
-keep class androidx.appcompat.** {*;}

# 保留继承的
-keep public class * extends androidx.annotation.**

# 保持自定义控件
-keep public class * extends android.view.View{
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}



#````````````````````````````````````butterknife

# Retain generated class which implement Unbinder.
-keep public class * implements butterknife.Unbinder { public <init>(**, android.view.View); }

# Prevent obfuscation of types which use ButterKnife annotations since the simple name
# is used to reflectively look up the generated ViewBinding.
-keep class butterknife.*
-keepclasseswithmembernames class * { @butterknife.* <methods>; }
-keepclasseswithmembernames class * { @butterknife.* <fields>; }


#``````````````````````````````````glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule {
 <init>(...);
}
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-keep class com.bumptech.glide.load.data.ParcelFileDescriptorRewinder$InternalRewinder {
  *** rewind();
}


#```````````````````````````````````Umeng
-dontshrink
-dontoptimize
-dontwarn com.google.android.maps.**
-dontwarn android.webkit.WebView
-dontwarn com.umeng.**
-dontwarn com.tencent.weibo.sdk.**
-dontwarn com.facebook.**
-keep public class javax.**
-keep public class android.webkit.**
-dontwarn android.support.v4.**
-keep enum com.facebook.**
-keepattributes Exceptions,InnerClasses,Signature
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable

-keep public interface com.facebook.**
-keep public interface com.tencent.**
-keep public interface com.umeng.socialize.**
-keep public interface com.umeng.socialize.sensor.**
-keep public interface com.umeng.scrshot.**

-keep public class com.umeng.socialize.* {*;}


-keep class com.facebook.**
-keep class com.facebook.** { *; }
-keep class com.umeng.scrshot.**
-keep public class com.tencent.** {*;}
-keep class com.umeng.socialize.sensor.**
-keep class com.umeng.socialize.handler.**
-keep class com.umeng.socialize.handler.*
-keep class com.umeng.weixin.handler.**
-keep class com.umeng.weixin.handler.*
-keep class com.umeng.qq.handler.**
-keep class com.umeng.qq.handler.*
-keep class UMMoreHandler{*;}
-keep class com.tencent.mm.sdk.modelmsg.WXMediaMessage {*;}
-keep class com.tencent.mm.sdk.modelmsg.** implements com.tencent.mm.sdk.modelmsg.WXMediaMessage$IMediaObject {*;}
-keep class im.yixin.sdk.api.YXMessage {*;}
-keep class im.yixin.sdk.api.** implements im.yixin.sdk.api.YXMessage$YXMessageData{*;}
-keep class com.tencent.mm.sdk.** {
   *;
}
-keep class com.tencent.mm.opensdk.** {
   *;
}
-keep class com.tencent.wxop.** {
   *;
}
-keep class com.tencent.mm.sdk.** {
   *;
}

-keep class com.twitter.** { *; }

-keep class com.tencent.** {*;}
-dontwarn com.tencent.**
-keep class com.kakao.** {*;}
-dontwarn com.kakao.**
-keep public class com.umeng.com.umeng.soexample.R$*{
    public static final int *;
}
-keep public class com.linkedin.android.mobilesdk.R$*{
    public static final int *;
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class com.tencent.open.TDialog$*
-keep class com.tencent.open.TDialog$* {*;}
-keep class com.tencent.open.PKDialog
-keep class com.tencent.open.PKDialog {*;}
-keep class com.tencent.open.PKDialog$*
-keep class com.tencent.open.PKDialog$* {*;}
-keep class com.umeng.socialize.impl.ImageImpl {*;}
-keep class com.sina.** {*;}
-dontwarn com.sina.**
-keep class  com.alipay.share.sdk.** {
   *;
}

-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

-keep class com.linkedin.** { *; }
-keep class com.android.dingtalk.share.ddsharemodule.** { *; }
-keepattributes Signature


#````````````````````````````` tencent MMKV

# OSS_ANDROID_TEMPLATE_FILE_HEADER
#
# Proguard config for publish an private aar module
# NOTE: It's different from libraries' consumer proguard config
#
-renamesourcefileattribute SourceFile
-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,EnclosingMethod

# Preserve all annotations.

-keepattributes *Annotation*

# Preserve all public classes, and their public and protected fields and
# methods.

-keep public class * {
    public protected *;
}

# Preserve all .class method names.

-keepclassmembernames class * {
    java.lang.Class class$(java.lang.String);
    java.lang.Class class$(java.lang.String, boolean);
}

# Preserve all native method names and the names of their classes.

-keepclasseswithmembernames class * {
    native <methods>;
}

# Preserve the special static methods that are required in all enumeration
# classes.

-keepclassmembers class * extends java.lang.Enum {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}



-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# ``````````````` liulishuo.okdownload
# https://github.com/square/okhttp/#proguard
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**
# A resource is loaded with a relative path so the package of this class must be preserved.
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase
# ------- end okhttp proguard rules ----
# ------- because of we using com.liulishuo.okdownload:okhttp on sample ----
-keepnames class com.liulishuo.okdownload.core.connection.DownloadOkHttp3Connection
# ------- end com.liulishuo.okdownload:okhttp proguard rules ----
# ------- because of we using com.liulishuo.okdownload:sqlite on sample ----
-keep class com.liulishuo.okdownload.core.breakpoint.BreakpointStoreOnSQLite {
        public com.liulishuo.okdownload.core.breakpoint.DownloadStore createRemitSelf();
        public com.liulishuo.okdownload.core.breakpoint.BreakpointStoreOnSQLite(android.content.Context);
}
# ------- end com.liulishuo.okdownload:sqlite proguard rules ----

# don't warn SuppressFBWarnings annotation used in com.liulishuo.okdownload.DownloadContex.java
-dontwarn edu.umd.cs.findbugs.annotations.SuppressFBWarnings
-dontwarn kotlinx.coroutines.**

#``````````````rxbindbing4
# AutoValue annotations are retained but dependency is compileOnly.
-dontwarn com.google.auto.value.AutoValue

# `````````````` ``````````````eventbus
-keepattributes *Annotation*
-keepclassmembers class * {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# And if you use AsyncExecutor:
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}


#`````````````````````````````````banner
# banner's proguard inclide glide butterknife gsyvideoplayer and exoplayer
# glide and butterknife is already Configured, gsyvideoplayer and exoplayer nerver used.so ignore;

#```````````hackware1993:MagicIndicator 貌似不需要混淆

#````````````````````````````````````SmartRefreshLayout
#SmartRefreshLayout 不需要添加混淆过滤代码，并且已经混淆测试通过，如果你在项目的使用中混淆之后出现问题，请及时通知我。
# https://github.com/scwang90/SmartRefreshLayout/

#`````````````richtext `````````````````don't need proguard


#``````````````````blurry```````````already Configured

#``````````````````````````jiaozivideoplayer  按需添加
-keep public class cn.jzvd.JZMediaSystem {*; }


#````````````````````````circleindicator   No need

#```````````````````````okhttputils
-dontwarn com.zhy.http.**
-keep class com.zhy.http.**{*;}
#okhttp
-dontwarn okhttp3.**
-keep class okhttp3.**{*;}
#okio
-dontwarn okio.**
-keep class okio.**{*;}


#````````````````````````````````````agentweb
-keep class com.just.agentweb.** {
    *;
}
-dontwarn com.just.agentweb.**


 #``````````````````````zzhoujay:html  ````````````````no need


#````````````````balysv:material-ripple   无说明 最近更新四年前。

#````````````````````````  implementation 'com.qianwen:update-app:3.5.2'没找到

#```````````````````````fastjson``````````
-keepattributes Signature
-dontwarn com.alibaba.fastjson.**
-keepattributes Annotation
-keep class com.alibaba.fastjson.**{*; }
-keep public class javax.**


#····················okhttp3 jar包自带

#·········· 'com.orhanobut:logger:1.15'  似乎不用



# Prevent R8 from leaving Data object members always null
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}


#·························io.reactivex:rxjava:+
-dontwarn java.util.concurrent.Flow*

-keep class me.leolin.shortcutbadger.impl.** { <init>(...); }

#com.tonystark.android:socket:2.4.3
-dontwarn com.xuhao.didi.socket.client.**
-dontwarn com.xuhao.didi.socket.common.**
-dontwarn com.xuhao.didi.socket.server.**
-dontwarn com.xuhao.didi.core.**

-keep class com.xuhao.didi.socket.client.** { *; }
-keep class com.xuhao.didi.socket.common.** { *; }
-keep class com.xuhao.didi.socket.server.** { *; }
-keep class com.xuhao.didi.core.** { *; }

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep class com.xuhao.didi.socket.client.sdk.client.OkSocketOptions$* {
    *;
}
-keep class com.xuhao.didi.socket.server.impl.OkServerOptions$* {
    *;
}

-keep class leakcanary.** {*;}

-dontwarn com.squareup.picasso.**


-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}

#极光推送
-dontoptimize
-dontpreverify

-dontwarn cn.jpush.**
-keep class cn.jpush.** { *; }
-dontwarn cn.jiguang.**
-keep class cn.jiguang.** { *; }

-dontwarn cn.com.chinatelecom.**
-keep class cn.com.chinatelecom.** { *; }
-dontwarn com.ct.**
-keep class com.ct.** { *; }
-dontwarn a.a.**
-keep class a.a.** { *; }
-dontwarn com.cmic.**
-keep class com.cmic.** { *; }
-dontwarn com.unicom.**
-keep class com.unicom.** { *; }
-dontwarn com.sdk.**
-keep class com.sdk.** { *; }

-dontwarn com.sdk.**
-keep class com.sdk.** { *; }


-dontwarn com.ledong.lib.**
-keep class com.ledong.lib.** {*;}
-dontwarn com.leto.game.**
-keep class com.leto.game.** {*;}
-dontwarn com.mgc.leto.**
-keep class com.mgc.leto.** {*;}