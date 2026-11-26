
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.util.Scanner;

public class DBConnection {

    // 1. Connect Java with MySQL
    public static Connection connectDatabase() throws SQLException {

        String url = "jdbc:mysql://localhost:3306/expense_tracker";
        String username = "root";
        String password = "123@123";

        return DriverManager.getConnection(url, username, password);
    }

    // 2. Add transaction using Transaction object
    public static void addTransaction(Transaction transaction)
            throws SQLException {

        String sql = "INSERT INTO transactions " +
                "(title, amount, type, category, transaction_date) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection con = connectDatabase();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, transaction.getTitle());
            ps.setDouble(2, transaction.getAmount());
            ps.setString(3, transaction.getType());
            ps.setString(4, transaction.getCategory());
            ps.setDate(5, Date.valueOf(transaction.getDate()));

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Transaction added successfully!");
            }
        }
    }

    // 3. View all transactions using Transaction objects
    public static void displayAllTransactions() throws SQLException {

        String sql = "SELECT * FROM transactions";

        try (Connection con = connectDatabase();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            boolean found = false;

            System.out.println("\n===== ALL TRANSACTIONS =====");

            while (rs.next()) {

                found = true;

                Transaction transaction = new Transaction(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getDouble("amount"),
                        rs.getString("type"),
                        rs.getString("category"),
                        rs.getDate("transaction_date").toString()
                );

                transaction.displayDetails();
                System.out.println("----------------------------");
            }

            if (!found) {
                System.out.println("No transactions found.");
            }
        }
    }

    // 4. Search transactions by type
    public static void displayTransactionsByType(String type)
            throws SQLException {

        String sql = "SELECT * FROM transactions WHERE type = ?";

        try (Connection con = connectDatabase();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, type);

            try (ResultSet rs = ps.executeQuery()) {

                boolean found = false;

                System.out.println("\n===== " + type.toUpperCase()
                        + " TRANSACTIONS =====");

                while (rs.next()) {

                    found = true;

                    Transaction transaction = new Transaction(
                            rs.getInt("id"),
                            rs.getString("title"),
                            rs.getDouble("amount"),
                            rs.getString("type"),
                            rs.getString("category"),
                            rs.getDate("transaction_date").toString()
                    );

                    transaction.displayDetails();
                    System.out.println("----------------------------");
                }

                if (!found) {
                    System.out.println("No matching transactions found.");
                }
            }
        }
    }

    // 5. Search transactions by category
    public static void displayTransactionsByCategory(String category)
            throws SQLException {

        String sql = "SELECT * FROM transactions WHERE category = ?";

        try (Connection con = connectDatabase();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, category);

            try (ResultSet rs = ps.executeQuery()) {

                boolean found = false;

                System.out.println("\n===== CATEGORY: "
                        + category.toUpperCase() + " =====");

                while (rs.next()) {

                    found = true;

                    Transaction transaction = new Transaction(
                            rs.getInt("id"),
                            rs.getString("title"),
                            rs.getDouble("amount"),
                            rs.getString("type"),
                            rs.getString("category"),
                            rs.getDate("transaction_date").toString()
                    );

                    transaction.displayDetails();
                    System.out.println("----------------------------");
                }

                if (!found) {
                    System.out.println("No matching transactions found.");
                }
            }
        }
    }

    // 6. Calculate total income
    public static double calculateTotalIncome() throws SQLException {

        String sql = "SELECT SUM(amount) AS totalIncome " +
                "FROM transactions WHERE type = 'Income'";

        try (Connection con = connectDatabase();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getDouble("totalIncome");
            }
        }

        return 0;
    }

    // 7. Calculate total expenses
    public static double calculateTotalExpense() throws SQLException {

        String sql = "SELECT SUM(amount) AS totalExpense " +
                "FROM transactions WHERE type = 'Expense'";

        try (Connection con = connectDatabase();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getDouble("totalExpense");
            }
        }

        return 0;
    }

    // 8. Calculate remaining balance
    public static double calculateBalance(double income, double expense) {

        return income - expense;
    }

    // 9. Display financial summary
    public static void displayFinancialSummary() throws SQLException {

        double totalIncome = calculateTotalIncome();
        double totalExpense = calculateTotalExpense();
        double balance = calculateBalance(totalIncome, totalExpense);

        System.out.println("\n===== FINANCIAL SUMMARY =====");
        System.out.println("Total Income: " + totalIncome);
        System.out.println("Total Expense: " + totalExpense);
        System.out.println("Remaining Balance: " + balance);
    }

    // 10. Delete transaction
    public static void deleteTransaction(int id) throws SQLException {

        String sql = "DELETE FROM transactions WHERE id = ?";

        try (Connection con = connectDatabase();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Transaction deleted successfully!");
            } else {
                System.out.println("No transaction found with this ID.");
            }
        }
    }

    // 11. Update transaction
    public static void updateTransaction(
            int id, String title, double amount,
            String type, String category, String date)
            throws SQLException {

        String sql = "UPDATE transactions SET title = ?, amount = ?, " +
                "type = ?, category = ?, transaction_date = ? WHERE id = ?";

        try (Connection con = connectDatabase();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, title);
            ps.setDouble(2, amount);
            ps.setString(3, type);
            ps.setString(4, category);
            ps.setDate(5, Date.valueOf(date));
            ps.setInt(6, id);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Transaction updated successfully!");
            } else {
                System.out.println("No transaction found with this ID.");
            }
        }
    }

    // 12. Read a valid positive amount
    public static double readAmount(Scanner sc) {

        while (true) {

            try {
                System.out.print("Enter amount: ");
                double amount = Double.parseDouble(sc.nextLine());

                if (amount > 0) {
                    return amount;
                }

                System.out.println("Amount must be greater than zero.");

            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    // 13. Read and validate transaction type
    public static String readType(Scanner sc) {

        while (true) {

            System.out.print("Enter type (Income/Expense): ");
            String type = sc.nextLine().trim();

            if (type.equalsIgnoreCase("Income")) {
                return "Income";
            }

            if (type.equalsIgnoreCase("Expense")) {
                return "Expense";
            }

            System.out.println("Please enter Income or Expense.");
        }
    }

    // 14. Read a non-empty value
    public static String readNonEmpty(Scanner sc, String message) {

        while (true) {

            System.out.print(message);
            String value = sc.nextLine().trim();

            if (!value.isEmpty()) {
                return value;
            }

            System.out.println("This field cannot be empty.");
        }
    }

    // 15. Read a valid date
    public static String readDate(Scanner sc) {

        while (true) {

            System.out.print("Enter date (YYYY-MM-DD): ");
            String date = sc.nextLine().trim();

            try {
                Date.valueOf(date);
                return date;

            } catch (IllegalArgumentException e) {
                System.out.println(
                        "Invalid date. Please use YYYY-MM-DD."
                );
            }
        }
    }

    // 16. Read a valid integer
    public static int readInteger(Scanner sc, String message) {

        while (true) {

            try {
                System.out.print(message);
                return Integer.parseInt(sc.nextLine().trim());

            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid whole number.");
            }
        }
    }

    // 17. Main method
    public static void main(String[] args) {

        try (Scanner sc = new Scanner(System.in)) {

            // Test database connection
            try (Connection con = connectDatabase()) {
                System.out.println("Database connected successfully!");
            }

            int choice = 0;

            while (choice != 8) {

                System.out.println("\n===== SMART EXPENSE TRACKER =====");
                System.out.println("1. Add Transaction");
                System.out.println("2. View All Transactions");
                System.out.println("3. Search by Type");
                System.out.println("4. View Financial Summary");
                System.out.println("5. Search by Category");
                System.out.println("6. Delete Transaction");
                System.out.println("7. Update Transaction");
                System.out.println("8. Exit");

                choice = readInteger(sc, "Enter your choice: ");

                try {

                    switch (choice) {

                        case 1: {
                            System.out.println("\n===== ADD TRANSACTION =====");

                            String title = readNonEmpty(
                                    sc, "Enter title: "
                            );

                            double amount = readAmount(sc);
                            String type = readType(sc);

                            String category = readNonEmpty(
                                    sc, "Enter category: "
                            );

                            String date = readDate(sc);

                            Transaction transaction = new Transaction(
                                    0, title, amount, type, category, date
                            );

                            addTransaction(transaction);
                            break;
                        }

                        case 2:
                            displayAllTransactions();
                            break;

                        case 3: {
                            String type = readType(sc);
                            displayTransactionsByType(type);
                            break;
                        }

                        case 4:
                            displayFinancialSummary();
                            break;

                        case 5: {
                            String category = readNonEmpty(
                                    sc, "Enter category to search: "
                            );

                            displayTransactionsByCategory(category);
                            break;
                        }

                        case 6: {
                            int id = readInteger(
                                    sc, "Enter transaction ID to delete: "
                            );

                            deleteTransaction(id);
                            break;
                        }

                        case 7: {
                            System.out.println("\n===== UPDATE TRANSACTION =====");

                            int id = readInteger(
                                    sc, "Enter transaction ID to update: "
                            );

                            String title = readNonEmpty(
                                    sc, "Enter new title: "
                            );

                            double amount = readAmount(sc);
                            String type = readType(sc);

                            String category = readNonEmpty(
                                    sc, "Enter new category: "
                            );

                            String date = readDate(sc);

                            updateTransaction(
                                    id, title, amount, type, category, date
                            );

                            break;
                        }

                        case 8:
                            System.out.println(
                                    "Thank you for using Smart Expense Tracker!"
                            );
                            break;

                        default:
                            System.out.println(
                                    "Invalid choice. Please select 1 to 8."
                            );
                    }

                } catch (SQLException e) {
                    System.out.println("Database error: " + e.getMessage());

                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid input: " + e.getMessage());
                }
            }

        } catch (SQLException e) {
            System.out.println(
                    "Could not connect to the database: " + e.getMessage()
            );
        }
    }
}
