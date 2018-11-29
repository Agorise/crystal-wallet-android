package cy.agorise.crystalwallet.activities;

import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.thekhaeng.pushdownanim.PushDownAnim;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cy.agorise.crystalwallet.R;
import cy.agorise.crystalwallet.application.CrystalSecurityMonitor;
import cy.agorise.crystalwallet.fragments.ImportAccountOptionsFragment;
import cy.agorise.crystalwallet.viewmodels.AccountSeedListViewModel;

public class IntroActivity extends CustomActivity {

    @BindView(R.id.ivAnimation)
    ImageView ivAnimation;

    @BindView(R.id.btnCreateAccount)
    Button btnCreateAccount;

    @BindView(R.id.btnImportAccount)
    Button btnImportAccount;

    /*
     * For the window animation
     * */
//    private MediaPlayer mediaPlayer;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        ButterKnife.bind(this);

        Glide.with(this)
                .load(R.drawable.appbar_background)
                .apply(new RequestOptions().centerCrop())
                .into(ivAnimation);

        /*
         *   Integration of library with button effects
         * */
        PushDownAnim.setPushDownAnimTo(btnCreateAccount)
                .setOnClickListener( new View.OnClickListener(){
                    @Override
                    public void onClick( View view ){
                        createAccount();
                    }
                } );
        PushDownAnim.setPushDownAnimTo(btnImportAccount)
                .setOnClickListener( new View.OnClickListener(){
                    @Override
                    public void onClick( View view ){
                        importAccount();
                    }
                } );


        this.getApplication().registerActivityLifecycleCallbacks(CrystalSecurityMonitor.getInstance(this));

        //Checks if the user has any seed created
        AccountSeedListViewModel accountSeedListViewModel = ViewModelProviders.of(this).get(AccountSeedListViewModel.class);

        if (accountSeedListViewModel.accountSeedsCount() == 0) {
            //If the user doesn't have any seeds created, then
            //send the user to create/import an account
            //Intent intent = new Intent(this, AccountSeedsManagementActivity.class);
            //Intent intent = new Intent(this, ImportSeedActivity.class);
            //Intent intent = new Intent(this, CreateSeedActivity.class);
            //startActivity(intent);
        } else {
            //Intent intent = new Intent(this, CreateSeedActivity.class);
            Intent intent = new Intent(this, BoardActivity.class);
            //Intent intent = new Intent(this, PocketRequestActivity.class);
            startActivity(intent);
            finish();
        }

        /*CrystalDatabase db = CrystalDatabase.getAppDatabase(getApplicationContext());
        List<AccountSeed> seeds = RandomSeedGenerator.generateSeeds(2);
        for(int i=0;i<seeds.size();i++) {
            long newId = db.accountSeedDao().insertAccountSeed(seeds.get(i));
            seeds.get(i).setId(newId);
        }
        List<CryptoNetAccount> accounts = RandomCryptoNetAccountGenerator.generateAccounts(5,seeds);
        for(int i=0;i<accounts.size();i++) {
            long newId = db.cryptoNetAccountDao().insertCryptoNetAccount(accounts.get(i))[0];
            accounts.get(i).setId(newId);
        }
        List<CryptoCoinBalance> balances = RandomCryptoCoinBalanceGenerator.generateCryptoCoinBalances(accounts,5,1,20);
        for(int i=0;i<balances.size();i++) {
            long newId = db.cryptoCoinBalanceDao().insertCryptoCoinBalance(balances.get(i))[0];
            balances.get(i).setId(newId);
        }
        List<CryptoCoinTransaction> transactions = RandomTransactionsGenerator.generateTransactions(accounts,100,1262304001,1496275201,1,999999999);
        for(int i=0;i<transactions.size();i++) {
            long newId = db.transactionDao().insertTransaction(transactions.get(i))[0];
            transactions.get(i).setId(newId);
        }*/

        /*transactionListView = this.findViewById(R.id.transaction_list);

        transactionListViewModel = ViewModelProviders.of(this).get(TransactionListViewModel.class);
        LiveData<PagedList<CryptoCoinTransaction>> transactionData = transactionListViewModel.getTransactionList();
        transactionListView.setData(null);

        transactionData.observe(this, new Observer<PagedList<CryptoCoinTransaction>>() {
            @Override
            public void onChanged(PagedList<CryptoCoinTransaction> cryptoCoinTransactions) {
                transactionListView.setData(cryptoCoinTransactions);
            }
        });*/
    }

    @OnClick(R.id.btnCreateAccount)
    public void createAccount() {
        Intent intent = new Intent(this, CreateSeedActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.btnImportAccount)
    public void importAccount() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("importAccountOptions");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        ImportAccountOptionsFragment newFragment = ImportAccountOptionsFragment.newInstance();
        newFragment.show(ft, "importAccountOptions");
        newFragment.setIntroActivity(globalActivity); //This activity should close when import succeds
    }
}
