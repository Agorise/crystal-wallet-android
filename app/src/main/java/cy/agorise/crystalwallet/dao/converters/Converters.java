package cy.agorise.crystalwallet.dao.converters;

import androidx.room.TypeConverter;

import java.util.Date;

import cy.agorise.crystalwallet.enums.CryptoCoin;
import cy.agorise.crystalwallet.enums.CryptoNet;
import cy.agorise.crystalwallet.enums.SeedType;
import cy.agorise.crystalwallet.models.BitsharesAsset;
import cy.agorise.crystalwallet.models.CryptoNetAccount;

/**
 * Created by Henry Varona on 13/9/2017.
 */

public class Converters {
    @TypeConverter
    public Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public Long dateToTimestamp(Date date) {
        if (date == null) {
            return null;
        } else {
            return date.getTime();
        }
    }

    @TypeConverter
    public long cryptoNetAccountToId(CryptoNetAccount account) {
        if (account == null) {
            return -1;
        } else {
            return account.getId();
        }
    }

    @TypeConverter
    public CryptoNetAccount fromCryptoNetAccountId(long value) {
        if (value == -1){
            return null;
        } else {
            CryptoNetAccount account = new CryptoNetAccount();
            account.setId(value);
            return account;
        }
    }

    @TypeConverter
    public String cryptoCoinToName(CryptoCoin coin) {
        if (coin == null) {
            return "";
        } else {
            return coin.name();
        }
    }

    @TypeConverter
    public CryptoCoin nameToCryptoCoin(String value) {
        if (value.equals("")){
            return null;
        } else {
            return CryptoCoin.valueOf(value);
        }
    }

    @TypeConverter
    public String cryptoNetToName(CryptoNet net){
        if (net == null) {
            return "";
        } else {
            return net.name();
        }
    }

    @TypeConverter
    public CryptoNet nameToCryptoNet(String value) {
        if (value.equals("")){
            return null;
        } else {
            return CryptoNet.valueOf(value);
        }
    }

    @TypeConverter
    public String seedTypeToName(SeedType value) {
        if (value == null) {
            return "";
        } else {
            return value.name();
        }
    }

    @TypeConverter
    public SeedType nameToSeedType(String value) {
        if (value.equals("")){
            return null;
        } else {
            return SeedType.valueOf(value);
        }
    }

    @TypeConverter
    public int cryptoNetToAccountNumber(CryptoNet value) {
        if (value == null) {
            return -1;
        } else {
            return value.getBip44Index();
        }
    }

    @TypeConverter
    public CryptoNet accountNumberToCryptoNet(int value) {
        return CryptoNet.fromBip44Index(value);
    }

    @TypeConverter
    public String assetTypeToName(BitsharesAsset.Type type){
        if(type == null){
            return "";
        }
        return type.name();
    }

    @TypeConverter
    public BitsharesAsset.Type nameToAssetType(String value){
        return BitsharesAsset.Type.valueOf(value);
    }
}
