package cy.agorise.crystalwallet.fragments;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.thekhaeng.pushdownanim.PushDownAnim;
import com.vincent.filepicker.Constant;
import com.vincent.filepicker.activity.AudioPickActivity;
import com.vincent.filepicker.filter.entity.AudioFile;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import cy.agorise.crystalwallet.R;
import cy.agorise.crystalwallet.enums.Language;
import cy.agorise.crystalwallet.models.GeneralSetting;
import cy.agorise.crystalwallet.viewmodels.GeneralSettingListViewModel;
import cy.agorise.crystalwallet.views.TimeZoneAdapter;

import static android.app.Activity.RESULT_OK;
import static com.vincent.filepicker.activity.AudioPickActivity.IS_NEED_RECORDER;


/**
 * Created by xd on 12/28/17.
 */

public class GeneralSettingsFragment extends Fragment {

    private HashMap<String,String> countriesMap;
    private GeneralSettingListViewModel generalSettingListViewModel;
    private LiveData<List<GeneralSetting>> generalSettingListLiveData;

    private Boolean spPreferredLanguageInitialized;
    private Boolean spTimeZoneInitialized;

    @BindView (R.id.spTaxableCountry)
    Spinner spTaxableCountry;
    @BindView (R.id.spPreferredLanguage)
    Spinner spPreferredLanguage;
    @BindView (R.id.spDisplayDateTime)
    Spinner spDisplayDateTime;
    @BindView (R.id.tvReceiveFundsSoundValue)
    TextView tvReceiveFundsSound;
    @BindView (R.id.btnContact)
    Button btnContact;

    public GeneralSettingsFragment() {
        this.spPreferredLanguageInitialized = false;
        this.spTimeZoneInitialized = false;
        // Required empty public constructor
    }

    public static GeneralSettingsFragment newInstance() {
        GeneralSettingsFragment fragment = new GeneralSettingsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        fragment.spPreferredLanguageInitialized = false;
        fragment.spTimeZoneInitialized = false;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_general_settings, container, false);
        ButterKnife.bind(this, v);

        /*
         *   Integration of library with button efects
         * */
        PushDownAnim.setPushDownAnimTo(btnContact)
                .setOnClickListener( new View.OnClickListener(){
                    @Override
                    public void onClick( View view ){

                    }
                } );

        generalSettingListViewModel = ViewModelProviders.of(this).get(GeneralSettingListViewModel.class);
        generalSettingListLiveData = generalSettingListViewModel.getGeneralSettingList();



        //Observes the general settings data
        generalSettingListLiveData.observe(this, new Observer<List<GeneralSetting>>() {
            @Override
            public void onChanged(@Nullable List<GeneralSetting> generalSettings) {
                loadSettings(generalSettings);
            }
        });



        return v;
    }

    public void initPreferredCountry(GeneralSetting preferredCountrySetting){
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
        ArrayAdapter<String> countryAdapter = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_spinner_item, countryAndCurrencyList);
        spTaxableCountry.setAdapter(countryAdapter);

        if (preferredCountrySetting != null) {
            String preferedCountryCode = preferredCountrySetting.getValue();
            spTaxableCountry.setSelection(((ArrayAdapter<String>) spTaxableCountry.getAdapter()).getPosition(countriesMap.get(preferedCountryCode)));
        }
    }

    public void initPreferredLanguage(GeneralSetting preferredLanguageSetting){
        ArrayAdapter<Language> preferredLanguageAdapter = new ArrayAdapter<Language>(getContext(), android.R.layout.simple_spinner_item, Language.values());
        spPreferredLanguage.setAdapter(preferredLanguageAdapter);
        if (preferredLanguageSetting != null) {
            spPreferredLanguage.setSelection(preferredLanguageAdapter.getPosition(Language.getByCode(preferredLanguageSetting.getValue())));
        }
    }

    public void initDateTimeFormat(GeneralSetting dateTimeFormatSetting){
        TimeZoneAdapter timeZoneAdapter;
        if (spDisplayDateTime.getAdapter() == null) {
            timeZoneAdapter = new TimeZoneAdapter(getContext(), android.R.layout.simple_spinner_dropdown_item);
            spDisplayDateTime.setAdapter(timeZoneAdapter);
        } else {
            timeZoneAdapter = (TimeZoneAdapter) spDisplayDateTime.getAdapter();
        }
        if (dateTimeFormatSetting != null) {
            spDisplayDateTime.setSelection(timeZoneAdapter.getPosition(dateTimeFormatSetting.getValue()));
        }
    }

    public void initReceiveFundsSound(GeneralSetting receiveFundsSoundSetting){
        if (receiveFundsSoundSetting != null){
            if (receiveFundsSoundSetting.getValue().equals("")){
                tvReceiveFundsSound.setText("Woohoo");
            } else {
                File audioFile = new File(receiveFundsSoundSetting.getValue());

                tvReceiveFundsSound.setText(audioFile.getName());
            }
        } else {
            tvReceiveFundsSound.setText("Woohoo");
        }
    }

    public GeneralSetting getSetting(String name){
        for (GeneralSetting generalSetting:this.generalSettingListLiveData.getValue()) {
            if (generalSetting.getName().equals(name)) {
                return generalSetting;
            }
        }

        return null;
    }


    @OnClick(R.id.tvReceiveFundsSoundValue)
    void onReceiveFundsSoundSelected(){
        Intent intent3 = new Intent(this.getContext(), AudioPickActivity.class);
        intent3.putExtra(IS_NEED_RECORDER, true);
        intent3.putExtra(Constant.MAX_NUMBER, 1);
        startActivityForResult(intent3, Constant.REQUEST_CODE_PICK_AUDIO);
    }

    @OnItemSelected(R.id.spTaxableCountry)
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

            String countryCode = countriesMap.get((String) spTaxableCountry.getSelectedItem());
            Locale locale = new Locale("", countryCode);
            Currency currency = Currency.getInstance(locale);

            generalSettingCountryCode.setValue(countryCode);
            generalSettingCurrency.setValue(currency.getCurrencyCode());
            this.generalSettingListViewModel.saveGeneralSettings(generalSettingCountryCode, generalSettingCurrency);
        }
    }

    @OnItemSelected(R.id.spDisplayDateTime)
    void onTimeZoneSelected(int position){
        //The first call will be when the spinner gets an adapter attached
        if (this.spTimeZoneInitialized) {
            String timeZoneIdSelected = (String) this.spDisplayDateTime.getSelectedItem();
            GeneralSetting generalSettingTimeZone = this.getSetting(GeneralSetting.SETTING_NAME_TIME_ZONE);

            if (generalSettingTimeZone == null) {
                generalSettingTimeZone = new GeneralSetting();
                generalSettingTimeZone.setName(GeneralSetting.SETTING_NAME_TIME_ZONE);
            }

            if ((generalSettingTimeZone.getValue() == null)||(!generalSettingTimeZone.getValue().equals(timeZoneIdSelected))) {
                generalSettingTimeZone.setValue(timeZoneIdSelected);
                this.generalSettingListViewModel.saveGeneralSettings(generalSettingTimeZone);
            }
        } else {
            this.spTimeZoneInitialized = true;
        }
    }

    @OnItemSelected(R.id.spPreferredLanguage)
    void onPreferredLanguageSelected(int position){
        //The first call will be when the spinner gets an adapter attached
        if (this.spPreferredLanguageInitialized) {
            Language languageSelected = (Language) this.spPreferredLanguage.getSelectedItem();
            GeneralSetting generalSettingPreferredLanguage = this.getSetting(GeneralSetting.SETTING_NAME_PREFERRED_LANGUAGE);

            if (generalSettingPreferredLanguage == null) {
                generalSettingPreferredLanguage = new GeneralSetting();
                generalSettingPreferredLanguage.setName(GeneralSetting.SETTING_NAME_PREFERRED_LANGUAGE);
            }

            if ((generalSettingPreferredLanguage.getValue() == null)||(!generalSettingPreferredLanguage.getValue().equals(languageSelected.getCode()))) {
                generalSettingPreferredLanguage.setValue(languageSelected.getCode());
                this.generalSettingListViewModel.saveGeneralSettings(generalSettingPreferredLanguage);

                Resources resources = getContext().getResources();
                Locale locale = new Locale(languageSelected.getCode());
                Locale.setDefault(locale);
                DisplayMetrics dm = resources.getDisplayMetrics();
                Configuration configuration = resources.getConfiguration();
                configuration.locale = locale;
                resources.updateConfiguration(configuration, dm);
                Intent i = getContext().getPackageManager()
                        .getLaunchIntentForPackage(getContext().getPackageName());
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        } else {
            this.spPreferredLanguageInitialized = true;
        }
    }

    public void loadSettings(List<GeneralSetting> generalSettings){

        initPreferredCountry(getSetting(GeneralSetting.SETTING_NAME_PREFERRED_COUNTRY));
        initPreferredLanguage(getSetting(GeneralSetting.SETTING_NAME_PREFERRED_LANGUAGE));
        initDateTimeFormat(getSetting(GeneralSetting.SETTING_NAME_TIME_ZONE));
        initReceiveFundsSound(getSetting(GeneralSetting.SETTING_NAME_RECEIVED_FUNDS_SOUND_PATH));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constant.REQUEST_CODE_PICK_AUDIO){
            if (resultCode == RESULT_OK) {
                ArrayList<AudioFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_AUDIO);
                if (list.size() > 0) {
                    AudioFile audioSelected = list.get(0);
                    String audioSelectedPath = audioSelected.getPath();

                    GeneralSetting generalSettingReceivedFundsSoundPath = this.getSetting(GeneralSetting.SETTING_NAME_RECEIVED_FUNDS_SOUND_PATH);

                    if (generalSettingReceivedFundsSoundPath == null){
                        generalSettingReceivedFundsSoundPath = new GeneralSetting();
                        generalSettingReceivedFundsSoundPath.setName(GeneralSetting.SETTING_NAME_RECEIVED_FUNDS_SOUND_PATH);
                    }

                    generalSettingReceivedFundsSoundPath.setValue(audioSelectedPath);
                    this.generalSettingListViewModel.saveGeneralSettings(generalSettingReceivedFundsSoundPath);
                    tvReceiveFundsSound.setText(audioSelected.getName());
                }
            }
        }
    }
}
