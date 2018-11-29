package cy.agorise.crystalwallet.dao;

import androidx.lifecycle.LiveData;
import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import cy.agorise.crystalwallet.models.Contact;
import cy.agorise.crystalwallet.models.ContactAddress;

/**
 * Created by Henry Varona on 1/17/2018.
 */
@Dao
public interface ContactDao {

    @Query("SELECT * FROM contact")
    LiveData<List<Contact>> getAll();

    @Query("SELECT * FROM contact ORDER BY name ASC")
    DataSource.Factory<Integer, Contact>  contactsByName();

    @Query("SELECT c.* FROM contact c WHERE c.id IN (SELECT DISTINCT(ca.contact_id) FROM contact_address ca WHERE ca.crypto_net == :cryptoNet) ORDER BY name ASC, email ASC")
    DataSource.Factory<Integer, Contact>  contactsByNameAndCryptoNet(String cryptoNet);

    @Query("SELECT * FROM contact WHERE id = :id")
    LiveData<Contact> getById(long id);

    @Query("SELECT count(*) FROM contact WHERE name = :name")
    boolean existsByName(String name);

    @Query("SELECT * FROM contact_address WHERE contact_id = :contactId")
    LiveData<List<ContactAddress>> getContactAddresses(long contactId);

    @Update(onConflict = OnConflictStrategy.ABORT)
    public void update(Contact... contacts);

    @Insert(onConflict = OnConflictStrategy.ABORT)
    public long[] add(Contact... contacts);

    @Insert(onConflict = OnConflictStrategy.ABORT)
    public void addAddresses(ContactAddress... contactAddresses);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    public void updateAddresses(ContactAddress... contactAddresses);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    public void updateAddressesFields(ContactAddress... contactAddresses);

    @Delete
    public void deleteContacts(Contact... contacts);
}
