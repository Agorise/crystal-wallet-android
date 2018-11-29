package cy.agorise.crystalwallet.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cy.agorise.crystalwallet.R;
import cy.agorise.crystalwallet.models.CryptoNetAccount;

/**
 * Created by Henry Varona on 01/20/2018.
 *
 * The adapter to show a list of crypto net account in a spinner.
 */

public class CryptoNetAccountAdapter extends ArrayAdapter<CryptoNetAccount> {
    private List<CryptoNetAccount> data;

    public CryptoNetAccountAdapter(Context context, int resource, List<CryptoNetAccount> objects) {
        super(context, resource, objects);
        this.data = objects;
    }

    @Override
    public CryptoNetAccount getItem(int position) {
        return data.get(position);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent);
    }

    /*
     * Creates the view for every element of the spinner
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.crypto_net_account_adapter_item, parent, false);
        TextView tvCryptoNetAccountName = v.findViewById(R.id.tvCryptoNetAccountName);
        ImageView ivCryptoNetIcon = v.findViewById(R.id.ivCryptoNetIcon);

        CryptoNetAccount cryptoNetAccount = getItem(position);
        tvCryptoNetAccountName.setText(cryptoNetAccount.getName());
        ivCryptoNetIcon.setImageResource(cryptoNetAccount.getCryptoNet().getIconImageResource());

        return v;
    }
}
