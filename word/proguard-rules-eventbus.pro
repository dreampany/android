-keepattributes *Annotation*,EnclosingMethod
-keepclassmembers class ** { @org.greenrobot.eventbus.Subscribe <methods>; }
-keep enum org.greenrobot.eventbus.ThreadMode { *; }
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent { <init>(java.lang.Throwable); }