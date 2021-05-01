package korneluk.serhij.chemlabfuel;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class ReceiverNotification extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        sendNotification(context, intent.getBooleanExtra("reagents", false));
    }

    private void sendNotification(Context context, boolean reagent) {
        Intent notificationIntent = new Intent(context, SplashActivity.class);
        notificationIntent.putExtra("notifications", true);
        notificationIntent.putExtra("reagents", reagent);
        PendingIntent contentIntent = PendingIntent.getActivity(context,
                0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Resources res = context.getResources();
        String channelId = "2020";
        if (reagent)
            channelId = "2030";
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId);
        builder.setContentIntent(contentIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.ic_launcher))
                .setAutoCancel(true)
                .setContentTitle("Истекает срок");
        if (reagent)
            builder.setContentText("годности реактива");
        else
            builder.setContentText("следующей аттестации, поверки, калибровки");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId("korneluk.serhij.chemlabfuel");
        }
        Notification notification = builder.build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("korneluk.serhij.chemlabfuel",
                    "ChemLabFuel", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(205, notification);
        Intent intent = new Intent(context, ReceiverSetAlarm.class);
        context.sendBroadcast(intent);
    }
}
