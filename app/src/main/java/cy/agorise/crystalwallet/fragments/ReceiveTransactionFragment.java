package cy.agorise.crystalwallet.fragments;

import android.app.Dialog;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import butterknife.OnClick;
import cy.agorise.crystalwallet.enums.CryptoCoin;
import cy.agorise.crystalwallet.enums.CryptoNet;
import cy.agorise.crystalwallet.requestmanagers.CalculateBitcoinUriRequest;
import cy.agorise.crystalwallet.requestmanagers.CryptoNetInfoRequestListener;
import cy.agorise.crystalwallet.requestmanagers.CryptoNetInfoRequests;
import cy.agorise.crystalwallet.util.CircularImageView;
import cy.agorise.crystalwallet.viewmodels.CryptoNetAccountListViewModel;
import cy.agorise.crystalwallet.views.CryptoNetAccountAdapter;
import cy.agorise.graphenej.Invoice;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemSelected;
import butterknife.OnTextChanged;
import cy.agorise.crystalwallet.R;
import cy.agorise.crystalwallet.dao.CrystalDatabase;
import cy.agorise.crystalwallet.models.CryptoCoinBalance;
import cy.agorise.crystalwallet.models.CryptoCurrency;
import cy.agorise.crystalwallet.models.CryptoNetAccount;
import cy.agorise.crystalwallet.models.GrapheneAccount;
import cy.agorise.crystalwallet.viewmodels.validators.ReceiveTransactionValidator;
import cy.agorise.crystalwallet.viewmodels.validators.UIValidatorListener;
import cy.agorise.crystalwallet.viewmodels.validators.validationfields.ValidationField;
import cy.agorise.crystalwallet.views.CryptoCurrencyAdapter;
import cy.agorise.graphenej.LineItem;

public class ReceiveTransactionFragment extends DialogFragment implements UIValidatorListener {

    ReceiveTransactionValidator receiveTransactionValidator;

    @BindView(R.id.spTo)
    Spinner spTo;
    @BindView(R.id.etAmount)
    EditText etAmount;
    @BindView(R.id.tvAmountError)
    TextView tvAmountError;
    @BindView(R.id.spAsset)
    Spinner spAsset;
    @BindView(R.id.tvAssetError)
    TextView tvAssetError;
    @BindView(R.id.ivQrCode)
    ImageView ivQrCode;
    @BindView(R.id.pbQrCode)
    ProgressBar pbQrCode;
    @BindView(R.id.tvCancel)
    TextView tvCancel;

    @BindView(R.id.gravatar)
    CircularImageView userImg;

    private Button btnShareQrCode;
    private Button btnClose;

    private long cryptoNetAccountId;
    private CryptoNetAccount cryptoNetAccount;
    private CryptoCurrency cryptoCurrency;
    private GrapheneAccount grapheneAccount;
    private CrystalDatabase db;

    private Invoice invoice;
    private ArrayList<LineItem> invoiceItems;

    private FloatingActionButton fabReceive;

    private AsyncTask qrCodeTask;

    private Double lastAmount = -1.0;

    public static ReceiveTransactionFragment newInstance(long cryptoNetAccountId) {
        ReceiveTransactionFragment f = new ReceiveTransactionFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putLong("CRYPTO_NET_ACCOUNT_ID", cryptoNetAccountId);
        f.setArguments(args);

        f.invoiceItems = new ArrayList<LineItem>();
        f.invoice = new Invoice("","","","",null,"","");

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        fabReceive = getActivity().findViewById(R.id.fabReceive);
        fabReceive.hide();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.ReceiveTransactionTheme);
        //builder.setTitle("Receive Assets");

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.receive_transaction, null);
        ButterKnife.bind(this, view);

        this.cryptoNetAccountId  = getArguments().getLong("CRYPTO_NET_ACCOUNT_ID",-1);

        if (this.cryptoNetAccountId != -1) {
            db = CrystalDatabase.getAppDatabase(this.getContext());
            this.cryptoNetAccount = db.cryptoNetAccountDao().getById(this.cryptoNetAccountId);

            CryptoNetAccountListViewModel cryptoNetAccountListViewModel = ViewModelProviders.of(this).get(CryptoNetAccountListViewModel.class);
            List<CryptoNetAccount> cryptoNetAccounts = cryptoNetAccountListViewModel.getCryptoNetAccountList();
            CryptoNetAccountAdapter toSpinnerAdapter = new CryptoNetAccountAdapter(this.getContext(), android.R.layout.simple_spinner_item, cryptoNetAccounts);
            spTo.setAdapter(toSpinnerAdapter);
            spTo.setSelection(0);

            setAccountUI();
        }

        builder.setView(view);

        /*builder.setPositiveButton("Share this QR",  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                shareQrCode();
            }
        });*/
        /*builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });*/

        AlertDialog dialog = builder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                btnShareQrCode = ((AlertDialog)dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                btnClose = ((AlertDialog)dialog).getButton(AlertDialog.BUTTON_NEGATIVE);

                btnShareQrCode.setEnabled(false);
            }
        });


        loadUserImage();
        return dialog;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Force dialog fragment to use the full width of the screen
        Window dialogWindow = getDialog().getWindow();
        assert dialogWindow != null;
        dialogWindow.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadUserImage();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                fabReceive.show();
            }
        }, 400);
    }

    public void loadUserImage(){
        //Search for a existing photo
        ContextWrapper cw = new ContextWrapper(this.getActivity().getBaseContext());
        File directory = cw.getDir("profile", Context.MODE_PRIVATE);
        if (!directory.exists()) {
            directory.mkdir();
        }
        File photoFile = new File(directory + File.separator + "photo.png");

        if (photoFile.exists()){
            Bitmap bitmap = BitmapFactory.decodeFile(photoFile.getPath());
            userImg.setImageBitmap(bitmap);
        }
    }

    public void setAccountUI(){
        if (this.cryptoNetAccount.getCryptoNet() == CryptoNet.BITSHARES) {
            /*
             * this is only for graphene accounts.
             *
             **/
            this.grapheneAccount = new GrapheneAccount(this.cryptoNetAccount);
            this.grapheneAccount.loadInfo(db.grapheneAccountInfoDao().getByAccountId(this.cryptoNetAccountId));

            final LiveData<List<CryptoCoinBalance>> balancesList = db.cryptoCoinBalanceDao().getBalancesFromAccount(cryptoNetAccountId);
            balancesList.observe(this, new Observer<List<CryptoCoinBalance>>() {
                @Override
                public void onChanged(@Nullable List<CryptoCoinBalance> cryptoCoinBalances) {
                    ArrayList<Long> assetIds = new ArrayList<Long>();
                    for (CryptoCoinBalance nextBalance : balancesList.getValue()) {
                        assetIds.add(nextBalance.getCryptoCurrencyId());
                    }
                    List<CryptoCurrency> cryptoCurrencyList = db.cryptoCurrencyDao().getByIds(assetIds);

                    /*
                     * Test
                     * */
                    //CryptoCurrency crypto1 = new CryptoCurrency();
                    //crypto1.setId(1);
                    //crypto1.setName("BITCOIN");
                    //crypto1.setPrecision(1);
                    //cryptoCurrencyList.add(crypto1);


                    CryptoCurrencyAdapter assetAdapter = new CryptoCurrencyAdapter(getContext(), android.R.layout.simple_spinner_item, cryptoCurrencyList);
                    spAsset.setAdapter(assetAdapter);
                }
            });

            receiveTransactionValidator = new ReceiveTransactionValidator(this.getContext(), this.cryptoNetAccount, spAsset, etAmount);
            receiveTransactionValidator.setListener(this);
        } else {
            CryptoCoin cryptoCoin = CryptoCoin.getByCryptoNet(this.cryptoNetAccount.getCryptoNet()).get(0);

            List<String> currencyList = new ArrayList<>();
            currencyList.add(cryptoCoin.getLabel());
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this.getContext(),android.R.layout.simple_list_item_1,currencyList);
            spAsset.setAdapter(arrayAdapter);

            receiveTransactionValidator = new ReceiveTransactionValidator(this.getContext(), this.cryptoNetAccount, spAsset, etAmount);
            receiveTransactionValidator.setListener(this);
        }
    }

    @OnItemSelected(R.id.spTo)
    public void afterToSelected(Spinner spinner, int position) {
        this.cryptoNetAccount = (CryptoNetAccount)spinner.getSelectedItem();
        setAccountUI();
        this.receiveTransactionValidator.validate();
    }

    @OnTextChanged(value = R.id.etAmount,
            callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void afterAmountChanged(Editable editable) {
        this.receiveTransactionValidator.validate();
    }

    @OnItemSelected(R.id.spAsset)
    public void afterAssetSelected(Spinner spinner, int position) {
        if (spinner.getSelectedItem() instanceof CryptoCurrency) {
            this.cryptoCurrency = (CryptoCurrency) spinner.getSelectedItem();
        }

        this.receiveTransactionValidator.validate();
    }

    public void shareQrCode(){
        if (this.receiveTransactionValidator.isValid()) {
            //Share Qr Code
        }
    }

    @OnClick(R.id.tvCancel)
    public void cancel(){
        this.dismiss();
    }

    @Override
    public void onValidationSucceeded(final ValidationField field) {
        final ReceiveTransactionFragment fragment = this;


        if (field.getView() == etAmount){
            tvAmountError.setText("");
        } else if (field.getView() == spAsset){
            tvAssetError.setText("");
        }

        if (btnShareQrCode != null) {
            if (receiveTransactionValidator.isValid()) {
                createQrCode();
                btnShareQrCode.setEnabled(true);
            } else {
                btnShareQrCode.setEnabled(false);
            }
        }
    }

    @Override
    public void onValidationFailed(ValidationField field) {
        if (field.getView() == spAsset){
            tvAssetError.setText(field.getMessage());
        } else if (field.getView() == etAmount){
            tvAmountError.setText(field.getMessage());
        }

        ivQrCode.setImageResource(android.R.color.transparent);
    }

    public void createQrCode(){
        final Double amount;
        try{
            amount = Double.valueOf(this.etAmount.getText().toString());

        } catch(NumberFormatException e){
            lastAmount = -1.0;
            Log.e("ReceiveFragment","Amount casting error.");
            return;
        }

        if (!amount.equals(lastAmount)) {
            pbQrCode.setVisibility(View.VISIBLE);
            lastAmount = amount;
            CryptoNetAccount toAccountSelected = (CryptoNetAccount) spTo.getSelectedItem();

            if (this.qrCodeTask != null) {
                this.qrCodeTask.cancel(true);
            }

            if (this.cryptoNetAccount.getCryptoNet() == CryptoNet.BITSHARES) {
                /*
                 * this is only for graphene accounts.
                 *
                 **/
                GrapheneAccount grapheneAccountSelected = new GrapheneAccount(toAccountSelected);
                grapheneAccountSelected.loadInfo(db.grapheneAccountInfoDao().getByAccountId(toAccountSelected.getId()));


                this.invoiceItems.clear();
                this.invoiceItems.add(
                        new LineItem("transfer", 1, amount)
                );

                LineItem items[] = new LineItem[this.invoiceItems.size()];
                items = this.invoiceItems.toArray(items);
                this.invoice.setLineItems(items);
                this.invoice.setTo(grapheneAccountSelected.getName());
                this.invoice.setCurrency(this.cryptoCurrency.getName());

                //if (this.qrCodeTask != null) {
                //    this.qrCodeTask.cancel(true);
                //}

                this.qrCodeTask = new AsyncTask<Object, Void, Void>() {

                    @Override
                    protected Void doInBackground(Object... voids) {
                        try {
                            final Bitmap bitmap = textToImageEncode(Invoice.toQrCode(invoice));

                            if (!this.isCancelled()) {
                                ReceiveTransactionFragment.this.getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ivQrCode.setImageBitmap(bitmap);
                                        pbQrCode.setVisibility(View.GONE);
                                    }
                                });
                            }
                        } catch (WriterException e) {
                            Log.e("ReceiveFragment", "Error creating QrCode");
                        }

                        return null;
                    }
                };

                this.qrCodeTask.execute(null, null, null);
            } else {
                final CryptoCoin cryptoCoin = CryptoCoin.getByCryptoNet(this.cryptoNetAccount.getCryptoNet()).get(0);

                final CalculateBitcoinUriRequest uriRequest = new CalculateBitcoinUriRequest(cryptoCoin, cryptoNetAccount, getContext(), amount);

                uriRequest.setListener(new CryptoNetInfoRequestListener() {
                    @Override
                    public void onCarryOut() {
                        if (uriRequest.getUri() != null) {
                            qrCodeTask = new AsyncTask<Object, Void, Void>() {

                                @Override
                                protected Void doInBackground(Object... voids) {
                                    try {
                                        final Bitmap bitmap = textToImageEncode(uriRequest.getUri());

                                        if (!this.isCancelled()) {
                                            ReceiveTransactionFragment.this.getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    //Double amountNow = -1.0;
                                                    //try{
                                                    //    amountNow = Double.valueOf(etAmount.getText().toString());
                                                    //} catch(NumberFormatException e){
                                                    //}
                                                    //if (amountNow >= 0) {
                                                        if (amount.equals(lastAmount)) {
                                                            if (!isCancelled()) {
                                                                ivQrCode.setImageBitmap(bitmap);
                                                                pbQrCode.setVisibility(View.GONE);
                                                            }
                                                        }
                                                    //}
                                                }
                                            });
                                        }
                                    } catch (WriterException e) {
                                        Log.e("ReceiveFragment", "Error creating QrCode");
                                    }

                                    return null;
                                }
                            };

                            qrCodeTask.execute(null, null, null);
                        } else {
                            Log.e("ReceiveFragment", "Error obtaining the uri");
                        }
                    }
                });

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        CryptoNetInfoRequests.getInstance().addRequest(uriRequest);
                    }
                });
                thread.start();
            }
        }
    }

    Bitmap textToImageEncode(String Value) throws WriterException {
        Bitmap bitmap = null;
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(Value, BarcodeFormat.QR_CODE, ivQrCode.getWidth(), ivQrCode.getHeight());
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            bitmap = barcodeEncoder.createBitmap(bitMatrix);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        return bitmap;
    }
}
