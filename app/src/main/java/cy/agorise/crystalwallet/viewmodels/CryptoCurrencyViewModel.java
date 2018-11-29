package cy.agorise.crystalwallet.viewmodels;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;

import cy.agorise.crystalwallet.dao.CrystalDatabase;
import cy.agorise.crystalwallet.models.CryptoCurrency;

/**
 * Created by Henry Varona on 4/11/2017.
 */

public class CryptoCurrencyViewModel extends AndroidViewModel {

    private CrystalDatabase db;

    public CryptoCurrencyViewModel(Application application) {
        super(application);
        this.db = CrystalDatabase.getAppDatabase(application.getApplicationContext());
    }

    public CryptoCurrency getCryptoCurrencyById(long id){
        return this.db.cryptoCurrencyDao().getById(id);
    }
}
