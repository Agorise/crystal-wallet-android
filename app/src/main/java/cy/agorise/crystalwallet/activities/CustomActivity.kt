package cy.agorise.crystalwallet.activities

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import cy.agorise.crystalwallet.util.FieldsValidator

/*
* Custom implementaion of the activity
* */
open class CustomActivity : AppCompatActivity() {

    /*
    * Contains the validator for general fields
    * */
    @JvmField protected var fieldsValidator = FieldsValidator()

    /*
    * Contains the global activity
    * */
    protected lateinit var globalActivity: Activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*
        * Save the current activity for further reference
        * */
        this.globalActivity = this
    }
}