import org.sqlite.SQLiteDataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class App4 {
    static SQLiteDataSource dataSource = new SQLiteDataSource();
    static Connection con;
    static LinkedHashMap<String, String> login = new LinkedHashMap<>();

    static {
        try {
            con = dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws SQLException {
        dataSource.setUrl("jdbc:sqlite: card.s3db");
        createBD();
        printMainMenu();
    }

    private static void printMainMenu() {

        printMenu();
        Scanner scanner = new Scanner(System.in);
        String num = scanner.nextLine();
        System.out.println();
        while (!Objects.equals(num, "0")) {
            if (num.equals("1")) {
                first();
            } else if (num.equals("2")){
                if (Objects.equals(second(), "0")){
                    break;
                }
            } else if (num.equals("0")){
                break;
            }
            System.out.println();
            printMenu();
            num = scanner.nextLine();
        }
        System.out.println();
    }

    private static void printMenu() {
        System.out.println("1. Create an account\n" +
                "2. Log into account\n" +
                "0. Exit");
    }

    private static void createBD() throws SQLException {
        try (Statement statement = con.createStatement()) {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS card(" +
                    "id INTEGER PRIMARY KEY," +
                    "number TEXT NOT NULL," +
                    "pin TEXT NOT NULL," +
                    "balance INTEGER default 0," +
                    "UNIQUE(number))");
        }
    }

    static void first() {
        System.out.println("Your card has been created\n" +
                "Your card number:");
        StringBuilder numberAccount = createBankAccount();

        System.out.println("Your card PIN:");
        StringBuilder pin = createPin();
        login.put(String.valueOf(numberAccount), String.valueOf(pin));
        try (Statement statement = con.createStatement()) {
            statement.executeUpdate("INSERT INTO card(number, pin) VALUES (" + numberAccount +
                    ", " + '\'' + pin + '\'' + ")");
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


    static String second() {
        System.out.println("Enter your card number: ");
        Scanner scanner = new Scanner(System.in);
        String numberAccount = scanner.nextLine();
        System.out.println("Enter your PIN:");
        String pin = scanner.nextLine();
        try {
            if (login.get(numberAccount).equals(pin)) {
                System.out.println("You have successfully logged in!\n");
                return checkBalance(numberAccount);
            } else {
                System.out.println();
                System.out.println("Wrong card number or PIN!");
                return null;
            }
        } catch (Exception e) {
            System.out.println();
            System.out.println("Wrong card number or PIN!");
            return null;
        }
    }

    private static String checkBalance(String numberAccount) throws SQLException {
        System.out.println();
        System.out.println("1. Balance\n" +
                "2. Add income\n" +
                "3. Do transfer\n" +
                "4. Close account\n" +
                "5. Log out\n" +
                "0. Exit");
        Scanner scanner = new Scanner(System.in);
        String num = scanner.nextLine();
        while (!Objects.equals(num, "0")) {
            if (num.equals("1")){
                System.out.println();
                System.out.printf("Balance: %d", returnBalance(numberAccount));
            } else if (num.equals("2")) {
                System.out.println();
                addIncome(numberAccount);
            } else if (num.equals("3")){
                System.out.println();
                doTransfer(numberAccount);
            } else if (num.equals("4")){
                System.out.println();
                closeAccount(numberAccount);
                break;
            } else if (num.equals("5")) {
                System.out.println();
                System.out.println("You have successfully logged out!");
                break;
            }
            System.out.println();
            System.out.println("1. Balance\n" +
                    "2. Add income\n" +
                    "3. Do transfer\n" +
                    "4. Close account\n" +
                    "5. Log out\n" +
                    "0. Exit");
            num = scanner.nextLine();
        }
        return num;
    }

    private static int returnBalance(String numAccount) throws SQLException {
        try (Statement statement = con.createStatement()) {
            try (ResultSet balanceBd = statement.executeQuery("SELECT balance FROM card where number =" + numAccount)) {
                return balanceBd.getInt("balance");
            }
        }
    }

    private static void addIncome(String numAccount) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter income:");
        int income = scanner.nextInt();
        try (Statement statement = con.createStatement()) {
            statement.executeUpdate("UPDATE card " +
                    "SET balance = balance +" + income +
                    " WHERE number = " + numAccount);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println();
        System.out.println("Income was added!");
    }

    private static void doTransfer(String numAccount) throws SQLException {
        System.out.println("Transfer\nEnter card number:");
        Scanner scanner = new Scanner(System.in);
        String cardForTransfer = scanner.next();
        if (!checkCardForTransfer(cardForTransfer)) {
            System.out.println("Probably you made a mistake in the card number. Please try again!");
        } else if (!login.containsKey(cardForTransfer)) {
            System.out.println("Such a card does not exist.");
        } else if (cardForTransfer.equals(numAccount)) {
            System.out.println("You can't transfer money to the same account!");
        } else {
            System.out.println("Enter how much money you want to transfer:");
            int moneyForTransfer = scanner.nextInt();
            if (returnBalance(numAccount) < moneyForTransfer) {
                System.out.println("Not enough money!");
            } else {
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
            }
            System.out.println();
            System.out.println("Success!");
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

    private static void closeAccount(String numAccount) throws SQLException {
        login.remove(numAccount);
        try (Statement statement = con.createStatement()) {
            statement.executeUpdate("DELETE FROM card WHERE number =" + numAccount);
        }
        System.out.println();
        System.out.println("The account has been closed!");
    }
}


