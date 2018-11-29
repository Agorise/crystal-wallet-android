package cy.agorise.crystalwallet.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import cy.agorise.crystalwallet.models.AccountSeed;

/**
 * Created by Henry Varona on 10/9/2017.
 */

@Dao
public interface AccountSeedDao {

    @Query("SELECT * FROM account_seed")
    LiveData<List<AccountSeed>> getAll();

    @Query("SELECT * FROM account_seed")
    List<AccountSeed> getAllNoLiveData();

    @Query("SELECT * FROM account_seed WHERE id = :id")
    LiveData<AccountSeed> findByIdLiveData(long id);

    @Query("SELECT * FROM account_seed WHERE id = :id")
    AccountSeed findById(long id);

    @Query("SELECT COUNT(*) from account_seed")
    int countAccountSeeds();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public long[] insertAccountSeeds(AccountSeed... seeds);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public long insertAccountSeed(AccountSeed seed);
}
