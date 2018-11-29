package cy.agorise.crystalwallet.views;


import androidx.recyclerview.widget.ListAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cy.agorise.crystalwallet.R;
import cy.agorise.crystalwallet.models.CryptoNetAccount;

/**
 * Created by Henry Varona on 05/27/2018.
 */

public class CryptoNetAccountListAdapter extends ListAdapter<CryptoNetAccount, CryptoNetAccountViewHolder> {

    public CryptoNetAccountListAdapter() {
        super(CryptoNetAccount.DIFF_CALLBACK);
    }

    @Override
    public CryptoNetAccountViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.crypto_net_account_list_item,parent,false);

        return new CryptoNetAccountViewHolder(v);
    }

    @Override
    public void onBindViewHolder(CryptoNetAccountViewHolder holder, int position) {
        CryptoNetAccount cryptoNetAccount = getItem(position);
        if (cryptoNetAccount != null) {
            holder.bindTo(cryptoNetAccount);
        } else {
            holder.clear();
        }
    }
}
