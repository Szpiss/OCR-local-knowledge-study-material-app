package com.example.smartscan.data.entity;

/**
 * 学习资料实体类，对应 SQLite 中的 StudyMaterial 表。
 *
 * 答辩重点：
 * 1. 一条 StudyMaterial 既可以是普通资料，也可以是错题资料。
 * 2. 是否进入错题中心由 isWrongQuestion 字段决定。
 * 3. imagePath 保存原图 Uri，content 保存 OCR 文本。
 */
public class StudyMaterial {
    private long id;
    private String title;
    private String content;
    private String subject;
    private String sourceType;
    private String tags;
    private String imagePath;       // 原图 Uri 字符串，详情页用它显示原图。
    private int isWrongQuestion;    // 0 表示普通资料，1 表示错题复习中心可见。
    private int difficulty;         // 错题难度，1-5 级。
    private String note;            // 用户备注。
    private String wrongReason;     // 错误原因，帮助用户复盘错题。
    private int mastered;           // 0 未掌握，1 已掌握。
    private long nextReviewTime;    // 下次复习时间，当前预留给后续提醒功能。
    private long createTime;
    private long updateTime;
    private int reviewCount;
    private long lastReviewTime;

    public StudyMaterial() {
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public String getSourceType() { return sourceType; }
    public void setSourceType(String sourceType) { this.sourceType = sourceType; }
    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }
    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    public int getIsWrongQuestion() { return isWrongQuestion; }
    public void setIsWrongQuestion(int isWrongQuestion) { this.isWrongQuestion = isWrongQuestion; }
    public int getDifficulty() { return difficulty; }
    public void setDifficulty(int difficulty) { this.difficulty = difficulty; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public String getWrongReason() { return wrongReason; }
    public void setWrongReason(String wrongReason) { this.wrongReason = wrongReason; }
    public int getMastered() { return mastered; }
    public void setMastered(int mastered) { this.mastered = mastered; }
    public long getNextReviewTime() { return nextReviewTime; }
    public void setNextReviewTime(long nextReviewTime) { this.nextReviewTime = nextReviewTime; }
    public long getCreateTime() { return createTime; }
    public void setCreateTime(long createTime) { this.createTime = createTime; }
    public long getUpdateTime() { return updateTime; }
    public void setUpdateTime(long updateTime) { this.updateTime = updateTime; }
    public int getReviewCount() { return reviewCount; }
    public void setReviewCount(int reviewCount) { this.reviewCount = reviewCount; }
    public long getLastReviewTime() { return lastReviewTime; }
    public void setLastReviewTime(long lastReviewTime) { this.lastReviewTime = lastReviewTime; }
}
