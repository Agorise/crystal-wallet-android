package cy.agorise.crystalwallet;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import java.util.List;

import cy.agorise.crystalwallet.Assertions.RecyclerViewItemsCountAssertion;
import cy.agorise.crystalwallet.activities.IntroActivity;
import cy.agorise.crystalwallet.dao.CrystalDatabase;
import cy.agorise.crystalwallet.models.CryptoCoinTransaction;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.withId;


/**
 * Created by Henry Varona on 19/9/2017.
 */
@RunWith(AndroidJUnit4.class)
public class TransactionListTest {
    CrystalDatabase db;

    List<CryptoCoinTransaction> transactions;
    int numberOfTransactions = 100;

    @Before
    public void addingTransactions(){
        db = CrystalDatabase.getAppDatabase(InstrumentationRegistry.getTargetContext());
        //transactions = RandomTransactionsGenerator.generateTransactions(numberOfTransactions,1262304001,1496275201,1,999999999);

        for(int i=0;i<transactions.size();i++) {
            db.transactionDao().insertTransaction(transactions.get(i));
        }
    }

    @Rule
    public ActivityTestRule<IntroActivity> activityTestRule = new ActivityTestRule<IntroActivity>(IntroActivity.class);

    @Test
    public void numberOfTransactionsInList(){
        onView(withId(R.id.transactionListView)).check(new RecyclerViewItemsCountAssertion(numberOfTransactions));
    }

    @After
    public void deleteTransactions(){
        this.db.transactionDao().deleteAllTransactions();
    }
}
