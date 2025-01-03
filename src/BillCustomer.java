import java.util.Scanner;

public class BillCustomer {

    static String transaction_type = "POS";
    static Bill bill = new Bill();
    static Scanner scanner = new Scanner(System.in);

    public static void execute(){



        while(true) {
            System.out.println("Enter item code: ");
            String code = scanner.next();

            if (code.equalsIgnoreCase("done")) break;
            System.out.println("Enter Qyt: ");
            int qyt = scanner.nextInt();

            Items item = Items.getItemByCode(code);
            if (item != null) {
                bill.addItem(item, qyt);
                System.out.println("Item added to the bill.");
            } else {
                System.out.println("Item not found.");
            }
        }

        // printing order summery
        bill.OrderSummary();

        // payment method
        paymentMethod();



        // need to change these
        bill.checkout(transaction_type, loyaltySystem.loyaltyNumber);
        bill.printBill(Main.employeeName);
    }

    public static void paymentMethod(){
        System.out.println("1. Cash");
        System.out.println("2. Loyalty points");
        System.out.println("Enter payment method: ");
        int payment_method = scanner.nextInt();

        switch (payment_method){
            case 1: cash(); break;
            case 2 : loyalty();
        }

    }

    public static void cash(){
        System.out.printf("Total Amount: %.2f%n", bill.getNetTotal());
        System.out.println("Cash Tendered: ");
        double cash_tendered = scanner.nextDouble();
        double cash_returned = bill.change_calculation(cash_tendered);
        System.out.println(cash_returned);
    }

    public static void loyalty(){
        System.out.printf("Total Amount: %.2f%n", bill.getNetTotal());
        System.out.println("Available Loyalty Points: " + bill.getLoyaltyPoints());
        System.out.println("Enter points to redeem (or 0 to exit): ");
        int points = scanner.nextInt();

        if(points == 0){
            paymentMethod();

        }
        else if (points > bill.getLoyaltyPoints() || bill.getNetTotal() > points ) {
            System.out.println("Insufficient points.");
            loyalty();
        }
        else {

            System.out.println("Loyalty points redeemed.");
        }
    }
}
