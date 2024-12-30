import JDBC.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReorderReport {

    static Connection connection = Database.connect();

    public static void execute() {
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
}
