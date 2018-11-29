package cy.agorise.crystalwallet.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.TypeConverters;

import cy.agorise.crystalwallet.dao.converters.Converters;

/**
 * This represents the extens attributes of the Bitshares Assets, to be saved in the database
 * Created by henry on 8/10/2017.
 */
@Entity(tableName = "bitshares_asset",
        primaryKeys = {"crypto_curreny_id"},
        foreignKeys = @ForeignKey(entity = CryptoCurrency.class,
                parentColumns = "id",
                childColumns = "crypto_curreny_id"))
public class BitsharesAssetInfo {
    //The crypto Currency representing this bitshares asset
    @ColumnInfo(name = "crypto_curreny_id")
    private long cryptoCurrencyId;
    // The bitshares internal id
    @ColumnInfo(name = "bitshares_id")
    private String bitsharesId;
    // The bitshares type see the enum below
    @ColumnInfo(name = "asset_type")
    @TypeConverters(Converters.class)
    private BitsharesAsset.Type assetType;

    public BitsharesAssetInfo() {
    }

    public BitsharesAssetInfo(String symbol, int precision, String bitsharesId, BitsharesAsset.Type assetType) {
        this.bitsharesId = bitsharesId;
        this.assetType = assetType;
    }

    public BitsharesAssetInfo(BitsharesAsset asset){
        this.cryptoCurrencyId = asset.getId();
        this.bitsharesId = asset.getBitsharesId();
        this.assetType = asset.getAssetType();
    }

    public String getBitsharesId() {
        return bitsharesId;
    }

    public void setBitsharesId(String bitsharesId) {
        this.bitsharesId = bitsharesId;
    }

    public BitsharesAsset.Type getAssetType() {
        return assetType;
    }

    public void setAssetType(BitsharesAsset.Type assetType) {
        this.assetType = assetType;
    }

    public long getCryptoCurrencyId() {
        return cryptoCurrencyId;
    }

    public void setCryptoCurrencyId(long cryptoCurrencyId) {
        this.cryptoCurrencyId = cryptoCurrencyId;
    }
}
