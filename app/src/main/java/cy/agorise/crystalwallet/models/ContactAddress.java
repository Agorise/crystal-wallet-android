package cy.agorise.crystalwallet.models;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import cy.agorise.crystalwallet.enums.CryptoNet;

/**
 * Represents a user contact address
 *
 * Created by Henry Varona on 1/16/2018.
 */

@Entity(tableName="contact_address",
        indices = {@Index(value = {"id"}, unique=true),@Index(value = {"contact_id","crypto_net"}, unique=true)})
public class ContactAddress {

    /**
     * The id on the database
     */
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long mId;

    @ColumnInfo(name = "contact_id")
    private long mContactId;

    /**
     * The crypto net of the address
     */
    @NonNull
    @ColumnInfo(name = "crypto_net")
    private CryptoNet mCryptoNet;

    @ColumnInfo(name="address")
    private String mAddress;

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        this.mId = id;
    }

    public long getContactId() {
        return mContactId;
    }

    public void setContactId(long contactId) {
        this.mContactId = contactId;
    }

    public CryptoNet getCryptoNet() {
        return mCryptoNet;
    }

    public void setCryptoNet(CryptoNet cryptoNet) {
        this.mCryptoNet = cryptoNet;
    }

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String address) {
        this.mAddress = address;
    }

   public static final DiffUtil.ItemCallback<ContactAddress> DIFF_CALLBACK = new DiffUtil.ItemCallback<ContactAddress>() {
        @Override
        public boolean areItemsTheSame(
                @NonNull ContactAddress oldContactAddress, @NonNull ContactAddress newContactAddress) {
            return (oldContactAddress.getContactId() == newContactAddress.getContactId())
                    && (oldContactAddress.getCryptoNet() == newContactAddress.getCryptoNet());
        }
        @Override
        public boolean areContentsTheSame(
                @NonNull ContactAddress oldContactAddress, @NonNull ContactAddress newContactAddress) {
            return oldContactAddress.equals(newContactAddress);
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ContactAddress that = (ContactAddress) o;

        if (mContactId != that.mContactId) return false;
        if (mCryptoNet != that.mCryptoNet) return false;
        return mAddress.equals(that.mAddress);
    }
}
