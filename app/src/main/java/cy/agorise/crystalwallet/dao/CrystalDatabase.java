package cy.agorise.crystalwallet.dao;

import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import android.content.Context;

import cy.agorise.crystalwallet.dao.converters.Converters;
import cy.agorise.crystalwallet.models.AccountSeed;
import cy.agorise.crystalwallet.models.BitcoinAddress;
import cy.agorise.crystalwallet.models.BitcoinTransaction;
import cy.agorise.crystalwallet.models.BitcoinTransactionGTxIO;
import cy.agorise.crystalwallet.models.BitsharesAccountNameCache;
import cy.agorise.crystalwallet.models.BitsharesAssetInfo;
import cy.agorise.crystalwallet.models.Contact;
import cy.agorise.crystalwallet.models.ContactAddress;
import cy.agorise.crystalwallet.models.CryptoCoinBalance;
import cy.agorise.crystalwallet.models.CryptoCoinTransaction;
import cy.agorise.crystalwallet.models.CryptoCurrency;
import cy.agorise.crystalwallet.models.CryptoCurrencyEquivalence;
import cy.agorise.crystalwallet.models.CryptoNetAccount;
import cy.agorise.crystalwallet.models.GeneralSetting;
import cy.agorise.crystalwallet.models.GrapheneAccountInfo;

/**
 * Manage the Database
 * Created by Henry Varona on 4/9/2017.
 */

@Database(entities = {
        AccountSeed.class,
        CryptoNetAccount.class,
        CryptoCoinTransaction.class,
        Contact.class,
        ContactAddress.class,
        CryptoCurrency.class,
        CryptoCoinBalance.class,
        GrapheneAccountInfo.class,
        BitsharesAssetInfo.class,
        BitsharesAccountNameCache.class,
        CryptoCurrencyEquivalence.class,
        GeneralSetting.class,
        BitcoinTransaction.class,
        BitcoinTransactionGTxIO.class,
        BitcoinAddress.class
}, version = 6, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class CrystalDatabase extends RoomDatabase {

    private static CrystalDatabase instance;

    public abstract AccountSeedDao accountSeedDao();
    public abstract CryptoNetAccountDao cryptoNetAccountDao();
    public abstract GrapheneAccountInfoDao grapheneAccountInfoDao();
    public abstract TransactionDao transactionDao();
    public abstract ContactDao contactDao();
    public abstract CryptoCoinBalanceDao cryptoCoinBalanceDao();
    public abstract CryptoCurrencyDao cryptoCurrencyDao();
    public abstract BitsharesAssetDao bitsharesAssetDao();
    public abstract BitsharesAccountNameCacheDao bitsharesAccountNameCacheDao();
    public abstract CryptoCurrencyEquivalenceDao cryptoCurrencyEquivalenceDao();
    public abstract GeneralSettingDao generalSettingDao();
    public abstract BitcoinTransactionDao bitcoinTransactionDao();
    public abstract BitcoinAddressDao bitcoinAddressDao();

    public static CrystalDatabase getAppDatabase(Context context) {
        if (instance == null) {
            instance =
                    Room.databaseBuilder(context,
                            CrystalDatabase.class, "CrystalWallet.db")
                            .allowMainThreadQueries()
                            //.addMigrations(MIGRATION_2_3)
                            //.addMigrations(MIGRATION_3_4)
                            //.addMigrations(MIGRATION_4_5)
                            //.addMigrations(MIGRATION_5_6)
                            .build();
        }
        return instance;
    }

    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE graphene_account ADD COLUMN upgraded_to_ltm INTEGER NOT NULL DEFAULT 0");
        }
    };

    static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE bitshares_account_name_cache ("
                    +"id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                    +"account_id TEXT UNIQUE NOT NULL,"
                    +"name TEXT)");

            database.execSQL("CREATE UNIQUE INDEX index_bitshares_account_name_cache_account_id ON bitshares_account_name_cache (account_id)");


        }
    };

    static final Migration MIGRATION_4_5 = new Migration(4, 5) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE bitcoin_transaction ("
                    +"crypto_coin_transaction_id INTEGER PRIMARY KEY NOT NULL,"
                    +"tx_id TEXT NOT NULL,"
                    +"block INTEGER NOT NULL,"
                    +"fee INTEGER NOT NULL,"
                    +"confirmations INTEGER NOT NULL,"
                    +"FOREIGN KEY (crypto_coin_transaction_id) REFERENCES crypto_coin_transaction(id) ON DELETE CASCADE)");

            database.execSQL("CREATE TABLE bitcoin_transaction_gt_io ("
                    +"bitcoin_transaction_id INTEGER NOT NULL,"
                    +"io_index INTEGER NOT NULL,"
                    +"address TEXT,"
                    +"is_output INTEGER NOT NULL,"
                    +"amount INTEGER NOT NULL,"
                    +"script_hex TEXT,"
                    +"original_txid TEXT,"
                    +"PRIMARY KEY (bitcoin_transaction_id, io_index, is_output),"
                    +"FOREIGN KEY (bitcoin_transaction_id) REFERENCES bitcoin_transaction(crypto_coin_transaction_id) ON DELETE CASCADE)");
        }
    };

    static final Migration MIGRATION_5_6 = new Migration(5, 6) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE bitcoin_address ("
                    +"account_id INTEGER NOT NULL,"
                    +"address_index INTEGER NOT NULL,"
                    +"is_change INTEGER NOT NULL,"
                    +"address TEXT NOT NULL,"
                    +"PRIMARY KEY (account_id, address_index, is_change),"
                    +"FOREIGN KEY (account_id) REFERENCES crypto_net_account(id) ON DELETE CASCADE)");
        }
    };
}
