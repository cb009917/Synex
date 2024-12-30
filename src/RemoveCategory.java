import JDBC.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class RemoveCategory {

    static Scanner scanner = new Scanner(System.in);
    static Connection connection = Database.connect();
    static getCategoryId categoryId = new getCategoryId();

    public static void execute(){

        try {
            System.out.println("Enter the Category name ");
            String category_name = scanner.nextLine();

            int category_id = categoryId.execute(category_name);


            // Check if item exists
            String checkQuery = "SELECT * FROM item_category WHERE Category_id = ?";
            PreparedStatement checkStmt = connection.prepareStatement(checkQuery);
            checkStmt.setInt(1, category_id);
            ResultSet resultSet = checkStmt.executeQuery();

            if (!resultSet.next()) {
                System.out.println("Category not found.");
                return;
            }

            // Confirm deletion
            System.out.println("Are you sure you want to delete this Category? (yes/no): ");
            String confirmation = scanner.nextLine();
            if (!confirmation.equalsIgnoreCase("yes")) {
                System.out.println("Deletion cancelled.");
                return;
            }

            // Delete category
            String deleteQuery = "DELETE FROM item_category WHERE Category_id = ?";
            PreparedStatement deleteStmt = connection.prepareStatement(deleteQuery);
            deleteStmt.setInt(1, category_id);

            int rowsDeleted = deleteStmt.executeUpdate();

            if (rowsDeleted > 0) {
                System.out.println("Category removed successfully!");
            } else {
                System.out.println("Failed to remove the Category.");
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }
}
