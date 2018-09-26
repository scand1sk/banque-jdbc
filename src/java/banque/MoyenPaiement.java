package banque;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

public class MoyenPaiement {
    private final int idMP;
    private int idClient;
    private int noCompte;
    private String type;
    private LocalDate expiration;

    public MoyenPaiement(int idMP, int idClient, int noCompte, String type, LocalDate expiration) {
        this.idMP = idMP;
        this.idClient = idClient;
        this.noCompte = noCompte;
        this.type = type;
        this.expiration = expiration;
    }

    public static MoyenPaiement create(Connection con, Client client, Compte compte, String type, LocalDate expiration) throws SQLException {
        String sql = "INSERT INTO MoyenPaiement (idClient, noCompte, type, expiration) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, client.getIdClient());
            pstmt.setInt(2, compte.getNoCompte());
            pstmt.setString(3, type);
            if (expiration == null) {
                pstmt.setNull(4, Types.DATE);
            } else {
                pstmt.setDate(4, Date.valueOf(expiration));
            }
            pstmt.executeUpdate();
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt("idMP");
                    return new MoyenPaiement(id, client.getIdClient(), compte.getNoCompte(), type, expiration);
                } else {
                    throw new IllegalStateException("Impossible de récupérer l'idMP");
                }
            }

        }
    }

    public static Optional<MoyenPaiement> load(Connection con, int idMP) throws SQLException {
        String sql = "SELECT * FROM MoyenPaiement WHERE idMP = ?";
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, idMP);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int idClient = rs.getInt("idClient");
                    int noCompte = rs.getInt("noCompte");
                    String type = rs.getString("type");
                    Date exp = rs.getDate("expiration");
                    LocalDate expiration = exp == null ? null : exp.toLocalDate();
                    return Optional.of(new MoyenPaiement(idMP, idClient, noCompte, type, expiration));
                } else {
                    return Optional.empty();
                }
            }
        }
    }

    public Operation debit(Connection con, BigDecimal montant, String libelle) throws SQLException {
        Operation o = Operation.create(con, montant.negate(), libelle, this);
        return o;
    }

    public int getIdMP() {
        return idMP;
    }
}
