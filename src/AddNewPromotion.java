import JDBC.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

public class AddNewPromotion {

    static Connection connection = Database.connect();

    public static void execute(Scanner scanner){

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
}
