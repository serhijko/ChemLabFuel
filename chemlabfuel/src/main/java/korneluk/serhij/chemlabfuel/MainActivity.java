package korneluk.serhij.chemlabfuel;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.TypedValue;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText useremail;
    private EditText userpass;
    private String email;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences fuel = getSharedPreferences("fuel", Context.MODE_PRIVATE);
        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().getBoolean("notifications", false)) {
                SharedPreferences.Editor editor = fuel.edit();
                editor.putInt("sort", 2);
                editor.apply();
            }
        }
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        useremail = findViewById(R.id.username);
        userpass = findViewById(R.id.password);
        Button login = findViewById(R.id.login);
        login.setOnClickListener(view -> {
            // Hide the keyboard
            InputMethodManager imm1 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm1 != null) {
                imm1.hideSoftInputFromWindow(useremail.getWindowToken(), 0);
            }
            email = useremail.getText().toString();
            password = userpass.getText().toString();
            if (!email.equals("") && !password.equals("")) {
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(MainActivity.this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in useremail's information
                        FirebaseUser user1 = mAuth.getCurrentUser();
                        updateUI(user1);
                    } else {
                        // If sign in fails, display a message to the useremail.
                        LinearLayout layout = new LinearLayout(this);
                        layout.setBackgroundResource(R.color.colorPrimary);
                        TextView toast = new TextView(this);
                        toast.setTextColor(getResources().getColor(R.color.colorIcons));
                        toast.setPadding(10, 10, 10, 10);
                        toast.setText("Неверный логин или пароль");
                        toast.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                        layout.addView(toast);
                        Toast mes = new Toast(this);
                        mes.setDuration(Toast.LENGTH_LONG);
                        mes.setView(layout);
                        mes.show();
                        updateUI(null);
                    }
                });
            } else {
                LinearLayout layout = new LinearLayout(this);
                layout.setBackgroundResource(R.color.colorPrimary);
                TextView toast = new TextView(this);
                toast.setTextColor(getResources().getColor(R.color.colorIcons));
                toast.setPadding(10, 10, 10, 10);
                toast.setText("Неверный логин или пароль");
                toast.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                layout.addView(toast);
                Toast mes = new Toast(this);
                mes.setDuration(Toast.LENGTH_LONG);
                mes.setView(layout);
                mes.show();
                updateUI(null);
            }
        });
        TextView textView = findViewById(R.id.link);
        textView.setClickable(true);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        String text = "<a href='https://github.com/serhijko/ChemLabFuel/blob/master/README.md'>Политика конфиденциальности</a>";
        textView.setText(Html.fromHtml(text));
        setToolbarTheme();
    }

    private void setToolbarTheme() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView title_toolbar = findViewById(R.id.title_toolbar);
        title_toolbar.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        setSupportActionBar(toolbar);
        title_toolbar.setText(R.string.app_main);
    }

    @Override
    public void onResume() {
        super.onResume();
        overridePendingTransition(R.anim.alphain, R.anim.alphaout);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if userEmail is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Intent intent = new Intent(this, ChemLabFuel.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            if (getIntent().getExtras() != null)
                intent.putExtra("reagent", getIntent().getExtras().getBoolean("reagent", false));
            startActivity(intent);
            finish();
        }
    }
}