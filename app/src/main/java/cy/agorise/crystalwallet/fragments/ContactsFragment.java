package cy.agorise.crystalwallet.fragments;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.PagedList;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cy.agorise.crystalwallet.R;
import cy.agorise.crystalwallet.models.Contact;
import cy.agorise.crystalwallet.viewmodels.ContactListViewModel;
import cy.agorise.crystalwallet.views.ContactListAdapter;

public class ContactsFragment extends Fragment {

    @BindView(R.id.rvContacts)
    RecyclerView rvContacts;

    @BindView(R.id.tvNoContacts)
    TextView tvNoContacts;

    ContactListAdapter adapter;

    FloatingActionButton fabAddContact;

    public ContactsFragment() {
        // Required empty public constructor
    }

    public static ContactsFragment newInstance() {
        ContactsFragment fragment = new ContactsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);
        ButterKnife.bind(this, view);

        // Configure RecyclerView and its adapter
        rvContacts.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ContactListAdapter();
        rvContacts.setAdapter(adapter);

        fabAddContact = getActivity().findViewById(R.id.fabAddContact);

        // Hides the fab when scrolling down
        rvContacts.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                // Scroll down
                if(dy > 0 && fabAddContact.isShown())
                    fabAddContact.hide();

                // Scroll up
                if(dy < 0 && !fabAddContact.isShown())
                    fabAddContact.show();
            }
        });

        // Gets contacts LiveData instance from ContactsViewModel
        ContactListViewModel contactListViewModel =
                ViewModelProviders.of(this).get(ContactListViewModel.class);
        LiveData<PagedList<Contact>> contactsLiveData = contactListViewModel.getContactList();

        contactsLiveData.observe(this, new Observer<PagedList<Contact>>() {
            @Override
            public void onChanged(@Nullable PagedList<Contact> contacts) {
                adapter.submitList(contacts);

                if(contacts != null && contacts.size() > 0){
                    tvNoContacts.setVisibility(View.INVISIBLE);
                }
                else{
                    tvNoContacts.setVisibility(View.VISIBLE);
                }
            }
        });

        return view;
    }
}
