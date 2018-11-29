package cy.agorise.crystalwallet.views;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import cy.agorise.crystalwallet.R;
import cy.agorise.crystalwallet.activities.AccountSeedSettingsActivity;
import cy.agorise.crystalwallet.models.AccountSeed;

/**
 * Created by Henry Varona on 17/9/2017.
 */

public class AccountSeedViewHolder extends RecyclerView.ViewHolder {
    private TextView tvAccountSeedName;
    private Context context;

    public AccountSeedViewHolder(View itemView) {
        super(itemView);
        tvAccountSeedName = (TextView) itemView.findViewById(R.id.tvAccountSeedName);
        context = itemView.getContext();
    }

    public void clear(){
        tvAccountSeedName.setText("loading...");
    }

    public void bindTo(final AccountSeed accountSeed) {
        if (accountSeed == null){
            tvAccountSeedName.setText("loading...");
        } else {
            tvAccountSeedName.setText(accountSeed.getName());

            tvAccountSeedName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, AccountSeedSettingsActivity.class);
                    intent.putExtra("SEED_ID", accountSeed.getId());
                    context.startActivity(intent);
                }
            });
        }
    }
}
