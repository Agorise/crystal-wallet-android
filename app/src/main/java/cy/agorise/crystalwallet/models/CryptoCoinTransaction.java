package cy.agorise.crystalwallet.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import java.util.Date;

/**
 * Represents a generic CryptoNet Transaction
 *
 * Created by Henry Varona on 11/9/2017.
 */
@Entity(
    tableName="crypto_coin_transaction",
    primaryKeys = {

    },
    indices={
        @Index(value={"account_id"}),
        @Index(value={"id_currency"}),
        @Index(value={"date", "account_id", "id_currency", "from", "to"},unique=true)
    },
    foreignKeys = {
        @ForeignKey(
            entity = CryptoNetAccount.class,
            parentColumns = "id",
            childColumns = "account_id",
            onDelete = ForeignKey.CASCADE
        ),
        @ForeignKey(entity = CryptoCurrency.class,
            parentColumns = "id",
            childColumns = "id_currency",
            onDelete = ForeignKey.CASCADE
        )
    }
)
public class CryptoCoinTransaction {

    /**
     * The account associated with this transaction
     */
    @Ignore
    protected CryptoNetAccount account;

    /**
     * The id on the database
     */
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="id")
    protected long id;
    /**
     * The full date of this transaction
     */
    @ColumnInfo(name="date")
    protected Date date;
    /**
     * If this transaction is input of the account associated with it
     */
    @ColumnInfo(name="is_input")
    protected boolean isInput;
    /**
     * The id of the account assoiciated, this is used for the foreign key definition
     */
    @ColumnInfo(name="account_id")
    protected long accountId = -1;
    /**
     * The amount of asset is moved in this transaction
     */
    @ColumnInfo(name="amount")
    protected long amount;

    /**
     * The id of the Crypto Currency to use in the database
     */
    @ColumnInfo(name="id_currency")
    protected int idCurrency;
    /**
     * If this transaction is confirmed
     */
    @ColumnInfo(name="is_confirmed")
    protected boolean isConfirmed;

    /**
     * The address or account the amount of assets comes from
     */
    @ColumnInfo(name="from")
    protected String from;

    /**
     * The address or account the amount of assets goes to
     */
    @ColumnInfo(name="to")
    protected String to;

    public String getFrom() { return from; }

    public void setFrom(String from) { this.from = from; }

    public String getTo() { return to; }

    public void setTo(String to) { this.to = to; }

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public CryptoNetAccount getAccount() {
        return account;
    }

    public void setAccount(CryptoNetAccount account) {
        this.account = account;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean getInput() {
        return isInput;
    }

    public void setInput(boolean input) {
        this.isInput = input;
    }

    public boolean isConfirmed() { return isConfirmed; }

    public void setConfirmed(boolean confirmed) { isConfirmed = confirmed; }

    public long getAmount() { return amount; }

    public void setAmount(long amount) { this.amount = amount; }

    public int getIdCurrency() { return idCurrency; }

    public void setIdCurrency(int idCurrency) { this.idCurrency = idCurrency; }


    public static final DiffUtil.ItemCallback<CryptoCoinTransaction> DIFF_CALLBACK = new DiffUtil.ItemCallback<CryptoCoinTransaction>() {
        @Override
        public boolean areItemsTheSame(
                @NonNull CryptoCoinTransaction oldTransaction, @NonNull CryptoCoinTransaction newTransaction) {
            return oldTransaction.getId() == newTransaction.getId();
        }
        @Override
        public boolean areContentsTheSame(
                @NonNull CryptoCoinTransaction oldTransaction, @NonNull CryptoCoinTransaction newTransaction) {
            return oldTransaction.equals(newTransaction);
        }
    };


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CryptoCoinTransaction that = (CryptoCoinTransaction) o;

        if (isInput != that.isInput) return false;
        if (accountId != that.accountId) return false;
        if (amount != that.amount) return false;
        if (idCurrency != that.idCurrency) return false;
        if (isConfirmed != that.isConfirmed) return false;
        if (date != null ? !date.equals(that.date) : that.date != null) return false;
        if (from != null ? !from.equals(that.from) : that.from != null) return false;
        return to != null ? to.equals(that.to) : that.to == null;

    }
}
