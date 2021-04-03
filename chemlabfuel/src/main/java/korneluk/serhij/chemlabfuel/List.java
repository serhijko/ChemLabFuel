package korneluk.serhij.chemlabfuel;

import androidx.annotation.NonNull;

public class List implements Comparable {

    private final int date;
    private final String description;
    private final String dateDescription;

    List(int date, String description, String dateDescription) {
        this.date = date;
        this.description = description;
        this.dateDescription = dateDescription;
    }

    @Override
    public int compareTo(@NonNull Object o) {
        List tmp = (List) o;
        if (this.date < tmp.date) {
            return -1;
        } else if (this.date > tmp.date) {
            return 1;
        }
        return 0;
    }
}
