import JDBC.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class ItemManagment {

    static Scanner scanner = new Scanner(System.in);
    static Connection connection = Database.connect();
    static getCategoryId getCategoryId = new getCategoryId();

    public static void execute(Scanner scanner){

        System.out.println("1. Add new item");
        System.out.println("2. Update existing item");
        System.out.println("3. Remove item");
        System.out.println("4. Exit");

        int crud_choice = scanner.nextInt();
        scanner.nextLine();

        switch (crud_choice){
            case 1: addNewItem();
                    break;
            case 2: updateItem();
                    break;
            case 3: removeItem();
                    break;
            case 4 :
                return;
        }
    }

    public static void addNewItem(){
        try{
            System.out.println("Enter item code: ");
            String code = scanner.nextLine();

            System.out.println("Enter item name: ");
            String name = scanner.nextLine();

            System.out.println("Enter shelf ID: ");
            String shelfId = scanner.nextLine();

            System.out.println("Enter category name: ");
            String categoryName = scanner.nextLine();


            System.out.println("Enter price: ");
            double price = scanner.nextDouble();
            scanner.nextLine();

            System.out.println("Enter manufacturer: ");
            String manufacturer = scanner.nextLine();


            int categoryId = getCategoryId.execute(categoryName);


            if (categoryId == -1) {
                System.out.println("Category not found. Please add the category first.");

            }

            // Step 2: Insert the new item into the item table
            String additemquary = "INSERT INTO item (code, shelf_id, category_id, name, price, manufacturer) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = connection.prepareStatement(additemquary);

            stmt.setString(1, code);
            stmt.setString(2, shelfId);
            stmt.setInt(3, categoryId);
            stmt.setString(4, name);
            stmt.setDouble(5, price);
            stmt.setString(6, manufacturer);

            int rowsInserted = stmt.executeUpdate();

            if (rowsInserted > 0) {
                System.out.println("Item added successfully!");
            }

        } catch ( SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }

    public static void removeItem() {
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

    public static void updateItem(){

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
