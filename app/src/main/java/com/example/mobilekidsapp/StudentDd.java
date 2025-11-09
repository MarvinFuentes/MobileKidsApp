package com.example.mobilekidsapp;

import static android.content.Context.MODE_PRIVATE;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class StudentDd extends SQLiteOpenHelper {
    SQLiteDatabase database;
    private static final String DATABASE_NAME = "student_v2.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "profiles";
    private static final String ID_COL = "id";
    private static final String COLOR_COL = "color";
    private static final String SHAPE_COL = "shape";

    //Constructor for our database handler
    public StudentDd(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //Called only once when DB is created for the first time
    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLOR_COL + " TEXT,"
                + SHAPE_COL + " TEXT)";

        //method to execute above sql query
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    //Inserting a new profile
    public void insertData(String color, String shape){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLOR_COL, color);
        values.put(SHAPE_COL, shape);

        db.insert(TABLE_NAME, null, values);
        db.close();
    }
}
