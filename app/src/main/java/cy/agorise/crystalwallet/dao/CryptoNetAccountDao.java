package cy.agorise.crystalwallet.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import cy.agorise.crystalwallet.models.AccountSeed;
import cy.agorise.crystalwallet.models.CryptoCoinBalance;
import cy.agorise.crystalwallet.models.CryptoNetAccount;
import cy.agorise.crystalwallet.models.CryptoNetBalance;

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
