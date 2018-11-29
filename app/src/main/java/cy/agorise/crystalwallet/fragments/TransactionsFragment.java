package cy.agorise.crystalwallet.fragments;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.PagedList;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;
import cy.agorise.crystalwallet.R;
import cy.agorise.crystalwallet.models.CryptoCoinTransactionExtended;
import cy.agorise.crystalwallet.viewmodels.TransactionListViewModel;
import cy.agorise.crystalwallet.views.TransactionListAdapter;
import cy.agorise.crystalwallet.views.TransactionOrderSpinnerAdapter;

public class TransactionsFragment extends Fragment {

    @BindView(R.id.spTransactionsOrder)
    Spinner spTransactionsOrder;

    @BindView(R.id.etTransactionSearch)
    EditText etTransactionSearch;

    @BindView(R.id.tvNoTransactions)
    TextView tvNoTransactions;

    @BindView(R.id.rvTransactions)
    RecyclerView rvTransactions;

    FloatingActionButton fabSend;
    FloatingActionButton fabReceive;


    TransactionListAdapter transactionListAdapter;
    TransactionListViewModel transactionListViewModel;
    LiveData<PagedList<CryptoCoinTransactionExtended>> transactionsLiveData;

    public TransactionsFragment() {
        // Required empty public constructor
    }

    public static TransactionsFragment newInstance() {
        TransactionsFragment fragment = new TransactionsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_transactions, container, false);
        ButterKnife.bind(this, view);

        rvTransactions.setLayoutManager(new LinearLayoutManager(getContext()));
        transactionListAdapter = new TransactionListAdapter(this);
        rvTransactions.setAdapter(transactionListAdapter);

        fabSend = getActivity().findViewById(R.id.fabSend);
        fabReceive = getActivity().findViewById(R.id.fabReceive);

        // TODO move this listener to the activity, to make this fragment reusable
        // Adds listener to the RecyclerView to show and hide buttons at the bottom of the screen
        rvTransactions.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx,int dy){
                super.onScrolled(recyclerView, dx, dy);

                // Scroll Down
                if( dy >0 ) {
                    if( fabSend.isShown() )
                        fabSend.hide();
                    if( fabReceive.isShown() )
                        fabReceive.hide();
                }
                // Scroll Up
                else if( dy <0 ) {
                    if( !fabSend.isShown() )
                        fabSend.show();
                    if( !fabReceive.isShown() )
                        fabReceive.show();
                }
            }
        });

        transactionListViewModel = ViewModelProviders.of(this).get(TransactionListViewModel.class);


        initTransactionsOrderSpinner();
        changeTransactionList();
        return view;
    }

    public void changeTransactionList(){
        TransactionOrderSpinnerAdapter.TransactionOrderSpinnerItem orderSelected =
                (TransactionOrderSpinnerAdapter.TransactionOrderSpinnerItem)(spTransactionsOrder.getSelectedItem());

        //if (transactionsLiveData != null){
            //transactionsLiveData.removeObservers(this);
        //}
        transactionListViewModel.initTransactionList(orderSelected.getField(),etTransactionSearch.getText().toString());
        transactionsLiveData = transactionListViewModel.getTransactionList();

        transactionsLiveData.observe(this, new Observer<PagedList<CryptoCoinTransactionExtended>>() {
            @Override
            public void onChanged(@Nullable PagedList<CryptoCoinTransactionExtended> transactions) {
                transactionListAdapter.submitList(transactions);

                if(transactions != null && transactions.size() > 0){
                    tvNoTransactions.setVisibility(View.INVISIBLE);
                }
                else{
                    tvNoTransactions.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @OnTextChanged(value = R.id.etTransactionSearch,
            callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void afterTransactionSearchChange(Editable editable) {
        changeTransactionList();
    }

    public void initTransactionsOrderSpinner(){
        List<TransactionOrderSpinnerAdapter.TransactionOrderSpinnerItem> spinnerValues = new ArrayList<>();
        spinnerValues.add(new TransactionOrderSpinnerAdapter.TransactionOrderSpinnerItem("date","Date",0,false));
        spinnerValues.add(new TransactionOrderSpinnerAdapter.TransactionOrderSpinnerItem("amount","Amount",0,false));
        spinnerValues.add(new TransactionOrderSpinnerAdapter.TransactionOrderSpinnerItem("is_input","In/Out",0,false));
        spinnerValues.add(new TransactionOrderSpinnerAdapter.TransactionOrderSpinnerItem("from","From",0,false));
        spinnerValues.add(new TransactionOrderSpinnerAdapter.TransactionOrderSpinnerItem("to","To",0,false));

        TransactionOrderSpinnerAdapter transactionOrderSpinnerAdapter =
                new TransactionOrderSpinnerAdapter(
                        getContext(), android.R.layout.simple_spinner_item,spinnerValues
                );
        spTransactionsOrder.setAdapter(transactionOrderSpinnerAdapter);

        spTransactionsOrder.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                changeTransactionList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
}
