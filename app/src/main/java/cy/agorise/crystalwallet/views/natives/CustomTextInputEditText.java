package cy.agorise.crystalwallet.views.natives;

import android.content.Context;
import com.google.android.material.textfield.TextInputEditText;
import android.util.AttributeSet;
import android.view.View;

import cy.agorise.crystalwallet.models.FieldValidatorModel;
import cy.agorise.crystalwallet.viewmodels.validators.customImpl.interfaces.UIValidator;

/*
* Custom implementation of the native control to get more over control it
* */
public class CustomTextInputEditText extends TextInputEditText implements UIValidator {

    /*
    * Contains the field validator, this aid to validate the field
    * */
    private FieldValidatorModel fieldValidatorModel = new FieldValidatorModel();

    /*
    * Interface to validate the field
    * */
    private UIValidator uiValidator;

    /*
    * Contains the last input value
    * */
    private String lastValue;


    public CustomTextInputEditText(Context context, AttributeSet attrs) {
        super(context, attrs);

        /*
        * Set listener to get the last value of the control
        * */
        this.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if(!hasFocus){
                    lastValue = getText().toString();
                }
            }
        });
    }

    public void setFieldValidatorModel(FieldValidatorModel fieldValidatorModel) {
        this.fieldValidatorModel = fieldValidatorModel;
    }
    public void setUiValidator(UIValidator uiValidator) {
        this.uiValidator = uiValidator;
    }
    public FieldValidatorModel getFieldValidatorModel() {
        return fieldValidatorModel;
    }

    public String getLastValue() {
        return lastValue;
    }
    /*
     * End of setters and getters
     * */

    @Override
    public void validate() {
        uiValidator.validate();
    }
}
