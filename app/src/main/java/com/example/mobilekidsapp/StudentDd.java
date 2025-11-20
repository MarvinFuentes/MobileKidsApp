package com.example.mobilekidsapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class StudentDd extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "student_v9.db";
    private static final int DATABASE_VERSION = 2;

    /*These are the attributes for the main profile table. Each row will represent a student profile and all of the
    Strings initialized below represents a column for the table name "profiles".*/
    private static final String TABLE_NAME = "profiles";
    private static final String ID_COL = "id";
    private static final String COLOR_COL = "color";
    private static final String SHAPE_COL = "shape";

    //These variables are responsible for storing each subject's progress bar position.
    private static final String ALPHABET_PROGRESS = "alphabetProgress";
    private static final String COUNTING_PROGRESS = "countingProgress";
    private static final String MATH_PROGRESS = "mathProgress";

    /*Just like the main table below are all the columns for the table called "student_drawings.
    This second table is responsible for storing the drawings from the alphabet and counting fragments. */
    private static final String TABLE_DRAWINGS = "student_drawings";
    private static final String DRAWING_COLOR_COL = "color";
    private static final String DRAWING_SHAPE_COL = "shape";
    private static final String DRAWING_SUB_COL = "subject";
    private static final String DRAWING_PAGE_COL = "page";
    private static final String DRAWING_BITMAP_COL = "bitmap";
    static final String TABLE_MATH = "math_table";
    static final String MATH_NUM1 = "num1";
    static final String MATH_OPERATION = "operation";
    static final String MATH_NUM2 = "num2";
    static final String MATH_INPUT = "input";

    //Constructor for our database handler
    public StudentDd(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //Called only once when DB is created for the first time.
    @Override
    public void onCreate(SQLiteDatabase db) {
        //The main table that stores all of the student profiles and their progress for all subjects.
        String mainTable = "CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLOR_COL + " TEXT,"
                + SHAPE_COL + " TEXT, "
                + ALPHABET_PROGRESS + " INTEGER DEFAULT 0, "
                + COUNTING_PROGRESS + " INTEGER DEFAULT 0, "
                + MATH_PROGRESS + " INTEGER DEFAULT 0)";

        /*The table that stores all saved drawings (one per letter or number).
        we also demonstrated in this query how a composite key can be very useful.*/
        String drawingTable = "CREATE TABLE " + TABLE_DRAWINGS + " ("
                + DRAWING_COLOR_COL + " TEXT, "
                + DRAWING_SHAPE_COL + " TEXT, "
                + DRAWING_SUB_COL + " TEXT, "
                + DRAWING_PAGE_COL + " TEXT, "
                + DRAWING_BITMAP_COL + " BLOB, "
                + "PRIMARY KEY("
                + DRAWING_COLOR_COL + ", "
                + DRAWING_SHAPE_COL + ", "
                + DRAWING_SUB_COL + ", "
                + DRAWING_PAGE_COL + "))";

        String mathTable = "CREATE TABLE " + TABLE_MATH + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "color TEXT, "
                + "shape TEXT, "
                + MATH_NUM1 + " INTEGER, "
                + MATH_OPERATION + " TEXT, "
                + MATH_NUM2 + " INTEGER, "
                + MATH_INPUT + " INTEGER, "
                + "UNIQUE(color, shape, num1, operation, num2) ON CONFLICT REPLACE"
                + ");";

        //The same method is called twice to execute both queries above.
        db.execSQL(mainTable);
        db.execSQL(drawingTable);
        db.execSQL(mathTable);
    }

    /*This method is called whenever the database is upgraded version.
    It is also responsible for resting the existing tables.*/
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DRAWINGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MATH);
        onCreate(db);
    }

    //This method is responsible for inserting new student profiles into the database.
    public void insertData(String color, String shape){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLOR_COL, color);
        values.put(SHAPE_COL, shape);

        db.insert(TABLE_NAME, null, values);
        db.close();
    }


    // updateProgress saves the progress for each subject.
    public void updateProgress(String color, String shape, String subject, int progress) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        switch (subject.toLowerCase()) {
            case "alphabet":
                values.put(ALPHABET_PROGRESS, progress);
                break;
            case "counting":
                values.put(COUNTING_PROGRESS, progress);
                break;
            case "math":
                values.put(MATH_PROGRESS, progress);
                break;
            default:
                db.close();
                return;
        }

        // Try updating the row
        int rowsUpdated = db.update(TABLE_NAME, values,
                COLOR_COL + "=? AND " + SHAPE_COL + "=?",
                new String[]{color, shape});

        // If no row was updated, insert a new one
        if (rowsUpdated == 0) {
            values.put(COLOR_COL, color);
            values.put(SHAPE_COL, shape);
            db.insert(TABLE_NAME, null, values);
        }

        db.close();
    }

    public int getProgress(String color, String shape, String subject){
        SQLiteDatabase db = this.getReadableDatabase();
        String col = "";

        switch (subject.toLowerCase()) {
            case "alphabet":
                col = ALPHABET_PROGRESS;
                break;
            case "counting":
                col = COUNTING_PROGRESS;
                break;
            case "math":
                col = MATH_PROGRESS;
                break;
            default:
                col = null;
        }

        if(col == null){
            db.close();
            return 0;
        }

        int progress = 0;

        try(Cursor cursor = db.query(TABLE_NAME, new String[]{col}, COLOR_COL + "=? AND " + SHAPE_COL + "=?",
                new String[]{color, shape}, null, null, null)){

            if(cursor != null && cursor.moveToFirst()){
                progress = cursor.getInt(0);
            }
        }

        db.close();
        return progress;
    }

    //This method is responsible for saving the individual bit maps for the alphabet and counting
    // activities as letter or number pages.
    public void saveBitmap(String color, String shape, String subject, String page, Bitmap bitmap){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DRAWING_COLOR_COL, color);
        values.put(DRAWING_SHAPE_COL, shape);
        values.put(DRAWING_SUB_COL, subject);
        values.put(DRAWING_PAGE_COL, page);

        if(bitmap != null){
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, stream);
            values.put(DRAWING_BITMAP_COL, stream.toByteArray());
        }

        db.insertWithOnConflict(TABLE_DRAWINGS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public Bitmap loadBitmap(String color, String shape, String subject, String page){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_DRAWINGS,
                        new String[]{DRAWING_BITMAP_COL},
                DRAWING_COLOR_COL + "=? AND " +
                        DRAWING_SHAPE_COL + "=? AND " +
                        DRAWING_SUB_COL + "=? AND " +
                        DRAWING_PAGE_COL + "=?",
                        new String[]{color, shape, subject, String.valueOf(page)},
                        null, null, null);

        Bitmap bitmap = null;

        if(cursor != null && cursor.moveToFirst()){
            byte [] blob = cursor.getBlob(0);
            if(blob != null){
                bitmap = BitmapFactory.decodeByteArray(blob, 0, blob.length);
            }
        }

        if(cursor != null){
            cursor.close();
        }

        db.close();
        return bitmap;
    }

    public void insertOrUpdateMathProgress(String color, String shape, int num1, String operation, int num2, String userInput) {
        if (userInput == null || userInput.isEmpty()) return; // skip empty answers

        int inputValue = Integer.parseInt(userInput);

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("color", color);
        values.put("shape", shape);
        values.put(MATH_NUM1, num1);
        values.put(MATH_OPERATION, operation);
        values.put(MATH_NUM2, num2);
        values.put(MATH_INPUT, inputValue);

        db.insertWithOnConflict(TABLE_MATH, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }


    public ArrayList<MathActivity.MathQuestion> getMathQuestionsForStudent(String color, String shape) {
        ArrayList<MathActivity.MathQuestion> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_MATH, null, "color=? AND shape=?", new String[]{color, shape}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int num1 = cursor.getInt(cursor.getColumnIndexOrThrow(MATH_NUM1));
                String op = cursor.getString(cursor.getColumnIndexOrThrow(MATH_OPERATION));
                int num2 = cursor.getInt(cursor.getColumnIndexOrThrow(MATH_NUM2));
                int input = cursor.getInt(cursor.getColumnIndexOrThrow(MATH_INPUT));

                MathActivity.MathQuestion q = new MathActivity.MathQuestion(num1, num2, op);
                q.userInput = String.valueOf(input);

                list.add(q);
            } while (cursor.moveToNext());
            cursor.close();
        }

        db.close();
        return list;
    }


}
