import java.util.Scanner;

public class StockManagement {

    public static void execute(Scanner scanner){

        AddNewStock addNewStock = new AddNewStock();
        UpdateStock updateStock = new UpdateStock();
        RemoveStock removeStock = new RemoveStock();

        System.out.println("1. Add new stock");
        System.out.println("2. Update existing stock");
        System.out.println("3. Remove stock");
        System.out.println("4. Exit");

        int crud_choice = scanner.nextInt();
        scanner.nextLine();

        switch (crud_choice){
            case 1: addNewStock.execute();
                break;
            case 2: updateStock.execute();
                break;
            case 3: removeStock.execute();
                break;
            case 4:
                return;
        }
    }
}
