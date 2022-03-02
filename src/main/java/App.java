import org.sqlite.SQLiteDataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class App {
    public static void main(String[] args) {
        String url = "jdbc:sqlite:C:/Apps/IdeaProjects/Simple_Bank_System/src/main/resources/westeros.db";
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(url);

        try (Connection con = dataSource.getConnection()) {
            if (con.isValid(5)) {
                System.out.println("Connection is valid.");
            }
            try (Statement statement = con.createStatement()) {
                // Statement execution
                statement.executeUpdate("CREATE TABLE IF NOT EXISTS HOUSES(" +
                        "id INTEGER PRIMARY KEY," +
                        "name TEXT NOT NULL," +
                        "words TEXT NOT NULL)");
                try {
                    statement.executeUpdate("INSERT INTO HOUSES VALUES " +
                            "(1, 'Targaryen of King''s Landing', 'Fire and Blood')," +
                            "(2, 'Stark of Winterfell', 'Summer is Coming')," +
                            "(3, 'Lannister of Casterly Rock', 'Hear Me Roar!')");
                } catch (Exception ignored) {

                }
                statement.executeUpdate("UPDATE HOUSES " +
                        "SET words = 'Winter is coming' " +
                        "WHERE id = 2");
                try (ResultSet greatHouses = statement.executeQuery("SELECT * FROM HOUSES")) {
                    while (greatHouses.next()) {
                        // Retrieve column values
                        int id = greatHouses.getInt("id");
                        String name = greatHouses.getString("name");
                        String words = greatHouses.getString("words");

                        System.out.printf("House %d%n", id);
                        System.out.printf("\tName: %s%n", name);
                        System.out.printf("\tWords: %s%n", words);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
