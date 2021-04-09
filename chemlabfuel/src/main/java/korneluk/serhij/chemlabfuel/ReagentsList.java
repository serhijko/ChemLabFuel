package korneluk.serhij.chemlabfuel;

import java.math.BigDecimal;

class ReagentsList {

    final long data;
    final int id;
    final String string;
    final BigDecimal residue;
    final BigDecimal minResidue;

    ReagentsList(long data, int id, String string, BigDecimal residue, BigDecimal minResidue) {
        this.data = data;
        this.id = id;
        this.string = string;
        this.residue = residue;
        this.minResidue = minResidue;
    }
}
