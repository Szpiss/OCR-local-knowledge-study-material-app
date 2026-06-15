package com.example.smartscan.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import com.example.smartscan.R;
import com.example.smartscan.data.entity.StudyMaterial;
import com.example.smartscan.data.repository.StudyMaterialRepository;
import com.example.smartscan.ocr.OcrManager;
import com.example.smartscan.ocr.OcrResultCallback;
import com.example.smartscan.util.AppPreferenceManager;
import com.example.smartscan.util.SubjectUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.InputStream;

/**
 * 扫描录入页面。
 *
 * 答辩重点：
 * 1. 这是“从图片到本地知识库”的入口。
 * 2. 支持系统相册选图、图片预览、中文/通用 OCR、模拟 OCR 兜底。
 * 3. 保存时会把 OCR 文本和图片 Uri 一起写入 SQLite。
 */
public class ScanActivity extends AppCompatActivity {
    private AppCompatImageView ivPreview;
    private TextView tvPreviewPlaceholder;
    private TextView tvPreviewHint;
    private TextInputEditText etContent;
    private TextInputEditText etTitle;
    private TextInputEditText etTags;
    private TextInputEditText etNote;
    private TextInputEditText etWrongReason;
    private Spinner spSubject;
    private Spinner spSourceType;
    private Spinner spOcrMode;
    private CheckBox cbWrong;
    private MaterialButton btnRealOcr;
    private StudyMaterialRepository repository;
    private OcrManager ocrManager;
    private Uri selectedImageUri;

    // selectedBitmap 是从 Uri 解码出来的图片对象，用于图片预览和整图 OCR。
    private Bitmap selectedBitmap;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        repository = new StudyMaterialRepository(this);
        ocrManager = new OcrManager();
        setupImagePicker();
        bindViews();
        setupSubjectSpinner();
        setupActions();
    }

    private void bindViews() {
        ivPreview = findViewById(R.id.iv_preview);
        tvPreviewPlaceholder = findViewById(R.id.tv_preview_placeholder);
        tvPreviewHint = findViewById(R.id.tv_preview_hint);
        etContent = findViewById(R.id.et_content);
        etTitle = findViewById(R.id.et_title);
        etTags = findViewById(R.id.et_tags);
        etNote = findViewById(R.id.et_note);
        etWrongReason = findViewById(R.id.et_wrong_reason);
        spSubject = findViewById(R.id.sp_subject);
        spSourceType = findViewById(R.id.sp_source_type);
        spOcrMode = findViewById(R.id.sp_ocr_mode);
        cbWrong = findViewById(R.id.cb_wrong);
        btnRealOcr = findViewById(R.id.btn_real_ocr);
    }

    private void setupImagePicker() {
        // ActivityResultLauncher 是新版 Activity 结果回调写法。
        // 比旧的 onActivityResult 更清晰，也更适合相册选择这种异步操作。
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() != Activity.RESULT_OK || result.getData() == null) {
                        Toast.makeText(this, "已取消选择图片", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Uri uri = result.getData().getData();
                    if (uri == null) {
                        Toast.makeText(this, "图片 Uri 为空，请重新选择", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // 尽量保存 Uri 的长期读取权限，这样详情页以后也能读取原图。
                    persistUriPermission(result.getData(), uri);
                    showSelectedImage(uri);
                });
    }

    private void setupSubjectSpinner() {
        // 科目、来源类型、OCR 模式都是 Spinner，下拉数据统一来自工具类或固定数组。
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, SubjectUtils.SUBJECTS);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSubject.setAdapter(adapter);
        ArrayAdapter<String> sourceAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, SubjectUtils.SOURCE_TYPES);
        sourceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSourceType.setAdapter(sourceAdapter);
        ArrayAdapter<String> ocrModeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                new String[]{"中文 OCR（推荐教材/题目）", "通用 OCR（英文/数字）"});
        ocrModeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spOcrMode.setAdapter(ocrModeAdapter);
        String defaultSubject = new AppPreferenceManager(this).getDefaultSubject();
        spSubject.setSelection(SubjectUtils.indexOf(defaultSubject));
    }

    private void setupActions() {
        MaterialButton btnChooseImage = findViewById(R.id.btn_choose_image);
        MaterialButton btnMockOcr = findViewById(R.id.btn_mock_ocr);
        MaterialButton btnSave = findViewById(R.id.btn_save);

        btnChooseImage.setOnClickListener(v -> openImagePicker());
        btnRealOcr.setOnClickListener(v -> startRealOcr());
        btnMockOcr.setOnClickListener(v -> {
            // 模拟 OCR 不依赖相册和 ML Kit，主要作为课堂演示兜底。
            etContent.setText(ocrManager.recognizeTextFromImage(""));
            if (text(etTitle).isEmpty()) {
                etTitle.setText("线性代数错题：矩阵相似与对角化");
            }
            if (text(etTags).isEmpty()) {
                etTags.setText("矩阵相似,特征值,对角化");
            }
        });
        btnSave.setOnClickListener(v -> saveMaterial());
    }

    private void openImagePicker() {
        // ACTION_OPEN_DOCUMENT 打开系统文件/相册选择器，返回的是可持久化授权的 Uri。
        // 这样不需要自己管理复杂的 Android 13 图片权限，也能兼容较多系统版本。
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        try {
            imagePickerLauncher.launch(intent);
        } catch (Exception e) {
            Toast.makeText(this, "无法打开系统相册：" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void persistUriPermission(Intent data, Uri uri) {
        int flags = data.getFlags() & Intent.FLAG_GRANT_READ_URI_PERMISSION;
        if (flags == 0) {
            return;
        }
        try {
            getContentResolver().takePersistableUriPermission(uri, flags);
        } catch (SecurityException e) {
            // 某些相册不支持持久化授权，当前会话仍可读取，保存时继续保留 Uri 字符串。
            // 即使这里失败，也不影响 OCR 和保存文本，只是后续原图可能无法长期显示。
        }
    }

    private void showSelectedImage(Uri uri) {
        // 选择图片后同时做两件事：
        // 1. 保存 Uri 字符串，后面写入 StudyMaterial.imagePath。
        // 2. 解码 Bitmap，交给 ImageView 做预览，同时给 ML Kit 做整图 OCR。
        selectedImageUri = uri;
        try {
            selectedBitmap = decodeBitmapFromUri(uri);
            if (selectedBitmap == null) {
                throw new IllegalStateException("图片无法解码");
            }
            ivPreview.setImageBitmap(selectedBitmap);
            ivPreview.setVisibility(View.VISIBLE);
            tvPreviewPlaceholder.setVisibility(View.GONE);
            tvPreviewHint.setVisibility(View.VISIBLE);
            Toast.makeText(this, "图片已选择，可直接开始 OCR", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            selectedImageUri = null;
            selectedBitmap = null;
            ivPreview.setVisibility(View.GONE);
            tvPreviewPlaceholder.setVisibility(View.VISIBLE);
            tvPreviewHint.setVisibility(View.GONE);
            Toast.makeText(this, "图片预览失败，请重新选择", Toast.LENGTH_SHORT).show();
        }
    }

    private void startRealOcr() {
        // 没选图时直接提示，不进入 ML Kit，避免空 Uri 崩溃。
        if (selectedImageUri == null || selectedBitmap == null) {
            Toast.makeText(this, "请先选择图片", Toast.LENGTH_SHORT).show();
            return;
        }
        btnRealOcr.setEnabled(false);
        btnRealOcr.setText("识别中...");
        // 当前阶段识别整张图片。框选功能受模拟器触摸板操作影响较大，已移除以保证验收稳定。
        Bitmap bitmapForOcr = selectedBitmap;
        int ocrMode = getSelectedOcrMode();
        ocrManager.recognizeTextFromBitmap(bitmapForOcr, ocrMode, new OcrResultCallback() {
            @Override
            public void onSuccess(String text) {
                btnRealOcr.setEnabled(true);
                btnRealOcr.setText("开始 OCR");
                String result = text == null ? "" : text.trim();
                if (result.isEmpty()) {
                    // OCR 识别到空文本时不清空用户已有输入，给用户继续编辑或模拟 OCR 的机会。
                    Toast.makeText(ScanActivity.this, "未识别到文字，可重试或使用模拟 OCR", Toast.LENGTH_SHORT).show();
                    return;
                }
                etContent.setText(result);
                fillTitleFromOcrIfNeeded(result);
                Toast.makeText(ScanActivity.this, "识别完成", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception e) {
                btnRealOcr.setEnabled(true);
                btnRealOcr.setText("开始 OCR");
                Toast.makeText(ScanActivity.this, "识别失败，请重试或使用模拟 OCR", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fillTitleFromOcrIfNeeded(String text) {
        // 用户还没填标题时，自动取 OCR 前 15 个字作为标题，减少录入成本。
        if (!text(etTitle).isEmpty()) {
            return;
        }
        String compact = text.replace('\n', ' ').trim();
        if (compact.isEmpty()) {
            return;
        }
        int end = Math.min(15, compact.length());
        etTitle.setText(compact.substring(0, end));
    }

    private Bitmap decodeBitmapFromUri(Uri uri) throws Exception {
        // 从系统 Uri 读取图片流并解码成 Bitmap。try-with-resources 会自动关闭输入流。
        try (InputStream inputStream = getContentResolver().openInputStream(uri)) {
            return BitmapFactory.decodeStream(inputStream);
        }
    }

    private int getSelectedOcrMode() {
        // Spinner 第 0 项是中文 OCR，第 1 项是通用 OCR。
        return spOcrMode.getSelectedItemPosition() == 0 ? OcrManager.MODE_CHINESE : OcrManager.MODE_LATIN;
    }

    private void saveMaterial() {
        // 保存前做最基本的表单校验：标题和 OCR 内容不能为空。
        String title = text(etTitle);
        String content = text(etContent);
        if (title.isEmpty()) {
            Toast.makeText(this, "请填写资料标题", Toast.LENGTH_SHORT).show();
            return;
        }
        if (content.isEmpty()) {
            Toast.makeText(this, "请先输入或模拟 OCR 识别内容", Toast.LENGTH_SHORT).show();
            return;
        }

        // 将页面输入组装成实体对象，再交给 Repository 写入 SQLite。
        // Activity 不直接写 SQL，是为了保持页面层和数据层分离。
        StudyMaterial material = new StudyMaterial();
        material.setTitle(title);
        material.setContent(content);
        material.setSubject((String) spSubject.getSelectedItem());
        material.setSourceType((String) spSourceType.getSelectedItem());
        material.setTags(text(etTags));
        // imagePath 保存相册 Uri 字符串，详情页通过 Uri 显示原图。
        material.setImagePath(selectedImageUri == null ? "" : selectedImageUri.toString());
        material.setIsWrongQuestion(cbWrong.isChecked() ? 1 : 0);
        material.setDifficulty(cbWrong.isChecked() ? 2 : 1);
        material.setNote(text(etNote));
        material.setWrongReason(text(etWrongReason));
        material.setMastered(0);
        material.setNextReviewTime(0);
        material.setReviewCount(0);
        material.setLastReviewTime(0);

        long id = repository.insertMaterial(material);
        if (id > 0) {
            Toast.makeText(this, "已保存到本地知识库", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "保存失败，请重试", Toast.LENGTH_SHORT).show();
        }
    }

    private String text(TextInputEditText editText) {
        // 小工具方法：统一处理输入框为空的情况，避免到处写 null 判断。
        return editText.getText() == null ? "" : editText.getText().toString().trim();
    }
}
