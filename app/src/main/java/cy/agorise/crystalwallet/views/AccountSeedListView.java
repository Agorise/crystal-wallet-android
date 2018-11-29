package cy.agorise.crystalwallet.views;

import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.List;

import cy.agorise.crystalwallet.R;
import cy.agorise.crystalwallet.models.AccountSeed;
import cy.agorise.crystalwallet.viewmodels.AccountSeedListViewModel;

/**
 * Created by Henry Varona on 10/9/2017.
 */

public class AccountSeedListView extends RelativeLayout {

    LayoutInflater mInflater;

    View rootView;
    RecyclerView listView;
    AccountSeedListAdapter listAdapter;

    AccountSeedListViewModel accountSeedListViewModel;

    public AccountSeedListView(Context context){
        super(context);
        this.mInflater = LayoutInflater.from(context);
        init();
    }

    public AccountSeedListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mInflater = LayoutInflater.from(context);
        init();
    }

    public AccountSeedListView(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
        this.mInflater = LayoutInflater.from(context);
        init();
    }

    public void init(){
        rootView = mInflater.inflate(R.layout.account_seed_list, this, true);
        this.listView = rootView.findViewById(R.id.accountSeedListView);

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext());
        this.listView.setLayoutManager(linearLayoutManager);
        this.listView.setNestedScrollingEnabled(false);
    }

    public void setData(List<AccountSeed> data){
        if (this.listAdapter == null) {
            this.listAdapter = new AccountSeedListAdapter();
            this.listView.setAdapter(this.listAdapter);
        }

        if (data != null) {
            this.listAdapter.submitList(data);
        }
    }


}
