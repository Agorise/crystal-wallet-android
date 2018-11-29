package cy.agorise.crystalwallet.activities;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.annotation.Nullable;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import cy.agorise.crystalwallet.R;
import cy.agorise.crystalwallet.models.Contact;
import cy.agorise.crystalwallet.models.ContactAddress;
import cy.agorise.crystalwallet.viewmodels.ContactViewModel;
import cy.agorise.crystalwallet.viewmodels.validators.CreateContactValidator;
import cy.agorise.crystalwallet.viewmodels.validators.ModifyContactValidator;
import cy.agorise.crystalwallet.viewmodels.validators.UIValidatorListener;
import cy.agorise.crystalwallet.viewmodels.validators.validationfields.ValidationField;
import cy.agorise.crystalwallet.views.ContactAddressListAdapter;

public class CreateContactActivity extends AppCompatActivity implements UIValidatorListener {

    @BindView(R.id.tilName)
    TextInputLayout tilName;
    @BindView(R.id.tietName)
    TextInputEditText tietName;
    @BindView(R.id.btnCancel)
    Button btnCancel;
    @BindView(R.id.btnCreate)
    Button btnCreate;
    @BindView(R.id.rvContactAddresses)
    RecyclerView rvContactAddresses;
    @BindView(R.id.btnAddAddress)
    Button btnAddAddress;
    List<ContactAddress> contactAddressList;
    ContactAddressListAdapter listAdapter;

    CreateContactValidator createContactValidator;
    ModifyContactValidator modifyContactValidator;

    Contact contact;

    long contactId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_contact);
        ButterKnife.bind(this);

        btnCreate.setEnabled(false);

        listAdapter = new ContactAddressListAdapter();
        rvContactAddresses.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rvContactAddresses.setAdapter(listAdapter);

        contactId = getIntent().getLongExtra("CONTACT_ID",-1);

        if (contactId >= 0){
            final ContactViewModel contactViewModel = ViewModelProviders.of(this).get(ContactViewModel.class);

            contactViewModel.init(contactId);
            LiveData<Contact> contactLiveData = contactViewModel.getContact();
            final CreateContactActivity thisActivity = this;

            contactLiveData.observe(this, new Observer<Contact>() {
                @Override
                public void onChanged(@Nullable Contact contactChanged) {
                    if (contactChanged != null){
                        contact = contactChanged;
                        tietName.setText(contact.getName());

                        LiveData<List<ContactAddress>> contactAddresses = contactViewModel.getContactAddresses();

                        contactAddresses.observe(thisActivity, new Observer<List<ContactAddress>>() {
                            @Override
                            public void onChanged(@Nullable List<ContactAddress> contactAddresses) {
                                contactAddressList = contactAddresses;
                                listAdapter.submitList(contactAddressList);
                                listAdapter.notifyDataSetChanged();
                            }
                        });

                        modifyContactValidator = new ModifyContactValidator(
                                thisActivity.getApplicationContext(), contact, tietName);
                        modifyContactValidator.setListener(thisActivity);
                        btnCreate.setText(R.string.modify);
                    } else {
                        //No contact was found, this will exit the activity
                        finish();
                    }
                }
            });
        } else {
            contactAddressList = new ArrayList<>();
            listAdapter.submitList(contactAddressList);
            createContactValidator = new CreateContactValidator(this.getApplicationContext(),tietName);
            createContactValidator.setListener(this);

            btnCreate.setVisibility(View.VISIBLE);
        }
    }

    public void validate(){
        if (this.createContactValidator != null){
            this.createContactValidator.validate();
        } else if (this.modifyContactValidator != null){
            this.modifyContactValidator.validate();
        }
    }

    public boolean isValid(){
        if (this.createContactValidator != null){
            return this.createContactValidator.isValid();
        } else if (this.modifyContactValidator != null){
            return this.modifyContactValidator.isValid();
        }

        return false;
    }

    @OnTextChanged(value = R.id.tietName,
            callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void afterContactNameChanged() {
        this.validate();
    }

    @OnClick(R.id.btnAddAddress)
    public void addAddress(){
        ContactAddress newContactAddress = new ContactAddress();
        this.contactAddressList.add(newContactAddress);
        this.listAdapter.notifyDataSetChanged();
    }

    @OnClick(R.id.btnCancel)
    public void cancel(){
        this.finish();
    }

    @OnClick(R.id.btnCreate)
    public void createOrModifyContact(){
        if(contactId >= 0) {
            // Modifying existing contact

            if (this.modifyContactValidator.isValid()) {
                this.contact.setName(tietName.getText().toString());
                this.contact.clearAddresses();

                for (ContactAddress contactAddress : contactAddressList){
                    this.contact.addAddress(contactAddress);
                }

                ContactViewModel contactViewModel = ViewModelProviders.of(this).get(ContactViewModel.class);
                if (contactViewModel.modifyContact(this.contact)){
                    this.finish();
                } else {
                    this.modifyContactValidator.validate();
                }
            }
        } else {
            // Creating a new contact

            if (this.createContactValidator.isValid()) {
                Contact newContact = new Contact();
                newContact.setName(tietName.getText().toString());

                for (ContactAddress contactAddress : contactAddressList){
                    newContact.addAddress(contactAddress);
                }

                ContactViewModel contactViewModel = ViewModelProviders.of(this).get(ContactViewModel.class);
                if (contactViewModel.addContact(newContact)){
                    this.finish();
                } else {
                    createContactValidator.validate();
                }
            }
        }
    }

    @Override
    public void onValidationSucceeded(final ValidationField field) {
        final CreateContactActivity activity = this;

        activity.runOnUiThread(new Runnable() {
            public void run() {

                if (field.getView() == tietName) {
                    tilName.setError("");
                }

                if (activity.isValid()){
                    btnCreate.setEnabled(true);
                } else {
                    btnCreate.setEnabled(false);
                }
            }
        });
    }

    @Override
    public void onValidationFailed(final ValidationField field) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (field.getView() == tietName) {
                    tilName.setError(field.getMessage());
                }
            }
        });
    }
}
