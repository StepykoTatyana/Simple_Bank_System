import org.sqlite.SQLiteDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class App3 {
    static String url;
    public static void main(String[] args) {
        int indexArgument = java.util.Arrays.asList(args).indexOf("-fileName");
        url = "jdbc:sqlite:" + args[indexArgument + 1];
        createBD();
        Scanner scanner = new Scanner(System.in);
        System.out.println("1. Create an account\n" +
                "2. Log into account\n" +
                "0. Exit");
        int num = scanner.nextInt();
        while (num != 0) {
            switch (num) {
                case 1:
                    ImSoLite.first();
                    break;
                case 2:
                    num = ImSoLite.second();
                    break;
            }
            if (num == 0) {
                break;
            }
            System.out.println("\n1. Create an account\n" +
                    "2. Log into account\n" +
                    "0. Exit");
            num = scanner.nextInt();
        }
        System.out.println("Bye!");
    }

    private static void createBD() {
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(url);
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
