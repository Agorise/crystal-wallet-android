package cy.agorise.crystalwallet.models;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import java.util.ArrayList;
import java.util.List;

import cy.agorise.crystalwallet.enums.CryptoNet;

/**
 * Represents a user contact
 *
 * Created by Henry Varona on 1/16/2018.
 */

@Entity(tableName="contact",
        indices = {@Index("id"),@Index(value = {"name"}, unique=true),@Index("email")})
public class Contact {

    /**
     * The id on the database
     */
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long mId;

    @ColumnInfo(name="name")
    private String mName;

    @ColumnInfo(name="email")
    private String mEmail;

    @ColumnInfo(name = "gravatar")
    private String mGravatar;

    @Ignore
    public List<ContactAddress> mAddresses;

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        this.mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getGravatar() {
        return mGravatar;
    }

    public void setGravatar(String gravatar) {
        this.mGravatar = gravatar;
    }

    public String getEmail() {
        return this.mEmail;
    }

    public void setEmail(String email) {
        this.mEmail = email;
    }

    public int addressesCount(){
        return this.mAddresses.size();
    }

    public ContactAddress getAddress(int index){
        return this.mAddresses.get(index);
    }

    public void clearAddresses(){
        if (this.mAddresses != null) {
            this.mAddresses.clear();
        }
    }

    public void addAddress(ContactAddress address){
        if (this.mAddresses == null) {
            this.mAddresses = new ArrayList<ContactAddress>();
        }
        this.mAddresses.add(address);
        address.setContactId(this.getId());
    }

    public ContactAddress getCryptoNetAddress(CryptoNet cryptoNet){
        if (this.mAddresses != null) {
            for (ContactAddress address : this.mAddresses) {
                if (address.getCryptoNet() == cryptoNet) {
                    return address;
                }
            }
        }

        return null;
    }

    public static final DiffUtil.ItemCallback<Contact> DIFF_CALLBACK = new DiffUtil.ItemCallback<Contact>() {
        @Override
        public boolean areItemsTheSame(
                @NonNull Contact oldContact, @NonNull Contact newContact) {
            return oldContact.getId() == newContact.getId();
        }
        @Override
        public boolean areContentsTheSame(
                @NonNull Contact oldContact, @NonNull Contact newContact) {
            return oldContact.equals(newContact);
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Contact contact = (Contact) o;

        if (mId != contact.mId) return false;
        if (!mName.equals(contact.mName)) return false;
        if (mGravatar != null ? !mGravatar.equals(contact.mGravatar) : contact.mGravatar != null)
            return false;
        return mAddresses != null ? mAddresses.equals(contact.mAddresses) : contact.mAddresses == null;
    }
}
