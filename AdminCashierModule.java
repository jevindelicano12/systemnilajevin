import java.util.List;
import java.util.Scanner;

/**
 * Admin Module for Managing Cashiers
 * Allows admin to add, remove, and edit cashier accounts
 */
public class AdminCashierModule {
    static Scanner sc = BrewiseCoffeeShop.sc;
    static Store store = BrewiseCoffeeShop.store;

    public static void manageCashiers() {
        while (true) {
            System.out.println("\n╔════════════════════════════════════════╗");
            System.out.println("║    💳 CASHIER MANAGEMENT PANEL 💳     ║");
            System.out.println("╠════════════════════════════════════════╣");
            System.out.println("║    [1]  View All Cashiers              ║");
            System.out.println("║    [2]  Add New Cashier                ║");
            System.out.println("║    [3]  Edit Cashier Password          ║");
            System.out.println("║    [4]  Deactivate Cashier             ║");
            System.out.println("║    [5]  Activate Cashier (REQUIRED)    ║");
            System.out.println("║    [6]  Remove Cashier                 ║");
            System.out.println("║    [7]  Back                           ║");
            System.out.println("╚════════════════════════════════════════╝");
            System.out.print("Enter your choice [1-7]: ");
            
            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1":
                    viewAllCashiers();
                    break;
                case "2":
                    addNewCashier();
                    break;
                case "3":
                    editCashierPassword();
                    break;
                case "4":
                    deactivateCashier();
                    break;
                case "5":
                    activateCashier();
                    break;
                case "6":
                    removeCashier();
                    break;
                case "7":
                    return;
                default:
                    System.out.println("[ERROR] Invalid choice. Please try again.");
            }
        }
    }

    private static void viewAllCashiers() {
        List<CashierAccount> cashiers = store.getCashiers();
        
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║    👥 ALL CASHIER ACCOUNTS 👥         ║");
        System.out.println("╠════════════════════════════════════════╣");
        
        if (cashiers.isEmpty()) {
            System.out.println("║  No cashiers found                     ║");
            System.out.println("╚════════════════════════════════════════╝");
            return;
        }
        
        for (int i = 0; i < cashiers.size(); i++) {
            CashierAccount c = cashiers.get(i);
            String status = c.isActive() ? "[ACTIVE] " : "[INACTIVE]";
            System.out.printf("║  %d. %-20s %s       ║%n", i + 1, c.username, status);
        }
        System.out.println("╚════════════════════════════════════════╝");
    }

    private static void addNewCashier() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║    ➕ ADD NEW CASHIER ➕               ║");
        System.out.println("╚════════════════════════════════════════╝");
        
        System.out.print("Enter new cashier username: ");
        String username = sc.nextLine().trim();
        
        if (username.isEmpty()) {
            System.out.println("❌ Username cannot be empty!");
            return;
        }
        
        if (store.findCashier(username) != null) {
            System.out.println("❌ Cashier '" + username + "' already exists!");
            return;
        }
        
        System.out.print("Enter password: ");
        String password = sc.nextLine().trim();
        
        if (password.isEmpty()) {
            System.out.println("❌ Password cannot be empty!");
            return;
        }
        
        boolean success = store.addCashier(username, password);
        if (success) {
            System.out.println("\n╔════════════════════════════════════════╗");
            System.out.println("║    ✅ CASHIER CREATED SUCCESSFULLY ✅  ║");
            System.out.println("╠════════════════════════════════════════╣");
            System.out.println("║  Username: " + BrewiseCoffeeShop.padRight(username, 26) + "║");
            System.out.println("║  Status:   INACTIVE (Pending Approval) ║");
            System.out.println("║                                        ║");
            System.out.println("║  ⚠️  IMPORTANT:                        ║");
            System.out.println("║  This cashier account has been         ║");
            System.out.println("║  created but is currently INACTIVE.    ║");
            System.out.println("║                                        ║");
            System.out.println("║  You must ACTIVATE this account        ║");
            System.out.println("║  before the cashier can access the     ║");
            System.out.println("║  payment system.                       ║");
            System.out.println("║                                        ║");
            System.out.println("║  Use option [5] to activate this       ║");
            System.out.println("║  cashier account.                      ║");
            System.out.println("╚════════════════════════════════════════╝");
            // Immediately save to storage
            PersistenceManager.saveStore(BrewiseCoffeeShop.store);
            System.out.println("💾 Changes saved to database!");
        } else {
            System.out.println("❌ Failed to add cashier!");
        }
    }

    private static void editCashierPassword() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║    🔐 EDIT CASHIER PASSWORD 🔐        ║");
        System.out.println("╚════════════════════════════════════════╝");
        
        System.out.print("Enter cashier username: ");
        String username = sc.nextLine().trim();
        
        CashierAccount cashier = store.findCashier(username);
        if (cashier == null) {
            System.out.println("❌ Cashier '" + username + "' not found!");
            return;
        }
        
        System.out.print("Enter new password: ");
        String newPassword = sc.nextLine().trim();
        
        if (newPassword.isEmpty()) {
            System.out.println("❌ Password cannot be empty!");
            return;
        }
        
        boolean success = store.editCashierPassword(username, newPassword);
        if (success) {
            System.out.println("✅ Password for '" + username + "' changed successfully!");
            // Save changes to storage
            PersistenceManager.saveStore(BrewiseCoffeeShop.store);
            System.out.println("💾 Changes saved to database!");
        } else {
            System.out.println("❌ Failed to edit password!");
        }
    }

    private static void deactivateCashier() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║    ⛔ DEACTIVATE CASHIER ⛔            ║");
        System.out.println("╚════════════════════════════════════════╝");
        
        System.out.print("Enter cashier username to deactivate: ");
        String username = sc.nextLine().trim();
        
        if (username.equals("admin")) {
            System.out.println("[ERROR] Cannot deactivate admin account!");
            return;
        }
        
        CashierAccount cashier = store.findCashier(username);
        if (cashier == null) {
            System.out.println("[ERROR] Cashier '" + username + "' not found!");
            return;
        }
        
        if (!cashier.isActive()) {
            System.out.println("[INFO] Cashier '" + username + "' is already INACTIVE!");
            return;
        }
        
        boolean success = store.deactivateCashier(username);
        if (success) {
            System.out.println("\n╔════════════════════════════════════════╗");
            System.out.println("║    ⛔ CASHIER DEACTIVATED ⛔           ║");
            System.out.println("╠════════════════════════════════════════╣");
            System.out.println("║  Username: " + BrewiseCoffeeShop.padRight(username, 26) + "║");
            System.out.println("║  Status:   [INACTIVE]                  ║");
            System.out.println("║                                        ║");
            System.out.println("║  This cashier will:                    ║");
            System.out.println("║  • Cannot login to the system          ║");
            System.out.println("║  • Cannot process payments             ║");
            System.out.println("║  • Account is suspended                ║");
            System.out.println("║                                        ║");
            System.out.println("║  Use option [5] to reactivate.         ║");
            System.out.println("╚════════════════════════════════════════╝");
            // Save changes to storage
            PersistenceManager.saveStore(BrewiseCoffeeShop.store);
            System.out.println("[SUCCESS] Changes saved to database!");
        } else {
            System.out.println("[ERROR] Failed to deactivate cashier!");
        }
    }

    private static void activateCashier() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║    ✅ ACTIVATE CASHIER ✅              ║");
        System.out.println("╚════════════════════════════════════════╝");
        
        System.out.print("Enter cashier username to activate: ");
        String username = sc.nextLine().trim();
        
        CashierAccount cashier = store.findCashier(username);
        if (cashier == null) {
            System.out.println("[ERROR] Cashier '" + username + "' not found!");
            return;
        }
        
        if (cashier.isActive()) {
            System.out.println("[INFO] Cashier '" + username + "' is already ACTIVE!");
            return;
        }
        
        boolean success = store.activateCashier(username);
        if (success) {
            System.out.println("\n╔════════════════════════════════════════╗");
            System.out.println("║    ✅ CASHIER ACTIVATED ✅             ║");
            System.out.println("╠════════════════════════════════════════╣");
            System.out.println("║  Username: " + BrewiseCoffeeShop.padRight(username, 26) + "║");
            System.out.println("║  Status:   [ACTIVE]                    ║");
            System.out.println("║                                        ║");
            System.out.println("║  This cashier can now:                 ║");
            System.out.println("║  • Login to the cashier system         ║");
            System.out.println("║  • Process payments                    ║");
            System.out.println("║  • Print receipts                      ║");
            System.out.println("║  • Generate transactions               ║");
            System.out.println("╚════════════════════════════════════════╝");
            // Save changes to storage
            PersistenceManager.saveStore(BrewiseCoffeeShop.store);
            System.out.println("[SUCCESS] Changes saved to database!");
        } else {
            System.out.println("[ERROR] Failed to activate cashier!");
        }
    }

    private static void removeCashier() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║    🗑️  REMOVE CASHIER 🗑️               ║");
        System.out.println("╚════════════════════════════════════════╝");
        
        System.out.print("Enter cashier username to remove: ");
        String username = sc.nextLine().trim();
        
        if (username.equals("admin")) {
            System.out.println("❌ Cannot remove admin account!");
            return;
        }
        
        CashierAccount cashier = store.findCashier(username);
        if (cashier == null) {
            System.out.println("❌ Cashier '" + username + "' not found!");
            return;
        }
        
        System.out.print("Are you sure you want to remove this cashier? (yes/no): ");
        String confirm = sc.nextLine().trim().toLowerCase();
        
        if (!confirm.equals("yes")) {
            System.out.println("❌ Removal cancelled!");
            return;
        }
        
        boolean success = store.removeCashier(username);
        if (success) {
            System.out.println("✅ Cashier '" + username + "' removed successfully!");
            // Save changes to storage
            PersistenceManager.saveStore(BrewiseCoffeeShop.store);
            System.out.println("💾 Changes saved to database!");
        } else {
            System.out.println("❌ Failed to remove cashier (minimum 1 cashier required)!");
        }
    }
}
