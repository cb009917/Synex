import JDBC.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class categoryManagment {

    static Scanner scanner = new Scanner(System.in);
    static Connection connection = Database.connect();
    static getCategoryId categoryId = new getCategoryId();

    public static void execute(Scanner scanner){



        System.out.println("1. Add new category");
        System.out.println("2. Update existing category");
        System.out.println("3. Remove category");
        System.out.println("4. View all categories");
        System.out.println("5. Exit");

        int crud_choice = scanner.nextInt();
        scanner.nextLine();

        switch (crud_choice){
            case 1: addCategory();
                break;
            case 2: updateCategory();
                break;
            case 3: removeCategory();
                break;
            case 4: readCategory();
                break;
            case 5:
                return;
        }
    }

    public static void addCategory() {
        System.out.println("Enter category name:");
        String category_name = scanner.nextLine();

        System.out.println("Enter description: ");
        String category_desc = scanner.nextLine();

        // Check if the category already exists
        try {
            String checkCategoryQuery = "SELECT COUNT(*) FROM item_category WHERE Name = ?";
            PreparedStatement checkStmt = connection.prepareStatement(checkCategoryQuery);
            checkStmt.setString(1, category_name);
            ResultSet resultSet = checkStmt.executeQuery();

            if (resultSet.next() && resultSet.getInt(1) > 0) {
                System.out.println("Category already exists. Please enter a different category name.");
                return; // Exit the method
            }
        } catch (SQLException e) {
            System.err.println("Error checking category: " + e.getMessage());
            return;
        }

        // Insert the new category
        try {
            String addCategoryQuery = "INSERT INTO item_category (Name, Description) VALUES (?, ?)";
            PreparedStatement stmt = connection.prepareStatement(addCategoryQuery);

            stmt.setString(1, category_name);
            stmt.setString(2, category_desc);

            int rowsInserted = stmt.executeUpdate();

            if (rowsInserted > 0) {
                System.out.println("Category added successfully!");
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }


    public static void removeCategory(){

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

    public static void updateCategory(){
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

    public static void readCategory() {

        String viewStockSql = "SELECT * FROM item_category";

        try (PreparedStatement stmt = connection.prepareStatement(viewStockSql);
             ResultSet resultSet = stmt.executeQuery()) {

            System.out.println("-----------------------------------------------------------");
            System.out.printf("%-12s %-20s %-20s\n", "Stock ID", "Name", "Description");
            System.out.println("-----------------------------------------------------------");

            while (resultSet.next()) {
                int category_id = resultSet.getInt("Category_id");
                String name = resultSet.getString("Name");
                String description = resultSet.getString("Description");

                System.out.printf("%-12d %-15s %-20s\n", category_id, name, description);
            }

        } catch (SQLException e) {
            System.out.println("Error reading stock data: " + e.getMessage());
        }

    }
}
