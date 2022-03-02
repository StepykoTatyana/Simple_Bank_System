import org.sqlite.SQLiteDataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class App2 {
    public static void main(String[] args) {
        int indexArgument = java.util.Arrays.asList(args).indexOf("-fileName");
        String url = "jdbc:sqlite:" + args[indexArgument + 1];
        createBD(url);
        Scanner scanner = new Scanner(System.in);
        System.out.println("1. Create an account\n" +
                "2. Log into account\n" +
                "0. Exit");
        int num = scanner.nextInt();
        while (num != 0) {
            switch (num) {
                case 1:
                    AdvancedSystem.first(url);
                    break;
                case 2:
                    num = AdvancedSystem.second();
                    break;
            }
            if (num == 0) {
                break;
            }
            System.out.println("\n1. Create an account\n" +
                    "2. Log into account\n" +
                    "0. Exit");
            num = scanner.nextInt();
            if (num == 0) {
                System.out.println("Bye!");
                break;
            }
        }
    }

    private static void createBD(String urlBD) {
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(urlBD);
        try (Connection con = dataSource.getConnection()) {
            try (Statement statement = con.createStatement()) {
                statement.executeUpdate("CREATE TABLE IF NOT EXISTS card(" +
                        "id INTEGER PRIMARY KEY," +
                        "number TEXT NOT NULL," +
                        "pin TEXT NOT NULL," +
                        "balance INTEGER default 0," +
                        "UNIQUE(number))");
            } catch (SQLException e) {
                e.printStackTrace();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
