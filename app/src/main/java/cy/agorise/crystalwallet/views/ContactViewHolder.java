package cy.agorise.crystalwallet.views;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import cy.agorise.crystalwallet.R;
import cy.agorise.crystalwallet.models.Contact;

/**
 * Created by Henry Varona on 1/17/2017.
 *
 * Represents an element view from the Contact List
 */

public class ContactViewHolder extends RecyclerView.ViewHolder {
    private TextView tvName;
    private ImageView ivThumbnail;
    private TextView tvLastPaid;
    private Context context;

    public ContactViewHolder(View itemView) {
        super(itemView);
        //TODO: use ButterKnife to load this
        tvName = (TextView) itemView.findViewById(R.id.tvContactName);
        ivThumbnail = (ImageView) itemView.findViewById(R.id.ivContactThumbnail);
        tvLastPaid = (TextView) itemView.findViewById(R.id.tvLastPaid);
        this.context = itemView.getContext();

    }

    /*
     * Clears the information in this element view
     */
    public void clear(){
        tvName.setText("");
        ivThumbnail.setImageResource(android.R.color.transparent);
        tvLastPaid.setText("");
    }

    /*
     * Binds this view with the data of an element of the list
     */
    public void bindTo(final Contact contact) {
        if (contact == null){
            this.clear();
        } else {
            tvName.setText(contact.getName());
            tvLastPaid.setText("Paid: 1 Jan, 2001 01:01");
        }
    }
}