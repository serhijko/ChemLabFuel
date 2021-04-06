package korneluk.serhij.chemlabfuel;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

public class ReceiverSetAlarm extends BroadcastReceiver {

    private final ArrayList<String> testData = new ArrayList<>();
    private long toDateAlarm = 45L;

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences fuel = context.getSharedPreferences("fuel", Context.MODE_PRIVATE);
        switch (fuel.getInt("notification", 0)) {
            case 0:
                toDateAlarm = 45L;
                break;
            case 1:
                toDateAlarm = 30L;
                break;
            case 2:
                toDateAlarm = 15L;
                break;
            case 3:
                toDateAlarm = 10L;
                break;
            case 4:
                toDateAlarm = 5L;
                break;
            case 5:
                toDateAlarm = 0L;
                break;
        }
        Task(context);
    }

    private void Task(Context context) {
        new Thread(() -> {
            testData.clear();
            if (ChemLabFuel.InventoryList == null) {
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    if (isNetworkAvailable(context)) {
                        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                        mDatabase.child("equipments").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                int size = 0;
                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                    if (postSnapshot.getValue() instanceof HashMap) {
                                        HashMap hashMap = (HashMap) postSnapshot.getValue();
                                        if (hashMap.size() > 12) {
                                            size++;
                                        }
                                    }
                                }
                                ChemLabFuel.InventoryList = new InventoryList[size];
                                size = 0;
                                for (DataSnapshot data : dataSnapshot.getChildren()) {
                                    if (data.getValue() instanceof HashMap) {
                                        HashMap hashMap = (HashMap) data.getValue();
                                        if (hashMap.size() > 12) {
                                            Object editedAt = hashMap.get("editedAt");
                                            Object editedBy = hashMap.get("editedBy");
                                            if (hashMap.get("editedAt") == null)
                                                editedAt = 0L;
                                            if (hashMap.get("editedBy") == null)
                                                editedBy = "";
                                            ChemLabFuel.InventoryList[size] = new InventoryList(context,
                                                    (String) hashMap.get("createdBy"),
                                                    (long) hashMap.get("data01"),
                                                    (String) hashMap.get("data02"),
                                                    (String) hashMap.get("data03"),
                                                    (String) hashMap.get("data04"),
                                                    (String) hashMap.get("data05"),
                                                    (String) hashMap.get("data06"),
                                                    (String) hashMap.get("data07"),
                                                    (String) hashMap.get("data08"),
                                                    (String) hashMap.get("data09"),
                                                    (String) hashMap.get("data10"),
                                                    (long) hashMap.get("data11"),
                                                    (String) hashMap.get("data12"), data.getKey(),
                                                    (long) editedAt, (String) editedBy);
                                            size++;
                                        }
                                    }
                                }
                                checkAlarm(context);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
                    }
                }
            } else {
                checkAlarm(context);
            }

        }).start();
    }

    private void checkAlarm(Context context) {
        long timer = toDateAlarm * 24L * 60L * 60L * 1000L;
        GregorianCalendar c = (GregorianCalendar) Calendar.getInstance();
        long realtime = c.getTimeInMillis();
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE), 8, 0, 0);
        long time = c.getTimeInMillis();
        for (InventoryList inventory_list_datum : ChemLabFuel.InventoryList) {
            int data01 = (int) inventory_list_datum.data01;
            removeAlarm(context, data01);
            if (toDateAlarm != 0L) {
                String data08 = inventory_list_datum.data08;
                if (data08 != null && !data08.equals("")) {
                    String[] t1 = data08.split("-");
                    c.set(Integer.parseInt(t1[0]), Integer.parseInt(t1[1]), Integer.parseInt(t1[2]),
                            8, 0, 0);
                    long timeSet = c.getTimeInMillis();
                    long timeRes = timeSet - time;
                    if (timeRes > -toDateAlarm * 24L * 60L * 60L * 1000L && timeRes < timer) {
                        c.setTimeInMillis(time);
                        if (realtime > time) {
                            if (c.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY)
                                c.add(Calendar.DATE, 3);
                            else
                                c.add(Calendar.DATE, 1);
                        }
                        setAlarm(context, c, data01);
                    } else if (timeRes > timer) {
                        GregorianCalendar calendar = new GregorianCalendar();
                        String data09 = inventory_list_datum.data09;
                        String data10 = inventory_list_datum.data10;
                        if (data09 != null && !data09.equals("")) {
                            String[] t2 = data09.split("-");
                            calendar.set(Integer.parseInt(t2[0]), Integer.parseInt(t2[1]) - 1,
                                    Integer.parseInt(t2[2]));
                            long t2l = calendar.getTimeInMillis();
                            if (data10 != null && !data10.equals("")) {
                                String[] t3 = data10.split("-");
                                calendar.set(Integer.parseInt(t3[0]), Integer.parseInt(t3[1]) - 1,
                                        Integer.parseInt(t3[2]));
                                long t3l = calendar.getTimeInMillis();
                                if (t2l < t3l) {
                                    c.add(Calendar.DATE, (int) -toDateAlarm);
                                    setAlarm(context, c, data01);
                                }
                            }
                        } else {
                            c.add(Calendar.DATE, (int) -toDateAlarm);
                            setAlarm(context, c, data01);
                        }
                    }
                }
            }
        }
    }

    private void removeAlarm(Context context, int requestCode) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pIntent = PendingIntent.getBroadcast(context, requestCode,
                new Intent(context, ReceiverNotification.class), 0);
        alarmManager.cancel(pIntent);
    }

    private void setAlarm(Context context, GregorianCalendar c, int requestCode) {
        boolean testAlarm = true;
        String testDataLocal = c.get(Calendar.YEAR) + "-" + c.get(Calendar.MONTH) + "-" +
                c.get(Calendar.DATE);
        for (int i = 0; i < testData.size(); i++) {
            if (testData.get(i).contains(testDataLocal)) {
                testAlarm = false;
                break;
            }
        }
        if (testAlarm) {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            PendingIntent pIntent = PendingIntent.getBroadcast(context, requestCode,
                    new Intent(context, ReceiverNotification.class), 0);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pIntent);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pIntent);
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pIntent);
            }
        }
        testData.add(c.get(Calendar.YEAR) + "-" + c.get(Calendar.MONTH) + "-" + c.get(Calendar.DATE));
    }

    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting()) { // connected to internet
            // connected to wifi or mobile provider
            return activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI ||
                    activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
        } else {
            // not connected to the internet
            return false;
        }
    }
}
