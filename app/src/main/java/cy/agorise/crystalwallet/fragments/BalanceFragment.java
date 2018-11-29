package cy.agorise.crystalwallet.fragments;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cy.agorise.crystalwallet.R;
import cy.agorise.crystalwallet.models.CryptoNetBalance;
import cy.agorise.crystalwallet.viewmodels.CryptoNetBalanceListViewModel;
import cy.agorise.crystalwallet.views.CryptoNetBalanceListAdapter;

public class BalanceFragment extends Fragment {

    CryptoNetBalanceListViewModel cryptoNetBalanceListViewModel;

    @BindView(R.id.tvNoBalances)
    TextView tvNoBalances;

    @BindView(R.id.rvBalances)
    RecyclerView rvBalances;

    CryptoNetBalanceListAdapter balancesAdapter;

    public BalanceFragment() {
        // Required empty public constructor
    }

    public static BalanceFragment newInstance() {
        BalanceFragment fragment = new BalanceFragment();
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
        View view = inflater.inflate(R.layout.fragment_balance, container, false);
        ButterKnife.bind(this, view);

        // Configure RecyclerView and its adapter
        rvBalances.setLayoutManager(new LinearLayoutManager(getContext()));
        balancesAdapter = new CryptoNetBalanceListAdapter(this);
        rvBalances.setAdapter(balancesAdapter);

        //Prevents the UI from an infinite scrolling of balances
        rvBalances.setNestedScrollingEnabled(false);

        cryptoNetBalanceListViewModel = ViewModelProviders.of(this).get(CryptoNetBalanceListViewModel.class);
        final LiveData<List<CryptoNetBalance>> cryptoNetBalanceData = cryptoNetBalanceListViewModel.getCryptoNetBalanceList();

        cryptoNetBalanceData.observe(this, new Observer<List<CryptoNetBalance>>() {
            @Override
            public void onChanged(List<CryptoNetBalance> cryptoNetBalances) {
                balancesAdapter.submitList(cryptoNetBalances);

                if(cryptoNetBalances != null && cryptoNetBalances.size() > 0) {
                    tvNoBalances.setVisibility(View.INVISIBLE);
                } else {
                    tvNoBalances.setVisibility(View.VISIBLE);
                }
            }
        });

        return view;
    }
}
