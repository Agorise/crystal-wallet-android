package cy.agorise.crystalwallet.viewmodels;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.paging.DataSource;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import cy.agorise.crystalwallet.dao.CrystalDatabase;
import cy.agorise.crystalwallet.models.CryptoCoinTransactionExtended;

/**
 * Created by Henry Varona on 12/9/2017.
 */

public class TransactionListViewModel extends AndroidViewModel {

    private LiveData<PagedList<CryptoCoinTransactionExtended>> transactionList;
    private CrystalDatabase db;

    public TransactionListViewModel(Application application) {
        super(application);
        this.db = CrystalDatabase.getAppDatabase(application.getApplicationContext());
        /*transactionList = this.db.transactionDao().transactionsByDate().create(0,
                new PagedList.Config.Builder()
                        .setEnablePlaceholders(true)
                        .setPageSize(10)
                        .setPrefetchDistance(10)
                        .build()
        );*/
    }

    public void initTransactionList(String orderField, String search){
        DataSource.Factory<Integer, CryptoCoinTransactionExtended> dataSource = null;

        switch (orderField){
            case "date":
                dataSource = this.db.transactionDao().transactionsByDate(search);
                break;
            case "amount":
                dataSource = this.db.transactionDao().transactionsByAmount(search);
                break;
            case "is_input":
                dataSource = this.db.transactionDao().transactionsByIsInput(search);
                break;
            case "from":
                dataSource = this.db.transactionDao().transactionsByFrom(search);
                break;
            case "to":
                dataSource = this.db.transactionDao().transactionsByTo(search);
                break;
            default:
                dataSource = this.db.transactionDao().transactionsByDate(search);
        }
        if (dataSource != null) {
            this.transactionList = new LivePagedListBuilder(dataSource,
                    new PagedList.Config.Builder()
                            .setEnablePlaceholders(true)
                            .setPageSize(10)
                            .setPrefetchDistance(10)
                            .build()
            ).build();
        } else {
            this.transactionList = null;
        }
    }

    public LiveData<PagedList<CryptoCoinTransactionExtended>> getTransactionList(){
        return this.transactionList;
    }
}
