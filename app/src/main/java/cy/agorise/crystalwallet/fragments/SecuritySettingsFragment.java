package cy.agorise.crystalwallet.fragments;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import org.apache.commons.codec.binary.Base32;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import cy.agorise.crystalwallet.R;
import cy.agorise.crystalwallet.application.CrystalSecurityMonitor;
import cy.agorise.crystalwallet.models.GeneralSetting;
import cy.agorise.crystalwallet.util.ChildViewPager;
import cy.agorise.crystalwallet.util.yubikey.Algorithm;
import cy.agorise.crystalwallet.util.yubikey.OathType;
import cy.agorise.crystalwallet.util.yubikey.YkOathApi;

/**
 * Created by xd on 1/17/18.
 * In this fragment the user should be able to select its preferred security option.
 */

public class SecuritySettingsFragment extends Fragment {

    public SecuritySettingsFragment() {
        // Required empty public constructor
    }

    public static SecuritySettingsFragment newInstance() {
        SecuritySettingsFragment fragment = new SecuritySettingsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    /*
    * Functionality not needed for now
    * */
    //private NfcAdapter mNfcAdapter;
    private PendingIntent pendingIntent;
    private IntentFilter ndef;
    IntentFilter[] intentFiltersArray;
    String[][] techList;


    @BindView(R.id.pager)
    public ChildViewPager mPager;

    public SecurityPagerAdapter securityPagerAdapter;

    @BindView(R.id.sPocketSecurity)
    Switch sPocketSecurity;
    @BindView(R.id.etPocketPassword)
    EditText etPocketPassword;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_security_settings, container, false);
        ButterKnife.bind(this, v);

        /*
        * For now this will not be implemented
        * */
        sPocketSecurity.setEnabled(false);

        securityPagerAdapter = new SecurityPagerAdapter(getChildFragmentManager());
        mPager.setAdapter(securityPagerAdapter);

        switch(CrystalSecurityMonitor.getInstance(getActivity()).actualSecurity()) {
            case GeneralSetting.SETTING_PASSWORD:
                mPager.setCurrentItem(1);
                break;
            case GeneralSetting.SETTING_PATTERN:
                mPager.setCurrentItem(2);
                break;
            default:
                mPager.setCurrentItem(0);
        }

        TabLayout tabLayout = v.findViewById(R.id.tabs);

        mPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mPager));

        /*
         * Functionality not needed for now
         * */
        //mNfcAdapter = NfcAdapter.getDefaultAdapter(this.getActivity());

        this.configureForegroundDispatch();

        return v;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            //CrystalSecurityMonitor.getInstance(null).callPasswordRequest(this.getActivity());
        }
    }

    private class SecurityPagerAdapter extends FragmentPagerAdapter {
        SecurityPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return new NoneSecurityFragment();
                case 1:
                    return new PinSecurityFragment();
                case 2:
                    final PatternSecurityFragment patternSecurityFragment = new PatternSecurityFragment();
                    patternSecurityFragment.setChildViewPager(mPager);
                    return patternSecurityFragment;
            }

            return null; //new OnConstructionFragment();
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

    @OnCheckedChanged(R.id.sPocketSecurity)
    public void onPocketSecurityActivated(CompoundButton button, boolean checked){
        if (checked) {
            enableNfc();
        } else {
            disableNfc();
        }
    }

    public void configureForegroundDispatch(){

        /*
         * Functionality not needed for now
         * */
        //if (mNfcAdapter != null) {
            //pendingIntent = PendingIntent.getActivity(
              //      this.getActivity(), 0, new Intent(this.getActivity(), getActivity().getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

            //ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
            //try {
                //ndef.addDataType("*/*");
                // Handles all MIME based dispatches.
                //You should specify only the ones that you need.
            //} catch (IntentFilter.MalformedMimeTypeException e) {
                //throw new RuntimeException("fail", e);
            //}
            //intentFiltersArray = new IntentFilter[]{ndef,};
            //techList = new String[][]{ new String[] {IsoDep.class.getName(), NfcA.class.getName(), MifareClassic.class.getName(), NdefFormatable.class.getName()} };

        //} else {
            //Toast.makeText(this.getContext(), "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
        //}
    }

    public void enableNfc(){
        //mNfcAdapter.enableForegroundDispatch(this.getActivity(), pendingIntent, intentFiltersArray, techList);
        Toast.makeText(this.getContext(), "Tap with your yubikey", Toast.LENGTH_LONG).show();
    }

    public void disableNfc(){
        //mNfcAdapter.disableForegroundDispatch(this.getActivity());
    }

    public void onPause() {
        super.onPause();
        disableNfc();
    }

    public void onResume() {
        super.onResume();
        /*if (mNfcAdapter != null) {
            enableNfc();
        }*/
    }

    public void onNewIntent(Intent intent) {
        Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        IsoDep tagIsoDep = IsoDep.get(tagFromIntent);
        Log.i("Tag from nfc","New Intent");
        try {

            String serviceName = "cy.agorise.crystalwallet";
            String encodedSecret = etPocketPassword.getText().toString();
            Base32 decoder = new Base32();

            if ((encodedSecret != null) && (!encodedSecret.equals("")) && decoder.isInAlphabet(encodedSecret)) {
                byte[] secret = decoder.decode(encodedSecret);
                tagIsoDep.connect();
                tagIsoDep.setTimeout(15000);
                YkOathApi ykOathApi = new YkOathApi(tagIsoDep);

                try {
                    ykOathApi.putCode(serviceName, secret, OathType.TOTP, Algorithm.SHA256, (byte) 6, 0, false);
                    CrystalSecurityMonitor.getInstance(null).setYubikeyOathTotpSecurity(CrystalSecurityMonitor.getServiceName(),encodedSecret);
                } catch(IOException e) {
                    Toast.makeText(this.getContext(), "There's no space for new credentials!", Toast.LENGTH_LONG).show();
                }

                tagIsoDep.close();
                Toast.makeText(this.getContext(), "Credential saved!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this.getContext(), "Invalid password for credential", Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
