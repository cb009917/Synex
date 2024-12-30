import JDBC.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class DailySalesReport {

    static Connection connection = Database.connect();
    public static void execute(Scanner scanner){
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
}
