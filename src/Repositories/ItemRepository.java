//package Repositories;
//
//import JDBC.Database;
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//
//public class ItemRepository {
//
//    public static Items getItemByCode(String code) {
//        Items item = null;
//
//        Connection connection = Database.connect();
//        try {
//            String sql = "SELECT * FROM items WHERE code = ?";
//            PreparedStatement statement = connection.prepareStatement(query);
//            statement.setString(1, code);
//            ResultSet resultSet = statement.executeQuery();
//            if (resultSet.next()) {
//                item = new Items(
//                        resultSet.getString("code"),
//                        resultSet.getString("name"),
//                        resultSet.getDouble("price"),
//                        resultSet.getString("manufacturer"),
//                        resultSet.getInt("stockLevel"),
//                        resultSet.getString("categoryId"),
//                        resultSet.getInt("shelfId")
//                );
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        } finally {
//            Database.disconnect();
//        }
//        return item;
//    }
//}
