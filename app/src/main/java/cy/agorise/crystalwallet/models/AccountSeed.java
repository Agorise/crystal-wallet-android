package cy.agorise.crystalwallet.models;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import org.bitcoinj.core.Base58;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.crypto.HDKeyDerivation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

import cy.agorise.crystalwallet.enums.SeedType;
import cy.agorise.crystalwallet.models.seed.BIP39;
import cy.agorise.graphenej.BrainKey;

import static cy.agorise.crystalwallet.enums.SeedType.BRAINKEY;

/**
 * Represents a type of crypto seed for HD wallets
 *
 * Created by Henry Varona on 6/9/2017.
 */
@Entity(tableName = "account_seed")
public class AccountSeed {

    /**
     * The id on the database
     */
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long mId;

    /**
     * The name or tag of this seed
     */
    @ColumnInfo(name = "name")
    private String mName;

    /**
     * The bytes of the master seed
     */
    @ColumnInfo(name = "master_seed")
    private String mMasterSeed;

    /**
     * The type of this seed: BIP39, BRAINKEY
     */
    private SeedType type;

    public long getId() {
        return mId;
    }

    public void setId(long id){
        this.mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public String getMasterSeed() {
        return mMasterSeed;
    }

    public void setMasterSeed(String mMasterSeed) {
        this.mMasterSeed = mMasterSeed;
    }

    public SeedType getType() {
        return type;
    }

    public void setType(SeedType type) {
        this.type = type;
    }

    public static final DiffUtil.ItemCallback<AccountSeed> DIFF_CALLBACK = new DiffUtil.ItemCallback<AccountSeed>() {
        @Override
        public boolean areItemsTheSame(
                @NonNull AccountSeed oldAccountSeed, @NonNull AccountSeed newAccountSeed) {
            return oldAccountSeed.getId() == newAccountSeed.getId();
        }
        @Override
        public boolean areContentsTheSame(
                @NonNull AccountSeed oldAccountSeed, @NonNull AccountSeed newAccountSeed) {
            return oldAccountSeed.equals(newAccountSeed);
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AccountSeed that = (AccountSeed) o;

        if (mId != that.mId) return false;
        return  mMasterSeed.equals(that.mMasterSeed);

    }

    public static AccountSeed getAccountSeed(SeedType type, Context context){
        BufferedReader reader = null;
        switch (type) {
            case BRAINKEY:
                try {
                    reader = new BufferedReader(new InputStreamReader(context.getAssets().open("brainkeydict.txt"), "UTF-8"));

                String dictionary = reader.readLine();

                String brainKeySuggestion = BrainKey.suggest(dictionary);
                AccountSeed seed = new AccountSeed();
                seed.setMasterSeed(brainKeySuggestion);
                seed.setType(BRAINKEY);
                return seed;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case BIP39:
                try {
                    reader = new BufferedReader(new InputStreamReader(context.getAssets().open("bip39dict.txt"), "UTF-8"));
                    String dictionary = reader.readLine();
                    //TODO save in db
                    return new BIP39(dictionary.split(","));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
        return null;
    }

    public ECKey getPrivateKey(){
        switch (this.type) {
            case BRAINKEY:
                return new BrainKey(this.mMasterSeed,0).getPrivateKey();
            case BIP39:
                return HDKeyDerivation.createMasterPrivateKey(new BIP39(mId, mMasterSeed).getSeed());
            case WIF:
                byte[] decoded = Base58.decode(this.mMasterSeed);
                byte[] privKey = Arrays.copyOfRange(decoded, 1, decoded.length - 4);
                byte[] checksum = Arrays.copyOfRange(decoded, decoded.length - 4, decoded.length);
                //TODO calculate chekcsum
                while(privKey.length>32){
                    privKey = Arrays.copyOfRange(privKey,0,privKey.length-1);
                }
                return ECKey.fromPrivate(privKey);
        }
        return null;
    }
}
