package cy.agorise.crystalwallet.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.net.URISyntaxException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cy.agorise.crystalwallet.R;
import cy.agorise.crystalwallet.activities.ImportSeedActivity;
import cy.agorise.crystalwallet.activities.IntroActivity;
import cy.agorise.crystalwallet.requestmanagers.FileServiceRequestListener;
import cy.agorise.crystalwallet.requestmanagers.FileServiceRequests;
import cy.agorise.crystalwallet.requestmanagers.ImportBackupRequest;
import cy.agorise.crystalwallet.util.UriTranslator;

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
        Intent fileIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        fileIntent.setType("*/*");
        fileIntent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(fileIntent, FILE_CONTENT_REQUEST_CODE);


    }

    @OnClick (R.id.btnImportSeed)
    public void importSeed(){
        Intent intent = new Intent(this.getActivity(), ImportSeedActivity.class);
        startActivity(intent);
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
                                                        Toast toast = Toast.makeText(
                                                                getContext(), "Backup restored!", Toast.LENGTH_LONG);
                                                        toast.show();

                                                        Intent intent = new Intent(getContext(), IntroActivity.class);
                                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                        startActivity(intent);
                                                    } else if (importBackupRequest.getStatus() == ImportBackupRequest.StatusCode.FAILED) {
                                                        Toast toast = Toast.makeText(
                                                                getContext(), "An error ocurred while restoring the backup!", Toast.LENGTH_LONG);
                                                        toast.show();
                                                    }
                                                }
                                            });
                                        }
                                    });

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
}
