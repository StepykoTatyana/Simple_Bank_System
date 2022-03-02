import org.sqlite.SQLiteDataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class Application {
    private Scanner scanner;
    static String url;
    private Statement statement;
    LinkedHashMap<String, String> login = new LinkedHashMap<>();
    String num = "-1";

    public Application(String url) {
        try {
            scanner = new Scanner(System.in);
            SQLiteDataSource dataSource = new SQLiteDataSource();

            Application.url = url;

            dataSource.setUrl(url);
            Connection connection = dataSource.getConnection();
            statement = connection.createStatement();
        } catch (SQLException ignored) {

        }
    }

    public void connectBD() throws SQLException {
        createBD();
        returnAllNumberCard();
        printMainMenu();
        //statement.executeUpdate("DELETE from card WHERE number is not null");
        System.out.println("Bye!");

    }


    private void printMainMenu() throws SQLException {
        while (!Objects.equals(num, "0")) {
            System.out.println();
            System.out.println("1. Create an account\n" +
                    "2. Log into account\n" +
                    "0. Exit");
            num = scanner.nextLine();
            if (num.equals("1")) {

                first();
            } else if (num.equals("2")) {
                second();
            }
        }
    }

    private void first() throws SQLException {
        System.out.println();
        System.out.println("Your card has been created\n" +
                "Your card number:");
        StringBuilder numberAccount = createBankAccount();

        System.out.println("Your card PIN:");
        StringBuilder pin = createPin();
        login.put(String.valueOf(numberAccount), String.valueOf(pin));

        statement.executeUpdate("INSERT INTO card(number, pin) VALUES (" + numberAccount +
                ", " + '\'' + pin + '\'' + ")");
    }

    private StringBuilder createBankAccount() {
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

    private StringBuilder createPin() {
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

    private void createBD() throws SQLException {
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS card(" +
                "id INTEGER PRIMARY KEY," +
                "number TEXT NOT NULL," +
                "pin TEXT NOT NULL," +
                "balance INTEGER default 0," +
                "UNIQUE(number))");
    }

    private void logout() {
        System.out.println("You have successfully logged out!");
    }

    private void balance(String numAccount) {
        System.out.println();
        System.out.printf("Balance: %d\n", returnBalance(numAccount));
    }

    private int returnBalance(String numAccount) {
        try (ResultSet balanceBd = statement.executeQuery("SELECT balance FROM card where number =" + numAccount)) {
            return balanceBd.getInt("balance");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return 0;
    }

    private void addIncome(String numAccount) throws SQLException {
        System.out.println();
        System.out.println("Enter income:");
        int income = Integer.parseInt(scanner.nextLine());
        statement.executeUpdate("UPDATE card " +
                "SET balance = balance +" + income +
                " WHERE number = " + numAccount);
        System.out.println("Income was added!");
        System.out.println();
    }

    private Boolean checkCardForTransfer(String cardForTransfer) {
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

    private void doTransfer(String numAccount) throws SQLException {
        System.out.println();
        System.out.println("Transfer");
        System.out.println("Enter card number:");
        String cardForTransfer = scanner.nextLine();
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
                statement.executeUpdate("UPDATE card " +
                        "SET balance = balance -" + moneyForTransfer +
                        " WHERE number = " + numAccount);
                statement.executeUpdate("UPDATE card " +
                        "SET balance = balance +" + moneyForTransfer +
                        " WHERE number = " + cardForTransfer);

                System.out.println("Success!");
            }
        }

    }

    private void closeAccount(String numAccount) throws SQLException {
        login.remove(numAccount);
        statement.executeUpdate("DELETE FROM card WHERE number =" + numAccount);
        System.out.println();
        System.out.println("The account has been closed!");
    }

    private void second() {
        System.out.println();
        System.out.println("Enter your card number: ");
        String numberAccount = scanner.nextLine();
        System.out.println("Enter your PIN:");
        String pin = scanner.nextLine();
        try {
            if (login.get(numberAccount).equals(pin)) {
                System.out.println();
                System.out.println("You have successfully logged in!\n");
                num = "-1";

                while (!Objects.equals(num, "0")) {
                    System.out.println("1. Balance\n" +
                            "2. Add income\n" +
                            "3. Do transfer\n" +
                            "4. Close account\n" +
                            "5. Log out\n" +
                            "0. Exit");
                    num = scanner.nextLine();
                    if (num.equals("1")) {
                        balance(numberAccount);
                    } else if (num.equals("2")) {
                        addIncome(numberAccount);
                    } else if (num.equals("3")) {
                        doTransfer(numberAccount);
                    } else if (num.equals("4")) {
                        closeAccount(numberAccount);
                        break;
                    } else if (num.equals("5")) {
                        logout();
                        break;
                    }
                }
            } else {
                System.out.println("Wrong card number or PIN!");
            }
        } catch (Exception e) {
            System.out.println("Wrong card number or PIN!");
        }
    }

    private void returnAllNumberCard() {
        try (ResultSet numberAllList = statement.executeQuery("SELECT Number, pin FROM card ")) {
            while (numberAllList.next()) {
                String numberAccount = numberAllList.getString("number");
                String pin = numberAllList.getString("pin");
                login.put(String.valueOf(numberAccount), String.valueOf(pin));
            }
            System.out.println(login);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

}
