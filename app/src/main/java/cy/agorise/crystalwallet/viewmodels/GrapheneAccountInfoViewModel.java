package cy.agorise.crystalwallet.viewmodels;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import cy.agorise.crystalwallet.dao.CrystalDatabase;
import cy.agorise.crystalwallet.models.GrapheneAccountInfo;

/**
 * Created by Henry Varona on 21/10/2017.
 */

public class GrapheneAccountInfoViewModel extends AndroidViewModel {

    private LiveData<GrapheneAccountInfo> grapheneAccountInfo;
    private CrystalDatabase db;

    public GrapheneAccountInfoViewModel(Application application) {
        super(application);
        this.db = CrystalDatabase.getAppDatabase(application.getApplicationContext());
    }

    public void loadGrapheneAccountInfo(int accountId){
        this.grapheneAccountInfo = this.db.grapheneAccountInfoDao().getGrapheneAccountInfo(accountId);
    }

    public void addGrapheneAccountInfo(GrapheneAccountInfo account){
        this.db.cryptoNetAccountDao().insertCryptoNetAccount();

        this.db.grapheneAccountInfoDao().insertGrapheneAccountInfo(account);
    }

    public LiveData<GrapheneAccountInfo> getGrapheneAccountInfo(){
        return this.grapheneAccountInfo;
    }

}
