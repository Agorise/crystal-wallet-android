package cy.agorise.crystalwallet.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;
import cy.agorise.crystalwallet.R;

/**
 * Created by henry varona on 4/17/18.
 */

public class ProfileSettingsFragment extends Fragment {

    @BindView(R.id.etProfileEmail)
    EditText etProfileEmail;

    public ProfileSettingsFragment() {
        // Required empty public constructor
    }

    public static ProfileSettingsFragment newInstance() {
        ProfileSettingsFragment fragment = new ProfileSettingsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profile_settings, container, false);
        ButterKnife.bind(this, v);

        return v;
    }



    @OnTextChanged(value = R.id.etProfileEmail,
            callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void afterEmailChanged(Editable editable) {

    }
}
