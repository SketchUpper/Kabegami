package org.xtimms.kabegami.db.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import org.xtimms.kabegami.db.Recents;

@Database(entities = Recents.class, version = LocalDatabase.DATABASE_VERSION)
public abstract class LocalDatabase extends RoomDatabase {

    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "Kabegami";

    public abstract RecentsDAO recentsDAO();

    private static LocalDatabase instance;

    public static LocalDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context, LocalDatabase.class, DATABASE_NAME)
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }

}
