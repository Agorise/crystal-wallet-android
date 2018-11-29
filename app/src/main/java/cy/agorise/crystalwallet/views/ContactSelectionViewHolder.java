package cy.agorise.crystalwallet.views;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;

import cy.agorise.crystalwallet.R;
import cy.agorise.crystalwallet.models.Contact;
import cy.agorise.crystalwallet.util.GlideApp;
import cy.agorise.crystalwallet.util.MD5Hash;

/**
 * Created by Henry Varona on 2/16/2018.
 *
 * Represents an element view from the Contact Selection List
 */

public class ContactSelectionViewHolder extends RecyclerView.ViewHolder {
    private TextView tvName;
    private ImageView ivThumbnail;
    private TextView tvLastPaid;
    private Context context;
    private ContactSelectionViewHolderListener listener;

    public ContactSelectionViewHolder(View itemView) {
        super(itemView);
        //TODO: use ButterKnife to load this
        tvName = (TextView) itemView.findViewById(R.id.tvContactName);
        ivThumbnail = (ImageView) itemView.findViewById(R.id.ivContactThumbnail);
        tvLastPaid = (TextView) itemView.findViewById(R.id.tvLastPaid);
        this.context = itemView.getContext();

    }

    public void setListener(ContactSelectionViewHolderListener listener){
        this.listener = listener;
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
            final ContactSelectionViewHolder thisViewHolder = this;

            tvName.setText(contact.getName());
            tvLastPaid.setText("Paid: 1 Jan, 2001 01:01");

            if (contact.getEmail() != null){
                String emailHash = MD5Hash.hash(contact.getEmail());
                String gravatarUrl = "http://www.gravatar.com/avatar/" + emailHash + "?s=204&d=404";

                GlideApp.with(this.context)
                        .load(gravatarUrl)
                        .apply(RequestOptions.circleCropTransform())
                        .into(ivThumbnail);
            }

            this.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null){
                        listener.onContactSelected(thisViewHolder, contact);
                    }
                }
            });
        }
    }

    public interface ContactSelectionViewHolderListener {
        public void onContactSelected(ContactSelectionViewHolder contactSelectionViewHolder, Contact contact);
    }
}
