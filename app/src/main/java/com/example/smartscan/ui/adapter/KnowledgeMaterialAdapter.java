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
 * 本地知识库列表适配器，突出资料管理入口和错题标记入口。
 *
 * 答辩重点：
 * RecyclerView 本身只负责高效展示列表；每一行怎么展示、按钮怎么响应，由 Adapter 完成。
 */
public class KnowledgeMaterialAdapter extends RecyclerView.Adapter<KnowledgeMaterialAdapter.ViewHolder> {
    public interface OnMarkWrongClickListener {
        // 通过接口把“标记错题”的具体数据库操作交给 Fragment，Adapter 只负责响应点击。
        void onMarkWrong(StudyMaterial material);
    }

    private final Context context;
    private final List<StudyMaterial> data = new ArrayList<>();
    private final OnMarkWrongClickListener listener;

    public KnowledgeMaterialAdapter(Context context, OnMarkWrongClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void submitList(List<StudyMaterial> materials) {
        // Fragment 查询到新数据后调用 submitList 刷新 RecyclerView。
        data.clear();
        if (materials != null) {
            data.addAll(materials);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_knowledge_material, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // onBindViewHolder 负责把 StudyMaterial 的字段绑定到列表项 UI 上。
        StudyMaterial material = data.get(position);
        holder.tvTitle.setText(emptyToDefault(material.getTitle(), "未命名资料"));
        holder.tvSummary.setText(makeSummary(material.getContent()));
        holder.tvMeta.setText("科目：" + emptyToDefault(material.getSubject(), "其他")
                + "  |  标签：" + emptyToDefault(material.getTags(), "无标签")
                + "  |  来源：" + emptyToDefault(material.getSourceType(), "其他")
                + "  |  " + DateUtils.formatShort(material.getCreateTime()));
        boolean isWrong = material.getIsWrongQuestion() == 1;
        // 如果已经是错题，就显示“错题”徽章，并禁用重复标记。
        holder.tvWrongBadge.setVisibility(isWrong ? View.VISIBLE : View.GONE);
        holder.btnMarkWrong.setEnabled(!isWrong);
        holder.btnMarkWrong.setText(isWrong ? "已是错题" : "标记为错题");
        holder.btnDetail.setOnClickListener(v -> openDetail(material));
        holder.itemView.setOnClickListener(v -> openDetail(material));
        holder.btnMarkWrong.setOnClickListener(v -> {
            if (listener != null) {
                listener.onMarkWrong(material);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    private void openDetail(StudyMaterial material) {
        // 进入详情页只传主键 id，详情页再查询完整数据。
        Intent intent = new Intent(context, MaterialDetailActivity.class);
        intent.putExtra(MaterialDetailActivity.EXTRA_MATERIAL_ID, material.getId());
        context.startActivity(intent);
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

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        TextView tvSummary;
        TextView tvMeta;
        TextView tvWrongBadge;
        MaterialButton btnMarkWrong;
        MaterialButton btnDetail;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvSummary = itemView.findViewById(R.id.tv_summary);
            tvMeta = itemView.findViewById(R.id.tv_meta);
            tvWrongBadge = itemView.findViewById(R.id.tv_wrong_badge);
            btnMarkWrong = itemView.findViewById(R.id.btn_mark_wrong);
            btnDetail = itemView.findViewById(R.id.btn_detail);
        }
    }
}
