package com.ads.taskeaze.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.ads.taskeaze.database.ClaimsDatabase;
// Added DAO
public class ClaimDAO {

    private ClaimsDatabase claimdb;

    public ClaimDAO(Context context) {
        claimdb = new ClaimsDatabase(context);
    }

    public long insertClaim(String date, double amount, String description) {
        SQLiteDatabase db = claimdb.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ClaimsDatabase.COLUMN_DATE, date);
        values.put(ClaimsDatabase.COLUMN_AMOUNT, amount);
        values.put(ClaimsDatabase.COLUMN_DESCRIPTION, description);
        long newRowId = db.insert(ClaimsDatabase.TABLE_CLAIMS, null, values);
        db.close();
        return newRowId;
    }
}
