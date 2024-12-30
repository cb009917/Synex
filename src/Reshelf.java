import JDBC.Database;

import java.sql.*;
import java.util.Date;

public class Reshelf {

    static Connection connection = Database.connect();
    public static void execute() {
        String fetchShelvesQuery = """
        SELECT shelf_id, item_id, max_qty - current_qty AS qty_to_reshelve
        FROM shelf_items
        WHERE current_qty < max_qty
    """;

        String fetchStockBatchQuery = """
        SELECT Batch_number, qty, expiry_date
        FROM Stock
        WHERE item_id = ? AND qty > 0
        ORDER BY expiry_date ASC, Batch_number ASC
        LIMIT 1
    """;

        String updateShelfQuery = """
        UPDATE shelf_items 
        SET current_qty = current_qty + ? 
        WHERE shelf_id = ?
    """;

        String updateStockQuery = """
        UPDATE Stock 
        SET qty = qty - ? 
        WHERE Batch_number = ?
    """;

        String updateItemQuery = """
        UPDATE item
        SET Stock_level = Stock_level - ? 
        WHERE item_id = ?
    """;

        try (Statement shelfStmt = connection.createStatement();
             ResultSet shelves = shelfStmt.executeQuery(fetchShelvesQuery)) {

            connection.setAutoCommit(false); // Begin transaction

            while (shelves.next()) {
                int shelfId = shelves.getInt("shelf_id");
                String itemCode = shelves.getString("item_id");
                int qtyToReshelve = shelves.getInt("qty_to_reshelve");

                if (qtyToReshelve <= 0) {
                    continue; // Skip shelves that do not need reshelving
                }

                try (PreparedStatement stockStmt = connection.prepareStatement(fetchStockBatchQuery)) {
                    stockStmt.setString(1, itemCode);

                    try (ResultSet stockBatch = stockStmt.executeQuery()) {
                        if (stockBatch.next()) {
                            String batchId = stockBatch.getString("Batch_number");
                            int availableQuantity = stockBatch.getInt("qty");
                            Date expiryDate = stockBatch.getDate("expiry_date");

                            int qtyReshelved = Math.min(qtyToReshelve, availableQuantity);

                            // Update the shelf
                            try (PreparedStatement updateShelfStmt = connection.prepareStatement(updateShelfQuery)) {
                                updateShelfStmt.setInt(1, qtyReshelved);
                                updateShelfStmt.setInt(2, shelfId);
                                updateShelfStmt.executeUpdate();
                            }


                            // Update the stock
                            try (PreparedStatement updateStockStmt = connection.prepareStatement(updateStockQuery)) {
                                updateStockStmt.setInt(1, qtyReshelved);
                                updateStockStmt.setString(2, batchId);
                                updateStockStmt.executeUpdate();
                            }

                            try (PreparedStatement updateItemStmt = connection.prepareStatement(updateItemQuery)) {
                                updateItemStmt.setInt(1, qtyReshelved);
                                updateItemStmt.setString(2, itemCode);
                                updateItemStmt.executeUpdate();
                            }

                            System.out.printf("Reshelved %d units of item %s from batch %s (Expiry: %s) to shelf %d.%n",
                                    qtyReshelved, itemCode, batchId, expiryDate != null ? expiryDate.toString() : "N/A", shelfId);
                        } else {
                            System.out.printf("No stock available to reshelve item %s on shelf %d.%n", itemCode, shelfId);
                        }
                    }
                }
            }

            connection.commit(); // Commit transaction
        } catch (SQLException e) {
            System.err.println("Error during reshelving: " + e.getMessage());
            try {
                connection.rollback(); // Rollback on error
            } catch (SQLException rollbackEx) {
                System.err.println("Rollback failed: " + rollbackEx.getMessage());
            }
        } finally {
            try {
                connection.setAutoCommit(true); // Reset auto-commit
            } catch (SQLException e) {
                System.err.println("Failed to reset auto-commit: " + e.getMessage());
            }
        }
    }
}
