package cy.agorise.crystalwallet.views;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ListAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cy.agorise.crystalwallet.R;
import cy.agorise.crystalwallet.models.CryptoNetBalance;

/**
 * Created by Henry Varona on 11/9/2017.
 *
 * An adapter to show the elements of a list of crypto net balances
 */

public class CryptoNetBalanceListAdapter extends ListAdapter<CryptoNetBalance, CryptoNetBalanceViewHolder> {

    /*
     * A LifecycleOwner fragment that will be used to call the ViewModelProviders
     */
    private Fragment fragment;

    public CryptoNetBalanceListAdapter(Fragment fragment) {
        super(CryptoNetBalance.DIFF_CALLBACK);
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public CryptoNetBalanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.balance_list_item,parent,false);

        return new CryptoNetBalanceViewHolder(v, fragment);
    }

    @Override
    public void onBindViewHolder(@NonNull CryptoNetBalanceViewHolder holder, int position) {
        CryptoNetBalance balance = getItem(position);
        if (balance != null) {
            holder.bindTo(balance);
        } else {
            holder.clear();
        }
    }
}
