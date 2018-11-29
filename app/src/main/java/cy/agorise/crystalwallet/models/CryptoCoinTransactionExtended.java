package cy.agorise.crystalwallet.models;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import java.util.Date;

/**
 * Represents a generic CryptoNet Transaction
 *
 * Created by Henry Varona on 15/5/2018.
 */
@Entity
public class CryptoCoinTransactionExtended {

    @Embedded
    public CryptoCoinTransaction cryptoCoinTransaction;

    @ColumnInfo(name="user_account_name")
    public String userAccountName;

    @ColumnInfo(name="contact_name")
    public String contactName;

    @ColumnInfo(name="bitshares_account_name")
    public String bitsharesAccountName;

    public String getUserAccountName(){
        return this.userAccountName;
    }

    public String getContactName(){
        return this.contactName;
    }

    public String getBitsharesAccountName(){
        return this.bitsharesAccountName;
    }

    public String getFrom() { return this.cryptoCoinTransaction.getFrom(); }

    public String getTo() { return this.cryptoCoinTransaction.getTo(); }

    public long getAccountId() {
        return this.cryptoCoinTransaction.getAccountId();
    }

    public CryptoNetAccount getAccount() {
        return this.cryptoCoinTransaction.getAccount();
    }

    public long getId() {
        return this.cryptoCoinTransaction.getId();
    }

    public Date getDate() {
        return this.cryptoCoinTransaction.getDate();
    }

    public boolean getInput() {
        return this.cryptoCoinTransaction.getInput();
    }

    public boolean isConfirmed() { return this.cryptoCoinTransaction.isConfirmed(); }

    public long getAmount() { return this.cryptoCoinTransaction.getAmount(); }

    public int getIdCurrency() { return this.cryptoCoinTransaction.getIdCurrency(); }

    public static final DiffUtil.ItemCallback<CryptoCoinTransactionExtended> DIFF_CALLBACK = new DiffUtil.ItemCallback<CryptoCoinTransactionExtended>() {
        @Override
        public boolean areItemsTheSame(
                @NonNull CryptoCoinTransactionExtended oldTransaction, @NonNull CryptoCoinTransactionExtended newTransaction) {
            return oldTransaction.getId() == newTransaction.getId();
        }
        @Override
        public boolean areContentsTheSame(
                @NonNull CryptoCoinTransactionExtended oldTransaction, @NonNull CryptoCoinTransactionExtended newTransaction) {
            return oldTransaction.equals(newTransaction);
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CryptoCoinTransactionExtended that = (CryptoCoinTransactionExtended) o;

        if (this.cryptoCoinTransaction != null ? !this.cryptoCoinTransaction.equals(that.cryptoCoinTransaction) : that.cryptoCoinTransaction != null) return false;
        if (this.userAccountName != null ? !this.userAccountName.equals(that.userAccountName) : that.userAccountName != null) return false;
        if (this.contactName != null ? !this.contactName.equals(that.contactName) : that.contactName != null) return false;

        return true;
    }
}
