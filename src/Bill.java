import JDBC.Database;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Bill {

    private int SerialNumber = 0;
    private List<BillItem> items = new ArrayList<>();
    private double totalPrice;
    private double CashTendered;
    private double ChangeReturned;
    private String transactionType;
    private  String BillDate;
    Connection connection = Database.connect();

    public double getCashTendered() {
        return CashTendered;
    }

    public void setCashTendered(double cashTendered) {
        CashTendered = cashTendered;
    }

    public double getChangeReturned() {
        return ChangeReturned;
    }

    public void setChangeReturned(double changeReturned) {
        ChangeReturned = changeReturned;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getBillDate() {
        return BillDate;
    }

    public void setBillDate(String billDate) {
        BillDate = billDate;
    }

    public int getSerialNumber() {
        return SerialNumber;
    }

    public void setSerialNumber(int serialNumber) {
        SerialNumber = serialNumber;
    }

    public List<BillItem> getItems() {
        return items;
    }

    public void setItems(List<BillItem> items) {
        this.items = items;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void addItem(Items item, int quantity) {
        double item_total = Calculate_item_total(item, quantity);
        items.add(new BillItem(item, quantity, item_total));
    }

    public double Calculate_item_total(Items item, int qyt){
           return item.getPrice() * qyt;
    }

    public void OrderSummary() {
        if (items.isEmpty()) {
            System.out.println("No items in the order.");
            return;
        }

        double BillTotal = 0;
        System.out.println("Order Summary:");
        System.out.println("----------------------------------");
        for (BillItem billItem : items) {
            String itemName = billItem.item.getName();
            int quantity = billItem.quantity;
            double item_total = billItem.item_price;

            System.out.printf("Item: %s | Quantity: %d | Total: %.2f%n", itemName, quantity, item_total);
            totalPrice += item_total;
        }

        System.out.println("----------------------------------");
        System.out.printf("Total Amount: %.2f%n", totalPrice);
    }

//    public double calculate_bill_total(double totalPrice){
//        this.totalPrice = totalPrice;
//        return totalPrice += totalPrice;
//    }

    public double change_calculation(double cash_given){
        this.CashTendered = cash_given;
        ChangeReturned =  CashTendered - totalPrice;
        return ChangeReturned;
    }

    public String BillDate() {
        // Get the current date and time
        LocalDateTime now = LocalDateTime.now();

        // Format it as a string for SQL (e.g., "2024-12-24 15:30:00")
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.BillDate = now.format(formatter);
        return BillDate;
    }

    // Generating bill id




    public boolean checkout(String transactionType) {

        this.transactionType = transactionType;

        PreparedStatement billStmt = null;
        PreparedStatement billItemStmt = null;
        PreparedStatement updateStockStmt = null;

        try {
         // Start transaction

            // Insert into Bill table
            String billquary = "INSERT INTO Bill (Total, Transaction_type, date, change_returned,Cash_given) VALUES (?, ?,?, ?,?)";
            billStmt = connection.prepareStatement(billquary, PreparedStatement.RETURN_GENERATED_KEYS);


            billStmt.setDouble(1, this.totalPrice);
            billStmt.setString(2, this.transactionType);
            billStmt.setString(3, BillDate());
            billStmt.setDouble(4, this.ChangeReturned);
            billStmt.setDouble(5, this.CashTendered);


            int rowsInserted = billStmt.executeUpdate();
            if (rowsInserted == 0) {
                throw new SQLException("Failed to insert bill.");
            }


            // Get the generated bill_id
            ResultSet generatedKeys = billStmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                this.SerialNumber = generatedKeys.getInt(1);
            } else {
                throw new SQLException("Failed to retrieve bill ID.");
            }

        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        try{
            // Insert into Bill_Item table
            String billItemSql = "INSERT INTO Bill_Item (bill_id, item_id, quantity, price) VALUES (?, ?, ?, ?)";
            billItemStmt = connection.prepareStatement(billItemSql);


            //update stock level
            String updatestockquary = "UPDATE item SET Stock_level = Stock_level - ? WHERE Item_id = ?";
            updateStockStmt = connection.prepareStatement(updatestockquary);


            for (BillItem billItem : items) {
                int itemId = billItem.item.getId();
                int quantity = billItem.quantity;
                double price = billItem.item_price; // Assume this method retrieves the item's price

                billItemStmt.setInt(1, this.getSerialNumber());
                billItemStmt.setInt(2, itemId);
                billItemStmt.setInt(3, quantity);
                billItemStmt.setDouble(4, price);
                billItemStmt.addBatch(); // Add to batch for execution

                updateStockStmt.setInt(1, quantity);
                updateStockStmt.setInt(2, itemId);
                updateStockStmt.addBatch();
            }

            billItemStmt.executeBatch();// Execute all inserts
            updateStockStmt.executeBatch();
            return true;

        }
        catch (SQLException e) {
            e.printStackTrace();

            //Need to do exception handerling

//            try {
//                if (conn != null) conn.rollback(); // Rollback on failure
//            } catch (SQLException ex) {
//                ex.printStackTrace();
//            }
            return false;

        }

//        finally {
//            // Close resources
//            try {
//                if (billStmt != null) billStmt.close();
//                if (billItemStmt != null) billItemStmt.close();
//                if (conn != null) conn.close();
//            } catch (SQLException ex) {
//                ex.printStackTrace();
//            }
//        }
    }

    // Helper method to get item price
//    private double getItemPrice(int itemId, Connection conn) throws SQLException {
//        String sql = "SELECT price FROM Item WHERE item_id = ?";
//        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
//            stmt.setInt(1, itemId);
//            ResultSet rs = stmt.executeQuery();
//            if (rs.next()) {
//                return rs.getDouble("price");
//            } else {
//                throw new SQLException("Item not found with ID: " + itemId);
//            }
//        }
//    }



    public void printBill(String employeeName) {
        System.out.println("=====================================");
        System.out.println("               BILL                  ");
        System.out.println("=====================================");

        // Display the employee who billed the transaction
        System.out.printf("Billed By: %-20s%n", employeeName);

        // Print the items
        for (BillItem billItem : items) {
            System.out.printf("Item: %-15s Quantity: %-5d Price: %.2f Total: %.2f%n",
                    billItem.item.getName(),
                    billItem.quantity,
                    billItem.item.getPrice(),
                    billItem.item_price);
        }

        // Print the total amount
        double totalAmount = totalPrice;
        System.out.printf("\nTotal Amount: %.2f%n", totalAmount);

        // Print the cash tendered and change
        System.out.printf("Cash Tendered: %.2f%n", getCashTendered());
        System.out.printf("Change Given: %.2f%n", getChangeReturned());

        System.out.println("=====================================");
    }


    class BillItem{
        private Items item;
        private int quantity;
        private double item_price;

        public BillItem(Items item, int quantity,double item_price ) {
            this.item = item;
            this.quantity = quantity;
            this.item_price = item_price;
        }

    }


}
