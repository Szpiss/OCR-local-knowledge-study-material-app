# PRD：智学扫描——校园 AI 资料扫描与错题整理 App

## 1. 产品名称

**智学扫描：基于 OCR 与本地知识库的学习资料管理 App**

项目正式名称建议使用：

**基于 ML Kit 与 SQLite 的移动端学习资料识别与本地知识库管理系统**

---

## 2. 产品背景

大学生在日常学习中经常需要整理试卷、课堂笔记、PPT 截图、课本内容和错题资料。

传统整理方式通常依赖：

- 相册截图
- 纸质笔记
- 手动输入
- 文件夹分类
- 微信收藏或网盘保存

这些方式存在以下问题：

1. 学习资料分散，后期难以查找。
2. 图片中的文字无法直接检索。
3. 错题整理依赖人工手动摘抄，效率较低。
4. 资料缺乏科目、标签、错题状态等结构化信息。
5. 无法形成长期可复用的个人学习知识库。

因此，本项目拟设计并实现一款移动端学习资料扫描与错题整理 App。系统通过 OCR 识别技术将图片中的文字提取出来，并将识别结果保存到手机本地数据库中，形成个人本地学习资料知识库。用户可以按照科目、标签、关键词和错题状态进行检索、管理和复习，从而提升学习资料整理效率和复习效率。

---

## 3. 产品目标

### 3.1 基础目标

1. 支持学习资料的新增、保存、查看、编辑和删除。
2. 支持 OCR 识别结果的编辑与保存。
3. 支持按照科目、标签和关键词检索资料。
4. 支持错题收藏和错题本管理。
5. 支持本地 SQLite 数据库持久化存储。
6. 支持用户偏好设置。
7. 支持复习提醒功能。
8. 支持基础统计分析。

### 3.2 课程目标

本项目需要体现移动应用开发课程中的核心知识点：

1. 多 Activity 页面跳转。
2. Fragment 页面组织。
3. RecyclerView 列表展示。
4. SQLite 本地数据库。
5. SharedPreferences 本地配置存储。
6. Service 或 WorkManager 后台任务。
7. BroadcastReceiver 广播监听。
8. 第三方依赖库引入。
9. Material Design 风格界面设计。
10. 项目文档和答辩说明完整。

---

## 4. 用户群体

| 用户类型 | 使用场景 |
|---|---|
| 大学生 | 整理课堂笔记、试卷、错题、PPT 截图 |
| 考研学生 | 整理高数、线代、英语、408 错题 |
| 教师或助教 | 整理题目材料、课堂资料 |
| 自学用户 | 建立个人学习资料库 |

---

## 5. 核心使用场景

### 5.1 场景一：扫描课堂笔记

用户在课后拍摄课堂笔记，通过 OCR 识别文字，选择所属科目并保存到知识库。

### 5.2 场景二：整理错题

用户扫描试卷错题，识别题目内容，标记为错题，添加错误原因、知识点标签和备注。

### 5.3 场景三：搜索历史资料

用户在复习时搜索“特征值”“KMP”“进程调度”等关键词，快速找到历史扫描资料。

### 5.4 场景四：每日复习提醒

用户开启复习提醒后，系统每天定时提醒用户复习错题。

---

## 6. 产品功能结构

```text
智学扫描 App
├── 首页模块
├── 扫描识别模块
├── 本地知识库模块
├── 错题复习中心模块
├── 资料详情模块
├── 统计模块
├── 设置模块
└── 复习提醒模块
```

---

## 6.1 模块定位说明：知识库与错题本的区别

本项目中，“本地知识库”和“错题本”不是两个重复的资料列表，而是存在明确的数据关系和业务分工。

### 6.1.1 本地知识库定位

**本地知识库是全部学习资料的总仓库。**

它负责保存和管理用户通过 OCR 识别或手动录入的所有学习资料，包括：

- 课堂笔记
- PPT 截图
- 课本内容
- 普通例题
- 试卷资料
- 错题资料
- 知识点总结

知识库的核心目标是：

1. 存储全部资料。
2. 支持资料分类。
3. 支持关键词、科目、标签、来源类型检索。
4. 支持资料查看、编辑、删除。
5. 支持将普通资料标记为错题。

### 6.1.2 错题本定位

**错题本是基于知识库数据筛选出的错题复习视图。**

错题本不单独维护一套重复数据，而是从 `StudyMaterial` 表中筛选 `isWrongQuestion = 1` 的资料进行展示和复习管理。

错题本的核心目标是：

1. 聚合用户标记过的错题资料。
2. 支持按科目、难度、掌握状态筛选。
3. 支持记录错误原因。
4. 支持记录复习次数和上次复习时间。
5. 支持标记“已掌握”。
6. 支持取消错题标记。

### 6.1.3 二者关系

```text
本地知识库
├── 普通资料
│   ├── 课堂笔记
│   ├── PPT 截图
│   ├── 课本内容
│   └── 普通例题
└── 错题资料
    ├── 高数错题
    ├── 线代错题
    ├── 英语错题
    └── 408 错题
```

数据库关系如下：

```sql
-- 知识库页面：查询全部资料
SELECT * FROM study_material;

-- 错题本页面：只查询错题资料
SELECT * FROM study_material
WHERE is_wrong_question = 1;
```

因此，知识库负责“资料沉淀和检索”，错题本负责“错题复盘和掌握状态管理”。

---

## 7. 功能需求

## 7.1 首页模块

### 7.1.1 功能说明

首页用于展示用户学习资料的整体情况和最近记录。

### 7.1.2 功能点

1. 显示总资料数。
2. 显示错题数量。
3. 显示今日新增资料数。
4. 显示累计复习次数。
5. 展示最近扫描资料列表。
6. 点击资料进入详情页。

### 7.1.3 页面元素

- 欢迎语
- 统计卡片
- 最近资料列表
- 快速扫描入口
- 快速进入错题本入口

### 7.1.4 优先级

高。

---

## 7.2 扫描识别模块

### 7.2.1 功能说明

用户可以通过拍照或选择图片进行 OCR 识别，并将识别结果保存为学习资料。

### 7.2.2 功能点

1. 支持拍照获取图片。
2. 支持从相册选择图片。
3. 调用 OCR 识别图片文字。
4. 展示识别结果。
5. 用户可编辑 OCR 文本。
6. 用户可填写标题。
7. 用户可选择所属科目。
8. 用户可填写标签。
9. 用户可选择是否加入错题本。
10. 用户可填写备注。
11. 保存资料到本地数据库。

### 7.2.3 第一阶段实现

第一阶段先不接入真实 OCR 和相机功能，使用模拟 OCR 文本代替。

点击“模拟 OCR 识别”按钮后，在文本框中自动填入示例内容，例如：

```text
设 A 为三阶矩阵，若 A 与对角矩阵相似，则 A 的特征值为主对角线元素。
本题考察矩阵相似、特征值与对角化条件。
```

### 7.2.4 第二阶段实现

第二阶段接入：

- Google ML Kit Text Recognition
- 系统相册选择
- 系统相机或 CameraX 拍照
- 图片路径保存

### 7.2.5 优先级

高。

---

## 7.3 本地知识库模块

### 7.3.1 功能说明

本地知识库用于管理所有扫描和录入的学习资料。

系统将 OCR 识别结果结构化保存到本地数据库中，每条资料包含标题、内容、科目、标签、图片路径、错题状态、难度、备注、创建时间、复习次数等信息。

用户可以对这些资料进行分类、搜索、筛选和复习，从而形成个人本地学习知识库。

### 7.3.2 功能点

1. 展示所有学习资料。
2. 支持按科目筛选。
3. 支持关键词搜索。
4. 支持按标签检索。
5. 支持查看资料详情。
6. 支持编辑资料。
7. 支持删除资料。
8. 支持相关资料推荐。

### 7.3.3 页面元素

- 搜索框
- 科目筛选按钮
- 标签筛选区域
- 资料列表 RecyclerView
- 空数据提示

### 7.3.5 科目分类

默认科目包括：

- 高数
- 线代
- 英语
- 408
- 数据结构
- 计算机组成原理
- 操作系统
- 计算机网络
- 其他

### 7.3.6 本地知识库体现方式

本项目中的“本地知识库”不是简单的文本保存，而是将 OCR 识别后的学习资料进行结构化存储。

每条资料至少包含：

- 标题
- OCR 文本
- 所属科目
- 标签
- 原图路径
- 是否错题
- 难度
- 用户备注
- 创建时间
- 更新时间
- 复习次数
- 上次复习时间

用户可以通过搜索、筛选、分类、错题收藏、复习记录等方式管理这些资料。

### 7.3.7 优先级

高。

---

## 7.4 错题复习中心模块

### 7.4.1 功能说明

错题复习中心用于集中管理用户在知识库中标记的错题资料。

错题本不是独立数据源，而是基于 `StudyMaterial` 表中 `isWrongQuestion = 1` 字段筛选得到的复习视图。其业务重点不是普通资料管理，而是错题复盘、错误原因记录、复习次数更新和掌握状态管理。

### 7.4.2 功能点

1. 只展示 `isWrongQuestion = 1` 的资料。
2. 页面标题显示为“错题复习中心”。
3. 顶部显示错题统计信息：错题总数、待复习数、已掌握数。
4. 支持按科目筛选错题。
5. 支持按难度筛选错题。
6. 支持按掌握状态筛选：未掌握、已掌握。
7. 支持查看错题详情。
8. 支持编辑错误原因和备注。
9. 支持记录复习次数。
10. 支持点击“复习一次”后 `reviewCount + 1`，并更新 `lastReviewTime`。
11. 支持点击“已掌握”后将 `mastered` 更新为 1。
12. 支持取消错题标记，取消前需要二次确认。

### 7.4.3 页面元素

- 页面标题：错题复习中心
- 错题总数统计
- 待复习数量统计
- 已掌握数量统计
- 科目筛选按钮
- 难度筛选按钮
- 掌握状态筛选按钮
- 错题列表 RecyclerView
- 空数据提示

### 7.4.4 列表项展示内容

错题本列表项偏向展示“复习属性”，每条错题卡片应包含：

- 标题
- 科目
- 难度
- 错误原因
- 复习次数
- 上次复习时间
- 掌握状态
- 复习一次按钮
- 错误原因输入框
- 掌握状态开关
- 已掌握按钮
- 查看详情按钮

### 7.4.5 优先级

高。

---

## 7.5 资料详情模块

### 7.5.1 功能说明

用户可以查看和编辑单条学习资料。

### 7.5.2 功能点

1. 展示资料标题。
2. 展示 OCR 文本。
3. 展示所属科目。
4. 展示标签。
5. 展示备注。
6. 展示是否为错题。
7. 展示创建时间。
8. 支持编辑资料。
9. 支持删除资料。
10. 支持标记或取消错题。
11. 支持增加复习次数。
12. 支持查看相关资料推荐。

### 7.5.3 页面元素

- 标题输入框
- 科目选择框
- 标签输入框
- OCR 内容编辑框
- 备注输入框
- 是否错题开关
- 难度选择
- 保存按钮
- 删除按钮
- 复习一次按钮
- 错误原因输入框
- 掌握状态开关

### 7.5.4 优先级

高。

---

## 7.6 统计模块

### 7.6.1 功能说明

统计模块用于展示用户的资料整理和错题复习情况。

### 7.6.2 功能点

1. 统计总资料数。
2. 统计错题总数。
3. 统计今日新增数量。
4. 统计各科目资料数量。
5. 统计各科目错题数量。
6. 统计累计复习次数。

### 7.6.3 页面元素

- 总资料数卡片
- 错题数量卡片
- 今日新增卡片
- 复习次数卡片
- 各科目资料统计列表
- 各科目错题统计列表

### 7.6.4 优先级

中。

---

## 7.7 设置模块

### 7.7.1 功能说明

设置模块用于保存用户偏好和系统配置。

### 7.7.2 功能点

1. 设置默认科目。
2. 开启或关闭每日复习提醒。
3. 设置提醒时间。
4. 清空本地数据。
5. 查看项目说明。
6. 保存设置到 SharedPreferences。

### 7.7.3 页面元素

- 默认科目选择
- 复习提醒开关
- 提醒时间选择
- 清空数据按钮
- 关于项目区域

### 7.7.4 优先级

高。

---

## 7.8 复习提醒模块

### 7.8.1 功能说明

系统根据用户设置的提醒时间，每日提醒用户复习错题。

### 7.8.2 功能点

1. 用户可以开启或关闭提醒。
2. 用户可以设置提醒时间。
3. 系统到点发送通知。
4. 手机重启后自动恢复提醒任务。

### 7.8.3 技术实现

1. 使用 WorkManager 或 Service 实现后台任务。
2. 使用 BroadcastReceiver 监听开机广播。
3. 使用 SharedPreferences 保存提醒配置。

### 7.8.4 优先级

中。

---

# 8. 非功能需求

## 8.1 性能需求

1. App 启动速度应较快。
2. 本地资料列表加载流畅。
3. OCR 识别过程应显示加载状态。
4. 数据库查询应避免阻塞主线程。
5. RecyclerView 列表应支持较多数据展示。

## 8.2 可用性需求

1. 页面结构清晰。
2. 按钮和输入框易于理解。
3. OCR 识别结果允许用户手动修改。
4. 数据保存后应有 Toast 提示。
5. 删除资料前应有确认弹窗。
6. 空数据状态需要有友好提示。

## 8.3 可靠性需求

1. 数据保存后重启 App 不丢失。
2. OCR 识别失败时需要错误提示。
3. 权限被拒绝时需要提示用户手动开启权限。
4. 数据库操作失败时需要提示用户。
5. App 不应出现明显崩溃。

## 8.4 安全与隐私需求

1. 用户学习资料默认只保存在本地。
2. 不上传用户图片和文本。
3. 清空数据前必须二次确认。
4. 本地图片路径和文本数据由用户自行管理。
5. 用户可以手动删除全部本地数据。

---

# 9. 数据结构设计

## 9.1 StudyMaterial 表

用于保存学习资料和错题信息。

| 字段名 | 类型 | 说明 |
|---|---|---|
| id | INTEGER | 主键，自增 |
| title | TEXT | 资料标题 |
| content | TEXT | OCR 识别内容 |
| subject | TEXT | 所属科目 |
| sourceType | TEXT | 来源类型 |
| tags | TEXT | 标签 |
| imagePath | TEXT | 图片路径 |
| isWrongQuestion | INTEGER | 是否错题，0 否，1 是 |
| difficulty | INTEGER | 难度等级，1-5 |
| note | TEXT | 用户备注 |
| createTime | INTEGER | 创建时间 |
| updateTime | INTEGER | 更新时间 |
| reviewCount | INTEGER | 复习次数 |
| lastReviewTime | INTEGER | 上次复习时间 |
| wrongReason | TEXT | 错误原因 |
| mastered | INTEGER | 是否已掌握，0 未掌握，1 已掌握 |
| nextReviewTime | INTEGER | 下次复习时间，预留字段 |

建表 SQL：

```sql
CREATE TABLE study_material (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    title TEXT NOT NULL,
    content TEXT,
    subject TEXT,
    source_type TEXT,
    tags TEXT,
    image_path TEXT,
    is_wrong_question INTEGER DEFAULT 0,
    difficulty INTEGER DEFAULT 1,
    note TEXT,
    create_time INTEGER,
    update_time INTEGER,
    review_count INTEGER DEFAULT 0,
    last_review_time INTEGER,
    wrong_reason TEXT,
    mastered INTEGER DEFAULT 0,
    next_review_time INTEGER
);
```

如果项目第一阶段已创建旧版数据库，后续升级时需要在 `DBHelper.onUpgrade()` 中添加字段，避免旧数据表缺字段导致崩溃：

```sql
ALTER TABLE study_material ADD COLUMN wrong_reason TEXT;
ALTER TABLE study_material ADD COLUMN mastered INTEGER DEFAULT 0;
ALTER TABLE study_material ADD COLUMN next_review_time INTEGER;
```

---

## 9.2 ReviewRecord 表

用于保存复习记录。

| 字段名 | 类型 | 说明 |
|---|---|---|
| id | INTEGER | 主键，自增 |
| materialId | INTEGER | 对应资料 id |
| reviewTime | INTEGER | 复习时间 |
| result | TEXT | 复习结果 |
| remark | TEXT | 复习备注 |

建表 SQL：

```sql
CREATE TABLE review_record (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    material_id INTEGER NOT NULL,
    review_time INTEGER NOT NULL,
    result TEXT,
    remark TEXT
);
```

---

## 9.3 TagItem 表，可选

用于保存标签统计。

| 字段名 | 类型 | 说明 |
|---|---|---|
| id | INTEGER | 主键，自增 |
| name | TEXT | 标签名称 |
| count | INTEGER | 使用次数 |

建表 SQL：

```sql
CREATE TABLE tag_item (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    count INTEGER DEFAULT 0
);
```

---

# 10. 页面流程

## 10.1 新增资料流程

```text
进入扫描页
  ↓
选择图片或模拟 OCR
  ↓
生成识别文本
  ↓
编辑标题、内容、科目、标签、备注
  ↓
选择是否加入错题本
  ↓
保存到本地数据库
  ↓
知识库页面刷新显示
```

---

## 10.2 查看资料流程

```text
知识库页面
  ↓
点击资料列表项
  ↓
进入资料详情页
  ↓
查看 OCR 文本、标签、备注
  ↓
编辑 / 删除 / 标记错题
```

---

## 10.3 错题复习流程

```text
进入错题本页面
  ↓
选择科目筛选
  ↓
点击错题
  ↓
查看错题详情
  ↓
填写复习备注
  ↓
增加复习次数
```

---

## 10.4 设置提醒流程

```text
进入我的 / 设置页面
  ↓
开启复习提醒
  ↓
设置提醒时间
  ↓
保存到 SharedPreferences
  ↓
注册 WorkManager / Service 任务
  ↓
到点发送通知
```

---

# 11. 技术架构设计

## 11.1 技术选型

| 模块 | 技术 |
|---|---|
| 开发语言 | Java |
| UI | XML |
| 页面结构 | Activity + Fragment |
| 列表展示 | RecyclerView |
| 数据库 | SQLiteOpenHelper |
| 本地配置 | SharedPreferences |
| OCR | Google ML Kit Text Recognition |
| 图片输入 | 系统相机 Intent / 相册选择 / CameraX |
| 后台提醒 | WorkManager 或 Service |
| 广播监听 | BroadcastReceiver |
| UI 风格 | Material Design |
| 图片加载 | Glide，可选 |
| 图表统计 | MPAndroidChart，可选 |

---

## 11.2 项目包结构

```text
com.example.smartscan
├── ui
│   ├── activity
│   │   ├── MainActivity.java
│   │   ├── ScanActivity.java
│   │   ├── MaterialDetailActivity.java
│   │   └── SettingActivity.java
│   │
│   ├── fragment
│   │   ├── HomeFragment.java
│   │   ├── KnowledgeFragment.java
│   │   ├── WrongBookFragment.java
│   │   ├── StatisticsFragment.java
│   │   └── ProfileFragment.java
│   │
│   └── adapter
│       ├── MaterialAdapter.java
│       ├── WrongQuestionAdapter.java
│       └── TagAdapter.java
│
├── data
│   ├── entity
│   │   ├── StudyMaterial.java
│   │   ├── ReviewRecord.java
│   │   └── TagItem.java
│   │
│   ├── db
│   │   └── DBHelper.java
│   │
│   └── repository
│       ├── MaterialRepository.java
│       └── ReviewRepository.java
│
├── ocr
│   ├── OcrManager.java
│   └── OcrResultCallback.java
│
├── worker
│   ├── ReviewReminderWorker.java
│   └── ReminderScheduler.java
│
├── receiver
│   └── BootReceiver.java
│
└── util
    ├── TimeUtils.java
    ├── PermissionUtils.java
    ├── ImageUtils.java
    └── PreferenceManager.java
```

---

# 12. 核心页面设计

## 12.1 MainActivity

### 页面职责

MainActivity 是应用主入口，使用 BottomNavigationView 承载多个 Fragment。

### 底部导航

```text
首页 | 知识库 | 扫描 | 错题本 | 我的
```

### 交互逻辑

1. 点击“首页”切换到 HomeFragment。
2. 点击“知识库”切换到 KnowledgeFragment。
3. 点击“扫描”跳转到 ScanActivity。
4. 点击“错题本”切换到 WrongBookFragment。
5. 点击“我的”切换到 ProfileFragment。

---

## 12.2 ScanActivity

### 页面职责

用于完成资料扫描、OCR 识别、资料编辑和保存。

### 第一阶段页面元素

1. 选择图片按钮。
2. 模拟 OCR 识别按钮。
3. 标题输入框。
4. OCR 内容编辑框。
5. 科目选择框。
6. 标签输入框。
7. 是否加入错题本开关。
8. 难度选择。
9. 备注输入框。
10. 保存按钮。

---

## 12.3 KnowledgeFragment

### 页面职责

用于展示和管理本地知识库，定位为“全部学习资料的总仓库”。

### 页面元素

1. 页面标题：本地知识库 / 全部资料。
2. 搜索框。
3. 科目筛选按钮。
4. 标签筛选区域。
5. 来源类型筛选区域。
6. 资料列表 RecyclerView。
7. 空数据提示。

### 交互逻辑

1. 默认展示全部资料。
2. 支持根据关键词、科目、标签、来源类型筛选资料。
3. 点击资料卡片进入 MaterialDetailActivity。
4. 点击“标记为错题”后将 `isWrongQuestion` 设置为 1。
5. 知识库页面强调资料管理，不直接承担错题复习任务。

---

## 12.4 WrongBookFragment

### 页面职责

用于展示所有错题资料，定位为“错题复习中心”。

该页面只展示 `isWrongQuestion = 1` 的资料，不展示普通学习资料。

### 页面元素

1. 页面标题：错题复习中心。
2. 错题总数统计。
3. 待复习数量统计。
4. 已掌握数量统计。
5. 科目筛选按钮。
6. 难度筛选按钮。
7. 掌握状态筛选按钮。
8. 错题列表 RecyclerView。
9. 空数据提示。

### 交互逻辑

1. 默认展示所有未掌握错题。
2. 支持按科目、难度、掌握状态筛选。
3. 点击“复习一次”后更新复习次数和上次复习时间。
4. 点击“已掌握”后更新掌握状态。
5. 点击“取消错题”前需要二次确认。
6. 错题本页面强调复习训练，不承担全部资料检索任务。

---

## 12.5 MaterialDetailActivity

### 页面职责

用于查看、编辑、删除单条资料。

### 页面元素

1. 标题输入框。
2. OCR 内容编辑框。
3. 科目选择框。
4. 标签输入框。
5. 备注输入框。
6. 错题开关。
7. 难度选择。
8. 保存按钮。
9. 删除按钮。
10. 复习一次按钮。

---

## 12.6 ProfileFragment

### 页面职责

用于管理用户设置。

### 页面元素

1. 默认科目设置。
2. 复习提醒开关。
3. 提醒时间设置。
4. 清空数据按钮。
5. 关于项目说明。

---

# 13. 版本规划

## 13.1 V1.0：基础框架版

### 目标

完成课程基础考察点。

### 功能

1. MainActivity + BottomNavigation。
2. Fragment 页面切换。
3. SQLite 数据库。
4. RecyclerView 列表。
5. 模拟 OCR 新增资料。
6. 知识库搜索。
7. 错题本。
8. 设置页 SharedPreferences。

---

## 13.2 V1.1：OCR 接入版

### 目标

接入真实 OCR 能力。

### 功能

1. 接入 ML Kit Text Recognition。
2. 接入图片选择。
3. 接入拍照功能。
4. 保存图片路径。
5. OCR 失败处理。

---

## 13.3 V1.2：提醒与统计版

### 目标

增强完整度。

### 功能

1. 接入 WorkManager。
2. 每日错题复习提醒。
3. BootReceiver 恢复提醒。
4. 统计页。
5. 图表展示。

---

## 13.4 V1.3：优化答辩版

### 目标

提升展示效果。

### 功能

1. UI 美化。
2. 空状态页面。
3. 测试数据生成。
4. 答辩演示流程优化。
5. 项目说明页。

---

# 14. 验收标准

## 14.1 基础验收

1. App 可以正常启动。
2. 页面无明显崩溃。
3. 底部导航正常切换。
4. 可以新增学习资料。
5. 可以保存到 SQLite。
6. 重启 App 后数据仍存在。
7. 可以搜索资料。
8. 可以标记错题。
9. 错题本可以正常显示错题。
10. 设置项可以保存。

---

## 14.2 高级验收

1. OCR 可以识别图片文字。
2. 可以从相册选择图片。
3. 可以保存原图路径。
4. 可以定时提醒复习。
5. 可以统计各科资料数量。
6. UI 较美观，适合课堂演示。

---

# 15. 第一阶段开发目标

第一阶段不要求实现真实 OCR，不要求接入真实相机。

第一阶段重点是把项目框架搭起来。

## 15.1 第一阶段必须完成

1. App 可以启动。
2. MainActivity 底部导航可用。
3. HomeFragment 可以显示统计卡片。
4. KnowledgeFragment 可以显示资料列表。
5. WrongBookFragment 可以显示错题列表。
6. ProfileFragment 可以保存设置。
7. ScanActivity 可以录入模拟 OCR 资料。
8. 数据可以保存到 SQLite。
9. 数据可以在知识库页面展示。
10. 点击列表可以进入详情页。
11. 详情页可以编辑、删除、标记错题。
12. 错题本页面可以显示错题数据。

---

# 16. Codex 开发要求

请 Codex 根据本 PRD 生成项目时，必须遵守以下要求：

1. 使用 Java + XML。
2. 使用标准 Android Studio 项目结构。
3. 使用 Activity + Fragment。
4. 使用 RecyclerView。
5. 使用 SQLiteOpenHelper。
6. 使用 SharedPreferences。
7. 使用 Material Design 风格。
8. 先实现模拟 OCR，不要第一阶段直接接入复杂 ML Kit。
9. 预留 ML Kit、CameraX、WorkManager、BroadcastReceiver 的接口。
10. 代码必须可以运行，不能只给伪代码。
11. 每个主要类添加中文注释。
12. 数据库操作封装在 DBHelper 或 Repository 中。
13. 不要把所有业务逻辑都写在 Activity 中。
14. UI 要简洁、美观、适合课堂展示。

---

# 17. 推荐第一阶段交付物

第一阶段交付物包括：

1. Android Studio 可运行项目。
2. SQLite 数据库结构。
3. MainActivity。
4. ScanActivity。
5. MaterialDetailActivity。
6. HomeFragment。
7. KnowledgeFragment。
8. WrongBookFragment。
9. ProfileFragment。
10. RecyclerView Adapter。
11. StudyMaterial 实体类。
12. DBHelper。
13. SharedPreferences 工具类。
14. OcrManager 模拟类。
15. BootReceiver 占位类。
16. ReviewReminderWorker 占位类。
17. ReminderScheduler 占位类。

---

# 18. 项目亮点总结

本项目的亮点包括：

1. 将 OCR 技术应用于学习资料整理场景。
2. 将识别结果结构化保存为本地知识库。
3. 支持科目、标签、关键词多维检索。
4. 支持错题收藏和复习记录。
5. 支持本地存储，保护用户隐私。
6. 覆盖 Activity、Fragment、RecyclerView、SQLite、SharedPreferences、BroadcastReceiver、后台任务等移动开发课程核心考察点。
7. 项目功能完整，适合课堂展示和答辩。
