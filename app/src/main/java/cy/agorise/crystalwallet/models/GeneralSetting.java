package cy.agorise.crystalwallet.models;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

/**
 * Created by Henry Varona on 6/11/2017.
 */
@Entity(tableName = "general_setting")
public class GeneralSetting {

    public final static String SETTING_NAME_PREFERRED_COUNTRY = "PREFERRED_COUNTRY";
    public final static String SETTING_NAME_PREFERRED_CURRENCY = "PREFERRED_CURRENCY";
    public final static String SETTING_NAME_PREFERRED_LANGUAGE = "PREFERRED_LANGUAGE";
    public final static String SETTING_NAME_TIME_ZONE = "TIME_ZONE";
    public final static String SETTING_PASSWORD = "PASSWORD";
    public final static String SETTING_PATTERN = "PATTERN";
    public final static String SETTING_NAME_RECEIVED_FUNDS_SOUND_PATH = "RECEIVED_FUNDS_SOUND_PATH";
    public final static String SETTING_LAST_LICENSE_READ = "LAST_LICENSE_READ";
    public final static String SETTING_YUBIKEY_OATH_TOTP_NAME = "YUBIKEY_OATH_TOTP_NAME";
    public final static String SETTING_YUBIKEY_OATH_TOTP_PASSWORD = "YUBIKEY_OATH_TOTP_PASSWORD";

    /**
     * The id on the database
     */
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long mId;

    /**
     * The name of this setting
     */
    @ColumnInfo(name = "name")
    private String mName;

    /**
     * The value of this setting
     */
    @ColumnInfo(name = "value")
    private String mValue;

    public long getId() {
        return mId;
    }

    public void setId(long id){
        this.mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public String getValue() {
        return mValue;
    }

    public void setValue(String mValue) {
        this.mValue = mValue;
    }

    public static final DiffUtil.ItemCallback<GeneralSetting> DIFF_CALLBACK = new DiffUtil.ItemCallback<GeneralSetting>() {
        @Override
        public boolean areItemsTheSame(
                @NonNull GeneralSetting oldSetting, @NonNull GeneralSetting newSetting) {
            return oldSetting.getId() == newSetting.getId();
        }
        @Override
        public boolean areContentsTheSame(
                @NonNull GeneralSetting oldSetting, @NonNull GeneralSetting newSetting) {
            return oldSetting.equals(newSetting);
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GeneralSetting that = (GeneralSetting) o;

        if (mId != that.mId) return false;
        if (mName != that.mName) return false;
        return  mValue.equals(that.mValue);

    }
}
