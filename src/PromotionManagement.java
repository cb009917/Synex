import java.util.Scanner;

public class PromotionManagement {

    public static void execute(Scanner scanner){

        AddNewPromotion addNewPromotion = new AddNewPromotion();
        RemovePromotion removePromotion = new RemovePromotion();

        System.out.println("1. Add new promotion");
        System.out.println("2. Update existing promotion");
        System.out.println("3. Remove promotion");
        System.out.println("4. Exit");
        int crud_choice = scanner.nextInt();
        scanner.nextLine();

        switch (crud_choice){
            case 1: addNewPromotion.execute(scanner);
                break;
//            case 2: Main.updatePromotion(scanner);
//                break;
            case 3: removePromotion.execute(scanner);
                break;
            case 4:
                return;
        }
    }
}
