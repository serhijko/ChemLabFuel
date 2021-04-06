package korneluk.serhij.chemlabfuel;

import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.util.TypedValue;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class Dialog_description extends DialogFragment {

    static Dialog_description getInstance(String title, String string) {
        Dialog_description description = new Dialog_description();
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putString("string", string);
        description.setArguments(bundle);
        return description;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        ScrollView scrollView = new ScrollView(getActivity());
        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        TextView textViewTitle = new TextView(getActivity());
        textViewTitle.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        textViewTitle.setPadding(10, 10, 10, 10);
        textViewTitle.setText(getArguments().getString("title"));
        textViewTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        textViewTitle.setTypeface(null, Typeface.BOLD);
        textViewTitle.setTextColor(getResources().getColor(R.color.colorIcons));
        linearLayout.addView(textViewTitle);
        TextView textView = new TextView(getActivity());
        textView.setPadding(10, 10, 10, 10);
        textView.setText(Html.fromHtml(getArguments().getString("string")));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        textView.setTextColor(getResources().getColor(R.color.colorPrimary_text));
        linearLayout.addView(scrollView);
        scrollView.addView(textView);
        builder.setPositiveButton(getString(R.string.good), (dialog, which) -> dialog.cancel());
        builder.setView(linearLayout);
        AlertDialog alert = builder.create();
        alert.setOnShowListener(dialogInterface -> {
            Button btnPositive = alert.getButton(Dialog.BUTTON_POSITIVE);
            btnPositive.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        });
        return alert;
    }
}
