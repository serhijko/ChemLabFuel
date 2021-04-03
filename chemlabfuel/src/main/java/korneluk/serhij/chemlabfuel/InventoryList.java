package korneluk.serhij.chemlabfuel;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

public class InventoryList implements Comparable<InventoryList> {

    final String createdBy;
    final long data01;
    final String data02;
    final String data03;
    final String data04;
    final String data05;
    final String data06;
    final String data07;
    final String data08;
    final String data09;
    final String data10;
    private final long data11;
    final String data12;
    final String uid;
    final long editedAt;
    final String editedBy;
    private SharedPreferences fuel;

    InventoryList(Context context, String createdBy, long data01, String data02, String data03, String data04, String data05, String data06, String data07, String data08, String data09, String data10, long data11, String data12, String uid, long editedAt, String editedBy) {
        this.createdBy = createdBy;
        this.data01 = data01;
        this.data02 = data02;
        this.data03 = data03;
        this.data04 = data04;
        this.data05 = data05;
        this.data06 = data06;
        this.data07 = data07;
        this.data08 = data08;
        this.data09 = data09;
        this.data10 = data10;
        this.data11 = data11;
        this.data12 = data12;
        this.uid = uid;
        this.editedAt = editedAt;
        this.editedBy = editedBy;
        fuel = context.getSharedPreferences("fuel", Context.MODE_PRIVATE);
    }

    @Override
    public int compareTo(@NonNull InventoryList o) {
        int sort = fuel.getInt("sort", 0);
        if (sort == 1)
            return data02.toLowerCase().compareTo(o.data02.toLowerCase());
        if (sort == 2) {
            if (this.data11 < o.data11) {
                return -1;
            } else if (this.data11 > o.data11) {
                return 1;
            }
            return 0;
        }
        if (sort == 0) {
            String zero = "";
            if (data01 < 10)
                zero = "0";
            String zero0 = "";
            if (o.data01 < 10)
                zero0 = "0";
            return (zero + data01 + ". " + data02.toLowerCase()).compareTo(zero0 + o.data01 + ". " +
                    o.data02.toLowerCase());
        }
        return 0;
    }
}
