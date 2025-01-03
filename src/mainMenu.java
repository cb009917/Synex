import java.util.Scanner;

public class mainMenu {




    public static void execute(Scanner scanner){
        loyaltySystem loyaltySystem = new loyaltySystem();
        DailySalesReport dailySalesReport = new DailySalesReport();
        StockReport stockReport = new StockReport();
        ReshelvingReport reshelveReport = new ReshelvingReport();
        ReorderReport reorderReport = new ReorderReport();
        BillReport billReport = new BillReport();
        PromotionManagement promotionManagement = new PromotionManagement();
        StockManagement stockManagement = new StockManagement();
        ItemManagment itemManagment = new ItemManagment();
        Reshelf reshelf = new Reshelf();



        while (true) {
            System.out.println("\n1. Bill Customer");
            System.out.println("2. View Bills");
            System.out.println("3. Manege Products");
            System.out.println("4. Category management");
            System.out.println("5. Daily sales report");
            System.out.println("6. Stock Report");
            System.out.println("7. Restock Report");
            System.out.println("8. Reshelve Report");
            System.out.println("9. Stock Management");
            System.out.println("10. Reshelve");
            System.out.println("11. Reorder Report");
            System.out.println("12. Bill Report");
            System.out.println("13. Promotion Management");
            System.out.println("14. Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 :
                    loyaltySystem.execute(scanner);
                    break;
                case 3 : itemManagment.execute(scanner);
                    break;
                case 4 : categoryManagment.execute(scanner);
                    break;

                case 5 : dailySalesReport.execute(scanner);
                    break;

                case 6 : stockReport.execute();
                    break;

//                case 7 : restockReport.execute();
//                    break;

                case 8 : reshelveReport.execute();
                    break;

                case 9 : stockManagement.execute(scanner);
                    break;

                case 10 : reshelf.execute();
                    break;

                case 11 : reorderReport.execute();
                    break;

                case 12 : billReport.execute();
                    break;

                case 13 : promotionManagement.execute(scanner);
                    break;

                case 14 : {
                    System.out.println("Thank you for using the POS system. Goodbye!");
                    return;
                }
                default : System.out.println("Invalid choice. Please try again.");
            }
        }

    }
}
