import org.sqlite.SQLiteDataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class AdvancedSystem {
    static LinkedHashMap<String, String> login = new LinkedHashMap<>();

    static int second() {
        System.out.println("\nEnter your card number: ");
        Scanner scanner = new Scanner(System.in);
        String numberAccount = scanner.next();
        System.out.println("Enter your PIN:");
        String pin = scanner.next();
        try {
            if (login.get(numberAccount).equals(pin)) {
                System.out.println("\nYou have successfully logged in!\n");
                System.out.println("1. Balance\n" +
                        "2. Log out\n" +
                        "0. Exit");
                int num = scanner.nextInt();
                while (num != 0) {
                    switch (num) {
                        case 1:
                            balance();
                            break;
                        case 2:
                            logout();
                            num = 0;
                            break;

                    }
                    if (num != 0) {
                        System.out.println("\n1. Balance\n" +
                                "2. Log out\n" +
                                "0. Exit");
                        num = scanner.nextInt();
                        if (num == 0) {
                            break;
                        }
                    }
                }
                return num;
            } else {
                System.out.println("\nWrong card number or PIN!");
                return 100;
            }
        } catch (Exception e) {
            System.out.println("\nWrong card number or PIN!");
            return 100;
        }
    }

    private static void logout() {
        System.out.println("\nYou have successfully logged out!");
    }

    private static void balance() {
        System.out.println("\nBalance: 0");
    }

    static void first(String urlBD) {
        System.out.println("\nYour card has been created\n" +
                "Your card number:");
        StringBuilder numberAccount = createBankAccount();

        System.out.println("Your card PIN:");
        StringBuilder pin = createPin();
        System.out.println(pin);
        login.put(String.valueOf(numberAccount), String.valueOf(pin));


        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(urlBD);
        try (Connection con = dataSource.getConnection()) {
            try (Statement statement = con.createStatement()) {
                statement.executeUpdate("INSERT INTO card(number, pin) VALUES (" + numberAccount +
                        ", " + '\'' + pin + '\'' + ")");
                try (ResultSet bank = statement.executeQuery("Select*from card")){
                    while (bank.next()) {
                        // Retrieve column values
                        int id = bank.getInt("id");
                        String pin1 = bank.getString("pin");


                        System.out.printf("House %d%n", id);
                        System.out.printf("\tName: %s%n", pin1);

                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static StringBuilder createBankAccount() {
        Random random = new Random();
        int inn = 400000;
        StringBuilder numberAccount = new StringBuilder();
        numberAccount.append(inn);

        int binBank = random.nextInt(9);
        for (int i = 0; i < 9; i++) {
            numberAccount.append(binBank);
            binBank = random.nextInt(9);
        }
        ArrayList<String> arrays = new ArrayList<>(Arrays.asList(String.valueOf(numberAccount).split("")));
        int mainSum = 0;
        int newNum;
        for (int i = 0; i < arrays.size(); i++) {
            if (i % 2 == 0) {
                newNum = Integer.parseInt(arrays.get(i)) * 2;
                newNum = (newNum > 9) ? (newNum - 9) : newNum;
                arrays.set(i, String.valueOf(newNum));
            } else {
                newNum = Integer.parseInt(arrays.get(i));
            }
            mainSum += newNum;
        }
        for (int i = 0; i < 10; i++) {
            if ((mainSum + i) % 10 == 0) {
                arrays.add(String.valueOf(i));
                break;
            }
        }
        numberAccount.append(arrays.get(arrays.size() - 1));
        System.out.println(numberAccount);
        return numberAccount;
    }

    private static StringBuilder createPin() {
        Random random = new Random();
        StringBuilder pin = new StringBuilder();
        int c = random.nextInt(9);
        for (int i = 1; i <= 4; i++) {
            pin.append(c);
            c = random.nextInt(9);
        }
        System.out.println(pin);
        return pin;
    }
}
