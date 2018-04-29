package com.digiparking.android.digiparking.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.widget.TextView;

import com.digiparking.android.digiparking.R;
import com.digiparking.android.digiparking.service.NotificationService;

/**
 * Created by milk1 on 4/17/2017.
 */

public class CountDownReceiver extends BroadcastReceiver {
    private TextView timer;
    private Context mContext;

    public CountDownReceiver(Context context, TextView v){
        timer = v;
        mContext = context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        long time = intent.getLongExtra(NotificationService.KEY_EXTRA_LONG_TIME_IN_MS, -1);

        if (time != -1) {
            Integer[] duree = TimerUtil.getMinSec(time);
            timer.setText(mContext.getString(R.string.timeleft, duree[TimerUtil.HOUR], duree[TimerUtil.MINUTE], duree[TimerUtil.SECOND]));
            if (duree[TimerUtil.HOUR] == 0 && duree[TimerUtil.MINUTE] <= 5) {
                timer.setTextColor(Color.RED);
            }
        }
    }

}
