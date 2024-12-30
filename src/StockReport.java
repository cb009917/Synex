import JDBC.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StockReport {

    static Connection connection = Database.connect();

    public static void execute() {
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
}
