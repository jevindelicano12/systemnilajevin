import java.util.*;

public class AdminModule {
    public static void adminMenu() {
        while (true) {
            System.out.println("\n--- ADMIN MENU ---");
            System.out.println("1 View Inventory");
            System.out.println("2 Edit Inventory");
            System.out.println("3 Back");
            System.out.println("4 Logout");
            int choice = BrewiseCoffeeShop.getInt("Enter choice: ", 1, 4);
            switch (choice) {
                case 1 -> BrewiseCoffeeShop.store.getInventory().forEach((k, v) -> System.out.println(k + " – " + v.name + ": " + v.quantity + " " + v.unit));
                case 2 -> { System.out.println("Inventory editing not implemented yet."); }
                case 3 -> { return; }
                case 4 -> { System.out.println("Admin logged out."); return; }
            }
        }
    }
}
