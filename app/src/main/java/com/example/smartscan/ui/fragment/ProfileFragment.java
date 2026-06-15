package com.example.smartscan.ui.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.smartscan.R;
import com.example.smartscan.data.repository.StudyMaterialRepository;
import com.example.smartscan.util.AppPreferenceManager;
import com.example.smartscan.util.SubjectUtils;
import com.example.smartscan.worker.ReminderScheduler;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

/**
 * 我的/设置 Fragment，使用 SharedPreferences 保存用户偏好。
 *
 * 答辩重点：
 * 1. 默认科目、提醒开关、提醒时间属于轻量配置，不需要放 SQLite。
 * 2. 使用 SharedPreferences 更适合保存这类简单键值对。
 * 3. 清空本地数据会调用 Repository 删除 StudyMaterial 表数据。
 */
public class ProfileFragment extends Fragment {
    private Spinner spDefaultSubject;
    private Switch swReminder;
    private TextInputEditText etReminderTime;
    private AppPreferenceManager preferenceManager;
    private StudyMaterialRepository repository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        preferenceManager = new AppPreferenceManager(requireContext());
        repository = new StudyMaterialRepository(requireContext());
        bindViews(view);
        setupSpinner();
        loadSettings();
        setupActions(view);
        return view;
    }

    private void bindViews(View view) {
        spDefaultSubject = view.findViewById(R.id.sp_default_subject);
        swReminder = view.findViewById(R.id.sw_reminder);
        etReminderTime = view.findViewById(R.id.et_reminder_time);
    }

    private void setupSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, SubjectUtils.SUBJECTS);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDefaultSubject.setAdapter(adapter);
    }

    private void loadSettings() {
        // 从 SharedPreferences 读取设置，回到页面时能看到上次保存的值。
        spDefaultSubject.setSelection(SubjectUtils.indexOf(preferenceManager.getDefaultSubject()));
        swReminder.setChecked(preferenceManager.isReminderEnabled());
        etReminderTime.setText(preferenceManager.getReminderTime());
    }

    private void setupActions(View view) {
        MaterialButton btnSaveSettings = view.findViewById(R.id.btn_save_settings);
        MaterialButton btnClearData = view.findViewById(R.id.btn_clear_data);
        btnSaveSettings.setOnClickListener(v -> saveSettings());
        btnClearData.setOnClickListener(v -> confirmClearData());
    }

    private void saveSettings() {
        // 保存用户偏好。当前提醒调度是预留接口，真实通知可在后续版本接入。
        String subject = (String) spDefaultSubject.getSelectedItem();
        String time = etReminderTime.getText() == null ? "20:30" : etReminderTime.getText().toString().trim();
        if (time.isEmpty()) {
            time = "20:30";
        }
        preferenceManager.setDefaultSubject(subject);
        preferenceManager.setReminderEnabled(swReminder.isChecked());
        preferenceManager.setReminderTime(time);
        new ReminderScheduler(requireContext()).scheduleDailyReminder(swReminder.isChecked(), time);
        Toast.makeText(requireContext(), "设置已保存", Toast.LENGTH_SHORT).show();
    }

    private void confirmClearData() {
        // 清空数据属于危险操作，因此弹窗二次确认，避免误删演示数据。
        new AlertDialog.Builder(requireContext())
                .setTitle("清空本地数据")
                .setMessage("此操作会删除所有学习资料和错题记录，确定继续吗？")
                .setNegativeButton("取消", null)
                .setPositiveButton("清空", (dialog, which) -> {
                    repository.clearAllData();
                    Toast.makeText(requireContext(), "本地数据已清空", Toast.LENGTH_SHORT).show();
                })
                .show();
    }
}
