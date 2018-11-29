package cy.agorise.crystalwallet.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import cy.agorise.crystalwallet.models.GrapheneAccountInfo;

/**
 * Created by Henry Varona on 10/9/2017.
 */

@Dao
public interface GrapheneAccountInfoDao {

    @Query("SELECT * FROM graphene_account")
    LiveData<List<GrapheneAccountInfo>> getAll();

    @Query("SELECT * FROM graphene_account WHERE crypto_net_account_id = :cryptoNetAccountId")
    LiveData<GrapheneAccountInfo> getGrapheneAccountInfo(long cryptoNetAccountId);

    @Query("SELECT * FROM graphene_account WHERE crypto_net_account_id = :cryptoNetAccountId")
    GrapheneAccountInfo getByAccountId(long cryptoNetAccountId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public long[] insertGrapheneAccountInfo(GrapheneAccountInfo... accounts);

}
