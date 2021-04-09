package korneluk.serhij.chemlabfuel;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
    private String user = "";
    private String title = "";
    private int groupPosition = 0;
    private int childPosition = 0;
    private static final String[] data = {"Килограмм", "Миллиграмм", "Литры", "Миллилитры"};
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

    static Dialog_reagents_description_edit getInstance(String user, String title, String minResidue) {
        Dialog_reagents_description_edit descriptionEdit = new Dialog_reagents_description_edit();
        Bundle bundle = new Bundle();
        bundle.putString("user", user);
        bundle.putString("title", title);
        bundle.putString("minResidue", minResidue);
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
                else
                    textView8Re.setText(getString(R.string.set_date, year, zero1, month + 1, zero2, dayOfMonth));
                break;
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_reagents_description_edit, null);
        TextView textViewTitleR = view.findViewById(R.id.textViewTitleR);

        editText2Re = view.findViewById(R.id.editText2Re);
        textView3Re = view.findViewById(R.id.textView3Re);
        editText5Re = view.findViewById(R.id.editText5Re);
        editText6Re = view.findViewById(R.id.editText6Re);
        editText7Re = view.findViewById(R.id.editText7Re);
        textView8Re = view.findViewById(R.id.textView8Re);
        editText9Re = view.findViewById(R.id.editText9Re);
        editText10Re = view.findViewById(R.id.editText10Re);
        spinner11Re = view.findViewById(R.id.spinner11Re);
        ArrayAdapter<String> adapter = new ListAdapter(getActivity());
        spinner11Re.setAdapter(adapter);
        spinner11Re.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        editText12Re = view.findViewById(R.id.editText12Re);
        editText13Re = view.findViewById(R.id.editText13Re);
        editText13Re.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_GO) {
                send();
                return true;
            }
            return false;
        });
        textView3Re.setOnClickListener(v -> {
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
        textView8Re.setOnClickListener(v -> {
            GregorianCalendar c;
            if (textView8Re.getText().toString().equals("")) {
                c = (GregorianCalendar) Calendar.getInstance();
            } else {
                String[] t1 = textView8Re.getText().toString().split("-");
                c = new GregorianCalendar(Integer.parseInt(t1[0]), Integer.parseInt(t1[1]) - 1, Integer.parseInt(t1[2]));
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
            minResidue = getArguments().getString("minResidue", "0");
        }
        if (add) {
            textViewTitleR.setText(R.string.add_entry);
            editText2Re.setText(title);
            editText13Re.setText(minResidue);
        } else {
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
        }

        editText2Re.setSelection(editText2Re.getText().length());
        editText5Re.setSelection(editText5Re.getText().length());
        editText6Re.setSelection(editText6Re.getText().length());
        editText7Re.setSelection(editText7Re.getText().length());
        editText9Re.setSelection(editText9Re.getText().length());
        editText10Re.setSelection(editText10Re.getText().length());
        editText12Re.setSelection(editText12Re.getText().length());
        editText13Re.setSelection(editText13Re.getText().length());
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
        if (editText9Re.getText().toString().trim().equals("")) {
            editText9Re.setText("12");
        }
        if (editText10Re.getText().toString().trim().equals("")) {
            editText10Re.setText(R.string.normal);
        }
        if (!editText12Re.getText().toString().trim().equals("") &&
                !editText12Re.getText().toString().trim().equals("") &&
                !textView8Re.getText().toString().trim().equals("")) {

            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            GregorianCalendar g = (GregorianCalendar) Calendar.getInstance();

            String reagentNumber = String.valueOf(groupPosition);
            String lotNumber = String.valueOf(childPosition);

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
                //uid = mDatabase.child("reagents").push().getKey();
                mDatabase.child("reagents").child(reagentNumber).child(lotNumber).child("createdAt").setValue(g.getTimeInMillis());
                mDatabase.child("reagents").child(reagentNumber).child(lotNumber).child("createdBy").setValue(user);
            }
            mDatabase.child("reagents").child(reagentNumber).child("name").setValue(editText2Re.getText().toString().trim());
            mDatabase.child("reagents").child(reagentNumber).child(lotNumber).child("data01").setValue(textView3Re.getText().toString().trim());
            mDatabase.child("reagents").child(reagentNumber).child(lotNumber).child("data02").setValue(editText5Re.getText().toString().trim());
            mDatabase.child("reagents").child(reagentNumber).child(lotNumber).child("data03").setValue(editText6Re.getText().toString().trim());
            mDatabase.child("reagents").child(reagentNumber).child(lotNumber).child("data04").setValue(editText7Re.getText().toString().trim());
            mDatabase.child("reagents").child(reagentNumber).child(lotNumber).child("data05").setValue(textView8Re.getText().toString().trim());
            mDatabase.child("reagents").child(reagentNumber).child(lotNumber).child("data06").setValue(Long.valueOf(editText9Re.getText().toString().trim()));
            mDatabase.child("reagents").child(reagentNumber).child(lotNumber).child("data07").setValue(editText10Re.getText().toString().trim());
            mDatabase.child("reagents").child(reagentNumber).child(lotNumber).child("data08").setValue((long) spinner11Re.getSelectedItemPosition());
            mDatabase.child("reagents").child(reagentNumber).child(lotNumber).child("data09").setValue(Double.valueOf(editText12Re.getText().toString().trim()));
            mDatabase.child("reagents").child(reagentNumber).child(lotNumber).child("data10").setValue(Double.valueOf(editText13Re.getText().toString().trim()));
            if (!add) {
                mDatabase.child("reagents").child(reagentNumber).child(lotNumber).child("editedAt").setValue(g.getTimeInMillis());
                mDatabase.child("reagents").child(reagentNumber).child(lotNumber).child("editedBy").setValue(user);
            }
            getActivity().sendBroadcast(new Intent(getActivity(), ReceiverSetAlarm.class));
            listener.UpdateList();
        } else {
            LinearLayout layout = new LinearLayout(getActivity());
            layout.setBackgroundResource(R.color.colorPrimary);
            TextView toast = new TextView(getActivity());
            toast.setTextColor(getResources().getColor(R.color.colorIcons));
            toast.setPadding(10, 10, 10, 10);
            toast.setText(R.string.error);
            toast.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            layout.addView(toast);
            Toast mes = new Toast(getActivity());
            mes.setDuration(Toast.LENGTH_LONG);
            mes.setView(layout);
            mes.show();
        }
        getDialog().cancel();
    }

    private class ListAdapter extends ArrayAdapter<String> {

        ListAdapter(Context context) {
            super(context, R.layout.simple_list_item2, data);
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
            viewHolder.textView.setText(data[position]);
            return mView;
        }
    }

    private static class ViewHolder {
        TextView textView;
    }
}
