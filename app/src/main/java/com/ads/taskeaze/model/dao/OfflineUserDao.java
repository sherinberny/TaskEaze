package com.ads.taskeaze.model.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.ads.taskeaze.model.entities.OfflineUser;

import java.util.List;

@Dao
public interface OfflineUserDao {
    @Insert
    void insertUser(OfflineUser user);

    @Query("SELECT * FROM offline_user")
    List<OfflineUser> getAll();

    @Delete
    void delete(OfflineUser user);
}