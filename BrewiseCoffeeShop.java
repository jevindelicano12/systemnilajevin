import java.util.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BrewiseCoffeeShop {
    static Scanner sc = new Scanner(System.in);
    static String adminPassword = "admin123";
    static String cashierPassword = "cashier123";
    static Store store;
    static String currentCashier = null; // Track current logged-in cashier

    // When true, unwind all the way back to main menu
    static boolean returnToMain = false;
    private static int transactionCounter = 5000;

    public static void main(String[] args) {
        // === DATABASE INITIALIZATION ===
        System.out.println("\n╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║                                                           ║");
        System.out.println("║          BREWISE COFFEE SHOP MANAGEMENT SYSTEM            ║");
        System.out.println("║                                                           ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println("  Working Directory: " + System.getProperty("user.dir"));
        
        // Load store from JSON
        store = PersistenceManager.loadStore();
        if (store == null || store.getProducts().isEmpty()) {
            System.out.println("  Initializing system with default data...");
            store = new Store(); // Initialize with default data
            PersistenceManager.saveStore(store);
            System.out.println("  [SUCCESS] System initialized and saved.");
        } else {
            System.out.println("  [SUCCESS] System loaded from database.");
        }
        
        // Add shutdown hook to save data on exit
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n  Saving data...");
            PersistenceManager.saveStore(store);
            System.out.println("  [SUCCESS] Data saved successfully!");
        }));
        
        mainMenu();
    }

    // ---------------------- MAIN MENU ----------------------
    static void mainMenu() {
        while (true) {
            System.out.println("\n╔═══════════════════════════════════════════════════════════╗");
            System.out.println("║                   MAIN MENU                               ║");
            System.out.println("╠═══════════════════════════════════════════════════════════╣");
            System.out.println("║                                                           ║");
            System.out.println("║    [1]  Browse Menu                                       ║");
            System.out.println("║    [2]  View/Edit Basket                                  ║");
            System.out.println("║    [3]  Checkout & Payment                                ║");
            System.out.println("║    [4]  Quick Stats                                       ║");
            System.out.println("║    [5]  Exit                                              ║");
            System.out.println("║                                                           ║");
            System.out.println("╚═══════════════════════════════════════════════════════════╝");
            
            System.out.print("  Enter your choice [1-5]: ");
            String input = sc.nextLine();

            switch (input) {
                case "1":
                    customerMode();
                    break;
                case "2":
                    viewBasket();
                    break;
                case "3":
                    viewBasket(); // Redirect to basket menu which has checkout option (option 4)
                    break;
                case "4":
                    quickStats();
                    break;
                case "5":
                    System.out.println("\n╔═══════════════════════════════════════════════════════════╗");
                    System.out.println("║                                                           ║");
                    System.out.println("║      Thank you for visiting Brewise Coffee Shop!         ║");
                    System.out.println("║             Have a great day!                             ║");
                    System.out.println("║                                                           ║");
                    System.out.println("╚═══════════════════════════════════════════════════════════╝\n");
                    return;
                case "admin:login":
                    adminLogin();
                    break;
                case "cashier:login":
                    cashierLogin();
                    break;
                default:
                    System.out.println("  [ERROR] Invalid input. Please try again.");
            }
        }
    }

    // ---------------------- CUSTOMER MODE ----------------------
    static void customerMode() {
        CustomerModule.customerMode();
    }

    // ---------------------- QUICK STATS ----------------------
    static void quickStats() {
        Map<String, BrewiseCoffeeShop.Order> completed = store.getCompletedOrders();
        double totalSales = 0;
        
        for (BrewiseCoffeeShop.Order order : completed.values()) {
            if (order.paid) {
                totalSales += order.totalPrice;
            }
        }
        
        System.out.println("\n╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║              TODAY'S QUICK STATISTICS                      ║");
        System.out.println("╠═══════════════════════════════════════════════════════════╣");
        System.out.println("║                                                           ║");
        System.out.printf("║  Total Sales:              %34.2f PHP║%n", totalSales);
        System.out.printf("║  Orders Completed:         %34d    ║%n", completed.size());
        System.out.printf("║  Average Order Value:      %34.2f PHP║%n", 
            completed.isEmpty() ? 0 : totalSales / completed.size());
        System.out.println("║                                                           ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
    }

    // ---------------------- MILKTEA MENU ----------------------
    static void milkteaMenu() {
        while (true) {
            System.out.println("\n╔══════════════════════════════════════════╗");
            System.out.println("║           MILKTEA MENU                   ║");
            System.out.println("╠══════════════════════════════════════════╣");
            System.out.println("║    1  Strawberry                         ║");
            System.out.println("║    2  Matcha                             ║");
            System.out.println("║    3  Back                               ║");
            System.out.println("╚══════════════════════════════════════════╝");
            int choice = getInt("Enter your choice [1-3]: ", 1, 3);

            switch (choice) {
                case 1: 
                    selectSize("Strawberry");
                    break;
                case 2:
                    selectSize("Matcha");
                    break;
                case 3:
                    return;
            }

            // If deeper menu requested return to main menu, help unwind
            if (returnToMain) {
                // leave flag set so higher menus also unwind
                return;
            }
        }
    }

    // ---------------------- SIZE SELECTION ----------------------
    static void selectSize(String drink) {
        while (true) {
            System.out.println("\n╔════════════════════════════════════════╗");
            System.out.println("║         " + padCenter(drink.toUpperCase() + " SIZE", 20) + "           ║");
            System.out.println("╠════════════════════════════════════════╣");
            System.out.println("║    1  12 oz Small  - ₱39               ║");
            System.out.println("║    2  16 oz Medium - ₱49               ║");
            System.out.println("║    3  22 oz Large  - ₱59               ║");
            System.out.println("║    4  Back                             ║");
            System.out.println("╚════════════════════════════════════════╝");
            int choice = getInt("Enter your choice [1-4]: ", 1, 4);
            int price = 0;
            String sizeLabel = "";

            switch (choice) {
                case 1: price = 39; sizeLabel = "12 oz Small"; break;
                case 2: price = 49; sizeLabel = "16 oz Medium"; break;
                case 3: price = 59; sizeLabel = "22 oz Large"; break;
                case 4: return;
            }

            int quantity = getInt("Quantity: ", 1, 100);
            selectSugarLevel(drink + " " + sizeLabel, price, quantity);
            // If user chose Add to Basket deeper in the flow, help unwind to main menu
            if (returnToMain) {
                // leave flag set so higher menus also unwind
                return;
            }
        }
    }

    // ---------------------- SUGAR LEVEL ----------------------
    static void selectSugarLevel(String drink, int price, int quantity) {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║                SUGAR LEVEL             ║");
        System.out.println("╠════════════════════════════════════════╣");
        System.out.println("║  " + drink + " x" + quantity+ "             ║" );
        System.out.println("║  Total: php" + price +"                          ║");
        System.out.println("╠════════════════════════════════════════╣");
        System.out.println("║    1  Less sweet                       ║");
        System.out.println("║    2  Standard                         ║");
        System.out.println("║    3  Sweet                            ║");
        System.out.println("╚════════════════════════════════════════╝");
        int choice = getInt("Enter your choice [1-3]: ", 1, 3);

        String sugar = switch (choice) {
            case 1 -> "Less Sweet";
            case 2 -> "Standard";
            case 3 -> "Sweet";
            default -> "Standard";
        };

        addOnsMenu(drink, price, quantity, sugar);
    }

    // ---------------------- ADD-ONS MENU ----------------------
    static void addOnsMenu(String drink, int basePrice, int quantity, String sugar) {
        double totalPrice = basePrice * quantity;
        List<String> addOns = new ArrayList<>();
        Map<String, Integer> addOnsQty = new HashMap<>();

        while (true) {
            System.out.println("\n╔════════════════════════════════════════╗");
            System.out.println("║                 ADD ONS                ║");
            System.out.println("╠════════════════════════════════════════╣");
            System.out.println("║  " + drink + " x" + quantity);
            System.out.println("║  Sugar Level: " + sugar);
            System.out.println("║  Price: ₱" + totalPrice);
            if (!addOns.isEmpty()) {
                System.out.println("╠════════════════════════════════════════╣");
                System.out.println("║  Current Add-ons:");
                addOns.forEach(a -> System.out.println("║   " + a + " x" + addOnsQty.get(a)));
            }
            System.out.println("╠════════════════════════════════════════╣");
            System.out.println("║    1  Nata   - Php10                   ║");
            System.out.println("║    2  Pearl  - Php10                   ║");
            System.out.println("║    3  Proceed                          ║");
            System.out.println("╚════════════════════════════════════════╝");
            int choice = getInt("Enter your choice [1-3]: ", 1, 3);

            if (choice == 3) {
                Order o = new Order(drink, quantity, sugar, addOns, addOnsQty, totalPrice, basePrice);
                confirmOrder(o);
                return;
            }

            int addQty = getInt("Quantity: ", 1, 10);
            String addonName = (choice == 1) ? "Nata" : "Pearl";
            addOns.add(addonName);
            addOnsQty.put(addonName, addOnsQty.getOrDefault(addonName, 0) + addQty);
            totalPrice += 10 * addQty;
        }
    }

    // ---------------------- CONFIRM ORDER ----------------------
    static void confirmOrder(Order order) {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║         ORDER SUMMARY                                     ║");
        System.out.println("╠════════════════════════════════════════╣");
        order.printSummary();
        System.out.println("╠════════════════════════════════════════╣");
        System.out.println("║    [1]  Add to Basket                                     ║");
        System.out.println("║    [2]  Checkout                                          ║");
        System.out.println("║    [3]  Cancel                                            ║");
        System.out.println("╚════════════════════════════════════════╝");
        int choice = getInt("Enter your choice [1-3]: ", 1, 3);

        switch (choice) {
            case 1:
                order.orderNumber = Integer.parseInt(store.generateOrderCode().substring(3));
                store.savePendingOrder(order);
                System.out.println("  [SUCCESS] Added to basket. Your order number is: " + order.orderNumber);
                System.out.println("Please give this ORDER NUMBER to the cashier when paying.");
                System.out.println("Copy this code: " + order.orderNumber);
                // signal to unwind all menus back to main welcome screen
                returnToMain = true;
                break;
            case 2:
                checkoutOrder(order);
                break;
            case 3:
                System.out.println("  [INFO] Order cancelled.");
                break;
        }
    }

    // ---------------------- CHECKOUT ORDER ----------------------
    static void checkoutOrder(Order order) {
        // Generate unique order number if not already set
        if (order.orderNumber == 0) {
            order.orderNumber = Integer.parseInt(store.generateOrderCode().substring(3));
        }
        store.savePendingOrder(order);

        System.out.println("\nCheckout options:");
        System.out.println("1. Dine In");
        System.out.println("2. Take Out");
        System.out.println("3. Back");
        int choice = getInt("Choice: ", 1, 3);

        if (choice == 1) {
            System.out.println("\n1. Pick at the counter");
            System.out.println("2. Serve");
            System.out.println("3. Back");
            int serveChoice = getInt("Choice: ", 1, 3);
            if (serveChoice == 1) {
                order.transactionId = generateTransactionId();
                showOrderDetails(order.orderNumber, order, "Dine In - Pick Up at counter");
            }
            if (serveChoice == 2) {
                order.transactionId = generateTransactionId();
                showOrderDetails(order.orderNumber, order, "Dine In - Serve");
            }
        } else if (choice == 2) {
            order.transactionId = generateTransactionId();
            showOrderDetails(order.orderNumber, order, "Take Out");
        }
    }

    // ---------------------- SHOW ORDER DETAILS ----------------------
    static void showOrderDetails(int orderNumber, Order order, String type) {
        System.out.println("\n--- ORDER DETAILS ---");
        System.out.println("Order Number: " + order.orderCode);
        System.out.println("Type: " + type);
        order.printSummary();
        System.out.println("Your unique order code is: " + order.orderCode);
        System.out.println("Please give this ORDER CODE to the cashier when paying.");
        System.out.println("Proceed to cashier!");
    }

    // ---------------------- VIEW BASKET ----------------------
    static void viewBasket() {
        while (true) {
            System.out.println("\n╔═══════════════════════════════════════════════════════════╗");
            System.out.println("║                      YOUR BASKET                          ║");
            System.out.println("╠═══════════════════════════════════════════════════════════╣");

            List<OrderItem> items = store.getCurrentBasketItems();
            if (items.isEmpty()) {
                System.out.println("║                                                           ║");
                System.out.println("║              Your basket is empty!                        ║");
                System.out.println("║                                                           ║");
                System.out.println("╠═══════════════════════════════════════════════════════════╣");
            } else {
                double basketTotal = 0;
                for (int i = 0; i < items.size(); i++) {
                    OrderItem item = items.get(i);
                    System.out.println("║  [" + (i + 1) + "]  " + padRight(item.product.name + " x" + item.quantity, 45) + " ║");
                    System.out.println("║      Sugar: " + padRight(item.sugarLevel, 13) + " | Total: " + String.format("%7.2f", item.getTotal()) + " PHP  ║");
                    if (!item.getAddOns().isEmpty()) {
                        for (AddOn addon : item.getAddOns()) {
                            int qty = item.getAddOnQuantity(addon);
                            System.out.println("║        + " + padRight(addon.name + " x" + qty, 50) + "║");
                        }
                    }
                    basketTotal += item.getTotal();
                    if (i < items.size() - 1) System.out.println("║  ─────────────────────────────────────────────────────   ║");
                }
                System.out.println("╠═══════════════════════════════════════════════════════════╣");
                System.out.println("║  TOTAL: " + padRight(String.format("%.2f PHP", basketTotal), 50) + "║");
                System.out.println("╠═══════════════════════════════════════════════════════════╣");
            }
            
            System.out.println("║                                                           ║");
            System.out.println("║    [1]  Add More Items                                    ║");
            System.out.println("║    [2]  Remove Item                                       ║");
            System.out.println("║    [3]  Edit Item                                         ║");
            System.out.println("║    [4]  Checkout                                          ║");
            System.out.println("║    [5]  Back                                              ║");
            System.out.println("║                                                           ║");
            System.out.println("╚═══════════════════════════════════════════════════════════╝");

            int choice = getInt("  Enter your choice [1-5]: ", 1, 5);

            switch (choice) {
                case 1:
                    customerMode();
                    break;
                case 2:
                    if (!items.isEmpty()) removeFromBasket();
                    else System.out.println("  [ERROR] Basket is empty! Nothing to remove.");
                    break;
                case 3:
                    if (!items.isEmpty()) editFromBasket();
                    else System.out.println("  [ERROR] Basket is empty! Nothing to edit.");
                    break;
                case 4:
                    if (!items.isEmpty()) {
                        java.util.List<String> orderCodes = store.checkoutBasket();
                        System.out.println("\n╔═══════════════════════════════════════════════════════════╗");
                        System.out.println("║              ORDER CONFIRMED!                             ║");
                        System.out.println("╠═══════════════════════════════════════════════════════════╣");
                        System.out.println("║                                                           ║");
                        System.out.println("║         Your Order Number(s):                             ║");
                        System.out.println("║                                                           ║");
                        for (String code : orderCodes) {
                            System.out.println("║              ORDER #" + padRight(code, 39) + "║");
                        }
                        System.out.println("║                                                           ║");
                        System.out.println("╠═══════════════════════════════════════════════════════════╣");
                        System.out.println("║  Please show this number to the cashier to complete      ║");
                        System.out.println("║  your payment!                                            ║");
                        System.out.println("╚═══════════════════════════════════════════════════════════╝");
                        return;
                    } else System.out.println("  [ERROR] Basket is empty! Nothing to checkout.");
                    break;
                case 5:
                    return;
            }
        }
    }
    
    // ---------------------- REMOVE FROM BASKET ----------------------
    static void removeFromBasket() {
        while (true) {
            System.out.println("\n╔═══════════════════════════════════════════════════════════╗");
            System.out.println("║              REMOVE FROM BASKET                           ║");
            System.out.println("╠═══════════════════════════════════════════════════════════╣");

            List<OrderItem> items = store.getCurrentBasketItems();
            for (int i = 0; i < items.size(); i++) {
                if (i > 0) System.out.println("║  ─────────────────────────────────────────────────────────  ║");
                System.out.println("║  [" + (i + 1) + "]  " + padRight(items.get(i).product.name + " x" + items.get(i).quantity, 45) + " ║");
                System.out.println("║      Subtotal: " + String.format("%7.2f", items.get(i).getTotal()) + " PHP                   ║");
            }

            System.out.println("╠═══════════════════════════════════════════════════════════╣");
            System.out.println("║  Enter 0 to cancel                                        ║");
            System.out.println("╚═══════════════════════════════════════════════════════════╝");

            int choice = getInt("  Choose item to remove [0-" + items.size() + "]: ", 0, items.size());
            if (choice == 0) return;

            OrderItem removed = items.get(choice - 1);
            store.removeFromBasket(removed);
            System.out.println("  [SUCCESS] Item removed from basket!");

            if (store.getCurrentBasketItems().isEmpty()) return;

            System.out.print("  Remove another item? (y/n): ");
            if (!sc.nextLine().trim().toLowerCase().startsWith("y")) return;
        }
    }

    // ---------------------- EDIT BASKET ITEM ----------------------
    static void editFromBasket() {
        while (true) {
            System.out.println("\n╔═══════════════════════════════════════════════════════════╗");
            System.out.println("║                 EDIT BASKET ITEM                          ║");
            System.out.println("╠═══════════════════════════════════════════════════════════╣");

            List<OrderItem> items = store.getCurrentBasketItems();
            if (items.isEmpty()) {
                System.out.println("║                                                           ║");
                System.out.println("║              Your basket is empty.                        ║");
                System.out.println("║                                                           ║");
                System.out.println("╚═══════════════════════════════════════════════════════════╝");
                return;
            }

            for (int i = 0; i < items.size(); i++) {
                OrderItem item = items.get(i);
                if (i > 0) System.out.println("║  ─────────────────────────────────────────────────────────  ║");
                System.out.println("║  [" + (i + 1) + "]  " + padRight(item.product.name + " x" + item.quantity, 45) + " ║");
                System.out.println("║      Sugar: " + padRight(item.sugarLevel, 13) + " | Total: " + String.format("%7.2f", item.getTotal()) + " PHP  ║");
            }

            System.out.println("╠═══════════════════════════════════════════════════════════╣");
            System.out.println("║  Enter 0 to cancel                                        ║");
            System.out.println("╚═══════════════════════════════════════════════════════════╝");

            int choice = getInt("  Choose item to edit [0-" + items.size() + "]: ", 0, items.size());
            if (choice == 0) return;

            OrderItem item = items.get(choice - 1);
            boolean continueEditing = true;

            while (continueEditing) {
                System.out.println("\n╔═══════════════════════════════════════════════════════════╗");
                System.out.println("║              EDIT OPTIONS FOR ITEM                       ║");
                System.out.println("╠═══════════════════════════════════════════════════════════╣");
                System.out.println("║                                                           ║");
                System.out.println("║    [1]  Change Quantity                                   ║");
                System.out.println("║    [2]  Change Sugar Level                                ║");
                System.out.println("║    [3]  Edit Add-ons                                      ║");
                System.out.println("║    [4]  Back                                              ║");
                System.out.println("║                                                           ║");
                System.out.println("╚═══════════════════════════════════════════════════════════╝");
                int opt = getInt("  Choose edit option [1-4]: ", 1, 4);

                switch (opt) {
                    case 1 -> {
                        int newQty = getInt("  Enter new quantity [1-10]: ", 1, 10);
                        item.quantity = newQty;
                        item.recalculate();
                        System.out.println("  [SUCCESS] Quantity updated.");
                    }
                    case 2 -> {
                        System.out.println("  [1]  Less Sweet");
                        System.out.println("  [2]  Standard");
                        System.out.println("  [3]  Sweet");
                        int s = getInt("  Choice [1-3]: ", 1, 3);
                        item.setSugarLevel(switch (s) {
                            case 1 -> "Less Sweet";
                            case 2 -> "Standard";
                            case 3 -> "Sweet";
                            default -> "Standard";
                        });
                        System.out.println("  [SUCCESS] Sugar level updated.");
                    }
                    case 3 -> {
                        System.out.println("\n╔═══════════════════════════════════════════════════════════╗");
                        System.out.println("║                  EDIT ADD-ONS                             ║");
                        System.out.println("╠═══════════════════════════════════════════════════════════╣");
                        System.out.println("  Current Add-ons:");
                        if (item.getAddOns().isEmpty()) {
                            System.out.println("    (None)");
                        } else {
                            for (AddOn addon : item.getAddOns()) {
                                System.out.println("    • " + addon.name);
                            }
                        }
                        System.out.println("╠═══════════════════════════════════════════════════════════╣");
                        List<AddOn> allAddOns = store.getAddOns();
                        for (int i = 0; i < allAddOns.size(); i++) {
                            System.out.println("  [" + (i + 1) + "]  " + allAddOns.get(i).name + " " + allAddOns.get(i).price + " PHP");
                        }
                        System.out.println("  [" + (allAddOns.size() + 1) + "]  Done");
                        System.out.println("╚═══════════════════════════════════════════════════════════╝");
                        int addonChoice = getInt("  Select add-on or Done: ", 1, allAddOns.size() + 1);
                        if (addonChoice <= allAddOns.size()) {
                            AddOn addon = allAddOns.get(addonChoice - 1);
                            int qty = getInt("  Quantity [1-5]: ", 1, 5);
                            item.addAddOn(addon, qty);
                            System.out.println("  [SUCCESS] Add-on added!");
                        } else {
                            System.out.println("  [INFO] Done editing add-ons.");
                        }
                    }
                    case 4 -> continueEditing = false;
                }
            }

            System.out.print("Edit another item? (y/n): ");
            if (!sc.nextLine().trim().toLowerCase().startsWith("y")) return;
        }
    }

    // ---------------------- CHECKOUT BASKET ----------------------
    static void checkoutBasket() {
        Map<String, Order> pending = store.getPendingOrders();
        if (pending.isEmpty()) {
            System.out.println("Basket is empty.");
            return;
        }

        System.out.println("\nCheckout options:");
        System.out.println("1. Dine In");
        System.out.println("2. Take Out");
        System.out.println("3. Back");
        int choice = getInt("Choice: ", 1, 3);

        if (choice == 1) {
            System.out.println("\n1. Pick at the counter");
            System.out.println("2. Serve");
            System.out.println("3. Back");
            int serveChoice = getInt("Choice: ", 1, 3);
            if (serveChoice == 1 || serveChoice == 2) {
                String type = serveChoice == 1 ? "Dine In - Pick Up at counter" : "Dine In - Serve";
                for (Order o : pending.values()) {
                    o.transactionId = generateTransactionId();
                    showOrderDetails(o.orderNumber, o, type);
                    store.completeOrder(o.orderCode);
                }
            }
        } else if (choice == 2) {
            for (Order o : pending.values()) {
                o.transactionId = generateTransactionId();
                showOrderDetails(o.orderNumber, o, "Take Out");
                store.completeOrder(o.orderCode);
            }
        }
    }

    // ---------------------- ADMIN LOGIN ----------------------
    static void adminLogin() {
        System.out.print("Enter Admin Password: ");
        String pass = sc.nextLine();
        if (pass.equals(adminPassword)) adminMenu();
        else System.out.println("  [ERROR] Access denied. Wrong password!");
    }

    // ---------------------- CASHIER LOGIN ----------------------
    static void cashierLogin() {
        System.out.print("Enter Cashier Username: ");
        String username = sc.nextLine().trim();
        System.out.print("Enter Cashier Password: ");
        String password = sc.nextLine();
        
        // Check if cashier exists in database
        CashierAccount cashier = store.findCashier(username);
        if (cashier == null) {
            System.out.println("\n╔═══════════════════════════════════════════════════════════╗");
            System.out.println("║                     ACCESS DENIED                          ║");
            System.out.println("╠═══════════════════════════════════════════════════════════╣");
            System.out.println("║  [ERROR] Cashier not found!                               ║");
            System.out.println("║  Please verify your username and try again.               ║");
            System.out.println("╚═══════════════════════════════════════════════════════════╝");
            return;
        }
        
        // Check if password matches
        if (password.equals(cashier.password)) {
            if (cashier.isActive()) {
                System.out.println("\n╔═══════════════════════════════════════════════════════════╗");
                System.out.println("║                  LOGIN SUCCESSFUL                         ║");
                System.out.println("╠═══════════════════════════════════════════════════════════╣");
                System.out.println("║  [SUCCESS] Welcome, " + padRight(username, 40) + "║");
                System.out.println("╚═══════════════════════════════════════════════════════════╝");
                currentCashier = username; // Set current cashier
                cashierSystem();
                currentCashier = null; // Clear on logout
            } else {
                System.out.println("\n╔═══════════════════════════════════════════════════════════╗");
                System.out.println("║                     ACCESS DENIED                          ║");
                System.out.println("╠═══════════════════════════════════════════════════════════╣");
                System.out.println("║  [ERROR] This cashier account is currently INACTIVE!     ║");
                System.out.println("║                                                           ║");
                System.out.println("║  Your account has been deactivated. Please contact an    ║");
                System.out.println("║  administrator to activate your account before you can   ║");
                System.out.println("║  access the payment system.                               ║");
                System.out.println("╚═══════════════════════════════════════════════════════════╝");
            }
        } else {
            System.out.println("\n╔═══════════════════════════════════════════════════════════╗");
            System.out.println("║                     ACCESS DENIED                          ║");
            System.out.println("╠═══════════════════════════════════════════════════════════╣");
            System.out.println("║  [ERROR] Wrong password!                                   ║");
            System.out.println("║  Please verify your password and try again.                ║");
            System.out.println("╚═══════════════════════════════════════════════════════════╝");
        }
    }

    // ---------------------- ADMIN MENU ----------------------
    static void adminMenu() {
        AdminModule.adminMenu();
    }

    // ---------------------- CASHIER SYSTEM ----------------------
    static void cashierSystem() {
        CashierModule.cashierSystem();
    }

    // ---------------------- HELPERS ----------------------
    static String padCenter(String text, int width) {
        int padding = width - text.length();
        if (padding <= 0) return text;
        int leftPad = padding / 2;
        int rightPad = padding - leftPad;
        return " ".repeat(leftPad) + text + " ".repeat(rightPad);
    }

    static String padRight(String text, int width) {
        if (text.length() >= width) return text.substring(0, Math.min(width, text.length()));
        return text + " ".repeat(width - text.length());
    }

    static String padSpaces(double amount) {
        String formatted = String.format("%.2f", amount);
        return " ".repeat(Math.max(0, 32 - (formatted.length() + 14)));
    }

    static String padSpaces(int number) {
        String formatted = String.valueOf(number);
        return " ".repeat(Math.max(0, 32 - (formatted.length() + 14)));
    }

    static int getInt(String prompt, int min, int max) {
        int value;
        while (true) {
            try {
                System.out.print(prompt);
                value = Integer.parseInt(sc.nextLine());
                if (value >= min && value <= max) return value;
                else System.out.println("  [ERROR] Enter a valid number between " + min + " and " + max);
            } catch (NumberFormatException e) { System.out.println("  [ERROR] Invalid input. Try again."); }
        }
    }

    // ---------------------- HELPERS FOR TRANSACTION ID ----------------------
    static String generateTransactionId() {
        transactionCounter++;
        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return "TX" + transactionCounter + "-" + ts;
    }

    // ---------------------- ORDER CLASS ----------------------
    public static class Order implements Serializable {
        private static final long serialVersionUID = 1L;
        String orderCode;
        int orderNumber; // for compatibility
        String transactionId;
        String drink;
        int quantity;
        String sugar;
        List<String> addOns;
        Map<String, Integer> addOnsQty;
        double totalPrice;
        double basePricePerUnit; // base price before addons
        List<OrderItem> items; // for multi-item orders
        boolean paid = false;
        java.time.LocalDateTime paymentTime; // Time when payment was received

        public Order(String drink, int quantity, String sugar, List<String> addOns, Map<String,Integer> addOnsQty, double totalPrice, double basePricePerUnit) {
            this.drink = drink; this.quantity = quantity; this.sugar = sugar;
            this.addOns = new ArrayList<>(addOns);
            this.addOnsQty = new HashMap<>(addOnsQty);
            this.basePricePerUnit = basePricePerUnit;
            this.totalPrice = totalPrice;
            this.orderCode = "";
            this.transactionId = null;
            this.paid = false;
            this.items = new ArrayList<>();
        }

        void recalcTotal() {
            double addonsTotal = 0.0;
            for (Integer v : addOnsQty.values()) addonsTotal += v * 10.0;
            this.totalPrice = basePricePerUnit * quantity + addonsTotal;
        }

        void printSummary() {
            // If multi-item order, print all items
            if (items != null && !items.isEmpty()) {
                for (OrderItem item : items) {
                    System.out.println("║  🥤 Drink: " + item.product.name);
                    System.out.println("║     └─ Size/Variant & Qty: x" + item.quantity);
                    System.out.println("║     └─ Unit Price: ₱" + String.format("%.2f", item.product.price));
                    
                    if (!item.getAddOns().isEmpty()) {
                        System.out.println("║  ➕ Add-ons:");
                        for (AddOn addon : item.getAddOns()) {
                            int qty = item.getAddOnQuantity(addon);
                            System.out.println("║     • " + addon.name + " x" + qty + " = ₱" + (qty * 10));
                        }
                    }
                    System.out.println("║  🍯 Sugar Level: " + item.sugarLevel);
                    System.out.println("║  💰 Item Total: ₱" + String.format("%.2f", item.getTotal()));
                    System.out.println("║");
                }
            } else {
                // Single item order (legacy)
                System.out.println("║  🥤 Drink: " + drink);
                System.out.println("║     └─ Size/Variant & Qty: x" + quantity + "");
                System.out.println("║     └─ Unit Price: ₱" + String.format("%.2f", basePricePerUnit));
                // (unit price displayed above)

                if (!addOns.isEmpty()) {
                    System.out.println("║  ➕ Add-ons:");
                    // show each unique addon and its qty
                    List<String> unique = new ArrayList<>(new LinkedHashSet<>(addOns));
                    for (String a : unique) {
                        int qty = addOnsQty.getOrDefault(a, 0);
                        System.out.println("║     • " + a + " x" + qty + " = ₱" + (qty * 10));
                    }
                }
                System.out.println("║  🍯 Sugar Level: " + sugar);
                System.out.println("║  💰 Item Total: ₱" + String.format("%.2f", totalPrice));
            }
        }
    }
}
