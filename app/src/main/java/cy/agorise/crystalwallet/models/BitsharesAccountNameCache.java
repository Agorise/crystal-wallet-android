package cy.agorise.crystalwallet.models;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

/**
 * Represents a cache of a Bitshares Account name
 *
 * Created by Henry Varona on 6/13/2018.
 */

@Entity(tableName = "bitshares_account_name_cache",
        indices = {@Index("id"),
                @Index(value = {"account_id"},unique = true)})
public class BitsharesAccountNameCache {

    /**
     * The id on the database
     */
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long mId;

    /**
     * The id of the account
     */
    @ColumnInfo(name = "account_id")
    @NonNull
    private String mAccountId;

    /*
     * The name of the account
     */
    @ColumnInfo(name = "name")
    private String mName;

    public BitsharesAccountNameCache() {
    }

    @Ignore
    public BitsharesAccountNameCache(long id, String accountId, String name) {
        this.mId = id;
        this.mAccountId = accountId;
        this.mName = name;
    }

    public long getId() {
        return mId;
    }

    public void setId(long id){
        this.mId = id;
    }

    public String getAccountId() {
        return mAccountId;
    }

    public void setAccountId(String accountId) {
        this.mAccountId = accountId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }
}
