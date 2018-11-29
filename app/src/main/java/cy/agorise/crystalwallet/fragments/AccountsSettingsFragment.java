package cy.agorise.crystalwallet.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.thekhaeng.pushdownanim.PushDownAnim;

import butterknife.BindView;
import butterknife.ButterKnife;
import cy.agorise.crystalwallet.R;

/**
 * Created by xd on 1/16/18.
 */

public class AccountsSettingsFragment extends Fragment {
    public AccountsSettingsFragment() {
        // Required empty public constructor
    }

    public static AccountsSettingsFragment newInstance() {
        AccountsSettingsFragment fragment = new AccountsSettingsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @BindView(R.id.tvUpgrade)
    public TextView tvUpgrade;

    @BindView(R.id.tvImport)
    public  TextView tvImport;

    @BindView(R.id.tvRefresh)
    public TextView tvRefresh;

    @BindView(R.id.tvRemove)
    public  TextView tvRemove;

    @BindView(R.id.btnUpgrade)
    public Button btnUpgrade;

    @BindView(R.id.btnImport)
    public Button btnImport;

    @BindView(R.id.btnRefresh)
    public Button btnRefresh;

    @BindView(R.id.btnRemove)
    public Button btnRemove;





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_accounts_settings, container, false);
        ButterKnife.bind(this, v);

        tvUpgrade.setText(makeFirstWordsBold(getResources().getString(R.string.upgrade_description)));
        tvImport.setText(makeFirstWordsBold(getResources().getString(R.string.import_description)));
        tvRefresh.setText(makeFirstWordsBold(getResources().getString(R.string.refresh_description)));
        tvRemove.setText(makeFirstWordsBold(getResources().getString(R.string.remove_description)));

        /*
         *   Integration of library with button efects
         * */
        PushDownAnim.setPushDownAnimTo(btnUpgrade)
                .setOnClickListener( new View.OnClickListener(){
                    @Override
                    public void onClick( View view ){
                    }
                } );
        PushDownAnim.setPushDownAnimTo(btnImport)
                .setOnClickListener( new View.OnClickListener(){
                    @Override
                    public void onClick( View view ){
                    }
                } );
        PushDownAnim.setPushDownAnimTo(btnRefresh)
                .setOnClickListener( new View.OnClickListener(){
                    @Override
                    public void onClick( View view ){

                    }
                } );
        PushDownAnim.setPushDownAnimTo(btnRemove)
                .setOnClickListener( new View.OnClickListener(){
                    @Override
                    public void onClick( View view ){
                    }
                } );


        return v;
    }

    private SpannableStringBuilder makeFirstWordsBold(String str) {
        SpannableStringBuilder ssb = new SpannableStringBuilder(str);
        ssb.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD),
                0, str.indexOf('.')+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return ssb;
    }
}
