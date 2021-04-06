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
import android.text.style.AbsoluteSizeSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TextView;

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

public class ChemLabFuel extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, Dialog_context_menu.Dialog_context_menu_Listener, Dialog_delete_confirm.Dialog_delete_confirm_listener, Dialog_date.Dialog_date_listener {

    private final ArrayList<String> inventory_list = new ArrayList<>();
    private final ArrayList<String> reagents_list = new ArrayList<>();
    private ArrayAdapter<String> arrayAdapter;
    private final ArrayList<ArrayList<String>> users = new ArrayList<>();
    private Dialog_description_edit descriptionEdit;
    private String userEdit;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    public static InventoryList[] InventoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chemlabfuel);
        SharedPreferences fuel = getSharedPreferences("fuel", Context.MODE_PRIVATE);

        reagents_list.add("Нет данных");
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.loading);
        ListView listView = findViewById(R.id.listView);
        arrayAdapter = new ListAdapter();
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);

        ListView listView2 = findViewById(R.id.listView2);
        listView2.setAdapter(new ReagentsListAdapter());

        TabHost tabHost = findViewById(R.id.tabhost);
        tabHost.setup();
        TabHost.TabSpec tabSpec;
        tabSpec = tabHost.newTabSpec("tag1");
        tabSpec.setIndicator(getString(R.string.equipments));
        tabSpec.setContent(R.id.tab1);
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("tag2");
        tabSpec.setIndicator(getString(R.string.reagents));
        tabSpec.setContent(R.id.tab2);
        tabHost.addTab(tabSpec);
        tabHost.setOnTabChangedListener(tabId -> {
            SharedPreferences.Editor editor = fuel.edit();
            if (tabId.contains("tag1"))
                editor.putBoolean("equipments", true);
            else
                editor.putBoolean("equipments", false);
            editor.apply();
        });
        if (fuel.getBoolean("equipments", true)) {
            tabHost.setCurrentTabByTag("tag1");
        } else {
            tabHost.setCurrentTabByTag("tag2");
        }
        setToolbarTheme();
        updateUI();
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
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        String fn = "";
        String ln = "";
        String fnG = "";
        String lnG = "";
        String zero1 = "";
        String zero2 = "";
        String editedString = "";
        if (!InventoryList[position].editedBy.equals("")) {
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
            startActivity(new Intent(this, MainActivity.class));
            finish();
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
            inventory_list.clear();
            for (InventoryList inventoryList : InventoryList) {
                inventory_list.add(inventoryList.data01 + ". " + inventoryList.data02);
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
            inventory_list.clear();
            for (InventoryList inventoryList : InventoryList) {
                inventory_list.add(inventoryList.data01 + ". " + inventoryList.data02);
            }
            arrayAdapter.notifyDataSetChanged();
            supportInvalidateOptionsMenu();
        }
        if (id == R.id.settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateUI() {
        progressBar.setVisibility(View.VISIBLE);
        supportInvalidateOptionsMenu();
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
                                InventoryList[size] = new InventoryList(ChemLabFuel.this,
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
                    inventory_list.clear();
                    for (InventoryList inventoryList : InventoryList) {
                        inventory_list.add(inventoryList.data01 + ". " + inventoryList.data02);
                    }
                    arrayAdapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);

                    sendBroadcast(new Intent(ChemLabFuel.this, ReceiverSetAlarm.class));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
            mDatabase.child("users").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
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
                public void onCancelled(@NonNull DatabaseError databaseError) {
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
                    inventory_list.add(inventoryList.data01 + ". " + inventoryList.data02);
                }
                String us = fuel.getString("users", "");
                Type type2 = new TypeToken<ArrayList<ArrayList<String>>>() {}.getType();
                users.addAll(gson.fromJson(us, type2));
                arrayAdapter.notifyDataSetChanged();
                Intent intent = new Intent(ChemLabFuel.this, ReceiverSetAlarm.class);
                sendBroadcast(intent);
            } else {
                Dialog_no_internet noInternet = new Dialog_no_internet();
                noInternet.show(getSupportFragmentManager(), "internet");
            }
            progressBar.setVisibility(View.GONE);
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) { // connected to the internet
            // connected to wifi or mobile providers
            return activeNetwork.getType() == ConnectivityManager.TYPE_WIFI ||
                    activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE;
        } else {
            // not connected to the internet
            return false;
        }
    }

    private class ListAdapter extends ArrayAdapter<String> {

        private final SharedPreferences fuel;

        ListAdapter() {
            super(ChemLabFuel.this, R.layout.simple_list_item, inventory_list);
            fuel = getSharedPreferences("fuel", Context.MODE_PRIVATE);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View mView, @NonNull ViewGroup parent) {
            ViewHolder viewHolder;
            if (mView == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                mView = vi.inflate(R.layout.simple_list_item, parent, false);
                viewHolder = new ViewHolder();
                mView.setTag(viewHolder);
                viewHolder.textView = mView.findViewById(R.id.label);
                viewHolder.button_popup = mView.findViewById(R.id.button_popup);
            } else {
                viewHolder = (ViewHolder) mView.getTag();
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
            viewHolder.textView.setText(Html.fromHtml(inventory_list.get(position) + dataLong));
            viewHolder.textView.setTextSize(fuel.getInt("fontSize", 18));
            return mView;
        }
    }

    private class ReagentsListAdapter extends ArrayAdapter<String> {

        private final SharedPreferences fuel;

        public ReagentsListAdapter() {
            super(ChemLabFuel.this, R.layout.simple_list_item, reagents_list);
            fuel = getSharedPreferences("fuel", Context.MODE_PRIVATE);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View mView, @NonNull ViewGroup parent) {
            ViewHolder viewHolder;
            if (mView == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                mView = vi.inflate(R.layout.simple_list_item, parent, false);
                viewHolder = new ViewHolder();
                mView.setTag(viewHolder);
                viewHolder.textView = mView.findViewById(R.id.label);
                viewHolder.button_popup = mView.findViewById(R.id.button_popup);
            } else {
                viewHolder = (ViewHolder) mView.getTag();
            }
            //viewHolder.button_popup.setOnClickListener(view -> showPopupMenu(viewHolder.button_popup, position));

            viewHolder.textView.setText(Html.fromHtml(reagents_list.get(position)));
            viewHolder.textView.setTextSize(fuel.getInt("fontSize", 18));
            return mView;
        }
    }

    private void showPopupMenu(ImageView imageView, int position) {
        PopupMenu popup = new PopupMenu(ChemLabFuel.this, imageView);
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

    private static class ViewHolder {
        TextView textView;
        ImageView button_popup;
    }
}
