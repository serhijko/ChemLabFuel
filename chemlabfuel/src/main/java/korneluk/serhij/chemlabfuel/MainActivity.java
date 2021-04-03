package korneluk.serhij.chemlabfuel;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.AbsoluteSizeSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, Dialog_context_menu.Dialog_context_menu_Listener, Dialog_delete_confirm.Dialog_delete_confirm_listener, Dialog_date.Dialog_date_listener {

    private FirebaseAuth mAuth;
    private EditText useremail;
    private EditText userpass;
    private Button login;
    private String email;
    private String password;
    private ListView listView;
    private ProgressBar progressBar;
    private final ArrayList<String> inventarny_spisok = new ArrayList<>();
    private final ArrayList<HashMap> inventarny_spisok_data = new ArrayList<>();
    private ArrayAdapter<String> arrayAdapter;
    private final ArrayList<ArrayList<String>> users = new ArrayList<>();
    private final ArrayList<ArrayList<Long>> notifications = new ArrayList<>();
    private TextView textView;
    private Dialog_description_edit descriptionEdit;
    private String userEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        useremail = findViewById(R.id.username);
        userpass = findViewById(R.id.password);
        login = findViewById(R.id.login);
        listView = findViewById(R.id.listView);
        progressBar = findViewById(R.id.loading);
        arrayAdapter = new ListAdapter(this);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);
        login.setOnClickListener(view -> {
            // Hide the keyboard
            InputMethodManager imm1 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm1 != null) {
                imm1.hideSoftInputFromWindow(useremail.getWindowToken(), 0);
            }
            email = useremail.getText().toString();
            password = userpass.getText().toString();
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
        });
        textView = findViewById(R.id.link);
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
    public void onStart() {
        super.onStart();
        // Check if useremail is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        String fn = "";
        String ln = "";
        String fnG = "";
        String lnG = "";
        String zero1 = "";
        String zero2 = "";
        String editedString = "";
        if (inventarny_spisok_data.get(position).get("editedAt") != null && inventarny_spisok_data.get(position).get("editedBy") != null) {
            long edited;
            if (inventarny_spisok_data.get(position).get("editedAt") instanceof Double)
                edited = (long) (double) inventarny_spisok_data.get(position).get("editedAt");
            else
                edited = (long) inventarny_spisok_data.get(position).get("editedAt");
            String editedBy = (String) inventarny_spisok_data.get(position).get("editedBy");
            for (int i = 0; i < users.size(); i++) {
                if (users.get(i).get(0).contains(editedBy)) {
                    fn = users.get(i).get(1);
                    ln = users.get(i).get(2);
                    break;
                }
            }
            GregorianCalendar c = new GregorianCalendar();
            c.setTimeInMillis(edited);
            if (c.get(Calendar.DATE) < 10) zero1 = "0";
            if (c.get(Calendar.MONTH) < 9) zero2 = "0";
            editedString = " Изменено " + zero1 + c.get(Calendar.DATE) + "." + zero2 + (c.get(Calendar.MONTH) + 1) + "." + c.get(Calendar.YEAR) + " " + fn + " " + ln;
        }
        String createdBy = (String) inventarny_spisok_data.get(position).get("createdBy");
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).get(0).contains(createdBy)) {
                fnG = users.get(i).get(1);
                lnG = users.get(i).get(2);
                break;
            }
        }
        String data02 = (String) inventarny_spisok_data.get(position).get("data02");
        String builder = "<strong>Марка, тип</strong><br>" + inventarny_spisok_data.get(position).get("data03") + "<br><br>" +
                "<strong>Заводской номер (инв. номер)</strong><br>" + inventarny_spisok_data.get(position).get("data04") + "<br><br>" +
                "<strong>Год выпуска (ввода в эксплуатацию)</strong><br>" + inventarny_spisok_data.get(position).get("data05") + "<br><br>" +
                "<strong>Периодичность метролог. аттестации, поверки, калибровки, мес.</strong><br>" + inventarny_spisok_data.get(position).get("data06") + "<br><br>" +
                "<strong>Дата последней аттестации, поверки, калибровки</strong><br>" + inventarny_spisok_data.get(position).get("data07") + "<br><br>" +
                "<strong>Дата следующей аттестации, поверки, калибровки</strong><br>" + inventarny_spisok_data.get(position).get("data08") + "<br><br>" +
                "<strong>Дата консервации</strong><br>" + inventarny_spisok_data.get(position).get("data09") + "<br><br>" +
                "<strong>Дата расконсервации</strong><br>" + inventarny_spisok_data.get(position).get("data10") + "<br><br>" +
                "<strong>Ответственный</strong><br>" + fnG + " " + lnG + "<br><br>" +
                "<strong>Примечания</strong><br>" + inventarny_spisok_data.get(position).get("data12") + editedString;
        Dialog_description description = Dialog_description.getInstance(data02, builder);
        description.show(getSupportFragmentManager(), "description");
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
        Dialog_context_menu menu = Dialog_context_menu.getInstance(position, (String) inventarny_spisok_data.get(position).get("data02"));
        menu.show(getSupportFragmentManager(), "menu");
        return true;
    }

    @Override
    public void onDialogEditPosition(int position) {
        descriptionEdit = Dialog_description_edit.getInstance(userEdit, (String) inventarny_spisok_data.get(position).get("uid"), (String) inventarny_spisok_data.get(position).get("data02"), (String) inventarny_spisok_data.get(position).get("data03"), (String) inventarny_spisok_data.get(position).get("data04"), (String) inventarny_spisok_data.get(position).get("data05"), (String) inventarny_spisok_data.get(position).get("data06"), (String) inventarny_spisok_data.get(position).get("data07"), (String) inventarny_spisok_data.get(position).get("data08"), (String) inventarny_spisok_data.get(position).get("data09"), (String) inventarny_spisok_data.get(position).get("data10"), (String) inventarny_spisok_data.get(position).get("data12"));
        descriptionEdit.show(getSupportFragmentManager(), "edit");
    }

    @Override
    public void onDialogDeleteClick(int position) {
        Dialog_delete_confirm confirm = Dialog_delete_confirm.getInstance((String) inventarny_spisok_data.get(position).get("data02"), position);
        confirm.show(getSupportFragmentManager(), "confirm");
    }

    @Override
    public void delete_data(int position) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("equipments").child((String) inventarny_spisok_data.get(position).get("uid")).removeValue();
    }

    @Override
    public void set_date(int textView, int year, int month, int dayOfMonth) {
        descriptionEdit.set_data(textView, year, month, dayOfMonth);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null)
            menu.findItem(R.id.exit).setVisible(false);
        else
            menu.findItem(R.id.exit).setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.add) {
            descriptionEdit = Dialog_description_edit.getInstance(userEdit, (long) inventarny_spisok_data.size());
            descriptionEdit.show(getSupportFragmentManager(), "edit");
        }
        if (id == R.id.exit) {
            mAuth.signOut();
            listView.setVisibility(View.GONE);
            useremail.setVisibility(View.VISIBLE);
            userpass.setVisibility(View.VISIBLE);
            login.setVisibility(View.VISIBLE);
            textView.setVisibility(View.VISIBLE);
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateUI(FirebaseUser user) {
        progressBar.setVisibility(View.VISIBLE);
        supportInvalidateOptionsMenu();
        if (user != null) {
            if (isNetworkAvailable()) {
                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                mDatabase.child("equipments").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        SharedPreferences fuel = getSharedPreferences("fuel", Context.MODE_PRIVATE);
                        Type type2 = new TypeToken<ArrayList<ArrayList<Long>>>() {}.getType();
                        Gson gson = new Gson();
                        String notify2 = fuel.getString("notify", "");
                        ArrayList<ArrayList<Long>> temp;
                        if (!notify2.equals("")) {
                            temp = new ArrayList<>(gson.fromJson(notify2, type2));
                        } else {
                            temp = new ArrayList<>();
                        }
                        int i = 0;
                        inventarny_spisok.clear();
                        inventarny_spisok_data.clear();
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            if (data.getValue() instanceof HashMap) {
                                HashMap hashMap = (HashMap) data.getValue();
                                inventarny_spisok_data.add(hashMap);
                                if (hashMap.size() > 12) {
                                    String uid = (String) hashMap.get("uid");
                                    if (uid == null)
                                        hashMap.put("uid", data.getKey());
                                    long data11 = 0;
                                    if (hashMap.get("data11") != null)
                                        data11 = (long) hashMap.get("data11");
                                    inventarny_spisok.add(hashMap.get("data01") + ". " + hashMap.get("data02"));
                                    ArrayList<Long> notify1 = new ArrayList<>();
                                    notify1.add((long) hashMap.get("data01"));
                                    notify1.add(data11);
                                    if (temp.size() < i) {
                                        if (temp.size() == 0)
                                            notify1.add(1L);
                                        else if (temp.get(i).get(1) != data11)
                                            notify1.add(1L);
                                        else
                                            notify1.add(0L);
                                    } else {
                                        notify1.add(1L);
                                    }
                                    notifications.add(notify1);
                                    i++;
                                }
                            }
                        }
                        listView.setVisibility(View.VISIBLE);
                        useremail.setVisibility(View.GONE);
                        userpass.setVisibility(View.GONE);
                        login.setVisibility(View.GONE);
                        textView.setVisibility(View.GONE);
                        arrayAdapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);
                        SharedPreferences.Editor editor = fuel.edit();
                        editor.putString("fuel_data", gson.toJson(inventarny_spisok_data));
                        editor.putString("notify", gson.toJson(notifications));
                        editor.apply();
                        Intent intent = new Intent(MainActivity.this, ReceiverSetAlarm.class);
                        sendBroadcast(intent);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                mDatabase.child("users").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot data : snapshot.getChildren()) {
                            String key = data.getKey();
                            if (mAuth.getUid().contains(key)) {
                                userEdit = key;
                            }
                            for (DataSnapshot data2 : data.getChildren()) {
                                if (data2.getValue() instanceof HashMap) {
                                    HashMap hashMap = (HashMap) data2.getValue();
                                    String firstName = (String) hashMap.get("firstName");
                                    String lastName = (String) hashMap.get("lastName");
                                    ArrayList<String> user = new ArrayList<>();
                                    user.add(key);
                                    user.add(firstName);
                                    user.add(lastName);
                                    users.add(user);
                                }
                            }
                        }
                        Gson gson = new Gson();
                        SharedPreferences fuel = getSharedPreferences("fuel", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = fuel.edit();
                        editor.putString("users", gson.toJson(users));
                        editor.apply();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            } else {
                SharedPreferences fuel = getSharedPreferences("fuel", Context.MODE_PRIVATE);
                String g = fuel.getString("fuel_data", "");
                if (!g.equals("")) {
                    Gson gson = new Gson();
                    Type type = new TypeToken<ArrayList<HashMap>>() {}.getType();
                    inventarny_spisok_data.addAll(gson.fromJson(g, type));
                    for (int i = 0; i < inventarny_spisok_data.size(); i++) {
                        HashMap hashMap = inventarny_spisok_data.get(i);
                        int data01 = (int) (double) hashMap.get("data01");
                        inventarny_spisok.add(data01 + ". " + hashMap.get("data02"));
                    }
                    String us = fuel.getString("users", "");
                    Type type2 = new TypeToken<ArrayList<ArrayList<String>>>() {}.getType();
                    users.addAll(gson.fromJson(us, type2));
                    arrayAdapter.notifyDataSetChanged();
                    Intent intent = new Intent(MainActivity.this, ReceiverSetAlarm.class);
                    sendBroadcast(intent);
                } else {
                    Dialog_no_internet noInternet = new Dialog_no_internet();
                    noInternet.show(getSupportFragmentManager(), "internet");
                }
                listView.setVisibility(View.VISIBLE);
                useremail.setVisibility(View.GONE);
                userpass.setVisibility(View.GONE);
                login.setVisibility(View.GONE);
                textView.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
            }
        } else {
            listView.setVisibility(View.GONE);
            useremail.setVisibility(View.VISIBLE);
            userpass.setVisibility(View.VISIBLE);
            login.setVisibility(View.VISIBLE);
            textView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) { // connected to the internet
            // connected to wifi or mobile providers
            return activeNetwork.getType() == ConnectivityManager.TYPE_WIFI || activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE;
        } else {
            // not connected to the internet
            return false;
        }
    }

    private class ListAdapter extends ArrayAdapter<String> {

        ListAdapter(@NonNull Context context) {
            super(context, R.layout.simple_list_item, inventarny_spisok);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.simple_list_item, parent, false);
                viewHolder = new ViewHolder();
                convertView.setTag(viewHolder);
                viewHolder.textView = convertView.findViewById(R.id.label);
                viewHolder.button_popup = convertView.findViewById(R.id.button_popup);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.button_popup.setOnClickListener(view -> {
                showPopupMenu(viewHolder.button_popup, position, inventarny_spisok.get(position));
            });
            long data;
            if (inventarny_spisok_data.get(position).get("data11") instanceof Double) {
                data = (long) (double) inventarny_spisok_data.get(position).get("data11");
            } else {
                if (inventarny_spisok_data.get(position).get("data11") != null)
                    data = (long) inventarny_spisok_data.get(position).get("data11");
                else data = 0;
            }
            GregorianCalendar g = new GregorianCalendar();
            g.setTimeInMillis(data);
            String dataLong = "";
            GregorianCalendar real = (GregorianCalendar) Calendar.getInstance();
            if (g.getTimeInMillis() - real.getTimeInMillis() > 0L && g.getTimeInMillis() - real.getTimeInMillis() < 45L * 24L * 60L * 60L * 1000L) {
                dataLong = "<b><font color=#9a2828>Осталось " + (g.get(Calendar.DATE) - real.get(Calendar.DATE)) + " дней(-я)</font>";
            } else if (g.getTimeInMillis() - real.getTimeInMillis() < 0L) {
                dataLong = "<b><font color=#9a2828>Просрочено</font>";
            }
            viewHolder.textView.setText(Html.fromHtml(inventarny_spisok.get(position) + dataLong));
            return convertView;
        }

        private void showPopupMenu(ImageView imageView, int position, String name) {
            PopupMenu popup = new PopupMenu(MainActivity.this, imageView);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.popup, popup.getMenu());
            for (int i = 0; i < popup.getMenu().size(); i++) {
                MenuItem item = popup.getMenu().getItem(i);
                SpannableString spanString = new SpannableString(popup.getMenu().getItem(i).getTitle().toString());
                int end = spanString.length();
                spanString.setSpan(new AbsoluteSizeSpan(18, true), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                item.setTitle(spanString);
            }
            popup.setOnMenuItemClickListener(menuItem -> {
                popup.dismiss();
                if (menuItem.getItemId() == R.id.menu_editor) {
                    onDialogEditPosition(position);
                    return true;
                }
                if (menuItem.getItemId() == R.id.menu_remove) {
                    onDialogDeleteClick(position);
                    return true;
                }
                return false;
            });
            popup.show();
        }
    }

    private static class ViewHolder {
        TextView textView;
        ImageView button_popup;
    }
}