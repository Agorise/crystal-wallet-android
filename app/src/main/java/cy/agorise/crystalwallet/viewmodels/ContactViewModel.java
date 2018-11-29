package cy.agorise.crystalwallet.viewmodels;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import cy.agorise.crystalwallet.dao.CrystalDatabase;
import cy.agorise.crystalwallet.models.Contact;
import cy.agorise.crystalwallet.models.ContactAddress;

/**
 * Created by Henry Varona on 2/4/2018.
 */

public class ContactViewModel extends AndroidViewModel {

    private CrystalDatabase db;
    private LiveData<Contact> contact;
    private LiveData<List<ContactAddress>> contactAddresses;

    public ContactViewModel(Application application) {
        super(application);
        this.db = CrystalDatabase.getAppDatabase(application.getApplicationContext());
    }

    public void init(long contactId){
        this.contact = this.db.contactDao().getById(contactId);
        this.contactAddresses = this.db.contactDao().getContactAddresses(contactId);
    }

    public LiveData<Contact> getContact(){
        return this.contact;
    }

    public LiveData<List<ContactAddress>> getContactAddresses(){
        return this.contactAddresses;
    }

    public boolean modifyContact(Contact contact){
        this.db.contactDao().update(contact);

        for (int i=0;i<contact.addressesCount();i++){
            ContactAddress nextAddress = contact.getAddress(i);
            if (nextAddress.getId() > 0){
                this.db.contactDao().updateAddresses(nextAddress);
            } else {
                nextAddress.setContactId(contact.getId());
                this.db.contactDao().addAddresses(nextAddress);
            }
        }

        return true;
    }

    public boolean addContact(Contact contact){
        long newContactId = this.db.contactDao().add(contact)[0];
        boolean contactWasAdded = newContactId >= 0;

        for (int i=0;i<contact.addressesCount();i++){
            ContactAddress nextAddress = contact.getAddress(i);
            nextAddress.setContactId(newContactId);
            this.db.contactDao().addAddresses(nextAddress);
        }

        return contactWasAdded;
    }
}
