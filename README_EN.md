![xx](assets/banner.png)
# Virtual Engine · BlackBox
> The only people who have anything to fear from free software are those whose products are worth even less. 
>
> <p align="right">——David Emery</p>


![](https://img.shields.io/badge/language-java-brightgreen.svg)

BlackBox is a virtual engine, it can clone and run virtual application on Android,  users don't have to install APK file to run the application on devices. BlackBox control all virtual applications, so you can do anything you want by using BlackBox.

## Discussion
[Telegram](https://t.me/fvblackbox)

## Support
Currently we don't consider supporting Android 4.x, it supports Android 5.0 ～ 12.0.

## Special Instruction

This project distinguishes between 32-bit and 64-bit, it was compiled to 2 different demos. If you can not find your target in application list, please use another architecture.

## Build
**Please make project before run application, otherwise it will crash when you clone app.**

## Usage
### Step 1.Add initialized code in the Application

```java
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        try {
            BlackBoxCore.get().doAttachBaseContext(base, new ClientConfiguration() {
                @Override
                public String getHostPackageName() {
                    return base.getPackageName();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        BlackBoxCore.get().doCreate();
    }
```

### Step 2.Install application
```java
    // Use package name to install provided that application has been install on device
    BlackBoxCore.get().installPackageAsUser("com.tencent.mm", userId);
    
    // Use APK path to install provided that application has not been install on device
    BlackBoxCore.get().installPackageAsUser(new File("/sdcard/com.tencent.mm.apk"), userId);
```

### Step 3.Launch application
```java
   BlackBoxCore.get().launchApk("com.tencent.mm", userId);
```

### App Cloner
<img src="assets/multi.gif" width="50%">

### API
#### Get application list that were installed in BlackBox
```java
   // flags can refer to the Android develop documentation
   BlackBoxCore.get().getInstalledApplications(flags, userId);
   
   BlackBoxCore.get().getInstalledPackages(flags, userId);
```

#### Get user information in BlackBox
```java
   List<BUserInfo> users = BlackBoxCore.get().getUsers();
```
If you want to perform more operations, please refer to the source code.


#### Xposed Support
- BlackBox has supported Xposed Modules.
- BlackBox has hidden Xposed Framework, [Xposed Checker](https://www.coolapk.com/apk/190247) and [XposedDetector](https://github.com/vvb2060/XposedDetector) can't detect it.


## How to contribute to this project 
### This project is divided into two modules
- app module, it is used to achieve UI and deal with user action.
- Bcore module, this module is the core of BlackBox, it is used to achieve all functionalities.

You can contribute to this project by making pull requests.
### About pull requests
1. Both Chinese and English commit message / comment are ok,  but you should elaborate on your code.
2. Please follow the code style and design pattern of this project.
3. Welcome everybody take part in this project.

## Plan
 - More Service API virtualization.
 - Provide more interfaces for developers (virtual location,  process injection, etc).

## Credits
- [VirtualApp](https://github.com/asLody/VirtualApp)
- [VirtualAPK](https://github.com/didi/VirtualAPK)
- [BlackReflection](https://github.com/CodingGay/BlackReflection)
- [FreeReflection](https://github.com/tiann/FreeReflection)
- [Pine](https://github.com/canyie/pine)

### License

> ```
> Copyright 2022 BlackBox
>
> Licensed under the Apache License, Version 2.0 (the "License");
> you may not use this file except in compliance with the License.
> You may obtain a copy of the License at
>
>    http://www.apache.org/licenses/LICENSE-2.0
>
> Unless required by applicable law or agreed to in writing, software
> distributed under the License is distributed on an "AS IS" BASIS,
> WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
> See the License for the specific language governing permissions and
> limitations under the License.
> ```
