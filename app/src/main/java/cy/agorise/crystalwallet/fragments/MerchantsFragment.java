package cy.agorise.crystalwallet.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import cy.agorise.crystalwallet.R;

public class MerchantsFragment extends Fragment {

    public MerchantsFragment() {
        // Required empty public constructor
    }

    public static MerchantsFragment newInstance() {
        MerchantsFragment fragment = new MerchantsFragment();
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
        View view = inflater.inflate(R.layout.fragment_merchants, container, false);
        ButterKnife.bind(this, view);

        return view;
    }
}
