package cy.agorise.crystalwallet.viewmodels;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import cy.agorise.crystalwallet.dao.CrystalDatabase;
import cy.agorise.crystalwallet.models.CryptoNetAccount;

/**
 * Created by Henry Varona on 1/21/2018.
 */

public class CryptoNetAccountListViewModel extends AndroidViewModel {

    private CrystalDatabase db;

    public CryptoNetAccountListViewModel(Application application) {
        super(application);
        this.db = CrystalDatabase.getAppDatabase(application.getApplicationContext());
    }

    public List<CryptoNetAccount> getCryptoNetAccountList(){
        return this.db.cryptoNetAccountDao().getAllCryptoNetAccount();
    }

    public LiveData<List<CryptoNetAccount>> getCryptoNetAccounts(){
        return this.db.cryptoNetAccountDao().getAll();
    }
}
