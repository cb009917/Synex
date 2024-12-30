import JDBC.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

public class AddCategory {

    static Scanner scanner = new Scanner(System.in);
    static Connection connection = Database.connect();

    public static void execute(){
        System.out.println("Enter category name:");
        String category_name = scanner.nextLine();

        System.out.println("Enter description: ");
        String category_decs = scanner.nextLine();


        try {
            String addCategoryQuary = "INSERT INTO item_category (Name, Description) VALUES (?, ?)";
            PreparedStatement stmt = connection.prepareStatement(addCategoryQuary);

            stmt.setString(1, category_name);
            stmt.setString(2, category_decs);

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
