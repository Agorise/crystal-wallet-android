package cy.agorise.crystalwallet.views;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import cy.agorise.crystalwallet.R;
import cy.agorise.crystalwallet.activities.CreateContactActivity;
import cy.agorise.crystalwallet.dao.CrystalDatabase;
import cy.agorise.crystalwallet.models.Contact;
import cy.agorise.crystalwallet.util.GlideApp;
import cy.agorise.crystalwallet.util.MD5Hash;

/**
 * Created by Henry Varona on 1/17/2017.
 *
 * Represents an element view from the Contact List
 */

public class ContactViewHolder extends RecyclerView.ViewHolder {
    private TextView tvName;
    private ImageView ivThumbnail;
    private TextView tvLastPaid;
    private ImageView ivDeleteContact;
    private Context context;

    ContactViewHolder(View itemView) {
        super(itemView);
        //TODO: use ButterKnife to load this
        tvName = itemView.findViewById(R.id.tvContactName);
        ivThumbnail = itemView.findViewById(R.id.ivContactThumbnail);
        tvLastPaid = itemView.findViewById(R.id.tvLastPaid);
        ivDeleteContact = itemView.findViewById(R.id.ivDeleteContact);
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

            this.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(itemView.getContext(), CreateContactActivity.class);
                    intent.putExtra("CONTACT_ID", contact.getId());
                    itemView.getContext().startActivity(intent);
                }
            });

            if (contact.getEmail() != null){
                String emailHash = MD5Hash.hash(contact.getEmail());
                String gravatarUrl = "http://www.gravatar.com/avatar/" + emailHash + "?s=204&d=404";

                GlideApp.with(context)
                        .load(gravatarUrl)
                        .placeholder(R.drawable.avatar_placeholder)
                        .into(ivThumbnail);
            }

            this.ivDeleteContact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //delete the contact
                    new AlertDialog.Builder(context)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setMessage("Are you sure you want to delete this contact?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                CrystalDatabase.getAppDatabase(context).contactDao().deleteContacts(contact);
                            }

                        })
                        .setNegativeButton("No", null)
                        .show();


                }
            });
        }
    }
}
