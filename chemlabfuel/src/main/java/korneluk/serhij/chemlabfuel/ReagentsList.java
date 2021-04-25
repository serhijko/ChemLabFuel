package korneluk.serhij.chemlabfuel;

import java.math.BigDecimal;
import java.util.ArrayList;

class ReagentsList {

    final long data;
    final int id;
    final String string;
    final BigDecimal residue;
    final BigDecimal minResidue;
    final int unit;
    final int check;
    final ArrayList<String> arrayList;

    ReagentsList(long data, int id, String string, BigDecimal residue, BigDecimal minResidue,
                 int unit, ArrayList<String> arrayList) {
        this.data = data;
        this.id = id;
        this.string = string;
        this.residue = residue;
        this.minResidue = minResidue;
        this.unit = unit;
        this.check = 1;
        this.arrayList = arrayList;
    }

    public ReagentsList(long data, int id, int check) {
        this.data = data;
        this.id = id;
        this.string = "";
        this.residue = null;
        this.minResidue = null;
        this.unit = 0;
        this.check = check;
        this.arrayList = null;
    }
}
