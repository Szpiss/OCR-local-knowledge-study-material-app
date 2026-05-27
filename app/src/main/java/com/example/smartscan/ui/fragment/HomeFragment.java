package com.example.smartscan.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartscan.R;
import com.example.smartscan.data.entity.Statistics;
import com.example.smartscan.data.entity.StudyMaterial;
import com.example.smartscan.data.repository.StudyMaterialRepository;
import com.example.smartscan.ui.adapter.MaterialAdapter;

import java.util.List;

/**
 * 首页 Fragment，展示欢迎语、统计卡片和最近扫描资料。
 */
public class HomeFragment extends Fragment {
    private StudyMaterialRepository repository;
    private MaterialAdapter adapter;
    private TextView tvEmpty;
    private View cardTotal;
    private View cardWrong;
    private View cardToday;
    private View cardReview;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        repository = new StudyMaterialRepository(requireContext());
        adapter = new MaterialAdapter(requireContext());
        initViews(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshData();
    }

    private void initViews(View view) {
        cardTotal = view.findViewById(R.id.card_total);
        cardWrong = view.findViewById(R.id.card_wrong);
        cardToday = view.findViewById(R.id.card_today);
        cardReview = view.findViewById(R.id.card_review);
        tvEmpty = view.findViewById(R.id.tv_empty_home);
        RecyclerView recyclerView = view.findViewById(R.id.rv_recent);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);
    }

    private void refreshData() {
        Statistics statistics = repository.getStatistics();
        bindStat(cardTotal, "总资料数", statistics.totalCount);
        bindStat(cardWrong, "错题数", statistics.wrongCount);
        bindStat(cardToday, "今日新增", statistics.todayCount);
        bindStat(cardReview, "复习次数", statistics.reviewCount);

        List<StudyMaterial> recent = repository.getRecentMaterials(5);
        adapter.submitList(recent);
        tvEmpty.setVisibility(recent.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void bindStat(View card, String label, int value) {
        TextView tvValue = card.findViewById(R.id.tv_stat_value);
        TextView tvLabel = card.findViewById(R.id.tv_stat_label);
        tvValue.setText(String.valueOf(value));
        tvLabel.setText(label);
    }
}
