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
import cy.agorise.crystalwallet.models.CryptoNetAccount;
import cy.agorise.crystalwallet.viewmodels.CryptoNetAccountListViewModel;

/**
 * Created by Henry Varona on 05/27/2018.
 */

public class CryptoNetAccountListView extends RelativeLayout {

    LayoutInflater mInflater;

    View rootView;
    RecyclerView listView;
    CryptoNetAccountListAdapter listAdapter;

    CryptoNetAccountListViewModel cryptoNetAccountListViewModel;

    public CryptoNetAccountListView(Context context){
        super(context);
        this.mInflater = LayoutInflater.from(context);
        init();
    }

    public CryptoNetAccountListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mInflater = LayoutInflater.from(context);
        init();
    }

    public CryptoNetAccountListView(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
        this.mInflater = LayoutInflater.from(context);
        init();
    }

    public void init(){
        rootView = mInflater.inflate(R.layout.crypto_net_account_list, this, true);
        this.listView = rootView.findViewById(R.id.cryptoNetAccountListView);

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext());
        this.listView.setLayoutManager(linearLayoutManager);
        this.listView.setNestedScrollingEnabled(false);
    }

    public void setData(List<CryptoNetAccount> data){
        if (this.listAdapter == null) {
            this.listAdapter = new CryptoNetAccountListAdapter();
            this.listView.setAdapter(this.listAdapter);
        }

        if (data != null) {
            this.listAdapter.submitList(data);
        }
    }


}
