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
import cy.agorise.crystalwallet.models.CryptoCoinBalance;

/**
 * Created by Henry Varona on 10/9/2017.
 *
 * A list view of crypto coin balances of a crypto net account
 */

public class CryptoCoinBalanceListView extends RelativeLayout {

    LayoutInflater mInflater;

    /*
     * The root view of this view
     */
    View rootView;
    /*
     * The list view that holds every crypto coin balance item
     */
    RecyclerView listView;
    /*
     * The adapter for the previous list view
     */
    CryptoCoinBalanceListAdapter listAdapter;


    /*
     * One of three constructors needed to be inflated from a layout
     */
    public CryptoCoinBalanceListView(Context context){
        super(context);
        this.mInflater = LayoutInflater.from(context);
        init();
    }

    /*
     * One of three constructors needed to be inflated from a layout
     */
    public CryptoCoinBalanceListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mInflater = LayoutInflater.from(context);
        init();
    }

    /*
     * One of three constructors needed to be inflated from a layout
     */
    public CryptoCoinBalanceListView(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
        this.mInflater = LayoutInflater.from(context);
        init();
    }

    /*
     * Initializes this view
     */
    public void init(){
        rootView = mInflater.inflate(R.layout.crypto_coin_balance_list, this, true);
        this.listView =  (RecyclerView) rootView.findViewById(R.id.cryptoCoinBalanceListView);

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext());
        this.listView.setLayoutManager(linearLayoutManager);
        //Prevents the UI from an infinite scrolling of balances
        this.listView.setNestedScrollingEnabled(false);
    }

    /*
     * Sets the data for the list of balances.
     *
     * @param data the list of crypto coin balances that will be show to the user
     */
    public void setData(List<CryptoCoinBalance> data, CryptoNetBalanceViewHolder cryptoNetBalanceViewHolder){
        //initializes the list adapter
        if (this.listAdapter == null) {
            this.listAdapter = new CryptoCoinBalanceListAdapter();
            this.listView.setAdapter(this.listAdapter);
        }

        //sets the data of the list adapter
        if (data != null) {
            this.listAdapter.setCryptoNetBalanceViewHolder(cryptoNetBalanceViewHolder);
            this.listAdapter.submitList(data);
        }
    }


}
