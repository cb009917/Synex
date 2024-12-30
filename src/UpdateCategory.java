import JDBC.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class UpdateCategory {

    static Scanner scanner = new Scanner(System.in);
    static Connection connection = Database.connect();

    public static void execute(){
        System.out.println("Enter item category name: ");
        String category_name = scanner.nextLine();
        getCategoryId getCategoryId = new getCategoryId();

        int category_id = getCategoryId.execute(category_name);

        try {
            String checkquery = "SELECT * FROM item_category WHERE Category_id = ?";
            PreparedStatement checkStmt = connection.prepareStatement(checkquery);
            checkStmt.setInt(1, category_id);
            ResultSet resultSet = checkStmt.executeQuery();

            if (!resultSet.next()) {
                System.out.println("Category not found.");
                return;
            }


            System.out.println("Category Details: " +
                    ", Name = " + resultSet.getString("Name") +
                    ", Description = " + resultSet.getString("Description") );



            System.out.println("Enter new name (or press Enter to keep current): ");
            String name = scanner.nextLine();

            System.out.println("Enter new Description (or press Enter to keep current): ");
            String description = scanner.nextLine();

            // Build the update query dynamically based on provided inputs
            String updateQuery = "UPDATE item_category SET ";
            boolean hasUpdate = false;

            if (!name.isBlank()) {
                updateQuery += "Name = ?, ";
                hasUpdate = true;
            }
            if (!description.isBlank()) {
                updateQuery += "Description = ?, ";
                hasUpdate = true;
            }

            if (!hasUpdate) {
                System.out.println("No changes specified. Exiting update process.");
                return;
            }

            // Remove trailing comma and add WHERE clause
            updateQuery = updateQuery.substring(0, updateQuery.length() - 2) + " WHERE Category_id = ?";

            PreparedStatement updateStmt = connection.prepareStatement(updateQuery);

            int paramIndex = 1;
            if (!name.isBlank()) updateStmt.setString(paramIndex++, name);
            if (!description.isBlank()) updateStmt.setString(paramIndex++, description);

            updateStmt.setInt(paramIndex, category_id);

            int rowsUpdated = updateStmt.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("Category updated successfully!");
            } else {
                System.out.println("Category update failed.");
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }
}
