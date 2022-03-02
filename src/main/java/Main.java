import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        String dbPath = null;
        for (int i = 0; i < args.length; i++) {
            if ("-filename".equalsIgnoreCase(args[i])) {
                dbPath = args[i + 1];
                break;
            }
        }
        String url;
        url = "jdbc:sqlite:" + args[1];
        Application app;
        if (dbPath != null) {
            try {
                app = new Application(url);
                app.connectBD();
            } catch (SQLException ignored) {
            }

        }
    }
}