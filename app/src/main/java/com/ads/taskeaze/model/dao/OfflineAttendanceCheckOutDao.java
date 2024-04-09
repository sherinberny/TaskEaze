package com.ads.taskeaze.model.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.ads.taskeaze.model.entities.OfflineAttendanceCheckOut;

import java.util.List;
@Dao
public interface OfflineAttendanceCheckOutDao {
    @Insert
    void insertCheckOut(OfflineAttendanceCheckOut checkOut);

    @Query("SELECT * FROM offline_attendance_checkout")
    List<OfflineAttendanceCheckOut> getAllCheckOuts();
}
