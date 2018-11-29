package cy.agorise.crystalwallet.activities;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.FileObserver;

import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.thekhaeng.pushdownanim.PushDownAnim;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cy.agorise.crystalwallet.R;
import cy.agorise.crystalwallet.models.AccountSeed;
import cy.agorise.crystalwallet.viewmodels.AccountSeedListViewModel;
import cy.agorise.crystalwallet.views.AccountSeedListView;
import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

/**
 * Created by Seven on 1/9/18.
 *
 */

public class AccountsActivity extends AppCompatActivity {

    @BindView(R.id.tvSettings)
    TextView tvSettings;

    @BindView(R.id.tvClose)
    TextView tvClose;

    //@BindView(R.id.vAccountList)
    //CryptoNetAccountListView vAccountList;

    @BindView(R.id.vAccountSeedList)
    AccountSeedListView vAccountSeedList;

    @BindView(R.id.user_img)
    CircleImageView userImg;

    @BindView(R.id.fabAddAccount)
    FloatingActionButton fabAddAccount;

    static final int NEW_PICTURE_REQUEST_CODE = 1;

    FileObserver fileObserver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounts);
        ButterKnife.bind(this);

        /*
         *   Integration of library with button efects
         * */
        PushDownAnim.setPushDownAnimTo(fabAddAccount)
                .setOnClickListener( new View.OnClickListener(){
                    @Override
                    public void onClick( View view ){
                        goToAddAccount();
                    }

                } );

        AccountSeedListViewModel accountSeedListViewModel = ViewModelProviders.of(this).get(AccountSeedListViewModel.class);
        LiveData<List<AccountSeed>> accountSeedLD = accountSeedListViewModel.getAccountSeedList();

        //CryptoNetAccountListViewModel crytpoNetAccountListViewModel = ViewModelProviders.of(this).get(CryptoNetAccountListViewModel.class);
        //LiveData<List<CryptoNetAccount>> accountData = crytpoNetAccountListViewModel.getCryptoNetAccounts();
        vAccountSeedList.setData(null);

        accountSeedLD.observe(this, new Observer<List<AccountSeed>>() {
            @Override
            public void onChanged(List<AccountSeed> accountsSeeds) {
                vAccountSeedList.setData(accountsSeeds);
            }
        });

        loadUserImage();
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadUserImage();
    }

    public void loadUserImage(){
        //Search for a existing photo
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("profile", Context.MODE_PRIVATE);
        if (!directory.exists()) {
            directory.mkdir();
        }
        File photoFile = new File(directory + File.separator + "photo.png");

        if (photoFile.exists()){
            Bitmap bitmap = BitmapFactory.decodeFile(photoFile.getPath());
            userImg.setImageBitmap(bitmap);
        }
    }

    @OnClick(R.id.tvSettings)
    public void onTvSettingsClick(){
        onBackPressed();
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.tvClose)
    public void cancel(){
        onBackPressed();
    }

    @OnClick(R.id.user_img)
    public void loadUserImageSetting(){
        ImagePicker.create(this)
                .single()
                .start();
    }

    @OnClick(R.id.fabAddAccount)
    public void goToAddAccount(){
        Intent intent = new Intent(this, CreateSeedActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            // Get a list of picked images
            List<Image> images = ImagePicker.getImages(data);
            // or get a single image only
            Image image = ImagePicker.getFirstImageOrNull(data);

            if (image != null){
                ContextWrapper cw = new ContextWrapper(getApplicationContext());
                File directory = cw.getDir("profile", Context.MODE_PRIVATE);
                if (!directory.exists()) {
                    directory.mkdir();
                }

                try {
                    File imageCompressed = new Compressor(this)
                            .setCompressFormat(Bitmap.CompressFormat.PNG)
                            .setMaxWidth(320)
                            .setMaxHeight(480)
                            .compressToFile(new File(image.getPath()));
                    imageCompressed.renameTo(new File(directory + File.separator + "photo.png"));


                    if (imageCompressed.exists()){
                        imageCompressed.delete();
                    }
                    imageCompressed.createNewFile();
                    //File inputPngFile = new File(image.getPath());
                    //File outputPngFile = new File("photo.png");
                    //new LibPngQuant().pngQuantFile(inputPngFile, outputPngFile);

                    //Bitmap bitmapDecoded= BitmapFactory.decodeFile(image.getPath());
                    //Bitmap bitmapResized = Bitmap.createScaledBitmap(bitmapDecoded, 320, 480, false);
                    //resizedbitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    //fos.close();
                } catch (Exception e) {
                    //Log.e("SAVE_IMAGE", e.getMessage(), e);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}