package cy.agorise.crystalwallet.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import cy.agorise.crystalwallet.dao.CrystalDatabase
import cy.agorise.crystalwallet.models.AccountSeed

class AccountSeedViewModel(application: Application) : AndroidViewModel(application) {

    private var accountSeed: LiveData<AccountSeed>? = null
    private val db: CrystalDatabase
    private val app: Application = application

    init{
        this.db = CrystalDatabase.getAppDatabase(this.app.getApplicationContext());
    }

    fun loadSeed(seedId: Long) {
        this.accountSeed = this.db.accountSeedDao().findByIdLiveData(seedId)
    }

    fun addSeed(seed: AccountSeed) {
        val newId = this.db.accountSeedDao().insertAccountSeed(seed)
        seed.id = newId
    }

    fun getAccountSeed(): LiveData<AccountSeed>? {
        return this.accountSeed
    }
}