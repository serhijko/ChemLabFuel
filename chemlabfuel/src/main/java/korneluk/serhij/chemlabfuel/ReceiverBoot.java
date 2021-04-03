package korneluk.serhij.chemlabfuel;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ReceiverBoot extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && intent.getAction() != null &&
                (intent.getAction().equals("android.intent.action.BOOT_COMPLETED") ||
                        intent.getAction().equals("android.intent.action.QUICKBOOT_POWERON") ||
                        intent.getAction().equals("com.htc.intent.action.QUICKBOOT_POWERON"))) {
            Intent intent1 = new Intent(context, ReceiverSetAlarm.class);
            context.sendBroadcast(intent1);
        }
    }
}
