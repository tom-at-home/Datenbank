import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

class SQLiteDB {
    
    private static final SQLiteDB dbcontroller = new SQLiteDB();
	
    private static Connection connection;
	
    private static final String DB_PATH = "testdb.db";
	
    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.out.println("Fehler beim Laden des JDBC-Treibers:\n" + e.getMessage());
        }
    }   
 
	private SQLiteDB(){
    }
    
    public static SQLiteDB getInstance(){
        return dbcontroller;
    }
 
    private void initDBConnection() {
        try {
            if (connection != null)
                return;
            System.out.println("Verbinde mit der Datenbank");
            connection = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);
            if (!connection.isClosed())
                System.out.println("...Verbindung wurde hergestellt.");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try {
                    if (!connection.isClosed() && connection != null) {
                        connection.close();
                        if (connection.isClosed())
                            System.out.println("Verbindung zur Datenbank wurde geschlossen");
                    }
                } catch (SQLException e) {
					System.out.println("Eine SQLException ist aufgetreten:\n" + e.getMessage());
                }
            }
        });
    }
	
    private void handleDB() {
        try {
            Statement stmt = connection.createStatement();
			
            stmt.execute("DROP TABLE IF EXISTS benutzer;");
            stmt.execute("CREATE TABLE benutzer (id INTEGER PRIMARY KEY, username CHAR(50), password CHAR(50),  logindate DATETIME );");
			
            stmt.execute("INSERT INTO benutzer (username, password, logindate  ) VALUES ('Hans', 'geheim', date('now'))");
            stmt.execute("INSERT INTO benutzer (username, password, logindate  ) VALUES ('Frida', 'noch geheimer', date('now'))");
            stmt.execute("INSERT INTO benutzer (username, password, logindate  ) VALUES ('Root', 'passwort', date('now'))");
            
            //ResultSet rs = stmt.executeQuery("SELECT * FROM benutzer;");
			ResultSet rs = stmt.executeQuery("SELECT * FROM benutzer WHERE username LIKE 'Ha%';");
            while (rs.next()) {
				System.out.println("ID:"+ rs.getInt("id") + "\t");
                System.out.println("Username = " + rs.getString("username"));
                System.out.println("Password = " + rs.getString("password"));
                System.out.println("Letzter Login = "
                        + rs.getString("logindate"));

				System.out.println("\n\n\n");
            }
            rs.close();
            connection.close();
			
        } catch (SQLException e) {
            System.out.println("Fehler in der Datenbank-Abfrage:\n" + e.getMessage());
            
        }
    }
	
    public static void main(String[] args) {
        SQLiteDB dbc = SQLiteDB.getInstance();
        dbc.initDBConnection();
        dbc.handleDB();
    }
    
}