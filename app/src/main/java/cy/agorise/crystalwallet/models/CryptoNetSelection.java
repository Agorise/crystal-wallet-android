package cy.agorise.crystalwallet.models;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import cy.agorise.crystalwallet.enums.CryptoNet;

public class CryptoNetSelection {
    CryptoNet cryptoNet;
    Boolean selected;

    public CryptoNetSelection(CryptoNet cryptoNet, Boolean selected) {
        this.cryptoNet = cryptoNet;
        this.selected = selected;
    }

    public CryptoNet getCryptoNet() {
        return cryptoNet;
    }

    public void setCryptoNet(CryptoNet cryptoNet) {
        this.cryptoNet = cryptoNet;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public static final DiffUtil.ItemCallback<CryptoNetSelection> DIFF_CALLBACK = new DiffUtil.ItemCallback<CryptoNetSelection>() {
        @Override
        public boolean areItemsTheSame(
                @NonNull CryptoNetSelection oldCryptoNetSelection, @NonNull CryptoNetSelection newCryptoNetSelection) {
            return oldCryptoNetSelection.getCryptoNet() == newCryptoNetSelection.getCryptoNet();
        }
        @Override
        public boolean areContentsTheSame(
                @NonNull CryptoNetSelection oldCryptoNetSelection, @NonNull CryptoNetSelection newCryptoNetSelection) {
            return oldCryptoNetSelection.equals(newCryptoNetSelection);
        }
    };
}
