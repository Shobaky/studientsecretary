package shobaky.studientsecretary;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {Material.class} ,version = 1 )
public abstract class  materialRoom extends RoomDatabase {
    public abstract materialDao getMatDao();

}
