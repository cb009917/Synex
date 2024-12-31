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
        System.out.println("4. Exit");
        int crud_choice = scanner.nextInt();
        scanner.nextLine();

        switch (crud_choice){
            case 1: addNewPromotion();
                break;
//            case 2: Main.updatePromotion(scanner);
//                break;
            case 3: removeItem();
                break;
            case 4:
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

}
