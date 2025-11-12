import java.util.*;

public class AdminModule {
    private static Store store = BrewiseCoffeeShop.store;

    public static void adminMenu() {
        while (true) {
            System.out.println("\n╔═══════════════════════════════════════════════════════════╗");
            System.out.println("║                     ADMIN MENU                            ║");
            System.out.println("╠═══════════════════════════════════════════════════════════╣");
            System.out.println("║                                                           ║");
            System.out.println("║    [1]  Inventory Management                              ║");
            System.out.println("║    [2]  Product Management                                ║");
            System.out.println("║    [3]  Sales & Reports                                   ║");
            System.out.println("║    [4]  Cashier Management                                ║");
            System.out.println("║    [5]  Back                                              ║");
            System.out.println("║    [6]  Logout                                            ║");
            System.out.println("║                                                           ║");
            System.out.println("╚═══════════════════════════════════════════════════════════╝");
            
            int choice = BrewiseCoffeeShop.getInt("  Enter your choice [1-6]: ", 1, 6);
            switch (choice) {
                case 1:
                    inventoryManagement();
                    break;
                case 2:
                    productManagement();
                    break;
                case 3:
                    salesReports();
                    break;
                case 4:
                    cashierManagement();
                    break;
                case 5:
                    return;
                case 6:
                    System.out.println("  [SUCCESS] Admin logged out.");
                    return;
            }
        }
    }

    // ==================== INVENTORY MANAGEMENT ====================
    private static void inventoryManagement() {
        while (true) {
            System.out.println("\n╔═══════════════════════════════════════════════════════════╗");
            System.out.println("║               INVENTORY MANAGEMENT                        ║");
            System.out.println("╠═══════════════════════════════════════════════════════════╣");
            System.out.println("║                                                           ║");
            System.out.println("║    [1]  View All Inventory                                ║");
            System.out.println("║    [2]  Update Stock Quantity                             ║");
            System.out.println("║    [3]  Set Reorder Threshold                             ║");
            System.out.println("║    [4]  View Low Stock Items                              ║");
            System.out.println("║    [5]  Back                                              ║");
            System.out.println("║                                                           ║");
            System.out.println("╚═══════════════════════════════════════════════════════════╝");
            
            int choice = BrewiseCoffeeShop.getInt("  Enter your choice [1-5]: ", 1, 5);
            switch (choice) {
                case 1:
                    viewInventory();
                    break;
                case 2:
                    updateStockQuantity();
                    break;
                case 3:
                    setReorderThreshold();
                    break;
                case 4:
                    viewLowStockItems();
                    break;
                case 5:
                    return;
            }
        }
    }

    private static void viewInventory() {
        System.out.println("\n╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║                   INVENTORY REPORT                         ║");
        System.out.println("╠═══════════════════════════════════════════════════════════╣");
        
        Map<String, InventoryItem> inventory = store.getInventory();
        if (inventory.isEmpty()) {
            System.out.println("║  No inventory items.                                      ║");
        } else {
            for (Map.Entry<String, InventoryItem> entry : inventory.entrySet()) {
                InventoryItem item = entry.getValue();
                String status = item.quantity <= item.reorderThreshold ? "[LOW]" : "[OK]";
                System.out.printf("║  %-30s Qty: %5.1f %-4s %s%n", 
                    item.name, item.quantity, item.unit, status);
            }
        }
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
    }

    private static void updateStockQuantity() {
        System.out.println("\n  Update Stock Quantity");
        System.out.println("  Available items:");
        Map<String, InventoryItem> inventory = store.getInventory();
        List<String> keys = new ArrayList<>(inventory.keySet());
        
        for (int i = 0; i < keys.size(); i++) {
            InventoryItem item = inventory.get(keys.get(i));
            System.out.println("  [" + (i + 1) + "]  " + item.name + " (Current: " + item.quantity + " " + item.unit + ")");
        }
        
        int itemChoice = BrewiseCoffeeShop.getInt("  Select item [1-" + keys.size() + "]: ", 1, keys.size());
        InventoryItem selectedItem = inventory.get(keys.get(itemChoice - 1));
        
        System.out.println("  Current quantity: " + selectedItem.quantity + " " + selectedItem.unit);
        double newQuantity = getDouble("  Enter new quantity: ");
        selectedItem.quantity = newQuantity;
        System.out.println("  [SUCCESS] Inventory updated!");
    }

    private static void setReorderThreshold() {
        System.out.println("\n  Set Reorder Threshold");
        Map<String, InventoryItem> inventory = store.getInventory();
        List<String> keys = new ArrayList<>(inventory.keySet());
        
        for (int i = 0; i < keys.size(); i++) {
            InventoryItem item = inventory.get(keys.get(i));
            System.out.println("  [" + (i + 1) + "]  " + item.name + " (Current threshold: " + item.reorderThreshold + ")");
        }
        
        int itemChoice = BrewiseCoffeeShop.getInt("  Select item [1-" + keys.size() + "]: ", 1, keys.size());
        InventoryItem selectedItem = inventory.get(keys.get(itemChoice - 1));
        
        double newThreshold = getDouble("  Enter reorder threshold: ");
        selectedItem.reorderThreshold = newThreshold;
        System.out.println("  [SUCCESS] Reorder threshold updated!");
    }

    private static void viewLowStockItems() {
        System.out.println("\n╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║                  LOW STOCK ITEMS ALERT                     ║");
        System.out.println("╠═══════════════════════════════════════════════════════════╣");
        
        Map<String, InventoryItem> inventory = store.getInventory();
        List<InventoryItem> lowStockItems = new ArrayList<>();
        
        for (InventoryItem item : inventory.values()) {
            if (item.quantity <= item.reorderThreshold) {
                lowStockItems.add(item);
            }
        }
        
        if (lowStockItems.isEmpty()) {
            System.out.println("║  All items are well-stocked!                             ║");
        } else {
            for (InventoryItem item : lowStockItems) {
                double shortage = item.reorderThreshold - item.quantity;
                System.out.printf("║  [!] %-28s Current: %5.1f  Need: %5.1f  ║%n", 
                    item.name, item.quantity, shortage);
            }
        }
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
    }

    // ==================== PRODUCT MANAGEMENT ====================
    private static void productManagement() {
        while (true) {
            System.out.println("\n╔═══════════════════════════════════════════════════════════╗");
            System.out.println("║                PRODUCT MANAGEMENT                         ║");
            System.out.println("╠═══════════════════════════════════════════════════════════╣");
            System.out.println("║                                                           ║");
            System.out.println("║    [1]  View All Products                                 ║");
            System.out.println("║    [2]  Update Product Price                              ║");
            System.out.println("║    [3]  Mark Product Unavailable                          ║");
            System.out.println("║    [4]  Mark Product Available                            ║");
            System.out.println("║    [5]  View Menu By Category                             ║");
            System.out.println("║    [6]  Back                                              ║");
            System.out.println("║                                                           ║");
            System.out.println("╚═══════════════════════════════════════════════════════════╝");
            
            int choice = BrewiseCoffeeShop.getInt("  Enter your choice [1-6]: ", 1, 6);
            switch (choice) {
                case 1:
                    viewAllProducts();
                    break;
                case 2:
                    updateProductPrice();
                    break;
                case 3:
                    markProductUnavailable();
                    break;
                case 4:
                    markProductAvailable();
                    break;
                case 5:
                    viewMenuByCategory();
                    break;
                case 6:
                    return;
            }
        }
    }

    private static void viewAllProducts() {
        System.out.println("\n╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║                   ALL PRODUCTS                             ║");
        System.out.println("╠═══════════════════════════════════════════════════════════╣");
        
        List<Product> products = store.getProducts();
        if (products.isEmpty()) {
            System.out.println("║  No products found.                                       ║");
        } else {
            for (int i = 0; i < products.size(); i++) {
                Product p = products.get(i);
                String status = p.available ? "[AVAIL]" : "[OUT]";
                System.out.printf("║  [%2d]  %-30s %.2f PHP %s%n", 
                    i + 1, p.name, p.price, status);
            }
        }
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
    }

    private static void updateProductPrice() {
        System.out.println("\n  Update Product Price");
        List<Product> products = store.getProducts();
        
        for (int i = 0; i < products.size(); i++) {
            Product p = products.get(i);
            System.out.println("  [" + (i + 1) + "]  " + p.name + " (Current: " + p.price + " PHP)");
        }
        
        int productChoice = BrewiseCoffeeShop.getInt("  Select product [1-" + products.size() + "]: ", 1, products.size());
        Product selectedProduct = products.get(productChoice - 1);
        
        double newPrice = getDouble("  Enter new price: ");
        selectedProduct.price = newPrice;
        System.out.println("  [SUCCESS] Price updated to " + newPrice + " PHP");
    }

    private static void markProductUnavailable() {
        System.out.println("\n  Mark Product Unavailable");
        List<Product> products = store.getProducts();
        
        for (int i = 0; i < products.size(); i++) {
            Product p = products.get(i);
            System.out.println("  [" + (i + 1) + "]  " + p.name + " [" + (p.available ? "AVAILABLE" : "UNAVAILABLE") + "]");
        }
        
        int productChoice = BrewiseCoffeeShop.getInt("  Select product [1-" + products.size() + "]: ", 1, products.size());
        Product selectedProduct = products.get(productChoice - 1);
        
        selectedProduct.available = false;
        System.out.println("  [SUCCESS] " + selectedProduct.name + " marked as UNAVAILABLE");
    }

    private static void markProductAvailable() {
        System.out.println("\n  Mark Product Available");
        List<Product> products = store.getProducts();
        
        for (int i = 0; i < products.size(); i++) {
            Product p = products.get(i);
            System.out.println("  [" + (i + 1) + "]  " + p.name + " [" + (p.available ? "AVAILABLE" : "UNAVAILABLE") + "]");
        }
        
        int productChoice = BrewiseCoffeeShop.getInt("  Select product [1-" + products.size() + "]: ", 1, products.size());
        Product selectedProduct = products.get(productChoice - 1);
        
        selectedProduct.available = true;
        System.out.println("  [SUCCESS] " + selectedProduct.name + " marked as AVAILABLE");
    }

    private static void viewMenuByCategory() {
        System.out.println("\n  View Menu By Category");
        List<String> categories = store.getCategories();
        
        for (int i = 0; i < categories.size(); i++) {
            System.out.println("  [" + (i + 1) + "]  " + categories.get(i));
        }
        
        int categoryChoice = BrewiseCoffeeShop.getInt("  Select category [1-" + categories.size() + "]: ", 1, categories.size());
        List<Product> products = store.getProductsByCategory(categories.get(categoryChoice - 1));
        
        System.out.println("\n  --- " + categories.get(categoryChoice - 1).toUpperCase() + " ---");
        for (Product p : products) {
            System.out.println("  • " + p.name + " - " + p.price + " PHP [" + (p.available ? "AVAIL" : "OUT") + "]");
        }
    }

    // ==================== SALES & REPORTS ====================
    private static void salesReports() {
        while (true) {
            System.out.println("\n╔═══════════════════════════════════════════════════════════╗");
            System.out.println("║                 SALES & REPORTS                           ║");
            System.out.println("╠═══════════════════════════════════════════════════════════╣");
            System.out.println("║                                                           ║");
            System.out.println("║    [1]  View Pending Orders                               ║");
            System.out.println("║    [2]  View Completed Orders                             ║");
            System.out.println("║    [3]  Daily Sales Summary                               ║");
            System.out.println("║    [4]  Order Count Statistics                            ║");
            System.out.println("║    [5]  Back                                              ║");
            System.out.println("║                                                           ║");
            System.out.println("╚═══════════════════════════════════════════════════════════╝");
            
            int choice = BrewiseCoffeeShop.getInt("  Enter your choice [1-5]: ", 1, 5);
            switch (choice) {
                case 1:
                    viewPendingOrders();
                    break;
                case 2:
                    viewCompletedOrders();
                    break;
                case 3:
                    dailySalesSummary();
                    break;
                case 4:
                    orderCountStatistics();
                    break;
                case 5:
                    return;
            }
        }
    }

    private static void viewPendingOrders() {
        System.out.println("\n╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║                   PENDING ORDERS                           ║");
        System.out.println("╠═══════════════════════════════════════════════════════════╣");
        
        Map<String, BrewiseCoffeeShop.Order> pending = store.getPendingOrders();
        if (pending.isEmpty()) {
            System.out.println("║  No pending orders.                                       ║");
        } else {
            for (Map.Entry<String, BrewiseCoffeeShop.Order> entry : pending.entrySet()) {
                BrewiseCoffeeShop.Order order = entry.getValue();
                System.out.printf("║  Order: %s  Total: %.2f PHP  Status: %s%n", 
                    entry.getKey(), order.totalPrice, (order.paid ? "PAID" : "PENDING"));
            }
        }
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
    }

    private static void viewCompletedOrders() {
        System.out.println("\n╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║                   COMPLETED ORDERS                         ║");
        System.out.println("╠═══════════════════════════════════════════════════════════╣");
        
        Map<String, BrewiseCoffeeShop.Order> completed = store.getCompletedOrders();
        if (completed.isEmpty()) {
            System.out.println("║  No completed orders.                                     ║");
        } else {
            for (Map.Entry<String, BrewiseCoffeeShop.Order> entry : completed.entrySet()) {
                BrewiseCoffeeShop.Order order = entry.getValue();
                System.out.printf("║  Order: %s  Total: %.2f PHP  TxID: %s%n", 
                    entry.getKey(), order.totalPrice, order.transactionId);
            }
        }
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
    }

    private static void dailySalesSummary() {
        System.out.println("\n╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║                  DAILY SALES SUMMARY                       ║");
        System.out.println("╠═══════════════════════════════════════════════════════════╣");
        
        Map<String, BrewiseCoffeeShop.Order> completed = store.getCompletedOrders();
        double totalSales = 0;
        
        for (BrewiseCoffeeShop.Order order : completed.values()) {
            if (order.paid) {
                totalSales += order.totalPrice;
            }
        }
        
        System.out.printf("║  Total Sales:           %.2f PHP%n", totalSales);
        System.out.printf("║  Orders Completed:      %d%n", completed.size());
        System.out.printf("║  Average Order Value:   %.2f PHP%n", 
            completed.isEmpty() ? 0 : totalSales / completed.size());
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
    }

    private static void orderCountStatistics() {
        System.out.println("\n╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║                  ORDER STATISTICS                          ║");
        System.out.println("╠═══════════════════════════════════════════════════════════╣");
        
        Map<String, BrewiseCoffeeShop.Order> pending = store.getPendingOrders();
        Map<String, BrewiseCoffeeShop.Order> completed = store.getCompletedOrders();
        
        System.out.printf("║  Pending Orders:        %d%n", pending.size());
        System.out.printf("║  Completed Orders:      %d%n", completed.size());
        System.out.printf("║  Total Orders:          %d%n", pending.size() + completed.size());
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
    }

    // ==================== CASHIER MANAGEMENT ====================
    private static void cashierManagement() {
        AdminCashierModule.manageCashiers();
    }

    // ==================== HELPER METHODS ====================
    private static double getDouble(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                double value = Double.parseDouble(BrewiseCoffeeShop.sc.nextLine());
                if (value < 0) {
                    System.out.println("  [ERROR] Please enter a positive number.");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("  [ERROR] Invalid input. Please enter a valid number.");
            }
        }
    }
}
