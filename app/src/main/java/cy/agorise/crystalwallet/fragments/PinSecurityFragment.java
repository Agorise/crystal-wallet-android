package cy.agorise.crystalwallet.fragments;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import butterknife.OnTextChanged;
import cy.agorise.crystalwallet.R;
import cy.agorise.crystalwallet.application.CrystalSecurityMonitor;
import cy.agorise.crystalwallet.dialogs.material.DialogMaterial;
import cy.agorise.crystalwallet.dialogs.material.NegativeResponse;
import cy.agorise.crystalwallet.dialogs.material.PositiveResponse;
import cy.agorise.crystalwallet.dialogs.material.QuestionDialog;
import cy.agorise.crystalwallet.models.GeneralSetting;
import cy.agorise.crystalwallet.util.PasswordManager;
import cy.agorise.crystalwallet.viewmodels.GeneralSettingListViewModel;
import cy.agorise.crystalwallet.viewmodels.validators.PinSecurityValidator;
import cy.agorise.crystalwallet.viewmodels.validators.UIValidatorListener;
import cy.agorise.crystalwallet.viewmodels.validators.validationfields.ValidationField;

/**
 * Created by xd on 1/18/18.
 */

public class PinSecurityFragment extends Fragment implements UIValidatorListener {

    @BindView(R.id.etNewPin)
    EditText etNewPin;
    @BindView(R.id.etConfirmPin)
    EditText etConfirmPin;

    @BindView(R.id.tvNewPinError)
    TextView tvNewPinError;
    @BindView(R.id.tvConfirmPinError)
    TextView tvConfirmPinError;

    @BindView(R.id.btnOK)
    Button btnOK;

    /*
    * Validates the new typing for the patterns
    * */
    private boolean first = true;

    /*
    * Flag to check if validation of fields is correct
    * */
    private boolean valid = false;

    GeneralSettingListViewModel generalSettingListViewModel;
    GeneralSetting passwordGeneralSetting;
    PinSecurityValidator pinSecurityValidator;

    public PinSecurityFragment() {
        // Required empty public constructor
    }

    public static PinSecurityFragment newInstance() {
        PinSecurityFragment fragment = new PinSecurityFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_pin_security, container, false);
        ButterKnife.bind(this, v);

        /*
        *   Initially not enabled til it passes validations
        * */
        btnOK.setEnabled(false);

        generalSettingListViewModel = ViewModelProviders.of(this).get(GeneralSettingListViewModel.class);
        LiveData<List<GeneralSetting>> generalSettingsLiveData = generalSettingListViewModel.getGeneralSettingList();

        pinSecurityValidator = new PinSecurityValidator(this.getContext(), etNewPin, etConfirmPin);
        pinSecurityValidator.setListener(this);

        /*
         * If PIN is configured set some text
         * */
        final CrystalSecurityMonitor crystalSecurityMonitor = CrystalSecurityMonitor.getInstance(getActivity());
        switch(CrystalSecurityMonitor.getInstance(getActivity()).actualSecurity()) {
            case GeneralSetting.SETTING_PASSWORD:

                if(etNewPin!=null && etConfirmPin!=null){
                    etNewPin.setText("123456");
                    etConfirmPin.setText("123456");
                }

                break;
            case GeneralSetting.SETTING_PATTERN:
                break;

            default:

                if(etNewPin!=null && etConfirmPin!=null) {
                    etNewPin.setText("");
                    etConfirmPin.setText("");
                }

        }

        /*
         * Focus in no where
         * */
        tvNewPinError.requestFocus();

        return v;
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        first = true;

        /*
         * If PIN is configured set some text
         * */
        final CrystalSecurityMonitor crystalSecurityMonitor = CrystalSecurityMonitor.getInstance(getActivity());
        switch(CrystalSecurityMonitor.getInstance(getActivity()).actualSecurity()) {
            case GeneralSetting.SETTING_PASSWORD:

                if(etNewPin!=null && etConfirmPin!=null){
                    etNewPin.setText("123456");
                    etConfirmPin.setText("123456");
                }

                break;
            case GeneralSetting.SETTING_PATTERN:
                break;

            default:

                if(etNewPin!=null && etConfirmPin!=null) {
                    etNewPin.setText("");
                    etConfirmPin.setText("");
                }
        }

        /*
         * Focus in no where
         * */
        if(getActivity()!=null){
            tvNewPinError.requestFocus();
        }
    }

    @OnClick(R.id.btnOK)
    void okClic(final View view) {

        /*
        * Only can continue if the fields are correctly validated
        * */
        if(valid){

            /*
             *   Question if continue or not
             * */
            final QuestionDialog questionDialog = new QuestionDialog(getActivity());
            questionDialog.setText(getActivity().getString(R.string.question_continue));
            questionDialog.setOnNegative(new NegativeResponse() {
                @Override
                public void onNegative(@NotNull DialogMaterial dialogMaterial) {
                }
            });
            questionDialog.setOnPositive(new PositiveResponse() {
                @Override
                public void onPositive() {
                    savePassword();
                }
            });
            questionDialog.show();
        }
    }

    @OnTextChanged(value = R.id.etNewPin,
            callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void afterNewPinChanged(Editable editable) {
        this.pinSecurityValidator.validate();
    }

    @OnTextChanged(value = R.id.etConfirmPin,
            callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void afterConfirmPinChanged(Editable editable) {
        this.pinSecurityValidator.validate();
    }

    public void clearFields(){
        if (!this.etNewPin.getText().toString().equals("")) {
            this.etNewPin.setText("");
        }
        if (!this.etConfirmPin.getText().toString().equals("")) {
            this.etConfirmPin.setText("");
        }
    }

    @OnFocusChange({R.id.etNewPin,R.id.etConfirmPin})
    public void focusChangePIN(View view, boolean hasFocus){
        if(hasFocus){
            if(first){
                first = false;
                clearFields();
            }
        }
    }

    @Override
    public void onValidationSucceeded(final ValidationField field) {
        final PinSecurityFragment fragment = this;

        this.getActivity().runOnUiThread(new Runnable() {
            public void run() {

                if (field.getView() == etNewPin){
                    tvNewPinError.setText("");
                } else if (field.getView() == etConfirmPin){
                    tvConfirmPinError.setText("");
                }

                if (pinSecurityValidator.isValid()){
                    //savePassword();

                    if(!first){

                        //Now is valid
                        valid = true;

                        /*
                         *   Enable ok button to continue
                         * */
                        btnOK.setEnabled(true);
                    }

                }
            }
        });
    }

    private void savePassword(){

        savePassword(etNewPin.getText().toString());

        CharSequence text = "Your password has been sucessfully changed!";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(getContext(), text, duration);
        toast.show();

        etNewPin.setText("123456");
        etConfirmPin.setText("123456");

        first = true;

        btnOK.setEnabled(false);

        /*
         * Focus in no where
         * */
        tvNewPinError.requestFocus();
    }

    public void savePassword(String password) {
        String passwordEncripted = PasswordManager.encriptPassword(password);
        CrystalSecurityMonitor.getInstance(getActivity()).setPasswordSecurity(passwordEncripted);

        //CrystalSecurityMonitor.getInstance(getActivity()).callPasswordRequest(this.getActivity());
    }

    @Override
    public void onValidationFailed(final ValidationField field) {

        //Still false
        valid = false;

        /*
         *   Disable til it passes validations
         * */
        btnOK.setEnabled(false);

        this.getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (field.getView() == etNewPin){
                    tvNewPinError.setText(field.getMessage());
                } else if (field.getView() == etConfirmPin){
                    tvConfirmPinError.setText(field.getMessage());
                }
            }
        });
    }
}
