package cy.agorise.crystalwallet.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import cy.agorise.crystalwallet.models.BitcoinAddress;

/**
 * Created by Henry Varona on 10/17/2018.
 */
@Dao
public interface BitcoinAddressDao {

    @Query("SELECT * FROM bitcoin_address")
    LiveData<BitcoinAddress> getAll();

    @Query("SELECT COUNT(*) FROM bitcoin_address ba WHERE ba.address = :address")
    Boolean addressExists(String address);

    @Query("SELECT * FROM bitcoin_address ba WHERE ba.address = :address")
    BitcoinAddress getdadress(String address);

    @Query("SELECT * FROM bitcoin_address ba WHERE ba.address_index = :index and ba.is_change = 'true'")
    BitcoinAddress getChangeByIndex(long index);

    @Query("SELECT * FROM bitcoin_address ba WHERE ba.address_index = :index and ba.is_change = 'false'")
    BitcoinAddress getExternalByIndex(long index);

    @Query("SELECT MAX(ba.address_index) FROM bitcoin_address ba WHERE ba.account_id = :accountId and ba.is_change = 'true' ")
    long getLastChangeAddress(long accountId);

    @Query("SELECT MAX(ba.address_index) FROM bitcoin_address ba WHERE ba.account_id = :accountId and ba.is_change = 'false' ")
    long getLastExternalAddress(long accountId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public long[] insertBitcoinAddresses(BitcoinAddress... addresses);
}
