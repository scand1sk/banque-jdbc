package banque;

import javax.naming.ldap.PagedResultsControl;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;

public class Operation {
    private final int idOperation;
    private LocalDateTime dateHeure;
    private BigDecimal montant;
    private String libelle;
    private int idMP;

    public Operation(int idOperation, LocalDateTime dateHeure, BigDecimal montant, String libelle, int idMP) {
        this.idOperation = idOperation;
        this.dateHeure = dateHeure;
        this.montant = montant;
        this.libelle = libelle;
        this.idMP = idMP;
    }

    public static Operation create(Connection con, BigDecimal montant, String libelle, MoyenPaiement mp) throws SQLException {
        String sql = "INSERT INTO Operation (montant, libelle, idMP) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setBigDecimal(1, montant);
            pstmt.setString(2, libelle);
            pstmt.setInt(3, mp.getIdMP());
            pstmt.executeUpdate();
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt("idOperation");
                    LocalDateTime dh = rs.getTimestamp("dateHeure").toLocalDateTime();
                    return new Operation(id, dh, montant, libelle, mp.getIdMP());
                } else {
                    throw new IllegalStateException("Impossible de récupérer l'opération");
                }
            }
        }
    }

    @Override
    public String toString() {
        return "Operation{" +
                "idOperation=" + idOperation +
                ", dateHeure=" + dateHeure +
                ", montant=" + montant +
                ", libelle='" + libelle + '\'' +
                ", idMP=" + idMP +
                '}';
    }
}
