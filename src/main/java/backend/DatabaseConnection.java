package backend;

import gameplay.OutputDisplayManager;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * classe che gestisce la connessione al database H2,
 * l'inizializzazione delle tabelle e l'esecuzione di query per ottenere dati.
 */
public class DatabaseConnection {

    static final String JDBC_DRIVER = "org.h2.Driver";
    static final String DB_URL = "jdbc:h2:./src/main/resources/database/il_tempio_dei_pianeti";
    static final String USER = "aaaa";
    static final String PASS = "1111";

    /**
     * connette al database e inizializza le tabelle se necessario.
     *
     * @return la connessione al database
     */
    public static Connection connect() {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String start = "RUNSCRIPT FROM 'src/main/resources/database/db_start.sql'";
        String fill = "RUNSCRIPT FROM 'src/main/resources/database/db_info.sql'";
        boolean emptyClassifica = true;
        boolean emptyDescr = true;
        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        try {
             Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             stmt = conn.prepareStatement(start);
             stmt.execute();
             stmt.close();

             String sql = "SELECT * FROM CLASSIFICA";
             stmt = conn.prepareStatement(sql);
             rs = stmt.executeQuery();
             while (rs.next()) {
                 emptyClassifica = false;
             }
             rs.close();
             String sql2 = "SELECT * FROM DESCRIZIONI";
             stmt = conn.prepareStatement(sql2);
             rs = stmt.executeQuery();
             while (rs.next()) {
                 emptyDescr = false;
             }
             rs.close();

             if (emptyClassifica && emptyDescr) {
                 stmt = conn.prepareStatement(fill);
                 stmt.execute();
                 stmt.close();
             }

             return conn;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * chiude la connessione al database.
     *
     * @param conn la connessione da chiudere
     */
    public static void close(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * stampa una descrizione dal database in base ai parametri forniti.
     *
     * @param idComando     l'id del comando
     * @param idStanza      l'id della stanza
     * @param idStato       l'id dello stato
     * @param idItem1       l'id del primo oggetto
     * @param idItem2       l'id del secondo oggetto
     */
    public static void printFromDB(String idComando, String idStanza, String idStato, String idItem1, String idItem2) {
        Connection conn;
        conn = DatabaseConnection.connect();
        String sql_query = "SELECT DESCRIZIONE FROM DESCRIZIONI WHERE COMANDO = '" + idComando + "' AND STANZA = '" + idStanza + "' AND STATO = '" + idStato + "' AND ITEM1 = '" + idItem1 + "' AND ITEM2 = '" + idItem2 + "'";
        OutputDisplayManager.displayText(DatabaseConnection.getDescriptionFromDB(conn, sql_query));
        DatabaseConnection.close(conn);
    }

    /**
     * restituisce la query SQL per ottenere la classifica ordinata per tempo.
     *
     * @return la stringa della query SQL
     */
    public static String queryClassifica() {
        return "SELECT * FROM CLASSIFICA ORDER BY FINALE DESC, TEMPO";
    }


    /**
     * restituisce la descrizione dal database.
     *
     * @param conn la connessione al database
     * @param sql_query la query SQL
     * @return la descrizione ottenuta dal database
     */
    public static String getDescriptionFromDB(Connection conn, String sql_query) {
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql_query);
            if (rs.next()) {
                return rs.getString("DESCRIZIONE");
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return "nessuna descrizione trovata :(";

    }


}