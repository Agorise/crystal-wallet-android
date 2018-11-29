package cy.agorise.crystalwallet.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;

/**
 * This represents the additional info of a bitshares account
 *
 * Created by henry on 24/9/2017.
 */

@Entity(tableName = "graphene_account",
        primaryKeys = {"crypto_net_account_id"},
        foreignKeys = @ForeignKey(entity = CryptoNetAccount.class,
        parentColumns = "id",
        childColumns = "crypto_net_account_id"))
public class GrapheneAccountInfo {

    /**
     * The database id of the cryptonetAccount
     */
    @ColumnInfo(name = "crypto_net_account_id")
    protected long cryptoNetAccountId;

    /**
     * The account name
     */
    @ColumnInfo(name = "account_name")
    protected String name;

    /**
     * The bitshares id of this account
     */
    @ColumnInfo(name = "account_id")
    protected String accountId;

    /**
     * If the bitshares account is upgraded to LTM
     */
    @ColumnInfo(name = "upgraded_to_ltm")
    protected boolean upgradedToLtm;

    /**
     * Baisc constructor
     * @param cryptoNetAccountId The database ud of the CryptoNetAccount
     */
    public GrapheneAccountInfo(long cryptoNetAccountId) {
        this.cryptoNetAccountId = cryptoNetAccountId;
    }

    /**
     * Constructor used to save in the database
     * @param account a complete graphene account with its info
     */
    public GrapheneAccountInfo(GrapheneAccount account) {
        this.cryptoNetAccountId = account.getId();
        this.name = account.getName();
        this.accountId = account.getAccountId();
    }

    public long getCryptoNetAccountId() {
        return cryptoNetAccountId;
    }

    public void setCryptoNetAccountId(long cryptoNetAccountId) {
        this.cryptoNetAccountId = cryptoNetAccountId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public boolean getUpgradedToLtm(){
        return this.upgradedToLtm;
    }

    public void setUpgradedToLtm(boolean upgradedToLtm){
        this.upgradedToLtm = upgradedToLtm;
    }
}
