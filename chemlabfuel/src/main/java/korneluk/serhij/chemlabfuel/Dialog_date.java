package korneluk.serhij.chemlabfuel;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * A simple {@link DialogFragment} subclass.
 * Use the {@link Dialog_date#getInstance} factory method to
 * create an instance of this fragment.
 */
public class Dialog_date extends DialogFragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_DATE = "date";
    private static final String ARG_VIEW = "textView";
    private static final String ARG_TITLE = "title";

    private Dialog_date_listener listener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param date Parameter 1.
     * @param textView Parameter 2.
     * @param title Parameter 3.
     * @return A new instance of fragment Dialog_date.
     */
    static Dialog_date getInstance(long date, int textView, String title) {
        Dialog_date descriptionFragment = new Dialog_date();
        Bundle args = new Bundle();
        args.putLong(ARG_DATE, date);
        args.putInt(ARG_VIEW, textView);
        args.putString(ARG_TITLE, title);
        descriptionFragment.setArguments(args);
        return descriptionFragment;
    }

    interface Dialog_date_listener {
        void set_date(int textView, int year, int month, int dayOfMonth);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            try {
                listener = (Dialog_date_listener) context;
            } catch (ClassCastException e) {
                throw new ClassCastException(context.toString() + " must implement Dialog_date_listener");
            }
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_date, null);
        TextView today = view.findViewById(R.id.today);
        TextView textView1 = view.findViewById(R.id.textView1C);
        TextView textView2 = view.findViewById(R.id.textView2);
        TextView title = view.findViewById(R.id.title);
        title.setText(getArguments().getString(ARG_TITLE));
        CalendarView calendarView = view.findViewById(R.id.calendarView);
        calendarView.setDate(getArguments().getLong(ARG_DATE));
        today.setOnClickListener(v -> {
            GregorianCalendar c = (GregorianCalendar) Calendar.getInstance();
            calendarView.setDate(c.getTimeInMillis());
        });
        textView1.setOnClickListener(v -> {
            GregorianCalendar c = new GregorianCalendar();
            c.setTimeInMillis(calendarView.getDate());
            c.add(Calendar.YEAR, -1);
            calendarView.setDate(c.getTimeInMillis());
        });
        textView2.setOnClickListener(v -> {
            GregorianCalendar c = new GregorianCalendar();
            c.setTimeInMillis(calendarView.getDate());
            c.add(Calendar.YEAR, 1);
            calendarView.setDate(c.getTimeInMillis());
        });
        int textView = getArguments().getInt(ARG_VIEW);
        calendarView.setOnDateChangeListener((v, year, month, dayOfMonth) -> {
            listener.set_date(textView, year, month, dayOfMonth);
            getDialog().cancel();
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);

        if (textView == 7 || textView == 9 || textView == 10) {
            builder.setNeutralButton("Удалить дату", (dialogInterface, i) -> {
                listener.set_date(getArguments().getInt(ARG_VIEW), 0, 0, 0);
            });
            builder.setPositiveButton("Отмена", (dialogInterface, i) -> dialogInterface.cancel());
        }
        if (textView == 3 || textView == 1) {
            builder.setPositiveButton("Отмена", (dialogInterface, i) -> dialogInterface.cancel());
        }
        if (textView == 8) {
            builder.setNegativeButton("Отмена", (dialogInterface, i) -> dialogInterface.cancel());
            builder.setPositiveButton("Установить месяц", (dialogInterface, i) -> {
                GregorianCalendar c = new GregorianCalendar();
                c.setTimeInMillis(calendarView.getDate());
                listener.set_date(textView, c.get(Calendar.YEAR), c.get(Calendar.MONTH), -1);
            });
        }
        AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(dialogInterface -> {
            Button btnPositive = alertDialog.getButton(Dialog.BUTTON_POSITIVE);
            btnPositive.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            Button btnNegative = alertDialog.getButton(Dialog.BUTTON_NEGATIVE);
            btnNegative.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            Button btnNeutral = alertDialog.getButton(Dialog.BUTTON_NEUTRAL);
            btnNeutral.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        });
        return alertDialog;
    }
}