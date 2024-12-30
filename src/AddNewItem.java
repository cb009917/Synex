import JDBC.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class AddNewItem {

    static Connection connection = Database.connect();

    public static void execute(){

        Scanner scanner = new Scanner(System.in);



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
}
