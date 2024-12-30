import JDBC.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class RemovePromotion {

    static Connection connection = Database.connect();

    public static void execute(Scanner scanner) {

        try {
            System.out.println("Enter the Promotion ID to remove: ");
            int promoId = scanner.nextInt();

            // Check if item exists
            String checkQuery = "SELECT * FROM discount WHERE discount_id = ?";
            PreparedStatement checkStmt = connection.prepareStatement(checkQuery);
            checkStmt.setInt(1, promoId);
            ResultSet resultSet = checkStmt.executeQuery();

            if (!resultSet.next()) {
                System.out.println("Promotion not found.");
                return;
            }

            // Confirm deletion
            System.out.println("Are you sure you want to delete this promotion? (yes/no): ");
            String confirmation = scanner.nextLine();
            if (!confirmation.equalsIgnoreCase("yes")) {
                System.out.println("Deletion cancelled.");
                return;
            }

            // Delete item
            String deleteQuery = "DELETE FROM discount WHERE discount_id = ?";
            PreparedStatement deleteStmt = connection.prepareStatement(deleteQuery);
            deleteStmt.setInt(1, promoId);

            int rowsDeleted = deleteStmt.executeUpdate();

            if (rowsDeleted > 0) {
                System.out.println("Promotion removed successfully!");
            } else {
                System.out.println("Failed to remove the promotion.");
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }
}
