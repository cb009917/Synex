import java.util.Scanner;

public class categoryManagment {

    public static void execute(Scanner scanner){

        AddCategory addCategory = new AddCategory();
        UpdateCategory updateCategory = new UpdateCategory();
        RemoveCategory removeCategory = new RemoveCategory();

        System.out.println("1. Add new category");
        System.out.println("2. Update existing category");
        System.out.println("3. Remove category");
        System.out.println("4. Exit");

        int crud_choice = scanner.nextInt();
        scanner.nextLine();

        switch (crud_choice){
            case 1: addCategory.execute();
                break;
            case 2: updateCategory.execute();
                break;
            case 3: removeCategory.execute();
                break;
            case 4:
                return;
        }
    }
}
