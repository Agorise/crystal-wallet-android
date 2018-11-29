package cy.agorise.crystalwallet.viewmodels;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import cy.agorise.crystalwallet.dao.CrystalDatabase;
import cy.agorise.crystalwallet.models.AccountSeed;

/**
 * Created by Henry Varona on 27/9/2017.
 */

public class AccountSeedListViewModel extends AndroidViewModel {

    private LiveData<List<AccountSeed>> accountSeedList;
    private CrystalDatabase db;

    public AccountSeedListViewModel(Application application) {
        super(application);
        this.db = CrystalDatabase.getAppDatabase(application.getApplicationContext());
        this.accountSeedList = this.db.accountSeedDao().getAll();
    }

    public LiveData<List<AccountSeed>> getAccountSeedList(){
        return this.accountSeedList;
    }

    public int accountSeedsCount(){
        return this.db.accountSeedDao().countAccountSeeds();
    }
}
