package com.example.smartscan.data.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.smartscan.data.db.DBHelper;
import com.example.smartscan.data.entity.Statistics;
import com.example.smartscan.data.entity.StudyMaterial;
import com.example.smartscan.data.entity.WrongStatistics;
import com.example.smartscan.util.DateUtils;
import com.example.smartscan.util.SubjectUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 学习资料仓库类，封装增删改查，避免 Activity/Fragment 直接操作数据库。
 */
public class StudyMaterialRepository {
    private final DBHelper dbHelper;

    public StudyMaterialRepository(Context context) {
        dbHelper = new DBHelper(context.getApplicationContext());
    }

    public long insertMaterial(StudyMaterial material) {
        long now = System.currentTimeMillis();
        material.setCreateTime(now);
        material.setUpdateTime(now);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.insert(DBHelper.TABLE_MATERIAL, null, toValues(material, true));
    }

    public int updateMaterial(StudyMaterial material) {
        material.setUpdateTime(System.currentTimeMillis());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.update(DBHelper.TABLE_MATERIAL, toValues(material, true), DBHelper.COL_ID + "=?",
                new String[]{String.valueOf(material.getId())});
    }

    public int deleteMaterial(long id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(DBHelper.TABLE_MATERIAL, DBHelper.COL_ID + "=?", new String[]{String.valueOf(id)});
    }

    public StudyMaterial getMaterialById(long id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try (Cursor cursor = db.query(DBHelper.TABLE_MATERIAL, null, DBHelper.COL_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null)) {
            if (cursor.moveToFirst()) {
                return fromCursor(cursor);
            }
        }
        return null;
    }

    public List<StudyMaterial> getAllMaterials() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try (Cursor cursor = db.query(DBHelper.TABLE_MATERIAL, null, null, null, null, null,
                DBHelper.COL_CREATE_TIME + " DESC")) {
            return fromCursorList(cursor);
        }
    }

    public List<StudyMaterial> getRecentMaterials(int limit) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try (Cursor cursor = db.query(DBHelper.TABLE_MATERIAL, null, null, null, null, null,
                DBHelper.COL_CREATE_TIME + " DESC", String.valueOf(limit))) {
            return fromCursorList(cursor);
        }
    }

    public List<StudyMaterial> searchMaterials(String keyword, String subject) {
        return searchMaterials(keyword, subject, "", SubjectUtils.ALL);
    }

    public List<StudyMaterial> searchMaterials(String keyword, String subject, String tag, String sourceType) {
        String safeKeyword = keyword == null ? "" : keyword.trim();
        String safeSubject = subject == null ? SubjectUtils.ALL : subject.trim();
        String safeTag = tag == null ? "" : tag.trim();
        String safeSourceType = sourceType == null ? SubjectUtils.ALL : sourceType.trim();
        StringBuilder selection = new StringBuilder("1=1");
        List<String> args = new ArrayList<>();

        if (!safeKeyword.isEmpty()) {
            selection.append(" AND (")
                    .append(DBHelper.COL_TITLE).append(" LIKE ? OR ")
                    .append(DBHelper.COL_CONTENT).append(" LIKE ? OR ")
                    .append(DBHelper.COL_TAGS).append(" LIKE ? OR ")
                    .append(DBHelper.COL_NOTE).append(" LIKE ?)");
            String like = "%" + safeKeyword + "%";
            args.add(like);
            args.add(like);
            args.add(like);
            args.add(like);
        }
        if (!safeSubject.isEmpty() && !SubjectUtils.ALL.equals(safeSubject)) {
            selection.append(" AND ").append(DBHelper.COL_SUBJECT).append("=?");
            args.add(safeSubject);
        }
        if (!safeTag.isEmpty()) {
            selection.append(" AND ").append(DBHelper.COL_TAGS).append(" LIKE ?");
            args.add("%" + safeTag + "%");
        }
        if (!safeSourceType.isEmpty() && !SubjectUtils.ALL.equals(safeSourceType)) {
            selection.append(" AND ").append(DBHelper.COL_SOURCE_TYPE).append("=?");
            args.add(safeSourceType);
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try (Cursor cursor = db.query(DBHelper.TABLE_MATERIAL, null, selection.toString(),
                args.toArray(new String[0]), null, null, DBHelper.COL_CREATE_TIME + " DESC")) {
            return fromCursorList(cursor);
        }
    }

    public List<StudyMaterial> getWrongMaterials() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try (Cursor cursor = db.query(DBHelper.TABLE_MATERIAL, null, DBHelper.COL_IS_WRONG + "=1",
                null, null, null, DBHelper.COL_CREATE_TIME + " DESC")) {
            return fromCursorList(cursor);
        }
    }

    public List<StudyMaterial> getWrongMaterials(String subject) {
        return getWrongMaterials(subject, 0, -1);
    }

    public List<StudyMaterial> getWrongMaterials(String subject, int difficulty, int mastered) {
        if (subject == null || subject.trim().isEmpty() || SubjectUtils.ALL.equals(subject)) {
            subject = "";
        }
        StringBuilder selection = new StringBuilder(DBHelper.COL_IS_WRONG + "=1");
        List<String> args = new ArrayList<>();
        if (!subject.isEmpty()) {
            selection.append(" AND ").append(DBHelper.COL_SUBJECT).append("=?");
            args.add(subject);
        }
        if (difficulty > 0) {
            selection.append(" AND ").append(DBHelper.COL_DIFFICULTY).append("=?");
            args.add(String.valueOf(difficulty));
        }
        if (mastered == 0 || mastered == 1) {
            selection.append(" AND ").append(DBHelper.COL_MASTERED).append("=?");
            args.add(String.valueOf(mastered));
        }
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try (Cursor cursor = db.query(DBHelper.TABLE_MATERIAL, null, selection.toString(),
                args.toArray(new String[0]), null, null, DBHelper.COL_UPDATE_TIME + " DESC")) {
            return fromCursorList(cursor);
        }
    }

    public void markAsWrong(long id) {
        StudyMaterial material = getMaterialById(id);
        if (material == null) {
            return;
        }
        material.setIsWrongQuestion(1);
        material.setMastered(0);
        if (material.getDifficulty() <= 0) {
            material.setDifficulty(2);
        }
        updateMaterial(material);
    }

    public void reviewOnce(long id) {
        StudyMaterial material = getMaterialById(id);
        if (material == null) {
            return;
        }
        long now = System.currentTimeMillis();
        material.setReviewCount(material.getReviewCount() + 1);
        material.setLastReviewTime(now);
        material.setNextReviewTime(now + 24 * 60 * 60 * 1000L);
        updateMaterial(material);
    }

    public void markMastered(long id) {
        StudyMaterial material = getMaterialById(id);
        if (material == null) {
            return;
        }
        material.setMastered(1);
        updateMaterial(material);
    }

    public void cancelWrong(long id) {
        StudyMaterial material = getMaterialById(id);
        if (material == null) {
            return;
        }
        material.setIsWrongQuestion(0);
        material.setMastered(0);
        updateMaterial(material);
    }

    public WrongStatistics getWrongStatistics() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        WrongStatistics statistics = new WrongStatistics();
        statistics.totalWrong = queryInt(db, "SELECT COUNT(*) FROM " + DBHelper.TABLE_MATERIAL + " WHERE " + DBHelper.COL_IS_WRONG + "=1", null);
        statistics.mastered = queryInt(db, "SELECT COUNT(*) FROM " + DBHelper.TABLE_MATERIAL + " WHERE " + DBHelper.COL_IS_WRONG + "=1 AND " + DBHelper.COL_MASTERED + "=1", null);
        statistics.pendingReview = queryInt(db, "SELECT COUNT(*) FROM " + DBHelper.TABLE_MATERIAL + " WHERE " + DBHelper.COL_IS_WRONG + "=1 AND " + DBHelper.COL_MASTERED + "=0", null);
        return statistics;
    }

    public Statistics getStatistics() {
        Statistics statistics = new Statistics();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        statistics.totalCount = queryInt(db, "SELECT COUNT(*) FROM " + DBHelper.TABLE_MATERIAL, null);
        statistics.wrongCount = queryInt(db, "SELECT COUNT(*) FROM " + DBHelper.TABLE_MATERIAL + " WHERE " + DBHelper.COL_IS_WRONG + "=1", null);
        statistics.todayCount = queryInt(db, "SELECT COUNT(*) FROM " + DBHelper.TABLE_MATERIAL + " WHERE " + DBHelper.COL_CREATE_TIME + ">=?",
                new String[]{String.valueOf(DateUtils.startOfToday())});
        statistics.reviewCount = queryInt(db, "SELECT IFNULL(SUM(" + DBHelper.COL_REVIEW_COUNT + "),0) FROM " + DBHelper.TABLE_MATERIAL, null);
        return statistics;
    }

    public void clearAllData() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DBHelper.TABLE_MATERIAL, null, null);
    }

    private int queryInt(SQLiteDatabase db, String sql, String[] args) {
        try (Cursor cursor = db.rawQuery(sql, args)) {
            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
        }
        return 0;
    }

    private ContentValues toValues(StudyMaterial material, boolean includeCreateTime) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COL_TITLE, material.getTitle());
        values.put(DBHelper.COL_CONTENT, material.getContent());
        values.put(DBHelper.COL_SUBJECT, material.getSubject());
        values.put(DBHelper.COL_SOURCE_TYPE, material.getSourceType());
        values.put(DBHelper.COL_TAGS, material.getTags());
        values.put(DBHelper.COL_IMAGE_PATH, material.getImagePath());
        values.put(DBHelper.COL_IS_WRONG, material.getIsWrongQuestion());
        values.put(DBHelper.COL_DIFFICULTY, material.getDifficulty());
        values.put(DBHelper.COL_NOTE, material.getNote());
        values.put(DBHelper.COL_WRONG_REASON, material.getWrongReason());
        values.put(DBHelper.COL_MASTERED, material.getMastered());
        values.put(DBHelper.COL_NEXT_REVIEW_TIME, material.getNextReviewTime());
        if (includeCreateTime) {
            values.put(DBHelper.COL_CREATE_TIME, material.getCreateTime());
        }
        values.put(DBHelper.COL_UPDATE_TIME, material.getUpdateTime());
        values.put(DBHelper.COL_REVIEW_COUNT, material.getReviewCount());
        values.put(DBHelper.COL_LAST_REVIEW_TIME, material.getLastReviewTime());
        return values;
    }

    private List<StudyMaterial> fromCursorList(Cursor cursor) {
        List<StudyMaterial> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            list.add(fromCursor(cursor));
        }
        return list;
    }

    private StudyMaterial fromCursor(Cursor cursor) {
        StudyMaterial material = new StudyMaterial();
        material.setId(cursor.getLong(cursor.getColumnIndexOrThrow(DBHelper.COL_ID)));
        material.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_TITLE)));
        material.setContent(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_CONTENT)));
        material.setSubject(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_SUBJECT)));
        material.setSourceType(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_SOURCE_TYPE)));
        material.setTags(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_TAGS)));
        material.setImagePath(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_IMAGE_PATH)));
        material.setIsWrongQuestion(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_IS_WRONG)));
        material.setDifficulty(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_DIFFICULTY)));
        material.setNote(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_NOTE)));
        material.setWrongReason(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_WRONG_REASON)));
        material.setMastered(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_MASTERED)));
        material.setNextReviewTime(cursor.getLong(cursor.getColumnIndexOrThrow(DBHelper.COL_NEXT_REVIEW_TIME)));
        material.setCreateTime(cursor.getLong(cursor.getColumnIndexOrThrow(DBHelper.COL_CREATE_TIME)));
        material.setUpdateTime(cursor.getLong(cursor.getColumnIndexOrThrow(DBHelper.COL_UPDATE_TIME)));
        material.setReviewCount(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_REVIEW_COUNT)));
        material.setLastReviewTime(cursor.getLong(cursor.getColumnIndexOrThrow(DBHelper.COL_LAST_REVIEW_TIME)));
        return material;
    }
}
