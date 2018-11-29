package cy.agorise.crystalwallet.util;

import android.content.Context;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cy.agorise.crystalwallet.BuildConfig;
import cy.agorise.crystalwallet.R;

/**
 * Created by xd on 1/24/18.
 * Status bar that shows the app version, block number and connection status
 */

public class BottomStatusBar extends ConstraintLayout{

    @BindView(R.id.tvBuildVersion)
    TextView tvBuildVersion;

    @BindView(R.id.tvBlockNumber)
    TextView tvBlockNumber;

    @BindView(R.id.ivSocketConnected)
    ImageView ivSocketConnected;


    public BottomStatusBar(Context context) {
        super(context);
        init();
    }

    public BottomStatusBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        inflater.inflate(R.layout.bottom_status_bar, this);
        ButterKnife.bind(this);

        // Automatically get build version from app information
        String buildVersion = "v";
        buildVersion += BuildConfig.VERSION_NAME;
        tvBuildVersion.setText(buildVersion);

        // TODO update block number

        // TODO update socket connection status
    }
}
