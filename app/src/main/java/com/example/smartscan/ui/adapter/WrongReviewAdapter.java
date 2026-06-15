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
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

/**
 * 错题复习中心适配器，提供复习一次、标记掌握、取消错题等训练动作。
 *
 * 答辩重点：
 * 这个 Adapter 和知识库 Adapter 不同，说明错题中心不是简单换标题，而是有复习动作。
 */
public class WrongReviewAdapter extends RecyclerView.Adapter<WrongReviewAdapter.ViewHolder> {
    public interface OnWrongActionListener {
        // 具体数据更新仍由 Fragment/Repository 处理，Adapter 不直接操作数据库。
        void onReviewOnce(StudyMaterial material);
        void onMarkMastered(StudyMaterial material);
        void onCancelWrong(StudyMaterial material);
    }

    private final Context context;
    private final List<StudyMaterial> data = new ArrayList<>();
    private final OnWrongActionListener listener;

    public WrongReviewAdapter(Context context, OnWrongActionListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void submitList(List<StudyMaterial> materials) {
        data.clear();
        if (materials != null) {
            data.addAll(materials);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wrong_material, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // 错题列表项展示复习相关信息：难度、复习次数、上次复习、掌握状态、错误原因。
        StudyMaterial material = data.get(position);
        holder.tvTitle.setText(emptyToDefault(material.getTitle(), "未命名错题"));
        holder.tvMeta.setText("科目：" + emptyToDefault(material.getSubject(), "其他")
                + "  |  难度：" + material.getDifficulty()
                + "  |  复习：" + material.getReviewCount() + "次"
                + "  |  上次：" + DateUtils.formatShort(material.getLastReviewTime())
                + "  |  " + (material.getMastered() == 1 ? "已掌握" : "未掌握"));
        holder.tvReason.setText("错误原因：" + emptyToDefault(material.getWrongReason(), "待补充"));
        holder.btnMastered.setEnabled(material.getMastered() != 1);
        holder.btnMastered.setText(material.getMastered() == 1 ? "已掌握" : "已掌握");
        holder.btnReviewOnce.setOnClickListener(v -> listener.onReviewOnce(material));
        holder.btnMastered.setOnClickListener(v -> listener.onMarkMastered(material));
        holder.btnCancelWrong.setOnClickListener(v -> listener.onCancelWrong(material));
        holder.btnDetail.setOnClickListener(v -> openDetail(material));
        holder.itemView.setOnClickListener(v -> openDetail(material));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    private void openDetail(StudyMaterial material) {
        Intent intent = new Intent(context, MaterialDetailActivity.class);
        intent.putExtra(MaterialDetailActivity.EXTRA_MATERIAL_ID, material.getId());
        context.startActivity(intent);
    }

    private String emptyToDefault(String value, String defaultValue) {
        return value == null || value.trim().isEmpty() ? defaultValue : value;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        TextView tvMeta;
        TextView tvReason;
        MaterialButton btnReviewOnce;
        MaterialButton btnMastered;
        MaterialButton btnCancelWrong;
        MaterialButton btnDetail;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_wrong_title);
            tvMeta = itemView.findViewById(R.id.tv_wrong_meta);
            tvReason = itemView.findViewById(R.id.tv_wrong_reason);
            btnReviewOnce = itemView.findViewById(R.id.btn_review_once);
            btnMastered = itemView.findViewById(R.id.btn_mastered);
            btnCancelWrong = itemView.findViewById(R.id.btn_cancel_wrong);
            btnDetail = itemView.findViewById(R.id.btn_detail);
        }
    }
}
