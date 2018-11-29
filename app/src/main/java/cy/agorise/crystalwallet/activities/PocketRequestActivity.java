package cy.agorise.crystalwallet.activities;

import android.app.PendingIntent;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.NdefFormatable;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.nio.ByteBuffer;

import butterknife.ButterKnife;
import cy.agorise.crystalwallet.R;
import cy.agorise.crystalwallet.application.CrystalSecurityMonitor;
import cy.agorise.crystalwallet.util.PasswordManager;
import cy.agorise.crystalwallet.util.yubikey.YkOathApi;

public class PocketRequestActivity extends AppCompatActivity {

    private NfcAdapter mNfcAdapter;
    private PendingIntent pendingIntent;
    private IntentFilter ndef;
    IntentFilter[] intentFiltersArray;
    String[][] techList;

    @Override
    public void onBackPressed() {
        //Do nothing to prevent the user to use the back button
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pocket_request);
        ButterKnife.bind(this);

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        this.configureForegroundDispatch();
    }

    public void configureForegroundDispatch(){
        if (mNfcAdapter != null) {
            pendingIntent = PendingIntent.getActivity(
                    this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

            ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
            try {
                ndef.addDataType("*/*");    /* Handles all MIME based dispatches.
                                       You should specify only the ones that you need. */
            } catch (IntentFilter.MalformedMimeTypeException e) {
                throw new RuntimeException("fail", e);
            }
            intentFiltersArray = new IntentFilter[]{ndef,};
            techList = new String[][]{ new String[] {IsoDep.class.getName(), NfcA.class.getName(), MifareClassic.class.getName(), NdefFormatable.class.getName()} };

        } else {
            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
        }
    }

    public void onPause() {
        super.onPause();
        if (mNfcAdapter != null) {
            mNfcAdapter.disableForegroundDispatch(this);
        }
    }

    public void onResume() {
        super.onResume();
        if (mNfcAdapter != null) {
            mNfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFiltersArray, techList);
        }
    }

    public void onNewIntent(Intent intent) {
        Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        IsoDep tagIsoDep = IsoDep.get(tagFromIntent);
        Log.i("Tag from nfc","New Intent");
        String yubikeySecret = CrystalSecurityMonitor.getInstance(null).get2ndFactorValue();

        try {
            tagIsoDep.connect();
            YkOathApi ykOathApi = new YkOathApi(tagIsoDep);

            long unixTime = System.currentTimeMillis() / 1000L;
            byte[] timeStep = ByteBuffer.allocate(8).putLong(unixTime / 30L).array();
            byte[] response;
            response = ykOathApi.calculate("cy.agorise.crystalwallet",timeStep,true);
            ByteBuffer responseBB = ByteBuffer.wrap(response);
            int digits = (int)responseBB.get();
            String challengeString = ""+(responseBB.getInt());
            String challenge = challengeString.substring(challengeString.length()-digits);
            while (challenge.length() < digits){
                challenge = '0'+challenge;
            }

            String storedChallenge = PasswordManager.totpd(yubikeySecret, unixTime, ykOathApi.getDeviceSalt());

            Toast.makeText(this, "Secret:"+yubikeySecret+" StoredChallenge:"+storedChallenge+" Yubikey:"+challenge , Toast.LENGTH_LONG).show();

            Log.i("TOTP","Secret: "+yubikeySecret);
            Log.i("TOTP", "Unixtime: "+unixTime);
            Log.i("TOTP", "Step: "+unixTime/30L);
            Log.i("TOTP", "StoredChallenge: "+storedChallenge);
            Log.i("TOTP", "Yubikey: "+challenge);

            tagIsoDep.close();
            //ykOathApi.
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}


