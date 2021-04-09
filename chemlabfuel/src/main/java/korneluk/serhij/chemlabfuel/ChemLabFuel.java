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
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ChemLabFuel extends AppCompatActivity implements AdapterView.OnItemClickListener,
        AdapterView.OnItemLongClickListener, Dialog_context_menu.Dialog_context_menu_Listener,
        Dialog_delete_confirm.Dialog_delete_confirm_listener, Dialog_date.Dialog_date_listener,
        Dialog_reagents_description_edit.listUpdaterListener, ExpandableListView.OnChildClickListener {

    private final ArrayList<String> inventory_list = new ArrayList<>();
    private ArrayAdapter<String> arrayAdapter;
    private ReagentsListAdapter arrayAdapter2;
    private final ArrayList<ArrayList<String>> users = new ArrayList<>();
    private Dialog_description_edit descriptionEdit;
    private Dialog_reagents_description_edit reagentsDescriptionEdit;
    private Dialog_reagent_consumption consumption;
    private String userEdit;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    public static ArrayList<InventoryList> InventoryList = new ArrayList<>();
    public static LinkedHashMap<Integer, LinkedHashMap<Integer, LinkedHashMap<Integer, String>>>
            ReagentsList = new LinkedHashMap<>();
    private final ArrayList<ReagentsList> listGroup = new ArrayList<>();
    private final ArrayList<ArrayList<String>> listChild = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chemlabfuel);
        SharedPreferences fuel = getSharedPreferences("fuel", Context.MODE_PRIVATE);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.loading);
        ListView listView = findViewById(R.id.listView);
        arrayAdapter = new ListAdapter();
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);

        arrayAdapter2 = new ReagentsListAdapter();
        ExpandableListView listView2 = findViewById(R.id.listView2);
        listView2.setAdapter(arrayAdapter2);
        listView2.setOnChildClickListener(this);

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
            editor.putBoolean("equipments", tabId.contains("tag1"));
            editor.apply();
            supportInvalidateOptionsMenu();
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
        if (adapterView.getId() == R.id.listView) {
            String fn = "";
            String ln = "";
            String fnG = "";
            String lnG = "";
            String zero1 = "";
            String zero2 = "";
            String editedString = "";
            if (!InventoryList.get(position).editedBy.equals("")) {
                long editedAt = InventoryList.get(position).editedAt;
                String editedBy = InventoryList.get(position).editedBy;
                for (int i = 0; i < users.size(); i++) {
                    if (users.get(i).get(0).contains(editedBy)) {
                        fn = users.get(i).get(1);
                        ln = users.get(i).get(2);
                        break;
                    }
                }
                GregorianCalendar c = new GregorianCalendar();
                c.setTimeInMillis(editedAt);
                if (c.get(Calendar.DATE) < 10) zero1 = "0";
                if (c.get(Calendar.MONTH) < 9) zero2 = "0";
                editedString = " Изменено " + zero1 + c.get(Calendar.DATE) + "." + zero2 +
                        (c.get(Calendar.MONTH) + 1) + "." + c.get(Calendar.YEAR) + " " + fn + " " + ln;
            }
            String createdBy = InventoryList.get(position).createdBy;
            for (int i = 0; i < users.size(); i++) {
                if (users.get(i).get(0).contains(createdBy)) {
                    fnG = users.get(i).get(1);
                    lnG = users.get(i).get(2);
                    break;
                }
            }
            String data02 = InventoryList.get(position).data02;
            String builder = "<strong>Марка, тип</strong><br>" + InventoryList.get(position).data03 + "<br><br>" +
                    "<strong>Заводской номер (инв. номер)</strong><br>" + InventoryList.get(position).data04 + "<br><br>" +
                    "<strong>Год выпуска (ввода в эксплуатацию)</strong><br>" + InventoryList.get(position).data05 + "<br><br>" +
                    "<strong>Периодичность метролог. аттестации, поверки, калибровки, мес.</strong><br>" + InventoryList.get(position).data06 + "<br><br>" +
                    "<strong>Дата последней аттестации, поверки, калибровки</strong><br>" + InventoryList.get(position).data07 + "<br><br>" +
                    "<strong>Дата следующей аттестации, поверки, калибровки</strong><br>" + InventoryList.get(position).data08 + "<br><br>" +
                    "<strong>Дата консервации</strong><br>" + InventoryList.get(position).data09 + "<br><br>" +
                    "<strong>Дата расконсервации</strong><br>" + InventoryList.get(position).data10 + "<br><br>" +
                    "<strong>Ответственный</strong><br>" + fnG + " " + lnG + "<br><br>" +
                    "<strong>Примечания</strong><br>" + InventoryList.get(position).data12 + editedString;
            Dialog_description description = Dialog_description.getInstance(data02, builder);
            description.show(getSupportFragmentManager(), "description");
        }
    }

    private ArrayList<String> search(int groupPosition, int childPosition) {
        ReagentsList GroupR = listGroup.get(groupPosition);
        String Group = GroupR.string;
        int t1 = listChild.get(groupPosition).get(childPosition).indexOf(": ");
        String Child = listChild.get(groupPosition).get(childPosition).substring(t1 + 2);
        int t2 = Child.indexOf(" <");
        if (t2 != -1)
            Child = Child.substring(0, t2);
        ArrayList<String> arrayList = new ArrayList<>();
        boolean end = false;
        for (Map.Entry<Integer, LinkedHashMap<Integer, LinkedHashMap<Integer, String>>> entry1 :
                ChemLabFuel.ReagentsList.entrySet()) {
            LinkedHashMap<Integer, LinkedHashMap<Integer, String>> value1 = entry1.getValue();
            for (Map.Entry<Integer, LinkedHashMap<Integer, String>> entry2 : value1.entrySet()) {
                LinkedHashMap<Integer, String> value2 = entry2.getValue();
                arrayList.clear();
                for (Map.Entry<Integer, String> entry3 : value2.entrySet()) {
                    if (entry3.getKey() >= 0 && entry3.getKey() <= 16)
                        arrayList.add(entry3.getValue());
                }
                if (Group.contains(arrayList.get(13)) && Child.contains(arrayList.get(15))) {
                    arrayList.add(String.valueOf(entry1.getKey()));
                    end = true;
                    break;
                }
            }
            if (end)
                break;
        }
        return arrayList;
    }

    @Override
    public boolean onChildClick(ExpandableListView expandableListView, View view,
                                int groupPosition, int childPosition, long id) {
        ArrayList<String> arrayList = search(groupPosition, childPosition);
        String fn = "";
        String ln = "";
        String fnG = "";
        String lnG = "";
        String zero1 = "";
        String zero2 = "";
        String editedString = "";
        if (!arrayList.get(12).equals("")) {
            long editedAt = Long.parseLong(arrayList.get(11));
            String editedBy = arrayList.get(12);
            for (int i = 0; i < users.size(); i++) {
                if (users.get(i).get(0).contains(editedBy)) {
                    fn = users.get(i).get(1);
                    ln = users.get(i).get(2);
                    break;
                }
            }
            GregorianCalendar c = new GregorianCalendar();
            c.setTimeInMillis(editedAt);
            if (c.get(Calendar.DATE) < 10) zero1 = "0";
            if (c.get(Calendar.MONTH) < 9) zero2 = "0";
            editedString = zero1 + c.get(Calendar.DATE) + "." + zero2 + (c.get(Calendar.MONTH) + 1)
                    + "." + c.get(Calendar.YEAR) + " " + fn + " " + ln;
        }
        String createdBy = arrayList.get(0);
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).get(0).contains(createdBy)) {
                fnG = users.get(i).get(1);
                lnG = users.get(i).get(2);
                break;
            }
        }
        String[] unit = {"Килограмм", "Миллиграмм", "Литры", "Миллилитры"};
        String data02 = arrayList.get(13);
        String builder = "<strong>" + getString(R.string.lot) + "</strong><br>" + arrayList.get(15) + "<br><br>" +
                "<strong>" + getString(R.string.date_of_receiving) + "</strong><br>" + arrayList.get(1) + "<br><br>" +
                "<strong>" + getString(R.string.supplier) + "</strong><br>" + arrayList.get(2) + "<br><br>" +
                "<strong>" + getString(R.string.complaints) + "</strong><br>" + arrayList.get(3) + "<br><br>" +
                "<strong>" + getString(R.string.qualification) + "</strong><br>" + arrayList.get(4) + "<br><br>" +
                "<strong>" + getString(R.string.manufacturing_date) + "</strong><br>" + arrayList.get(5) + "<br><br>" +
                "<strong>" + getString(R.string.shelf_life) + "</strong><br>" + arrayList.get(6) + "<br><br>" +
                "<strong>" + getString(R.string.storage_conditions) + "</strong><br>" + arrayList.get(7) + "<br><br>" +
                "<strong>" + getString(R.string.unit_of_measurement) + "</strong><br>" + unit[Integer.parseInt(arrayList.get(8))] + "<br><br>" +
                "<strong>" + getString(R.string.amount_remaining) + "</strong><br>" + arrayList.get(9) + "<br><br>" +
                "<strong>" + getString(R.string.minimal_amount) + "</strong><br>" + arrayList.get(10) + "<br><br>" +
                "<strong>" + getString(R.string.responsible) + "</strong><br>" + fnG + " " + lnG + "<br><br>" +
                "<strong>" + getString(R.string.changed) + "</strong><br>" + editedString +
                "<strong>" + getString(R.string.consumption_journal) + arrayList.get(16).replace("\n", "<br>");
        Dialog_description description = Dialog_description.getInstance(data02, builder);
        description.show(getSupportFragmentManager(), "description");
        return false;
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
        Dialog_context_menu menu = Dialog_context_menu.getInstance(position, InventoryList.get(position).data02);
        menu.show(getSupportFragmentManager(), "menu");
        return true;
    }

    @Override
    public void UpdateList() {
        arrayAdapter2.notifyDataSetChanged();
    }

    @Override
    public void onDialogEditPosition(int position) {
        if (isNetworkAvailable()) {
            descriptionEdit = Dialog_description_edit.getInstance(userEdit, InventoryList.get(position).uid,
                    InventoryList.get(position).data02, InventoryList.get(position).data03,
                    InventoryList.get(position).data04, InventoryList.get(position).data05,
                    InventoryList.get(position).data06, InventoryList.get(position).data07,
                    InventoryList.get(position).data08, InventoryList.get(position).data09,
                    InventoryList.get(position).data10, InventoryList.get(position).data12);
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
                    InventoryList.get(position).data02, -1, position);
            confirm.show(getSupportFragmentManager(), "confirm");
        } else {
            Dialog_no_internet noInternet = new Dialog_no_internet();
            noInternet.show(getSupportFragmentManager(), "internet");
        }
    }

    @Override
    public void delete_data(int groupPosition, int position) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        if (groupPosition == -1) {
            mDatabase.child("equipments").child(InventoryList.get(position).uid).removeValue();
        } else {
            ArrayList<String> arrayList = search(groupPosition, position);
            if (ReagentsList.get(Integer.parseInt(arrayList.get(17))).size() == 1)
                mDatabase.child("reagents").child(arrayList.get(14)).removeValue();
            else
                mDatabase.child("reagents").child(arrayList.get(14)).child(arrayList.get(15)).removeValue();
        }
    }

    @Override
    public void set_date(int textView, int year, int month, int dayOfMonth) {
        if (descriptionEdit != null)
            descriptionEdit.set_date(textView, year, month, dayOfMonth);
        if (reagentsDescriptionEdit != null)
            reagentsDescriptionEdit.set_date(textView, year, month, dayOfMonth);
        if (consumption != null)
            consumption.set_date(textView, year, month, dayOfMonth);
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
        menu.findItem(R.id.add).setVisible(fuel.getBoolean("equipments", true));
        menu.findItem(R.id.add_reagent).setVisible(!fuel.getBoolean("equipments", true));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.add) {
            if (isNetworkAvailable()) {
                descriptionEdit = Dialog_description_edit.getInstance(userEdit, (long) InventoryList.size());
                descriptionEdit.show(getSupportFragmentManager(), "edit");
                reagentsDescriptionEdit = null;
                consumption = null;
            } else {
                Dialog_no_internet noInternet = new Dialog_no_internet();
                noInternet.show(getSupportFragmentManager(), "internet");
            }
        }
        if (id == R.id.add_reagent) {
            if (isNetworkAvailable()) {
                reagentsDescriptionEdit = Dialog_reagents_description_edit.getInstance(userEdit, "", "0");
                reagentsDescriptionEdit.show(getSupportFragmentManager(), "edit");
                descriptionEdit = null;
                consumption = null;
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
                Collections.sort(InventoryList, new InventoryListSort(this));
                Collections.sort(listGroup, new ReagentsListSort(this));
            } else {
                editor.putInt("sort", 1);
                editor.apply();
                Collections.sort(InventoryList, new InventoryListSort(this));
                Collections.sort(listGroup, new ReagentsListSort(this));
            }
            inventory_list.clear();
            for (InventoryList inventoryList : InventoryList) {
                inventory_list.add(inventoryList.data01 + ". " + inventoryList.data02);
            }
            arrayAdapter.notifyDataSetChanged();
            arrayAdapter2.notifyDataSetChanged();
            supportInvalidateOptionsMenu();
        }
        if (id == R.id.sortTime) {
            SharedPreferences fuel = getSharedPreferences("fuel", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = fuel.edit();
            if (item.isChecked()) {
                editor.putInt("sort", 0);
                editor.apply();
                Collections.sort(InventoryList, new InventoryListSort(this));
                Collections.sort(listGroup, new ReagentsListSort(this));
            } else {
                editor.putInt("sort", 2);
                editor.apply();
                Collections.sort(InventoryList, new InventoryListSort(this));
                Collections.sort(listGroup, new ReagentsListSort(this));
            }
            inventory_list.clear();
            for (InventoryList inventoryList : InventoryList) {
                inventory_list.add(inventoryList.data01 + ". " + inventoryList.data02);
            }
            arrayAdapter.notifyDataSetChanged();
            arrayAdapter2.notifyDataSetChanged();
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
                                InventoryList.add(new InventoryList((String) hashMap.get("createdBy"),
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
                                        (long) editedAt, (String) editedBy));
                            }
                        }
                    }
                    Collections.sort(InventoryList, new InventoryListSort(ChemLabFuel.this));
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
            mDatabase.child("reagents").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    ReagentsList.clear();
                    listGroup.clear();
                    listChild.clear();
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        String name = (String) data.child("name").getValue();
                        if (name == null)
                            name = "";
                        String id = data.getKey();
                        BigDecimal residueSum = BigDecimal.ZERO;
                        BigDecimal minResidue = BigDecimal.ZERO;
                        LinkedHashMap<Integer, LinkedHashMap<Integer, String>> listN = new LinkedHashMap<>();
                        ArrayList<String> lot = new ArrayList<>();
                        GregorianCalendar g = (GregorianCalendar) Calendar.getInstance();
                        long today = g.getTimeInMillis();
                        long data06 = 0;
                        for (DataSnapshot data2 : data.getChildren()) {
                            String term = "";
                            int i = 0;
                            if (data2.getValue() instanceof HashMap) {
                                HashMap hashMap = (HashMap) data2.getValue();
                                if (hashMap.size() >= 12) {
                                    LinkedHashMap<Integer, String> lists = new LinkedHashMap<>();
                                    Object editedAt = data2.child("editedAt").getValue();
                                    Object editedBy = data2.child("editedBy").getValue();
                                    if (editedAt == null)
                                        editedAt = 0L;
                                    if (editedBy == null)
                                        editedBy = "";
                                    Object data11 = data2.child("data11").getValue();
                                    if (data11 == null)
                                        data11 = "";
                                    lists.put(i, (String) data2.child("createdBy").getValue()); // 0
                                    i++;
                                    lists.put(i, (String) data2.child("data01").getValue()); // 1
                                    i++;
                                    lists.put(i, (String) data2.child("data02").getValue()); // 2
                                    i++;
                                    lists.put(i, (String) data2.child("data03").getValue()); // 3
                                    i++;
                                    lists.put(i, (String) data2.child("data04").getValue()); // 4
                                    i++;
                                    lists.put(i, (String) data2.child("data05").getValue()); // 5
                                    i++;
                                    lists.put(i, String.valueOf(data2.child("data06").getValue())); // 6
                                    i++;
                                    lists.put(i, (String) data2.child("data07").getValue()); // 7
                                    i++;
                                    lists.put(i, String.valueOf(data2.child("data08").getValue())); // 8
                                    i++;
                                    lists.put(i, String.valueOf(data2.child("data09").getValue())); // 9
                                    i++;
                                    lists.put(i, String.valueOf(data2.child("data10").getValue())); // 10
                                    i++;
                                    lists.put(i, String.valueOf(editedAt)); // 11
                                    i++;
                                    lists.put(i, (String) editedBy); // 12
                                    i++;
                                    lists.put(i, name); // 13
                                    i++;
                                    lists.put(i, id); // 14
                                    i++;
                                    lists.put(i, data2.getKey()); // 15
                                    i++;
                                    lists.put(i, (String) data11); // 16
                                    listN.put(Integer.parseInt(data2.getKey()), lists);

                                    String data05 = (String) data2.child("data05").getValue();
                                    String[] d = data05.split("-");
                                    g.set(Integer.parseInt(d[0]), Integer.parseInt(d[1]) - 1, Integer.parseInt(d[2]));
                                    data06 = g.getTimeInMillis();
                                    g.add(Calendar.MONTH, (int) (long) data2.child("data06").getValue());
                                    BigDecimal residue;
                                    if (data2.child("data09").getValue() instanceof Double)
                                        residue = BigDecimal.valueOf((double) data2.child("data09").getValue());
                                    else
                                        residue = BigDecimal.valueOf((double) (long) data2.child("data09").getValue());
                                    if (today < g.getTimeInMillis()) {
                                        residueSum = residue.add(residue);
                                        g.add(Calendar.DATE, -45);
                                        if (today > g.getTimeInMillis())
                                            term = " <font color=#9A2828>" + getString(R.string.shelf_life_expires) + "</font>";
                                    } else {
                                        term = " <font color=#9A2828>" + getString(R.string.shelf_life_has_expired) + "</font>";
                                    }
                                    if (data2.child("data10").getValue() instanceof Double)
                                        minResidue = BigDecimal.valueOf((double) data2.child("data10").getValue());
                                    else
                                        minResidue = BigDecimal.valueOf((double) (long) data2.child("data10").getValue());
                                    lot.add(getString(R.string.lot) + ": " + data2.getKey() + term);
                                }
                            }
                        }
                        listGroup.add(new ReagentsList(data06, Integer.parseInt(id), name, residueSum, minResidue));
                        listChild.add(lot);
                        ReagentsList.put(Integer.parseInt(id), listN);
                    }
                    Collections.sort(listGroup, new ReagentsListSort(ChemLabFuel.this));
                    arrayAdapter2.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);

                    //sendBroadcast(new Intent(ChemLabFuel.this, ReceiverSetAlarm.class));
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
            String data8 = InventoryList.get(position).data08;
            if (data8 != null && !data8.equals("")) {
                String[] t1 = data8.split("-");
                GregorianCalendar calendar = new GregorianCalendar();
                String data09 = InventoryList.get(position).data09;
                String data10 = InventoryList.get(position).data10;
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
    }

    private class ReagentsListAdapter extends BaseExpandableListAdapter {

        private final SharedPreferences fuel;

        ReagentsListAdapter() {
            fuel = getSharedPreferences("fuel", Context.MODE_PRIVATE);
        }

        @Override
        public int getGroupCount() {
            return listGroup.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return listChild.get(groupPosition).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return listGroup.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return listChild.get(groupPosition).get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            ViewHolderGroup group;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.group_view, parent, false);
                group = new ViewHolderGroup();
                convertView.setTag(group);
                group.textView = convertView.findViewById(R.id.textGroup);
            } else {
                group = (ViewHolderGroup) convertView.getTag();
            }
            group.textView.setTextSize(fuel.getInt("fontSize", 18));
            String residue = " (" + getString(R.string.residue) + ": " +
                    listGroup.get(groupPosition).residue + ")";
            int compare = listGroup.get(groupPosition).residue.compareTo(listGroup.get(groupPosition).minResidue);
            if (compare <= 0)
                residue = " (<font color=#9A2828" + getString(R.string.residue) + ": " +
                        listGroup.get(groupPosition).residue + "</font>)";
            group.textView.setText(Html.fromHtml(listGroup.get(groupPosition).id + ". " +
                    listGroup.get(groupPosition).string + residue));
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                                 View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.simple_list_item3, parent, false);
                viewHolder = new ViewHolder();
                convertView.setTag(viewHolder);
                viewHolder.textView = convertView.findViewById(R.id.label3);
                viewHolder.button_popup = convertView.findViewById(R.id.button_popup3);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.button_popup.setOnClickListener(v -> showPopupMenu(viewHolder.button_popup,
                    groupPosition, childPosition));
            viewHolder.textView.setText(Html.fromHtml(listChild.get(groupPosition).get(childPosition)));
            viewHolder.textView.setTextSize(fuel.getInt("fontSize", 18));
            return convertView;
        }

        private void showPopupMenu(ImageView imageView, int groupPosition, int childPosition) {
            PopupMenu popup = new PopupMenu(ChemLabFuel.this, imageView);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.popup_reagent, popup.getMenu());
            for (int i = 0; i < popup.getMenu().size(); i++) {
                MenuItem item = popup.getMenu().getItem(i);
                SpannableString spanString = new SpannableString(popup.getMenu().getItem(i).getTitle().toString());
                int end = spanString.length();
                spanString.setSpan(new AbsoluteSizeSpan(18, true), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                item.setTitle(spanString);
            }
            popup.setOnMenuItemClickListener(menuItem -> {
                popup.dismiss();
                if (menuItem.getItemId() == R.id.menu_add) {
                    reagentsDescriptionEdit = Dialog_reagents_description_edit.getInstance(userEdit,
                            listGroup.get(groupPosition).string, listGroup.get(groupPosition).minResidue.toString());
                    reagentsDescriptionEdit.show(getSupportFragmentManager(), "edit");
                    descriptionEdit = null;
                    consumption = null;
                    return true;
                }
                if (menuItem.getItemId() == R.id.menu_consumption) {
                    ArrayList<String> arrayList = search(groupPosition, childPosition);
                    consumption = Dialog_reagent_consumption.getInstance(Integer.parseInt(arrayList.get(14)),
                            Integer.parseInt(arrayList.get(15)));
                    consumption.show(getSupportFragmentManager(), "consumption");
                    descriptionEdit = null;
                    reagentsDescriptionEdit = null;
                    return true;
                }
                if (menuItem.getItemId() == R.id.menu_consumption) {
                    ArrayList<String> arrayList = search(groupPosition, childPosition);
                    reagentsDescriptionEdit = Dialog_reagents_description_edit.getInstance(userEdit,
                            Integer.parseInt(arrayList.get(14)), Integer.parseInt(arrayList.get(15)));
                    reagentsDescriptionEdit.show(getSupportFragmentManager(), "edit");
                    descriptionEdit = null;
                    consumption = null;
                    return true;
                }
                if (menuItem.getItemId() == R.id.menu_remove) {
                    String listGroupSt = listChild.get(groupPosition).get(childPosition);
                    int t1 = listGroupSt.indexOf(" <");
                    if (t1 != -1)
                        listGroupSt = listGroupSt.substring(0, t1);
                    Dialog_delete_confirm confirm =
                            Dialog_delete_confirm.getInstance(
                                    listGroup.get(groupPosition).string + " " + listGroupSt,
                                    groupPosition, childPosition);
                    confirm.show(getSupportFragmentManager(), "confirm");
                    return true;
                }
                return false;
            });
            popup.show();
        }
    }

    private static class ViewHolderGroup {
        TextView textView;
    }

    private static class ViewHolder {
        TextView textView;
        ImageView button_popup;
    }
}
