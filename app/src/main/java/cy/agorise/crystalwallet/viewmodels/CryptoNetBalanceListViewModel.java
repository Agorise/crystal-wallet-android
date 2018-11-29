package cy.agorise.crystalwallet.viewmodels;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import cy.agorise.crystalwallet.dao.CrystalDatabase;
import cy.agorise.crystalwallet.models.CryptoNetBalance;

/**
 * Created by Henry Varona on 27/9/2017.
 */

public class CryptoNetBalanceListViewModel extends AndroidViewModel {

    private LiveData<List<CryptoNetBalance>> cryptoNetBalanceList;
    private CrystalDatabase db;

    public CryptoNetBalanceListViewModel(Application application) {
        super(application);
        this.db = CrystalDatabase.getAppDatabase(application.getApplicationContext());
        this.cryptoNetBalanceList = this.db.cryptoCoinBalanceDao().getAllBalances();
    }

    public LiveData<List<CryptoNetBalance>> getCryptoNetBalanceList(){
        return this.cryptoNetBalanceList;
    }

    public long getFirstBitsharesAccountId(){
        return this.db.cryptoCoinBalanceDao().getFirstBitsharesAccountId();
    }
}
