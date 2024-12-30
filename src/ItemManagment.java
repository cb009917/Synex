import java.util.Scanner;

public class ItemManagment {

    public static void execute(Scanner scanner){

        RemoveItem removeItem = new RemoveItem();
        AddNewItem  addNewItem = new AddNewItem();
        UpdateItem updateItem = new UpdateItem();

        System.out.println("1. Add new item");
        System.out.println("2. Update existing item");
        System.out.println("3. Remove item");
        System.out.println("4. Exit");

        int crud_choice = scanner.nextInt();
        scanner.nextLine();

        switch (crud_choice){
            case 1: addNewItem.execute();
                    break;
            case 2: updateItem.execute();
                    break;
            case 3: removeItem.execute();
                    break;
            case 4 :
                return;
        }
    }
}
