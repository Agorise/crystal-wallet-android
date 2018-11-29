package cy.agorise.crystalwallet.views;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import cy.agorise.crystalwallet.R;
import cy.agorise.crystalwallet.activities.CryptoCoinTransactionReceiptActivity;
import cy.agorise.crystalwallet.models.CryptoCoinTransactionExtended;
import cy.agorise.crystalwallet.models.CryptoCurrency;
import cy.agorise.crystalwallet.models.CryptoNetAccount;
import cy.agorise.crystalwallet.models.GeneralSetting;
import cy.agorise.crystalwallet.viewmodels.CryptoCurrencyViewModel;
import cy.agorise.crystalwallet.viewmodels.CryptoNetAccountViewModel;
import cy.agorise.crystalwallet.viewmodels.GeneralSettingListViewModel;

/**
 * Created by Henry Varona on 17/9/2017.
 *
 * Represents a transaction view in the crypto net account transaction list
 */

public class TransactionViewHolder extends RecyclerView.ViewHolder {

    private View     vPaymentDirection;
    private TextView tvFrom;
    private ImageView ivDirectionArrow;
    private TextView tvTo;
    private TextView tvCryptoAmount;
    private TextView tvFiatEquivalent;
    private TextView tvDate;
    private TextView tvTime;

    private Fragment fragment;

    private long cryptoCoinTransactionId;

    TransactionViewHolder(View itemView, Fragment fragment) {
        super(itemView);
        //TODO: use ButterKnife to load this
        this.cryptoCoinTransactionId = -1;

        View rootView = itemView.findViewById(R.id.rootView);
        vPaymentDirection = itemView.findViewById(R.id.vPaymentDirection);
        tvFrom = itemView.findViewById(R.id.tvFrom);
        ivDirectionArrow = itemView.findViewById(R.id.ivDirectionArrow);
        tvTo = itemView.findViewById(R.id.tvTo);
        tvDate = itemView.findViewById(R.id.tvDate);
        tvTime = itemView.findViewById(R.id.tvTime);
        tvCryptoAmount = itemView.findViewById(R.id.tvCryptoAmount);
        tvFiatEquivalent = itemView.findViewById(R.id.tvFiatEquivalent);
        this.fragment = fragment;

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eReceiptOfThisTransaction();
            }
        });
    }

    /*
     * dispatch the user to the receipt activity using this transaction
     */
    private void eReceiptOfThisTransaction(){
        //if the transaction was loaded
        if (this.cryptoCoinTransactionId >= 0) {
            Context context = fragment.getContext();
            Intent startActivity = new Intent();
            startActivity.setClass(context, CryptoCoinTransactionReceiptActivity.class);
            startActivity.setAction(CryptoCoinTransactionReceiptActivity.class.getName());
            //Pass the transaction id as an extra parameter to the receipt activity
            startActivity.putExtra("CRYPTO_COIN_TRANSACTION_ID", this.cryptoCoinTransactionId);
            startActivity.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            context.startActivity(startActivity);
        }
    }

    /*
     * Clears all info in this element view
     */
    public void clear(){
        tvFrom.setText("loading...");
        tvTo.setText("");
        tvCryptoAmount.setText("");
        tvFiatEquivalent.setText("");
        tvDate.setText("");
        tvTime.setText("");
    }

    /*
     * Binds a transaction object with this element view
     */
    public void bindTo(final CryptoCoinTransactionExtended transaction/*, final OnTransactionClickListener listener*/) {
        if (transaction == null){
            clear();
        } else {
            this.cryptoCoinTransactionId = transaction.getId();
            CryptoCurrencyViewModel cryptoCurrencyViewModel = ViewModelProviders.of(this.fragment).get(CryptoCurrencyViewModel.class);
            CryptoCurrency cryptoCurrency = cryptoCurrencyViewModel.getCryptoCurrencyById(transaction.getIdCurrency());
            CryptoNetAccountViewModel cryptoNetAccountViewModel = ViewModelProviders.of(this.fragment).get(CryptoNetAccountViewModel.class);
            cryptoNetAccountViewModel.loadCryptoNetAccount(transaction.getAccountId());

            String pattern = new String(new char[cryptoCurrency.getPrecision()]).replace('\0', '#');
            pattern = "#."+pattern;

            DecimalFormat df = new DecimalFormat(pattern);
            //String amountString = String.format("%.2f",transaction.getAmount()/Math.pow(10,cryptoCurrency.getPrecision()));
            String amountString = df.format(transaction.getAmount()/Math.pow(10,cryptoCurrency.getPrecision()));

            GeneralSettingListViewModel generalSettingListViewModel = ViewModelProviders.of(this.fragment).get(GeneralSettingListViewModel.class);
            GeneralSetting timeZoneSetting = generalSettingListViewModel.getGeneralSettingByName(GeneralSetting.SETTING_NAME_TIME_ZONE);

            TimeZone userTimeZone;
            if (timeZoneSetting != null){
                userTimeZone = TimeZone.getTimeZone(timeZoneSetting.getValue());
            } else {
                userTimeZone = TimeZone.getTimeZone("cet");
            }

            DateFormat dateFormat = new SimpleDateFormat("dd MMM");
            dateFormat.setTimeZone(userTimeZone);
            DateFormat hourFormat = new SimpleDateFormat("HH:mm:ss");
            hourFormat.setTimeZone(userTimeZone);

            if(transaction.getInput()) {
                vPaymentDirection.setBackgroundColor(fragment.getContext().getResources().getColor(R.color.receiveAmount));
                ivDirectionArrow.setImageDrawable(fragment.getContext().getDrawable(R.drawable.ic_arrow_forward_receive));
            } else {
                vPaymentDirection.setBackgroundColor(fragment.getContext().getResources().getColor(R.color.sendAmount));
                ivDirectionArrow.setImageDrawable(fragment.getContext().getDrawable(R.drawable.ic_arrow_forward_send));
            }

            tvDate.setText(dateFormat.format(transaction.getDate()));
            tvTime.setText(hourFormat.format(transaction.getDate()));

            tvFrom.setText(transaction.getFrom());
            tvTo.setText(transaction.getTo());

            LiveData<CryptoNetAccount> cryptoNetAccountLiveData = cryptoNetAccountViewModel.getCryptoNetAccount();

            //cryptoNetAccountLiveData.observe(this.fragment, new Observer<CryptoNetAccount>() {
            //    @Override
            //    public void onChanged(@Nullable CryptoNetAccount cryptoNetAccount) {
            // TODO is this useful??
                    if (transaction.getInput()){
                        tvTo.setText(transaction.getUserAccountName());

                        if ((transaction.getContactName() != null)&&(!transaction.equals(""))){
                            tvFrom.setText(transaction.getContactName());
                        } else if ((transaction.getBitsharesAccountName() != null)&&(!transaction.equals(""))){
                            tvFrom.setText(transaction.getBitsharesAccountName());
                        }
                    } else {
                        tvFrom.setText(transaction.getUserAccountName());

                        if ((transaction.getContactName() != null)&&(!transaction.equals(""))){
                            tvTo.setText(transaction.getContactName());
                        } else if ((transaction.getBitsharesAccountName() != null)&&(!transaction.equals(""))){
                            tvTo.setText(transaction.getBitsharesAccountName());
                        }
                    }
            //    }
            //});

            String finalAmountText = transaction.getInput() ? "+ " : "";
            finalAmountText += amountString + " " + cryptoCurrency.getName();
            tvCryptoAmount.setText(finalAmountText);
        }
    }
}
