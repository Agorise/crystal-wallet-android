package cy.agorise.crystalwallet.views;


import androidx.recyclerview.widget.ListAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cy.agorise.crystalwallet.R;
import cy.agorise.crystalwallet.models.Contact;

/**
 * Created by Henry Varona on 1/16/2018.
 *
 * An adapter to show the elements of a list of contacts
 */

public class ContactListAdapter extends ListAdapter<Contact, ContactViewHolder> {

    public ContactListAdapter() {
        super(Contact.DIFF_CALLBACK);
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_list_item,parent,false);


        return new ContactViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        Contact contact = getItem(position);
        if (contact != null) {
            holder.bindTo(contact);
        } else {
            holder.clear();
        }
    }
}
