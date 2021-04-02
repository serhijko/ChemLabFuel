package korneluk.serhij.chemlabfuel;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Dialog_description_edit extends DialogFragment {

    static Dialog_description_edit getInstance(String uid, String data2, String data3, String data4, String data5, String data6, String data7, String data8, String data9, String data10) {
        Dialog_description_edit descriptionEdit = new Dialog_description_edit();
        Bundle bundle = new Bundle();
        bundle.putString("uid", uid);
        bundle.putString("data2", data2);
        bundle.putString("data3", data3);
        bundle.putString("data4", data4);
        bundle.putString("data5", data5);
        bundle.putString("data6", data6);
        bundle.putString("data7", data7);
        bundle.putString("data8", data8);
        bundle.putString("data9", data9);
        bundle.putString("data10", data10);
        descriptionEdit.setArguments(bundle);
        return descriptionEdit;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_description_edit, null);
        TextView textViewTitle = view.findViewById(R.id.textViewTitle);
        EditText editText2 = view.findViewById(R.id.editText2e);
        EditText editText3 = view.findViewById(R.id.editText3e);
        EditText editText4 = view.findViewById(R.id.editText4e);
        EditText editText5 = view.findViewById(R.id.editText5e);
        EditText editText6 = view.findViewById(R.id.editText6e);
        EditText editText7 = view.findViewById(R.id.editText7e);
        EditText editText8 = view.findViewById(R.id.editText8e);
        EditText editText9 = view.findViewById(R.id.editText9e);
        EditText editText10 = view.findViewById(R.id.editText10e);
        String uid = getArguments().getString("uid", "");
        textViewTitle.setText(getArguments().getString("data2"));
        editText2.setText(getArguments().getString("data2"));
        editText3.setText(getArguments().getString("data3"));
        editText4.setText(getArguments().getString("data4"));
        editText5.setText(getArguments().getString("data5"));
        editText6.setText(getArguments().getString("data6"));
        editText7.setText(getArguments().getString("data7"));
        editText8.setText(getArguments().getString("data8"));
        editText9.setText(getArguments().getString("data9"));
        editText10.setText(getArguments().getString("data10"));
        editText2.setSelection(editText2.getText().length());
        editText3.setSelection(editText3.getText().length());
        editText4.setSelection(editText4.getText().length());
        editText5.setSelection(editText5.getText().length());
        editText6.setSelection(editText6.getText().length());
        editText7.setSelection(editText7.getText().length());
        editText8.setSelection(editText8.getText().length());
        editText9.setSelection(editText9.getText().length());
        editText10.setSelection(editText10.getText().length());

        // Show keyboard
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        builder.setView(view);

        builder.setPositiveButton("Сохранить", (dialogInterface, which) -> {
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.child("equipments").child(uid).child("data02").setValue(editText2.getText().toString());
            mDatabase.child("equipments").child(uid).child("data03").setValue(editText3.getText().toString());
            mDatabase.child("equipments").child(uid).child("data04").setValue(editText4.getText().toString());
            mDatabase.child("equipments").child(uid).child("data05").setValue(editText5.getText().toString());
            mDatabase.child("equipments").child(uid).child("data06").setValue(editText6.getText().toString());
            mDatabase.child("equipments").child(uid).child("data07").setValue(editText7.getText().toString());
            mDatabase.child("equipments").child(uid).child("data08").setValue(editText8.getText().toString());
            mDatabase.child("equipments").child(uid).child("data09").setValue(editText9.getText().toString());
            mDatabase.child("equipments").child(uid).child("data10").setValue(editText10.getText().toString());
            dialogInterface.cancel();
        });
        builder.setNegativeButton("Отмена", (dialogInterface, which) -> dialogInterface.cancel());
        AlertDialog alert = builder.create();
        alert.setOnShowListener(dialogInterface -> {
            Button btnPositive = alert.getButton(DialogInterface.BUTTON_POSITIVE);
            btnPositive.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            Button btnNegative = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
            btnNegative.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        });
        return alert;
    }
}
