package com.digiparking.android.digiparking.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.digiparking.android.digiparking.CreationTicketActivity;
import com.digiparking.android.digiparking.R;
import com.digiparking.android.digiparking.TicketDetailActivity;
import com.digiparking.android.digiparking.modele.Ticket;
import com.digiparking.android.digiparking.util.TimerUtil;

/**
 * Created by milk1 on 4/7/2017.
 */

public class NotificationService extends Service {

    private PendingIntent contentIntent;
    private boolean isRunning = false;
    private NotificationManager mNotificationManager;
    CountDownTimer mCountDownTimer;
    private static NotificationService mNotificationService;
    private static final int NOTIFICATION_ID = 1;
    public static final String REFRESH_TIME_INTENT = "REFRESH_TIME_INTENT";
    public static final String KEY_EXTRA_LONG_TIME_IN_MS = "timeInMs";
    private static long currentMs;
    private Ticket mTicketEnCours;

    public NotificationService(){
        super();
        mNotificationService = this;

    }

    @Override
    public void onCreate() {

        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        super.onCreate();

    }

    @Override
    public void onDestroy() {
        if (null != mCountDownTimer) {
            mCountDownTimer.cancel();
            mNotificationManager.cancel(NOTIFICATION_ID);
        }
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mTicketEnCours = intent.getParcelableExtra(CreationTicketActivity.EXTRA_TICKET_OBJ);
        Intent backToDetail = new Intent(this, TicketDetailActivity.class);
        backToDetail.putExtra("activityFrom", "firstTicketClicked");
        backToDetail.putExtra(CreationTicketActivity.EXTRA_TICKET_OBJ, mTicketEnCours);
        backToDetail.putExtra("ETAT_TICKET", true);
        contentIntent = PendingIntent.getActivity(getApplicationContext(), 1, backToDetail, PendingIntent.FLAG_UPDATE_CURRENT);
        if (!isRunning){
            long timeInMs = intent.getLongExtra(KEY_EXTRA_LONG_TIME_IN_MS, 0L);
            startTimer(timeInMs);
        }else{
            mNotificationManager.cancel(NOTIFICATION_ID);
            NotificationService.this.stopSelf(startId);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void startTimer(long initTimerInMs) {
        mCountDownTimer = new CountDownTimer(initTimerInMs, 1000){

            @Override
            public void onTick(long millisUntilFinished) {
                isRunning = true;
                currentMs = millisUntilFinished;
                Log.i(NotificationService.class.getName(), "Time " + millisUntilFinished / 1000);
                showNotificationTime(millisUntilFinished);
                sendEvent(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                isRunning = false;
                sendEvent(0L);
                showNotification();
                Vibrator v = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(1000);
            }
        };

        mCountDownTimer.start();
    }

    private void sendEvent(long millisUntilFinished) {

        Intent intent = new Intent(REFRESH_TIME_INTENT);
        intent.setAction("com.digiparking.receiveBroadCast");
        intent.putExtra(KEY_EXTRA_LONG_TIME_IN_MS, millisUntilFinished);
        sendBroadcast(intent);
    }

    // notificaton end timer
    private void showNotification() {
        NotificationCompat.Builder notifBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_timer_finished)
                        .setContentTitle("Terminé")
                        .setContentIntent(contentIntent)
                        .setContentText("Temps écoulé !!!")
                        .setVibrate(new long[]{1000, 1000});
        mNotificationManager.notify(NOTIFICATION_ID, notifBuilder.build());
    }

    private void showNotificationTime(long timeInMs) {
        Integer[] minSec = TimerUtil.getMinSec(timeInMs);
        NotificationCompat.Builder notifBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.ic_timer)
                            .setContentTitle("Temps reste")
                            .setContentIntent(contentIntent)
                            .setContentText("Temps restant : " + getString(R.string.timeleft, minSec[TimerUtil.HOUR], minSec[TimerUtil.MINUTE], minSec[TimerUtil.SECOND]));

        mNotificationManager.notify(NOTIFICATION_ID, notifBuilder.build());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new Binder();
    }

    public static long getCurrentMs(){
        return currentMs;
    }

}
