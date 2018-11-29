package cy.agorise.crystalwallet.fragments;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.thekhaeng.pushdownanim.PushDownAnim;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cy.agorise.crystalwallet.R;
import cy.agorise.crystalwallet.activities.BackupSeedActivity;
import cy.agorise.crystalwallet.dao.CrystalDatabase;
import cy.agorise.crystalwallet.models.GeneralSetting;
import cy.agorise.crystalwallet.requestmanagers.CreateBackupRequest;
import cy.agorise.crystalwallet.requestmanagers.FileServiceRequestListener;
import cy.agorise.crystalwallet.requestmanagers.FileServiceRequests;

/**
 * Created by Seven on 1/11/18.
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

        /*
         *   Integration of library with button efects
         * */
        PushDownAnim.setPushDownAnimTo(btnBinFile)
                .setOnClickListener( new View.OnClickListener(){
                    @Override
                    public void onClick( View view ){
                        makeBackupFile();
                    }

                } );
        PushDownAnim.setPushDownAnimTo(btnBrainkey)
                .setOnClickListener( new View.OnClickListener(){
                    @Override
                    public void onClick( View view ){
                        btnBrainOnClick();
                    }
                } );

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
        intent. putExtra("SEED_ID","");
        startActivity(intent);
    }


    @OnClick(R.id.btnBinFile)
    public void makeBackupFile(){

        /*
        * Check for WRITE_EXTERNAL_STORAGE permission
        * */
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkPermission()) {
                // Code for above or equal 23 API Oriented Device
                // Your Permission granted already .Do next code

                makeBackupfileAfterPermission();

            } else {
                requestPermission(); // Code for permission
            }
        }
        else {

            // Code for Below 23 API Oriented Device
            // Do next code

            makeBackupfileAfterPermission();
        }
    }


    private void makeBackupfileAfterPermission(){

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

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.Permision_storage), Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Now you can use local drive .");

                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");

                    makeBackupfileAfterPermission();
                }
                break;
        }
    }
}
