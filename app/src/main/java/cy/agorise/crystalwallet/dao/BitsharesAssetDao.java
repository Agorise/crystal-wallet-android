package cy.agorise.crystalwallet.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import cy.agorise.crystalwallet.models.BitsharesAssetInfo;

/**
 * Created by henry on 18/10/2017.
 */
@Dao
public interface BitsharesAssetDao {

    @Query("SELECT * FROM bitshares_asset")
    LiveData<List<BitsharesAssetInfo>> getAll();

    @Query("SELECT * FROM bitshares_asset WHERE crypto_curreny_id = :cryptoCurrencyId")
    BitsharesAssetInfo getBitsharesAssetInfo(long cryptoCurrencyId);

    @Query("SELECT * FROM bitshares_asset WHERE crypto_curreny_id = :cryptoCurrencyId")
    BitsharesAssetInfo getBitsharesAssetInfoFromCurrencyId(long cryptoCurrencyId);

    @Query("SELECT * FROM bitshares_asset WHERE bitshares_id = :bitsharesId")
    BitsharesAssetInfo getBitsharesAssetInfoById(String bitsharesId);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public long[] insertBitsharesAssetInfo(BitsharesAssetInfo... accounts);
}
