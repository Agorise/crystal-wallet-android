package cy.agorise.crystalwallet.models;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import cy.agorise.crystalwallet.enums.CryptoNet;

/**
 * Created by Henry Varona on 6/9/2017.
 *
 * Represents a generic Account Balance from a specific CryptoNet
 */

@Entity
public class CryptoNetBalance {

    /**
     * The id of the account of this balance
     */
    @ColumnInfo(name = "account_id")
    private long mAccountId;

    /**
     * The crypto net of the account
     */
    @ColumnInfo(name = "crypto_net")
    private CryptoNet mCryptoNet;

    public long getAccountId() {
        return mAccountId;
    }

    public CryptoNet getCryptoNet() {
        return mCryptoNet;
    }

    public void setAccountId(long accountId) {
        this.mAccountId = accountId;
    }

    public void setCryptoNet(CryptoNet cryptoNet) {
        this.mCryptoNet = cryptoNet;
    }

    public static final DiffUtil.ItemCallback<CryptoNetBalance> DIFF_CALLBACK = new DiffUtil.ItemCallback<CryptoNetBalance>() {
        @Override
        public boolean areItemsTheSame(
                @NonNull CryptoNetBalance oldBalance, @NonNull CryptoNetBalance newBalance) {
            return oldBalance.getAccountId() == newBalance.getAccountId();
        }
        @Override
        public boolean areContentsTheSame(
                @NonNull CryptoNetBalance oldBalance, @NonNull CryptoNetBalance newBalance) {
            return oldBalance.equals(newBalance);
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CryptoNetBalance that = (CryptoNetBalance) o;
        return mAccountId == that.mAccountId;

    }
}
