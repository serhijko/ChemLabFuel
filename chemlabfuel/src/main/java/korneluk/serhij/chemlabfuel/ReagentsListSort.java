package korneluk.serhij.chemlabfuel;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Comparator;

public class ReagentsListSort implements Comparator<ReagentsList> {

    private final SharedPreferences fuel;

    ReagentsListSort(Context context) {
        fuel = context.getSharedPreferences("fuel", Context.MODE_PRIVATE);
    }

    @Override
    public int compare(ReagentsList o1, ReagentsList o2) {
        int sort = fuel.getInt("sort", 0);
        if (sort == 1)
            return o1.string.toLowerCase().compareTo(o2.string.toLowerCase());
        if (sort == 2) {
            if (o1.data < o2.data) {
                return -1;
            } else if (o1.data > o2.data) {
                return 1;
            }
            return 0;
        }
        if (sort == 0) {
            String zero1 = "";
            if (o1.id < 10)
                zero1 = "0";
            String zero2 = "";
            if (o2.id < 10)
                zero2 = "0";
            return (zero1 + o1.id + ". " + o1.string.toLowerCase()).compareTo(
                    zero2 + o2.id + ". " + o2.string.toLowerCase());
        }
        return 0;
    }
}
