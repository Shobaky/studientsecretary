package shobaky.studientsecretary;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface materialDao {
    @Query("Select * FROM Material")
    List<Material> getAll();

    @Query("DELETE FROM Material WHERE Name = (:Name)")
    void deleteMaterial(String Name);

    @Insert
    void insertMat(Material...materials);
}
