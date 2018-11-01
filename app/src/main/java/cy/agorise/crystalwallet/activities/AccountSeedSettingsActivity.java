package cy.agorise.crystalwallet.activities;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cy.agorise.crystalwallet.R;
import cy.agorise.crystalwallet.fragments.BitsharesSettingsFragment;
import cy.agorise.crystalwallet.fragments.GeneralAccountSeedCoinSettingsFragment;
import cy.agorise.crystalwallet.fragments.GeneralAccountSeedFragment;
import cy.agorise.crystalwallet.fragments.GeneralCryptoNetAccountSettingsFragment;
import cy.agorise.crystalwallet.models.AccountSeed;
import cy.agorise.crystalwallet.models.CryptoNetAccount;
import cy.agorise.crystalwallet.viewmodels.AccountSeedViewModel;
import cy.agorise.crystalwallet.viewmodels.CryptoNetAccountViewModel;

/**
 * Created by henry varona on 10/29/18.
 *
 */

public class AccountSeedSettingsActivity extends AppCompatActivity{

    @BindView(R.id.ivGoBack)
    public ImageView ivGoBack;

    @BindView(R.id.pager)
    public ViewPager mPager;

    public SettingsPagerAdapter settingsPagerAdapter;


    @BindView(R.id.tvBuildVersion)
    public TextView tvBuildVersion;

    @BindView(R.id.tabs)
    public TabLayout tabs;

    private AccountSeed accountSeed;

    @BindView(R.id.ivAppBarAnimation)
    ImageView ivAppBarAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_seed_activity_settings);
        ButterKnife.bind(this);
        final AccountSeedSettingsActivity thisActivity = this;

        long accountSeedId = getIntent().getLongExtra("SEED_ID",-1);

        if (accountSeedId > -1) {
            AccountSeedViewModel accountSeedViewModel = ViewModelProviders.of(this).get(AccountSeedViewModel.class);
            accountSeedViewModel.loadSeed(accountSeedId);
            LiveData<AccountSeed> accountSeedLiveData = accountSeedViewModel.getAccountSeed();

            accountSeedLiveData.observe(this, new Observer<AccountSeed>() {
                @Override
                public void onChanged(@Nullable AccountSeed accountSeed) {
                    thisActivity.accountSeed = accountSeed;

                    settingsPagerAdapter = new SettingsPagerAdapter(getSupportFragmentManager());
                    mPager.setAdapter(settingsPagerAdapter);

                    TabLayout tabLayout = findViewById(R.id.tabs);

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
                    return GeneralAccountSeedFragment.newInstance(accountSeed.getId());
                case 1:
                    return GeneralAccountSeedCoinSettingsFragment.newInstance(accountSeed.getId());
            }

            return null;
        }

        @Override
        public int getCount() {
            int tabCount = 2;

            return tabCount;
        }
    }

    @OnClick(R.id.ivGoBack)
    public void goBack(){
        onBackPressed();
    }
}
