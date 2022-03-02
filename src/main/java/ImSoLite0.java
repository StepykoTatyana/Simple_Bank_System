import org.sqlite.SQLiteDataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class ImSoLite0 {
    static LinkedHashMap<String, String> login = new LinkedHashMap<>();
    static String num;
    static String url;

    static String second() {
        System.out.println("Enter your card number: ");
        Scanner scanner = new Scanner(System.in);
        String numberAccount = scanner.next();
        scanner.nextLine();
        System.out.println("Enter your PIN:");
        String pin = scanner.next();
        try {
            if (login.get(numberAccount).equals(pin)) {
                System.out.println("You have successfully logged in!\n");
                System.out.println("1. Balance\n" +
                        "2. Add income\n" +
                        "3. Do transfer\n" +
                        "4. Close account\n" +
                        "5. Log out\n" +
                        "0. Exit");
                System.out.println();
                String num = scanner.next();
                scanner.nextLine();
                System.out.println(num);
                while (!Objects.equals(num, "0")) {
                    switch (num) {
                        case "1":
                            balance(numberAccount);
                            break;
                        case "2":
                            addIncome(numberAccount);
                            break;
                        case "3":
                            doTransfer(numberAccount);
                            break;
                        case "4":
                            closeAccount(numberAccount);
                            break;
                        case "5":
                            logout();
                            break;
                    }
                    if (num.equals("1") | num.equals("2") | num.equals("3")) {
                        System.out.println("1. Balance\n" +
                                "2. Add income\n" +
                                "3. Do transfer\n" +
                                "4. Close account\n" +
                                "5. Log out\n" +
                                "0. Exit");
                        num = scanner.next();
                        scanner.nextLine();
                    } else {
                        break;
                    }
                }
                return num;
            } else {
                System.out.println("Wrong card number or PIN!");
                return null;
            }
        } catch (Exception e) {
            System.out.println("Wrong card number or PIN!");
            return null;
        }
    }

    private static void closeAccount(String numAccount) {
        login.remove(numAccount);
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(url);
        try (Connection con = dataSource.getConnection()) {
            try (Statement statement = con.createStatement()) {
                statement.executeUpdate("DELETE FROM card WHERE number =" + numAccount);
            } catch (SQLException e) {
                e.printStackTrace();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("The account has been closed!");
    }

    private static void doTransfer(String numAccount) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Transfer");
        System.out.println("Enter card number:");
        String cardForTransfer = scanner.next();


        if (!checkCardForTransfer(cardForTransfer)) {
            System.out.println("Probably you made a mistake in the card number. Please try again!");
        } else if (!login.containsKey(cardForTransfer)) {
            System.out.println("Such a card does not exist.");
        } else if (cardForTransfer.equals(numAccount)) {
            System.out.println("You can't transfer money to the same account!");
        } else {
            System.out.println("Enter how much money you want to transfer:");
            int moneyForTransfer = Integer.parseInt(scanner.nextLine());
            if (returnBalance(numAccount) < moneyForTransfer) {
                System.out.println("Not enough money!");
            } else {
                SQLiteDataSource dataSource = new SQLiteDataSource();
                dataSource.setUrl(url);
                try (Connection con = dataSource.getConnection()) {
                    try (Statement statement = con.createStatement()) {
                        statement.executeUpdate("UPDATE card " +
                                "SET balance = balance -" + moneyForTransfer +
                                " WHERE number = " + numAccount);
                        statement.executeUpdate("UPDATE card " +
                                "SET balance = balance +" + moneyForTransfer +
                                " WHERE number = " + cardForTransfer);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                System.out.println("Success!");
            }
        }

    }

    private static Boolean checkCardForTransfer(String cardForTransfer) {
        ArrayList<String> arrays = new ArrayList<>(Arrays.asList(String.valueOf(cardForTransfer).split("")));
        int lastNum = Integer.parseInt(arrays.get(arrays.size() - 1));
        arrays.remove(arrays.size() - 1);
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
        return (mainSum + lastNum) % 10 == 0;
    }

    private static void addIncome(String numAccount) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter income:");
        int income = Integer.parseInt(scanner.nextLine());
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(url);
        try (Connection con = dataSource.getConnection()) {
            try (Statement statement = con.createStatement()) {
                statement.executeUpdate("UPDATE card " +
                        "SET balance = balance +" + income +
                        " WHERE number = " + numAccount);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Income was added!");
    }

    private static void logout() {
        System.out.println("You have successfully logged out!");
    }

    private static void balance(String numAccount) {
        System.out.printf("Balance: %d\n", returnBalance(numAccount));
    }

    private static int returnBalance(String numAccount) {
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(url);
        try (Connection con = dataSource.getConnection()) {
            try (Statement statement = con.createStatement()) {
                try (ResultSet balanceBd = statement.executeQuery("SELECT balance FROM card where number =" + numAccount)) {
                    return balanceBd.getInt("balance");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    static void first() {
        System.out.println("Your card has been created\n" +
                "Your card number:");
        StringBuilder numberAccount = createBankAccount();

        System.out.println("Your card PIN:");
        StringBuilder pin = createPin();
        login.put(String.valueOf(numberAccount), String.valueOf(pin));
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(url);
        try (Connection con = dataSource.getConnection()) {
            try (Statement statement = con.createStatement()) {
                statement.executeUpdate("INSERT INTO card(number, pin) VALUES (" + numberAccount +
                        ", " + '\'' + pin + '\'' + ")");
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

    static void printMainMenu(String urlDb) {
        url = urlDb;
        createBD();
        if (login.size() == 0){
            System.out.println("1. Create an account\n" +
                    "2. Log into account\n" +
                    "0. Exit");
            Scanner scanner = new Scanner(System.in);
            num = scanner.nextLine();
            while (!Objects.equals(num, "0")) {
                switch (num) {
                    case "1":
                        first();
                        break;
                    case "2":
                        num = second();
                        System.out.println(num);
                        break;
                }
                if (!Objects.equals(num, "0")) {
                    System.out.println(num);
                    System.out.println("1. Create an account\n" +
                            "2. Log into account\n" +
                            "0. Exit");
                    num = scanner.nextLine();
                } else {
                    System.out.println("Bye!");
                }
            }
        }

    }
}
