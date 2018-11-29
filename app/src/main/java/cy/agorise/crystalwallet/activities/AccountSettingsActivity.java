package cy.agorise.crystalwallet.activities;

import android.os.Bundle;
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
import cy.agorise.crystalwallet.fragments.BackupsSettingsFragment;
import cy.agorise.crystalwallet.fragments.GeneralSettingsFragment;
import cy.agorise.crystalwallet.fragments.SecuritySettingsFragment;

/**
 * Created by henry varona on 05/27/18.
 *
 */

public class AccountSettingsActivity extends AppCompatActivity{

    @BindView(R.id.ivGoBack)
    public ImageView ivGoBack;

    @BindView(R.id.pager)
    public ViewPager mPager;

    public SettingsPagerAdapter settingsPagerAdapter;

    @BindView(R.id.tvBuildVersion)
    public TextView tvBuildVersion;

    @BindView(R.id.ivAppBarAnimation)
    ImageView ivAppBarAnimation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crypto_net_account_activity_settings);
        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Sets AppBar animation
        Glide.with(this)
                .load(R.drawable.appbar_background)
                .apply(new RequestOptions().centerCrop())
                .into(ivAppBarAnimation);

        settingsPagerAdapter = new SettingsPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(settingsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);

        mPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mPager));
    }

    private class SettingsPagerAdapter extends FragmentStatePagerAdapter {
        SettingsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return new GeneralSettingsFragment();
                case 1:
                    return new SecuritySettingsFragment();
                case 2:
                    return new BackupsSettingsFragment();
                //case 3:
                //    return new AccountsSettingsFragment();
            }


            return null; //new OnConstructionFragment();
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

    @OnClick(R.id.ivGoBack)
    public void goBack(){
        onBackPressed();
    }
}
