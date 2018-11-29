package cy.agorise.crystalwallet.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import cy.agorise.crystalwallet.models.BitcoinTransaction;
import cy.agorise.crystalwallet.models.BitcoinTransactionExtended;
import cy.agorise.crystalwallet.models.BitcoinTransactionGTxIO;

/**
 * Created by Henry Varona on 10/02/2018.
 */
@Dao
public interface BitcoinTransactionDao {

    @Query("SELECT * FROM crypto_coin_transaction cct, bitcoin_transaction bt WHERE bt.crypto_coin_transaction_id = cct.id")
    LiveData<BitcoinTransactionExtended> getAll();

    @Query("SELECT * FROM bitcoin_transaction bt WHERE bt.tx_id = :txid")
    List<BitcoinTransaction> getTransactionsByTxid(String txid);

    @Query("SELECT * FROM bitcoin_transaction bt WHERE bt.crypto_coin_transaction_id = :idCryptoCoinTransaction")
    BitcoinTransaction getBitcoinTransactionByCryptoCoinTransaction(long idCryptoCoinTransaction);

    @Query("SELECT * FROM bitcoin_transaction_gt_io bt WHERE bt.bitcoin_transaction_id= :idBitcoinTransaction")
    List<BitcoinTransactionGTxIO> getGtxIOByTransaction(long idBitcoinTransaction);

    @Query("SELECT * FROM bitcoin_transaction_gt_io bt WHERE bt.address= :address")
    List<BitcoinTransactionGTxIO> getGtxIOByAddress(String address);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public long[] insertBitcoinTransaction(BitcoinTransaction... transactions);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public long[] insertBitcoinTransactionGTxIO(BitcoinTransactionGTxIO... transactiongtxios);
}
