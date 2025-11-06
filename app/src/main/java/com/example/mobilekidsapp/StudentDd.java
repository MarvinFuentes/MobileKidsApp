package com.example.mobilekidsapp;

import static android.content.Context.MODE_PRIVATE;
import static android.database.sqlite.SQLiteDatabase.openOrCreateDatabase;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class StudentDd {
    String databaseName = "student.db";

    //STEP 1
    SQLiteDatabase database;
    
    //STEP 1
    public void openDatabase(String databaseName){
        database = openOrCreateDatabase(databaseName, MODE_PRIVATE, null);
    }

    private SQLiteDatabase openOrCreateDatabase(String databaseName, int modePrivate, Object o) {
        return null;
    }

    //STEP 2
    public void createTable(String tableName){
        if(database != null){
            String sql = "create table" + tableName + "(_id integer PRIMARY KEY autoincrement, name text, age integer, mobile text)";
            database.execSQL(sql);
        }
    }
    
    public void insertData(String color, String shape){
        if(database != null){
            String sql = "insert into student(shape, color) values('','')";
            database.execSQL(sql);
        }
    }
    
    //STEP 4
    public void selectData(String tableName){
        if(database != null){
            String sql = "select shape, color from " + tableName;

            Cursor cursor = database.rawQuery(sql, null);
            
            for(int i = 0; i < cursor.getCount(); i++){
                cursor.moveToNext();
                String color = cursor.getString(0);
                String shape = cursor.getString(1);
                
                // Find a way to show the shapes the students decided to fill with color
                //probably will have to manipulate with xml file by removing the
                //android:strokeColor to transparent or none and changing the
                //android:fillColor from transparent to the color from the color_select_... file.
            }
            cursor.close();
        }
    }
    

}
