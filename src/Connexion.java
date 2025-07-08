import java.sql.*;
import java.util.Map;

public class Connexion {
    private static Connection con;
    private static final String driver = "com.mysql.cj.jdbc.Driver";
    private static String url;
    private static String user;
    private static String pass;

    static {
        try {
            // Charger les variables d’environnement
            Map<String, String> env = EnvLoader.loadEnv(".env");
            url = "jdbc:mysql://" + env.get("DB_HOST") + ":" + env.get("DB_PORT") + "/" + env.get("DB_NAME");
            user = env.get("DB_USER");
            pass = env.get("DB_PASS");

            // Charger le driver MySQL
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            System.err.println("Driver MySQL introuvable : " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des variables .env : " + e.getMessage());
        }
    }

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(url, user, pass);
        } catch (SQLException e) {
            System.err.println("Erreur de connexion à la base de données : " + e.getMessage());
            return null;
        }
    }
}

// This code establishes a connection to a MySQL database using JDBC.