import JDBC.Database;

import java.sql.*;
import java.util.Date;
import java.util.Scanner;

public class Main {

    // can we define it here
    static Connection connection = Database.connect();
    static String employeeName;
    static int loyaltyNumber;

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
             System.out.println("10. Reshelve");
             System.out.println("11. Reorder Report");
             System.out.println("12. Bill Report");
             System.out.println("13. Exit");
             System.out.print("Enter your choice: ");
             int choice = scanner.nextInt();
             scanner.nextLine();

             switch (choice) {
                 case 1 : Main.loyaltySystem(scanner);
                     break;
                 case 3 : Main.itemManagement(scanner);
                     break;
                 case 4 : Main.categoryManagement(scanner);
                     break;

                 case 5 : Main.DailySalesReport(scanner);
                          break;

                 case 6 : Main.StockReport();
                          break;

                 case 7 : Main.StockReport();
                 break;

                 case 8 : Main.ReshelvingReport();
                 break;

                 case 9 : Main.stockManagement(scanner);
                     break;

                 case 10 : Main.reshelve();
                 break;

                 case 11 : Main.ReorderReport();
                 break;

                 case 12 : Main.BillReport();
                 break;
                 case 13 : {
                     System.out.println("Thank you for using the POS system. Goodbye!");
                     return;
                 }
                 default : System.out.println("Invalid choice. Please try again.");
             }
         }

     }

     // Loyalty System
     public static void loyaltySystem(Scanner scanner){
         System.out.println("Enter your Loyalty Number (or press Enter to skip): ");
         String loyalty_number = scanner.nextLine();

         if (loyalty_number.isEmpty()) {
            Main.BillCustomer(scanner);
         }


         try {
             loyaltyNumber = Integer.parseInt(loyalty_number);

             // Query to check if the loyalty number exists
             String query = "SELECT l.loyalty_id, c.name, l.total_points " +
                     "FROM loyalty l " +
                     "JOIN customer c ON l.Customer_id = c.id " +
                     "WHERE l.loyalty_id = ?";

             try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                 preparedStatement.setInt(1, loyaltyNumber);
                 ResultSet resultSet = preparedStatement.executeQuery();

                 if (resultSet.next()) {
                     // Fetching loyalty details
                     int loyaltyId = resultSet.getInt("loyalty_id");
                     String customerName = resultSet.getString("name");
                     int totalPoints = resultSet.getInt("total_points");


                     System.out.println("Welcome " + customerName);


                     // Proceed with the next operation (e.g., main menu or loyalty actions)
                     BillCustomer(scanner);

                 }

                 else {
                     System.out.println("Invalid Loyalty Number. Please try again.");
                     loyaltySystem(scanner); // Retry
                 }
             }
         } catch (NumberFormatException e) {
             System.out.println("Invalid input. Please enter a valid loyalty number or press Enter to skip.");
             loyaltySystem(scanner); // Retry
         } catch (SQLException e) {
             System.err.println("Error verifying loyalty number: " + e.getMessage());
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
        bill.checkout(transaction_type, loyaltyNumber);
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


    // Report generation

    public static void DailySalesReport(Scanner scanner){
        System.out.println("Enter the date for the daily sales report (YYYY-MM-DD): ");
        String date = scanner.nextLine();

        String query = """
               
                  SELECT i.code, i.name, SUM(bi.quantity) as total_quantity,\s
                   SUM(bi.price) as total_revenue
                   FROM bill b
                   JOIN bill_item bi ON b.Serial_number = bi.bill_id
                   JOIN item i ON bi.item_id = i.item_id
                   WHERE DATE(b.Date) = ?
                   GROUP BY i.item_id, i.code, i.name
                  
                """;

        String totalRevenueQuery = """
               SELECT 
                    SUM(Total) AS total_revenue
               FROM 
                    bill 
                WHERE 
                    Date = ?;
                """;

        try (PreparedStatement statement = connection.prepareStatement(query);
             PreparedStatement totalStatement = connection.prepareStatement(totalRevenueQuery)) {

            // Set the date parameter for both queries
            statement.setString(1, date);
            totalStatement.setString(1, date);

            // Execute the main query for items
            ResultSet resultSet = statement.executeQuery();

            System.out.println("Sales Report for Date: " + date);
            System.out.println("------------------------------------------------------------");
            System.out.printf("%-15s %-25s %-10s %-15s\n", "Item Code", "Item Name", "Quantity", "Revenue");
            System.out.println("------------------------------------------------------------");

            while (resultSet.next()) {
                String itemCode = resultSet.getString("Code");
                String itemName = resultSet.getString("name");
                int totalQuantity = resultSet.getInt("total_quantity");
                double totalRevenue = resultSet.getDouble("total_revenue");

                System.out.printf("%-15s %-25s %-10d %-15.2f\n", itemCode, itemName, totalQuantity, totalRevenue);
            }

            // Execute the total revenue query
            ResultSet totalResultSet = totalStatement.executeQuery();
            if (totalResultSet.next()) {
                double totalRevenue = totalResultSet.getDouble("total_revenue");
                System.out.println("------------------------------------------------------------");
                System.out.println("Total Revenue for Date " + date + ": " + totalRevenue);
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }


    }

    public static void ReorderReport() {
        String query = """
                SELECT 
                    i.code AS item_code,
                    i.name AS item_name,
                    s.qty AS current_stock
                FROM 
                    stock s
                JOIN 
                    item i ON s.item_id = i.item_id
                WHERE 
                    s.qty < 50;
                """;

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();

            System.out.println("Reorder Level Report");
            System.out.println("------------------------------------------------");
            System.out.printf("%-15s %-25s %-10s\n", "Item Code", "Item Name", "Current Stock");
            System.out.println("------------------------------------------------");

            while (resultSet.next()) {
                String itemCode = resultSet.getString("item_code");
                String itemName = resultSet.getString("item_name");
                int currentStock = resultSet.getInt("current_stock");

                System.out.printf("%-15s %-25s %-10d\n", itemCode, itemName, currentStock);
            }

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }

    public static void StockReport() {
        String query = """
                SELECT 
                    s.Batch_number AS batch_number,
                    i.code AS item_code,
                    i.name AS item_name,
                    s.qty AS quantity,
                    s.qty_type AS quantity_type,
                    s.batch_price AS batch_price,
                    s.date_of_purchase AS purchase_date,
                    s.expiry_date AS expiry_date
                FROM 
                    stock s
                JOIN 
                    item i ON s.item_id = i.item_id;
                """;

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();

            System.out.println("Stock Report (Batch-wise)");
            System.out.println("------------------------------------------------------------------------------------------");
            System.out.printf("%-15s %-15s %-25s %-10s %-10s %-15s %-15s %-15s\n",
                    "Batch No", "Item Code", "Item Name", "Qty", "Qty Type", "Batch Price", "Purchase Date", "Expiry Date");
            System.out.println("------------------------------------------------------------------------------------------");

            while (resultSet.next()) {
                String batchNumber = resultSet.getString("batch_number");
                String itemCode = resultSet.getString("item_code");
                String itemName = resultSet.getString("item_name");
                int quantity = resultSet.getInt("quantity");
                String quantityType = resultSet.getString("quantity_type");
                double batchPrice = resultSet.getDouble("batch_price");
                String purchaseDate = resultSet.getString("purchase_date");
                String expiryDate = resultSet.getString("expiry_date");

                System.out.printf("%-15s %-15s %-25s %-10d %-10s %-15.2f %-15s %-15s\n",
                        batchNumber, itemCode, itemName, quantity, quantityType, batchPrice, purchaseDate, expiryDate);
            }

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }

    public static void BillReport() {
        String query = """
                SELECT 
                    b.Serial_number AS bill_number,
                    b.Date AS bill_date,
                    bi.item_id AS item_code,
                    i.name AS item_name,
                    bi.quantity AS quantity,
                    bi.price AS total_price,
                    b.Total AS total_bill_amount
                FROM 
                    bill b
                JOIN 
                    bill_item bi ON b.Serial_number = bi.bill_id
                JOIN 
                    item i ON bi.item_id = i.item_id
                ORDER BY 
                    b.Date DESC, b.Serial_number ASC;
                """;

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();

            System.out.println("Bill Report (Customer Transactions)");
            System.out.println("---------------------------------------------------------------------------------------------");
            System.out.printf("%-10s %-15s %-10s %-20s %-10s %-15s %-15s\n",
                    "Bill No", "Bill Date", "Item Code", "Item Name", "Qty", "Total (Item)", "Total (Bill)");
            System.out.println("---------------------------------------------------------------------------------------------");

            while (resultSet.next()) {
                int billNumber = resultSet.getInt("bill_number");
                String billDate = resultSet.getString("bill_date");
                String itemCode = resultSet.getString("item_code");
                String itemName = resultSet.getString("item_name");
                int quantity = resultSet.getInt("quantity");
                double totalPrice = resultSet.getDouble("total_price");
                double totalBillAmount = resultSet.getDouble("total_bill_amount");

                System.out.printf("%-10d %-15s %-10s %-20s %-10d %-15.2f %-15.2f\n",
                        billNumber, billDate, itemCode, itemName, quantity, totalPrice, totalBillAmount);
            }

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }

    public static void ReshelvingReport() {
        String query = """
        SELECT 
            i.Code AS ItemCode,
            i.Name AS ItemName,
            si.shelf_id AS ShelfID,
            (si.max_qty - si.current_qty) AS QuantityToReshelve
        FROM 
            shelf_items si
        JOIN 
            item i ON si.item_id = i.item_id
        WHERE 
            (si.max_qty - si.current_qty) > 0
        ORDER BY 
            si.shelf_id, i.Name;
    """;

        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            System.out.println("Reshelving Report:");
            System.out.println("----------------------------------------------------------------------");
            System.out.println("| Item Code | Item Name                 | Shelf ID | Reshelve Qty  |");
            System.out.println("----------------------------------------------------------------------");

            while (resultSet.next()) {
                String itemCode = resultSet.getString("ItemCode");
                String itemName = resultSet.getString("ItemName");
                int shelfId = resultSet.getInt("ShelfID");
                int toReshelve = resultSet.getInt("QuantityToReshelve");

                System.out.printf("| %-9s | %-25s | %-8d | %-10d |%n", itemCode, itemName, shelfId, toReshelve);
            }

            System.out.println("------------------------------------------------------------");

        } catch (SQLException e) {
            System.err.println("Error generating reshelving report: " + e.getMessage());
        }
    }


    public static void reshelve() {
        String fetchShelvesQuery = """
        SELECT shelf_id, item_id, max_qty - current_qty AS qty_to_reshelve
        FROM shelf_items
        WHERE current_qty < max_qty
    """;

        String fetchStockBatchQuery = """
        SELECT Batch_number, qty, expiry_date
        FROM Stock
        WHERE item_id = ? AND qty > 0
        ORDER BY expiry_date ASC, Batch_number ASC
        LIMIT 1
    """;

        String updateShelfQuery = """
        UPDATE shelf_items 
        SET current_qty = current_qty + ? 
        WHERE shelf_id = ?
    """;

        String updateStockQuery = """
        UPDATE Stock 
        SET qty = qty - ? 
        WHERE Batch_number = ?
    """;

        String updateItemQuery = """
        UPDATE item
        SET Stock_level = Stock_level - ? 
        WHERE item_id = ?
    """;

        try (Statement shelfStmt = connection.createStatement();
             ResultSet shelves = shelfStmt.executeQuery(fetchShelvesQuery)) {

            connection.setAutoCommit(false); // Begin transaction

            while (shelves.next()) {
                int shelfId = shelves.getInt("shelf_id");
                String itemCode = shelves.getString("item_id");
                int qtyToReshelve = shelves.getInt("qty_to_reshelve");

                if (qtyToReshelve <= 0) {
                    continue; // Skip shelves that do not need reshelving
                }

                try (PreparedStatement stockStmt = connection.prepareStatement(fetchStockBatchQuery)) {
                    stockStmt.setString(1, itemCode);

                    try (ResultSet stockBatch = stockStmt.executeQuery()) {
                        if (stockBatch.next()) {
                            String batchId = stockBatch.getString("Batch_number");
                            int availableQuantity = stockBatch.getInt("qty");
                            Date expiryDate = stockBatch.getDate("expiry_date");

                            int qtyReshelved = Math.min(qtyToReshelve, availableQuantity);

                            // Update the shelf
                            try (PreparedStatement updateShelfStmt = connection.prepareStatement(updateShelfQuery)) {
                                updateShelfStmt.setInt(1, qtyReshelved);
                                updateShelfStmt.setInt(2, shelfId);
                                updateShelfStmt.executeUpdate();
                            }


                            // Update the stock
                            try (PreparedStatement updateStockStmt = connection.prepareStatement(updateStockQuery)) {
                                updateStockStmt.setInt(1, qtyReshelved);
                                updateStockStmt.setString(2, batchId);
                                updateStockStmt.executeUpdate();
                            }

                            try (PreparedStatement updateItemStmt = connection.prepareStatement(updateItemQuery)) {
                                updateItemStmt.setInt(1, qtyReshelved);
                                updateItemStmt.setString(2, itemCode);
                                updateItemStmt.executeUpdate();
                            }

                            System.out.printf("Reshelved %d units of item %s from batch %s (Expiry: %s) to shelf %d.%n",
                                    qtyReshelved, itemCode, batchId, expiryDate != null ? expiryDate.toString() : "N/A", shelfId);
                        } else {
                            System.out.printf("No stock available to reshelve item %s on shelf %d.%n", itemCode, shelfId);
                        }
                    }
                }
            }

            connection.commit(); // Commit transaction
        } catch (SQLException e) {
            System.err.println("Error during reshelving: " + e.getMessage());
            try {
                connection.rollback(); // Rollback on error
            } catch (SQLException rollbackEx) {
                System.err.println("Rollback failed: " + rollbackEx.getMessage());
            }
        } finally {
            try {
                connection.setAutoCommit(true); // Reset auto-commit
            } catch (SQLException e) {
                System.err.println("Failed to reset auto-commit: " + e.getMessage());
            }
        }
    }

}