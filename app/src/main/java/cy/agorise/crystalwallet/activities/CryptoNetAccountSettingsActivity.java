package cy.agorise.crystalwallet.activities;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cy.agorise.crystalwallet.R;
import cy.agorise.crystalwallet.fragments.BitsharesSettingsFragment;
import cy.agorise.crystalwallet.fragments.GeneralCryptoNetAccountSettingsFragment;
import cy.agorise.crystalwallet.models.CryptoNetAccount;
import cy.agorise.crystalwallet.viewmodels.CryptoNetAccountViewModel;

/**
 * Created by henry varona on 05/28/18.
 *
 */

public class CryptoNetAccountSettingsActivity extends AppCompatActivity{

    @BindView(R.id.ivGoBack)
    public ImageView ivGoBack;

    @BindView(R.id.pager)
    public ViewPager mPager;

    public SettingsPagerAdapter settingsPagerAdapter;


    @BindView(R.id.tvBuildVersion)
    public TextView tvBuildVersion;

    @BindView(R.id.tabs)
    public TabLayout tabs;

    private CryptoNetAccount cryptoNetAccount;

    @BindView(R.id.ivAppBarAnimation)
    ImageView ivAppBarAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crypto_net_account_activity_settings);
        ButterKnife.bind(this);
        final CryptoNetAccountSettingsActivity thisActivity = this;

        long accountId = getIntent().getLongExtra("CRYPTO_NET_ACCOUNT_ID",-1);

        if (accountId > -1) {
            CryptoNetAccountViewModel cryptoNetAccountViewModel = ViewModelProviders.of(this).get(CryptoNetAccountViewModel.class);
            cryptoNetAccountViewModel.loadCryptoNetAccount(accountId);
            LiveData<CryptoNetAccount> cryptoNetAccountLiveData = cryptoNetAccountViewModel.getCryptoNetAccount();

            cryptoNetAccountLiveData.observe(this, new Observer<CryptoNetAccount>() {
                @Override
                public void onChanged(@Nullable CryptoNetAccount cryptoNetAccount) {
                    thisActivity.cryptoNetAccount = cryptoNetAccount;

                    settingsPagerAdapter = new SettingsPagerAdapter(getSupportFragmentManager());
                    mPager.setAdapter(settingsPagerAdapter);

                    TabLayout tabLayout = findViewById(R.id.tabs);

                    switch(cryptoNetAccount.getCryptoNet()){
                        case BITSHARES:
                            tabLayout.addTab(tabLayout.newTab().setText("Bitshares"));
                            break;
                    }
                    mPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
                    tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mPager));
                }
            });

            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            // Sets AppBar animation
            Glide.with(this)
                    .load(R.drawable.appbar_background)
                    .apply(new RequestOptions().centerCrop())
                    .into(ivAppBarAnimation);


        } else {
            this.finish();
        }
    }

    private class SettingsPagerAdapter extends FragmentStatePagerAdapter {
        SettingsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return GeneralCryptoNetAccountSettingsFragment.newInstance(cryptoNetAccount.getId());
            }

            if (cryptoNetAccount != null){
                switch (cryptoNetAccount.getCryptoNet()){
                    case BITSHARES:
                        switch(position){
                            case 1:
                                return BitsharesSettingsFragment.newInstance(cryptoNetAccount.getId());
                        }

                        break;
                }
            }

            return null;
        }

        @Override
        public int getCount() {
            int tabCount = 1;

            if (cryptoNetAccount != null){
                switch (cryptoNetAccount.getCryptoNet()){
                    case BITSHARES:
                        tabCount = tabCount+1;
                        break;
                }
            }

            return tabCount;
        }
    }

    @OnClick(R.id.ivGoBack)
    public void goBack(){
        onBackPressed();
    }
}
