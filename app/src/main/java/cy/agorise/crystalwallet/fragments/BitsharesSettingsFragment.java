package cy.agorise.crystalwallet.fragments;

import androidx.lifecycle.LiveData;

import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.thekhaeng.pushdownanim.PushDownAnim;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cy.agorise.crystalwallet.R;
import cy.agorise.crystalwallet.dao.CrystalDatabase;
import cy.agorise.crystalwallet.models.CryptoNetAccount;
import cy.agorise.crystalwallet.models.GeneralSetting;
import cy.agorise.crystalwallet.models.GrapheneAccount;
import cy.agorise.crystalwallet.models.GrapheneAccountInfo;
import cy.agorise.crystalwallet.requestmanagers.CryptoNetInfoRequestListener;
import cy.agorise.crystalwallet.requestmanagers.CryptoNetInfoRequests;
import cy.agorise.crystalwallet.requestmanagers.ValidateBitsharesLTMUpgradeRequest;
import cy.agorise.crystalwallet.viewmodels.GeneralSettingListViewModel;


/**
 * Created by xd on 12/28/17.
 */

public class BitsharesSettingsFragment extends Fragment {

    private GeneralSettingListViewModel generalSettingListViewModel;
    private LiveData<List<GeneralSetting>> generalSettingListLiveData;

    @BindView (R.id.tvUpgradeToLtm)
    TextView tvUpgradeToLtm;
    @BindView (R.id.btnUpgradeToLtm)
    Button btnUpgradeToLtm;
    @BindView (R.id.tvAlreadyLtm)
    TextView tvAlreadyLtm;

    CryptoNetAccount cryptoNetAccount;
    GrapheneAccountInfo grapheneAccountInfo;
    GrapheneAccount grapheneAccount;




    public BitsharesSettingsFragment() {
        if (getArguments() != null) {
            long cryptoNetAcountId = getArguments().getLong("CRYPTO_NET_ACCOUNT_ID", -1);

            if (cryptoNetAcountId > -1) {
                this.cryptoNetAccount = CrystalDatabase.getAppDatabase(getContext()).cryptoNetAccountDao().getById(cryptoNetAcountId);
                this.grapheneAccountInfo = CrystalDatabase.getAppDatabase(getContext()).grapheneAccountInfoDao().getByAccountId(this.cryptoNetAccount.getId());
                this.grapheneAccount = new GrapheneAccount(this.cryptoNetAccount);
                this.grapheneAccount.loadInfo(this.grapheneAccountInfo);
            }
        }
        // Required empty public constructor
    }

    public static BitsharesSettingsFragment newInstance(long cryptoNetAccountId) {
        BitsharesSettingsFragment fragment = new BitsharesSettingsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);

        if (cryptoNetAccountId > -1){
            fragment.cryptoNetAccount = CrystalDatabase.getAppDatabase(fragment.getContext()).cryptoNetAccountDao().getById(cryptoNetAccountId);
            fragment.grapheneAccountInfo = CrystalDatabase.getAppDatabase(fragment.getContext()).grapheneAccountInfoDao().getByAccountId(fragment.cryptoNetAccount.getId());
            fragment.grapheneAccount = new GrapheneAccount(fragment.cryptoNetAccount);
            fragment.grapheneAccount.loadInfo(fragment.grapheneAccountInfo);
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
        View v = inflater.inflate(R.layout.fragment_bitshares_settings, container, false);
        ButterKnife.bind(this, v);

        /*
         *   Integration of library with button efects
         * */
        PushDownAnim.setPushDownAnimTo(btnUpgradeToLtm)
                .setOnClickListener( new View.OnClickListener(){
                    @Override
                    public void onClick( View view ){
                        upgradeAccountToLtm();
                    }
                } );

        initAlreadyLtm();

        return v;
    }

    @OnClick (R.id.btnUpgradeToLtm)
    public void upgradeAccountToLtm(){
        final ValidateBitsharesLTMUpgradeRequest request = new ValidateBitsharesLTMUpgradeRequest(this.getContext(),this.grapheneAccount);

        request.setListener(new CryptoNetInfoRequestListener() {
            @Override
            public void onCarryOut() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast toast;

                        switch (request.getStatus()){
                            case SUCCEEDED:
                                tvUpgradeToLtm.setVisibility(View.GONE);
                                btnUpgradeToLtm.setVisibility(View.GONE);
                                tvAlreadyLtm.setVisibility(View.VISIBLE);
                                break;
                            case NO_INTERNET:
                            case NO_SERVER_CONNECTION:
                                toast = Toast.makeText(getContext(), "There was an error connecting to the server. Please try again.", Toast.LENGTH_SHORT);
                                toast.show();
                                break;
                            case NO_FUNDS:
                                toast = Toast.makeText(getContext(), "Not enough funds to make the upgrade.", Toast.LENGTH_SHORT);
                                toast.show();
                                break;
                            case NO_ASSET_INFO_DB:
                            case NO_ASSET_INFO:
                            case PETITION_FAILED:
                            default:
                                toast = Toast.makeText(getContext(), "There was an error with the request. Please try again.", Toast.LENGTH_SHORT);
                                toast.show();
                        }
                    }
                });
            }
        });

        CryptoNetInfoRequests.getInstance().addRequest(request);
    }

    public void initAlreadyLtm(){
        if (this.grapheneAccount.getUpgradedToLtm()){
            tvUpgradeToLtm.setVisibility(View.GONE);
            btnUpgradeToLtm.setVisibility(View.GONE);
            tvAlreadyLtm.setVisibility(View.VISIBLE);
        } else {
            tvUpgradeToLtm.setVisibility(View.VISIBLE);
            btnUpgradeToLtm.setVisibility(View.VISIBLE);
            tvAlreadyLtm.setVisibility(View.GONE);
        }
    }
}
