package cy.agorise.crystalwallet.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import cy.agorise.crystalwallet.models.CryptoCurrencyEquivalence;

/**
 * Created by henry on 15/10/2017.
 */

@Dao
public interface CryptoCurrencyEquivalenceDao {

    @Query("SELECT * FROM crypto_currency_equivalence")
    List<CryptoCurrencyEquivalence> getAll();

    @Query("SELECT * FROM crypto_currency_equivalence WHERE id = :id")
    CryptoCurrencyEquivalence getById(long id);

    @Query("SELECT * FROM crypto_currency_equivalence WHERE from_crypto_currency_id = :fromCurrencyId AND to_crypto_currency_id = :toCurrencyId")
    LiveData<CryptoCurrencyEquivalence> getByFromTo(long fromCurrencyId, long toCurrencyId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public long[] insertCryptoCurrencyEquivalence(CryptoCurrencyEquivalence... currenciesEquivalences);

}
