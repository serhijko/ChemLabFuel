package korneluk.serhij.chemlabfuel;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class Dialog_context_menu extends DialogFragment {

    static Dialog_context_menu getInstance(int position, String name) {
        Dialog_context_menu Instance = new Dialog_context_menu();
        Bundle args = new Bundle();
        args.putInt("position", position);
        args.putString("name", name);
        Instance.setArguments(args);
        return Instance;
    }

    interface Dialog_context_menu_Listener {
        void onDialogEditPosition(int position);

        void onDialogDeleteClick(int position);
    }

    private Dialog_context_menu_Listener menuListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            try {
                menuListener = (Dialog_context_menu_Listener) context;
            } catch (ClassCastException e) {
                throw new ClassCastException(context.toString() + " must implement Dialog_context_menu_Listener");
            }
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        TextView textViewTitle = new TextView(getActivity());
        textViewTitle.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        textViewTitle.setPadding(10, 10, 10, 10);
        textViewTitle.setText(getArguments().getString("name", ""));
        textViewTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        textViewTitle.setTypeface(null, Typeface.BOLD);
        textViewTitle.setTextColor(getResources().getColor(R.color.colorIcons));
        linearLayout.addView(textViewTitle);
        TextView textView = new TextView(getActivity());
        textView.setPadding(10, 20, 10, 20);
        textView.setText("Рэдагаваць");
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        textView.setTextColor(getResources().getColor(R.color.colorPrimary_text));
        textView.setOnClickListener(view -> {
            getDialog().cancel();
            menuListener.onDialogEditPosition(getArguments().getInt("position", 0));
        });
        linearLayout.addView(textView);
        TextView textView2 = new TextView(getActivity());
        textView2.setPadding(10, 20, 10, 20);
        textView2.setText("Выдаліць");
        textView2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        textView2.setTextColor(getResources().getColor(R.color.colorPrimary_text));
        textView2.setOnClickListener(view -> {
            getDialog().cancel();
            menuListener.onDialogDeleteClick(getArguments().getInt("position", 0));
        });
        linearLayout.addView(textView2);
        builder.setView(linearLayout);
        return builder.create();
    }
}
