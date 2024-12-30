import JDBC.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BillReport {

    static Connection connection = Database.connect();
    public static void execute(){
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
}
