package com.example.mobilekidsapp;

import static android.content.Context.MODE_PRIVATE;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;

public class StudentDd extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "student_v4.db";
    private static final int DATABASE_VERSION = 2;

    /*These are the attributes for the main profile table. Each row will represent a student profile and all of the
    Strings initialized below represents a column for the table named "profiles".*/
    private static final String TABLE_NAME = "profiles";
    private static final String ID_COL = "id";
    private static final String COLOR_COL = "color";
    private static final String SHAPE_COL = "shape";

    // Each colum is used to store each subject's progress bar.
    private static final String ALPHABET_PROGRESS = "alphabetProgress";
    private static final String COUNTING_PROGRESS = "countingProgress";
    private static final String MATH_PROGRESS = "mathProgress";

    /*Just like the main table, below are all the columns for the table called "student_drawings".
    This second table is responsible for storing the drawings from the alphabet and counting pages.
    Each entry represents one saved page for letters or numbers*/
    private static final String TABLE_DRAWINGS = "student_drawings";
    private static final String DRAWING_COLOR_COL = "color";
    private static final String DRAWING_SHAPE_COL = "shape";
    private static final String DRAWING_SUB_COL = "subject";
    private static final String DRAWING_PAGE_COL = "page";
    private static final String DRAWING_BITMAP_COL = "bitmap";

    // Constructor for our database helper.
    public StudentDd(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Called only once when the database is created for the first time.
    @Override
    public void onCreate(SQLiteDatabase db) {
        // The main profile table stores all of the student profiles and their progress for each subject.
        String mainTable = "CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLOR_COL + " TEXT,"
                + SHAPE_COL + " TEXT, "
                + ALPHABET_PROGRESS + " INTEGER DEFAULT 0, "
                + COUNTING_PROGRESS + " INTEGER DEFAULT 0, "
                + MATH_PROGRESS + " INTEGER DEFAULT 0)";

        /* The table that stores all saved drawings (one per letter or number).
        We used a composite key to uniquely identify each page for each student
        and for the counting or alphabet activity.*/
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

        // We execute both create table statements.
        db.execSQL(mainTable);
        db.execSQL(drawingTable);
    }

    /* This method is called whenever the database changes version.
    It is also responsible for resetting the existing tables and rebuilding them.*/
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DRAWINGS);
        onCreate(db);
    }

    /* This method is responsible for inserting new student profiles into the main table
    using the color and shape. */
    public void insertData(String color, String shape){
        // We use getWritableDatabase() to enable inserting new data into our table.
        SQLiteDatabase db = this.getWritableDatabase();

        // We now use ContentValues to insert the new color and shape into the main profile table.
        // We use put() to place the color and shape into their specific columns.
        ContentValues values = new ContentValues();
        values.put(COLOR_COL, color);
        values.put(SHAPE_COL, shape);

        // We insert the new profile into the main table and then we close the database.
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    /* updateProgress() calls getWritableDatabase and uses the arguments passed
      which include color, shape, subject, and progress to ensure that we update the correct
      profile and the correct subject. Saves or updates progress for a chosen subject. */
    public void updateProgress(String color, String shape, String subject, int progress) {
        // We use getWritableDatabase() to enable inserting new data into our table.
        // We also use ContentValues to insert the progress for the specific profile and subject.
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        // The switch case is very helpful for locating exactly which subject we are updating. Once we
        // know which subject we are updating, then we use put() to insert the progress integer into
        // the correct column.
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

        // We try to update an existing row. If no row matches, then a new one must be inserted.
        int rowsUpdated = db.update(TABLE_NAME, values,
                COLOR_COL + "=? AND " + SHAPE_COL + "=?",
                new String[]{color, shape});

        // If no row matches, then a new one is inserted.
        if (rowsUpdated == 0) {
            values.put(COLOR_COL, color);
            values.put(SHAPE_COL, shape);
            db.insert(TABLE_NAME, null, values);
        }

        // We close the database.
        db.close();
    }

    /* getProgress calls the getReadableDatabase() we use the switch case to compare the empty
     string called col and assign it the correct progress column based on the subject. This method
     includes 3 parameters to know exactly which profile we are pulling progress from.  */
    public int getProgress(String color, String shape, String subject){
        SQLiteDatabase db = this.getReadableDatabase();
        String col = "";

        // We use the switch case to identify exactly which subject's progress we want to get.
        // Then we set the empty string 'col' to the name of that progress column.
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

        // We check if the column is null. If it is, then we return 0 and close the database.
        if(col == null){
            db.close();
            return 0;
        }

        // We initialize progress so that we can store the retrieved value.
        int progress = 0;

        // We check if the cursor is valid and positioned on the first row.
        // It is then we pull the integer inside that specific progress column
        // and store it into the progress variable.
        try(Cursor cursor = db.query(TABLE_NAME, new String[]{col}, COLOR_COL + "=? AND " + SHAPE_COL + "=?",
                new String[]{color, shape}, null, null, null)){

            if(cursor != null && cursor.moveToFirst()){
                progress = cursor.getInt(0);
            }
        }

        // We close the database and return the progress.
        db.close();
        return progress;
    }

    /* saveBitmap() uses the arguments passed (color, shape, subject, and page) to ensure that
    we are saving bitmaps to the correct student profile and subject. This method is responsible
    for saving the individual bitmaps for the alphabet and counting activities as letter or number pages. */
    public void saveBitmap(String color, String shape, String subject, String page, Bitmap bitmap){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        // We used put() to save the bitmap into the correct profile using the selected shape and
        // color, and then narrow it down to the subject.
        values.put(DRAWING_COLOR_COL, color);
        values.put(DRAWING_SHAPE_COL, shape);
        values.put(DRAWING_SUB_COL, subject);
        values.put(DRAWING_PAGE_COL, page);

        // We check if the bitmap is not null. If it is not null, then we compress the bitmap into
        // PNG format. The PNG is then converted into a byte array and stored in DRAWING_BITMAP_COL,
        // which uses the BLOB type of data.
        if(bitmap != null){
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, stream);
            values.put(DRAWING_BITMAP_COL, stream.toByteArray());
        }

        /* We use insertWithOnConflict to ensure that the composite key stays unique. If the the drawing for that
        page already exists, then it will automatically be replaced. This prevets duplicate rows for the same page.
        Finally, we close the database. */
        db.insertWithOnConflict(TABLE_DRAWINGS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    /* loadBitmap () loads saved drawings by using the arguments passed such as color, shape, subject
    and page. */
    public Bitmap loadBitmap(String color, String shape, String subject, String page){
        SQLiteDatabase db = this.getReadableDatabase();

        // We match all of the four key columns to ensure we are loading the correct bitmap.
        Cursor cursor = db.query(TABLE_DRAWINGS,
                        new String[]{DRAWING_BITMAP_COL},
                DRAWING_COLOR_COL + "=? AND " +
                        DRAWING_SHAPE_COL + "=? AND " +
                        DRAWING_SUB_COL + "=? AND " +
                        DRAWING_PAGE_COL + "=?",
                        new String[]{color, shape, subject, String.valueOf(page)},
                        null, null, null);

        Bitmap bitmap = null;

        /* This if statement checks if the cursor is not null and if it can move to the first
        available row using .moveRoFirst(). We then try to retrieve the the PNG byte array,
         and if it is successful, we convert that byte data back into a bitmap */
        if(cursor != null && cursor.moveToFirst()){
            byte [] blob = cursor.getBlob(0);
            if(blob != null){
                bitmap = BitmapFactory.decodeByteArray(blob, 0, blob.length);
            }
        }

        // We make sure that the cursor is not null and if so then we close the cursor.
        if(cursor != null){
            cursor.close();
        }

        // We close the database and return the bitmap that was decoded.
        db.close();
        return bitmap;
    }
}
