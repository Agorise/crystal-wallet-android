package cy.agorise.crystalwallet.application;

import android.app.Activity;

import android.app.Application;
import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;

import cy.agorise.crystalwallet.activities.PatternRequestActivity;
import cy.agorise.crystalwallet.activities.PinRequestActivity;
import cy.agorise.crystalwallet.activities.PocketRequestActivity;
import cy.agorise.crystalwallet.interfaces.OnResponse;
import cy.agorise.crystalwallet.models.GeneralSetting;
import cy.agorise.crystalwallet.viewmodels.GeneralSettingListViewModel;

/**
 * Created by Henry Varona on 27/1/2018.
 */

public class CrystalSecurityMonitor implements Application.ActivityLifecycleCallbacks {
    private int numStarted = 0;
    private String passwordEncrypted;
    private String patternEncrypted;
    private String yubikeyOathTotpPasswordEncrypted;

    private static CrystalSecurityMonitor instance;
    private GeneralSettingListViewModel generalSettingListViewModel;

    private CrystalSecurityMonitor(final FragmentActivity fragmentActivity){
        //For security reasons, this code is not asynchronous because the pattern or password data
        //must be retrieved before the principal activity starts
        generalSettingListViewModel = ViewModelProviders.of(fragmentActivity).get(GeneralSettingListViewModel.class);

        this.passwordEncrypted = "";
        this.patternEncrypted = "";
        this.yubikeyOathTotpPasswordEncrypted = "";
        GeneralSetting passwordGeneralSetting = generalSettingListViewModel.getGeneralSettingByName(GeneralSetting.SETTING_PASSWORD);;
        GeneralSetting patternGeneralSetting = generalSettingListViewModel.getGeneralSettingByName(GeneralSetting.SETTING_PATTERN);;
        GeneralSetting yubikeyOathTotpPasswordSetting = generalSettingListViewModel.getGeneralSettingByName(GeneralSetting.SETTING_YUBIKEY_OATH_TOTP_PASSWORD);;

        if (passwordGeneralSetting != null){
            this.passwordEncrypted = passwordGeneralSetting.getValue();
        }
        if (patternGeneralSetting != null){
            this.patternEncrypted = patternGeneralSetting.getValue();
        }
        if (yubikeyOathTotpPasswordSetting != null){
            this.yubikeyOathTotpPasswordEncrypted = yubikeyOathTotpPasswordSetting.getValue();
        }
    }

    public static CrystalSecurityMonitor getInstance(final FragmentActivity fragmentActivity){
        if (instance == null){
            instance = new CrystalSecurityMonitor(fragmentActivity);
        }

        return instance;
    }

    public static String getServiceName(){
        return "cy.agorise.crystalwallet";
    }

    public void clearSecurity(){
        this.patternEncrypted = "";
        this.passwordEncrypted = "";

        generalSettingListViewModel.deleteGeneralSettingByName(GeneralSetting.SETTING_PASSWORD);
        generalSettingListViewModel.deleteGeneralSettingByName(GeneralSetting.SETTING_PATTERN);
    }

    public void clearSecurity2ndFactor(){
        this.yubikeyOathTotpPasswordEncrypted = "";

        generalSettingListViewModel.deleteGeneralSettingByName(GeneralSetting.SETTING_YUBIKEY_OATH_TOTP_PASSWORD);
    }

    public void setPasswordSecurity(String password){
        clearSecurity();
        this.passwordEncrypted = password;
        GeneralSetting passwordGeneralSetting = new GeneralSetting();
        passwordGeneralSetting.setName(GeneralSetting.SETTING_PASSWORD);
        passwordGeneralSetting.setValue(password);

        generalSettingListViewModel.saveGeneralSetting(passwordGeneralSetting);
    }

    public void setPatternEncrypted(String pattern){
        clearSecurity();
        this.patternEncrypted = pattern;
        GeneralSetting patternGeneralSetting = new GeneralSetting();
        patternGeneralSetting.setName(GeneralSetting.SETTING_PATTERN);
        patternGeneralSetting.setValue(pattern);

        generalSettingListViewModel.saveGeneralSetting(patternGeneralSetting);
    }

    public String actualSecurity(){
        if ((this.patternEncrypted != null) && (!this.patternEncrypted.equals(""))){
            return GeneralSetting.SETTING_PATTERN;
        } else if ((this.passwordEncrypted != null) && (!this.passwordEncrypted.equals(""))){
            return GeneralSetting.SETTING_PASSWORD;
        }

        return "";
    }

    public boolean is2ndFactorSet(){
        return !this.yubikeyOathTotpPasswordEncrypted.equals("");
    }

    public void setYubikeyOathTotpSecurity(String name, String password){
        this.yubikeyOathTotpPasswordEncrypted = password;
        GeneralSetting yubikeyOathTotpSetting = new GeneralSetting();
        yubikeyOathTotpSetting.setName(GeneralSetting.SETTING_YUBIKEY_OATH_TOTP_PASSWORD);
        yubikeyOathTotpSetting.setValue(password);

        generalSettingListViewModel.saveGeneralSetting(yubikeyOathTotpSetting);
    }

    @Override
    public void onActivityStarted(Activity activity) {
        if (numStarted == 0) {
            if (!actualSecurity().equals("")){
                callPasswordRequest(activity,null);
            }
        }
        numStarted++;
    }

    @Override
    public void onActivityStopped(Activity activity) {
        numStarted--;
        if (numStarted == 0) {
            if (!actualSecurity().equals("")){
                callPasswordRequest(activity,null);
            }
        }
    }

    public void callPasswordRequest(Activity activity, final OnResponse onResponsePattern){
        if ((!activity.getIntent().hasExtra("ACTIVITY_TYPE")) || (!activity.getIntent().getStringExtra("ACTIVITY_TYPE").equals("PASSWORD_REQUEST"))) {
            Intent intent = null;
            if ((this.passwordEncrypted != null) && (!this.passwordEncrypted.equals(""))) {

                intent = new Intent(activity, PinRequestActivity.class);

                PinRequestActivity.setOnResponse(null);

                /*
                 * Connect error and success listeners
                 * */
                if(onResponsePattern != null){
                    PinRequestActivity.setOnResponse(onResponsePattern);
                }

            } else if ((this.patternEncrypted != null) && (!this.patternEncrypted.equals(""))) {

                intent = new Intent(activity, PatternRequestActivity.class);

                PatternRequestActivity.setOnResponse(null);

                /*
                 * Connect error and success listeners
                 * */
                if(onResponsePattern != null){
                    PatternRequestActivity.setOnResponse(onResponsePattern);
                }
            } else {
                onResponsePattern.onSuccess();
            }
            if (intent != null) {
                intent.putExtra("ACTIVITY_TYPE", "PASSWORD_REQUEST");
                activity.startActivity(intent);
            }
        }
    }

    public void call2ndFactor(Activity activity){
        Intent intent = null;
        if ((this.yubikeyOathTotpPasswordEncrypted != null) && (!this.yubikeyOathTotpPasswordEncrypted.equals(""))) {
            intent = new Intent(activity, PocketRequestActivity.class);
            //intent.putExtra("ACTIVITY_TYPE", "PASSWORD_REQUEST");
            activity.startActivity(intent);
        }
    }

    public String get2ndFactorValue(){
        return this.yubikeyOathTotpPasswordEncrypted;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {
        //
    }

    @Override
    public void onActivityResumed(Activity activity) {
        //
    }

    @Override
    public void onActivityPaused(Activity activity) {
        //
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
        //
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        //
    }
}