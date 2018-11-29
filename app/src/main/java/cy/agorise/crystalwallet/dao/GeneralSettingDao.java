package cy.agorise.crystalwallet.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import cy.agorise.crystalwallet.models.GeneralSetting;

/**
 * Created by Henry Varona on 10/9/2017.
 */

@Dao
public interface GeneralSettingDao {

    @Query("SELECT * FROM general_setting")
    LiveData<List<GeneralSetting>> getAll();

    @Query("SELECT * FROM general_setting WHERE name = :name")
    LiveData<GeneralSetting> getByName(String name);

    @Query("SELECT * FROM general_setting WHERE name = :name")
    GeneralSetting getSettingByName(String name);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public long[] insertGeneralSettings(GeneralSetting... generalSettings);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public long insertGeneralSetting(GeneralSetting generalSetting);

    @Delete
    public void deleteGeneralSettings(GeneralSetting... generalSettings);

    @Query("DELETE FROM general_setting WHERE name = :name")
    public void deleteByName(String name);
}
