package cy.agorise.crystalwallet.viewmodels;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import cy.agorise.crystalwallet.dao.CrystalDatabase;
import cy.agorise.crystalwallet.enums.CryptoNet;
import cy.agorise.crystalwallet.models.Contact;

/**
 * Created by Henry Varona on 1/17/2018.
 */

public class ContactListViewModel extends AndroidViewModel {

    private LiveData<PagedList<Contact>> contactList;
    private CrystalDatabase db;

    public ContactListViewModel(Application application) {
        super(application);
        this.db = CrystalDatabase.getAppDatabase(application.getApplicationContext());
        contactList = new LivePagedListBuilder(this.db.contactDao().contactsByName(),
                new PagedList.Config.Builder()
                        .setEnablePlaceholders(true)
                        .setPageSize(10)
                        .setPrefetchDistance(10)
                        .build()
        ).build();
    }

    public void init(CryptoNet cryptoNet){
        contactList = new LivePagedListBuilder(this.db.contactDao().contactsByNameAndCryptoNet(cryptoNet.name()),
                new PagedList.Config.Builder()
                        .setEnablePlaceholders(true)
                        .setPageSize(10)
                        .setPrefetchDistance(10)
                        .build()
        ).build();
    }

    public LiveData<PagedList<Contact>> getContactList(){
        return this.contactList;
    }

    public  boolean contactExists(String name){
        return this.db.contactDao().existsByName(name);
    }
}
