package cy.agorise.crystalwallet.viewmodels;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import cy.agorise.crystalwallet.dao.CrystalDatabase;
import cy.agorise.crystalwallet.models.GeneralSetting;

/**
 * Created by Henry Varona on 6/11/2017.
 */

public class GeneralSettingListViewModel extends AndroidViewModel {

    private LiveData<List<GeneralSetting>> generalSettingList;
    private CrystalDatabase db;

    public GeneralSettingListViewModel(Application application) {
        super(application);
        this.db = CrystalDatabase.getAppDatabase(application.getApplicationContext());
        generalSettingList = this.db.generalSettingDao().getAll();
    }

    public LiveData<List<GeneralSetting>> getGeneralSettingList(){
        return this.generalSettingList;
    }

    public void saveGeneralSetting(GeneralSetting generalSetting){
        this.db.generalSettingDao ().insertGeneralSetting(generalSetting);
    }

    public void saveGeneralSettings(GeneralSetting... generalSettings){
        this.db.generalSettingDao().insertGeneralSettings(generalSettings);
    }

    public GeneralSetting getGeneralSettingByName(String name){
        return this.db.generalSettingDao().getSettingByName(name);
    }

    public LiveData<GeneralSetting> getGeneralSettingLiveDataByName(String name){
        return this.db.generalSettingDao().getByName(name);
    }

    public void deleteGeneralSettings(GeneralSetting... generalSettings){
        this.db.generalSettingDao().deleteGeneralSettings(generalSettings);
    }

    public void deleteGeneralSettingByName(String name){
        this.db.generalSettingDao().deleteByName(name);
    }

    //public void addGeneralSetting(String name, String value){
    //    this.db.generalSettingDao().addGeneralSetting(name,value);
    //}
}
