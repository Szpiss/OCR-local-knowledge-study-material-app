package com.example.smartscan.ui.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartscan.R;
import com.example.smartscan.data.entity.StudyMaterial;
import com.example.smartscan.data.repository.StudyMaterialRepository;
import com.example.smartscan.util.DateUtils;
import com.example.smartscan.util.SubjectUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

/**
 * 资料详情页面，支持查看、编辑、删除以及标记/取消错题。
 */
public class MaterialDetailActivity extends AppCompatActivity {
    public static final String EXTRA_MATERIAL_ID = "material_id";

    private TextInputEditText etTitle;
    private TextInputEditText etContent;
    private TextInputEditText etTags;
    private TextInputEditText etNote;
    private TextInputEditText etWrongReason;
    private Spinner spSubject;
    private Spinner spSourceType;
    private Spinner spDifficulty;
    private CheckBox cbWrong;
    private CheckBox cbMastered;
    private TextView tvCreateTime;
    private TextView tvWrongInfo;
    private LinearLayout layoutWrongFields;
    private StudyMaterialRepository repository;
    private StudyMaterial material;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material_detail);
        repository = new StudyMaterialRepository(this);
        bindViews();
        setupSpinner();
        loadMaterial();
        setupActions();
    }

    private void bindViews() {
        etTitle = findViewById(R.id.et_detail_title);
        etContent = findViewById(R.id.et_detail_content);
        etTags = findViewById(R.id.et_detail_tags);
        etNote = findViewById(R.id.et_detail_note);
        etWrongReason = findViewById(R.id.et_detail_wrong_reason);
        spSubject = findViewById(R.id.sp_detail_subject);
        spSourceType = findViewById(R.id.sp_detail_source_type);
        spDifficulty = findViewById(R.id.sp_detail_difficulty);
        cbWrong = findViewById(R.id.cb_detail_wrong);
        cbMastered = findViewById(R.id.cb_detail_mastered);
        tvCreateTime = findViewById(R.id.tv_create_time);
        tvWrongInfo = findViewById(R.id.tv_wrong_info);
        layoutWrongFields = findViewById(R.id.layout_wrong_fields);
    }

    private void setupSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, SubjectUtils.SUBJECTS);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSubject.setAdapter(adapter);
        ArrayAdapter<String> sourceAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, SubjectUtils.SOURCE_TYPES);
        sourceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSourceType.setAdapter(sourceAdapter);
        ArrayAdapter<String> difficultyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                new String[]{"难度1", "难度2", "难度3", "难度4", "难度5"});
        difficultyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDifficulty.setAdapter(difficultyAdapter);
    }

    private void loadMaterial() {
        long id = getIntent().getLongExtra(EXTRA_MATERIAL_ID, -1);
        material = repository.getMaterialById(id);
        if (material == null) {
            Toast.makeText(this, "资料不存在或已被删除", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        etTitle.setText(material.getTitle());
        etContent.setText(material.getContent());
        etTags.setText(material.getTags());
        etNote.setText(material.getNote());
        etWrongReason.setText(material.getWrongReason());
        spSubject.setSelection(SubjectUtils.indexOf(material.getSubject()));
        spSourceType.setSelection(indexOf(SubjectUtils.SOURCE_TYPES, material.getSourceType()));
        spDifficulty.setSelection(Math.max(0, Math.min(4, material.getDifficulty() - 1)));
        cbWrong.setChecked(material.getIsWrongQuestion() == 1);
        cbMastered.setChecked(material.getMastered() == 1);
        tvCreateTime.setText("创建时间：" + DateUtils.formatDateTime(material.getCreateTime())
                + "    复习次数：" + material.getReviewCount());
        refreshWrongFieldsVisibility();
    }

    private void setupActions() {
        MaterialButton btnUpdate = findViewById(R.id.btn_update);
        MaterialButton btnDelete = findViewById(R.id.btn_delete);
        btnUpdate.setOnClickListener(v -> updateMaterial());
        btnDelete.setOnClickListener(v -> confirmDelete());
        cbWrong.setOnCheckedChangeListener((buttonView, isChecked) -> refreshWrongFieldsVisibility());
    }

    private void updateMaterial() {
        if (material == null) {
            return;
        }
        String title = text(etTitle);
        if (title.isEmpty()) {
            Toast.makeText(this, "标题不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        material.setTitle(title);
        material.setContent(text(etContent));
        material.setSubject((String) spSubject.getSelectedItem());
        material.setSourceType((String) spSourceType.getSelectedItem());
        material.setTags(text(etTags));
        material.setNote(text(etNote));
        material.setIsWrongQuestion(cbWrong.isChecked() ? 1 : 0);
        material.setDifficulty(spDifficulty.getSelectedItemPosition() + 1);
        material.setWrongReason(text(etWrongReason));
        material.setMastered(cbWrong.isChecked() && cbMastered.isChecked() ? 1 : 0);
        repository.updateMaterial(material);
        Toast.makeText(this, "资料已更新", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("删除资料")
                .setMessage("确定删除这条学习资料吗？")
                .setNegativeButton("取消", null)
                .setPositiveButton("删除", (dialog, which) -> {
                    repository.deleteMaterial(material.getId());
                    Toast.makeText(this, "资料已删除", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .show();
    }

    private String text(TextInputEditText editText) {
        return editText.getText() == null ? "" : editText.getText().toString().trim();
    }

    private void refreshWrongFieldsVisibility() {
        boolean isWrong = cbWrong.isChecked();
        layoutWrongFields.setVisibility(isWrong ? android.view.View.VISIBLE : android.view.View.GONE);
        if (isWrong && material != null) {
            tvWrongInfo.setText("复习次数：" + material.getReviewCount()
                    + "    上次复习：" + DateUtils.formatShort(material.getLastReviewTime())
                    + "    状态：" + (material.getMastered() == 1 ? "已掌握" : "未掌握"));
        }
    }

    private int indexOf(String[] values, String value) {
        if (value == null) {
            return 0;
        }
        for (int i = 0; i < values.length; i++) {
            if (values[i].equals(value)) {
                return i;
            }
        }
        return 0;
    }
}
