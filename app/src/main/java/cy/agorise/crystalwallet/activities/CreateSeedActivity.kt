package cy.agorise.crystalwallet.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.google.android.material.textfield.TextInputEditText
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.thekhaeng.pushdownanim.PushDownAnim
import com.vincent.filepicker.ToastUtil
import cy.agorise.crystalwallet.R
import cy.agorise.crystalwallet.dialogs.material.*
import cy.agorise.crystalwallet.requestmanagers.CryptoNetInfoRequests
import cy.agorise.crystalwallet.requestmanagers.ValidateCreateBitsharesAccountRequest
import cy.agorise.crystalwallet.viewmodels.validators.customImpl.interfaces.UIValidatorListener
import cy.agorise.crystalwallet.viewmodels.validators.customImpl.validationFields.BitsharesAccountNameValidation
import cy.agorise.crystalwallet.viewmodels.validators.customImpl.validationFields.BitsharesAccountNameValidation.OnAccountExist
import cy.agorise.crystalwallet.viewmodels.validators.customImpl.validationFields.CustomValidationField
import cy.agorise.crystalwallet.viewmodels.validators.customImpl.validationFields.PinDoubleConfirmationValidationField
import cy.agorise.crystalwallet.views.natives.CustomTextInputEditText
import kotlinx.android.synthetic.main.create_seed.*




/*
* This activity creates a new account with some security concerns
* */
class CreateSeedActivity : CustomActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*
        * Assign the view to this controller
        * */
        setContentView(R.layout.create_seed)

        /*
         * Add the controls to the validator
         * */
        this.fieldsValidator.add(tietPin)
        this.fieldsValidator.add(tietPinConfirmation)
        this.fieldsValidator.add(tietAccountName)

        /*
        *   Integration of library with button effects
        * */
        PushDownAnim.setPushDownAnimTo(btnCancel)
                .setOnClickListener { finish() }
        PushDownAnim.setPushDownAnimTo(btnCreate)
                .setOnClickListener { createSeed() }

        /*
        * Validations listener
        * */
        val uiValidatorListener = object : UIValidatorListener {

            override fun onValidationSucceeded(customValidationField: CustomValidationField) {

                try {

                    /*
                     * Remove error
                     * */
                    runOnUiThread {
                        val customTextInputEditText = customValidationField.currentView as CustomTextInputEditText
                        customTextInputEditText.error = null
                        customTextInputEditText.fieldValidatorModel.setValid()
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onValidationFailed(customValidationField: CustomValidationField) {

                /*
                 * Set error label
                 * */
                runOnUiThread {
                    val customTextInputEditText = customValidationField.currentView as CustomTextInputEditText
                    customTextInputEditText.error = customTextInputEditText.fieldValidatorModel.message
                    customTextInputEditText.fieldValidatorModel.setInvalid()
                }
            }
        }

        //Create the pin double validation
        val pinDoubleConfirmationValidationField = PinDoubleConfirmationValidationField(this, tietPin, tietPinConfirmation, uiValidatorListener)

        // Listener for the validation for success or fail
        tietPin?.setUiValidator(pinDoubleConfirmationValidationField) //Validator for the field
        tietPinConfirmation?.setUiValidator(pinDoubleConfirmationValidationField) //Validator for the field

        // Account name validator
        val bitsharesAccountNameValidation = BitsharesAccountNameValidation(this, tietAccountName, uiValidatorListener)
        val onAccountExist = object : OnAccountExist {
            override fun onAccountExists() {
                runOnUiThread {
                    Toast.makeText(globalActivity, resources.getString(R.string.account_name_already_exist), Toast.LENGTH_LONG).show()
                }
            }

        }
        bitsharesAccountNameValidation.setOnAccountExist(onAccountExist)
        tietAccountName?.setUiValidator(bitsharesAccountNameValidation)

        /*This button should not be enabled till all the fields be correctly filled*/
        btnCreate.isEnabled = false

        // Set the focus on the first field and show keyboard
        tilPin?.requestFocus()
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(tilPin, InputMethodManager.SHOW_IMPLICIT)

        tietPin.afterTextChanged {
            this.fieldsValidator.validate()
            validateFieldsToContinue()
        }

        tietPinConfirmation.afterTextChanged {
            this.fieldsValidator.validate()
            validateFieldsToContinue()
        }

        tietAccountName.afterTextChanged {
            this.fieldsValidator.validate()
            validateFieldsToContinue()
        }

        btnCancel.setOnClickListener { finish() }
        btnCreate.setOnClickListener { createSeed() }
    }

    /**
     * Extension function to easily add a text watcher
     */
    fun TextInputEditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
        this.addTextChangedListener(object :TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(editable: Editable?) {
                afterTextChanged.invoke(editable.toString())
            }
        })
    }

    fun createSeed() {

        /*
        * Question if continue or not
        * */
        var questionDialog:QuestionDialog = QuestionDialog(globalActivity)
        questionDialog.setText(getString(R.string.continue_question))
        questionDialog.setOnNegative(object : NegativeResponse{
            override fun onNegative(dialogMaterial: DialogMaterial) {
                dialogMaterial.dismiss()
            }
        })
        questionDialog.setOnPositive(object : PositiveResponse{
            override fun onPositive() {

                // Make request to create a bitshares account
                var accountName:String = tietAccountName?.getText().toString().trim()
                val request = ValidateCreateBitsharesAccountRequest(accountName, applicationContext)

                //Makes dialog to tell the user that the account is been created
                val creatingAccountMaterialDialog = CrystalDialog(globalActivity)
                creatingAccountMaterialDialog.setText(globalActivity.resources.getString(R.string.window_create_seed_DialogMessage))
                creatingAccountMaterialDialog.progress()
                this@CreateSeedActivity.runOnUiThread {
                    creatingAccountMaterialDialog.show()
                }
                request.setListener {
                    creatingAccountMaterialDialog.dismiss()
                    if (request.status == ValidateCreateBitsharesAccountRequest.StatusCode.SUCCEEDED) {
                        val accountSeed = request.account
                        val intent = Intent(applicationContext, BackupSeedActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        intent.putExtra("SEED_ID", accountSeed.id)
                        intent.putExtra("newAccount", true)
                        startActivity(intent)
                    }
                    else if (request.status == ValidateCreateBitsharesAccountRequest.StatusCode.ACCOUNT_EXIST) {
                        ToastUtil.getInstance(globalActivity).showToast(globalActivity.getString(R.string.Account_already_exists))
                        btnCreate.isEnabled = false
                    }
                    else {
                        fieldsValidator.validate()
                    }
                }

                (object : Thread() {
                    override fun run() {
                        /* Run thread*/
                        CryptoNetInfoRequests.getInstance().addRequest(request)
                    }
                }).start()
            }
        })
        questionDialog.show()
    }

    /*
    * Validate that all is complete to continue to create
    * */
    private fun validateFieldsToContinue() {

        var result = false //Contains the final result

        val pinValid: Boolean? = this.tietPin?.getFieldValidatorModel()?.isValid
        val pinConfirmationValid = this.tietPinConfirmation?.getFieldValidatorModel()?.isValid
        val pinAccountNameValid = this.tietAccountName?.getFieldValidatorModel()?.isValid

        if (pinValid!! &&
                pinConfirmationValid!! &&
                pinAccountNameValid!!) {
            result = true //Validation is correct
        }

        // If the result is true so the user can continue to the creation of the account
        btnCreate.isEnabled = result
    }
}