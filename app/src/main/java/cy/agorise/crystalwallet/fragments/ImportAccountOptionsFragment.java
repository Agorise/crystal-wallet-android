package cy.agorise.crystalwallet.fragments;

import android.app.Activity;
import android.app.Dialog;
import androidx.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.thekhaeng.pushdownanim.PushDownAnim;
import com.vincent.filepicker.ToastUtil;

import java.net.URISyntaxException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cy.agorise.crystalwallet.R;
import cy.agorise.crystalwallet.activities.BoardActivity;
import cy.agorise.crystalwallet.activities.ImportSeedActivity;
import cy.agorise.crystalwallet.dialogs.material.CrystalDialog;
import cy.agorise.crystalwallet.requestmanagers.FileServiceRequestListener;
import cy.agorise.crystalwallet.requestmanagers.FileServiceRequests;
import cy.agorise.crystalwallet.requestmanagers.ImportBackupRequest;
import cy.agorise.crystalwallet.util.UriTranslator;
import cy.agorise.crystalwallet.viewmodels.AccountSeedListViewModel;

import static android.app.Activity.RESULT_OK;

/**
 * Created by xd on 1/25/18.
 * Shows a dialog where the user can select how to import his/her existing account
 */

public class ImportAccountOptionsFragment extends DialogFragment {

    public static final int FILE_CONTENT_REQUEST_CODE = 0;

    @BindView(R.id.btnCancel)
    Button btnClose;
    @BindView(R.id.btnImportBackup)
    Button btnImportBackup;
    @BindView(R.id.btnImportSeed)
    Button btnImportSeed;

    private static final int PERMISSION_REQUEST_CODE = 1;

    /*
        Dialog for loading
    */
    private CrystalDialog crystalDialog;

    /*
    * Contains the activity to close in case import succed
    * */
    private Activity introActivity;





    public ImportAccountOptionsFragment() {
        // Required empty public constructor
    }

    public static ImportAccountOptionsFragment newInstance() {
        ImportAccountOptionsFragment fragment = new ImportAccountOptionsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_import_account_options, null);
        ButterKnife.bind(this, view);

        /*
        *   Integration of library with button efects
        * */
        PushDownAnim.setPushDownAnimTo(btnClose)
        .setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick( View view ){
                cancel();
            }

        } );
        PushDownAnim.setPushDownAnimTo(btnImportBackup)
                .setOnClickListener( new View.OnClickListener(){
                    @Override
                    public void onClick( View view ){
                        importBackup();
                    }

                } );
        PushDownAnim.setPushDownAnimTo(btnImportSeed)
                .setOnClickListener( new View.OnClickListener(){
                    @Override
                    public void onClick( View view ){
                        importSeed();
                    }

                } );

        return builder.setView(view).create();
    }

    @Override
    public void onResume() {
        super.onResume();

        // Force dialog fragment to use the full width of the screen
        Window dialogWindow = getDialog().getWindow();
        assert dialogWindow != null;
        dialogWindow.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @OnClick(R.id.btnCancel)
    public void cancel() {
        dismiss();
    }

    @OnClick (R.id.btnImportBackup)
    public void importBackup(){

        if (Build.VERSION.SDK_INT >= 23) {

            if (checkPermission()) {

                Intent fileIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                fileIntent.setType("*/*");
                fileIntent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(fileIntent, FILE_CONTENT_REQUEST_CODE);

            } else {
                requestPermission(); // Code for permission
            }
        }
        else {

            Intent fileIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            fileIntent.setType("*/*");
            fileIntent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(fileIntent, FILE_CONTENT_REQUEST_CODE);
        }
    }

    @OnClick (R.id.btnImportSeed)
    public void importSeed(){

        if (Build.VERSION.SDK_INT >= 23) {

            if (checkPermission()) {

                Intent intent = new Intent(this.getActivity(), ImportSeedActivity.class);
                startActivity(intent);

            } else {
                requestPermission(); // Code for permission
            }
        }
        else {
            Intent intent = new Intent(this.getActivity(), ImportSeedActivity.class);
            startActivity(intent);
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

        Log.i("log", "requestPermission() entered");

        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.Permision_storage), Toast.LENGTH_LONG).show();
        } else {
           // ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            requestPermissions(new String[] {android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Intent intent = new Intent(getActivity(), ImportSeedActivity.class);
                    startActivity(intent);

                } else {
                    ToastUtil.getInstance(getActivity()).showToast(getActivity().getString(R.string.Permission_Denied_WRITE_EXTERNAL_STORAGE));
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == FILE_CONTENT_REQUEST_CODE) && (resultCode == RESULT_OK)){
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View passwordDialogView = inflater.inflate(R.layout.dialog_password_input, null);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
            alertDialogBuilder.setView(passwordDialogView);

            final EditText passwordInput = (EditText) passwordDialogView.findViewById(R.id.etPasswordInput);

            alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    String passwordString = (passwordInput.getText()).toString();

                                    Uri fileUri = data.getData();

                                    String filePath = null;
                                    try {
                                        filePath = UriTranslator.getFilePath(getContext(), fileUri);
                                    } catch (URISyntaxException e) {
                                        e.printStackTrace();
                                    }

                                    final ImportBackupRequest importBackupRequest = new ImportBackupRequest(getContext(), passwordString, filePath);

                                    importBackupRequest.setListener(new FileServiceRequestListener() {
                                        @Override
                                        public void onCarryOut() {
                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if (importBackupRequest.getStatus() == ImportBackupRequest.StatusCode.SUCCEEDED) {

                                                        //Checks if the user has any seed created
                                                        AccountSeedListViewModel accountSeedListViewModel = ViewModelProviders.of((FragmentActivity) getContext()).get(AccountSeedListViewModel.class);

                                                        if(introActivity!=null){
                                                            introActivity.finish();
                                                        }

                                                        Intent intent = new Intent(getContext(), BoardActivity.class);
                                                        startActivity(intent);
                                                        dismiss();

                                                        /*
                                                         * Hide the loading dialog
                                                         * */
                                                        crystalDialog.dismiss();

                                                    } else if (importBackupRequest.getStatus() == ImportBackupRequest.StatusCode.FAILED) {

                                                        /*
                                                         * Hide the loading dialog
                                                         * */
                                                        crystalDialog.dismiss();

                                                        Toast toast = Toast.makeText(
                                                                getContext(), "An error ocurred while restoring the backup!", Toast.LENGTH_LONG);
                                                        toast.show();
                                                    }
                                                }
                                            });
                                        }
                                    });

                                    /*
                                     * Show loading dialog
                                     * */
                                    crystalDialog = new CrystalDialog((Activity) getContext());
                                    crystalDialog.setText(getContext().getString(R.string.Restoring_backup_from_file));
                                    crystalDialog.progress();
                                    crystalDialog.show();

                                    FileServiceRequests.getInstance().addRequest(importBackupRequest);
                                }
                            })
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    dialog.cancel();
                                }
                            });

            AlertDialog passwordDialog = alertDialogBuilder.create();
            passwordDialog.show();

        }
    }


    public void setIntroActivity(Activity introActivity) {
        this.introActivity = introActivity;
    }
}
