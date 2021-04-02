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
import androidx.core.app.NotificationManagerCompat;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class ReceiverNotification extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        sendNotif(context, intent.getStringExtra("name"),
                intent.getLongExtra("data", 0),
                intent.getIntExtra("id", 205));
    }

    private void sendNotif(Context context, String name, long data, int id) {
        GregorianCalendar g = new GregorianCalendar();
        g.setTimeInMillis(data);
        Intent notificationIntent = new Intent(context, SplashActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context,
                0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        Resources res = context.getResources();
        String channelId = "2020";
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId);
        builder.setContentIntent(contentIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.ic_launcher))
                .setAutoCancel(true)
                .setContentTitle(name)
                .setContentText(g.get(Calendar.DATE) + ". " + (g.get(Calendar.MONTH) + 1) + "." +
                        g.get(Calendar.YEAR) + " истекает срок следующей аттестации, поверки, калибровки");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId("korneluk.serhij.chemlabfuel");
        }
        Notification notification = builder.build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("korneluk.serhij.chemlabfuel",
                    "chemlabfuel", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(id, notification);
    }
}
