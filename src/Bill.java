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
    double netTotal;
    double discount;
    private double CashTendered;
    private double ChangeReturned;
    private double loyaltyPoints;
    private String transactionType;
    private  String BillDate;
    Connection connection = Database.connect();

    public double getCashTendered() {
        return CashTendered;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getNetTotal() {
        return netTotal;
    }

    public void setNetTotal(double netTotal) {
        this.netTotal = netTotal;
    }

    public double getLoyaltyPoints() {
        return loyaltyPoints;
    }

    public void setLoyaltyPoints(double loyaltyPoints) {
        this.loyaltyPoints = loyaltyPoints;
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
            setTotalPrice(totalPrice += item_total);
        }

        // discount calculation
        discountCalculation();

        System.out.println("----------------------------------");
        System.out.printf("Total Amount: %.2f%n", getNetTotal());


    }

//    public double calculate_bill_total(double totalPrice){
//        this.totalPrice = totalPrice;
//        return totalPrice += totalPrice;
//    }

    public double change_calculation(double cash_given){


        this.CashTendered = cash_given;
        ChangeReturned =  CashTendered - getNetTotal();
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

    public void discountCalculation(){

        PreparedStatement discountStmt = null;
        double totalDiscount = 0.0;

        // discount calculation
        try{
            for (BillItem billItem : items) {
                // Query to get discount for the item
                String discountQuery = "SELECT discount_percentage FROM discount " +
                        "WHERE item_id = ? AND (start_date <= CURDATE() AND end_date >= CURDATE())";
                discountStmt = connection.prepareStatement(discountQuery);
                discountStmt.setInt(1, billItem.item.getId());
                ResultSet discountResult = discountStmt.executeQuery();

                double discountPercentage = 0.0;
                if (discountResult.next()) {
                    discountPercentage = discountResult.getDouble("discount_percentage");
                }

                // Calculate discounted price
                double originalPrice = billItem.item_price;
                double discountAmount = originalPrice * (discountPercentage / 100);
                totalDiscount += discountAmount;
                setDiscount(totalDiscount);

//                billItem.item_price = discountedPrice; // Update the price with the discount
//                netTotal += discountedPrice * billItem.quantity;
            }



            netTotal = getTotalPrice() - totalDiscount;
            setNetTotal(netTotal);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }



    public boolean checkout(String transactionType ,int loyalty_id) {

        this.transactionType = transactionType;

        PreparedStatement billStmt = null;
        PreparedStatement billItemStmt = null;
        PreparedStatement updateStockStmt = null;
        PreparedStatement loyaltyStmt = null;


        try {
         // Start transaction

            // Insert into Bill table
            String billquary = "INSERT INTO Bill (Total, Transaction_type, date, change_returned,Cash_given, Discount, loyalty_points_earned) VALUES (?, ?,?, ?,?,?,?)";
            billStmt = connection.prepareStatement(billquary, PreparedStatement.RETURN_GENERATED_KEYS);


            billStmt.setDouble(1, getNetTotal());
            billStmt.setString(2, this.transactionType);
            billStmt.setString(3, BillDate());
            billStmt.setDouble(4, this.ChangeReturned);
            billStmt.setDouble(5, this.CashTendered);
            billStmt.setDouble(6, getDiscount());
            billStmt.setDouble(7, getLoyaltyPoints());


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
            String updatestockquary = "UPDATE shelf_items SET current_qty = current_qty - ? WHERE item_id = ?";
            updateStockStmt = connection.prepareStatement(updatestockquary);


            //update loyalty points
            String updateloyaltyquary = "UPDATE loyalty SET total_points = total_points + ? WHERE loyalty_id = ?";
            loyaltyStmt = connection.prepareStatement(updateloyaltyquary);

            setLoyaltyPoints(getNetTotal() / 1000);

            loyaltyStmt.setDouble(1, (getLoyaltyPoints()));
            loyaltyStmt.setInt(2, loyalty_id);

            System.out.println(loyalty_id);

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
            loyaltyStmt.executeUpdate();
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

        System.out.println(netTotal);
        System.out.println("==============================================================");
        System.out.println("                            BILL                  ");
        System.out.println("==============================================================");

        // Display the employee who billed the transaction
        System.out.printf("Billed By: %-20s%n", employeeName);
        System.out.println("Date: " + BillDate());

        System.out.println("------------------------------------------------------------");
        System.out.printf("%-20s %-10s %-10s %-15s\n", "Item", "Price", "Quantity", "Total");
        System.out.println("------------------------------------------------------------");

        // Print the items
        for (BillItem billItem : items) {

            System.out.printf("%-20s %-10.2f %-10d %-15.2f\n",
                    billItem.item.getName(),
                    billItem.item.getPrice(),
                    billItem.quantity,
                    billItem.item_price);
        }

        System.out.println("------------------------------------------------------------");
        // Print the total amount
        double totalAmount = totalPrice;
        System.out.printf("\nBill Total: %.2f%n", totalAmount);
        System.out.println("Total Discount: " + getDiscount());
        System.out.println("------------------------------------------------------------");
        // Print the cash tendered and change
        System.out.println("Net Amount: " + getNetTotal());
        System.out.printf("Cash Tendered: %.2f%n", getCashTendered());
        System.out.printf("Change Given: %.2f%n", getChangeReturned());
        System.out.println("Points Earned: " + getLoyaltyPoints());

        System.out.println("=====================================");

        resetOrder();

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

    private void resetOrder() {
        items.clear();
        setTotalPrice(0);
        setDiscount(0);
        setCashTendered(0);
        setChangeReturned(0);
        setLoyaltyPoints(0);
        setNetTotal(0);
    }


}
