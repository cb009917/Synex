import JDBC.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;


public class RemoveItem {

    static Scanner scanner = new Scanner(System.in);
    static Connection connection = Database.connect();

    public static void execute(){
        try {
            System.out.println("Enter the item code: ");
            String code = scanner.nextLine();

            // Check if item exists
            String checkQuery = "SELECT * FROM item WHERE Code = ?";
            PreparedStatement checkStmt = connection.prepareStatement(checkQuery);
            checkStmt.setString(1, code);
            ResultSet resultSet = checkStmt.executeQuery();

            if (!resultSet.next()) {
                System.out.println("Item with code " + code + " not found.");
                return;
            }

            // Confirm deletion
            System.out.println("Are you sure you want to delete this item? (yes/no): ");
            String confirmation = scanner.nextLine();
            if (!confirmation.equalsIgnoreCase("yes")) {
                System.out.println("Deletion cancelled.");
                return;
            }

            // Delete item
            String deleteQuery = "DELETE FROM item WHERE code = ?";
            PreparedStatement deleteStmt = connection.prepareStatement(deleteQuery);
            deleteStmt.setString(1, code);

            int rowsDeleted = deleteStmt.executeUpdate();

            if (rowsDeleted > 0) {
                System.out.println("Item removed successfully!");
            } else {
                System.out.println("Failed to remove the item.");
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
//        catch (
//                SQLException e) {
//            System.err.println("Database error: " + e.getMessage());
//        }
    }
}
