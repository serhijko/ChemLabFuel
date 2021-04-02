package korneluk.serhij.chemlabfuel;

import android.app.backup.BackupAgentHelper;
import android.app.backup.BackupDataInput;
import android.os.ParcelFileDescriptor;

import java.io.IOException;

public class MyBackupAgent extends BackupAgentHelper {

    @Override
    public void onCreate() {
    }

    @Override
    public void onRestore(BackupDataInput data, int appVersionCode, ParcelFileDescriptor newState)
            throws IOException {
        super.onRestore(data, appVersionCode, newState);
    }
}
