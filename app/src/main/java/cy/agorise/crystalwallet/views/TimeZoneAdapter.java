package cy.agorise.crystalwallet.views;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import cy.agorise.crystalwallet.R;

/**
 * Created by Henry Varona on 21/3/2018.
 */

public class TimeZoneAdapter extends ArrayAdapter<String> {

    LayoutInflater inflater;
    String[] timeZoneIds;

    public TimeZoneAdapter(@NonNull Context context, int resource) {
        super(context, resource);

        this.inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.timeZoneIds = TimeZone.getAvailableIDs();
    }

    @Override
    public int getCount() {
        return this.timeZoneIds.length;
    }

    @Override
    public int getPosition(@Nullable String item) {
        for (int i=0;i<this.timeZoneIds.length;i++){
            if (this.timeZoneIds[i].equals(item)){
                return i;
            }
        }

        return -1;
    }

    @Override
    public String getItem(int position) {
        return timeZoneIds[position];
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String timeZoneId = getItem(position);
        TimeZone nextTimeZone = TimeZone.getTimeZone(timeZoneId);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(nextTimeZone);
        long hours = TimeUnit.MILLISECONDS.toHours(nextTimeZone.getRawOffset());
        long minutes = TimeUnit.MILLISECONDS.toMinutes(nextTimeZone.getRawOffset())
                - TimeUnit.HOURS.toMinutes(hours);
        minutes = Math.abs(minutes);
        String hoursString = (hours > 0?"+"+hours:""+hours);

        View timeZoneItemView = this.inflater.inflate(R.layout.time_zone_spinner_item,null,true);

        TextView tvTimeZoneLabel = (TextView) timeZoneItemView.findViewById(R.id.tvTimeZoneLabel);
        tvTimeZoneLabel.setText(String.format("%s (GMT%s:%02d)", nextTimeZone.getID(), hoursString, minutes));
        return timeZoneItemView;
    }
}
