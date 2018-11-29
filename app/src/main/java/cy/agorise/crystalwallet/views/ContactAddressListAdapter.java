package cy.agorise.crystalwallet.views;


import androidx.recyclerview.widget.ListAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cy.agorise.crystalwallet.R;
import cy.agorise.crystalwallet.models.ContactAddress;

/**
 * Created by Henry Varona on 2/5/2018.
 *
 * An adapter to show the elements of a list of contacts addresses
 */


public class ContactAddressListAdapter extends ListAdapter<ContactAddress, ContactAddressViewHolder> {

    public ContactAddressListAdapter() {
        super(ContactAddress.DIFF_CALLBACK);
    }

    @Override
    public ContactAddressViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_address_list_item,parent,false);

        return new ContactAddressViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ContactAddressViewHolder holder, int position) {
        ContactAddress contactAddress = getItem(position);
        if (contactAddress != null) {
            holder.bindTo(contactAddress);
        } else {
            holder.clear();
        }
    }
}
