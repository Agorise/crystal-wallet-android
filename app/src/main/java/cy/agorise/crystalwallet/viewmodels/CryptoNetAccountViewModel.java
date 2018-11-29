package cy.agorise.crystalwallet.viewmodels;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import cy.agorise.crystalwallet.dao.CrystalDatabase;
import cy.agorise.crystalwallet.models.CryptoNetAccount;

/**
 * Created by Henry Varona on 21/10/2017.
 */

public class CryptoNetAccountViewModel extends AndroidViewModel {

    private LiveData<CryptoNetAccount> cryptoNetAccount;
    private CrystalDatabase db;

    public CryptoNetAccountViewModel(Application application) {
        super(application);
        this.db = CrystalDatabase.getAppDatabase(application.getApplicationContext());
    }

    public void loadCryptoNetAccount(long accountId){
        this.cryptoNetAccount = this.db.cryptoNetAccountDao().getByIdLiveData(accountId);
    }

    public void addCryptoNetAccount(CryptoNetAccount account){
        long newId = this.db.cryptoNetAccountDao().insertCryptoNetAccount(account)[0];
        account.setId(newId);
    }

    public LiveData<CryptoNetAccount> getCryptoNetAccount(){
        return this.cryptoNetAccount;
    }
}
