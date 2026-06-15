package com.example.smartscan.ui.activity;

import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
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
 *
 * 答辩重点：
 * 1. 这里体现 CRUD 中的 Read、Update、Delete。
 * 2. 普通资料和错题资料共用同一个详情页，错题字段根据 isWrongQuestion 动态显示。
 * 3. 原图通过 imagePath 保存的 Uri 显示，便于核对 OCR 文本。
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
    private TextView tvNoImage;
    private ImageView ivOriginalImage;
    private View cardOriginalImage;
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
        tvNoImage = findViewById(R.id.tv_no_image);
        ivOriginalImage = findViewById(R.id.iv_original_image);
        cardOriginalImage = findViewById(R.id.card_original_image);
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
        // 列表页跳转详情页时只传 id，详情页再从 SQLite 查询完整数据。
        // 这样避免 Intent 携带大量文本或图片 Uri 数据。
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
        // 如果资料是通过相册 OCR 保存的，这里会显示原图；模拟 OCR 数据则显示暂无原图。
        showOriginalImage(material.getImagePath());
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
        // 保存修改：把页面控件中的值重新写回实体，再调用 Repository 更新数据库。
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
        // 只有仍然是错题时，已掌握状态才有意义；取消错题时自动清为未掌握。
        material.setMastered(cbWrong.isChecked() && cbMastered.isChecked() ? 1 : 0);
        repository.updateMaterial(material);
        Toast.makeText(this, "资料已更新", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void confirmDelete() {
        // 删除是高风险操作，因此先弹二次确认，避免误删资料。
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
        // 错题相关字段只在“标记为错题”时展示。
        // 这样详情页同时服务普通资料和错题资料，界面不会显得冗余。
        boolean isWrong = cbWrong.isChecked();
        layoutWrongFields.setVisibility(isWrong ? android.view.View.VISIBLE : android.view.View.GONE);
        if (isWrong && material != null) {
            tvWrongInfo.setText("复习次数：" + material.getReviewCount()
                    + "    上次复习：" + DateUtils.formatShort(material.getLastReviewTime())
                    + "    状态：" + (material.getMastered() == 1 ? "已掌握" : "未掌握"));
        }
    }

    private void showOriginalImage(String imagePath) {
        // imagePath 保存的是系统返回的 Uri 字符串。
        // 如果 Uri 为空或失效，页面只隐藏原图区域，不影响 OCR 文本查看。
        if (imagePath == null || imagePath.trim().isEmpty()) {
            cardOriginalImage.setVisibility(View.GONE);
            tvNoImage.setVisibility(View.VISIBLE);
            return;
        }
        try {
            ivOriginalImage.setImageURI(Uri.parse(imagePath));
            cardOriginalImage.setVisibility(View.VISIBLE);
            tvNoImage.setVisibility(View.GONE);
        } catch (Exception e) {
            cardOriginalImage.setVisibility(View.GONE);
            tvNoImage.setVisibility(View.VISIBLE);
            tvNoImage.setText("原图无法读取");
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
