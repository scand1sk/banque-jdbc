package banque;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Client {
    private final int idClient;
    private String nom;

    private Client(int idClient, String nom) {
        this.idClient = idClient;
        this.nom = nom;
    }

    public static Client create(Connection con, String nom) throws SQLException {
        String sql = "INSERT INTO Client (nom) VALUES (?)";
        try (PreparedStatement pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, nom);
            pstmt.executeUpdate();
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt("idClient");
                    return new Client(id, nom);
                } else {
                    throw new IllegalStateException("Impossible d'obtenir l'idClient");
                }
            }
        }
    }

    public static Optional<Client> load(Connection con, int idClient) throws SQLException {
        String sql = "SELECT * FROM Client WHERE idClient = ?";
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, idClient);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(loadResultSet(rs));
                } else {
                    return Optional.empty();
                }
            }
        }
    }

    public static Client loadResultSet(ResultSet rs) throws SQLException {
        int idClient = rs.getInt("idClient");
        String nom = rs.getString("nom");
        return new Client(idClient, nom);
    }

    public List<Compte> getComptes(Connection con) throws SQLException {
        String sql = "SELECT * FROM Titulaire JOIN Compte USING (noCompte) WHERE idClient = ?";
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, idClient);
            try (ResultSet rs = pstmt.executeQuery()) {
                List<Compte> comptes = new ArrayList<>();
                while (rs.next()) {
                    comptes.add(Compte.loadResultSet(rs));
                }
                return comptes;
            }
        }
    }

    @Override
    public String toString() {
        return "Client{" +
                "idClient=" + idClient +
                ", nom='" + nom + '\'' +
                '}';
    }

    public int getIdClient() {
        return idClient;
    }

}
