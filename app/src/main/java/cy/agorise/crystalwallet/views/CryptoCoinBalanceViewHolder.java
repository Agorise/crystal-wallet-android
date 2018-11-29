package cy.agorise.crystalwallet.views;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import android.content.Context;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import cy.agorise.crystalwallet.R;
import cy.agorise.crystalwallet.dao.CrystalDatabase;
import cy.agorise.crystalwallet.models.CryptoCoinBalance;
import cy.agorise.crystalwallet.models.CryptoCurrency;
import cy.agorise.crystalwallet.models.CryptoCurrencyEquivalence;
import cy.agorise.crystalwallet.models.GeneralSetting;

/**
 * Created by Henry Varona on 17/9/2017.
 *
 * Represents an element view from a Crypto Coin Balance List
 */

public class CryptoCoinBalanceViewHolder extends RecyclerView.ViewHolder {
    /*
     * the view holding the crypto coin name
     */
    private TextView cryptoCoinName;
    /*
     * the view holding the crypto coin balance amount
     */
    private TextView cryptoCoinBalance;
    /*
     * the view holding the crypto coin balance equivalent value
     */
    private TextView cryptoCoinBalanceEquivalence;

    private Context context;

    private CryptoNetBalanceViewHolder cryptoNetBalanceViewHolder;

    public CryptoCoinBalanceViewHolder(View itemView, CryptoNetBalanceViewHolder cryptoNetBalanceViewHolder) {
        super(itemView);
        //TODO: use ButterKnife to load this
        cryptoCoinName = (TextView) itemView.findViewById(R.id.tvCryptoCoinName);
        cryptoCoinBalance = (TextView) itemView.findViewById(R.id.tvCryptoCoinBalanceAmount);
        cryptoCoinBalanceEquivalence = (TextView) itemView.findViewById(R.id.tvCryptoCoinBalanceEquivalence);
        this.cryptoNetBalanceViewHolder = cryptoNetBalanceViewHolder;
        this.context = itemView.getContext();

    }

    /*
     * Clears the information in this element view
     */
    public void clear(){
        cryptoCoinName.setText("loading...");
        cryptoCoinBalance.setText("");
        cryptoCoinBalanceEquivalence.setText("");
    }

    /*
     * Binds this view with the data of an element of the list
     */
    public void bindTo(final CryptoCoinBalance balance/*, final OnTransactionClickListener listener*/) {
        if (balance == null){
            this.clear();
        } else {
            //Retrieves the preferred currency selected by the user
            final LiveData<GeneralSetting> preferedCurrencySetting = CrystalDatabase.getAppDatabase(this.context).generalSettingDao().getByName(GeneralSetting.SETTING_NAME_PREFERRED_CURRENCY);
            //Retrieves the currency of this balance
            //final CryptoCurrency currencyFrom = CrystalDatabase.getAppDatabase(context).cryptoCurrencyDao().getByIdLiveData(balance.getCryptoCurrencyId());
            LiveData<CryptoCurrency> currencyFromLD = CrystalDatabase.getAppDatabase(context).cryptoCurrencyDao().getLDById(balance.getCryptoCurrencyId());


            currencyFromLD.observe((LifecycleOwner) this.context, new Observer<CryptoCurrency>() {
                @Override
                public void onChanged(@Nullable final CryptoCurrency currencyFrom) {
                    //Sets the name and amount of the balance in the view
                    String balanceString = String.format("%.2f",balance.getBalance()/Math.pow(10,currencyFrom.getPrecision()));
                    cryptoCoinName.setText(currencyFrom.getName());
                    cryptoCoinBalance.setText(balanceString);

                    //Observes the preferred currency of the user. If the user changes it, this will change the equivalent value
                    preferedCurrencySetting.observe((LifecycleOwner) context, new Observer<GeneralSetting>() {
                        @Override
                        public void onChanged(@Nullable GeneralSetting generalSetting) {
                            if (generalSetting != null) {
                                //Gets the currency object of the preferred currency
                                LiveData<CryptoCurrency> currencyToLiveData = CrystalDatabase.getAppDatabase(context).cryptoCurrencyDao().getLiveDataByName(generalSetting.getValue());

                                currencyToLiveData.observe((LifecycleOwner) context, new Observer<CryptoCurrency>() {
                                    @Override
                                    public void onChanged(@Nullable CryptoCurrency cryptoCurrency) {
                                        if (cryptoCurrency != null) {
                                            CryptoCurrency currencyTo = cryptoCurrency;

                                            //Retrieves the equivalent value of this balance using the "From" currency and the "To" currency
                                            LiveData<CryptoCurrencyEquivalence> currencyEquivalenceLiveData = CrystalDatabase.getAppDatabase(context)
                                                    .cryptoCurrencyEquivalenceDao().getByFromTo(
                                                            currencyTo.getId(),
                                                            currencyFrom.getId()

                                                    );

                                            //Observes the equivalent value. If the equivalent value changes, this will keep the value showed correct
                                            currencyEquivalenceLiveData.observe((LifecycleOwner) context, new Observer<CryptoCurrencyEquivalence>() {
                                                @Override
                                                public void onChanged(final @Nullable CryptoCurrencyEquivalence cryptoCurrencyEquivalence) {
                                                    if (cryptoCurrencyEquivalence != null) {
                                                        LiveData<CryptoCurrency> toCurrencyLD = CrystalDatabase.getAppDatabase(context).cryptoCurrencyDao().getLDById(cryptoCurrencyEquivalence.getFromCurrencyId());

                                                        toCurrencyLD.observe((LifecycleOwner) context, new Observer<CryptoCurrency>() {
                                                            @Override
                                                            public void onChanged(@Nullable CryptoCurrency toCurrency) {

                                                                double equivalentValue = 0;
                                                                String equivalenceString = "";
                                                                if (cryptoCurrencyEquivalence.getValue() > 0) {
                                                                    equivalentValue = (balance.getBalance() / Math.pow(10, currencyFrom.getPrecision())) /
                                                                            (cryptoCurrencyEquivalence.getValue() / Math.pow(10, toCurrency.getPrecision()));
                                                                    equivalenceString = String.format(
                                                                            "%.2f",
                                                                            equivalentValue
                                                                    );
                                                                } else {
                                                                    equivalentValue = 0;
                                                                    equivalenceString = "0";
                                                                }

                                                                cryptoNetBalanceViewHolder.setEquivalentBalance(balance,equivalentValue);
                                                                cryptoCoinBalanceEquivalence.setText(
                                                                        equivalenceString + " " + toCurrency.getName());
                                                            }
                                                        });
                                                    }
                                                }
                                            });
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            });
        }
    }
}
