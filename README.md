![xx](assets/banner.png)
### [English Version](README_EN.md)

# 虚拟引擎 · BlackBox
> The only people who have anything to fear from free software are those whose products are worth even less. 
>
> <p align="right">——David Emery</p>

![](https://img.shields.io/badge/language-java-brightgreen.svg)

黑盒BlackBox，是一款虚拟引擎，可以在Android上克隆、运行虚拟应用，拥有免安装运行能力。黑盒可以掌控被运行的虚拟应用，做任何想做的事情。

## 交流
[Telegram](https://t.me/fvblackbox)

## 支持
暂不考虑4x，目前已兼容 5.0 ～ 12.0并跟进后续新系统。

如果条件允许，降级targetSdkVersion到28或以下可以获得更好的兼容性。

***稳定性未经大量测试，仅供学习交流，请勿用于其他用途***

## 编译版本下载
稳定版与测试版下载
- 稳定版 由管理员手动发布经过验证稳定后的版本。[下载地址](https://github.com/FBlackBox/BlackBox/releases)
- 测试版 由机器自动编译最新的代码的版本，可体验最新体验也有可能存在问题。 [下载地址](https://github.com/AutoBlackBox/BlackBox/tags)

## 架构说明
本项目区分32位与64位，目前是2个不同的app，如在Demo已安装列表内无法找到需要开启的app说明不支持，请编译其他的架构。

## 如何使用
### Step 1.初始化，在Application中加入以下代码初始化

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

### Step 2.安装应用至黑盒内
```java
    // 已安装的应用可以提供包名
    BlackBoxCore.get().installPackageAsUser("com.tencent.mm", userId);
    
    // 未安装的应用可以提供路径
    BlackBoxCore.get().installPackageAsUser(new File("/sdcard/com.tencent.mm.apk"), userId);
```

### Step 2.运行黑盒内的应用
```java
   BlackBoxCore.get().launchApk("com.tencent.mm", userId);
```

### 多开应用操作
<img src="assets/multiw.gif" width="50%">

### 相关API
#### 获取黑盒内已安装的应用
```java
   // flgas与常规获取已安装应用保持一致即可
   BlackBoxCore.get().getInstalledApplications(flags, userId);
   
   BlackBoxCore.get().getInstalledPackages(flags, userId);
```

#### 获取黑盒内的User信息
```java
   List<BUserInfo> users = BlackBoxCore.get().getUsers();
```
更多其他操作看BlackBoxCore函数名大概就知道了。


#### Xposed相关
- 已支持使用XP模块
- Xposed已粗略过检测，[Xposed Checker](https://www.coolapk.com/apk/190247)、[XposedDetector](https://github.com/vvb2060/XposedDetector) 均无法检测


## 如何参与开发？
### 应用分2个模块
- app模块，用户操作与UI模块
- Bcore模块，此模块为BlackBox的核心模块，负责完成整个黑盒的调度。

如需要参与开发请直接pr就可以了，相关教程请Google或者看 [如何在 GitHub 提交第一个 pull request](https://chinese.freecodecamp.org/news/how-to-make-your-first-pull-request-on-github/)
### PR须知
1. 中英文说明都可以，但是一定要详细说明问题
2. 请遵从原项目的代码风格、设计模式，请勿个性化。
3. PR不分大小，有问题随时欢迎提交。

## 计划
 - 更多的Service API 虚拟化（目前许多是使用系统API，只有少数已实现）
 - 提供更多接口给开发者（虚拟定位、应用注入等）

## 赞助
本项目为免费开源项目，日常维护耗费大量精力。如想加快进度或请作者喝杯咖啡。

- BTC: 3FCo9QtaSbGMhmZYzvL4XUoJUUxZeSdha4
- USDT（TRC20）: TDzBj9eV1Cdmmj9xd5Y1YLsQqC8zVgi7yd

## 感谢
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
