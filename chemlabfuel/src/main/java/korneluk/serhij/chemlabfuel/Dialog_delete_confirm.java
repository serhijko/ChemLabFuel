package korneluk.serhij.chemlabfuel;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
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

public class Dialog_delete_confirm extends DialogFragment {

    private Dialog_delete_confirm_listener listener;

    static Dialog_delete_confirm getInstance(String title, int position) {
        Dialog_delete_confirm description = new Dialog_delete_confirm();
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putInt("position", position);
        description.setArguments(bundle);
        return description;
    }

    interface Dialog_delete_confirm_listener {
        void delete_data(int position);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            try {
                listener = (Dialog_delete_confirm_listener) context;
            } catch (ClassCastException e) {
                throw new ClassCastException(context.toString() + " must implement Dialog_delete_confirm_listener");
            }
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        TextView textViewTitle = new TextView(getActivity());
        textViewTitle.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        textViewTitle.setPadding(10, 10, 10, 10);
        textViewTitle.setText(R.string.remove_question);
        textViewTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        textViewTitle.setTypeface(null, Typeface.BOLD);
        textViewTitle.setTextColor(getResources().getColor(R.color.colorIcons));
        linearLayout.addView(textViewTitle);
        TextView textView = new TextView(getActivity());
        textView.setPadding(10, 10, 10, 10);
        textView.setText(getString(R.string.remove_confirm, getArguments().getString("title")));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        textView.setTextColor(getResources().getColor(R.color.colorPrimary_text));
        linearLayout.addView(textView);
        ad.setView(linearLayout);
        ad.setPositiveButton(R.string.delete, (dialogInterface, i) -> listener.delete_data(getArguments().getInt("position")));
        ad.setNegativeButton(R.string.cancel, (dialogInterface, i) -> dialogInterface.cancel());
        AlertDialog alertDialog = ad.create();
        alertDialog.setOnShowListener(dialogInterface -> {
            Button btnPositive = alertDialog.getButton(Dialog.BUTTON_POSITIVE);
            btnPositive.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            Button btnNegative = alertDialog.getButton(Dialog.BUTTON_NEGATIVE);
            btnNegative.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        });
        return alertDialog;
    }
}
