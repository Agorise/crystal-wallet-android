package cy.agorise.crystalwallet.viewmodels;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import cy.agorise.crystalwallet.dao.CrystalDatabase;
import cy.agorise.crystalwallet.models.CryptoCurrencyEquivalence;

/**
 * Created by Henry Varona on 4/11/2017.
 */

public class CryptoCurrencyEquivalenceViewModel extends AndroidViewModel {

    private LiveData<CryptoCurrencyEquivalence> cryptoCurrencyEquivalence;
    private CrystalDatabase db;

    public CryptoCurrencyEquivalenceViewModel(Application application) {
        super(application);
        this.db = CrystalDatabase.getAppDatabase(application.getApplicationContext());
    }

    public void init(long fromCurrency, long toCurrency){
        this.cryptoCurrencyEquivalence = this.db.cryptoCurrencyEquivalenceDao().getByFromTo(fromCurrency,toCurrency);
    }

    public LiveData<CryptoCurrencyEquivalence> getCryptoCurrencyEquivalence(){
        return this.cryptoCurrencyEquivalence;
    }
}
