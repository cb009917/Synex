import JDBC.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

public class AddNewStock {

    static Scanner scanner = new Scanner(System.in);
    static Connection connection = Database.connect();

    public static void execute(){

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
}
