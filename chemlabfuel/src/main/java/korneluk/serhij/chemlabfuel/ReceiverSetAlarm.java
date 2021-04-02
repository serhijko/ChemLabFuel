package korneluk.serhij.chemlabfuel;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

public class ReceiverSetAlarm extends BroadcastReceiver {

    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        Task();
    }

    private void Task() {
        new Thread(() -> {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            SharedPreferences fuel = context.getSharedPreferences("fuel", Context.MODE_PRIVATE);
            Type type = new TypeToken<ArrayList<HashMap>>() {}.getType();
            String g = fuel.getString("fuel_data", "");
            if (!g.equals("")) {
                Gson gson = new Gson();
                Type type2 = new TypeToken<ArrayList<ArrayList<Long>>>() {}.getType();
                String notify2 = fuel.getString("notify", "");
                ArrayList<ArrayList<Long>> notification = new ArrayList<>(gson.fromJson(notify2, type2));
                ArrayList<HashMap> inventarny_spisok_data = new ArrayList<>(gson.fromJson(g, type));
                GregorianCalendar c = (GregorianCalendar) Calendar.getInstance();
                for (int i = 0; i < inventarny_spisok_data.size(); i++) {
                    if (notification.get(i).get(2) == 1L) {
                        long data;
                        if (inventarny_spisok_data.get(i).get("data11") != null)
                            data = (long) (double) inventarny_spisok_data.get(i).get("data11");
                        else
                            data = (c.get(Calendar.YEAR) + 10L) * 365L * 24L * 60L * 60L * 1000L;
                        int code = (int) (double) inventarny_spisok_data.get(i).get("data01");
                        String interval = (String) inventarny_spisok_data.get(i).get("data06");
                        if (interval == null || interval.equals("")) {
                            interval = "12";
                        }
                        long interV = (Long.parseLong(interval) / 12) * 365L * 24L * 60L * 60L * 1000L;
                        c.setTimeInMillis(data);
                        c.add(Calendar.DATE, -45);
                        Intent intent = new Intent(context, ReceiverNotification.class);
                        intent.putExtra("name", (String) inventarny_spisok_data.get(i).get("data02"));
                        intent.putExtra("data", data);
                        intent.putExtra("id", code);
                        PendingIntent pIntent = PendingIntent.getBroadcast(context, code, intent, 0);
                        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                                mkTime(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE),
                                        c.get(Calendar.HOUR_OF_DAY)), interV, pIntent);
                    }
                }
            }
        }).start();
    }

    private long mkTime(int year, int month, int day, int hourOfDay) {
        GregorianCalendar calendar = new GregorianCalendar(year, month, day, hourOfDay, 0, 0);
        return calendar.getTimeInMillis();
    }
}
