package banque;

import org.postgresql.ds.PGSimpleDataSource;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;

public class Main {
    public static void main(String[] args) throws SQLException {
        PGSimpleDataSource ds = new PGSimpleDataSource();
        ds.setServerName("localhost");
        ds.setUser("vion");
        ds.setPassword("vion");
        ds.setDatabaseName("banque");

        try (Connection con = ds.getConnection()) {

            MoyenPaiement mp = MoyenPaiement.load(con, 1).get();
            Operation o = mp.debit(con, BigDecimal.valueOf(200), "Loyer");
            System.out.println(o);

        }
    }
}
