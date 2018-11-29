package cy.agorise.crystalwallet.activities;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.thekhaeng.pushdownanim.PushDownAnim;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cy.agorise.crystalwallet.R;
import cy.agorise.crystalwallet.models.AccountSeed;
import cy.agorise.crystalwallet.viewmodels.AccountSeedViewModel;

//tvBrainKey
public class BackupSeedActivity extends AppCompatActivity {

    AccountSeedViewModel accountSeedViewModel;

    @BindView(R.id.tvBrainKey)
    TextView textfieldBrainkey;
    @BindView(R.id.btnOK)
    Button btnOk;
    @BindView(R.id.btnCopy)
    Button btnCopy;
    @BindView(R.id.btnCancel)
    Button btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.backup_seed);

        ButterKnife.bind(this);

        /*
         *   Integration of library with button efects
         * */
        PushDownAnim.setPushDownAnimTo(btnCancel)
                .setOnClickListener( new View.OnClickListener(){
                    @Override
                    public void onClick( View view ){
                        btnCancelClick();
                    }
                } );
        PushDownAnim.setPushDownAnimTo(btnOk)
                .setOnClickListener( new View.OnClickListener(){
                    @Override
                    public void onClick( View view ){
                        btnOkClick();
                    }
                } );
        PushDownAnim.setPushDownAnimTo(btnCopy)
                .setOnClickListener( new View.OnClickListener(){
                    @Override
                    public void onClick( View view ){
                        btnCopyClick();
                    }
                } );
        /*
        *   If comes from new account creation hide the cancel button
        * */
        Bundle b = getIntent().getExtras();
        boolean newAccount = b.getBoolean("newAccount");
        if(newAccount ){
            ViewGroup layout = (ViewGroup) btnOk.getParent();
            if(null!=layout) //for safety only  as you are doing onClick
                layout.removeView(btnOk);
            //btnOk.setVisibility(View.INVISIBLE);
        }

        long seedId = getIntent().getLongExtra("SEED_ID",-1);

        if (seedId > -1) {
            accountSeedViewModel = ViewModelProviders.of(this).get(AccountSeedViewModel.class);
            accountSeedViewModel.loadSeed(seedId);
            LiveData<AccountSeed> liveDataAccountSeed = accountSeedViewModel.getAccountSeed();
            liveDataAccountSeed.observe(this, new Observer<AccountSeed>() {
                @Override
                public void onChanged(@Nullable AccountSeed accountSeed) {
                    textfieldBrainkey.setText(accountSeed.getMasterSeed());
                }
            });
            accountSeedViewModel.loadSeed(seedId);

        } else {

            /*
            *
            * The first time you create the account, the "seed" is showed propertly in this window,
            * but when you want to check it again the "seed" does not exist anymore and
            * for this cause the program gets into this point and finish the window
            *
            * */
            finish();
        }
    }

    @OnClick(R.id.btnCancel)
    public void btnCancelClick(){
        Intent intent = new Intent(this, BoardActivity.class);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.btnOK)
    public void btnOkClick(){
        Intent intent = new Intent(this, BoardActivity.class);
        startActivity(intent);
        finish();
    }

    /*
     *   Clic on button copy to clipboard
     * */
    @OnClick(R.id.btnCopy)
    public void btnCopyClick(){

        /*
         *  Save to clipboard the brainkey chain
         * */
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(textfieldBrainkey.getText(), textfieldBrainkey.getText().toString());
        clipboard.setPrimaryClip(clip);

        /*
         * Success message
         * */
        Toast.makeText(this.getBaseContext(),getResources().getString(R.string.window_seed_toast_clipboard), Toast.LENGTH_SHORT).show();
    }
}
