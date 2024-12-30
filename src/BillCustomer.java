import java.util.Scanner;

public class BillCustomer {


    public static void execute(Scanner scanner){

        String transaction_type = "POS";
        Bill bill = new Bill();

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

        System.out.println("Cash Tendered: ");
        double cash_tendered = scanner.nextDouble();
        double cash_returned = bill.change_calculation(cash_tendered);
        System.out.println(cash_returned);


        // need to change these
        bill.checkout(transaction_type, loyaltySystem.loyaltyNumber);
        bill.printBill(Main.employeeName);


    }
}
