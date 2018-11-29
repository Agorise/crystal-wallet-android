package cy.agorise.crystalwallet.views;

import androidx.recyclerview.widget.ListAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cy.agorise.crystalwallet.R;
import cy.agorise.crystalwallet.models.AccountSeed;

/**
 * Created by Henry Varona on 11/9/2017.
 */

public class AccountSeedListAdapter extends ListAdapter<AccountSeed, AccountSeedViewHolder> {

    public AccountSeedListAdapter() {
        super(AccountSeed.DIFF_CALLBACK);
    }

    @Override
    public AccountSeedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.account_seed_list_item,parent,false);

        return new AccountSeedViewHolder(v);
    }

    @Override
    public void onBindViewHolder(AccountSeedViewHolder holder, int position) {
        AccountSeed accountSeed = getItem(position);
        if (accountSeed != null) {
            holder.bindTo(accountSeed);
        } else {
            holder.clear();
        }
    }
}
