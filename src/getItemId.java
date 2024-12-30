import JDBC.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class getItemId {

    static Connection connection = Database.connect();

    public static int execute(String code) {
        try {
            String query = "SELECT Item_id FROM item WHERE Code = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, code);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("Item_id");
            }
        } catch (SQLException e) {
            System.err.println("Error while fetching item ID: " + e.getMessage());
        }
        return -1; // Return -1 if category not found
    }
}
