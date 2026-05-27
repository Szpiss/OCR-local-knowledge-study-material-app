package com.example.smartscan.util;

/**
 * 科目常量工具类，统一页面筛选与下拉框内容。
 */
public class SubjectUtils {
    public static final String ALL = "全部";
    public static final String[] SUBJECTS = {"高数", "线代", "英语", "408", "其他"};
    public static final String[] FILTER_SUBJECTS = {"全部", "高数", "线代", "英语", "408", "其他"};
    public static final String[] SOURCE_TYPES = {"课堂笔记", "试卷", "PPT", "课本", "其他"};
    public static final String[] FILTER_SOURCE_TYPES = {"全部", "课堂笔记", "试卷", "PPT", "课本", "其他"};
    public static final String[] FILTER_DIFFICULTIES = {"全部", "难度1", "难度2", "难度3", "难度4", "难度5"};
    public static final String[] FILTER_MASTERED = {"全部", "未掌握", "已掌握"};

    public static int indexOf(String subject) {
        if (subject == null) {
            return 0;
        }
        for (int i = 0; i < SUBJECTS.length; i++) {
            if (SUBJECTS[i].equals(subject)) {
                return i;
            }
        }
        return 0;
    }
}
