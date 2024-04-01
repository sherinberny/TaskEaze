package com.ads.taskeaze.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ClaimsDatabase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "claims.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_CLAIMS = "claims";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_AMOUNT = "amount";
    public static final String COLUMN_DESCRIPTION = "description";

    private static final String CREATE_CLAIMS_TABLE =
            "CREATE TABLE " + TABLE_CLAIMS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_DATE + " TEXT, " +
                    COLUMN_AMOUNT + " REAL, " +
                    COLUMN_DESCRIPTION + " TEXT)";

    public ClaimsDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CLAIMS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLAIMS);
        onCreate(db);
    }

}
