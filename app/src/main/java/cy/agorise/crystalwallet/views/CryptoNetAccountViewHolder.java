package cy.agorise.crystalwallet.views;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import cy.agorise.crystalwallet.R;
import cy.agorise.crystalwallet.activities.CryptoNetAccountSettingsActivity;
import cy.agorise.crystalwallet.models.CryptoNetAccount;

/**
 * Created by Henry Varona on 17/9/2017.
 */

public class CryptoNetAccountViewHolder extends RecyclerView.ViewHolder {
    private TextView tvCryptoNetAccountName;
    private Context context;

    public CryptoNetAccountViewHolder(View itemView) {
        super(itemView);
        tvCryptoNetAccountName = (TextView) itemView.findViewById(R.id.tvCryptoNetAccountName);
        context = itemView.getContext();
    }

    public void clear(){
        tvCryptoNetAccountName.setText("loading...");
    }

    public void bindTo(final CryptoNetAccount cryptoNetAccount) {
        if (cryptoNetAccount == null){
            tvCryptoNetAccountName.setText("loading...");
        } else {
            tvCryptoNetAccountName.setText(cryptoNetAccount.getName());

            tvCryptoNetAccountName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, CryptoNetAccountSettingsActivity.class);
                    intent.putExtra("CRYPTO_NET_ACCOUNT_ID", cryptoNetAccount.getId());
                    context.startActivity(intent);
                }
            });
        }
    }
}
