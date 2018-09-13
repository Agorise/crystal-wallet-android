package cy.agorise.crystalwallet.viewmodels.validators.validationfields;

import android.widget.EditText;

import cy.agorise.crystalwallet.R;
import cy.agorise.crystalwallet.requestmanagers.CryptoNetInfoRequestListener;
import cy.agorise.crystalwallet.requestmanagers.CryptoNetInfoRequests;
import cy.agorise.crystalwallet.requestmanagers.ValidateImportBitsharesAccountRequest;

/**
 * Created by Henry Varona on 7/10/2017.
 */

public class BitsharesAccountMnemonicValidationField extends ValidationField {

    private EditText mnemonicField;
    private EditText accountNameField;

    public BitsharesAccountMnemonicValidationField(EditText mnemonicField, EditText accountNameField){
        super(mnemonicField);
        this.mnemonicField = mnemonicField;
        this.accountNameField = accountNameField;
    }

    public void validate(){
        final String newMnemonicValue = mnemonicField.getText().toString();
        final String newAccountNameValue = accountNameField.getText().toString();
        final String mixedValue = newMnemonicValue+"_"+newAccountNameValue;

        this.setLastValue(mixedValue);
        this.startValidating();
        final ValidationField field = this;

        final ValidateImportBitsharesAccountRequest request = new ValidateImportBitsharesAccountRequest(newAccountNameValue,newMnemonicValue,null);
        request.setListener(new CryptoNetInfoRequestListener() {
            @Override
            public void onCarryOut() {
                if(request.getStatus().equals(ValidateImportBitsharesAccountRequest.StatusCode.SUCCEEDED)){
                    setValidForValue(mixedValue, true);
                }else{
                    //TODO handle error request
                    setMessageForValue(mixedValue,validator.getContext().getResources().getString(R.string.error_invalid_account));
                    setValidForValue(mixedValue, false);
                }
            }
        });
        CryptoNetInfoRequests.getInstance().addRequest(request);
    }
}
