package shobaky.studientsecretary;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {reminder.class},version = 1)
public abstract class reminderRoom extends RoomDatabase {
    public abstract reminderDao getRemindDao();
}
