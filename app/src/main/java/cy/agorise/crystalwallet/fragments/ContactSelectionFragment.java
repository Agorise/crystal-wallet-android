package cy.agorise.crystalwallet.fragments;

import android.app.Dialog;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.PagedList;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;
import cy.agorise.crystalwallet.R;
import cy.agorise.crystalwallet.dao.CrystalDatabase;
import cy.agorise.crystalwallet.enums.CryptoNet;
import cy.agorise.crystalwallet.models.Contact;
import cy.agorise.crystalwallet.viewmodels.ContactListViewModel;
import cy.agorise.crystalwallet.views.ContactSelectionListAdapter;

public class ContactSelectionFragment extends DialogFragment implements ContactSelectionListAdapter.ContactSelectionListAdapterListener{

    private CryptoNet cryptoNet;
    private CrystalDatabase db;
    private AlertDialog.Builder builder;

    @BindView(R.id.contactListView)
    RecyclerView contactSelectionListView;

    public static ContactSelectionFragment newInstance(CryptoNet cryptoNet) {
        ContactSelectionFragment f = new ContactSelectionFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putString("CRYPTO_NET", cryptoNet.name());
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
        //AlertDialog.Builder
        builder = new AlertDialog.Builder(getActivity(), R.style.SendTransactionTheme);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.contact_list, null);
        ButterKnife.bind(this, view);

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext());
        this.contactSelectionListView.setLayoutManager(linearLayoutManager);
        //Prevents the list to start again when scrolling to the end
        this.contactSelectionListView.setNestedScrollingEnabled(false);
        final ContactSelectionListAdapter contactSelectionListAdapter = new ContactSelectionListAdapter();
        contactSelectionListAdapter.setListener(this);
        contactSelectionListView.setAdapter(contactSelectionListAdapter);

        this.cryptoNet  = CryptoNet.valueOf(getArguments().getString("CRYPTO_NET"));
        if (this.cryptoNet != null) {
            ContactListViewModel contactListViewModel = ViewModelProviders.of(this).get(ContactListViewModel.class);
            contactListViewModel.init(this.cryptoNet);
            LiveData<PagedList<Contact>> contactsLiveData = contactListViewModel.getContactList();

            contactsLiveData.observe(this, new Observer<PagedList<Contact>>() {
                @Override
                public void onChanged(@Nullable PagedList<Contact> contacts) {
                    contactSelectionListAdapter.submitList(contacts);
                }
            });
        }

        return builder.setView(view).create();
    }

    @Override
    public void onContactSelected(Contact contact) {
        Intent result = new Intent();
        result.putExtra("CONTACT_ID", contact.getId());
        getTargetFragment().onActivityResult(getTargetRequestCode(), 1, result);
        this.dismiss();
    }
}
