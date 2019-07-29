### 介绍
聪明的Fragment管理框架。

### 依赖
#### 自身
##### AndroidLibrary - EventbusActivityScope
implementation 'org.greenrobot:eventbus:3.1.1'
##### AndroidLibrary - FragmentationCore
api 'com.github.snpmyn:*Util*:master-SNAPSHOT'
#### com.github.snpmyn:Util(api)
##### AndroidLibrary - UtilOne
* api 'com.github.bumptech.glide:glide:4.9.0'（避重）
* api 'com.google.android.material:material:1.1.0-alpha08'（避重）
* api 'com.jakewharton.timber:timber:4.7.1'（避重）
* implementation 'com.qw:soulpermission:1.1.8'
* implementation 'org.apache.commons:commons-lang3:3.9'

### 权限
#### com.github.snpmyn:Util
##### app
* <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />（避重）
* <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />（避重）
