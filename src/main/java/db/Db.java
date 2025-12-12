package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class Db {

    private static final String HOST = System.getenv().getOrDefault("PG_HOST", "localhost");
    private static final String PORT = System.getenv().getOrDefault("PG_PORT", "5432");
    private static final String DB = System.getenv().getOrDefault("PG_DB", "lampreaDB");
    private static final String USER = System.getenv().getOrDefault("PG_USER", "postgres");
    private static final String PASS = System.getenv().getOrDefault("PG_PASS", "ThePowerFP");

    private static final String URL = "jdbc:postgresql://" + HOST + ":" + PORT + "/" + DB;

    private Db() {}

    //Objeto que monta una conexión con bases de datos
    public static Connection getConnection() throws SQLException {
        Properties p = new Properties();
        p.setProperty("user", USER);
        p.setProperty("password", PASS);
        return DriverManager.getConnection(URL, p );
    }

}
