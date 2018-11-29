package cy.agorise.crystalwallet.views;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;

import cy.agorise.crystalwallet.R;
import cy.agorise.crystalwallet.models.CryptoNetSelection;

/**
 * Created by Henry Varona on 10/30/2018.
 *
 */

public class CryptoNetSelectionListAdapter extends ListAdapter<CryptoNetSelection, CryptoNetSelectionViewHolder> {

    private ArrayList<CryptoNetSelection> cryptoNetSelections = new ArrayList<CryptoNetSelection>();
    private ArrayList<CryptoNetSelectionListener> listeners = new ArrayList<>();

    public CryptoNetSelectionListAdapter(ArrayList<CryptoNetSelection> cryptoNetSelections) {
        super(CryptoNetSelection.DIFF_CALLBACK);
        this.cryptoNetSelections = cryptoNetSelections;
    }

    public CryptoNetSelection findCryptoNetSelectionByName(String name){
        for(CryptoNetSelection nextCryptoNetSelection : this.cryptoNetSelections){
            if (nextCryptoNetSelection.getCryptoNet().getLabel().equals(name)){
                return nextCryptoNetSelection;
            }
        }

        return null;
    }

    @NonNull
    @Override
    public CryptoNetSelectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.crypto_net_selection_item,parent,false);
        final CheckBox cbCryptoNetSelected = v.findViewById(R.id.cbCryptoNetSelected);
        final TextView tvCryptoNetName = v.findViewById(R.id.tvCryptoNetName);

        cbCryptoNetSelected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                CryptoNetSelection cryptoNetSelection = findCryptoNetSelectionByName(tvCryptoNetName.getText().toString());
                cryptoNetSelection.setSelected(b);
                _fireOnChecked(cryptoNetSelection);
            }
        });


        return new CryptoNetSelectionViewHolder(v);
    }

    @Override
    public int getItemCount() {
        return cryptoNetSelections.size();
    }

    @Override
    public void onBindViewHolder(@NonNull CryptoNetSelectionViewHolder holder, int position) {
        holder.bindTo(cryptoNetSelections.get(position));
    }

    public void addListener(CryptoNetSelectionListener listener){
        this.listeners.add(listener);
    }

    public void _fireOnChecked(CryptoNetSelection selection){
        for(CryptoNetSelectionListener listener:this.listeners){
            listener.onCryptoNetSelectionChecked(selection);
        }
    }

    public interface CryptoNetSelectionListener{
        public void onCryptoNetSelectionChecked(CryptoNetSelection source);
    }
}
