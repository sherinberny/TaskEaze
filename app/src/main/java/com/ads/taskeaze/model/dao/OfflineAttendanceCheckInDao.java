package com.ads.taskeaze.model.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.ads.taskeaze.model.entities.OfflineAttendanceCheckIn;

import java.util.List;

@Dao
public interface OfflineAttendanceCheckInDao {
    @Insert
    void insertCheckIn(OfflineAttendanceCheckIn checkIn);

    @Query("SELECT * FROM offline_attendance_checkin")
    List<OfflineAttendanceCheckIn> getAllCheckIns();
}
