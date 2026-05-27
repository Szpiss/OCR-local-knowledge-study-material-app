package com.example.smartscan.ui.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartscan.R;
import com.example.smartscan.data.entity.StudyMaterial;
import com.example.smartscan.data.entity.WrongStatistics;
import com.example.smartscan.data.repository.StudyMaterialRepository;
import com.example.smartscan.ui.adapter.WrongReviewAdapter;
import com.example.smartscan.util.SubjectUtils;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.List;

/**
 * 错题本 Fragment，只展示 isWrongQuestion = 1 的学习资料。
 */
public class WrongBookFragment extends Fragment {
    private StudyMaterialRepository repository;
    private WrongReviewAdapter adapter;
    private TextView tvEmpty;
    private View cardTotal;
    private View cardPending;
    private View cardMastered;
    private String currentSubject = SubjectUtils.ALL;
    private int currentDifficulty = 0;
    private int currentMastered = -1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wrong_book, container, false);
        repository = new StudyMaterialRepository(requireContext());
        adapter = new WrongReviewAdapter(requireContext(), new WrongReviewAdapter.OnWrongActionListener() {
            @Override
            public void onReviewOnce(StudyMaterial material) {
                repository.reviewOnce(material.getId());
                Toast.makeText(requireContext(), "已记录一次复习", Toast.LENGTH_SHORT).show();
                refreshList();
            }

            @Override
            public void onMarkMastered(StudyMaterial material) {
                repository.markMastered(material.getId());
                Toast.makeText(requireContext(), "已标记为掌握", Toast.LENGTH_SHORT).show();
                refreshList();
            }

            @Override
            public void onCancelWrong(StudyMaterial material) {
                confirmCancelWrong(material);
            }
        });
        initViews(view);
        setupSubjectChips(view);
        setupDifficultyChips(view);
        setupMasteredChips(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshList();
    }

    private void initViews(View view) {
        tvEmpty = view.findViewById(R.id.tv_empty_wrong);
        cardTotal = view.findViewById(R.id.card_wrong_total);
        cardPending = view.findViewById(R.id.card_wrong_pending);
        cardMastered = view.findViewById(R.id.card_wrong_mastered);
        RecyclerView recyclerView = view.findViewById(R.id.rv_wrong);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);
    }

    private void setupSubjectChips(View view) {
        ChipGroup chipGroup = view.findViewById(R.id.chip_group_wrong_subject);
        for (String subject : SubjectUtils.FILTER_SUBJECTS) {
            Chip chip = new Chip(requireContext());
            chip.setText(subject);
            chip.setCheckable(true);
            chip.setId(View.generateViewId());
            chipGroup.addView(chip);
            if (SubjectUtils.ALL.equals(subject)) {
                chip.setChecked(true);
            }
        }
        chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (!checkedIds.isEmpty()) {
                Chip checked = group.findViewById(checkedIds.get(0));
                currentSubject = checked.getText().toString();
                refreshList();
            }
        });
    }

    private void setupDifficultyChips(View view) {
        ChipGroup chipGroup = view.findViewById(R.id.chip_group_difficulty);
        for (String difficulty : SubjectUtils.FILTER_DIFFICULTIES) {
            Chip chip = new Chip(requireContext());
            chip.setText(difficulty);
            chip.setCheckable(true);
            chip.setId(View.generateViewId());
            chipGroup.addView(chip);
            if (SubjectUtils.ALL.equals(difficulty)) {
                chip.setChecked(true);
            }
        }
        chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (!checkedIds.isEmpty()) {
                Chip checked = group.findViewById(checkedIds.get(0));
                String text = checked.getText().toString();
                currentDifficulty = SubjectUtils.ALL.equals(text) ? 0 : Integer.parseInt(text.replace("难度", ""));
                refreshList();
            }
        });
    }

    private void setupMasteredChips(View view) {
        ChipGroup chipGroup = view.findViewById(R.id.chip_group_mastered);
        for (String status : SubjectUtils.FILTER_MASTERED) {
            Chip chip = new Chip(requireContext());
            chip.setText(status);
            chip.setCheckable(true);
            chip.setId(View.generateViewId());
            chipGroup.addView(chip);
            if (SubjectUtils.ALL.equals(status)) {
                chip.setChecked(true);
            }
        }
        chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (!checkedIds.isEmpty()) {
                Chip checked = group.findViewById(checkedIds.get(0));
                String text = checked.getText().toString();
                currentMastered = "已掌握".equals(text) ? 1 : ("未掌握".equals(text) ? 0 : -1);
                refreshList();
            }
        });
    }

    private void refreshList() {
        WrongStatistics statistics = repository.getWrongStatistics();
        bindStat(cardTotal, "错题总数", statistics.totalWrong);
        bindStat(cardPending, "待复习", statistics.pendingReview);
        bindStat(cardMastered, "已掌握", statistics.mastered);

        List<StudyMaterial> list = repository.getWrongMaterials(currentSubject, currentDifficulty, currentMastered);
        adapter.submitList(list);
        tvEmpty.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void bindStat(View card, String label, int value) {
        TextView tvValue = card.findViewById(R.id.tv_stat_value);
        TextView tvLabel = card.findViewById(R.id.tv_stat_label);
        tvValue.setText(String.valueOf(value));
        tvLabel.setText(label);
    }

    private void confirmCancelWrong(StudyMaterial material) {
        new AlertDialog.Builder(requireContext())
                .setTitle("取消错题标记")
                .setMessage("确定将这条资料从错题复习中心移除吗？资料仍会保留在本地知识库。")
                .setNegativeButton("取消", null)
                .setPositiveButton("确认", (dialog, which) -> {
                    repository.cancelWrong(material.getId());
                    Toast.makeText(requireContext(), "已取消错题标记", Toast.LENGTH_SHORT).show();
                    refreshList();
                })
                .show();
    }
}
