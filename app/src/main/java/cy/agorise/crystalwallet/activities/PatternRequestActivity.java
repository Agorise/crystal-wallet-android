package cy.agorise.crystalwallet.activities;

import android.app.Activity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.TextView;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import cy.agorise.crystalwallet.R;
import cy.agorise.crystalwallet.application.CrystalSecurityMonitor;
//import cy.agorise.crystalwallet.interfaces.OnResponse;
import cy.agorise.crystalwallet.interfaces.OnResponse;
import cy.agorise.crystalwallet.models.GeneralSetting;
import cy.agorise.crystalwallet.util.PasswordManager;
import cy.agorise.crystalwallet.viewmodels.GeneralSettingListViewModel;

public class PatternRequestActivity extends AppCompatActivity {

    private String patternEncrypted;

    @BindView(R.id.tvPatternText)
    TextView tvPatternText;

    @BindView(R.id.txtBadtry)
    TextView txtBadtry;

    /*
    * External listener for success or fail
    * */
    private static OnResponse onResponse;

    /*
     * Contains the bad tries
     * */
    private int tries = 0;

    /*
     * Seconds counter
     * */
    private int seconds = 15;




    @Override
    public void onBackPressed() {
        //Do nothing to prevent the user to use the back button
    }

    @BindView(R.id.patternLockView)
    PatternLockView patternLockView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pattern_request);
        ButterKnife.bind(this);

        //onResponse = null;

        GeneralSettingListViewModel generalSettingListViewModel = ViewModelProviders.of(this).get(GeneralSettingListViewModel.class);
        LiveData<List<GeneralSetting>> generalSettingsLiveData = generalSettingListViewModel.getGeneralSettingList();

        final PatternRequestActivity thisActivity = this;

        generalSettingsLiveData.observe(this, new Observer<List<GeneralSetting>>() {
            @Override
            public void onChanged(@Nullable List<GeneralSetting> generalSettings) {
                patternEncrypted = "";

                if (generalSettings != null){
                    for (GeneralSetting generalSetting:generalSettings) {
                        if (generalSetting.getName().equals(GeneralSetting.SETTING_PATTERN)){
                            if (!generalSetting.getValue().isEmpty()){
                                patternEncrypted = generalSetting.getValue();

                                patternLockView.addPatternLockListener(new PatternLockViewListener() {
                                    @Override
                                    public void onStarted() {

                                    }

                                    @Override
                                    public void onProgress(List<PatternLockView.Dot> progressPattern) {

                                    }

                                    @Override
                                    public void onComplete(List<PatternLockView.Dot> pattern) {
                                        if (PasswordManager.checkPassword(patternEncrypted,patternToString(pattern))){
                                            if (CrystalSecurityMonitor.getInstance(null).is2ndFactorSet()) {
                                                //CrystalSecurityMonitor.getInstance(null).call2ndFactor(thisActivity);
                                                thisActivity.finish();

                                                /*if(onResponse != null){
                                                    onResponse.onSuccess();
                                                }*/

                                            } else {
                                                thisActivity.finish();

                                                /*if(onResponse != null){
                                                    onResponse.onSuccess();
                                                }*/
                                            }
                                        } else {
                                            incorrect();

                                            /*if(onResponse != null){
                                                onResponse.onFailed();
                                            }*/
                                        }
                                    }

                                    @Override
                                    public void onCleared() {

                                    }
                                });
                            }
                            break;
                        }
                    }
                }
            }
        });
    }

    public static void setOnResponse(OnResponse onResponse) {
        PatternRequestActivity.onResponse = onResponse;
    }

    private void incorrect(){

        /*
         * One more bad try
         * */
        ++tries;

        final Activity activity = this;

        /*
         * User can not go more up to 5 bad tries
         * */
        if(tries==4) {
            tries = 0;

            patternLockView.setEnabled(false);
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
                                                      patternLockView.setEnabled(true);
                                                      txtBadtry.setVisibility(View.INVISIBLE);
                                                      patternLockView.clearPattern();
                                                      patternLockView.requestFocus();
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
         * Show error
         * */
        tvPatternText.setText(activity.getResources().getString(R.string.Incorrect_pattern));
        tvPatternText.setTextColor(Color.RED);
        tvPatternText.setVisibility(View.VISIBLE);
        final Timer t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {

                                  @Override
                                  public void run() {

                                      activity.runOnUiThread(new Runnable() {
                                          @Override
                                          public void run() {

                                              t.cancel();
                                              tvPatternText.setVisibility(View.INVISIBLE);
                                              patternLockView.clearPattern();
                                              patternLockView.requestFocus();

                                          }
                                      });
                                  }

                              },
                //Set how long before to start calling the TimerTask (in milliseconds)
                1000,
                //Set the amount of time between each execution (in milliseconds)
                1000);
    }

    public String patternToString(List<PatternLockView.Dot> pattern){
        String patternString = "";
        for (PatternLockView.Dot nextDot : pattern){
            patternString = patternString+(nextDot.getRow()*3+nextDot.getColumn());
        }

        return patternString;
    }
}


