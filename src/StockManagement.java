import JDBC.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class StockManagement {

    static Scanner scanner = new Scanner(System.in);
    static Connection connection = Database.connect();
    public static void execute(Scanner scanner){


        System.out.println("============================");
        System.out.println("      Stock Management");
        System.out.println("============================");

        System.out.println("1. Add new stock");
        System.out.println("2. Update existing stock");
        System.out.println("3. Remove stock");
        System.out.println("4. View all stocks");
        System.out.println("5. Exit");

        int crud_choice = scanner.nextInt();
        scanner.nextLine();

        switch (crud_choice){
            case 1: addNewStock();
                break;
            case 2: updateStock();
                break;
            case 3: removeStock();
                break;
            case 4: readStock();
                break;
            case 5:
                return;
        }
    }

    public static void addNewStock(){

        System.out.print("Enter Item Code: ");
        String itemcode = scanner.nextLine();

        System.out.print("Enter Batch Number: ");
        String batchNumber = scanner.nextLine();

        System.out.println("Quantity type (Units / KG): ");
        String quantity_type = scanner.nextLine();

        System.out.print("Enter Quantity: ");
        int quantity = scanner.nextInt();

        System.out.println("Batch price: ");
        int batch_price = scanner.nextInt();
        scanner.nextLine();

        System.out.println("Date of purchase (YYYY-MM-DD): ");
        String date_of_purchase = scanner.nextLine();

        System.out.print("Enter Expiration Date (YYYY-MM-DD): ");
        String expirationDate = scanner.next();

        System.out.print("Enter Supplier Name: ");
        scanner.nextLine();
        String supplier = scanner.nextLine();


        getItemId getItemId = new getItemId();

        int item_id = getItemId.execute(itemcode);

        if (item_id == -1) {
            System.out.println("Item not found. Please add the Item first.");
            return;
        }

        try {
            String addstockQuary = "INSERT INTO stock (Batch_number, item_id, qty, qty_type, batch_price, date_of_purchase, expiry_date) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = connection.prepareStatement(addstockQuary);

            String adjestItemQuary =  """
                        UPDATE item 
                        SET Stock_level = Stock_level + ? 
                        WHERE item_id = ?
                       """;
            PreparedStatement Itemstmt = connection.prepareStatement(adjestItemQuary);

            stmt.setString(1, batchNumber);
            stmt.setInt(2, item_id);
            stmt.setInt(3, quantity);
            stmt.setString(4, quantity_type);
            stmt.setInt(5, batch_price);
            stmt.setString(6, date_of_purchase);
            stmt.setString(7, expirationDate);

            Itemstmt.setInt(1, quantity);
            Itemstmt.setInt(2, item_id);

            Itemstmt.executeUpdate();
            int rowsInserted = stmt.executeUpdate();

            if (rowsInserted > 0) {
                System.out.println("Item added successfully!");
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateStock(){
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

    public static void removeStock(){
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

    public static void readStock() {

        String viewStockSql = "SELECT * FROM stock";

        try (PreparedStatement stmt = connection.prepareStatement(viewStockSql);
             ResultSet resultSet = stmt.executeQuery()) {

            System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------------------");
            System.out.printf("%-10s %-10s %-20s %-10s %-20s %-20s %-20s %-10s\n", "Stock ID", "Item ID", "Batch number", "Qty", "Qty type", "Batch price", "Date of purchase", "Expiry date");
            System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------------------");

            while (resultSet.next()) {
                int stockId = resultSet.getInt("stock_id");
                String batch_number = resultSet.getString("Batch_number");
                int itemId = resultSet.getInt("item_id");
                int quantity = resultSet.getInt("qty");
                String qty_type = resultSet.getString("qty_type");

                int batch_price = resultSet.getInt("batch_price");
                String date_of_purchase = resultSet.getString("date_of_purchase");
                String expiry_date = resultSet.getString("expiry_date");

                System.out.printf("%-10d %-10d %-20s %-10d %-20s %-20d %-20s %-10s\n", stockId, itemId, batch_number, quantity, qty_type, batch_price, date_of_purchase, expiry_date);
            }

        } catch (SQLException e) {
            System.out.println("Error reading stock data: " + e.getMessage());
        }

    }

}
