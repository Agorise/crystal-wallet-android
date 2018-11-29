package cy.agorise.crystalwallet.views;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import cy.agorise.crystalwallet.R;
import cy.agorise.crystalwallet.dao.CrystalDatabase;
import cy.agorise.crystalwallet.fragments.ReceiveTransactionFragment;
import cy.agorise.crystalwallet.fragments.SendTransactionFragment;
import cy.agorise.crystalwallet.models.CryptoCoinBalance;
import cy.agorise.crystalwallet.models.CryptoNetBalance;
import cy.agorise.crystalwallet.models.GeneralSetting;
import cy.agorise.crystalwallet.viewmodels.CryptoCoinBalanceListViewModel;

/**
 * Created by Henry Varona on 17/9/2017.
 *
 * Represents an element view from a Crypto Net Balance List
 */

public class CryptoNetBalanceViewHolder extends RecyclerView.ViewHolder {
    /*
     * the view holding the icon of the crypto net
     */
    private ImageView cryptoNetIcon;

    /*
     * the view holding the name of the crypto net
     */
    TextView cryptoNetName;

    /*
     * the view holding the equivalent total of the crypto net user account
     */
    TextView cryptoNetEquivalentTotal;

    /*
     * The list view of the crypto coins balances of this crypto net balance
     */
    CryptoCoinBalanceListView cryptoCoinBalanceListView;

    HashMap<CryptoCoinBalance, Double> equivalentTotalHashMap;

    /*
     * The button for sending transactions from this crypto net balance account
     */
    @BindView(R.id.btnSendFromThisAccount)
    Button btnSendFromThisAccount;

    /*
     * The button for receiving transactions to this crypto net balance account
     */
    @BindView(R.id.btnReceiveWithThisAccount)
    Button btnReceiveToThisAccount;

    Context context;

    /*
     * the id of the account of this crypto net balance. It will be loaded
     * when the element is bounded.
     */
    long cryptoNetAccountId;

    /*
     * A LifecycleOwner fragment. It will be used to call the ViewModelProviders
     */
    private Fragment fragment;

    String preferredCurrency = "";

    public CryptoNetBalanceViewHolder(View itemView, Fragment fragment) {
        super(itemView);
        //-1 represents a crypto net account not loaded yet
        this.cryptoNetAccountId = -1;

        //TODO: use ButterKnife to load the views
        cryptoNetIcon = itemView.findViewById(R.id.ivCryptoNetIcon);
        cryptoNetName = itemView.findViewById(R.id.tvCryptoNetName);
        cryptoNetEquivalentTotal = itemView.findViewById(R.id.tvCryptoNetEquivalentTotal);
        cryptoCoinBalanceListView = itemView.findViewById(R.id.cryptoCoinBalancesListView);
        btnSendFromThisAccount = itemView.findViewById(R.id.btnSendFromThisAccount);
        btnReceiveToThisAccount = itemView.findViewById(R.id.btnReceiveWithThisAccount);

        //Setting the send button
        btnSendFromThisAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendFromThisAccount();
            }
        });
        btnReceiveToThisAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                receiveToThisAccount();
            }
        });
        this.fragment = fragment;
        this.context = itemView.getContext();

        LiveData<GeneralSetting> preferedCurrencySetting = CrystalDatabase.getAppDatabase(this.context).generalSettingDao().getByName(GeneralSetting.SETTING_NAME_PREFERRED_CURRENCY);

        preferedCurrencySetting.observe((LifecycleOwner)this.itemView.getContext(), new Observer<GeneralSetting>() {
            @Override
            public void onChanged(@Nullable GeneralSetting generalSetting) {
                if(generalSetting != null){
                    preferredCurrency = generalSetting.getValue();
                }
            }
        });

    }

    public void clear(){
        cryptoNetName.setText("loading...");
    }

    /*
     * dispatch the user to the send activity using this account
     */
    public void sendFromThisAccount(){
        /*
        //if the crypto net account was loaded
        if (this.cryptoNetAccountId >= 0) {
            Intent startActivity = new Intent();
            startActivity.setClass(context, SendTransactionActivity.class);
            startActivity.setAction(SendTransactionActivity.class.getName());
            //Pass the account id as an extra parameter to the send activity
            startActivity.putExtra("CRYPTO_NET_ACCOUNT_ID", this.cryptoNetAccountId);
            startActivity.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            context.startActivity(startActivity);
        }*/

        FragmentTransaction ft = fragment.getFragmentManager().beginTransaction();
        Fragment prev = fragment.getFragmentManager().findFragmentByTag("SendDialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        SendTransactionFragment newFragment = SendTransactionFragment.newInstance(this.cryptoNetAccountId);
        newFragment.show(ft, "SendDialog");
    }

    /*
     * dispatch the user to the receive activity using this account
     */
    public void receiveToThisAccount(){
        FragmentTransaction ft = fragment.getFragmentManager().beginTransaction();
        Fragment prev = fragment.getFragmentManager().findFragmentByTag("ReceiveDialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        ReceiveTransactionFragment newFragment = ReceiveTransactionFragment.newInstance(this.cryptoNetAccountId);
        newFragment.show(ft, "ReceiveDialog");
    }

    public void setEquivalentBalance(CryptoCoinBalance cryptoCoinBalance, double equivalentValue){
        if (this.equivalentTotalHashMap == null){
            this.equivalentTotalHashMap = new HashMap<CryptoCoinBalance, Double>();
        }

        if (cryptoCoinBalance != null) {
            this.equivalentTotalHashMap.put(cryptoCoinBalance, equivalentValue);
            float totalEquivalent = 0;

            for (Double nextEquivalent : this.equivalentTotalHashMap.values()){
                totalEquivalent += nextEquivalent;
            }

            this.cryptoNetEquivalentTotal.setText(""+totalEquivalent+" "+preferredCurrency);
        }
    }

    /*
     * Binds this view with the data of an element of the list
     */
    public void bindTo(final CryptoNetBalance balance) {
        if (balance == null ){
            cryptoNetName.setText("loading...");
        } else {
            final CryptoNetBalanceViewHolder thisViewHolder = this;
            this.cryptoNetAccountId = balance.getAccountId();
            this.cryptoNetIcon.setImageResource(balance.getCryptoNet().getIconImageResource());
            /*
            * The first letter should be in Uppercase
            * */
            final String crypto = balance.getCryptoNet().getLabel().toLowerCase();
            final String upperString = crypto.substring(0,1).toUpperCase() + crypto.substring(1);

            cryptoNetName.setText(upperString);

            //Loads the crypto coin balance list of this account using a ViewModel and retrieving a LiveData List
            CryptoCoinBalanceListViewModel cryptoCoinBalanceListViewModel = ViewModelProviders.of(this.fragment).get(CryptoCoinBalanceListViewModel.class);
            cryptoCoinBalanceListViewModel.init(balance.getAccountId());
            LiveData<List<CryptoCoinBalance>> cryptoCoinBalanceData = cryptoCoinBalanceListViewModel.getCryptoCoinBalanceList();

            cryptoCoinBalanceListView.setData(null, this);

            //Observes the LiveData, so any of its changes on the database will be reloaded here
            cryptoCoinBalanceData.observe((LifecycleOwner)this.itemView.getContext(), new Observer<List<CryptoCoinBalance>>() {
                @Override
                public void onChanged(List<CryptoCoinBalance> cryptoCoinBalances) {
                    cryptoCoinBalanceListView.setData(cryptoCoinBalances, thisViewHolder);


                }
            });
        }
    }
}
