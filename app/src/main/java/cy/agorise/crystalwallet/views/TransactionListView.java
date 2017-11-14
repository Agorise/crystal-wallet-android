package cy.agorise.crystalwallet.views;

import android.arch.paging.PagedList;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import cy.agorise.crystalwallet.R;
import cy.agorise.crystalwallet.models.CryptoCoinTransaction;
import cy.agorise.crystalwallet.viewmodels.TransactionListViewModel;

/**
 * Created by Henry Varona on 10/9/2017.
 *
 * A list view showing many crypto net account transactions elements
 */

public class TransactionListView extends RelativeLayout {

    LayoutInflater mInflater;

    /*
     * The root view of this view
     */
    View rootView;
    /*
     * The list view that holds every transaction item
     */
    RecyclerView listView;
    /*
     * The adapter for the previous list view
     */
    TransactionListAdapter listAdapter;

    TransactionListViewModel transactionListViewModel;

    /*
     * how much transactions will remain to show before the list loads more
     */
    private int visibleThreshold = 5;
    /*
     * if true, the transaction list will be loading new data
     */
    private boolean loading = true;

    /*
     * One of three constructors needed to be inflated from a layout
     */
    public TransactionListView(Context context){
        super(context);
        this.mInflater = LayoutInflater.from(context);
        init();
    }

    /*
     * One of three constructors needed to be inflated from a layout
     */
    public TransactionListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mInflater = LayoutInflater.from(context);
        init();
    }

    /*
     * One of three constructors needed to be inflated from a layout
     */
    public TransactionListView(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
        this.mInflater = LayoutInflater.from(context);
        init();
    }

    /*
     * Initializes this view
     */
    public void init(){
        rootView = mInflater.inflate(R.layout.transaction_list, this, true);
        this.listView = rootView.findViewById(R.id.transactionListView);

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext());
        this.listView.setLayoutManager(linearLayoutManager);
        //Prevents the list to start again when scrolling to the end
        this.listView.setNestedScrollingEnabled(false);


        /*this.listView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(!loading && linearLayoutManager.getItemCount() <= (linearLayoutManager.findLastVisibleItemPosition() + visibleThreshold)){
                    onLoadMore();
                    loading = true;
                }
            }
        });*/
    }

    //public void onLoadMore(){
    //    listAdapter.add();

    //}

    /*
     * Sets the elements data of this view
     *
     * @param data the transactions that will be showed to the user
     */
    public void setData(PagedList<CryptoCoinTransaction> data){
        //Initializes the adapter of the transaction list
        if (this.listAdapter == null) {
            this.listAdapter = new TransactionListAdapter();
            this.listView.setAdapter(this.listAdapter);
        }

        //Sets the data of the transaction list
        if (data != null) {
            this.listAdapter.setList(data);
        }
    }


}
