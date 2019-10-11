package com.droid2developers.frozenocr.controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.droid2developers.frozenocr.model.OCRModel;

import java.util.LinkedList;
import java.util.List;

public class SQLiteHandler extends SQLiteOpenHelper {

    private static final String TAG = SQLiteHandler.class.getSimpleName();
    // Database Details
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "VISION";
    private static final String TABLE_NAME = "Recognition";

    // Query parameters
    private static final String KEY_TIMESTAMP = "time_stamp";
    private static final String KEY_SAVED_URL = "saved_url";
    private static final String KEY_EXTRA_TEXT = "extra_text";

    private static final String[] COLUMNS = { KEY_TIMESTAMP, KEY_SAVED_URL, KEY_EXTRA_TEXT};


    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATION_TABLE = "CREATE TABLE "+ TABLE_NAME +  " ("
                + "time_stamp TEXT PRIMARY KEY ," + " saved_url TEXT ,"
                + "extra_text TEXT)";
        sqLiteDatabase.execSQL(CREATION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // this deletes last table that exists
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        this.onCreate(sqLiteDatabase);
    }




    public void deleteRecognition(OCRModel ocrModel) {

        // Get reference to writable DB
        Log.d(TAG, "deleteFavourite: timeStamp = " + ocrModel.getTimeStamp());
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, "time_stamp = ?", new String[] {String.valueOf(ocrModel.getTimeStamp())});
        db.close();
    }

    public void deleteRecognitionData(){
        // Get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+ TABLE_NAME);
    }


    public void addRecognition(OCRModel ocrModel) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TIMESTAMP, ocrModel.getTimeStamp());
        values.put(KEY_SAVED_URL, ocrModel.getImageUri());
        values.put(KEY_EXTRA_TEXT, ocrModel.getExtaText());
        // insert
        db.insert(TABLE_NAME,null, values);
        db.close();
        Log.d(TAG, "addRecognition: Data Added!");
    }



    public List<OCRModel> getAllRecognitions() {

        List<OCRModel> mRecoList = new LinkedList<>();
        String query = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                OCRModel ocrModel = new OCRModel();
                ocrModel.setTimeStamp(Long.parseLong(cursor.getString(0)));
                ocrModel.setImageUri(cursor.getString(1));
                ocrModel.setExtaText(cursor.getString(2));
                mRecoList.add(ocrModel);
                Log.d(TAG, "getAllRecognitions: TimeStamp = " + ocrModel.getTimeStamp());

            } while (cursor.moveToNext());
            cursor.close();
        }
        return mRecoList;
    }

}
