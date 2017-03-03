import org.h2.tools.Server;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    public static void createTables(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.execute("CREATE TABLE IF NOT EXISTS  todos " +
                "(id IDENTITY, text VARCHAR, is_done VARCHAR, user_id INTEGER)");

        stmt.execute("CREATE TABLE IF NOT EXISTS users " +
                "(id IDENTITY , user_name VARCHAR)");
    }

    public static void insertToDo(Connection conn, String text, int user_id) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO todos VALUES (NULL, ?, FALSE, ? );");
        stmt.setString(1, text);
        stmt.setInt(2, user_id);
        stmt.execute();
    }

    public static ToDoItem selectItem(Connection conn, int id) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(
                "SELECT * FROM todos JOIN users ON todos.user_id = users.id WHERE todos.user_id = ?"); //
        stmt.setInt(1, id);
        ResultSet results = stmt.executeQuery();
        if (results.next()) {
            int user_id = results.getInt("todos.user_id");
            boolean is_done = results.getBoolean("todos.is_done");
            String text = results.getString("todos.text");
            return new ToDoItem(id, text, is_done, user_id);
        }
        return null;
    }

    public static ArrayList<ToDoItem> selectItems(Connection conn, int user_id) throws SQLException {
        ArrayList<ToDoItem> toDoItems = new ArrayList<>();
        PreparedStatement stmt = conn.prepareStatement(
                "SELECT * FROM todos JOIN users ON todos.user_id = users.id WHERE todos.user_id = ?");
        stmt.setInt(1, user_id);
        ResultSet resultSet = stmt.executeQuery();
        while (resultSet.next()) {
            boolean is_done = resultSet.getBoolean("is_done");
            String userName = resultSet.getString("users.user_name");
            String text = resultSet.getString("todos.text");
            toDoItems.add(new ToDoItem(user_id, text, is_done, userName));
        }
        return toDoItems;
    }

    public static void toggleToDo(Connection conn, int id) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("UPDATE todos  SET is_done = NOT is_done WHERE id = ?");
        stmt.setInt(1, id);
        stmt.execute();
    }

    public static void insertUser(Connection conn, String user_name) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO users VALUES (NULL, ?);");
        stmt.setString(1, user_name);
        stmt.execute();
    }

    public static User selectUser(Connection conn, String user_name) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE user_name = ?");
        stmt.setString(1, user_name);
        ResultSet results = stmt.executeQuery();
        if (results.next()) {
            int id = results.getInt("ID");
            user_name = results.getString("USER_NAME");
            return new User(id, user_name);
        }

        return null;
    }

    private static void deleteItem(Connection conn, int itemNumDelete) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM todos WHERE ID = ?");
        stmt.setInt(1, itemNumDelete);
        stmt.execute();
    }

    public static void main(String[] args) throws SQLException {
        Server.createWebServer().start();
        Connection conn = DriverManager.getConnection("jdbc:h2:./main");
        createTables(conn);
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to the To Do Database. Please enter your name to continue:");
        String loginName = scanner.nextLine();
        insertUser(conn, loginName);
        User userList = selectUser(conn, loginName);

        while (true) {
            System.out.println("1. Create to-do item");
            System.out.println("2. Toggle do-item");
            System.out.println("3. List to-do items");
            System.out.println("4. Delete to-do item");

            String option = scanner.nextLine();

            if (option.equals("1")) {
                System.out.println("Enter your to-do item: ");
                String text = scanner.nextLine();
                insertToDo(conn, text, (selectUser(conn, loginName).id));

            } else if (option.equals("2")) {
                System.out.println("Enter the number of the item you want to toggle: ");
                int itemNum = Integer.parseInt(scanner.nextLine());
                toggleToDo(conn, itemNum);

            } else if (option.equals("3")) {
                ArrayList<ToDoItem> itemArrayList = selectItems(conn, 1);
                int i = 0;
                for (ToDoItem item : itemArrayList) {
                    String checkbox = "[ ] ";
                    if (item.isDone) {
                        checkbox = "[X] ";
                    }
                    System.out.printf(checkbox + " " + itemArrayList.get(i).text + "\n");
                    i++;
                }
            } else if (option.equals("4")) {
                System.out.println("Enter the number of the item you want to delete: ");
                Scanner scanner1 = new Scanner(System.in);
                int itemNumDelete = Integer.parseInt(scanner1.nextLine());
                deleteItem(conn, itemNumDelete);
                System.out.println("Item deleted!");
            }
        }

    }
}