package korneluk.serhij.chemlabfuel;

import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class Dialog_no_internet extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        TextView textViewTitle = new TextView(getActivity());
        textViewTitle.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        textViewTitle.setPadding(10, 10, 10, 10);
        textViewTitle.setText("Нет интернет соединения");
        textViewTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        textViewTitle.setTypeface(null, Typeface.BOLD);
        textViewTitle.setTextColor(getResources().getColor(R.color.colorIcons));
        linearLayout.addView(textViewTitle);
        TextView textView = new TextView(getActivity());
        textView.setPadding(10, 10, 10, 10);
        textView.setText("Проверьте настройки соединения");
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        textView.setTextColor(getResources().getColor(R.color.colorPrimary_text));
        linearLayout.addView(textView);
        ad.setView(linearLayout);
        ad.setPositiveButton("Хорошо", (dialogInterface, i) -> dialogInterface.cancel());
        AlertDialog alert = ad.create();
        alert.setOnShowListener(dialogInterface -> {
            Button btnPositive = alert.getButton(Dialog.BUTTON_POSITIVE);
            btnPositive.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        });
        return alert;
    }
}
