package cy.agorise.crystalwallet.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import cy.agorise.crystalwallet.models.CryptoCurrency;

/**
 * Created by henry on 15/10/2017.
 */

@Dao
public interface CryptoCurrencyDao {

    @Query("SELECT * FROM crypto_currency")
    List<CryptoCurrency> getAll();

    @Query("SELECT * FROM crypto_currency WHERE id = :id")
    CryptoCurrency getById(long id);

    @Query("SELECT * FROM crypto_currency WHERE id = :id")
    LiveData<CryptoCurrency> getLDById(long id);

    @Query("SELECT * FROM crypto_currency WHERE name = :name AND crypto_net = :cryptoNet")
    CryptoCurrency getByNameAndCryptoNet(String name,String cryptoNet);

    @Query("SELECT * FROM crypto_currency WHERE id IN (:ids)")
    List<CryptoCurrency> getByIds(List<Long> ids);

    @Query("SELECT * FROM crypto_currency WHERE name = :name")
    LiveData<CryptoCurrency> getLiveDataByName(String name);

    @Query("SELECT * FROM crypto_currency WHERE name = :name and crypto_net = :cryptoNet")
    CryptoCurrency getByName(String name, String cryptoNet);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public long[] insertCryptoCurrency(CryptoCurrency... currencies);

}
