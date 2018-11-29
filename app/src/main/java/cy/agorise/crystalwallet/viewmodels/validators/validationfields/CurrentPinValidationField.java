package cy.agorise.crystalwallet.viewmodels.validators.validationfields;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import android.widget.EditText;

import java.util.List;

import cy.agorise.crystalwallet.models.GeneralSetting;
import cy.agorise.crystalwallet.util.PasswordManager;
import cy.agorise.crystalwallet.viewmodels.GeneralSettingListViewModel;

/**
 * Created by Henry Varona on 1/28/2018.
 */

public class CurrentPinValidationField extends ValidationField {

    private EditText currentPinField;
    String currentPassword = "";

    public CurrentPinValidationField(EditText currentPinField){
        super(currentPinField);
        this.currentPinField = currentPinField;
        GeneralSettingListViewModel generalSettingListViewModel = ViewModelProviders.of((FragmentActivity)view.getContext()).get(GeneralSettingListViewModel.class);
        LiveData<List<GeneralSetting>> generalSettingsLiveData = generalSettingListViewModel.getGeneralSettingList();
        generalSettingsLiveData.observe((LifecycleOwner) this.view.getContext(), new Observer<List<GeneralSetting>>() {
            @Override
            public void onChanged(@Nullable List<GeneralSetting> generalSettings) {
                for (GeneralSetting generalSetting:generalSettings) {
                    if (generalSetting.getName().equals(GeneralSetting.SETTING_PASSWORD)){
                        currentPassword = generalSetting.getValue();
                        break;
                    }
                }
            }
        });
    }

    public void validate(){
        final String newValue = currentPinField.getText().toString();

        if (this.currentPassword.equals("")) {
            this.setLastValue("");
            this.startValidating();
            setValidForValue("",true);
        } else if (!newValue.equals(this.getLastValue())) {
            this.setLastValue(newValue);
            this.startValidating();
            if (newValue.equals("")){
                setMessageForValue(lastValue, "");
                setValidForValue(lastValue, false);
            } else {

                if (PasswordManager.checkPassword(this.currentPassword, newValue)) {
                    setValidForValue(lastValue, true);
                } else {
                    setMessageForValue(lastValue, "Password is invalid.");
                    setValidForValue(lastValue, false);
                }
            }
        }
    }
}
