import JDBC.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class loyaltySystem {

    static int loyaltyNumber;
    static Connection connection = Database.connect();
    static Scanner scanner = new Scanner(System.in);

    public static void execute(Scanner scanner){
        System.out.println("Enter your Loyalty Number (or press Enter to skip): ");
        String loyalty_number = scanner.nextLine();
        BillCustomer billCustomer = new BillCustomer();

        if (loyalty_number.isEmpty()) {
            billCustomer.execute(scanner);
        }


        try {
            loyaltyNumber = Integer.parseInt(loyalty_number);

            // Query to check if the loyalty number exists
            String query = "SELECT l.loyalty_id, c.name, l.total_points " +
                    "FROM loyalty l " +
                    "JOIN customer c ON l.Customer_id = c.id " +
                    "WHERE l.loyalty_id = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, loyaltyNumber);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    // Fetching loyalty details
                    int loyaltyId = resultSet.getInt("loyalty_id");
                    String customerName = resultSet.getString("name");
                    int totalPoints = resultSet.getInt("total_points");


                    System.out.println("Welcome " + customerName);


                    // Proceed with the next operation (e.g., main menu or loyalty actions)
                    BillCustomer billcustomer = new BillCustomer();
                    billcustomer.execute(scanner);

                }

                else {
                    System.out.println("Invalid Loyalty Number. Please try again.");
                    return;
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid loyalty number or press Enter to skip.");
            return; // Retry
        } catch (SQLException e) {
            System.err.println("Error verifying loyalty number: " + e.getMessage());
        }
    }
}
