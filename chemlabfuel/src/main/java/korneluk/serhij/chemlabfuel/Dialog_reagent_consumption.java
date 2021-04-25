package korneluk.serhij.chemlabfuel;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * A simple {@link DialogFragment} subclass.
 * Use the {@link Dialog_reagent_consumption#getInstance} factory method to
 * get an instance of this fragment.
 */
public class Dialog_reagent_consumption extends DialogFragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String GROUP_POSITION = "groupPosition";
    private static final String CHILD_POSITION = "childPosition";
    private static final String UNIT = "unit";

    private int groupPosition = 0;
    private int childPosition = 0;
    private int unit = 0;
    private TextView textView1Ce;
    private EditText editText2Ce;
    private EditText editText3Ce;
    private EditText editText4Ce;
    private GregorianCalendar c;
    private String[] units = {"килограммах", "миллиграммах", "литрах", "миллилитрах"};

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param groupPosition Parameter 1.
     * @param childPosition Parameter 2.
     * @return A new instance of fragment Dialog_reagent_consumption.
     */
    static Dialog_reagent_consumption getInstance(int groupPosition, int childPosition, int unit) {
        Dialog_reagent_consumption fragmentDescription = new Dialog_reagent_consumption();
        Bundle args = new Bundle();
        args.putInt(GROUP_POSITION, groupPosition);
        args.putInt(CHILD_POSITION, childPosition);
        args.putInt(UNIT, unit);
        fragmentDescription.setArguments(args);
        return fragmentDescription;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            groupPosition = getArguments().getInt(GROUP_POSITION, 1);
            childPosition = getArguments().getInt(CHILD_POSITION, 1);
            unit = getArguments().getInt(UNIT, 0);
        }
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_reagent_consumption, null);
        TextView textViewTitleC = view.findViewById(R.id.textViewTitleC);
        textView1Ce = view.findViewById(R.id.textView1Ce);
        TextView amount = view.findViewById(R.id.quantity);
        amount.setText(amount.getText().toString() + " в " + units[unit]);
        c = (GregorianCalendar) Calendar.getInstance();
        set_date(1, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE));
        Button button1C = view.findViewById(R.id.button1C);
        button1C.setOnClickListener(v -> {
            String[] t1 = textView1Ce.getText().toString().split("-");
            c = new GregorianCalendar(Integer.parseInt(t1[0]), Integer.parseInt(t1[1]) - 1, Integer.parseInt(t1[2]));
            TextView textView1C = view.findViewById(R.id.textView1C);
            Dialog_date date = Dialog_date.getInstance(c.getTimeInMillis(), 1, textView1C.getText().toString());
            date.show(getFragmentManager(), "date");
        });
        textViewTitleC.setText(ChemLabFuel.ReagentsList.get(groupPosition).get(childPosition).get(13));
        editText3Ce = view.findViewById(R.id.editText3Ce);
        editText3Ce.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_GO) {
                send();
                return true;
            }
            return false;
        });
        editText2Ce = view.findViewById(R.id.editText2Ce);
        editText2Ce.addTextChangedListener(new MyTextWatcher(editText2Ce));
        editText4Ce = view.findViewById(R.id.editText4Ce);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setPositiveButton(getString(R.string.save), (dialog, which) -> {
            send();
        });
        builder.setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.cancel());
        builder.setView(view);
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
        if (!editText2Ce.getText().toString().trim().equals("") && !editText3Ce.getText().toString().equals("")) {
            String journal = ChemLabFuel.ReagentsList.get(groupPosition).get(childPosition).get(16);
            if (!journal.equals(""))
                journal += "\n";
            journal += textView1Ce.getText().toString() + " " +
                    new BigDecimal(editText2Ce.getText().toString().trim() + " " +
                            editText3Ce.getText().toString() + "\n");
            String reagentNumber = String.valueOf(groupPosition);
            String lotNumber = String.valueOf(childPosition);
            BigDecimal con = new BigDecimal(editText2Ce.getText().toString().trim());
            BigDecimal consumption =
                    new BigDecimal(ChemLabFuel.ReagentsList.get(groupPosition).get(childPosition).get(9)).subtract(con);
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.child("reagents").child(reagentNumber).child(lotNumber).child("data09").setValue(consumption.doubleValue());
            mDatabase.child("reagents").child(reagentNumber).child(lotNumber).child("data11").setValue(journal);
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

    void set_date(int textView, int year, int month, int dayOfMonth) {
        String zero1 = "";
        String zero2 = "";
        if (month < 9) zero1 = "0";
        if (dayOfMonth < 10) zero2 = "0";
        if (textView == 1) {
            if (year == 0)
                textView1Ce.setText("");
            else
                textView1Ce.setText(getString(R.string.set_date, year, zero1, month + 1, zero2, dayOfMonth));
        }
    }

    private class MyTextWatcher implements TextWatcher {

        private int editPosition;
        private EditText editText;

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
            edit = edit.replace(".", ",");
            editText.removeTextChangedListener(this);
            editText.setText(edit);
            editText.setSelection(editPosition);
            editText.addTextChangedListener(this);
        }
    }
}
