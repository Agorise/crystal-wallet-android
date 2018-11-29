package cy.agorise.crystalwallet.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cy.agorise.crystalwallet.R;
import cy.agorise.crystalwallet.application.CrystalSecurityMonitor;
import cy.agorise.crystalwallet.dialogs.material.DialogMaterial;
import cy.agorise.crystalwallet.dialogs.material.NegativeResponse;
import cy.agorise.crystalwallet.dialogs.material.PositiveResponse;
import cy.agorise.crystalwallet.dialogs.material.QuestionDialog;

/**
 * Created by xd on 1/18/18.
 */

public class NoneSecurityFragment extends Fragment {

    @BindView(R.id.btnOK)
    Button btnOK;




    public NoneSecurityFragment() {
        // Required empty public constructor
    }

    public static NoneSecurityFragment newInstance() {
        NoneSecurityFragment fragment = new NoneSecurityFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_none_security, container, false);
        ButterKnife.bind(this, v);



        return v;
    }

    @OnClick(R.id.btnOK)
    public void btnOKClic(){

        /*
        * Question if user is sure to remove the security
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
                CrystalSecurityMonitor.getInstance(null).clearSecurity();

                Toast.makeText(getActivity().getBaseContext(),getActivity().getString(R.string.Security_mode_changed_to_none),
                        Toast.LENGTH_SHORT).show();
            }
        });
        questionDialog.show();
    }

    /*@Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            CrystalSecurityMonitor.getInstance(null).clearSecurity();
        }
    }*/
}
