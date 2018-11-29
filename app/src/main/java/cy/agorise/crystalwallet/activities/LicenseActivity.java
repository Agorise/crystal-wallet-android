package cy.agorise.crystalwallet.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

import com.thekhaeng.pushdownanim.PushDownAnim;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cy.agorise.crystalwallet.R;
import cy.agorise.crystalwallet.dao.CrystalDatabase;
import cy.agorise.crystalwallet.models.GeneralSetting;

public class LicenseActivity extends AppCompatActivity {

    @BindView(R.id.wvEULA) WebView wvEULA;

    @BindView(R.id.btnDisAgree)
    Button btnDisAgree;

    @BindView(R.id.btnAgree)
    Button btnAgree;

    CrystalDatabase db;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_license);

        ButterKnife.bind(this);

        /*
         *   Integration of library with button efects
         * */
        PushDownAnim.setPushDownAnimTo(btnDisAgree)
                .setOnClickListener( new View.OnClickListener(){
                    @Override
                    public void onClick( View view ){
                        onDisagree();
                    }
                } );
        PushDownAnim.setPushDownAnimTo(btnAgree)
                .setOnClickListener( new View.OnClickListener(){
                    @Override
                    public void onClick( View view ){
                        onAgree();
                    }
                } );

        db = CrystalDatabase.getAppDatabase(this.getApplicationContext());
        int licenseVersion = getResources().getInteger(R.integer.license_version);
        GeneralSetting generalSettingLastLicenseRead = db.generalSettingDao().getSettingByName(GeneralSetting.SETTING_LAST_LICENSE_READ);

        if ((generalSettingLastLicenseRead != null) && (Integer.parseInt(generalSettingLastLicenseRead.getValue()) >= licenseVersion)) {
            Intent intent = new Intent(this, IntroActivity.class);
            startActivity(intent);
            finish();
        }
        else{

            /*
            * Load the licence only if it is necesarry
            * */
            String html = getString(R.string.licence_html);
            wvEULA.loadData(html, "text/html", "UTF-8");
        }
    }

    @OnClick(R.id.btnAgree)
    public void onAgree() {
        CrystalDatabase db = CrystalDatabase.getAppDatabase(this.getApplicationContext());
        GeneralSetting lastLicenseReadSetting = new GeneralSetting();
        lastLicenseReadSetting.setName(GeneralSetting.SETTING_LAST_LICENSE_READ);
        lastLicenseReadSetting.setValue(""+getResources().getInteger(R.integer.license_version));

        db.generalSettingDao().deleteByName(GeneralSetting.SETTING_LAST_LICENSE_READ);
        db.generalSettingDao().insertGeneralSetting(lastLicenseReadSetting);

        Intent intent = new Intent(this, IntroActivity.class);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.btnDisAgree)
    public void onDisagree() {
        finish();
    }
}
