package cy.agorise.crystalwallet.activities;

import android.app.Activity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import cy.agorise.crystalwallet.R;
import cy.agorise.crystalwallet.application.CrystalSecurityMonitor;
import cy.agorise.crystalwallet.interfaces.OnResponse;
import cy.agorise.crystalwallet.models.GeneralSetting;
import cy.agorise.crystalwallet.util.PasswordManager;
import cy.agorise.crystalwallet.viewmodels.GeneralSettingListViewModel;

public class PinRequestActivity extends AppCompatActivity {
    private String passwordEncrypted;

    @BindView(R.id.btnOK)
    Button btnOK;

    @BindView(R.id.txtBadtry)
    TextView txtBadtry;

    /*
     * Contains the bad tries
     * */
    private int tries = 0;

    /*
     * Seconds counter
     * */
    private int seconds = 15;

    /*
     * External listener for success or fail
     * */
    private static OnResponse onResponse;



    @Override
    public void onBackPressed() {
        //Do nothing to prevent the user to use the back button
    }

    @BindView(R.id.etPassword)
    EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_request);
        ButterKnife.bind(this);

        //onResponse = null;

        /*
         * Initially the button is disabled till the user type a valid PIN
         * */
        btnOK.setEnabled(false);

        GeneralSettingListViewModel generalSettingListViewModel = ViewModelProviders.of(this).get(GeneralSettingListViewModel.class);
        LiveData<List<GeneralSetting>> generalSettingsLiveData = generalSettingListViewModel.getGeneralSettingList();
        generalSettingsLiveData.observe(this, new Observer<List<GeneralSetting>>() {
            @Override
            public void onChanged(@Nullable List<GeneralSetting> generalSettings) {
                passwordEncrypted = "";

                if (generalSettings != null){
                    for (GeneralSetting generalSetting:generalSettings) {
                        if (generalSetting.getName().equals(GeneralSetting.SETTING_PASSWORD)){
                            if (!generalSetting.getValue().isEmpty()){
                                passwordEncrypted = generalSetting.getValue();
                            }
                            break;
                        }
                    }
                }
            }
        });
    }


    @OnClick(R.id.btnOK)
    void okClic(final View view) {

        if (PasswordManager.checkPassword(passwordEncrypted, etPassword.getText().toString())) {
            if (CrystalSecurityMonitor.getInstance(null).is2ndFactorSet()) {
                CrystalSecurityMonitor.getInstance(null).call2ndFactor(this);

                if(onResponse != null){
                    onResponse.onSuccess();
                }

            } else {
                this.finish();

                if(onResponse != null){
                    onResponse.onFailed();
                }
            }
        }
        else{

            /*
             * One more bad try
             * */
            ++tries;

            final Activity activity = this;

            etPassword.setTextColor(Color.RED);

            /*
             * User can not go more up to 5 bad tries
             * */
            if(tries==4){
                tries = 0;

                btnOK.setEnabled(false);
                etPassword.setEnabled(false);
                txtBadtry.setVisibility(View.VISIBLE);

                txtBadtry.setText(txtBadtry.getText().toString().replace("%%",String.valueOf(seconds)));

                final Timer t = new Timer();
                //Set the schedule function and rate
                t.scheduleAtFixedRate(new TimerTask() {

                                          @Override
                                          public void run() {

                                              --seconds;

                                              if(seconds==0){
                                                  t.cancel();

                                                  seconds = 15;

                                                  activity.runOnUiThread(new Runnable() {
                                                      @Override
                                                      public void run() {
                                                          btnOK.setEnabled(true);
                                                          etPassword.setEnabled(true);
                                                          txtBadtry.setVisibility(View.INVISIBLE);
                                                          etPassword.setText("");
                                                          etPassword.setTextColor(Color.WHITE);
                                                      }
                                                  });
                                              }
                                              else{
                                                  activity.runOnUiThread(new Runnable() {
                                                      @Override
                                                      public void run() {
                                                          txtBadtry.setText(activity.getResources().getString(R.string.wrong_pin_wait).replace("%%",String.valueOf(seconds)));
                                                      }
                                                  });
                                              }
                                          }

                                      },
                        //Set how long before to start calling the TimerTask (in milliseconds)
                        1000,
                        //Set the amount of time between each execution (in milliseconds)
                        1000);

                return;
            }

            /*
             *   Set in red the rext and reset the password after a period of time
             * */
            final Timer t = new Timer();
            //Set the schedule function and rate
            t.scheduleAtFixedRate(new TimerTask() {

                                      @Override
                                      public void run() {
                                          //Called each time when 1000 milliseconds (1 second) (the period parameter)
                                          activity.runOnUiThread(new Runnable() {
                                              @Override
                                              public void run() {
                                                  etPassword.setText("");
                                                  etPassword.setTextColor(Color.WHITE);
                                                  t.cancel();
                                              }
                                          });
                                      }

                                  },
                    //Set how long before to start calling the TimerTask (in milliseconds)
                    500,
                    //Set the amount of time between each execution (in milliseconds)
                    500);
        }
    }


    public static void setOnResponse(OnResponse onResponse) {
        PinRequestActivity.onResponse = onResponse;
    }


    @OnTextChanged(value = R.id.etPassword,
            callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void afterPasswordChanged(Editable editable) {

        /*
         * If it is valid length enable button
         * */
        if(etPassword.getText().length()>=6){
            btnOK.setEnabled(true);
        }
        else{
            btnOK.setEnabled(false);
        }
    }
}