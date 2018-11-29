package cy.agorise.crystalwallet.activities;

import android.app.Activity;
import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.thekhaeng.pushdownanim.PushDownAnim;

import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import cy.agorise.crystalwallet.R;
import cy.agorise.crystalwallet.dialogs.material.CrystalLoading;
import cy.agorise.crystalwallet.dialogs.material.DialogMaterial;
import cy.agorise.crystalwallet.dialogs.material.NegativeResponse;
import cy.agorise.crystalwallet.dialogs.material.PositiveResponse;
import cy.agorise.crystalwallet.dialogs.material.QuestionDialog;
import cy.agorise.crystalwallet.requestmanagers.CryptoNetInfoRequestListener;
import cy.agorise.crystalwallet.requestmanagers.CryptoNetInfoRequests;
import cy.agorise.crystalwallet.requestmanagers.ImportBitsharesAccountRequest;
import cy.agorise.crystalwallet.viewmodels.AccountSeedViewModel;
import cy.agorise.crystalwallet.viewmodels.validators.ImportSeedValidator;
import cy.agorise.crystalwallet.viewmodels.validators.UIValidatorListener;
import cy.agorise.crystalwallet.viewmodels.validators.validationfields.ValidationField;

public class ImportSeedActivity extends AppCompatActivity implements UIValidatorListener {

    AccountSeedViewModel accountSeedViewModel;
    ImportSeedValidator importSeedValidator;

    @BindView(R.id.etPin)
    EditText etPin;

    @BindView(R.id.txtErrorPIN)
    TextView txtErrorPIN;

    @BindView(R.id.txtErrorAccount)
    TextView txtErrorAccount;

    //@BindView(R.id.tvPinError)
    //TextView tvPinError;

    @BindView(R.id.etPinConfirmation)
    EditText etPinConfirmation;
    //@BindView(R.id.tvPinConfirmationError)
    //TextView tvPinConfirmationError;

    @BindView(R.id.etSeedWords)
    EditText etSeedWords;
    //@BindView(R.id.tvSeedWordsError)
    //TextView tvSeedWordsError;

    @BindView (R.id.etAccountName)
    EditText etAccountName;
    //@BindView(R.id.tvAccountNameError)
    //TextView tvAccountNameError;

    @BindView(R.id.btnImport)
    Button btnImport;

    @BindView(R.id.btnCancel)
    Button btnCancel;

    final Activity activity = this;

    /*
    * Flag to check correct PIN equality
    * */
    private boolean pinsOK = false;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.import_seed);

        ButterKnife.bind(this);

        /*
         *   Integration of library with button efects
         * */
        PushDownAnim.setPushDownAnimTo(btnCancel)
                .setOnClickListener( new View.OnClickListener(){
                    @Override
                    public void onClick( View view ){
                        cancel();
                    }
                } );
        PushDownAnim.setPushDownAnimTo(btnImport)
                .setOnClickListener( new View.OnClickListener(){
                    @Override
                    public void onClick( View view ){
                        importSeed();
                    }
                } );

        /*
        * Initially the button CREATE WALLET should be disabled
        * */
        disableCreate();

        /*
        * When a text change in any of the fields
        * */
        etPin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                /*
                 * Validate that PINs are equals
                 * */
                validatePINS();

                /*
                 * If all is ready to continue enable the button, contrarie case disable it
                 * */
                if(allFieldsAreOK()){
                    enableCreate();
                }
                else{
                    disableCreate();
                }
            }
        });
        etPinConfirmation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                /*
                 * Validate that PINs are equals
                 * */
                validatePINS();

                /*
                 * If all is ready to continue enable the button, contrarie case disable it
                 * */
                if(allFieldsAreOK()){
                    enableCreate();
                }
                else{
                    disableCreate();
                }
            }
        });
        etSeedWords.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                /*
                 * Validate that PINs are equals
                 * */
                validatePINS();

                /*
                 * If all is ready to continue enable the button, contrarie case disable it
                 * */
                if(allFieldsAreOK()){
                    enableCreate();
                }
                else{
                    disableCreate();
                }

                /*
                * Hide error field
                * */
                clearErrors();
            }
        });
        /*
        etAccountName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                //
                // Validate that PINs are equals
                //
                validatePINS();

                //
                // If all is ready to continue enable the button, contrarie case disable it
                //
                if(allFieldsAreOK()){
                    enableCreate();
                }
                else{
                    disableCreate();
                }
            }
        });
        */

        accountSeedViewModel = ViewModelProviders.of(this).get(AccountSeedViewModel.class);
        importSeedValidator = new ImportSeedValidator(this.getApplicationContext(),etPin,etPinConfirmation,etAccountName,etSeedWords);
        importSeedValidator.setListener(this);
    }


    private void clearErrors(){
        txtErrorPIN.setVisibility(View.INVISIBLE);
        txtErrorAccount.setVisibility(View.INVISIBLE);
    }


    /*
    * Validate that PINs are equals
    * */
    private void validatePINS(){

        final String pin = etPin.getText().toString().trim();
        final String confirmoPIN = etPinConfirmation.getText().toString().trim();
        if(!pin.isEmpty() && !confirmoPIN.isEmpty()){
            if(pin.compareTo(confirmoPIN)!=0){
                pinsOK = false;
                txtErrorPIN.setVisibility(View.VISIBLE);
            }
            else{
                pinsOK = true;
                clearErrors();
            }
        }
        else{
            pinsOK = false;
            clearErrors();
        }
    }


    /*
    *   Method to validate if all the fields are fill and correctly
    * */
    private boolean allFieldsAreOK(){

        boolean complete = false;
        if( etPin.getText().toString().trim().compareTo("")!=0 &&
            etPinConfirmation.getText().toString().trim().compareTo("")!=0 &&
                etSeedWords.getText().toString().trim().compareTo("")!=0 /*&&
                etAccountName.getText().toString().trim().compareTo("")!=0*/){
            if(pinsOK){
                complete = true;
            }
        }
        return complete;
    }

    @OnTextChanged(value = R.id.etPin,
            callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void afterPinChanged(Editable editable) {
        this.importSeedValidator.validate();
    }

    @OnTextChanged(value = R.id.etPinConfirmation,
            callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void afterPinConfirmationChanged(Editable editable) {
        this.importSeedValidator.validate();
    }

    @OnTextChanged(value = R.id.etSeedWords,
            callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void afterSeedWordsChanged(Editable editable) {
        this.importSeedValidator.validate();
    }
    /*@OnTextChanged(value = R.id.etAccountName,
            callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void afterAccountNameChanged(Editable editable) {
        this.importSeedValidator.validate();
    }*/

    @OnClick(R.id.btnCancel)
    public void cancel(){
        this.finish();
    }

    @OnClick(R.id.btnImport)
    public void importSeed(){
        final ImportSeedActivity thisActivity = this;

        if (this.importSeedValidator.isValid()) {

            /*
             * Question if continue
             * */
            final QuestionDialog questionDialog = new QuestionDialog(activity);
            questionDialog.setText(activity.getString(R.string.question_continue));
            questionDialog.setOnNegative(new NegativeResponse() {
                @Override
                public void onNegative(@NotNull DialogMaterial dialogMaterial) {
                }
            });
            questionDialog.setOnPositive(new PositiveResponse() {
                @Override
                public void onPositive() {

                    /*
                     * Loading dialog
                     * */
                    final CrystalLoading crystalLoading = new CrystalLoading(activity);
                    crystalLoading.show();

                    /*
                     * Final service connection
                     * */
                    finalStep(crystalLoading);

                    /*
                     * Validate mnemonic with the server
                     * */
                    /*final ImportBitsharesAccountRequest request = new ImportBitsharesAccountRequest(etSeedWords.getText().toString().trim(),activity);
                    request.setListener(new CryptoNetInfoRequestListener() {
                        @Override
                        public void onCarryOut() {
                            if(request.getStatus().equals(ImportBitsharesAccountRequest.StatusCode.SUCCEEDED)){

                                //Correct

                                finalStep(crystalLoading);

                            }
                            else{

                                crystalLoading.dismiss();

                                txtErrorAccount.setVisibility(View.VISIBLE);
                                txtErrorAccount.setText(activity.getResources().getString(R.string.error_invalid_account));
                            }
                        }
                    });
                    CryptoNetInfoRequests.getInstance().addRequest(request);*/

                }
            });
            questionDialog.show();
        }
    }




    private void finalStep(final CrystalLoading crystalLoading){

        final ImportSeedActivity thisActivity = this;

        final ImportBitsharesAccountRequest validatorRequest =
                new ImportBitsharesAccountRequest(etSeedWords.getText().toString(), getApplicationContext(), true);

        validatorRequest.setListener(new CryptoNetInfoRequestListener() {
            @Override
            public void onCarryOut() {

                /*
                 * Hide the loading dialog
                 * */
                crystalLoading.dismiss();

                if (!validatorRequest.getStatus().equals(ImportBitsharesAccountRequest.StatusCode.SUCCEEDED)) {

                    switch (validatorRequest.getStatus()){
                        case PETITION_FAILED:
                        case NO_INTERNET:
                        case NO_SERVER_CONNECTION:
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    txtErrorAccount.setText(activity.getResources().getString(R.string.NO_SERVER_CONNECTION));
                                }
                            });
                            break;
                        case ACCOUNT_DOESNT_EXIST:
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    txtErrorAccount.setText(activity.getResources().getString(R.string.ACCOUNT_DOESNT_EXIST));
                                }
                            });
                            break;
                        case BAD_SEED:
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    txtErrorAccount.setText(activity.getResources().getString(R.string.BAD_SEED));
                                }
                            });
                            break;
                        case NO_ACCOUNT_DATA:
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    txtErrorAccount.setText(activity.getResources().getString(R.string.NO_ACCOUNT_DATA));
                                }
                            });
                            break;

                            default:
                                txtErrorAccount.setText(activity.getResources().getString(R.string.ERROR_UNRECOGNIZABLE));

                    }

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            txtErrorAccount.setVisibility(View.VISIBLE);
                        }
                    });

                    //Toast.makeText(thisActivity.getApplicationContext(),errorText,Toast.LENGTH_LONG).show();

                } else {
                    Intent intent = new Intent(thisActivity, BoardActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        });

        CryptoNetInfoRequests.getInstance().addRequest(validatorRequest);
    }

    @Override
    public void onValidationSucceeded(final ValidationField field) {
        final ImportSeedActivity activity = this;

        activity.runOnUiThread(new Runnable() {
            public void run() {

                if (field.getView() == etPin) {
                    //tvPinError.setText("");
                } else if (field.getView() == etPinConfirmation){
                    //tvPinConfirmationError.setText("");
                } else if (field.getView() == etAccountName){
                    //tvAccountNameError.setText("");
                } else if (field.getView() == etSeedWords){
                    //tvSeedWordsError.setText("");
                }

                if (activity.importSeedValidator.isValid()){
                    enableCreate();
                } else {
                    disableCreate();
                }

            }
        });
    }

    @Override
    public void onValidationFailed(ValidationField field) {
        if (field.getView() == etPin) {
            //tvPinError.setText(field.getMessage());
        } else if (field.getView() == etPinConfirmation){
            //tvPinConfirmationError.setText(field.getMessage());
        } else if (field.getView() == etAccountName){
            //tvAccountNameError.setText(field.getMessage());
        } else if (field.getView() == etSeedWords){
            //tvSeedWordsError.setText(field.getMessage());
        }
    }


    /*
     * Enable create button
     * */
    private void enableCreate() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //btnImport.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                btnImport.setEnabled(true);
            }
        });
    }

    /*
     * Disable create button
     * */
    private void disableCreate() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btnImport.setEnabled(false);
                //btnImport.setBackground(getDrawable(R.drawable.disable_style));
            }
        });
    }
}
