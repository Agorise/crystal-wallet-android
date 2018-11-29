package cy.agorise.crystalwallet.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import cy.agorise.crystalwallet.models.CryptoNetAccount;

/**
 * Created by Henry Varona on 10/9/2017.
 */

@Dao
public interface CryptoNetAccountDao {

    @Query("SELECT * FROM crypto_net_account")
    LiveData<List<CryptoNetAccount>> getAll();

    @Query("SELECT cna.* FROM crypto_net_account cna")
    List<CryptoNetAccount> getAllCryptoNetAccount();

    @Query("SELECT cna.* FROM crypto_net_account cna WHERE seed_id = :seedId")
    List<CryptoNetAccount> getAllCryptoNetAccountBySeed( long seedId);

    @Query("SELECT cna.* FROM crypto_net_account cna WHERE crypto_net == 'BITCOIN'")
    LiveData<List<CryptoNetAccount>>  getAllBitcoins();

    @Query("SELECT * FROM crypto_net_account WHERE id = :accountId")
    LiveData<CryptoNetAccount> getByIdLiveData( long accountId);

    @Query("SELECT * FROM crypto_net_account WHERE id = :accountId")
    CryptoNetAccount getById( long accountId);

    @Query("SELECT * FROM crypto_net_account WHERE crypto_net = 'BITSHARES'")
    LiveData<List<CryptoNetAccount>> getBitsharesAccounts();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public long[]  insertCryptoNetAccount(CryptoNetAccount... accounts);
}
