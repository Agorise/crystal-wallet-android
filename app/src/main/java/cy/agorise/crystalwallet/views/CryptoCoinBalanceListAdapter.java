package cy.agorise.crystalwallet.views;


import androidx.recyclerview.widget.ListAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cy.agorise.crystalwallet.R;
import cy.agorise.crystalwallet.models.CryptoCoinBalance;

/**
 * Created by Henry Varona on 11/9/2017.
 *
 * An adapter to show the elements of a list of crypto coin balances
 */

public class CryptoCoinBalanceListAdapter extends ListAdapter<CryptoCoinBalance, CryptoCoinBalanceViewHolder> {

    CryptoNetBalanceViewHolder cryptoNetBalanceViewHolder;

    public CryptoCoinBalanceListAdapter() {
        super(CryptoCoinBalance.DIFF_CALLBACK);
    }

    public void setCryptoNetBalanceViewHolder(CryptoNetBalanceViewHolder cryptoNetBalanceViewHolder){
        this.cryptoNetBalanceViewHolder = cryptoNetBalanceViewHolder;
    }

    @Override
    public CryptoCoinBalanceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.crypto_coin_balance_list_item,parent,false);


        return new CryptoCoinBalanceViewHolder(v, cryptoNetBalanceViewHolder);
    }

    @Override
    public void onBindViewHolder(CryptoCoinBalanceViewHolder holder, int position) {
        CryptoCoinBalance balance = getItem(position);
        if (balance != null) {
            holder.bindTo(balance);
        } else {
            holder.clear();
        }
    }
}
