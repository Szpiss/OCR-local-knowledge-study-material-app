package com.example.smartscan.data.entity;

/**
 * 首页统计数据实体，便于 Fragment 只关心展示逻辑。
 *
 * Repository 负责查询数据库并填充这些数值，HomeFragment 只负责显示。
 */
public class Statistics {
    public int totalCount;
    public int wrongCount;
    public int todayCount;
    public int reviewCount;
}
