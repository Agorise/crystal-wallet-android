package cy.agorise.crystalwallet.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cy.agorise.crystalwallet.R;
import cy.agorise.crystalwallet.dao.CrystalDatabase;
import cy.agorise.crystalwallet.enums.CryptoCoin;
import cy.agorise.crystalwallet.enums.CryptoNet;
import cy.agorise.crystalwallet.models.AccountSeed;
import cy.agorise.crystalwallet.models.CryptoNetSelection;
import cy.agorise.crystalwallet.requestmanagers.CreateBitcoinAccountRequest;
import cy.agorise.crystalwallet.requestmanagers.CryptoNetInfoRequestListener;
import cy.agorise.crystalwallet.requestmanagers.CryptoNetInfoRequests;
import cy.agorise.crystalwallet.views.CryptoNetSelectionListAdapter;


/**
 * Created by xd on 12/28/17.
 */

public class GeneralAccountSeedCoinSettingsFragment extends Fragment implements CryptoNetSelectionListAdapter.CryptoNetSelectionListener {

    @BindView(R.id.rvCoinSelection)
    RecyclerView rvCoinSelection;

    AccountSeed accountSeed;

    ArrayList<CryptoNetSelection> cryptoNetSelectionList;

    public GeneralAccountSeedCoinSettingsFragment() {

        if (getArguments() != null) {
            long accountSeedId = getArguments().getLong("SEED_ID", -1);

            if (accountSeedId > -1) {
                this.accountSeed = CrystalDatabase.getAppDatabase(getContext()).accountSeedDao().findById(accountSeedId);
            }
        }

        cryptoNetSelectionList = new ArrayList<CryptoNetSelection>();
        CryptoNetSelection nextCryptoNetSelection;
        for (CryptoNet nextCryptoNet : CryptoNet.values()){

            if ((nextCryptoNet != CryptoNet.UNKNOWN) && (nextCryptoNet != CryptoNet.BITCOIN_TEST)) {
                nextCryptoNetSelection = new CryptoNetSelection(nextCryptoNet, false);
                cryptoNetSelectionList.add(nextCryptoNetSelection);
            }
        }
        // Required empty public constructor
    }

    public static GeneralAccountSeedCoinSettingsFragment newInstance(long accountSeedId) {
        GeneralAccountSeedCoinSettingsFragment fragment = new GeneralAccountSeedCoinSettingsFragment();
        Bundle args = new Bundle();
        args.putLong("SEED_ID", accountSeedId);
        fragment.setArguments(args);


        if (accountSeedId > -1){
            fragment.accountSeed = CrystalDatabase.getAppDatabase(fragment.getContext()).accountSeedDao().findById(accountSeedId);
        }

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_general_account_seed_coin_settings, container, false);
        ButterKnife.bind(this, v);

        CryptoNetSelectionListAdapter cryptoNetSelectionListAdapter = new CryptoNetSelectionListAdapter(this.cryptoNetSelectionList);
        cryptoNetSelectionListAdapter.addListener(this);
        rvCoinSelection.setAdapter(cryptoNetSelectionListAdapter);
        LinearLayoutManager llm = new LinearLayoutManager(this.getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rvCoinSelection.setLayoutManager(llm);

        return v;
    }

    @Override
    public void onCryptoNetSelectionChecked(CryptoNetSelection source) {
        //Toast.makeText(this.getContext(),"the coin "+source.getCryptoNet().name()+" was "+(source.getSelected()?"selected":"unselected"),Toast.LENGTH_LONG).show();
        List<CryptoCoin> cryptoCoins = CryptoCoin.getByCryptoNet(source.getCryptoNet());


        final CreateBitcoinAccountRequest request = new CreateBitcoinAccountRequest(this.accountSeed,this.getContext(),cryptoCoins.get(0));

        request.setListener(new CryptoNetInfoRequestListener() {
            @Override
            public void onCarryOut() {
                if (request.getStatus() == CreateBitcoinAccountRequest.StatusCode.SUCCEEDED){
                    Toast.makeText(getContext(),"The account was successfully created",Toast.LENGTH_LONG);
                } else {
                    Toast.makeText(getContext(),"There was an error enabling the account",Toast.LENGTH_LONG);
                }
            }
        });

        CryptoNetInfoRequests.getInstance().addRequest(request);
    }
}
