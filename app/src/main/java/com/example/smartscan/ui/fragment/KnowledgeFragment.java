package com.example.smartscan.ui.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.example.smartscan.data.repository.StudyMaterialRepository;
import com.example.smartscan.ui.adapter.KnowledgeMaterialAdapter;
import com.example.smartscan.util.SubjectUtils;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

/**
 * 本地知识库 Fragment，支持关键词搜索和科目筛选。
 */
public class KnowledgeFragment extends Fragment {
    private StudyMaterialRepository repository;
    private KnowledgeMaterialAdapter adapter;
    private TextInputEditText etSearch;
    private TextInputEditText etTagFilter;
    private TextView tvEmpty;
    private String currentSubject = SubjectUtils.ALL;
    private String currentSourceType = SubjectUtils.ALL;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_knowledge, container, false);
        repository = new StudyMaterialRepository(requireContext());
        adapter = new KnowledgeMaterialAdapter(requireContext(), material -> {
            repository.markAsWrong(material.getId());
            Toast.makeText(requireContext(), "已标记为错题，可在错题复习中心查看", Toast.LENGTH_SHORT).show();
            refreshList();
        });
        initViews(view);
        setupSubjectChips(view);
        setupSourceChips(view);
        setupSearch();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshList();
    }

    private void initViews(View view) {
        etSearch = view.findViewById(R.id.et_search);
        etTagFilter = view.findViewById(R.id.et_tag_filter);
        tvEmpty = view.findViewById(R.id.tv_empty);
        RecyclerView recyclerView = view.findViewById(R.id.rv_materials);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);
    }

    private void setupSourceChips(View view) {
        ChipGroup chipGroup = view.findViewById(R.id.chip_group_source);
        for (String sourceType : SubjectUtils.FILTER_SOURCE_TYPES) {
            Chip chip = new Chip(requireContext());
            chip.setText(sourceType);
            chip.setCheckable(true);
            chip.setId(View.generateViewId());
            chipGroup.addView(chip);
            if (SubjectUtils.ALL.equals(sourceType)) {
                chip.setChecked(true);
            }
        }
        chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (!checkedIds.isEmpty()) {
                Chip checked = group.findViewById(checkedIds.get(0));
                currentSourceType = checked.getText().toString();
                refreshList();
            }
        });
    }

    private void setupSubjectChips(View view) {
        ChipGroup chipGroup = view.findViewById(R.id.chip_group_subject);
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

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { refreshList(); }
            @Override public void afterTextChanged(Editable s) { }
        });
        etTagFilter.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { refreshList(); }
            @Override public void afterTextChanged(Editable s) { }
        });
    }

    private void refreshList() {
        String keyword = etSearch == null || etSearch.getText() == null ? "" : etSearch.getText().toString();
        String tag = etTagFilter == null || etTagFilter.getText() == null ? "" : etTagFilter.getText().toString();
        List<StudyMaterial> list = repository.searchMaterials(keyword, currentSubject, tag, currentSourceType);
        adapter.submitList(list);
        tvEmpty.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
    }
}
