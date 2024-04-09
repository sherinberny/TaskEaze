package com.ads.taskeaze.model.dao;

import androidx.room.Dao;
import androidx.room.Insert;

import com.ads.taskeaze.model.entities.Claim;
// added claimDao
@Dao
public interface ClaimDao {
    @Insert
    default long insertClaim(Claim claim) {
        return 0;
    }
}
