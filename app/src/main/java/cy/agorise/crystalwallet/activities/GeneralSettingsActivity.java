package cy.agorise.crystalwallet.activities;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemSelected;
import cy.agorise.crystalwallet.R;
import cy.agorise.crystalwallet.models.GeneralSetting;
import cy.agorise.crystalwallet.viewmodels.GeneralSettingListViewModel;

public class GeneralSettingsActivity extends AppCompatActivity {

    @BindView(R.id.spPreferedCountry)
    Spinner spPreferedCountry;

    private GeneralSettingListViewModel generalSettingListViewModel;
    private LiveData<List<GeneralSetting>> generalSettingListLiveData;
    private HashMap<String,String> countriesMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.general_settings);

        ButterKnife.bind(this);

        this.generalSettingListViewModel = ViewModelProviders.of(this).get(GeneralSettingListViewModel.class);
        generalSettingListLiveData = generalSettingListViewModel.getGeneralSettingList();

        // Initializes the countries spinner
        countriesMap = new HashMap<String, String>();
        String[] countryCodeList = Locale.getISOCountries();
        ArrayList<String> countryAndCurrencyList = new ArrayList<String>();
        String countryAndCurrencyLabel = "";
        for (String countryCode : countryCodeList) {
            Locale locale = new Locale("", countryCode);
            try {
                Currency currency = Currency.getInstance(locale);
                countryAndCurrencyLabel = locale.getDisplayCountry() + " (" + currency.getCurrencyCode() + ")";
                countryAndCurrencyList.add(countryAndCurrencyLabel);
                countriesMap.put(countryCode, countryAndCurrencyLabel);
                countriesMap.put(countryAndCurrencyLabel, countryCode);
            } catch (Exception e) {

            }
        }
        Collections.sort(countryAndCurrencyList);
        countryAndCurrencyList.add(0,"SELECT COUNTRY");
        ArrayAdapter<String> countryAdapter = new ArrayAdapter<String>(this.getApplicationContext(), android.R.layout.simple_spinner_item, countryAndCurrencyList);
        spPreferedCountry.setAdapter(countryAdapter);

        //Observes the general settings data
        generalSettingListLiveData.observe(this, new Observer<List<GeneralSetting>>() {
            @Override
            public void onChanged(@Nullable List<GeneralSetting> generalSettings) {
                loadSettings(generalSettings);
            }
        });
    }

    public GeneralSetting getSetting(String name){
        for (GeneralSetting generalSetting:this.generalSettingListLiveData.getValue()) {
            if (generalSetting.getName().equals(name)) {
                return generalSetting;
            }
        }

        return null;
    }

    @OnItemSelected(R.id.spPreferedCountry)
    void onItemSelected(int position) {
        if (position != 0) {
            GeneralSetting generalSettingCountryCode = this.getSetting(GeneralSetting.SETTING_NAME_PREFERRED_COUNTRY);
            GeneralSetting generalSettingCurrency = this.getSetting(GeneralSetting.SETTING_NAME_PREFERRED_CURRENCY);

            if (generalSettingCountryCode == null){
                generalSettingCountryCode = new GeneralSetting();
                generalSettingCountryCode.setName(GeneralSetting.SETTING_NAME_PREFERRED_COUNTRY);
            }
            if (generalSettingCurrency == null){
                generalSettingCurrency = new GeneralSetting();
                generalSettingCurrency.setName(GeneralSetting.SETTING_NAME_PREFERRED_CURRENCY);
            }

            String countryCode = countriesMap.get((String) spPreferedCountry.getSelectedItem());
            Locale locale = new Locale("", countryCode);
            Currency currency = Currency.getInstance(locale);

            generalSettingCountryCode.setValue(countryCode);
            generalSettingCurrency.setValue(currency.getCurrencyCode());
            this.generalSettingListViewModel.saveGeneralSettings(generalSettingCountryCode, generalSettingCurrency);
        }
    }

    public void loadSettings(List<GeneralSetting> generalSettings){
        for (GeneralSetting generalSetting:generalSettings) {
            if (generalSetting.getName().equals(GeneralSetting.SETTING_NAME_PREFERRED_COUNTRY)){
                String preferedCountryCode = generalSetting.getValue();
                spPreferedCountry.setSelection(((ArrayAdapter<String>)spPreferedCountry.getAdapter()).getPosition(countriesMap.get(preferedCountryCode)));
            }
        }
    }
}
