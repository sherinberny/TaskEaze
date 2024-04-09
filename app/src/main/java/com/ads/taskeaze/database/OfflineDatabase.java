package com.ads.taskeaze.database;

import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;

public class OfflineDatabase {
    private static AppDatabase appDatabase;

    public static synchronized AppDatabase getAppDatabase(Context context) {
        if (appDatabase == null) {
            appDatabase = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "taskeaze.db")
                    .build();
        }
        return appDatabase;
    }
}
