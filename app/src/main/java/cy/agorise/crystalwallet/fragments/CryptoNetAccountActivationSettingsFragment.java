package cy.agorise.crystalwallet.fragments;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.thekhaeng.pushdownanim.PushDownAnim;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cy.agorise.crystalwallet.R;
import cy.agorise.crystalwallet.dao.CrystalDatabase;
import cy.agorise.crystalwallet.models.AccountSeed;
import cy.agorise.crystalwallet.models.CryptoNetAccount;


/**
 * Created by xd on 12/28/17.
 */

public class CryptoNetAccountActivationSettingsFragment extends Fragment {

    @BindView(R.id.tvMnemonic)
    TextView tvMnemonic;

    @BindView(R.id.btnCopy)
    Button btnCopy;

    CryptoNetAccount cryptoNetAccount;
    AccountSeed accountSeed;




    public CryptoNetAccountActivationSettingsFragment() {

        if (getArguments() != null) {
            long cryptoNetAcountId = getArguments().getLong("CRYPTO_NET_ACCOUNT_ID", -1);

            if (cryptoNetAcountId > -1) {
                this.cryptoNetAccount = CrystalDatabase.getAppDatabase(getContext()).cryptoNetAccountDao().getById(cryptoNetAcountId);
                this.accountSeed = CrystalDatabase.getAppDatabase(getContext()).accountSeedDao().findById(this.cryptoNetAccount.getSeedId());
            }
        }
        // Required empty public constructor
    }

    public static CryptoNetAccountActivationSettingsFragment newInstance(long cryptoNetAccountId) {
        CryptoNetAccountActivationSettingsFragment fragment = new CryptoNetAccountActivationSettingsFragment();
        Bundle args = new Bundle();
        args.putLong("CRYPTO_NET_ACCOUNT_ID", cryptoNetAccountId);
        fragment.setArguments(args);


        if (cryptoNetAccountId > -1){
            fragment.cryptoNetAccount = CrystalDatabase.getAppDatabase(fragment.getContext()).cryptoNetAccountDao().getById(cryptoNetAccountId);
            fragment.accountSeed = CrystalDatabase.getAppDatabase(fragment.getContext()).accountSeedDao().findById(fragment.cryptoNetAccount.getSeedId());
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
        View v = inflater.inflate(R.layout.fragment_general_crypto_net_account_settings, container, false);
        ButterKnife.bind(this, v);

        /*
         *   Integration of library with button efects
         * */
        PushDownAnim.setPushDownAnimTo(btnCopy)
                .setOnClickListener( new View.OnClickListener(){
                    @Override
                    public void onClick( View view ){
                        btnCopyClick();
                    }

                } );

        initAlreadyLtm();

        return v;
    }

    public void initAlreadyLtm(){
        if (this.cryptoNetAccount != null) {
            tvMnemonic.setText(this.accountSeed.getMasterSeed());
        }
    }

    /*
     *   Clic on button copy to clipboard
     * */
    @OnClick(R.id.btnCopy)
    public void btnCopyClick(){

        /*
         *  Save to clipboard the brainkey chain
         * */
        final Activity activity = getActivity();
        ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(tvMnemonic.getText(), tvMnemonic.getText().toString());
        clipboard.setPrimaryClip(clip);

        /*
         * Success message
         * */
        Toast.makeText(activity,getResources().getString(R.string.window_seed_toast_clipboard), Toast.LENGTH_SHORT).show();
    }
}
