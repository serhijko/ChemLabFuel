package korneluk.serhij.chemlabfuel;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Comparator;

public class InventoryListSort implements Comparator<InventoryList> {

    private final SharedPreferences fuel;

    public InventoryListSort(Context context) {
        fuel = context.getSharedPreferences("fuel", Context.MODE_PRIVATE);
    }

    @Override
    public int compare(InventoryList o1, InventoryList o2) {
        int sort = fuel.getInt("sort", 0);
        if (sort == 1)
            return o1.data02.toLowerCase().compareTo(o2.data02.toLowerCase());
        if (sort == 2) {
            if (o1.data11 < o2.data11) {
                return -1;
            } else if (o1.data11 > o2.data11) {
                return 1;
            }
            return 0;
        }
        if (sort == 0) {
            String zero1 = "";
            if (o1.data01 < 10)
                zero1 = "0";
            String zero2 = "";
            if (o2.data01 < 10)
                zero2 = "0";
            return (zero1 + o1.data01 + ". " + o1.data02.toLowerCase()).compareTo(
                    zero2 + o2.data01 + ". " + o2.data02.toLowerCase());
        }
        return 0;
    }
}
