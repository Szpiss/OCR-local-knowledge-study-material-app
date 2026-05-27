package com.example.smartscan.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * SQLiteOpenHelper 数据库创建与升级类，第一阶段使用本地 SQLite 保存资料。
 */
public class DBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "smart_scan.db";
    public static final int DATABASE_VERSION = 2;
    public static final String TABLE_MATERIAL = "StudyMaterial";

    public static final String COL_ID = "id";
    public static final String COL_TITLE = "title";
    public static final String COL_CONTENT = "content";
    public static final String COL_SUBJECT = "subject";
    public static final String COL_SOURCE_TYPE = "sourceType";
    public static final String COL_TAGS = "tags";
    public static final String COL_IMAGE_PATH = "imagePath";
    public static final String COL_IS_WRONG = "isWrongQuestion";
    public static final String COL_DIFFICULTY = "difficulty";
    public static final String COL_NOTE = "note";
    public static final String COL_WRONG_REASON = "wrongReason";
    public static final String COL_MASTERED = "mastered";
    public static final String COL_NEXT_REVIEW_TIME = "nextReviewTime";
    public static final String COL_CREATE_TIME = "createTime";
    public static final String COL_UPDATE_TIME = "updateTime";
    public static final String COL_REVIEW_COUNT = "reviewCount";
    public static final String COL_LAST_REVIEW_TIME = "lastReviewTime";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_MATERIAL + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_TITLE + " TEXT, "
                + COL_CONTENT + " TEXT, "
                + COL_SUBJECT + " TEXT, "
                + COL_SOURCE_TYPE + " TEXT, "
                + COL_TAGS + " TEXT, "
                + COL_IMAGE_PATH + " TEXT, "
                + COL_IS_WRONG + " INTEGER, "
                + COL_DIFFICULTY + " INTEGER, "
                + COL_NOTE + " TEXT, "
                + COL_WRONG_REASON + " TEXT, "
                + COL_MASTERED + " INTEGER DEFAULT 0, "
                + COL_NEXT_REVIEW_TIME + " INTEGER DEFAULT 0, "
                + COL_CREATE_TIME + " INTEGER, "
                + COL_UPDATE_TIME + " INTEGER, "
                + COL_REVIEW_COUNT + " INTEGER, "
                + COL_LAST_REVIEW_TIME + " INTEGER"
                + ")";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            addColumnIfNeeded(db, COL_WRONG_REASON + " TEXT");
            addColumnIfNeeded(db, COL_MASTERED + " INTEGER DEFAULT 0");
            addColumnIfNeeded(db, COL_NEXT_REVIEW_TIME + " INTEGER DEFAULT 0");
        }
    }

    private void addColumnIfNeeded(SQLiteDatabase db, String columnSql) {
        try {
            db.execSQL("ALTER TABLE " + TABLE_MATERIAL + " ADD COLUMN " + columnSql);
        } catch (Exception ignored) {
            // 字段已存在时忽略，避免课程演示中反复安装调试导致升级崩溃。
        }
    }
}
