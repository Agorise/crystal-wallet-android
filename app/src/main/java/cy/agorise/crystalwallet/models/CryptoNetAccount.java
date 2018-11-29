package cy.agorise.crystalwallet.models;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;


import cy.agorise.crystalwallet.enums.CryptoNet;

/**
 * Represents a generic CryptoNet Account
 *
 * Created by Henry Varona on 6/9/2017.
 */

@Entity(tableName = "crypto_net_account",
        indices = {@Index("id"),@Index("seed_id"),@Index(value = {"seed_id","crypto_net","account_index"},unique=true)},
        foreignKeys = @ForeignKey(entity = AccountSeed.class,
        parentColumns = "id",
        childColumns = "seed_id"))
public class CryptoNetAccount {

    /**
     * The id on the database
     */
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long mId;

    /**
     * The id of the seed used by this account
     */
    @ColumnInfo(name = "seed_id")
    private long mSeedId;

    /**
     * The account index on this wallet
     */
    @ColumnInfo(name = "account_index")
    private int mAccountIndex;

    /**
     * The crypto net of the account
     */
    @ColumnInfo(name = "crypto_net")
    private CryptoNet mCryptoNet;

    /*
     * The name of the account
     */
    @ColumnInfo(name = "name")
    private String mName;

    public CryptoNetAccount() {
    }

    @Ignore
    public CryptoNetAccount(long mId, long mSeedId, int mAccountIndex, CryptoNet mCryptoNet) {
        this.mId = mId;
        this.mSeedId = mSeedId;
        this.mAccountIndex = mAccountIndex;
        this.mCryptoNet = mCryptoNet;
    }

    public long getId() {
        return mId;
    }

    public void setId(long id){
        this.mId = id;
    }

    public long getSeedId() {
        return mSeedId;
    }

    public void setSeedId(long mSeedId) {
        this.mSeedId = mSeedId;
    }

    public int getAccountIndex() {
        return mAccountIndex;
    }

    public void setAccountIndex(int mAccountIndex) {
        this.mAccountIndex = mAccountIndex;
    }

    public CryptoNet getCryptoNet() {
        return mCryptoNet;
    }

    public void setCryptoNet(CryptoNet cryptoNet) {
        this.mCryptoNet = cryptoNet;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public String toString(){
        return this.getName();
    }

    public static final DiffUtil.ItemCallback<CryptoNetAccount> DIFF_CALLBACK = new DiffUtil.ItemCallback<CryptoNetAccount>() {
        @Override
        public boolean areItemsTheSame(
                @NonNull CryptoNetAccount oldAccount, @NonNull CryptoNetAccount newAccount) {
            return oldAccount.getId() == newAccount.getId();
        }
        @Override
        public boolean areContentsTheSame(
                @NonNull CryptoNetAccount oldAccount, @NonNull CryptoNetAccount newAccount) {
            return oldAccount.equals(newAccount);
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CryptoNetAccount that = (CryptoNetAccount) o;

        if (mId != that.mId) return false;
        if (mSeedId != that.mSeedId) return false;
        if (mAccountIndex != that.mAccountIndex) return false;
        if (mCryptoNet != that.mCryptoNet) return false;
        return mName != null ? mName.equals(that.mName) : that.mName == null;
    }
}
