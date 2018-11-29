package cy.agorise.crystalwallet.views;


import androidx.recyclerview.widget.ListAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cy.agorise.crystalwallet.R;
import cy.agorise.crystalwallet.models.Contact;

/**
 * Created by Henry Varona on 2/16/2018.
 *
 * An adapter to show the elements of a list of contacts to be selected by the user
 */

public class ContactSelectionListAdapter extends ListAdapter<Contact, ContactSelectionViewHolder> implements ContactSelectionViewHolder.ContactSelectionViewHolderListener {

    private ContactSelectionListAdapterListener listener;

    public ContactSelectionListAdapter() {
        super(Contact.DIFF_CALLBACK);
    }

    @Override
    public ContactSelectionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_selection_list_item,parent,false);
        return new ContactSelectionViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ContactSelectionViewHolder holder, int position) {
        Contact contact = getItem(position);
        if (contact != null) {
            holder.bindTo(contact);
            holder.setListener(this);
        } else {
            holder.clear();
        }
    }

    @Override
    public void onContactSelected(ContactSelectionViewHolder contactSelectionViewHolder, Contact contact) {
        if (this.listener != null){
            this.listener.onContactSelected(contact);
        }
    }

    public void setListener(ContactSelectionListAdapterListener listener){
        this.listener = listener;
    }

    public interface ContactSelectionListAdapterListener{
        public void onContactSelected(Contact contact);
    }
}
