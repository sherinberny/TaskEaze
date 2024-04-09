package com.ads.taskeaze.model.entities;

import android.content.Context;

import androidx.room.Room;

import com.ads.taskeaze.database.AppDatabase;
import com.ads.taskeaze.model.dao.ClaimDao;
// Added ClaimRepos
public class ClaimRepository {
    private ClaimDao claimDao;

    public ClaimRepository(Context context) {
        AppDatabase db = Room.databaseBuilder(context.getApplicationContext(),
                AppDatabase.class, "database-name").build();
        claimDao = db.claimDao();
    }

    public long insertClaim(String date, double amount, String description) {
        Claim claim = new Claim(date, amount, description); // Fix constructor call
        return claimDao.insertClaim(claim);
    }
}
