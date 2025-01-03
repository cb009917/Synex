import JDBC.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class PromotionManagement {

    static Scanner scanner = new Scanner(System.in);
    static Connection connection = Database.connect();

    public static void execute(Scanner scanner){

        System.out.println("1. Add new promotion");
        System.out.println("2. Update existing promotion");
        System.out.println("3. Remove promotion");
        System.out.println("4. View all promotions");
        System.out.println("5. Exit");
        int crud_choice = scanner.nextInt();
        scanner.nextLine();

        switch (crud_choice){
            case 1: addNewPromotion();
                break;
//            case 2: Main.updatePromotion(scanner);
//                break;
            case 3: removeItem();
                break;
            case 4: readPromotion();
                break;
            case 5:
                return;
        }
    }

    public static void addNewPromotion(){

        System.out.print("Enter Promotion Item: ");
        String itemcode = scanner.nextLine();

        System.out.print("Enter Discount Percentage: ");
        int discountPercentage = scanner.nextInt();
        scanner.nextLine();

        System.out.println("Promotion start date (YYYY-MM-DD): ");
        String promotionStartDate = scanner.nextLine();

        System.out.print("Promotion end date (YYYY-MM-DD): ");
        String promotionEndDate = scanner.next();

        getItemId itemId = new getItemId();
        int item_id = itemId.execute(itemcode);

        if (item_id == -1) {
            System.out.println("Item not found. Please add the Item first.");
            return;
        }

        try {
            String addPromoQuary = "INSERT INTO discount (item_id,discount_percentage, start_date, end_date) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = connection.prepareStatement(addPromoQuary);

            stmt.setInt(1, item_id);
            stmt.setInt(2, discountPercentage);
            stmt.setString(3, promotionStartDate);
            stmt.setString(4, promotionEndDate);



            int rowsInserted = stmt.executeUpdate();

            if (rowsInserted > 0) {
                System.out.println("Promotion added successfully!");
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static void removeItem() {

        try {
            System.out.println("Enter the Promotion ID to remove: ");
            int promoId = scanner.nextInt();

            // Check if item exists
            String checkQuery = "SELECT * FROM discount WHERE discount_id = ?";
            PreparedStatement checkStmt = connection.prepareStatement(checkQuery);
            checkStmt.setInt(1, promoId);
            ResultSet resultSet = checkStmt.executeQuery();

            if (!resultSet.next()) {
                System.out.println("Promotion not found.");
                return;
            }

            // Confirm deletion
            System.out.println("Are you sure you want to delete this promotion? (yes/no): ");
            String confirmation = scanner.nextLine();
            if (!confirmation.equalsIgnoreCase("yes")) {
                System.out.println("Deletion cancelled.");
                return;
            }

            // Delete item
            String deleteQuery = "DELETE FROM discount WHERE discount_id = ?";
            PreparedStatement deleteStmt = connection.prepareStatement(deleteQuery);
            deleteStmt.setInt(1, promoId);

            int rowsDeleted = deleteStmt.executeUpdate();

            if (rowsDeleted > 0) {
                System.out.println("Promotion removed successfully!");
            } else {
                System.out.println("Failed to remove the promotion.");
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }

    public static void readPromotion() {

        String viewStockSql = "SELECT * FROM discount";

        try (PreparedStatement stmt = connection.prepareStatement(viewStockSql);
             ResultSet resultSet = stmt.executeQuery()) {

            System.out.println("-----------------------------------------------------------");
            System.out.printf("%-15s %-10s %-25s %-15s %-15s\n", "Discount ID", "Item ID", "Discount Percentage", "Start Date", "End Date");
            System.out.println("-----------------------------------------------------------");

            while (resultSet.next()) {
                int discount_id = resultSet.getInt("discount_id");
                int item_id = resultSet.getInt("item_id");
                int discount_percentage = resultSet.getInt("discount_percentage");
                String startDate = resultSet.getString("start_date");
                String endDate = resultSet.getString("end_date");

                System.out.printf("%-15d %-10d %-25d %-15s %-15s\n", discount_id, item_id, discount_percentage, startDate, endDate);
            }

        } catch (SQLException e) {
            System.out.println("Error reading Promotion data: " + e.getMessage());
        }

    }

}
