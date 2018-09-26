package banque;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Compte {
    private final int noCompte;
    private BigDecimal solde;
    private BigDecimal decouvertAutorise;
    private String intitule;

    private Compte(int noCompte, BigDecimal solde, BigDecimal decouvertAutorise, String intitule) {
        this.noCompte = noCompte;
        this.solde = solde;
        this.decouvertAutorise = decouvertAutorise;
        this.intitule = intitule;
    }

    public static Compte create(Connection con, String intitule, BigDecimal solde, BigDecimal decouvertAutorise) throws SQLException {
        String sql = "INSERT INTO Compte (intitule, solde, decouvertAutorise) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, intitule);
            pstmt.setBigDecimal(2, solde);
            pstmt.setBigDecimal(3, decouvertAutorise);
            pstmt.executeUpdate();
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt("noCompte");
                    return new Compte(id, solde, decouvertAutorise, intitule);
                } else {
                    throw new IllegalStateException("Impossible d'obtenir le no de compte");
                }
            }
        }
    }

    public static Compte loadResultSet(ResultSet rs) throws SQLException {
        int noCompte = rs.getInt("noCompte");
        String intitule = rs.getString("intitule");
        BigDecimal solde = rs.getBigDecimal("solde");
        BigDecimal decouvertAutorise = rs.getBigDecimal("decouvertAutorise");
        return new Compte(noCompte, solde, decouvertAutorise, intitule);
    }

    public void addTitulaire(Connection con, Client client) throws SQLException {
        String sql = "INSERT INTO Titulaire (idClient, noCompte) VALUES (?, ?)";
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, client.getIdClient());
            pstmt.setInt(2, noCompte);
            pstmt.executeUpdate();
        }
    }

    public List<Client> getTitulaires(Connection con) throws SQLException {
        String sql = "SELECT * FROM Client JOIN Titulaire USING (idClient) WHERE noCompte = ?";
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, noCompte);
            try (ResultSet rs = pstmt.executeQuery()) {
                List<Client> titulaires = new ArrayList<>();
                while (rs.next()) {
                    titulaires.add(Client.loadResultSet(rs));
                }
                return titulaires;
            }
        }
    }

    public static Optional<Compte> load(Connection con, int noCompte) throws SQLException {
        String sql = "SELECT * FROM Compte WHERE noCompte = ?";
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, noCompte);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(loadResultSet(rs));
                } else {
                    return Optional.empty();
                }
            }
        }
    }

    @Override
    public String toString() {
        return "Compte{" +
                "noCompte=" + noCompte +
                ", solde=" + solde +
                ", decouvertAutorise=" + decouvertAutorise +
                ", intitule='" + intitule + '\'' +
                '}';
    }

    public int getNoCompte() {
        return noCompte;
    }
}
