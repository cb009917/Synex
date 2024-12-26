import JDBC.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Items {
    private int id;
    private String code;
    private String name;
    private double price;
    private String manufacturer;
    private int stockLevel;
    private String categoryId;
    private int ShelfId;

    public Items(int item_id, String code, String name, double price, String manufacturer, int stockLevel, String categoryId, int shelfId) {
        this.code = code;
        this.id = item_id;
        this.name = name;
        this.price = price;
        this.manufacturer = manufacturer;
        this.stockLevel = stockLevel;
        this.categoryId = categoryId;
        ShelfId = shelfId;
    }

    public String getCode() {
        return code;
    }

    public int getId(){
        return id;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public int getStockLevel() {
        return stockLevel;
    }

    public void setStockLevel(int stockLevel) {
        this.stockLevel = stockLevel;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public int getShelfId() {
        return ShelfId;
    }

    public void setShelfId(int shelfId) {
        ShelfId = shelfId;
    }


    public static Items getItemByCode(String code) {
        Items item = null;

        Connection connection = Database.connect();
        try {
            String sql = "SELECT * FROM item WHERE code = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, code);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                item = new Items(
                        resultSet.getInt("Item_id"),
                        resultSet.getString("code"),
                        resultSet.getString("name"),
                        resultSet.getDouble("price"),
                        resultSet.getString("manufacturer"),
                        resultSet.getInt("Stock_level"),
                        resultSet.getString("Category_id"),
                        resultSet.getInt("Shelf_id")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
//        finally {
//            Database.disconnect();
//        }
        return item;
    }
}
