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
        //bundle.putString("journal", journal);
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
        LinearLayout linearLayout1 = new LinearLayout(getActivity());
        linearLayout1.setOrientation(LinearLayout.VERTICAL);
        linearLayout1.addView(textView);
        /*String journal = getArguments().getString("journal", "");
        if (!journal.equals("")) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<ArrayList<String>>>() {}.getType();
            ArrayList<ArrayList<String>> journal1 = gson.fromJson(journal, type);
            for (int i = 0; i < journal1.size(); i++) {
                String createBy = journal1.get(i).get(5);
                String fnG = "";
                String lnG = "";
                for (int i2 = 0; i2 < ChemLabFuel.users.size(); i2++) {
                    if (ChemLabFuel.users.get(i2).get(0).contains(createBy)) {
                        fnG = ChemLabFuel.users.get(i2).get(1);
                        lnG = ChemLabFuel.users.get(i2).get(2);
                        break;
                    }
                }
                TextView textView1 = new TextView(getActivity());
                textView1.setPadding(10, 10, 10, 10);
                textView1.setText(journal1.get(i).get(0) + " " + journal1.get(i).get(1) + " " +
                        journal1.get(i).get(2) + " " + journal1.get(i).get(3) + " " +
                        journal1.get(i).get(4) + " " + fnG + " " + lnG);
                textView1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                textView1.setTextColor(getResources().getColor(R.color.colorPrimary_text));
                textView1.setOnClickListener(v -> {
                    TextView textView2 = (TextView) v;
                    LinearLayout layout = new LinearLayout(getActivity());
                    layout.setBackgroundResource(R.color.colorPrimary);
                    TextView toast = new TextView(getActivity());
                    toast.setTextColor(getResources().getColor(R.color.colorIcons));
                    toast.setPadding(10, 10, 10, 10);
                    toast.setText(textView2.getText());
                    toast.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                    layout.addView(toast);
                    Toast mes = new Toast(getActivity());
                    mes.setDuration(Toast.LENGTH_LONG);
                    mes.setView(layout);
                    mes.show();
                });
                linearLayout1.addView(textView1);
            }
        }*/
        scrollView.addView(linearLayout1);
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
