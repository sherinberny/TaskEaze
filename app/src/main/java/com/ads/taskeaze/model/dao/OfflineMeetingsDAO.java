package com.ads.taskeaze.model.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.ads.taskeaze.model.entities.OfflineMeetings;
import com.ads.taskeaze.model.entities.OfflineUser;

import java.util.List;

@Dao
public interface OfflineMeetingsDAO {
    @Insert
    void insertMeetings(OfflineMeetings meetings);

    @Query("SELECT * FROM offline_meetings WHERE onDate=:onDate")
    List<OfflineMeetings> getAllMeetingsDate(String onDate);

}
