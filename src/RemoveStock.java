import JDBC.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class RemoveStock {

    static Scanner scanner = new Scanner(System.in);
    static Connection connection = Database.connect();

    public static void execute(){
        try {
            System.out.println("Enter the Batch number: ");
            String batch_number = scanner.nextLine();

            // Check if item exists
            String checkQuery = "SELECT * FROM stock WHERE Batch_number = ?";
            PreparedStatement checkStmt = connection.prepareStatement(checkQuery);
            checkStmt.setString(1, batch_number);
            ResultSet resultSet = checkStmt.executeQuery();

            if (!resultSet.next()) {
                System.out.println("Batch not found.");
                return;
            }

            // Confirm deletion
            System.out.println("Are you sure you want to delete this batch? (yes/no): ");
            String confirmation = scanner.nextLine();
            if (!confirmation.equalsIgnoreCase("yes")) {
                System.out.println("Deletion cancelled.");
                return;
            }

            // Delete item
            String deleteQuery = "DELETE FROM stock WHERE Batch_number = ?";
            PreparedStatement deleteStmt = connection.prepareStatement(deleteQuery);
            deleteStmt.setString(1, batch_number);

            int rowsDeleted = deleteStmt.executeUpdate();

            if (rowsDeleted > 0) {
                System.out.println("Batch removed successfully!");
            } else {
                System.out.println("Failed to remove the batch.");
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }
}
