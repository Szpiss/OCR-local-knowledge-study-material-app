package com.example.smartscan.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartscan.R;
import com.example.smartscan.data.entity.StudyMaterial;
import com.example.smartscan.ui.activity.MaterialDetailActivity;
import com.example.smartscan.util.DateUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 学习资料 RecyclerView 适配器，负责列表项展示与详情页跳转。
 *
 * 这个 Adapter 主要用于首页“最近扫描资料”列表。
 * 知识库和错题中心因为业务动作更多，分别使用 KnowledgeMaterialAdapter 和 WrongReviewAdapter。
 */
public class MaterialAdapter extends RecyclerView.Adapter<MaterialAdapter.MaterialViewHolder> {
    private final Context context;
    private final List<StudyMaterial> data = new ArrayList<>();

    public MaterialAdapter(Context context) {
        this.context = context;
    }

    public void submitList(List<StudyMaterial> materials) {
        // 简单项目里直接清空再刷新即可；如果数据很多，后续可换成 DiffUtil 优化性能。
        data.clear();
        if (materials != null) {
            data.addAll(materials);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MaterialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_material, parent, false);
        return new MaterialViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MaterialViewHolder holder, int position) {
        // 首页列表只展示核心摘要：标题、科目、标签、时间、是否错题。
        StudyMaterial material = data.get(position);
        holder.tvTitle.setText(emptyToDefault(material.getTitle(), "未命名资料"));
        holder.tvSummary.setText(makeSummary(material.getContent()));
        holder.tvSubject.setText(emptyToDefault(material.getSubject(), "其他"));
        holder.tvTags.setText(emptyToDefault(material.getTags(), "无标签"));
        holder.tvTime.setText(DateUtils.formatShort(material.getCreateTime()));
        holder.tvWrongBadge.setVisibility(material.getIsWrongQuestion() == 1 ? View.VISIBLE : View.GONE);
        holder.itemView.setOnClickListener(v -> {
            // 点击最近资料直接进入详情页，便于老师验收页面跳转。
            Intent intent = new Intent(context, MaterialDetailActivity.class);
            intent.putExtra(MaterialDetailActivity.EXTRA_MATERIAL_ID, material.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    private String makeSummary(String content) {
        if (content == null || content.trim().isEmpty()) {
            return "暂无 OCR 文本";
        }
        return content.replace('\n', ' ').trim();
    }

    private String emptyToDefault(String value, String defaultValue) {
        return value == null || value.trim().isEmpty() ? defaultValue : value;
    }

    static class MaterialViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        TextView tvSummary;
        TextView tvSubject;
        TextView tvTags;
        TextView tvTime;
        TextView tvWrongBadge;

        MaterialViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvSummary = itemView.findViewById(R.id.tv_summary);
            tvSubject = itemView.findViewById(R.id.tv_subject);
            tvTags = itemView.findViewById(R.id.tv_tags);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvWrongBadge = itemView.findViewById(R.id.tv_wrong_badge);
        }
    }
}
