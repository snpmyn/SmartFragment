<div align=center><img src="https://github.com/snpmyn/SmartFragment/raw/master/image.png"/></div>

[![SNAPSHOT](https://jitpack.io/v/Jaouan/Revealator.svg)](https://jitpack.io/#snpmyn/SmartFragment)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/0cff515e44f14f8dafc26d1ed483b7fa)](https://www.codacy.com/manual/snpmyn/SmartFragment?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=snpmyn/SmartFragment&amp;utm_campaign=Badge_Grade)
[![GitHub license](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat)](https://www.apache.org/licenses/LICENSE-2.0)
[![API](https://img.shields.io/badge/API-19%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=19)

[![GitHub stars](https://img.shields.io/github/stars/Bigkoo/SmartFragment.svg?style=social)](https://github.com/Bigkoo/SmartFragment/stargazers) 
[![GitHub forks](https://img.shields.io/github/forks/Bigkoo/SmartFragment.svg?style=social)](https://github.com/Bigkoo/SmartFragment/network) 
[![GitHub watchers](https://img.shields.io/github/watchers/Bigkoo/SmartFragment.svg?style=social)](https://github.com/Bigkoo/SmartFragment/watchers)

### 介绍
聪明的碎片管理框架。

### 架构

| 模块 | 说明 | 补充 |
|:-:|:-:|:-:|
| 示例app | 用法举例 | 无 |
| 一方库FragmentationCore | 核心功能实现 | 无 |
| 一方库Fragmentation | 基类实现 | 无 |
| 一方库EventbusActivityScope | 传值功能实现 | 无 |

### 依赖、权限

| 模块 | 依赖 |
|:-:|:-:|
| 示例app | implementation project(path: ':fragmentation') |
| 示例app | implementation project(path: ':eventbusactivityscope') |
| 一方库FragmentationCore | api 'com.github.snpmyn.Util:*utilone*:v1.0.1'（避重）|
| 一方库Fragmentation | api project(path: ':fragmentationcore') |
| 一方库EventbusActivityScope | implementation 'com.github.snpmyn.Util:*utilone*:v1.0.1' |
| 一方库EventbusActivityScope | implementation 'org.greenrobot:eventbus:3.1.1' |
| 二方库Util-示例app | implementation project(path: ':utilone') |
| 二方库Util-示例app | implementation project(path: ':utiltwo') |
| 二方库Util-UtilOne | api 'com.github.bumptech.glide:glide:4.10.0'（避重）|
| 二方库Util-UtilOne | api 'com.google.android.material:material:1.2.0-alpha01'（避重）|
| 二方库Util-UtilOne | api 'io.reactivex:rxandroid:1.2.1'（避重）|
| 二方库Util-UtilOne | api 'io.reactivex:rxjava:1.3.8'（避重）|
| 二方库Util-UtilOne | api 'com.jakewharton.timber:timber:4.7.1'（避重）|
| 二方库Util-UtilOne | api 'com.tencent:mmkv-static:1.0.23'（避重）|
| 二方库Util-UtilOne | implementation 'com.getkeepsafe.relinker:relinker:1.3.1' |
| 二方库Util-UtilOne | implementation 'com.qw:soulpermission:1.2.2_x' |
| 二方库Util-UtilOne | implementation 'org.apache.commons:commons-lang3:3.9' |
| 二方库Util-UtilTwo | implementation 'androidx.core:core-ktx:1.2.0-beta02' |
| 二方库Util-UtilTwo | implementation "org.jetbrains.kotlin:*kotlin-stdlib-jdk7*:$kotlin_version" |

| 模块 | 权限 |
|:-:|:-:|
| 示例app | 无 |
| 一方库FragmentationCore | 无 |
| 一方库Fragmentation | 无 |
| 一方库EventbusActivityScope | 无 |
| 二方库Util-app | android:name="android.permission.WRITE_EXTERNAL_STORAGE"（避重）|
| 二方库Util-app | android:name="android.permission.READ_EXTERNAL_STORAGE"（避重）|
| 二方库UtilOne | 无 |
| 二方库UtilTwo | 无 |

### 使用
> [SECURITY](https://github.com/snpmyn/SmartFragment/blob/master/SECURITY.md)<br>
> 版本快速迭代中，拉取失败暂时查看源码。

build.gradle(module)
```
// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    repositories {
        google()
        jcenter()

    }
    dependencies {
        classpath 'com.android.tools.build:gradle:3.5.2'

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        maven { url "https://jitpack.io" }
    }
}

task clean(type: Delete) {
    delete rootProject.buildDir
}
```
build.gradle(app)
```
apply plugin: 'com.android.application'

android {
    ...
    defaultConfig {
        ...      
    }       
    compileOptions {
        sourceCompatibility 1.8
        targetCompatibility 1.8
    }
    configurations.all {
        resolutionStrategy.cacheChangingModulesFor 0, 'seconds'
    }       
}

dependencies {
    implementation 'com.github.snpmyn.SmartFragment:fragmentation:v1.0.1'    
    implementation 'com.github.snpmyn.SmartFragment:eventbusactivityscope:v1.0.1'    
}
```

### License
```
Copyright 2019 snpmyn

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
