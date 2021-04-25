package korneluk.serhij.chemlabfuel;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.Map;

public class Dialog_reagents_description_edit extends DialogFragment {

    private static boolean add;
    TextView textViewTitleR;
    private EditText editText2Re;
    private TextView textView3Re;
    private EditText editText5Re;
    private EditText editText6Re;
    private EditText editText7Re;
    private TextView textView8Re;
    private EditText editText9Re;
    private EditText editText10Re;
    private Spinner spinner11Re;
    private EditText editText12Re;
    private EditText editText13Re;
    private EditText editText14Re;
    private EditText editText15Re;
    private Spinner spinner9R;
    private String user = "";
    private String title = "";
    private int groupPosition = 0;
    private int childPosition = 0;
    private int unit = 0;
    private static final String[] units = {"Килограмм", "Миллиграмм", "Литры", "Миллилитры"};
    private listUpdaterListener listener;

    static Dialog_reagents_description_edit getInstance(String user, int groupPosition, int childPosition) {
        Dialog_reagents_description_edit descriptionEdit = new Dialog_reagents_description_edit();
        Bundle bundle = new Bundle();
        bundle.putString("user", user);
        bundle.putInt("groupPosition", groupPosition);
        bundle.putInt("childPosition", childPosition);
        descriptionEdit.setArguments(bundle);
        add = false;
        return descriptionEdit;
    }

    static Dialog_reagents_description_edit getInstance(String user, String title, String minResidue, int unit) {
        Dialog_reagents_description_edit descriptionEdit = new Dialog_reagents_description_edit();
        Bundle bundle = new Bundle();
        bundle.putString("user", user);
        bundle.putString("title", title);
        bundle.putString("minResidue", minResidue);
        bundle.putInt("unit", unit);
        descriptionEdit.setArguments(bundle);
        add = true;
        return descriptionEdit;
    }

    interface listUpdaterListener {
        void UpdateList();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            try {
                listener = (listUpdaterListener) context;
            } catch (ClassCastException e) {
                throw new ClassCastException(context.toString() + " must implement listUpdateListener");
            }
        }
    }

    void set_date(int textView, int year, int month, int dayOfMonth) {
        String zero1 = "";
        String zero2 = "";
        if (month < 9) zero1 = "0";
        if (dayOfMonth < 10) zero2 = "0";
        switch (textView) {
            case 3:
                if (year == 0)
                    textView3Re.setText("");
                else
                    textView3Re.setText(getString(R.string.set_date, year, zero1, month + 1, zero2, dayOfMonth));
                break;
            case 8:
                if (year == 0)
                    textView8Re.setText("");
                else if (dayOfMonth == -1)
                    textView8Re.setText(getString(R.string.set_date2, year, zero1, month + 1));
                else
                    textView8Re.setText(getString(R.string.set_date, year, zero1, month + 1, zero2, dayOfMonth));
                break;
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        String[] year_month = {"Год", "Месяц"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_reagents_description_edit, null);
        textViewTitleR = view.findViewById(R.id.textViewTitleR);
        editText2Re = view.findViewById(R.id.editText2Re);
        editText2Re.addTextChangedListener(new MyTextWatcher(editText2Re));
        textView3Re = view.findViewById(R.id.textView3Re);
        editText5Re = view.findViewById(R.id.editText5Re);
        editText6Re = view.findViewById(R.id.editText6Re);
        editText7Re = view.findViewById(R.id.editText7Re);
        textView8Re = view.findViewById(R.id.textView8Re);
        editText9Re = view.findViewById(R.id.editText9Re);
        editText10Re = view.findViewById(R.id.editText10Re);
        spinner11Re = view.findViewById(R.id.spinner11Re);
        spinner9R = view.findViewById(R.id.spinner9R);
        spinner9R.setAdapter(new ListAdapter(getActivity(), year_month));
        spinner11Re.setAdapter(new ListAdapter(getActivity(), units));
        spinner11Re.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        editText12Re = view.findViewById(R.id.editText12Re);
        editText12Re.addTextChangedListener(new MyTextWatcher(editText12Re));
        editText13Re = view.findViewById(R.id.editText13Re);
        editText13Re.addTextChangedListener(new MyTextWatcher(editText13Re));
        editText14Re = view.findViewById(R.id.editText14Re);
        editText15Re = view.findViewById(R.id.editText15Re);
        Button button3R = view.findViewById(R.id.button3R);
        button3R.setOnClickListener(v -> {
            GregorianCalendar c;
            if (textView3Re.getText().toString().equals("")) {
                c = (GregorianCalendar) Calendar.getInstance();
            } else {
                String[] t1 = textView3Re.getText().toString().split("-");
                c = new GregorianCalendar(Integer.parseInt(t1[0]), Integer.parseInt(t1[1]) - 1, Integer.parseInt(t1[2]));
            }
            TextView textView3R = view.findViewById(R.id.textView3R);
            Dialog_date date = Dialog_date.getInstance(c.getTimeInMillis(), 3, textView3R.getText().toString());
            date.show(getFragmentManager(), "date");
        });
        Button button8R = view.findViewById(R.id.button8R);
        button8R.setOnClickListener(v -> {
            GregorianCalendar c;
            if (textView8Re.getText().toString().equals("")) {
                c = (GregorianCalendar) Calendar.getInstance();
            } else {
                String[] t1 = textView8Re.getText().toString().split("-");
                if (t1.length == 3)
                    c = new GregorianCalendar(Integer.parseInt(t1[0]), Integer.parseInt(t1[1]) - 1, Integer.parseInt(t1[2]));
                else
                    c = new GregorianCalendar(Integer.parseInt(t1[0]), Integer.parseInt(t1[1]) - 1, 1);
            }
            TextView textView8R = view.findViewById(R.id.textView8R);
            Dialog_date date = Dialog_date.getInstance(c.getTimeInMillis(), 8, textView8R.getText().toString());
            date.show(getFragmentManager(), "date");
        });
        String minResidue = "";
        if (getArguments() != null) {
            user = getArguments().getString("user", "");
            title = getArguments().getString("title", "");
            groupPosition = getArguments().getInt("groupPosition", 1);
            childPosition = getArguments().getInt("childPosition", 1);
            minResidue = getArguments().getString("minResidue", "");
            unit = getArguments().getInt("unit", 0);
        }
        if (add) {
            textViewTitleR.setText(R.string.add_entry);
            editText2Re.setText(title);
            editText13Re.setText(minResidue);
            editText13Re.setImeOptions(EditorInfo.IME_ACTION_GO);
            editText13Re.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    send();
                    return true;
                }
                return false;
            });
            spinner11Re.setSelection(unit);
            TextView textView15R = view.findViewById(R.id.textView15R);
            textView15R.setVisibility(View.GONE);
            editText15Re.setVisibility(View.GONE);
        } else {
            spinner9R.setVisibility(View.GONE);
            textViewTitleR.setText(ChemLabFuel.ReagentsList.get(groupPosition).get(childPosition).get(13));
            editText2Re.setText(ChemLabFuel.ReagentsList.get(groupPosition).get(childPosition).get(13));
            textView3Re.setText(ChemLabFuel.ReagentsList.get(groupPosition).get(childPosition).get(1));
            editText5Re.setText(ChemLabFuel.ReagentsList.get(groupPosition).get(childPosition).get(2));
            editText6Re.setText(ChemLabFuel.ReagentsList.get(groupPosition).get(childPosition).get(3));
            editText7Re.setText(ChemLabFuel.ReagentsList.get(groupPosition).get(childPosition).get(4));
            textView8Re.setText(ChemLabFuel.ReagentsList.get(groupPosition).get(childPosition).get(5));
            editText9Re.setText(String.valueOf(ChemLabFuel.ReagentsList.get(groupPosition).get(childPosition).get(6)));
            editText10Re.setText(ChemLabFuel.ReagentsList.get(groupPosition).get(childPosition).get(7));
            spinner11Re.setSelection(Integer.parseInt(ChemLabFuel.ReagentsList.get(groupPosition).get(childPosition).get(8)));
            editText12Re.setText(String.valueOf(ChemLabFuel.ReagentsList.get(groupPosition).get(childPosition).get(9)));
            editText13Re.setText(String.valueOf(ChemLabFuel.ReagentsList.get(groupPosition).get(childPosition).get(10)));
            editText14Re.setText(String.valueOf(ChemLabFuel.ReagentsList.get(groupPosition).get(childPosition).get(17)));
            editText15Re.setText(String.valueOf(ChemLabFuel.ReagentsList.get(groupPosition).get(childPosition).get(16)));
        }

        editText2Re.setSelection(editText2Re.getText().length());
        editText5Re.setSelection(editText5Re.getText().length());
        editText6Re.setSelection(editText6Re.getText().length());
        editText7Re.setSelection(editText7Re.getText().length());
        editText9Re.setSelection(editText9Re.getText().length());
        editText10Re.setSelection(editText10Re.getText().length());
        editText12Re.setSelection(editText12Re.getText().length());
        editText13Re.setSelection(editText13Re.getText().length());
        editText14Re.setSelection(editText14Re.getText().length());
        // show the keyboard
        //InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        //imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        builder.setView(view);

        builder.setPositiveButton(getString(R.string.save), (dialog, which) -> {
            send();
        });
        builder.setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.cancel());
        AlertDialog alert = builder.create();
        alert.setOnShowListener(dialog -> {
            Button btnPositive = alert.getButton(Dialog.BUTTON_POSITIVE);
            btnPositive.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            Button btnNegative = alert.getButton(Dialog.BUTTON_NEGATIVE);
            btnNegative.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        });
        return alert;
    }

    private void send() {
        if (editText6Re.getText().toString().trim().equals("")) {
            editText6Re.setText(R.string.no);
        }
        if (textView8Re.getText().toString().trim().equals("")) {
            Calendar calendar = Calendar.getInstance();
            int month = calendar.get(Calendar.MONTH) + 1;
            String zero = "";
            if (month < 10) zero = "0";
            textView8Re.setText(calendar.get(Calendar.YEAR) + "-" + zero + month);
        }
        if (editText9Re.getText().toString().trim().equals("")) {
            editText9Re.setText("1");
        }
        if (editText10Re.getText().toString().trim().equals("")) {
            editText10Re.setText(R.string.normal);
        }
        if (editText12Re.getText().toString().trim().equals("")) {
            editText12Re.setText("0");
        }
        if (editText13Re.getText().toString().trim().equals("")) {
            editText13Re.setText("0");
        }
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        GregorianCalendar g = (GregorianCalendar) Calendar.getInstance();

        String reagentNumber = String.valueOf(groupPosition);
        String lotNumber = String.valueOf(childPosition);
        long text9 = Long.valueOf(editText9Re.getText().toString().trim());
        if (add && spinner9R.getSelectedItemPosition() == 0) {
            text9 = text9 * 12;
        }
        if (add) {
            if (ChemLabFuel.ReagentsList.size() != 0) {
                for (Map.Entry<Integer, LinkedHashMap<Integer, LinkedHashMap<Integer, String>>> entry1 :
                        ChemLabFuel.ReagentsList.entrySet()) {
                    LinkedHashMap<Integer, LinkedHashMap<Integer, String>> value1 = entry1.getValue();
                    for (Map.Entry<Integer, LinkedHashMap<Integer, String>> entry2 : value1.entrySet()) {
                        LinkedHashMap<Integer, String> value2 = entry2.getValue();
                        String name = "no";
                        for (Map.Entry<Integer, String> entry3 : value2.entrySet()) {
                            if (entry3.getKey() == 13) {
                                name = entry3.getValue();
                            }
                            if (entry3.getKey() == 14) {
                                if (title.equals("")) {
                                    groupPosition = Integer.parseInt(entry3.getValue()) + 1;
                                    reagentNumber = String.valueOf(groupPosition);
                                } else if (editText2Re.getText().toString().trim().contains(name)) {
                                    groupPosition = Integer.parseInt(entry3.getValue());
                                    reagentNumber = String.valueOf(groupPosition);
                                }
                            }
                            if (entry3.getKey() == 15) {
                                if (editText2Re.getText().toString().trim().contains(name)) {
                                    childPosition = Integer.parseInt(entry3.getValue()) + 1;
                                    lotNumber = String.valueOf(childPosition);
                                }
                            }
                        }
                    }

                }
            }
            mDatabase.child("reagents").child(reagentNumber).child(lotNumber).child("createdAt").setValue(g.getTimeInMillis());
            mDatabase.child("reagents").child(reagentNumber).child(lotNumber).child("createdBy").setValue(user);
        }
        mDatabase.child("reagents").child(reagentNumber).child("name").setValue(editText2Re.getText().toString().trim());
        mDatabase.child("reagents").child(reagentNumber).child(lotNumber).child("data01").setValue(textView3Re.getText().toString().trim());
        mDatabase.child("reagents").child(reagentNumber).child(lotNumber).child("data02").setValue(editText5Re.getText().toString().trim());
        mDatabase.child("reagents").child(reagentNumber).child(lotNumber).child("data03").setValue(editText6Re.getText().toString().trim());
        mDatabase.child("reagents").child(reagentNumber).child(lotNumber).child("data04").setValue(editText7Re.getText().toString().trim());
        mDatabase.child("reagents").child(reagentNumber).child(lotNumber).child("data05").setValue(textView8Re.getText().toString().trim());
        mDatabase.child("reagents").child(reagentNumber).child(lotNumber).child("data06").setValue(text9);
        mDatabase.child("reagents").child(reagentNumber).child(lotNumber).child("data07").setValue(editText10Re.getText().toString().trim());
        mDatabase.child("reagents").child(reagentNumber).child(lotNumber).child("data08").setValue((long) spinner11Re.getSelectedItemPosition());
        mDatabase.child("reagents").child(reagentNumber).child(lotNumber).child("data09").setValue(Double.valueOf(editText12Re.getText().toString().trim().replace(".", ",")));
        mDatabase.child("reagents").child(reagentNumber).child(lotNumber).child("data10").setValue(Double.valueOf(editText13Re.getText().toString().trim().replace(".", ",")));
        mDatabase.child("reagents").child(reagentNumber).child(lotNumber).child("data11").setValue(editText15Re.getText().toString().trim());
        mDatabase.child("reagents").child(reagentNumber).child(lotNumber).child("data12").setValue(editText14Re.getText().toString().trim());
        if (!add) {
            mDatabase.child("reagents").child(reagentNumber).child(lotNumber).child("editedAt").setValue(g.getTimeInMillis());
            mDatabase.child("reagents").child(reagentNumber).child(lotNumber).child("editedBy").setValue(user);
        }
        getActivity().sendBroadcast(new Intent(getActivity(), ReceiverSetAlarm.class));
        listener.UpdateList();
        getDialog().cancel();
    }

    private class MyTextWatcher implements TextWatcher {

        private int editPosition;
        private final EditText editText;

        MyTextWatcher(EditText editText) {
            this.editText = editText;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            editPosition = start + count;
        }

        @Override
        public void afterTextChanged(Editable s) {
            String edit = s.toString();
            if (editText.getId() == R.id.editText2Re && !edit.equals("")) {
                textViewTitleR.setText(edit);
            } else {
                edit = edit.replace(".", ",");
                editText.removeTextChangedListener(this);
                editText.setText(edit);
                editText.setSelection(editPosition);
                editText.addTextChangedListener(this);
            }
        }
    }

    private class ListAdapter extends ArrayAdapter<String> {

        private String[] unitsA;

        ListAdapter(Context context, String[] units) {
            super(context, R.layout.simple_list_item2, units);
            unitsA = units;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View mView, @NonNull ViewGroup parent) {
            ViewHolder viewHolder;
            if (mView == null) {
                LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                mView = vi.inflate(R.layout.simple_list_item2, parent, false);
                viewHolder = new ViewHolder();
                mView.setTag(viewHolder);
                viewHolder.textView = mView.findViewById(R.id.label);
            } else {
                viewHolder = (ViewHolder) mView.getTag();
            }
            viewHolder.textView.setText(unitsA[position]);
            return mView;
        }
    }

    private static class ViewHolder {
        TextView textView;
    }
}
