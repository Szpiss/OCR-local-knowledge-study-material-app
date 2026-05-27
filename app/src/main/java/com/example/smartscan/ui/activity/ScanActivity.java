package com.example.smartscan.ui.activity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartscan.R;
import com.example.smartscan.data.entity.StudyMaterial;
import com.example.smartscan.data.repository.StudyMaterialRepository;
import com.example.smartscan.ocr.OcrManager;
import com.example.smartscan.util.AppPreferenceManager;
import com.example.smartscan.util.SubjectUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

/**
 * 扫描录入页面。第一阶段使用模拟 OCR 文本，后续可扩展图片选择、CameraX 和 ML Kit。
 */
public class ScanActivity extends AppCompatActivity {
    private TextInputEditText etContent;
    private TextInputEditText etTitle;
    private TextInputEditText etTags;
    private TextInputEditText etNote;
    private TextInputEditText etWrongReason;
    private Spinner spSubject;
    private Spinner spSourceType;
    private CheckBox cbWrong;
    private StudyMaterialRepository repository;
    private OcrManager ocrManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        repository = new StudyMaterialRepository(this);
        ocrManager = new OcrManager();
        bindViews();
        setupSubjectSpinner();
        setupActions();
    }

    private void bindViews() {
        etContent = findViewById(R.id.et_content);
        etTitle = findViewById(R.id.et_title);
        etTags = findViewById(R.id.et_tags);
        etNote = findViewById(R.id.et_note);
        etWrongReason = findViewById(R.id.et_wrong_reason);
        spSubject = findViewById(R.id.sp_subject);
        spSourceType = findViewById(R.id.sp_source_type);
        cbWrong = findViewById(R.id.cb_wrong);
    }

    private void setupSubjectSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, SubjectUtils.SUBJECTS);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSubject.setAdapter(adapter);
        ArrayAdapter<String> sourceAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, SubjectUtils.SOURCE_TYPES);
        sourceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSourceType.setAdapter(sourceAdapter);
        String defaultSubject = new AppPreferenceManager(this).getDefaultSubject();
        spSubject.setSelection(SubjectUtils.indexOf(defaultSubject));
    }

    private void setupActions() {
        MaterialButton btnChooseImage = findViewById(R.id.btn_choose_image);
        MaterialButton btnMockOcr = findViewById(R.id.btn_mock_ocr);
        MaterialButton btnSave = findViewById(R.id.btn_save);

        btnChooseImage.setOnClickListener(v -> Toast.makeText(this, "第一阶段暂未接入相册，已预留接口", Toast.LENGTH_SHORT).show());
        btnMockOcr.setOnClickListener(v -> {
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

    private void saveMaterial() {
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

        StudyMaterial material = new StudyMaterial();
        material.setTitle(title);
        material.setContent(content);
        material.setSubject((String) spSubject.getSelectedItem());
        material.setSourceType((String) spSourceType.getSelectedItem());
        material.setTags(text(etTags));
        material.setImagePath("");
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
        return editText.getText() == null ? "" : editText.getText().toString().trim();
    }
}
