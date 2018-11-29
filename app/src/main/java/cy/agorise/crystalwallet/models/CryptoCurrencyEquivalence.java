package cy.agorise.crystalwallet.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;

/**
 * Created by Henry Varona on 1/11/2017.
 */

@Entity(
    tableName="crypto_currency_equivalence",
    indices = {
        @Index(value = {"from_crypto_currency_id","to_crypto_currency_id"}, unique=true),
        @Index(value = {"from_crypto_currency_id"}),
        @Index(value = {"to_crypto_currency_id"}),
    },
    foreignKeys = {
        @ForeignKey(
            entity = CryptoCurrency.class,
            parentColumns = "id",
            childColumns = "from_crypto_currency_id",
            onDelete = ForeignKey.CASCADE
        ),
        @ForeignKey(
            entity = CryptoCurrency.class,
            parentColumns = "id",
            childColumns = "to_crypto_currency_id",
            onDelete = ForeignKey.CASCADE
        )
    }
)
public class CryptoCurrencyEquivalence {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long id;
    @ColumnInfo(name="from_crypto_currency_id")
    private long fromCurrencyId;
    @ColumnInfo(name="to_crypto_currency_id")
    private long toCurrencyId;
    @ColumnInfo(name="value")
    private int value;
    @ColumnInfo(name="last_checked")
    private Date lastChecked;

    public CryptoCurrencyEquivalence(long fromCurrencyId, long toCurrencyId, int value, Date lastChecked){
        this.fromCurrencyId = fromCurrencyId;
        this.toCurrencyId = toCurrencyId;
        this.value = value;
        this.lastChecked = lastChecked;
    }

    public long getId(){
        return this.id;
    }

    public void setId(long id){
        this.id = id;
    }

    public long getFromCurrencyId() {
        return fromCurrencyId;
    }

    public void setFromCurrencyId(long fromCurrencyId) {
        this.fromCurrencyId = fromCurrencyId;
    }

    public long getToCurrencyId() {
        return toCurrencyId;
    }

    public void setToCurrencyId(long toCurrencyId) {
        this.toCurrencyId = toCurrencyId;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public Date getLastChecked() {
        return lastChecked;
    }

    public void setLastChecked(Date lastChecked) {
        this.lastChecked = lastChecked;
    }
}
