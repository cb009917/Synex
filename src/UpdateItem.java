import JDBC.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class UpdateItem {

    public static void execute(){

        Scanner scanner = new Scanner(System.in);
        Connection connection = Database.connect();

        System.out.println("Enter item code: ");
        String code = scanner.nextLine();

        try {
            String checkquery = "SELECT * FROM item WHERE code = ?";
            PreparedStatement checkStmt = connection.prepareStatement(checkquery);
            checkStmt.setString(1, code);
            ResultSet resultSet = checkStmt.executeQuery();

            if (!resultSet.next()) {
                System.out.println("Item with code " + code + " not found.");
                return;
            }


            System.out.println("Item Details: Code = " + resultSet.getString("code") +
                    ", Name = " + resultSet.getString("name") +
                    ", Shelf ID = " + resultSet.getString("shelf_id") +
                    ", Category ID = " + resultSet.getInt("category_id") +
                    ", Price = " + resultSet.getDouble("price") +
                    ", Manufacturer = " + resultSet.getString("manufacturer"));


            System.out.println("Enter new name (or press Enter to keep current): ");
            String name = scanner.nextLine();

            System.out.println("Enter new shelf ID (or press Enter to keep current): ");
            String shelfId = scanner.nextLine();

            System.out.println("Enter new price (or press Enter to keep current): ");
            String priceInput = scanner.nextLine();

            System.out.println("Enter new manufacturer (or press Enter to keep current): ");
            String manufacturer = scanner.nextLine();

            // Build the update query dynamically based on provided inputs
            String updateQuery = "UPDATE item SET ";
            boolean hasUpdate = false;

            if (!name.isBlank()) {
                updateQuery += "name = ?, ";
                hasUpdate = true;
            }
            if (!shelfId.isBlank()) {
                updateQuery += "shelf_id = ?, ";
                hasUpdate = true;
            }
            if (!priceInput.isBlank()) {
                updateQuery += "price = ?, ";
                hasUpdate = true;
            }
            if (!manufacturer.isBlank()) {
                updateQuery += "manufacturer = ?, ";
                hasUpdate = true;
            }

            if (!hasUpdate) {
                System.out.println("No changes specified. Exiting update process.");
                return;
            }

            // Remove trailing comma and add WHERE clause
            updateQuery = updateQuery.substring(0, updateQuery.length() - 2) + " WHERE code = ?";

            PreparedStatement updateStmt = connection.prepareStatement(updateQuery);

            int paramIndex = 1;
            if (!name.isBlank()) updateStmt.setString(paramIndex++, name);
            if (!shelfId.isBlank()) updateStmt.setString(paramIndex++, shelfId);
            if (!priceInput.isBlank()) updateStmt.setDouble(paramIndex++, Double.parseDouble(priceInput));
            if (!manufacturer.isBlank()) updateStmt.setString(paramIndex++, manufacturer);
            updateStmt.setString(paramIndex, code);

            int rowsUpdated = updateStmt.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("Item updated successfully!");
            } else {
                System.out.println("Item update failed.");
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }
}
