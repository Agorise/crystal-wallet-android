package cy.agorise.crystalwallet.views;

import androidx.paging.PagedListAdapter;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cy.agorise.crystalwallet.R;
import cy.agorise.crystalwallet.models.CryptoCoinTransactionExtended;

/**
 * Created by Henry Varona on 11/9/2017.
 *
 * An adapter to show the elements of a list of crypto net account transactions.
 *
 * Extends from a paged list, so not all transactions will be loaded immediately, but only a segment
 * that will be extended with the scroll of the user
 */

public class TransactionListAdapter extends PagedListAdapter<CryptoCoinTransactionExtended, TransactionViewHolder> {

    private Fragment fragment;

    public TransactionListAdapter(Fragment fragment) {
        super(CryptoCoinTransactionExtended.DIFF_CALLBACK);
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_list_item,parent,false);


        return new TransactionViewHolder(v, this.fragment);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        CryptoCoinTransactionExtended transaction = getItem(position);
        if (transaction != null) {
            holder.bindTo(transaction);
        } else {
            holder.clear();
        }
    }
}
