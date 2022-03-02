import org.sqlite.SQLiteDataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class ImSoLite {
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
                        "2. Add income\n" +
                        "3. Do transfer\n" +
                        "4. Close account\n" +
                        "5. Log out\n" +
                        "0. Exit");
                int num = scanner.nextInt();
                while (num != 0) {
                    switch (num) {
                        case 1:
                            balance(numberAccount);
                            break;
                        case 2:
                            addIncome(numberAccount);
                            break;
                        case 3:
                            doTransfer(numberAccount);
                            break;
                        case 4:
                            closeAccount(numberAccount);
                            break;
                        case 5:
                            logout();
                            break;
                    }
                    if (num == 1 | num == 2 | num == 3) {
                        System.out.println("\n1. Balance\n" +
                                "2. Add income\n" +
                                "3. Do transfer\n" +
                                "4. Close account\n" +
                                "5. Log out\n" +
                                "0. Exit");
                        num = scanner.nextInt();
                        scanner.nextLine();
                    } else {
                        break;
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

    private static void closeAccount(String numAccount) {
        login.remove(numAccount);
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(App3.url);
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
        System.out.println("\nTransfer");
        System.out.println("Enter card number:");
        String cardForTransfer = scanner.next();


        if (!checkCardForTransfer(cardForTransfer)) {
            System.out.println("\nProbably you made a mistake in the card number. Please try again!");
        } else if (!login.containsKey(cardForTransfer)) {
            System.out.println("\nSuch a card does not exist.");
        } else if (cardForTransfer.equals(numAccount)) {
            System.out.println("You can't transfer money to the same account!");
        } else {
            System.out.println("Enter how much money you want to transfer:");
            int moneyForTransfer = scanner.nextInt();
            if (returnBalance(numAccount) < moneyForTransfer) {
                System.out.println("Not enough money!");
            } else {
                SQLiteDataSource dataSource = new SQLiteDataSource();
                dataSource.setUrl(App3.url);
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
        System.out.println("\nEnter income:");
        int income = scanner.nextInt();
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(App3.url);
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
        System.out.println("\nYou have successfully logged out!");
    }

    private static void balance(String numAccount) {
        System.out.printf("\nBalance: %d\n", returnBalance(numAccount));
    }

    private static int returnBalance(String numAccount) {
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(App3.url);
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
        System.out.println("\nYour card has been created\n" +
                "Your card number:");
        StringBuilder numberAccount = createBankAccount();

        System.out.println("Your card PIN:");
        StringBuilder pin = createPin();
        login.put(String.valueOf(numberAccount), String.valueOf(pin));
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(App3.url);
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
}
