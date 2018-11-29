package cy.agorise.crystalwallet.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;

/**
 * Represents a Bitcoin alike Transaction Inputs and Outputs
 *
 * Created by Henry Varona on 10/2/2018.
 */
@Entity(
    tableName="bitcoin_transaction_gt_io",
    primaryKeys = {"bitcoin_transaction_id", "io_index", "is_output"},
    foreignKeys = {
        @ForeignKey(
            entity = BitcoinTransaction.class,
            parentColumns = "crypto_coin_transaction_id",
            childColumns = "bitcoin_transaction_id",
            onDelete = ForeignKey.CASCADE
        )
    }
)
public class BitcoinTransactionGTxIO {

    /**
     * The id of the bitcoin transaction
     */
    @ColumnInfo(name="bitcoin_transaction_id")
    protected long bitcoinTransactionId;

    /**
     * The index in the transaction
     */
    @ColumnInfo(name="io_index")
    protected int index;

    /**
     * The address of the input or output
     */
    @ColumnInfo(name="address")
    protected String address;

    /**
     * determines if this is an input or output
     */
    @ColumnInfo(name="is_output")
    protected boolean isOutput;

    @ColumnInfo(name="amount")
    protected long amount;

    @ColumnInfo(name="script_hex")
    protected String scriptHex;

    @ColumnInfo(name="original_txid")
    protected String originalTxId;

    public BitcoinTransactionGTxIO() {
    }

    public BitcoinTransactionGTxIO(long bitcoinTransactionId, int index, String address, boolean isOutput) {
        this.bitcoinTransactionId = bitcoinTransactionId;
        this.index = index;
        this.address = address;
        this.isOutput = isOutput;
    }

    public long getBitcoinTransactionId() {
        return bitcoinTransactionId;
    }

    public void setBitcoinTransactionId(long bitcoinTransactionId) {
        this.bitcoinTransactionId = bitcoinTransactionId;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isOutput() {
        return isOutput;
    }

    public void setOutput(boolean output) {
        isOutput = output;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public String getScriptHex() {
        return scriptHex;
    }

    public void setScriptHex(String scriptHex) {
        this.scriptHex = scriptHex;
    }

    public String getOriginalTxId() {
        return originalTxId;
    }

    public void setOriginalTxId(String originalTxId) {
        this.originalTxId = originalTxId;
    }
}
