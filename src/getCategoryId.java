import JDBC.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;



public class getCategoryId {

    static Connection connection = Database.connect();

     public static int execute(String categoryName) {
        try {
            String query = "SELECT Category_id FROM item_category WHERE name = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, categoryName);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("Category_id");
            }
        } catch (SQLException e) {
            System.err.println("Error while fetching category ID: " + e.getMessage());
        }
        return -1; // Return -1 if category not found
    }
}
