package com.javierviveros.entrevistacopilot;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "entrevistas.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_CONVERSATIONS = "conversations";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TIMESTAMP = "timestamp";
    public static final String COLUMN_QUESTION = "question";
    public static final String COLUMN_ANSWER = "answer";
    public static final String COLUMN_LANGUAGE = "language";

    public static class HistoryItem {
        public long id;
        public String timestamp;
        public String question;
        public String answer;
        public String language;

        public HistoryItem(long id, String timestamp, String question, String answer, String language) {
            this.id = id;
            this.timestamp = timestamp;
            this.question = question;
            this.answer = answer;
            this.language = language;
        }
    }

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_CONVERSATIONS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TIMESTAMP + " TEXT, " +
                COLUMN_QUESTION + " TEXT, " +
                COLUMN_ANSWER + " TEXT, " +
                COLUMN_LANGUAGE + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONVERSATIONS);
        onCreate(db);
    }

    public long insertConversation(String question, String answer, String language) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        String timeStr = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

        values.put(COLUMN_TIMESTAMP, timeStr);
        values.put(COLUMN_QUESTION, question);
        values.put(COLUMN_ANSWER, answer);
        values.put(COLUMN_LANGUAGE, language);

        return db.insert(TABLE_CONVERSATIONS, null, values);
    }

    public List<HistoryItem> getAllConversations() {
        return searchConversations(null);
    }

    public List<HistoryItem> searchConversations(String query) {
        List<HistoryItem> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String selection = null;
        String[] selectionArgs = null;

        if (query != null && !query.trim().isEmpty()) {
            selection = COLUMN_QUESTION + " LIKE ? OR " + COLUMN_ANSWER + " LIKE ?";
            String wild = "%" + query.trim() + "%";
            selectionArgs = new String[]{wild, wild};
        }

        Cursor cursor = db.query(
                TABLE_CONVERSATIONS,
                null,
                selection,
                selectionArgs,
                null,
                null,
                COLUMN_ID + " DESC"
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String time = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP));
                String q = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_QUESTION));
                String a = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ANSWER));
                String lang = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LANGUAGE));
                list.add(new HistoryItem(id, time, q, a, lang));
            } while (cursor.moveToNext());
            cursor.close();
        }

        return list;
    }

    public void clearAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONVERSATIONS, null, null);
    }
}
