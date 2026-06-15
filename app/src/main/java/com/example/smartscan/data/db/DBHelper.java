package com.example.smartscan.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * SQLiteOpenHelper 数据库创建与升级类。
 *
 * 答辩重点：
 * 1. 本项目所有学习资料和错题都保存在同一张 StudyMaterial 表中。
 * 2. 错题本不是单独一张表，而是通过 isWrongQuestion 字段筛选出来的复习视图。
 * 3. 使用 SQLiteOpenHelper 是为了体现课程中“本地数据库建表、升级、CRUD”的考察点。
 */
public class DBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "smart_scan.db";

    // 当前版本为 2：V1.1/V1.0 后续补充了错题复习相关字段。
    // 如果用户设备上已有旧数据库，Android 会自动调用 onUpgrade 做增量升级。
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
        // 建表时一次性创建完整字段。字段名使用 Java 风格，方便和实体类 StudyMaterial 对应。
        // imagePath 保存的是系统相册返回的 Uri 字符串，不复制大图文件，节省存储空间。
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
        // 这里不直接 DROP TABLE，是为了避免升级时把用户已经保存的学习资料删掉。
        // 答辩时可以强调：数据库升级采用 ALTER TABLE 增量加字段，保证数据安全。
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
            // 这种写法适合课堂演示环境：既能兼容旧库，也能避免重复升级时出错。
        }
    }
}
