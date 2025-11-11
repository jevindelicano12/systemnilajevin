import java.util.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BrewiseCoffeeShop {
    static Scanner sc = new Scanner(System.in);
    static String adminPassword = "admin123";
    static String cashierPassword = "cashier123";
    static Store store;

    // When true, unwind all the way back to main menu
    static boolean returnToMain = false;
    private static int transactionCounter = 5000;

    public static void main(String[] args) {
        // Create or load store
        store = PersistenceManager.loadStore();
        if (store == null) {
            store = new Store();
        }
        mainMenu();
    }

    // ---------------------- MAIN MENU ----------------------
    static void mainMenu() {
        while (true) {
            System.out.println("\n╔════════════════════════════════════════╗");
            System.out.println("║           BREWISE COFFEE SHOP          ║");
            System.out.println("╠════════════════════════════════════════╣");
            System.out.println("║    1  View Menu                        ║");
            System.out.println("║    2  View/Edit Basket                 ║");
            System.out.println("║    3  Checkout                         ║");
            System.out.println("║    4  Exit                             ║");
            System.out.println("╚════════════════════════════════════════╝");
            System.out.print("Enter your choice [1-4]: ");
            String input = sc.nextLine();

            switch (input) {
                case "1":
                    customerMode();
                    break;
                case "2":
                    viewBasket();
                    break;
                case "3":
                    checkoutBasket();
                    break;
                case "4":
                    System.out.println("Thank you for visiting Brewise Coffee Shop!");
                    return;
                case "admin:login":
                    adminLogin();
                    break;
                case "cashier:login":
                    cashierLogin();
                    break;
                default:
                    System.out.println("❌ Invalid input. Please try again.");
            }
        }
    }

    // ---------------------- CUSTOMER MODE ----------------------
    static void customerMode() {
        CustomerModule.customerMode();
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
        System.out.println("║         📝 ORDER SUMMARY 📝           ║");
        System.out.println("╠════════════════════════════════════════╣");
        order.printSummary();
        System.out.println("╠════════════════════════════════════════╣");
        System.out.println("║    1 🛒 Add to Basket                 ║");
        System.out.println("║    2 💳 Checkout                      ║");
        System.out.println("║    3 ❌ Cancel                        ║");
        System.out.println("╚════════════════════════════════════════╝");
        int choice = getInt("Enter your choice [1-3]: ", 1, 3);

        switch (choice) {
            case 1:
                order.orderNumber = Integer.parseInt(store.generateOrderCode().substring(3));
                store.savePendingOrder(order);
                System.out.println("✅ Added to basket. Your order number is: " + order.orderNumber);
                System.out.println("Please give this ORDER NUMBER to the cashier when paying.");
                System.out.println("Copy this code: " + order.orderNumber);
                // signal to unwind all menus back to main welcome screen
                returnToMain = true;
                break;
            case 2:
                checkoutOrder(order);
                break;
            case 3:
                System.out.println("❌ Order cancelled.");
                break;
        }
    }

    // ---------------------- CHECKOUT ORDER ----------------------
    static void checkoutOrder(BrewiseCoffeeShop.Order order) {
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
        System.out.println("Order Number: " + orderNumber);
        System.out.println("Type: " + type);
        order.printSummary();
        System.out.println("Your unique order code is: " + orderNumber);
        System.out.println("Please give this ORDER CODE to the cashier when paying.");
        System.out.println("Proceed to cashier!");
    }

    // ---------------------- VIEW BASKET ----------------------
    static void viewBasket() {
        while (true) {
            System.out.println("\n╔════════════════════════════════════════╗");
            System.out.println("║           🛒 YOUR BASKET 🛒           ║");
            System.out.println("╠════════════════════════════════════════╣");
            
            Map<String, BrewiseCoffeeShop.Order> pending = store.getPendingOrders();
            if (pending.isEmpty()) {
                System.out.println("║         Your basket is empty!          ║");
                System.out.println("╠════════════════════════════════════════╣");
            } else {
                double grandTotal = 0;
                List<Order> orders = new ArrayList<>(pending.values());
                for (int i = 0; i < orders.size(); i++) {
                    if (i > 0) System.out.println("╟────────────────────────────────────────╢");
                    System.out.println("║  📝 Order #" + orders.get(i).orderNumber);
                    orders.get(i).printSummary();
                    grandTotal += orders.get(i).totalPrice;
                }
                System.out.println("╠════════════════════════════════════════╣");
                System.out.println("║  💰 GRAND TOTAL: ₱" + String.format("%.2f", grandTotal) + padSpaces(grandTotal) + "║");
                System.out.println("║  📦 Total Items: " + orders.size() + padSpaces(orders.size()) + "║");
                System.out.println("╠════════════════════════════════════════╣");
            }
            
            System.out.println("║    1 ➕ Add More Items                 ║");
            System.out.println("║    2 ❌ Remove Item                    ║");
            System.out.println("║    3 ✏️ Edit Item                     ║");
            System.out.println("║    4 💳 Checkout                      ║");
            System.out.println("║    5 ◀️ Back                         ║");
            System.out.println("╚════════════════════════════════════════╝");

            int choice = getInt("Enter your choice [1-5]: ", 1, 5);

            switch (choice) {
                case 1:
                    customerMode();
                    break;
                case 2:
                    if (!pending.isEmpty()) removeFromBasket();
                    else System.out.println("❌ Basket is empty! Nothing to remove.");
                    break;
                case 3:
                    if (!pending.isEmpty()) editFromBasket();
                    else System.out.println("❌ Basket is empty! Nothing to edit.");
                    break;
                case 4:
                    if (!pending.isEmpty()) {
                        checkoutBasket();
                        return;
                    } else System.out.println("❌ Basket is empty! Nothing to checkout.");
                    break;
                case 5:
                    return;
            }
        }
    }
    
    // ---------------------- REMOVE FROM BASKET ----------------------
    static void removeFromBasket() {
        while (true) {
            System.out.println("\n╔════════════════════════════════════════╗");
            System.out.println("║         🗑️ REMOVE FROM BASKET 🗑️        ║");
            System.out.println("╠════════════════════════════════════════╣");
            
            List<BrewiseCoffeeShop.Order> orders = new ArrayList<>(store.getPendingOrders().values());
            for (int i = 0; i < orders.size(); i++) {
                if (i > 0) System.out.println("╟────────────────────────────────────────╢");
                System.out.println("║  " + (i + 1) + ". Order #" + orders.get(i).orderNumber);
                orders.get(i).printSummary();
            }
            
            System.out.println("╠════════════════════════════════════════╣");
            System.out.println("║    Enter 0 to cancel                   ║");
            System.out.println("╚════════════════════════════════════════╝");
            
            int choice = getInt("Choose item to remove [0-" + orders.size() + "]: ", 0, orders.size());
            if (choice == 0) return;
            
            Order removed = orders.get(choice - 1);
            store.removePendingOrder(String.valueOf(removed.orderNumber));
            System.out.println("✅ Item removed from basket!");
            
            if (store.getPendingOrders().isEmpty()) return;
            
            System.out.print("Remove another item? (y/n): ");
            if (!sc.nextLine().trim().toLowerCase().startsWith("y")) return;
        }
    }

    // ---------------------- EDIT BASKET ITEM ----------------------
    static void editFromBasket() {
        while (true) {
            System.out.println("\n╔════════════════════════════════════════╗");
            System.out.println("║           ✏️ EDIT BASKET ITEM ✏️       ║");
            System.out.println("╠════════════════════════════════════════╣");

            List<BrewiseCoffeeShop.Order> orders = new ArrayList<>(store.getPendingOrders().values());
            for (int i = 0; i < orders.size(); i++) {
                if (i > 0) System.out.println("╟────────────────────────────────────────╢");
                System.out.println("║  " + (i + 1) + ". Order #" + orders.get(i).orderNumber);
                orders.get(i).printSummary();
            }

            System.out.println("╠════════════════════════════════════════╣");
            System.out.println("║    Enter 0 to cancel                   ║");
            System.out.println("╚════════════════════════════════════════╝");

            int choice = getInt("Choose item to edit [0-" + orders.size() + "]: ", 0, orders.size());
            if (choice == 0) return;

            Order o = orders.get(choice - 1);
            boolean continueEditing = true;

            while (continueEditing) {
                System.out.println("\n╔════════════════════════════════════════╗");
                System.out.println("║         🛠️ Edit Options for Item 🛠️     ║");
                System.out.println("╠════════════════════════════════════════╣");
                System.out.println("║    1 🔢 Change Quantity               ║");
                System.out.println("║    2 🍯 Change Sugar Level            ║");
                System.out.println("║    3 ➕/➖ Edit Add-ons                 ║");
                System.out.println("║    4 ◀️ Back                         ║");
                System.out.println("╚════════════════════════════════════════╝");
                int opt = getInt("Choose edit option [1-4]: ", 1, 4);

                switch (opt) {
                    case 1 -> {
                        int newQty = getInt("Enter new quantity: ", 1, 100);
                        o.quantity = newQty;
                        o.recalcTotal();
                        store.savePendingOrder(o);
                        System.out.println("✅ Quantity updated.");
                    }
                    case 2 -> {
                        System.out.println("1 Less sweet\n2 Standard\n3 Sweet");
                        int s = getInt("Choice: ", 1, 3);
                        o.sugar = switch (s) {
                            case 1 -> "Less Sweet";
                            case 2 -> "Standard";
                            default -> "Sweet";
                        };
                        store.savePendingOrder(o);
                        System.out.println("✅ Sugar level updated.");
                    }
                    case 3 -> {
                        // Edit add-ons
                        System.out.println("1 Add add-on\n2 Remove add-on\n3 Back");
                        int aopt = getInt("Choice: ", 1, 3);
                        if (aopt == 1) {
                            System.out.println("1 Nata\n2 Pearl");
                            int which = getInt("Which add-on to add: ", 1, 2);
                            int qty = getInt("Quantity to add: ", 1, 20);
                            String name = (which == 1) ? "Nata" : "Pearl";
                            o.addOns.add(name);
                            o.addOnsQty.put(name, o.addOnsQty.getOrDefault(name, 0) + qty);
                            o.recalcTotal();
                            store.savePendingOrder(o);
                            System.out.println("✅ Add-on added.");
                        } else if (aopt == 2) {
                            if (o.addOns.isEmpty()) {
                                System.out.println("No add-ons to remove.");
                            } else {
                                List<String> unique = new ArrayList<>(new LinkedHashSet<>(o.addOns));
                                for (int i = 0; i < unique.size(); i++) {
                                    System.out.println((i + 1) + ". " + unique.get(i) + " x" + o.addOnsQty.get(unique.get(i)));
                                }
                                int wi = getInt("Which to remove [1-" + unique.size() + "]: ", 1, unique.size());
                                String name = unique.get(wi - 1);
                                int cur = o.addOnsQty.getOrDefault(name, 0);
                                int rem = getInt("Quantity to remove (max " + cur + "): ", 1, cur);
                                int left = cur - rem;
                                if (left <= 0) {
                                    o.addOnsQty.remove(name);
                                    // remove all occurrences in list
                                    o.addOns.removeIf(x -> x.equals(name));
                                } else {
                                    o.addOnsQty.put(name, left);
                                }
                                o.recalcTotal();
                                store.savePendingOrder(o);
                                System.out.println("✅ Add-on updated.");
                            }
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
        Map<String, BrewiseCoffeeShop.Order> pending = store.getPendingOrders();
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
                    // Generate unique order number if not already set
                    if (o.orderNumber == 0) {
                        o.orderNumber = Integer.parseInt(store.generateOrderCode().substring(3));
                    }
                    o.transactionId = generateTransactionId();
                    showOrderDetails(o.orderNumber, o, type);
                    store.completeOrder(String.valueOf(o.orderNumber));
                }
            }
        } else if (choice == 2) {
            for (Order o : pending.values()) {
                // Generate unique order number if not already set
                if (o.orderNumber == 0) {
                    o.orderNumber = Integer.parseInt(store.generateOrderCode().substring(3));
                }
                o.transactionId = generateTransactionId();
                showOrderDetails(o.orderNumber, o, "Take Out");
                store.completeOrder(String.valueOf(o.orderNumber));
            }
        }
    }

    // ---------------------- ADMIN LOGIN ----------------------
    static void adminLogin() {
        System.out.print("Enter Admin Password: ");
        String pass = sc.nextLine();
        if (pass.equals(adminPassword)) adminMenu();
        else System.out.println("❌ Access denied. Wrong password!");
    }

    // ---------------------- CASHIER LOGIN ----------------------
    static void cashierLogin() {
        System.out.print("Enter Cashier Password: ");
        String pass = sc.nextLine();
        if (pass.equals(cashierPassword)) cashierSystem();
        else System.out.println("❌ Access denied. Wrong password!");
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
                else System.out.println("❌ Enter a valid number between " + min + " and " + max);
            } catch (NumberFormatException e) { System.out.println("❌ Invalid input. Try again."); }
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
        int orderNumber;
        String transactionId;
        String drink;
        int quantity;
        String sugar;
        List<String> addOns;
        Map<String, Integer> addOnsQty;
        double totalPrice;
        double basePricePerUnit; // base price before addons
        boolean paid = false;

        public Order(String drink, int quantity, String sugar, List<String> addOns, Map<String,Integer> addOnsQty, double totalPrice, double basePricePerUnit) {
            this.drink = drink; this.quantity = quantity; this.sugar = sugar;
            this.addOns = new ArrayList<>(addOns);
            this.addOnsQty = new HashMap<>(addOnsQty);
            this.basePricePerUnit = basePricePerUnit;
            this.totalPrice = totalPrice;
            this.transactionId = null;
            this.paid = false;
        }

        void recalcTotal() {
            double addonsTotal = 0.0;
            for (Integer v : addOnsQty.values()) addonsTotal += v * 10.0;
            this.totalPrice = basePricePerUnit * quantity + addonsTotal;
        }

        void printSummary() {
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
