package com.example.myapplication.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UserDatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME    = "user_db";
    private static final int    DB_VERSION = 1;

    public static final String TABLE_USERS  = "users";
    public static final String COL_ID       = "id";
    public static final String COL_NAME     = "name";
    public static final String COL_EMAIL    = "email";
    public static final String COL_PASSWORD = "password";
    public static final String COL_PHONE    = "phone";
    public static final String COL_GENDER   = "gender";

    public UserDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_USERS + " (" +
                COL_ID       + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NAME     + " TEXT NOT NULL, " +
                COL_EMAIL    + " TEXT NOT NULL UNIQUE, " +
                COL_PASSWORD + " TEXT NOT NULL, " +
                COL_PHONE    + " TEXT NOT NULL, " +
                COL_GENDER   + " TEXT NOT NULL" +
                ")";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    /** Insert new user, returns rowId or -1 if failed (e.g. email dup) */
    public long insertUser(String name, String email, String password, String phone, String gender) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_NAME,     name);
        cv.put(COL_EMAIL,    email);
        cv.put(COL_PASSWORD, password);
        cv.put(COL_PHONE,    phone);
        cv.put(COL_GENDER,   gender);
        long id = db.insert(TABLE_USERS, null, cv);
        db.close();
        return id;
    }

    /** Returns true if a user with this email+password exists */
    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(
                TABLE_USERS,
                new String[]{COL_ID},
                COL_EMAIL + "=? AND " + COL_PASSWORD + "=?",
                new String[]{email, password},
                null,null,null
        );
        boolean exists = (c.getCount() > 0);
        c.close();
        db.close();
        return exists;
    }
}
