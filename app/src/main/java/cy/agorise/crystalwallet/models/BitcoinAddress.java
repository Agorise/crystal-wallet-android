package cy.agorise.crystalwallet.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.annotation.NonNull;

/**
 * Represents a Bitcoin derivated address
 *
 * Created by Henry Varona on 10/17/2018.
 */
@Entity(
    tableName="bitcoin_address",
    primaryKeys = {"account_id","address_index","is_change"},
    foreignKeys = {
        @ForeignKey(
                entity = CryptoNetAccount.class,
                parentColumns = "id",
                childColumns = "account_id",
                onDelete = ForeignKey.CASCADE
        )
    }
)
public class BitcoinAddress {

    /**
     * The id of the account associated
     */
    @ColumnInfo(name="account_id")
    protected long accountId;

    /**
     * The index of this address
     */
    @ColumnInfo(name="address_index")
    @NonNull protected long index;

    /**
     * Whether or not this address is a change one
     */
    @ColumnInfo(name="is_change")
    @NonNull protected boolean isChange;

    /**
     * Address
     */
    @ColumnInfo(name="address")
    @NonNull protected String address;

    public BitcoinAddress(long accountId, @NonNull long index, boolean isChange, String address) {
        this.accountId = accountId;
        this.index = index;
        this.isChange = isChange;
        this.address = address;
    }

    public BitcoinAddress() {
    }

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    @NonNull
    public long getIndex() {
        return index;
    }

    public void setIndex(@NonNull long index) {
        this.index = index;
    }

    public boolean isChange() {
        return isChange;
    }

    public void setChange(boolean change) {
        isChange = change;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
