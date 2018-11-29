package cy.agorise.crystalwallet.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.io.File;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import butterknife.OnTextChanged;
import cy.agorise.crystalwallet.R;
import cy.agorise.crystalwallet.application.CrystalSecurityMonitor;
import cy.agorise.crystalwallet.dialogs.material.CrystalDialog;
import cy.agorise.crystalwallet.enums.CryptoCoin;
import cy.agorise.crystalwallet.enums.CryptoNet;
import cy.agorise.crystalwallet.interfaces.OnResponse;
import cy.agorise.crystalwallet.requestmanagers.BitcoinSendRequest;
import cy.agorise.crystalwallet.requestmanagers.BitcoinUriParseRequest;
import cy.agorise.crystalwallet.requestmanagers.CryptoNetInfoRequest;
import cy.agorise.crystalwallet.requestmanagers.CryptoNetInfoRequestListener;
import cy.agorise.crystalwallet.requestmanagers.CryptoNetInfoRequests;
import cy.agorise.crystalwallet.requestmanagers.ValidateBitsharesSendRequest;
import cy.agorise.crystalwallet.dao.CrystalDatabase;
import cy.agorise.crystalwallet.models.ContactAddress;
import cy.agorise.crystalwallet.models.CryptoCoinBalance;
import cy.agorise.crystalwallet.models.CryptoCurrency;
import cy.agorise.crystalwallet.models.CryptoNetAccount;
import cy.agorise.crystalwallet.models.GrapheneAccount;
import cy.agorise.crystalwallet.util.CircularImageView;
import cy.agorise.crystalwallet.viewmodels.ContactViewModel;
import cy.agorise.crystalwallet.viewmodels.CryptoNetAccountListViewModel;
import cy.agorise.crystalwallet.viewmodels.validators.SendTransactionValidator;
import cy.agorise.crystalwallet.viewmodels.validators.UIValidatorListener;
import cy.agorise.crystalwallet.viewmodels.validators.validationfields.ValidationField;
import cy.agorise.crystalwallet.views.CryptoCurrencyAdapter;
import cy.agorise.crystalwallet.views.CryptoNetAccountAdapter;
import cy.agorise.graphenej.Invoice;
import cy.agorise.graphenej.LineItem;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class SendTransactionFragment extends DialogFragment implements UIValidatorListener, ZXingScannerView.ResultHandler {

    private final String TAG = getClass().getName();

    SendTransactionValidator sendTransactionValidator;

    @BindView(R.id.spFrom)
    Spinner spFrom;
    @BindView(R.id.tvFromError)
    TextView tvFromError;
    @BindView(R.id.etTo)
    EditText etTo;
    @BindView(R.id.viewSend)
    View viewSend;
    @BindView(R.id.tvToError)
    TextView tvToError;
    @BindView(R.id.fabCloseCamera)
    FloatingActionButton btnCloseCamera;
    @BindView(R.id.spAsset)
    Spinner spAsset;
    @BindView(R.id.tvAssetError)
    TextView tvAssetError;
    //@BindView(R.id.scrollMain)
    //ScrollView scrollMain;
    @BindView(R.id.etAmount)
    EditText etAmount;
    @BindView(R.id.tvAmountError)
    TextView tvAmountError;
    @BindView (R.id.etMemo)
    EditText etMemo;
    @BindView(R.id.tvMemoError)
    TextView tvMemoError;
    @BindView(R.id.btnSend)
    FloatingActionButton btnSend;
    @BindView(R.id.ivPeople)
    ImageView ivPeople;
    @BindView(R.id.ivCamera)
    ZXingScannerView mScannerView;

    CryptoCurrencyAdapter assetAdapter;

    @BindView(R.id.gravatar)
    CircularImageView userImg;

    @BindView(R.id.viewCamera)
    View viewCamera;

    /* Flag to control when the camera is visible and when is hidden */
    private boolean cameraVisible = false;

    private long cryptoNetAccountId;
    private CryptoNetAccount cryptoNetAccount;
    private GrapheneAccount grapheneAccount;
    private CrystalDatabase db;
    private FloatingActionButton fabSend;
    private AlertDialog.Builder builder;

    /*
        Dialog for loading
    */
    private CrystalDialog crystalDialog;

    public static SendTransactionFragment newInstance(long cryptoNetAccountId) {
        SendTransactionFragment f = new SendTransactionFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putLong("CRYPTO_NET_ACCOUNT_ID", cryptoNetAccountId);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        fabSend = getActivity().findViewById(R.id.fabSend);
        fabSend.hide();

        //AlertDialog.Builder
        builder = new AlertDialog.Builder(getActivity(), R.style.dialog_theme_full);
        //builder.setTitle("Send");

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.send_transaction, null);
        ButterKnife.bind(this, view);

        this.cryptoNetAccountId  = getArguments().getLong("CRYPTO_NET_ACCOUNT_ID",-1);

        final Activity activity = getActivity();

        /*
         *   Add style to the spinner android
         * */
        spFrom.setBackground(getContext().getDrawable(R.drawable.square_color));

        if (this.cryptoNetAccountId != -1) {
            db = CrystalDatabase.getAppDatabase(this.getContext());
            this.cryptoNetAccount = db.cryptoNetAccountDao().getById(this.cryptoNetAccountId);

            CryptoNetAccountListViewModel cryptoNetAccountListViewModel = ViewModelProviders.of(this).get(CryptoNetAccountListViewModel.class);
            List<CryptoNetAccount> cryptoNetAccounts = cryptoNetAccountListViewModel.getCryptoNetAccountList();
            CryptoNetAccountAdapter fromSpinnerAdapter = new CryptoNetAccountAdapter(this.getContext(), android.R.layout.simple_spinner_item, cryptoNetAccounts);

            /*
             *   If only one account block the control
             * */
            //if(cryptoNetAccounts.size()==1){
            //    spFrom.setEnabled(false);
            //}

            spFrom.setAdapter(fromSpinnerAdapter);
            spFrom.setSelection(0);

            setAccountUI();
            /*
             * Custom material spinner implementation
             * */
            //spFrom.setItems(cryptoNetAccounts);
            //spFrom.setSelectedIndex(0);
            //spFrom.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<CryptoNetAccount>() {
            //    @Override
             //   public void onItemSelected(MaterialSpinner view, int position, long id, CryptoNetAccount item) {
            //        sendTransactionValidator.validate();
            //    }
            //});
            //spFrom.setOnNothingSelectedListener(new MaterialSpinner.OnNothingSelectedListener() {

            //    @Override public void onNothingSelected(MaterialSpinner spinner) {

            //    }
            //});

            // etFrom.setText(this.grapheneAccount.getName());
        }

        loadUserImage();

        /* Check for CAMERA permission */
        if (Build.VERSION.SDK_INT >= 23 && !checkCameraPermission())
            requestCameraPermission();

        return builder.setView(view).create();
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

            // TODO SendTransactionValidator to accept spFrom
            sendTransactionValidator = new SendTransactionValidator(this.getContext(), this.cryptoNetAccount, spFrom, etTo, spAsset, etAmount, etMemo);
            sendTransactionValidator.setListener(this);

        } else {
            CryptoCoin cryptoCoin = CryptoCoin.getByCryptoNet(this.cryptoNetAccount.getCryptoNet()).get(0);

            List<String> currencyList = new ArrayList<>();
            currencyList.add(cryptoCoin.getLabel());
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this.getContext(),android.R.layout.simple_list_item_1,currencyList);
            spAsset.setAdapter(arrayAdapter);

            // TODO SendTransactionValidator to accept spFrom
            sendTransactionValidator = new SendTransactionValidator(this.getContext(), this.cryptoNetAccount, spFrom, etTo, spAsset, etAmount, etMemo);
            sendTransactionValidator.setListener(this);

        }
    }

    private boolean checkCameraPermission() {
        int result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.CAMERA)) {
            Toast.makeText(getActivity(), getActivity().getString(R.string.permission_denied_camera), Toast.LENGTH_LONG).show();

            /* Disable the button of the camera visibility */
            btnCloseCamera.setVisibility(View.INVISIBLE);

        } else {
            requestPermissions(new String[] {android.Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_CAMERA_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Now you can use camera .");

                    getActivity().runOnUiThread(new Runnable(){
                        public void run() {
                            Toast.makeText(getActivity(), getActivity().getString(R.string.permission_granted_camera), Toast.LENGTH_LONG).show();
                        }
                    });

                } else {
                    Log.e("value", "Permission Denied, You cannot use the camera.");

                    getActivity().runOnUiThread(new Runnable(){
                        public void run() {
                            Toast.makeText(getActivity(), getActivity().getString(R.string.permission_denied_camera), Toast.LENGTH_LONG).show();
                        }
                    });
                }
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();

        // Force dialog fragment to use the full width of the screen
        Window dialogWindow = getDialog().getWindow();
        assert dialogWindow != null;
        dialogWindow.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadUserImage();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //        btnScanQrCode = ((AlertDialog)dialog).getButton(AlertDialog.BUTTON_NEUTRAL);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                fabSend.show();
            }
        }, 400);
        mScannerView.stopCamera();
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

    @OnItemSelected(R.id.spFrom)
    public void afterFromSelected(Spinner spinner, int position) {
        this.cryptoNetAccount = (CryptoNetAccount)spinner.getSelectedItem();
        setAccountUI();
        this.sendTransactionValidator.validate();
    }

    @OnTextChanged(value = R.id.etTo,
            callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void afterToChanged(Editable editable) {
        this.sendTransactionValidator.validate();
    }

    @OnItemSelected(R.id.spAsset)
    public void afterAssetSelected(Spinner spinner, int position) {
        this.sendTransactionValidator.validate();
    }

    @OnTextChanged(value = R.id.etAmount,
            callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void afterAmountChanged(Editable editable) {
        this.sendTransactionValidator.validate();
    }


    @OnClick(R.id.fabCloseCamera)
    public void onClickCloseCamera(){
        if(cameraVisible)
            hideCamera();
        else
            showCamera();
    }

    /**
     * Shows the camera and hide the black background
     * */
    private void showCamera(){
        /* Change visibilities of views */
        viewCamera.setVisibility(View.GONE);
        mScannerView.setVisibility(View.VISIBLE);

        /* Change icon */
        btnCloseCamera.setImageDrawable(getResources().getDrawable(R.drawable.ic_close));

        /* Reset variable */
        cameraVisible = true;

        /* Star the camera again */
        beginScanQrCode();
    }


    /**
     * Hides the camera and show the black background
     * */
    private void hideCamera(){
        /* Change visibilities of views */
        viewCamera.setVisibility(View.VISIBLE);
        mScannerView.setVisibility(View.INVISIBLE);

        /* Change icon */
        btnCloseCamera.setImageDrawable(getResources().getDrawable(R.drawable.ok));

        /* Reset variable */
        cameraVisible = false;
    }

    @OnTextChanged(value = R.id.etMemo,
            callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void afterMemoChanged(Editable editable) {
        this.sendTransactionValidator.validate();
    }

    @OnClick(R.id.ivPeople)
    public void searchContact(){
        FragmentTransaction ft = this.getActivity().getSupportFragmentManager().beginTransaction();
        Fragment prev = this.getActivity().getSupportFragmentManager().findFragmentByTag("ContactSelectionDialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Show a contact selection list
        ContactSelectionFragment contactSelectionFragment = ContactSelectionFragment.newInstance(this.cryptoNetAccount.getCryptoNet());
        contactSelectionFragment.setTargetFragment(this, 1);
        contactSelectionFragment.show(ft, "ContactSelectionDialog");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);

        if(requestCode == 1) {
            if(resultCode == 1) {
                long contactId = data.getLongExtra("CONTACT_ID",-1);
                if (contactId > -1){
                    ContactViewModel contactViewModel = ViewModelProviders.of(this).get(ContactViewModel.class);
                    contactViewModel.init(contactId);
                    LiveData<List<ContactAddress>> contactAddressesLiveData = contactViewModel.getContactAddresses();

                    contactAddressesLiveData.observe(this, new Observer<List<ContactAddress>>() {
                        @Override
                        public void onChanged(@Nullable List<ContactAddress> contactAddresses) {
                            if (contactAddresses != null) {
                                for (ContactAddress contactAddress : contactAddresses) {
                                    if (contactAddress.getCryptoNet() == cryptoNetAccount.getCryptoNet()) {
                                        etTo.setText(contactAddress.getAddress());
                                    }
                                }
                            }
                        }
                    });
                }
            }
        }
    }

    @OnClick(R.id.btnCancel)
    public void cancel(){
        this.dismiss();
    }

    @OnClick(R.id.btnSend)
    public void sendTransaction(){
        final SendTransactionFragment thisFragment = this;
        final CryptoNetInfoRequest sendRequest;

        if (this.sendTransactionValidator.isValid()) {
            //CryptoNetAccount fromAccountSelected = (CryptoNetAccount) spFrom.getItems().get(spFrom.getSelectedIndex());
            CryptoNetAccount fromAccountSelected = (CryptoNetAccount) spFrom.getSelectedItem();


            if (fromAccountSelected.getCryptoNet() == CryptoNet.BITSHARES) {
                /*
                 * this is only for graphene accounts.
                 *
                 **/
                GrapheneAccount grapheneAccountSelected = new GrapheneAccount(fromAccountSelected);
                grapheneAccountSelected.loadInfo(db.grapheneAccountInfoDao().getByAccountId(fromAccountSelected.getId()));


                //TODO convert the amount to long type using the precision of the currency
                Double amountFromEditText = Double.parseDouble(this.etAmount.getText().toString());
                Long amount = (long) Math.floor(amountFromEditText * Math.round(Math.pow(10, ((CryptoCurrency) spAsset.getSelectedItem()).getPrecision())));

                /*final ValidateBitsharesSendRequest*/
                sendRequest = new ValidateBitsharesSendRequest(
                        this.getContext(),
                        grapheneAccountSelected,
                        this.etTo.getText().toString(),
                        amount,
                        ((CryptoCurrency) spAsset.getSelectedItem()).getName(),
                        etMemo.getText().toString()
                );

                sendRequest.setListener(new CryptoNetInfoRequestListener() {
                    @Override
                    public void onCarryOut() {
                        if (((ValidateBitsharesSendRequest)sendRequest).getStatus().equals(ValidateBitsharesSendRequest.StatusCode.SUCCEEDED)) {
                            try {
                                crystalDialog.dismiss();
                                thisFragment.dismiss();
                                //thisFragment.finalize();
                            } catch (Throwable throwable) {
                                throwable.printStackTrace();
                            }
                        } else {
                            Toast.makeText(getContext(), getContext().getString(R.string.unable_to_send_amount), Toast.LENGTH_LONG);
                        }
                    }
                });
            } else {
                CryptoCoin cryptoCoin = CryptoCoin.getByCryptoNet(this.cryptoNetAccount.getCryptoNet()).get(0);

                //TODO convert the amount to long type using the precision of the currency
                Double amountFromEditText = Double.parseDouble(this.etAmount.getText().toString());
                Long amount = (long) Math.floor(amountFromEditText * (Math.pow(10, cryptoCoin.getPrecision())));

                sendRequest = new BitcoinSendRequest(
                        this.getContext(),
                        this.cryptoNetAccount,
                        this.etTo.getText().toString(),
                        amount,
                        cryptoCoin,
                        etMemo.getText().toString()
                );

                sendRequest.setListener(new CryptoNetInfoRequestListener() {
                    @Override
                    public void onCarryOut() {
                        if (((BitcoinSendRequest)sendRequest).getStatus().equals(ValidateBitsharesSendRequest.StatusCode.SUCCEEDED)) {
                            try {
                                crystalDialog.dismiss();
                                thisFragment.dismiss();
                                //thisFragment.finalize();
                            } catch (Throwable throwable) {
                                throwable.printStackTrace();
                            }
                        } else {
                            Toast.makeText(getContext(), getContext().getString(R.string.unable_to_send_amount), Toast.LENGTH_LONG);
                        }
                    }
                });
            }

            /*
             * If exists mode scurity show it and valide events in case of success or fail
             * */
            CrystalSecurityMonitor.getInstance(this.getActivity()).callPasswordRequest(this.getActivity(), new OnResponse() {
                @Override
                public void onSuccess() {

                    /*
                     * Show loading dialog
                     * */
                    crystalDialog = new CrystalDialog((Activity) getContext());
                    crystalDialog.setText("Sending");
                    crystalDialog.progress();
                    crystalDialog.show();

                    CryptoNetInfoRequests.getInstance().addRequest(sendRequest);
                }

                @Override
                public void onFailed() {

                }
            });
            //CryptoNetInfoRequests.getInstance().addRequest(sendRequest);
        }
    }

    public void beginScanQrCode(){
        //mScannerView = new ZXingScannerView(getContext());
        mScannerView.setFormats(Collections.singletonList(BarcodeFormat.QR_CODE));
        mScannerView.setAspectTolerance(0.5f);
        mScannerView.setAutoFocus(true);
        mScannerView.setLaserColor(R.color.colorAccent);
        mScannerView.setMaskColor(R.color.colorAccent);
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    // Camera Permissions
    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static String[] PERMISSIONS_CAMERA = {
            Manifest.permission.CAMERA
    };


    @Override
    public void onValidationSucceeded(final ValidationField field) {
        final SendTransactionFragment fragment = this;

        getActivity().runOnUiThread(new Runnable() {
            public void run() {

                if (field.getView() == spFrom) {
                    tvFromError.setText("");
                } else if (field.getView() == etTo) {
                    tvToError.setText("");
                } else if (field.getView() == etAmount) {
                    tvAmountError.setText("");
                } else if (field.getView() == spAsset) {
                    tvAssetError.setText("");
                } else if (field.getView() == etMemo) {
                    tvMemoError.setText("");
                }

                if (btnSend != null) {
                    if (sendTransactionValidator.isValid()) {
                        btnSend.setEnabled(true);
                    } else {
                        btnSend.setEnabled(false);
                    }
                }
            }
        });
    }

    @Override
    public void onValidationFailed(final ValidationField field) {
        getActivity().runOnUiThread(new Runnable() {
            public void run() {

                if (field.getView() == spFrom) {
                    tvFromError.setText(field.getMessage());
                } else if (field.getView() == etTo) {
                    tvToError.setText(field.getMessage());
                } else if (field.getView() == spAsset) {
                    tvAssetError.setText(field.getMessage());
                } else if (field.getView() == etAmount) {
                    tvAmountError.setText(field.getMessage());
                } else if (field.getView() == etMemo) {
                    tvMemoError.setText(field.getMessage());
                }
            }
        });
    }

    @Override
    public void handleResult(Result result) {
        try {
            Invoice invoice = Invoice.fromQrCode(result.getText());

            Log.d(TAG, "QR Code read: " + invoice.toJsonString());

            etTo.setText(invoice.getTo());

            for (int i = 0; i < assetAdapter.getCount(); i++) {
                if (assetAdapter.getItem(i).getName().equals(invoice.getCurrency().toUpperCase())) {
                    spAsset.setSelection(i);
                    break;
                }
            }
            etMemo.setText(invoice.getMemo());


            double amount = 0.0;
            for (LineItem nextItem : invoice.getLineItems()) {
                amount += nextItem.getQuantity() * nextItem.getPrice();
            }
            DecimalFormat df = new DecimalFormat("####.####");
            df.setRoundingMode(RoundingMode.CEILING);
            df.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.ENGLISH));
            etAmount.setText(df.format(amount));
            Log.i("SendFragment", result.getText());
            return;
        }catch(Exception e){
            e.printStackTrace();
        }

        //Is not a bitshares QR
        //CryptoCoin cryptoCoin = CryptoCoin.getByCryptoNet(this.cryptoNetAccount.getCryptoNet()).get(0);
        final BitcoinUriParseRequest bitcoinUriParseRequest = new BitcoinUriParseRequest(result.getText(), CryptoCoin.BITCOIN);

        bitcoinUriParseRequest.setListener(new CryptoNetInfoRequestListener() {
            @Override
            public void onCarryOut() {
                if (bitcoinUriParseRequest.getAddress() != null) {
                    if (!bitcoinUriParseRequest.getAddress().equals("")) {
                        etTo.setText(bitcoinUriParseRequest.getAddress());
                    }
                    if (bitcoinUriParseRequest.getAmount() > 0) {
                        etAmount.setText(bitcoinUriParseRequest.getAmount().toString());
                    }
                    if (!bitcoinUriParseRequest.getMemo().equals("")) {
                        etMemo.setText(bitcoinUriParseRequest.getMemo());
                    }
                } else {
                    Toast.makeText(getContext(), "Not a valid QR info", Toast.LENGTH_LONG);
                }
            }
        });

        CryptoNetInfoRequests.getInstance().addRequest(bitcoinUriParseRequest);
    }
}