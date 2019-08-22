# SocialCarnival

[![](https://jitpack.io/v/iamdennisme/SocialCarnival.svg)](https://jitpack.io/#iamdennisme/SocialCarnival)

social lib  with rx,just soupport wechat and qq

## Download

- Add it in your root build.gradle at the end of repositories:

```
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```

- Add the social carnival dependency

```
dependencies {
   implementation 'com.github.iamdennisme:SocialCarnival:x.y.z'
}
```

* since 1.0.0，i removed social sdk in lib. you should add social sdk in your project
example
```
dependencies {
       implementation 'com.tencent.mm.opensdk:wechat-sdk-android-without-mta:5.3.1'
}
```

## Usage

### Init

```Kotlin
class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
         SocialCarnival.init(mApplication)
         SocialCarnival.get.socialConfig.qqAppId = "your app id"
         SocialCarnival.get.socialConfig.wechatAppId =  "your app id"
    }
}
```
#### if you use wechat,do this
- modify manifest
```xml
    <activity
                android:name="com.dennisce.socialcarnival.wechat.WXEntryActivity"
                android:configChanges="orientation|keyboardHidden|navigation|screenSize"
                android:exported="true"
                android:launchMode="singleInstance"
                android:theme="@android:style/Theme.Translucent.NoTitleBar"/>

        <activity-alias
                android:name=".wxapi.WXEntryActivity"
                android:exported="true"
                android:screenOrientation="portrait"
                android:targetActivity="com.dennisce.socialcarnival.wechat.WXEntryActivity"
                android:theme="@android:style/Theme.Translucent.NoTitleBar"/> 

         <activity-alias
                    android:name=".wxapi.WXPayEntryActivity"
                    android:exported="true"
                    android:screenOrientation="portrait"
                    android:targetActivity="com.dennisce.socialcarnival.wechat.WXEntryActivity"
                    android:theme="@android:style/Theme.Translucent.NoTitleBar" />
```
#### if you use qq,do this
- modify manifest
```xml
    <activity
                android:name="com.tencent.tauth.AuthActivity"
                android:noHistory="true"
                android:launchMode="singleTask" >
            <intent-filter>
                <action android:name="android.intent.action.VIEW" />
                <category android:name="android.intent.category.DEFAULT" />
                <category android:name="android.intent.category.BROWSABLE" />
                <data android:scheme="tencentYourId" />
            </intent-filter>
        </activity>
        <activity
                android:name="com.tencent.connect.common.AssistActivity"
                android:configChanges="orientation|keyboardHidden"
                android:screenOrientation="behind"
                android:theme="@android:style/Theme.Translucent.NoTitleBar" />
```
- modify your activity
```kotlin
override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        SocialCarnival.get.setActivityResult(requestCode, resultCode, data)
    }
```

### use
- login
```kotlin
     SocialCarnival.get.authorize(act,SocialAuthorizeType.WECHAT).subscribe({
                //success onNext a map of code(wechat)
                //onNext a map of qq info(qq)
            },{
                //fail
            })
```
- share 
```kotlin
         val res= BitmapFactory.decodeResource(act.resources, R.mipmap.ic_launcher)
            SocialCarnival.get.share(act,
                SocialShareType.QQ, ImageShareMedia(res)
            ).subscribe({
              //success 
            },{
              //fail
            })
```

### proguard-rules
```
#qq & weixin
-dontwarn  com.tencent.**
-keep class com.tencent.** {*;}
```

### other
- implement ShareMedia classes for sharing
- Please refer to the implementation of logic QQHandler or WechatHandler

