import JDBC.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BillReport {

    static Connection connection = Database.connect();
    public static void execute(){
        String query = """
                SELECT *
                FROM bill
                
                """;

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();

            System.out.println("Bill Report (Customer Transactions)");
            System.out.println("------------------------------------");
            System.out.printf("%-10s %-15s %-10s\n",
                    "Bill No", "Bill Date", "Total(Bill)");
            System.out.println("------------------------------------");

            while (resultSet.next()) {
                int billNumber = resultSet.getInt("Serial_number");
                String billDate = resultSet.getString("Date");
                double billTotal = resultSet.getDouble("Total");


                System.out.printf("%-10d %-15s %-15.2f\n",
                        billNumber, billDate, billTotal);
            }

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }
}
