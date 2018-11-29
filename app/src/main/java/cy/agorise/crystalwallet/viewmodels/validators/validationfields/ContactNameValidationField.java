package cy.agorise.crystalwallet.viewmodels.validators.validationfields;

import androidx.lifecycle.ViewModelProviders;
import androidx.fragment.app.FragmentActivity;
import android.widget.EditText;

import cy.agorise.crystalwallet.models.Contact;
import cy.agorise.crystalwallet.viewmodels.ContactListViewModel;

/**
 * Created by Henry Varona on 2/03/2017.
 */

public class ContactNameValidationField extends ValidationField {

    private EditText nameField;
    private Contact contact;

    public ContactNameValidationField(EditText nameField){
        super(nameField);
        this.nameField = nameField;
        this.contact = null;
    }

    public ContactNameValidationField(EditText nameField, Contact contact){
        super(nameField);
        this.nameField = nameField;
        this.contact = contact;
    }

    public void validate(){
        final String newValue = this.nameField.getText().toString();

        if (this.contact != null){
            if (this.contact.getName().equals(newValue)){
                this.setLastValue(newValue);
                this.startValidating();
                this.setValidForValue(newValue, true);
                return;
            }
        }

        if (!newValue.equals("")) {
            if (!newValue.equals(this.getLastValue())) {
                this.setLastValue(newValue);
                this.startValidating();

                ContactListViewModel contactListViewModel = ViewModelProviders.of((FragmentActivity) view.getContext()).get(ContactListViewModel.class);
                if (contactListViewModel.contactExists(newValue)) {
                    this.setMessageForValue(newValue, "This name is already used by another contact.");
                    this.setValidForValue(newValue, false);
                } else {
                    this.setValidForValue(newValue, true);
                }
            }
        } else {
            this.setLastValue("");
            this.startValidating();
            this.setMessageForValue("", "");
            this.setValidForValue("", false);
        }
    }
}
