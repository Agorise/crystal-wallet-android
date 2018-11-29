package cy.agorise.crystalwallet.activities;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cy.agorise.crystalwallet.R;
import cy.agorise.crystalwallet.models.AccountSeed;
import cy.agorise.crystalwallet.viewmodels.AccountSeedListViewModel;
import cy.agorise.crystalwallet.views.AccountSeedListView;

public class AccountSeedsManagementActivity extends AppCompatActivity {

    AccountSeedsManagementActivity accountSeedsManagementActivity;
    AccountSeedListViewModel accountSeedListViewModel;

    @BindView(R.id.vAccountSeedList)
    AccountSeedListView vAccountSeedList;

    @BindView(R.id.btnImportAccountSeed)
    Button btnImportAccountSeed;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_seeds_management);
        ButterKnife.bind(this);

        accountSeedListViewModel = ViewModelProviders.of(this).get(AccountSeedListViewModel.class);
        LiveData<List<AccountSeed>>  accountSeedData = accountSeedListViewModel.getAccountSeedList();
        vAccountSeedList.setData(null);

        accountSeedData.observe(this, new Observer<List<AccountSeed>>() {
            @Override
            public void onChanged(List<AccountSeed> accountSeeds) {
                vAccountSeedList.setData(accountSeeds);
            }
        });
    }

    @OnClick (R.id.btnImportAccountSeed)
    public void importAccountSeed(){
        Intent intent = new Intent(this, ImportSeedActivity.class);
        startActivity(intent);
    }
}
