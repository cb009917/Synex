import JDBC.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReshelvingReport {

    static Connection connection = Database.connect();
    public static void execute() {
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
}
