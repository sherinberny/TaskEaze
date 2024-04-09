package com.ads.taskeaze.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.ads.taskeaze.model.dao.OfflineAttendanceCheckInDao;
import com.ads.taskeaze.model.dao.OfflineAttendanceCheckOutDao;
import com.ads.taskeaze.model.dao.OfflineMeetingsDAO;
import com.ads.taskeaze.model.dao.OfflineUserDao;
import com.ads.taskeaze.model.entities.OfflineAttendanceCheckIn;
import com.ads.taskeaze.model.entities.OfflineAttendanceCheckOut;
import com.ads.taskeaze.model.entities.OfflineMeetings;
import com.ads.taskeaze.model.entities.OfflineUser;

@Database(entities = {OfflineAttendanceCheckIn.class,
        OfflineAttendanceCheckOut.class, OfflineUser.class, OfflineMeetings.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract OfflineAttendanceCheckInDao offlineAttendanceCheckInDao();
    public abstract OfflineAttendanceCheckOutDao offlineAttendanceCheckOutDao();
    public abstract OfflineUserDao offlineUserDao();
    public abstract OfflineMeetingsDAO offlineMeetingsDAO();
}

