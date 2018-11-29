package cy.agorise.crystalwallet.views;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import cy.agorise.crystalwallet.R;
import cy.agorise.crystalwallet.enums.CryptoNet;
import cy.agorise.crystalwallet.models.ContactAddress;

/**
 * Created by Henry Varona on 2/5/2017.
 *
 * Represents an element view from the Contact Address List
 */

public class ContactAddressViewHolder extends RecyclerView.ViewHolder {
    private Spinner spCryptoNet;
    private EditText etAddress;
    private Context context;
    private CryptoNet[] cryptoNetArray;
    private ArrayAdapter<CryptoNet> cryptoNetSpinnerAdapter;

    public ContactAddressViewHolder(View itemView) {
        super(itemView);
        //TODO: use ButterKnife to load this
        spCryptoNet = (Spinner) itemView.findViewById(R.id.spCryptoNet);
        etAddress = (EditText) itemView.findViewById(R.id.etAddress);
        this.context = itemView.getContext();


        //load spinners values
        cryptoNetArray = CryptoNet.values();
        cryptoNetSpinnerAdapter = new ArrayAdapter<CryptoNet>(
                this.context,
                android.R.layout.simple_list_item_1,
                cryptoNetArray
        );
        spCryptoNet.setAdapter(cryptoNetSpinnerAdapter);
    }

    /*
     * Clears the information in this element view
     */
    public void clear(){
        spCryptoNet.setSelection(0);
        etAddress.setText("");
    }

    /*
     * Binds this view with the data of an element of the list
     */
    public void bindTo(final ContactAddress contactAddress) {
        if (contactAddress == null){
            this.clear();
        } else {
            etAddress.setText(contactAddress.getAddress());

            CryptoNet nextCryptoNet;
            for (int i=0;i<cryptoNetArray.length;i++){
                nextCryptoNet = cryptoNetArray[i];
                if (nextCryptoNet.equals(contactAddress.getCryptoNet())){
                    spCryptoNet.setSelection(i);
                    break;
                }
            }


            spCryptoNet.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    contactAddress.setCryptoNet(((CryptoNet)spCryptoNet.getSelectedItem()));
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    //
                }
            });
            etAddress.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    //
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    //
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    contactAddress.setAddress(editable.toString());
                }
            });
            //etAddress.setText(contactAddress.getAddress());
        }
    }
}
