package org.sipdroid.sipua.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import org.sipdroid.sipua.ui.MainActivity;
import org.sipdroid.sipua.R;


public class Alarm extends BroadcastReceiver {


//    NotificationManager manager;
//    NotificationCompat.Builder builder;
//
    //오레오 이상은 반드시 채널을 설정해줘야 Notification이 작동함
    private static final int NOTIFICATIONID=0;
    private static final String CHANNEL_ID = "IdRestart";
    private static final String CHANNEL_NAME = "NameRestart";
    private NotificationManager notificationManager;
    private NotificationCompat.Builder notifyBuilder;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("Alarm","알람입니다.");    // 로그 확인용

        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,CHANNEL_NAME,notificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("Notification from Mascot");
            // Manager을 이용하여 Channel 생성
            notificationManager.createNotificationChannel(notificationChannel);
        }
        notifyBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("AUTO_CALL")
                .setContentText("앱을 재시작하였습니다.")
                .setSmallIcon(R.drawable.icon64);

        notificationManager.notify(NOTIFICATIONID, notifyBuilder.build());


        if(intent.getStringExtra("app").equals("restart")){
            Log.e("Alarm", "앱재시작감지");
            Intent i = new Intent(context, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(i);
        }
    }
}
