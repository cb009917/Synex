import JDBC.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class getEmployeeName {

    static Connection connection = Database.connect();
    public static String execute(int employeeId) {
        String employeeName = null;

        String query = "SELECT name FROM employees WHERE employee_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, employeeId);

            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                employeeName = resultSet.getString("name"); // Retrieve employee's name
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return employeeName; // Returns null if no match is found
    }
}
