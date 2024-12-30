import JDBC.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class UpdateStock {

    static Scanner scanner = new Scanner(System.in);
    static Connection connection = Database.connect();

    public static void execute(){
        System.out.println("Enter batch number: ");
        String batch_number = scanner.nextLine();


        try {
            String checkquery = "SELECT * FROM stock WHERE Batch_number = ?";
            PreparedStatement checkStmt = connection.prepareStatement(checkquery);
            checkStmt.setString(1, batch_number);
            ResultSet resultSet = checkStmt.executeQuery();

            if (!resultSet.next()) {
                System.out.println("Batch not found.");
                return;
            }


            System.out.println("Batch Details: " +
                    ", Batch number = " + resultSet.getString("Batch_number") +
                    ", Item id = " + resultSet.getString("item_id") +
                    ", Qty = " + resultSet.getString("qty") +
                    ", Qty Type = " + resultSet.getString("qty_type") +
                    ", Batch Price = " + resultSet.getString("batch_price") +
                    ", Date of purchase = " + resultSet.getString("date_of_purchase") +
                    ", Expiry Date = " + resultSet.getString("expiry_date"));



            System.out.println("Enter new Item id (or press Enter to keep current): ");
            String item_id = scanner.nextLine();

            System.out.println("Enter new Quantity type (or press Enter to keep current): ");
            String qty_type = scanner.nextLine();

            System.out.println("Enter new Quantity (or press Enter to keep current): ");
            String qtyInput = scanner.nextLine();
            Integer qty = qtyInput.isBlank() ? null : Integer.parseInt(qtyInput);


            System.out.println("Enter new batch price (or press Enter to keep current): ");
            String batchPriceInput = scanner.nextLine();
            Integer batch_price = batchPriceInput.isBlank() ? null : Integer.parseInt(batchPriceInput);

            System.out.println("Enter new Date of purchase (or press Enter to keep current): ");
            String date_of_purchase = scanner.nextLine();

            System.out.println("Enter new expiry date (or press Enter to keep current): ");
            String expiry_date = scanner.nextLine();

            // Build the update query dynamically based on provided inputs
            String updateQuery = "UPDATE stock SET ";
            boolean hasUpdate = false;

            if (!item_id.isBlank()) {
                updateQuery += "item_id = ?, ";
                hasUpdate = true;
            }
            if (qty != null) {
                updateQuery += "qty = ?, ";
                hasUpdate = true;
            }
            if (!qty_type.isBlank()) {
                updateQuery += "qty_type = ?, ";
                hasUpdate = true;
            }
            if ( batch_price != null) {
                updateQuery += "batch_price = ?, ";
                hasUpdate = true;
            }
            if (!date_of_purchase.isBlank()) {
                updateQuery += "date_of_purchase = ?, ";
                hasUpdate = true;
            }
            if (!expiry_date.isBlank()) {
                updateQuery += "expiry_date = ?, ";
                hasUpdate = true;
            }

            if (!hasUpdate) {
                System.out.println("No changes specified. Exiting update process.");
                return;
            }


            // Remove trailing comma and add WHERE clause
            updateQuery = updateQuery.substring(0, updateQuery.length() - 2) + " WHERE Batch_number = ?";

            PreparedStatement updateStmt = connection.prepareStatement(updateQuery);


            int paramIndex = 1;
            if (!item_id.isBlank()) updateStmt.setString(paramIndex++, item_id);
            if (qty != null) updateStmt.setInt(paramIndex++, qty);
            if (!qty_type.isBlank()) updateStmt.setString(paramIndex++, qty_type);
            if (batch_price != null) updateStmt.setInt(paramIndex++, batch_price);
            if (!date_of_purchase.isBlank()) updateStmt.setString(paramIndex++, date_of_purchase);
            if (!expiry_date.isBlank()) updateStmt.setString(paramIndex++, expiry_date);

            updateStmt.setString(paramIndex, batch_number);

            int rowsUpdated = updateStmt.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("Stock updated successfully!");
            } else {
                System.out.println("Stock update failed.");
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }


    }
}
