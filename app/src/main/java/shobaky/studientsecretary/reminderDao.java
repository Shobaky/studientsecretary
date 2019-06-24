package shobaky.studientsecretary;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import java.util.List;

@Dao
public  interface reminderDao {
    @Query("Select * FROM reminder")
    List<reminder> reminders();

    @Query("DELETE FROM reminder WHERE id = (:id)")
    void delete(long id);

    @Insert
    long[] insertReminder(reminder...reminders);
}
