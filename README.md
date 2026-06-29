# 图书收藏 (BookCollector)

一款基于 Jetpack Compose + Room + MVVM 架构的 Android 图书管理应用。

## 功能特性

- 📚 **图书收藏**：添加、删除、管理个人图书收藏
- 🔍 **智能搜索**：支持按书名、作者搜索本地图书
- 📝 **阅读状态**：标记想读、在读、已读状态
- ⭐ **评分功能**：为图书打分（1-5 星）
- 🗒️ **笔记记录**：为每本书添加个人笔记
- 🌐 **在线搜索**：接入 OpenLibrary API 搜索图书信息

## 技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| Kotlin | 2.1.0 | 开发语言 |
| Jetpack Compose | 2024.02.00 | UI 框架 |
| Room | 2.6.1 | 本地数据库 |
| Retrofit | 2.9.0 | 网络请求 |
| DataStore | 1.1.1 | 轻量存储 |
| Coil | 2.5.0 | 图片加载 |

## 运行环境

- Android Studio Hedgehog 或更高版本
- JDK 17
- Android SDK API 26+ (Android 8.0)

## 截图预览

![首页](./screenshots/collect.png)
![详情页](./screenshots/detail.png)

## 项目结构

```
app/src/main/java/com/example/finalproject/
├── data/           # 数据层（数据库、网络、仓库）
├── model/          # 数据模型
├── navigation/     # 导航配置
├── ui/             # UI 层（页面、组件、主题）
├── viewmodel/      # 视图模型
└── MainActivity.kt # 应用入口
```

## 作者

- **姓名**：吴仪
- **学号**：2025003016

## 许可证

本项目仅供学习交流使用。
