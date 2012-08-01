package main.java.hultberg.sur.Hultberg.utils;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;

import main.java.hultberg.sur.Hultberg.Hultberg;
import main.java.hultberg.sur.Hultberg.sql.sqlConnector;

public class MySQLHandler {
	private Connection sqlConnection = null;
    private sqlConnector sqlConnector;

    public MySQLHandler(Hultberg instance) {
        this.sqlConnector = instance.getSqlConnector();
    }

    public boolean initialize() {
        if (this.sqlConnection == null) {
            this.sqlConnection = this.sqlConnector.getConnection();
            if (this.sqlConnection == null) {
                Hultberg.log.log(Level.SEVERE, "[Hultberg] Feil ved tilkobling til databasen.");
            }
        }

        return true;
    }

    public Connection getConnection() {
        if (this.sqlConnection == null) {
            Hultberg.log.log(Level.INFO,
                    "[Hultberg] Tilkoblingen returnerte null, oppretter ny kobling.");
            initialize();
        }
        return this.sqlConnection;
    }

    public void closeConnection() {
        try {
            this.sqlConnection.close();
        } catch (SQLException e) {
            Hultberg.log.log(Level.SEVERE,
                    "[Hultberg] Kunne ikke lukke tilkoblingen til databasen.", e);
            e.printStackTrace();
        }
    }

    public void checkWarnings() {
        try {
            SQLWarning warning = this.sqlConnection.getWarnings();
            while (warning != null) {
                Hultberg.log
                        .log(Level.WARNING,
                                "[Hultberg] SQL-Advarsel: ", warning);
                warning = warning.getNextWarning();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Hultberg.log.log(Level.SEVERE, "[Hultberg] Fikk ikke hentet warnings fra databasetilkobling! ", e);
        }
    }


    public boolean update(String query, Object[] array) {
        Connection conn = null;
        PreparedStatement ps = null;
        int counter = 1;
        long time = System.currentTimeMillis();
        if (array != null) {
            try {
                // conn = sqlConnector.getConnection();
                conn = this.sqlConnection;
                ps = conn.prepareStatement(query);

                for (Object o : array) {
                    if (o instanceof Integer) {
                        ps.setInt(counter, (Integer) o);
                    } else if (o instanceof String) {
                        ps.setString(counter, (String) o);
                    } else {
                        Hultberg.log.log(Level.SEVERE,
                                "[Hultberg] Ukjent objekt i mysql-handler.(" + o.getClass().toString() + ")");
                        Hultberg.log.log(Level.SEVERE, Arrays.toString(array));
                        return false;
                    }
                    counter++; // �ker indeks
                }
                array = null;
                ps.setEscapeProcessing(true);
                ps.executeUpdate();
                ps.close();
                // conn.close();

                long newTime = System.currentTimeMillis();
                if (newTime - time > 30) {
                    Hultberg.log.log(Level.INFO,
                            "[Hultberg] En spørring tok veldig lang tid( "
                                    + (newTime - time)
                                    + " ms). Spørringen var: " + query);
                }
                checkWarnings();
                ;
                return true;
            } catch (SQLException ex) {
                Hultberg.log.log(Level.SEVERE, "[Hultberg] SQL Exception", ex);
                return false;
            }
        } else { // Arrayet er tomt, ergo har man brukt funksjonen feil
            Hultberg.log.log(Level.SEVERE,
                    "[Hultberg] Empty array in mysqlhandler.update");
            return false;
        }
    }

    public boolean update(String query) {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = this.sqlConnection;
            stmt = conn.createStatement();
            stmt.executeUpdate(query);
            stmt.close();
            checkWarnings();
            return true;
        } catch (SQLException ex) {
            Hultberg.log.log(Level.SEVERE, "[Hultberg] SQL Exception", ex);
            return false;
        }
    }

    public int insert(String query) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int id = -1;
        try {
            conn = this.sqlConnection;
            ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setEscapeProcessing(true);
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                id = rs.getInt(1);
            }
            rs.close();
            ps.close();
            checkWarnings();
            return id;
        } catch (SQLException ex) {
            Hultberg.log.log(Level.SEVERE, "[Hultberg] SQL Exception", ex);
            return 0;
        }
    }

    public String getColumn(String query, String c) {
        Connection conn = null;
        Statement ps = null;
        ResultSet rs = null;
        String column = "";
        try {
            conn = this.sqlConnection;
            ps = conn.createStatement();
            rs = ps.executeQuery(query);

            if (rs.next()) {
                column = rs.getString(c);
            }

            rs.close();
            ps.close();
            checkWarnings();
            return column;
        } catch (SQLException ex) {
            Hultberg.log.log(Level.SEVERE, "[Hultberg] SQL Exception", ex);
            return null;
        }
    }

    public String getColumn(String query, String c, Object[] array) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String column = "";
        int counter = 1;
        if (array != null) {
            try {
                conn = this.sqlConnection;
                ps = conn.prepareStatement(query);

                // Her kjører man inn variablene
                for (Object o : array) {
                    if (o instanceof Integer) {
                        ps.setInt(counter, (Integer) o); // indeks og object som
                        // blir castet
                    } else if (o instanceof String) {
                        ps.setString(counter, (String) o);
                    } else {
                        Hultberg.log
                                .log(Level.SEVERE,
                                        "[Hultberg] Nullobjekt i mysql-handler. (getColumn)");
                        return null;
                    }
                    counter++;
                }
                array = null;
                rs = ps.executeQuery();

                if (rs.next()) {
                    column = rs.getString(c);
                }
                rs.close();
                ps.close();
                checkWarnings();
            } catch (SQLException ex) {
                Hultberg.log.log(Level.SEVERE, "[Hultberg] SQL Exception", ex);
                return null;
            }
        }
        return column;
    }

    public ArrayList<ArrayList<String>> getRows(String query, Object[] array) {
        ArrayList<ArrayList<String>> rows = new ArrayList<ArrayList<String>>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int counter = 1;
        int rowCounter = 0;

        if (array != null) {
            try {
                // conn = sqlConnector.getConnection();
                conn = this.sqlConnection;
                ps = conn.prepareStatement(query);

                // Her kjører man inn variablene
                for (Object o : array) {
                    if (o instanceof Integer) {
                        ps.setInt(counter, (Integer) o); // indeks og object som
                        // blir castet
                    } else if (o instanceof String) {
                        ps.setString(counter, (String) o);
                    } else {
                        Hultberg.log
                                .log(Level.SEVERE,
                                        "[Hultberg] Nullobjekt i mysql-handler. (getRows)");
                        return null;
                    }
                    counter++;
                }

                rs = ps.executeQuery();

                if (rs.next()) {
                    rows.add(new ArrayList<String>());
                    for (int i = 1; i <= array.length; i++) {
                        rows.get(rowCounter).add(rs.getString(i));
                    }
                    rowCounter++;
                }

                array = null;
                rs.close();
                ps.close();
                checkWarnings();
                // conn.close();
            } catch (SQLException ex) {
                Hultberg.log.log(Level.SEVERE, "[Hultberg] SQL Exception", ex);
                return null;
            }
        } else {
            Hultberg.log.log(Level.SEVERE, Thread.currentThread()
                    .getStackTrace()[0].getMethodName() + " - array is null");
            return null;
        }
        return rows;
    }

    /**
* Checks if the string contains any non-alphabet characters or _
*
* @param string The string to check.
* @return true if it doesn't any non-alphabet characters or _, false if it does.
*/
    public static boolean checkString(String string) {

        for (int i = 0; i < string.length(); i++) {
            if (!Character.isLetterOrDigit(string.codePointAt(i))) {
                if (string.charAt(i) != '_') {
                    return false;
                }
            }
        }

        return true;
    }
}
