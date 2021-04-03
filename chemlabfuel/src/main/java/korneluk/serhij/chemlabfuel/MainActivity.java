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
import java.util.Arrays;
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
    //private final ArrayList<ArrayMap<String, String>> inventarny_spisok_data = new ArrayList<>();
    private ArrayAdapter<String> arrayAdapter;
    private final ArrayList<ArrayList<String>> users = new ArrayList<>();
    private TextView textView;
    private Dialog_description_edit descriptionEdit;
    private String userEdit;
    public static InventoryList[] InventoryList;

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
        if (InventoryList[position].editedBy.equals("")) {
            long edited = InventoryList[position].editedAt;
            String editedBy = InventoryList[position].editedBy;
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
        String createdBy = InventoryList[position].createdBy;
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).get(0).contains(createdBy)) {
                fnG = users.get(i).get(1);
                lnG = users.get(i).get(2);
                break;
            }
        }
        String data02 = InventoryList[position].data02;
        String builder = "<strong>Марка, тип</strong><br>" + InventoryList[position].data03 + "<br><br>" +
                "<strong>Заводской номер (инв. номер)</strong><br>" + InventoryList[position].data04 + "<br><br>" +
                "<strong>Год выпуска (ввода в эксплуатацию)</strong><br>" + InventoryList[position].data05 + "<br><br>" +
                "<strong>Периодичность метролог. аттестации, поверки, калибровки, мес.</strong><br>" + InventoryList[position].data06 + "<br><br>" +
                "<strong>Дата последней аттестации, поверки, калибровки</strong><br>" + InventoryList[position].data07 + "<br><br>" +
                "<strong>Дата следующей аттестации, поверки, калибровки</strong><br>" + InventoryList[position].data08 + "<br><br>" +
                "<strong>Дата консервации</strong><br>" + InventoryList[position].data09 + "<br><br>" +
                "<strong>Дата расконсервации</strong><br>" + InventoryList[position].data10 + "<br><br>" +
                "<strong>Ответственный</strong><br>" + fnG + " " + lnG + "<br><br>" +
                "<strong>Примечания</strong><br>" + InventoryList[position].data12 + editedString;
        Dialog_description description = Dialog_description.getInstance(data02, builder);
        description.show(getSupportFragmentManager(), "description");
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
        Dialog_context_menu menu = Dialog_context_menu.getInstance(position, InventoryList[position].data02);
        menu.show(getSupportFragmentManager(), "menu");
        return true;
    }

    @Override
    public void onDialogEditPosition(int position) {
        if (isNetworkAvailable()) {
            descriptionEdit = Dialog_description_edit.getInstance(userEdit, InventoryList[position].uid,
                    InventoryList[position].data02, InventoryList[position].data03,
                    InventoryList[position].data04, InventoryList[position].data05,
                    InventoryList[position].data06, InventoryList[position].data07,
                    InventoryList[position].data08, InventoryList[position].data09,
                    InventoryList[position].data10, InventoryList[position].data12);
            descriptionEdit.show(getSupportFragmentManager(), "edit");
        } else {
            Dialog_no_internet noInternet = new Dialog_no_internet();
            noInternet.show(getSupportFragmentManager(), "internet");
        }
    }

    @Override
    public void onDialogDeleteClick(int position) {
        if (isNetworkAvailable()) {
            Dialog_delete_confirm confirm = Dialog_delete_confirm.getInstance(
                    InventoryList[position].data02, position);
            confirm.show(getSupportFragmentManager(), "confirm");
        } else {
            Dialog_no_internet noInternet = new Dialog_no_internet();
            noInternet.show(getSupportFragmentManager(), "internet");
        }
    }

    @Override
    public void delete_data(int position) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("equipments").child(InventoryList[position].uid).removeValue();
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
        menu.findItem(R.id.exit).setVisible(currentUser != null);
        SharedPreferences fuel = getSharedPreferences("fuel", Context.MODE_PRIVATE);
        int sort = fuel.getInt("sort", 0);
        menu.findItem(R.id.sortAlpha).setChecked(sort == 1);
        menu.findItem(R.id.sortTime).setChecked(sort == 2);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.add) {
            if (isNetworkAvailable()) {
                descriptionEdit = Dialog_description_edit.getInstance(userEdit, (long) InventoryList.length);
                descriptionEdit.show(getSupportFragmentManager(), "edit");
            } else {
                Dialog_no_internet noInternet = new Dialog_no_internet();
                noInternet.show(getSupportFragmentManager(), "internet");
            }
        }
        if (id == R.id.exit) {
            mAuth.signOut();
            listView.setVisibility(View.GONE);
            useremail.setVisibility(View.VISIBLE);
            userpass.setVisibility(View.VISIBLE);
            login.setVisibility(View.VISIBLE);
            textView.setVisibility(View.VISIBLE);
        }
        if (id == R.id.sortAlpha) {
            SharedPreferences fuel = getSharedPreferences("fuel", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = fuel.edit();
            if (item.isChecked()) {
                editor.putInt("sort", 0);
                editor.apply();
                Arrays.sort(InventoryList);
            } else {
                editor.putInt("sort", 1);
                editor.apply();
                Arrays.sort(InventoryList);
            }
            inventarny_spisok.clear();
            for (InventoryList inventoryList : InventoryList) {
                inventarny_spisok.add(inventoryList.data01 + ". " + inventoryList.data02);
            }
            arrayAdapter.notifyDataSetChanged();
            supportInvalidateOptionsMenu();
        }
        if (id == R.id.sortTime) {
            SharedPreferences fuel = getSharedPreferences("fuel", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = fuel.edit();
            if (item.isChecked()) {
                editor.putInt("sort", 0);
                editor.apply();
                Arrays.sort(InventoryList);
            } else {
                editor.putInt("sort", 2);
                editor.apply();
                Arrays.sort(InventoryList);
            }
            inventarny_spisok.clear();
            for (InventoryList inventoryList : InventoryList) {
                inventarny_spisok.add(inventoryList.data01 + ". " + inventoryList.data02);
            }
            arrayAdapter.notifyDataSetChanged();
            supportInvalidateOptionsMenu();
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
                        int size = 0;
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            if (postSnapshot.getValue() instanceof HashMap) {
                                HashMap hashMap = (HashMap) postSnapshot.getValue();
                                if (hashMap.size() > 12) {
                                    size++;
                                }
                            }
                        }
                        InventoryList = new InventoryList[size];
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
                                    InventoryList[size] = new InventoryList(MainActivity.this,
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
                        Arrays.sort(InventoryList);
                        inventarny_spisok.clear();
                        for (InventoryList inventoryList : InventoryList) {
                            inventarny_spisok.add(inventoryList.data01 + ". " + inventoryList.data02);
                        }
                        listView.setVisibility(View.VISIBLE);
                        useremail.setVisibility(View.GONE);
                        userpass.setVisibility(View.GONE);
                        login.setVisibility(View.GONE);
                        textView.setVisibility(View.GONE);
                        arrayAdapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);

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
                    Type type = new TypeToken<InventoryList[]>() {}.getType();
                    InventoryList = gson.fromJson(g, type);
                    for (InventoryList inventoryList : InventoryList) {
                        inventarny_spisok.add(inventoryList.data01 + ". " + inventoryList.data02);
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
                showPopupMenu(viewHolder.button_popup, position);
            });
            GregorianCalendar g = (GregorianCalendar) Calendar.getInstance();
            long real = g.getTimeInMillis();
            String dataLong = "";
            String data8 = InventoryList[position].data08;
            if (data8 != null && !data8.equals("")) {
                String[] t1 = data8.split("-");
                GregorianCalendar calendar = new GregorianCalendar();
                String data09 = InventoryList[position].data09;
                String data10 = InventoryList[position].data10;
                if (data09 != null && !data09.equals("")) {
                    String[] t2 = data09.split("-");
                    calendar.set(Integer.parseInt(t2[0]), Integer.parseInt(t2[1]) - 1, Integer.parseInt(t2[2]));
                    long t2l = calendar.getTimeInMillis();
                    if (data10 != null && !data10.equals("")) {
                        String[] t3 = data10.split("-");
                        calendar.set(Integer.parseInt(t3[0]), Integer.parseInt(t3[1]) - 1, Integer.parseInt(t3[2]));
                        long t3l = calendar.getTimeInMillis();
                        if (t2l < t3l) {
                            g.set(Integer.parseInt(t1[0]), Integer.parseInt(t3[1]) - 1, Integer.parseInt(t3[2]));
                            if (g.getTimeInMillis() - real > 0L && g.getTimeInMillis() - real < 45L * 24L * 60L * 60L * 1000L) {
                                long dat = g.getTimeInMillis() - real;
                                g.setTimeInMillis(dat);
                                dataLong = "<br><font color=#9a2828>Осталось " +
                                        (g.get(Calendar.DAY_OF_YEAR) - 1) + " дней(-я)</font>";
                            } else if (g.getTimeInMillis() - real < 0L) {
                                dataLong = "<br><font color=#9a2828>Просрочено</font>";
                            }
                        }
                    }
                } else {
                    g.set(Integer.parseInt(t1[0]), Integer.parseInt(t1[1]) - 1, Integer.parseInt(t1[2]));
                    if (g.getTimeInMillis() - real > 0L && g.getTimeInMillis() - real < 45L * 24L * 60L * 60L * 1000L) {
                        long dat = g.getTimeInMillis() - real;
                        g.setTimeInMillis(dat);
                        dataLong = "<br><font color=#9a2828>Осталось " +
                                (g.get(Calendar.DAY_OF_YEAR) - 1) + " дней(-я)</font>";
                    } else if (g.getTimeInMillis() - real < 0L) {
                        dataLong = "<br><font color=#9a2828>Просрочено</font>";
                    }
                }
            }
            viewHolder.textView.setText(Html.fromHtml(inventarny_spisok.get(position) + dataLong));
            return convertView;
        }

        private void showPopupMenu(ImageView imageView, int position) {
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