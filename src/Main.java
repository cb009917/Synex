import JDBC.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {

    // can we define it here
    static Connection connection = Database.connect();
    static String employeeName;

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);


        System.out.println("Welcome to the Synex POS System!");
        System.out.print("Please enter your Employee ID : ");

        int employeeId = scanner.nextInt();

        // Retrieve the employee's name
        employeeName = getEmployeeName(employeeId);

        if (employeeName != null) {
            System.out.println("Access granted. Welcome, " + employeeName + " (Employee ID: " + employeeId + ")");
            mainMenu(scanner); // Pass employee ID to the main menu
        } else {
            System.out.println("Access denied. Invalid Employee ID.");
            System.out.println("Please try again.");

        }

        scanner.close();

    }

    public static String getEmployeeName(int employeeId) {
        String employeeName = null;

        String query = "SELECT name FROM employees WHERE employee_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, employeeId);

            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                employeeName = resultSet.getString("name"); // Retrieve employee's name
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return employeeName; // Returns null if no match is found
    }


     public static void mainMenu(Scanner scanner){
         while (true) {
             System.out.println("\n1. Bill Customer");
             System.out.println("2. View Bills");
             System.out.println("3. Manege Products");
             System.out.println("4. Category management");
             System.out.println("5. Daily sales report");
             System.out.println("6. Stock Report");
             System.out.println("7. Restock Report");
             System.out.println("8. Reshelve Report");
             System.out.println("9. Stock Management");
             System.out.println("10. Exit");
             System.out.print("Enter your choice: ");
             int choice = scanner.nextInt();
             scanner.nextLine();

             switch (choice) {
                 case 1 : Main.BillCustomer(scanner);
                     break;
                 case 3 : Main.itemManagement(scanner);
                     break;
                 case 4 : Main.categoryManagement(scanner);
                     break;

                 case 9 : Main.stockManagement(scanner);
                     break;
                 case 10 : {
                     System.out.println("Thank you for using the POS system. Goodbye!");
                     return;
                 }
                 default : System.out.println("Invalid choice. Please try again.");
             }
         }

     }


    public static void BillCustomer(Scanner scanner){

        String transaction_type = "POS";
        Bill bill = new Bill();

        while(true) {
            System.out.println("Enter item code: ");
            String code = scanner.next();

            if (code.equalsIgnoreCase("done")) break;
            System.out.println("Enter Qyt: ");
            int qyt = scanner.nextInt();

            Items item = Items.getItemByCode(code);
            if (item != null) {
                bill.addItem(item, qyt);
                System.out.println("Item added to the bill.");
            } else {
                System.out.println("Item not found.");
            }
        }

        // printing order summery
        bill.OrderSummary();

        System.out.println("Cash Tendered: ");
        double cash_tendered = scanner.nextDouble();
        double cash_returned = bill.change_calculation(cash_tendered);
        System.out.println(cash_returned);
        bill.checkout(transaction_type);
        bill.printBill(employeeName);


    }

    public static void itemManagement(Scanner scanner){

        System.out.println("1. Add new item");
        System.out.println("2. Update existing item");
        System.out.println("3. Remove item");

        int crud_choice = scanner.nextInt();
        scanner.nextLine();

        switch (crud_choice){
            case 1: Main.addNewItem(scanner);
                    break;
            case 2: Main.updateItem(scanner);
                    break;
            case 3: Main.removeItem(scanner);
                    break;
        }
    }

    public static void categoryManagement(Scanner scanner){

        System.out.println("1. Add new category");
        System.out.println("2. Update existing category");
        System.out.println("3. Remove category");

        int crud_choice = scanner.nextInt();
        scanner.nextLine();

        switch (crud_choice){
            case 1: Main.addNewCategory(scanner);
                break;
            case 2: Main.updateCategory(scanner);
                break;
            case 3: Main.removeCategory(scanner);
                break;
        }
    }

    public static void stockManagement(Scanner scanner){

        System.out.println("1. Add new stock");
        System.out.println("2. Update existing stock");
        System.out.println("3. Remove stock");

        int crud_choice = scanner.nextInt();
        scanner.nextLine();

        switch (crud_choice){
            case 1: Main.addNewStock(scanner);
                break;
            case 2: Main.updatestock(scanner);
                break;
            case 3: Main.removestock(scanner);
                break;
        }
    }

    //ITEM CRUD
    public static void removeItem(Scanner scanner) {
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
    }

    // needs changes, direct chat copy
    public static void updateItem(Scanner scanner){

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

    public static void addNewItem(Scanner scanner){

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


        int categoryId = getCategoryID(categoryName);

        if (categoryId == -1) {
            System.out.println("Category not found. Please add the category first.");
            return;
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

    private static int getCategoryID(String categoryName) {
        try {
            String query = "SELECT Category_id FROM item_category WHERE name = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, categoryName);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("Category_id");
            }
        } catch (SQLException e) {
            System.err.println("Error while fetching category ID: " + e.getMessage());
        }
        return -1; // Return -1 if category not found
    }

    // CATEGORY CRUD
    public static void addNewCategory(Scanner scanner){

        System.out.println("Enter category name:");
        String category_name = scanner.nextLine();

        System.out.println("Enter description: ");
        String category_decs = scanner.nextLine();


        try {
            String addCategoryQuary = "INSERT INTO item_category (Name, Description) VALUES (?, ?)";
            PreparedStatement stmt = connection.prepareStatement(addCategoryQuary);

            stmt.setString(1, category_name);
            stmt.setString(2, category_decs);

            int rowsInserted = stmt.executeUpdate();

            if (rowsInserted > 0) {
                System.out.println("Item added successfully!");
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }


    }

    public static void removeCategory(Scanner scanner) {
        try {
            System.out.println("Enter the Category name ");
            String category_name = scanner.nextLine();

            int category_id = getCategoryID(category_name);


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

    public static void updateCategory(Scanner scanner){

        System.out.println("Enter item category name: ");
        String category_name = scanner.nextLine();

        int category_id = getCategoryID(category_name);

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

    // STOCK CRUD
    public static void addNewStock(Scanner scanner){

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




        int item_id = getItemId(itemcode);

        if (item_id == -1) {
            System.out.println("Item not found. Please add the Item first.");
            return;
        }

        try {
            String addstockQuary = "INSERT INTO stock (Batch_number, item_id, qty, qty_type, batch_price, date_of_purchase, expiry_date) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = connection.prepareStatement(addstockQuary);

            stmt.setString(1, batchNumber);
            stmt.setInt(2, item_id);
            stmt.setInt(3, quantity);
            stmt.setString(4, quantity_type);
            stmt.setInt(5, batch_price);
            stmt.setString(6, date_of_purchase);
            stmt.setString(7, expirationDate);

            int rowsInserted = stmt.executeUpdate();

            if (rowsInserted > 0) {
                System.out.println("Item added successfully!");
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static void removestock(Scanner scanner) {
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

    public static void updatestock(Scanner scanner){

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

    private static int getItemId(String code) {
        try {
            String query = "SELECT Item_id FROM item WHERE Code = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, code);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("Item_id");
            }
        } catch (SQLException e) {
            System.err.println("Error while fetching item ID: " + e.getMessage());
        }
        return -1; // Return -1 if category not found
    }
}