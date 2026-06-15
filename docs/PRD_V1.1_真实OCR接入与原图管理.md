# PRD_V1.1：真实 OCR 接入与原图管理

## 1. 文档说明

本文档是“智学扫描”项目第二阶段 V1.1 的阶段 PRD。

开发时应同时参考：

- `PRD_Main_智学扫描_项目总需求.md`：了解项目整体定位、模块边界、数据结构和长期目标。
- `PRD_V1.1_真实OCR接入与原图管理.md`：作为当前阶段的具体开发任务书。

本阶段只实现 V1.1 中定义的功能，不要重构整个项目，不要破坏 V1.0 已通过验收的功能。

---

## 2. 当前项目状态

V1.0 基础框架已经完成，当前项目应已具备以下能力：

1. App 可以正常启动。
2. MainActivity 使用 BottomNavigationView。
3. 已有 HomeFragment、KnowledgeFragment、WrongBookFragment、ProfileFragment 等页面。
4. ScanActivity 支持模拟 OCR 新增资料。
5. SQLite 本地数据库已经可用。
6. RecyclerView 可以展示资料列表和错题列表。
7. MaterialDetailActivity 支持查看、编辑、删除、标记错题。
8. SharedPreferences 支持设置保存。
9. 知识库和错题本的定位已经区分：
   - 知识库是全部资料总仓库。
   - 错题本是 `isWrongQuestion = 1` 的错题复习视图。

---

## 3. 阶段目标

V1.1 的目标是将项目从“模拟 OCR 系统”升级为“真实可演示的 OCR 资料扫描系统”。

本阶段需要实现：

```text
从相册选择图片
  ↓
ScanActivity 显示图片预览
  ↓
调用 Google ML Kit Text Recognition 识别文字
  ↓
识别结果自动填充到 OCR 内容编辑框
  ↓
用户编辑标题、科目、标签、错题状态、备注
  ↓
保存 OCR 文本和图片路径到 SQLite
  ↓
知识库页面展示资料
  ↓
详情页显示原图和 OCR 文本
```

---

## 4. 阶段边界

### 4.1 本阶段必须完成

1. 相册选择图片。
2. 图片预览。
3. ML Kit OCR 识别。
4. OCR 结果填入文本框。
5. 保存图片 Uri 或路径到 `imagePath`。
6. 详情页显示原图。
7. 保留模拟 OCR 入口。
8. 基础权限兼容与异常处理。
9. 增强中文教材场景：接入中文 OCR 模型，并提供“中文 OCR / 通用 OCR”模式选择。
10. 增强整页教材识别体验：提供中文 OCR / 通用 OCR 模式选择，识别结果允许用户手动修正。

### 4.2 本阶段暂不强制完成

1. CameraX 实时拍照。
2. 拍照与图片裁剪。
3. 图片压缩。
4. 批量识别多张图片。
5. 云端同步。
6. AI 自动生成题目解析。
7. WorkManager 复习提醒。
8. BroadcastReceiver 开机恢复提醒。
9. 图表统计美化。

这些功能可以放到 V1.2 或 V1.3。

---

## 5. 技术要求

1. 继续使用 Java + XML。
2. 保持现有 Activity + Fragment + RecyclerView + SQLiteOpenHelper 架构。
3. 接入 Google ML Kit Text Recognition，并同时支持 Latin 通用模型与 Chinese 中文模型。
4. 支持从系统相册选择图片。
5. 支持图片预览。
6. 支持将图片 Uri 或路径保存到 `StudyMaterial.imagePath` 字段。
7. MaterialDetailActivity 支持显示原图。
8. 保留原来的“模拟 OCR”按钮作为兜底演示入口，不要删除。
9. 权限处理要完整，避免 Android 版本兼容问题导致崩溃。
10. 所有新增代码必须有必要中文注释。
11. 项目必须可以正常编译运行。
12. 本阶段对数学公式、矩阵、上下标等复杂版式不承诺完全自动识别，应保留 OCR 文本可编辑能力，允许用户手动修正。

---

## 6. ScanActivity 改造要求

### 6.1 页面定位

ScanActivity 是本阶段最核心的页面，用于完成图片选择、图片预览、OCR 识别、资料编辑和保存。

### 6.2 页面元素

ScanActivity 应包含以下元素：

1. 页面标题：“扫描识别”。
2. 图片预览区域。
3. “选择图片”按钮。
4. “开始 OCR 识别”按钮。
5. “模拟 OCR”按钮。
6. OCR 模式选择：中文 OCR（推荐教材/题目）、通用 OCR（英文/数字）。
7. 图片识别提示文案。
8. 标题输入框。
9. OCR 内容编辑框。
10. 科目选择。
11. 标签输入。
12. 是否加入错题本开关。
13. 难度选择。
14. 错误原因输入框。
15. 备注输入框。
16. 保存按钮。

### 6.3 交互流程

#### 6.3.1 选择图片

1. 用户点击“选择图片”。
2. App 打开系统相册。
3. 用户选择一张图片。
4. App 获取图片 Uri。
5. App 将图片显示到 ImageView 中。
6. App 将图片 Uri 字符串暂存到当前页面变量 `selectedImageUri`。
7. App 同步解码 Bitmap，供图片预览和整图 OCR 使用。

#### 6.3.1.1 整图 OCR 说明

1. 用户选择图片后，图片预览区域显示所选图片。
2. 点击“开始 OCR 识别”后，当前阶段识别整张图片。
3. OCR 结果会自动填入文本框，用户可以继续手动修改。
4. 如果整页教材干扰较多，建议用户先在系统相册或其他工具中处理图片后再选择。
5. 本阶段不实现框选识别，避免模拟器触摸板操作不稳定影响验收演示。
6. 后续版本可继续扩展拍照裁剪或区域识别能力。

#### 6.3.2 开始 OCR 识别

1. 用户点击“开始 OCR 识别”。
2. 如果未选择图片，Toast 提示：“请先选择图片”。
3. 如果已选择图片，根据 OCR 模式选择中文模型或通用模型，并调用 OcrManager 识别文字。
4. 当前阶段对整张 Bitmap 进行 OCR 识别。
5. 识别过程中可以禁用识别按钮或显示加载状态。
6. 识别成功后，将结果填入 OCR 内容编辑框。
7. 如果标题为空，可以自动取识别文本前 15 个字作为标题。
8. Toast 提示：“识别完成”。
9. 识别失败时，Toast 提示：“识别失败，请重试或使用模拟 OCR”。
10. 识别失败时，不要清空已有 OCR 文本。

#### 6.3.3 模拟 OCR

真实 OCR 接入后，仍需保留“模拟 OCR”按钮。

点击“模拟 OCR”后，向 OCR 内容编辑框填入一段示例学习资料，例如：

```text
设 A 为三阶矩阵，若 A 与对角矩阵相似，则 A 的特征值为主对角线元素。本题考察矩阵相似、特征值与对角化条件。
```

保留该功能的目的：

1. 真实 OCR 因权限、依赖或模拟器环境失败时，仍可稳定课堂演示。
2. 方便测试数据库、知识库和错题本流程。
3. 降低验收风险。

#### 6.3.4 保存资料

点击保存时，需要保存以下字段：

1. `title`
2. `content`
3. `subject`
4. `sourceType`
5. `tags`
6. `imagePath`
7. `isWrongQuestion`
8. `difficulty`
9. `wrongReason`
10. `note`
11. `createTime`
12. `updateTime`

其中：

- `content` 保存 OCR 识别文本或模拟 OCR 文本。
- `imagePath` 保存图片 Uri 字符串。
- 如果用户未选择图片，则 `imagePath` 可以为空。

---

## 7. OcrManager 设计要求

### 7.1 类位置

建议放在：

```text
com.example.smartscan.ocr.OcrManager
```

### 7.2 方法设计

OcrManager 应提供以下方法：

```java
public void recognizeTextFromImage(Context context, Uri imageUri, OcrResultCallback callback)
```

### 7.3 方法逻辑

1. 如果 `imageUri == null`，通过 callback 返回失败，不要崩溃。
2. 使用 `InputImage.fromFilePath(context, imageUri)` 构造图片。
3. 使用 `TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)` 获取识别器。
4. 调用 recognizer 识别图片文字。
5. 识别成功后返回完整文本。
6. 识别失败后返回异常信息。

### 7.4 OcrResultCallback 设计

建议接口如下：

```java
public interface OcrResultCallback {
    void onSuccess(String text);
    void onFailure(Exception e);
}
```

---

## 8. MaterialDetailActivity 改造要求

### 8.1 原图显示

详情页需要支持原图预览。

逻辑：

1. 如果 `StudyMaterial.imagePath` 不为空：
   - 使用 ImageView 显示原图。
2. 如果 `imagePath` 为空：
   - 隐藏原图区域；或
   - 显示“暂无原图”。

### 8.2 加载方式

如果项目已引入 Glide，可以使用 Glide 加载 Uri。

如果未引入 Glide，可以先使用：

```java
imageView.setImageURI(Uri.parse(imagePath));
```

### 8.3 不得破坏现有功能

详情页现有功能必须保留：

1. 查看资料。
2. 编辑资料。
3. 删除资料。
4. 标记或取消错题。
5. 修改难度。
6. 修改错误原因。
7. 修改掌握状态。
8. 复习一次。

---

## 9. 数据库和实体类要求

### 9.1 字段检查

请检查 `StudyMaterial` 是否已包含以下字段：

```text
imagePath
isWrongQuestion
difficulty
wrongReason
mastered
reviewCount
lastReviewTime
nextReviewTime
```

### 9.2 imagePath 字段

本阶段必须确保 `imagePath` 能从 ScanActivity 保存，并在 MaterialDetailActivity 中读取和显示。

### 9.3 数据库升级

如果当前 SQLite 数据库中没有 `image_path` 字段，需要：

1. 修改建表语句。
2. 修改 insert 方法。
3. 修改 update 方法。
4. 修改 query 方法。
5. 修改实体类字段映射。
6. 提升数据库版本号。
7. 在 `onUpgrade` 中添加兼容逻辑，避免老数据库崩溃。

示例：

```sql
ALTER TABLE study_material ADD COLUMN image_path TEXT;
```

如果同时缺少错题复习字段，也需要补充：

```sql
ALTER TABLE study_material ADD COLUMN wrong_reason TEXT;
ALTER TABLE study_material ADD COLUMN mastered INTEGER DEFAULT 0;
ALTER TABLE study_material ADD COLUMN next_review_time INTEGER;
```

---

## 10. AndroidManifest 与依赖要求

### 10.1 ML Kit 依赖

需要在 Gradle 中加入 ML Kit Text Recognition 依赖。

具体版本可根据当前项目 Gradle 插件和仓库配置选择稳定版本。

本阶段实际采用 bundled 模型，保证首次演示时模型可立即使用：

```gradle
implementation "com.google.mlkit:text-recognition:16.0.1"
implementation "com.google.mlkit:text-recognition-chinese:16.0.1"
```

其中：

- `text-recognition` 用于通用 Latin/数字场景。
- `text-recognition-chinese` 用于中文教材、中文题干和中英数字混排场景。
- 数学公式、矩阵、上下标和手写批注仍可能识别不完整，需要用户在 OCR 文本框中手动修正。

### 10.2 图片读取权限

需要兼容不同 Android 版本：

1. Android 13 及以上：使用图片读取相关权限。
2. Android 12 及以下：使用外部存储读取权限。
3. 如果使用系统 Photo Picker 或系统相册返回 Uri，可减少权限依赖，但仍需做好异常处理。

### 10.3 Manifest 要求

1. 不要添加不必要的危险权限。
2. 如果使用系统相册选择，确保 Activity Result 或 intent 调用兼容。
3. 不要因为权限被拒绝导致 App 崩溃。

---

## 11. 权限与异常处理

本阶段必须处理以下异常场景：

1. 用户没有选择图片就点击 OCR。
2. 用户取消相册选择。
3. 图片 Uri 为空。
4. 图片无法读取。
5. ML Kit 识别失败。
6. OCR 返回空文本。
7. 权限被拒绝。
8. 详情页 imagePath 为空或 Uri 失效。

处理原则：

1. 使用 Toast 或页面提示说明问题。
2. 不要让 App 崩溃。
3. OCR 失败时保留用户已有输入。
4. 提供模拟 OCR 作为兜底。

---

## 12. UI 要求

### 12.1 ScanActivity

1. 图片预览区域大小适中。
2. 如果未选择图片，显示默认占位区域或提示“请选择图片”。
3. OCR 按钮、模拟 OCR 按钮和保存按钮排列清晰。
4. OCR 结果编辑框高度适中，便于编辑较长文本。
5. 页面整体风格继续保持 Material Design。
6. 图片预览区域应清晰显示所选图片，不影响页面滚动和其他输入控件。
7. OCR 模式选择应放在识别按钮附近，便于用户根据中文教材或英文数字资料切换。

### 12.2 MaterialDetailActivity

1. 原图预览应位于 OCR 文本上方或详情页较显眼位置。
2. 原图不应过大导致页面难以滚动。
3. 没有原图时不要显示空白大区域。
4. 保持已有编辑、删除、错题操作按钮清晰可见。

---

## 13. 本阶段验收标准

V1.1 完成后，应满足以下验收标准：

1. App 正常启动。
2. V1.0 已有功能不丢失。
3. 进入 ScanActivity 后可以点击“选择图片”。
4. 可以打开系统相册。
5. 用户选择图片后，页面能显示图片预览。
6. 点击“开始 OCR 识别”后可以调用 ML Kit 识别图片文字。
7. 识别结果可以自动填入 OCR 内容编辑框。
8. OCR 文本可以继续手动编辑。
9. 保存后，知识库页面可以看到新资料。
10. 保存后，数据库中的 `imagePath` 不为空。
11. 点击资料进入详情页后，可以显示原图和 OCR 文本。
12. 标记为错题后，错题复习中心能看到该资料。
13. 未选择图片直接点击识别不会崩溃。
14. 识别失败时有提示，不会清空已有内容。
15. 模拟 OCR 功能仍然可用。
16. 权限被拒绝或图片选择取消时 App 不崩溃。
17. 可以选择“中文 OCR”模式识别中文教材图片。
18. 可以选择“中文 OCR”或“通用 OCR”模式。
19. OCR 识别不理想时，可以手动编辑文本或使用模拟 OCR 兜底。

---

## 14. 不允许破坏的 V1.0 功能

开发 V1.1 时，不允许破坏以下功能：

1. 底部导航切换。
2. 首页统计卡片。
3. 知识库资料列表。
4. 知识库搜索和筛选。
5. 错题复习中心列表。
6. 详情页查看、编辑、删除。
7. 标记错题和取消错题。
8. 复习一次和掌握状态。
9. 设置页 SharedPreferences 保存。
10. SQLite 数据持久化。

---

## 15. 推荐给 Codex 的执行提示词

```text
请阅读 docs/PRD_Main_智学扫描_项目总需求.md 了解项目整体定位。

当前只执行 docs/PRD_V1.1_真实OCR接入与原图管理.md 中的任务。

项目第一阶段已经完成，请不要重建项目，也不要重构无关代码。

本阶段目标是：
1. 在 ScanActivity 中支持从相册选择图片。
2. 显示图片预览。
3. 接入 Google ML Kit Text Recognition 识别图片文字。
4. 将识别结果自动填入 OCR 内容编辑框。
5. 保存图片 Uri 到 StudyMaterial.imagePath 字段。
6. 在 MaterialDetailActivity 中显示原图。
7. 保留模拟 OCR 按钮作为兜底。
8. 完善权限和异常处理。

要求：
1. 使用 Java + XML。
2. 保持现有 Activity + Fragment + RecyclerView + SQLiteOpenHelper 架构。
3. 不要破坏 V1.0 已有功能。
4. 不要删除模拟 OCR 功能。
5. 不要一次性实现 CameraX、WorkManager、BroadcastReceiver 等后续阶段功能。
6. 修改完成后列出改动文件、每个文件改了什么、测试步骤。
7. 保证项目可以正常编译运行。
```

---

## 16. 本阶段交付物

V1.1 完成后应交付：

1. 修改后的 ScanActivity。
2. 修改后的 ScanActivity XML 布局。
3. OcrManager。
4. OcrResultCallback。
5. 修改后的 MaterialDetailActivity。
6. 修改后的详情页 XML 布局。
7. 修改后的 StudyMaterial 实体类，如需要。
8. 修改后的 DBHelper 或 Repository，如需要。
9. 修改后的 AndroidManifest，如需要。
10. 修改后的 Gradle 依赖。
11. 简要测试说明。

---

## 17. 阶段完成后的下一步

V1.1 完成并稳定后，再进入 V1.2：复习提醒与统计模块。

V1.2 重点是：

1. WorkManager 或 Service。
2. 每日错题复习提醒。
3. BroadcastReceiver 监听开机广播。
4. 通知栏提醒。
5. 统计页完善。

不要在 V1.1 阶段提前实现这些内容。
