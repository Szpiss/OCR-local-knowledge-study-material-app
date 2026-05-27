# 基于 OCR 与本地知识库的学习资料整理 App

本仓库是移动应用开发大作业 **“智学扫描：基于 ML Kit 与 SQLite 的校园学习资料识别与错题管理 App”** 的第一阶段基础框架版本。

第一阶段重点是搭建可运行的 Android 项目骨架，完成页面导航、本地数据库、模拟 OCR 录入、知识库管理、错题复习中心和用户设置等基础功能。真实 OCR、CameraX 拍照和 WorkManager 定时提醒已预留接口，后续阶段可继续接入。

## 当前阶段完成内容

- 使用 Android Studio 标准项目结构。
- 使用 Java 开发，UI 使用 XML 布局。
- 使用 Material Design 风格组件。
- 使用 `Activity + Fragment` 架构。
- 使用 `BottomNavigationView` 实现首页、知识库、扫描、错题本、我的五个入口。
- 使用 `RecyclerView` 展示学习资料和错题复习列表。
- 使用 `SQLiteOpenHelper` 实现本地数据库持久化。
- 使用 `SharedPreferences` 保存默认科目、提醒开关和提醒时间。
- 实现模拟 OCR 录入资料。
- 实现资料查看、编辑、删除、标记错题。
- 实现知识库搜索、科目筛选、标签筛选、来源类型筛选。
- 实现错题复习中心：错题统计、科目筛选、难度筛选、掌握状态筛选、复习一次、标记已掌握、取消错题。
- 预留 ML Kit OCR、CameraX、WorkManager、BroadcastReceiver 相关接口。

## 项目结构

```text
com.example.smartscan
├── ui
│   ├── activity
│   │   ├── MainActivity.java
│   │   ├── ScanActivity.java
│   │   └── MaterialDetailActivity.java
│   ├── fragment
│   │   ├── HomeFragment.java
│   │   ├── KnowledgeFragment.java
│   │   ├── WrongBookFragment.java
│   │   └── ProfileFragment.java
│   └── adapter
│       ├── MaterialAdapter.java
│       ├── KnowledgeMaterialAdapter.java
│       └── WrongReviewAdapter.java
├── data
│   ├── entity
│   │   ├── StudyMaterial.java
│   │   ├── Statistics.java
│   │   └── WrongStatistics.java
│   ├── db
│   │   └── DBHelper.java
│   └── repository
│       └── StudyMaterialRepository.java
├── ocr
│   └── OcrManager.java
├── worker
│   ├── ReviewReminderWorker.java
│   └── ReminderScheduler.java
├── receiver
│   └── BootReceiver.java
└── util
    ├── AppPreferenceManager.java
    ├── DateUtils.java
    └── SubjectUtils.java
```

## 核心模块说明

### 首页

`HomeFragment` 展示总资料数、错题数、今日新增、复习次数等统计卡片，并展示最近扫描资料列表。

### 扫描录入

`ScanActivity` 当前使用模拟 OCR 文本，不接入真实相机和图片识别。用户可以填写标题、科目、来源类型、标签、错题状态、错误原因和备注，并保存到本地 SQLite 数据库。

### 本地知识库

`KnowledgeFragment` 定位为全部学习资料的本地知识库，展示所有 `StudyMaterial` 数据。它偏向资料管理，支持：

- 关键词搜索
- 科目筛选
- 标签筛选
- 来源类型筛选
- 查看详情
- 将普通资料标记为错题

### 错题复习中心

`WrongBookFragment` 不是独立数据源，而是基于 `StudyMaterial` 表中 `isWrongQuestion = 1` 筛选出的复习视图。它偏向复习训练，支持：

- 错题总数、待复习数、已掌握数统计
- 按科目筛选
- 按难度筛选
- 按掌握状态筛选
- 复习一次并更新 `reviewCount` 和 `lastReviewTime`
- 标记已掌握
- 二次确认后取消错题标记

### 资料详情

`MaterialDetailActivity` 支持查看、编辑、删除资料。若资料被标记为错题，会显示难度、错误原因、复习次数、上次复习时间和掌握状态等错题字段。

### 设置

`ProfileFragment` 使用 `SharedPreferences` 保存默认科目、每日复习提醒开关和提醒时间，并提供清空本地数据入口。

## 数据库设计

数据库使用 `SQLiteOpenHelper`，核心表为 `StudyMaterial`。主要字段包括：

| 字段 | 说明 |
|---|---|
| `id` | 主键，自增 |
| `title` | 资料标题 |
| `content` | OCR 识别文本 |
| `subject` | 所属科目 |
| `sourceType` | 来源类型 |
| `tags` | 标签 |
| `imagePath` | 图片路径，第一阶段预留 |
| `isWrongQuestion` | 是否错题，0 否，1 是 |
| `difficulty` | 难度等级 |
| `note` | 用户备注 |
| `wrongReason` | 错误原因 |
| `mastered` | 是否已掌握 |
| `nextReviewTime` | 下次复习时间，预留 |
| `createTime` | 创建时间 |
| `updateTime` | 更新时间 |
| `reviewCount` | 复习次数 |
| `lastReviewTime` | 上次复习时间 |

数据库版本当前为 `2`，已在 `onUpgrade()` 中通过 `ALTER TABLE` 增量添加错题复习字段，避免已有数据库升级时崩溃。

## 预留扩展点

- `OcrManager`：后续接入 Google ML Kit Text Recognition。
- `ScanActivity`：后续接入系统相册、系统相机或 CameraX。
- `ReviewReminderWorker`：后续接入 WorkManager 实现每日复习提醒。
- `ReminderScheduler`：后续统一管理提醒任务调度。
- `BootReceiver`：后续用于开机后恢复复习提醒。

## 运行方式

1. 使用 Android Studio 打开本仓库。
2. 等待 Gradle 同步完成。
3. 选择 `app` 模块运行到模拟器或真机。

也可以在命令行编译：

```bash
./gradlew :app:assembleDebug
```

编译成功后，Debug APK 位于：

```text
app/build/outputs/apk/debug/app-debug.apk
```

## 文档

一阶段 PRD 已放在：

```text
docs/PRD_智学扫描一阶段基础框架.md
```

## 当前版本说明

当前版本是课程作业第一阶段基础框架版，目标是保证项目结构清晰、页面可运行、课程考察点明确。真实 OCR、拍照识别、定时通知等复杂能力尚未接入，但代码中已经保留对应目录和接口，便于第二阶段继续开发。
