package cy.agorise.crystalwallet.models;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

/**
 * Represents a Bitcoin alike Transaction
 *
 * Created by Henry Varona on 10/2/2018.
 */
public class BitcoinTransactionExtended {

    @Embedded
    public CryptoCoinTransaction cryptoCoinTransaction;

    @Embedded
    public BitcoinTransaction bitcoinTransaction;

    @Relation(parentColumn = "id", entityColumn = "bitcoin_transaction_id", entity = BitcoinTransactionGTxIO.class)
    public List<BitcoinTransactionGTxIO> bitcoinTransactionGTxIOList;
}
