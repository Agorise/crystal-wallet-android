package cy.agorise.crystalwallet.fragments;

import android.Manifest;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cy.agorise.crystalwallet.R;
import cy.agorise.crystalwallet.activities.BackupSeedActivity;
import cy.agorise.crystalwallet.activities.IntroActivity;
import cy.agorise.crystalwallet.dao.CrystalDatabase;
import cy.agorise.crystalwallet.models.GeneralSetting;
import cy.agorise.crystalwallet.requestmanagers.CreateBackupRequest;
import cy.agorise.crystalwallet.requestmanagers.FileServiceRequest;
import cy.agorise.crystalwallet.requestmanagers.FileServiceRequestListener;
import cy.agorise.crystalwallet.requestmanagers.FileServiceRequests;

/**
 * Created by xd on 1/11/18.
 */

public class BackupsSettingsFragment extends Fragment{

    private static final int PERMISSION_REQUEST_CODE = 1;



    public BackupsSettingsFragment() {
        // Required empty public constructor
    }

    public static BackupsSettingsFragment newInstance() {
        BackupsSettingsFragment fragment = new BackupsSettingsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @BindView(R.id.btnBrainkey)
    public Button btnBrainkey;

    @BindView(R.id.tvBinFile)
    public TextView tvBinFile;

    @BindView(R.id.tvBrainkey)
    public TextView tvBrainkey;

    @BindView(R.id.tvWIFKey)
    public TextView tvWIFKey;

    @BindView(R.id.btnBinFile)
    public Button btnBinFile;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_backups_settings, container, false);
        ButterKnife.bind(this, v);

        tvBinFile.setText(makeFirstWordsBold(getResources().getString(R.string.bin_file_description)));
        tvBrainkey.setText(makeFirstWordsBold(getResources().getString(R.string.brainkey_description)));
        tvWIFKey.setText(makeFirstWordsBold(getResources().getString(R.string.wif_key_description)));

        return v;
    }

    private SpannableStringBuilder makeFirstWordsBold(String str) {
        SpannableStringBuilder ssb = new SpannableStringBuilder(str);
        ssb.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD),
                0, str.indexOf('.')+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return ssb;
    }


    @OnClick(R.id.btnBrainkey)
    public void btnBrainOnClick(){

        Intent intent = new Intent(getContext(), BackupSeedActivity.class);
        intent.putExtra("SEED_ID","");
        startActivity(intent);
    }

    @OnClick(R.id.btnBinFile)
    public void makeBackupFile(){

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

            LiveData<GeneralSetting> generalSettingLD = CrystalDatabase.getAppDatabase(getContext()).generalSettingDao().getByName(GeneralSetting.SETTING_PASSWORD);

            generalSettingLD.observe(this, new Observer<GeneralSetting>() {
                @Override
                public void onChanged(@Nullable GeneralSetting generalSetting) {

                    String password = "";
                    if (generalSetting != null) {
                        password = generalSetting.getValue();
                    }

                    final CreateBackupRequest backupFileRequest = new CreateBackupRequest(getContext(), password);
                    backupFileRequest.setListener(new FileServiceRequestListener() {
                        @Override
                        public void onCarryOut() {
                            if (backupFileRequest.getStatus() == CreateBackupRequest.StatusCode.SUCCEEDED){
                                Toast toast = Toast.makeText(
                                        getContext(), "Backup done! File: "+backupFileRequest.getFilePath(), Toast.LENGTH_LONG);
                                toast.show();
                            } else if (backupFileRequest.getStatus() == CreateBackupRequest.StatusCode.FAILED){
                                Toast toast = Toast.makeText(
                                        getContext(), "An error ocurred while making the backup!", Toast.LENGTH_LONG);
                                toast.show();
                            }
                        }
                    });

                    FileServiceRequests.getInstance().addRequest(backupFileRequest);
                }
            });
        }
    }
}
