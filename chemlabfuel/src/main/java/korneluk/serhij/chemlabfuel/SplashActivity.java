package korneluk.serhij.chemlabfuel;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        if (getIntent().getExtras() != null) {
            intent.putExtra("notifications",
                    getIntent().getExtras().getBoolean("notifications", false));
            intent.putExtra("reagent",
                    getIntent().getExtras().getBoolean("reagent", false));
        }
        startActivity(intent);
        finish();
    }
}
