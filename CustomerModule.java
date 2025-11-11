import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

public class CustomerModule {
    public static void customerMode() {
        Store store = BrewiseCoffeeShop.store;
        List<String> categories = store.getCategories();
        
        while (true) {
            System.out.println("\n╔════════════════════════════════════════╗");
            System.out.println("║           ✨ CUSTOMER MENU ✨            ║");
            System.out.println("╠════════════════════════════════════════╣");
            
            // Show all product categories dynamically
            for (int i = 0; i < categories.size(); i++) {
                System.out.printf("║    %d  %-32s ║%n", (i + 1), categories.get(i));
            }
            System.out.printf("║    %d  %-32s ║%n", (categories.size() + 1), "Back");
            System.out.println("╚════════════════════════════════════════╝");
            
            int choice = BrewiseCoffeeShop.getInt("Enter your choice [1-" + (categories.size() + 1) + "]: ", 
                                                 1, categories.size() + 1);

            if (choice == categories.size() + 1) return;
            
            // Show menu for selected category
            String selectedCategory = categories.get(choice - 1);
            productMenu(selectedCategory);

            // If deeper menu requested return to main menu, do it
            if (BrewiseCoffeeShop.returnToMain) {
                BrewiseCoffeeShop.returnToMain = false;
                return;
            }
        }
    }

    private static void productMenu(String category) {
        Store store = BrewiseCoffeeShop.store;
        List<Product> products = store.getProductsByCategory(category);
        
        while (true) {
            System.out.println("\n╔════════════════════════════════════════╗");
            System.out.println("║          " + BrewiseCoffeeShop.padCenter(category.toUpperCase(), 20) + "         ║");
            System.out.println("╠════════════════════════════════════════╣");
            
            // Show all products in this category
            for (int i = 0; i < products.size(); i++) {
                Product p = products.get(i);
                System.out.printf("║    %d  %-20s  ₱%-7.2f  ║%n", (i + 1), p.name, p.price);
            }
            System.out.printf("║    %d  %-32s ║%n", (products.size() + 1), "Back");
            System.out.println("╚════════════════════════════════════════╝");
            
            int choice = BrewiseCoffeeShop.getInt("Enter your choice [1-" + (products.size() + 1) + "]: ", 
                                                 1, products.size() + 1);
            
            if (choice == products.size() + 1) return;
            
            Product selected = products.get(choice - 1);
            customizeProduct(selected);
            
            // If order added to basket, unwind to main menu
            if (BrewiseCoffeeShop.returnToMain) return;
        }
    }

    private static void customizeProduct(Product product) {
        Store store = BrewiseCoffeeShop.store;
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║           🛠️ CUSTOMIZE ORDER 🛠️         ║");
        System.out.println("╠════════════════════════════════════════╣");
        System.out.println("║  " + product.name);
        System.out.println("║  Price: ₱" + product.price);
        System.out.println("║  " + product.description);
        System.out.println("╠════════════════════════════════════════╣");
        
        int quantity = BrewiseCoffeeShop.getInt("Quantity [1-10]: ", 1, 10);
        
        // Get sugar level
        System.out.println("\nSugar Level:");
        System.out.println("1. Less Sweet");
        System.out.println("2. Standard");
        System.out.println("3. Sweet");
        int sugarChoice = BrewiseCoffeeShop.getInt("Choice [1-3]: ", 1, 3);
        String sugar = switch (sugarChoice) {
            case 1 -> "Less Sweet";
            case 2 -> "Standard";
            default -> "Sweet";
        };
        
        // Create OrderItem
        OrderItem item = new OrderItem(product, quantity);
        item.setSugarLevel(sugar);
        
        // Handle add-ons
        while (true) {
            System.out.println("\n╔════════════════════════════════════════╗");
            System.out.println("║                 ADD ONS                ║");
            System.out.println("╠════════════════════════════════════════╣");
            System.out.println("║  Current Total: ₱" + item.getTotal());
            
            if (!item.getAddOns().isEmpty()) {
                System.out.println("╠════════════════════════════════════════╣");
                System.out.println("║  Selected Add-ons:");
                for (AddOn addon : item.getAddOns()) {
                    System.out.printf("║    • %s - ₱%.2f%n", addon.name, addon.price);
                }
            }
            
            System.out.println("╠════════════════════════════════════════╣");
            List<AddOn> availableAddOns = store.getAddOns();
            for (int i = 0; i < availableAddOns.size(); i++) {
                AddOn addon = availableAddOns.get(i);
                System.out.printf("║    %d  %-20s  ₱%-7.2f  ║%n", 
                                (i + 1), addon.name, addon.price);
            }
            System.out.printf("║    %d  %-32s ║%n", 
                            (availableAddOns.size() + 1), "Done");
            System.out.println("╚════════════════════════════════════════╝");
            
            int choice = BrewiseCoffeeShop.getInt("Choice [1-" + (availableAddOns.size() + 1) + "]: ", 
                                                 1, availableAddOns.size() + 1);
            
            if (choice == availableAddOns.size() + 1) break;
            
            // Add selected add-on
            AddOn selected = availableAddOns.get(choice - 1);
            int addonQty = BrewiseCoffeeShop.getInt("Quantity [1-5]: ", 1, 5);
            item.addAddOn(selected, addonQty);
        }
        
        // Confirm and create order
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║         📝 ORDER SUMMARY 📝           ║");
        System.out.println("╠════════════════════════════════════════╣");
        System.out.println("║  🥤 " + product.name);
        System.out.println("║     └─ Quantity: x" + quantity);
        System.out.println("║     └─ Unit Price: ₱" + product.price);
        System.out.println("║  🍯 Sugar Level: " + sugar);
        
        if (!item.getAddOns().isEmpty()) {
            System.out.println("║  ➕ Add-ons:");
            for (AddOn addon : item.getAddOns()) {
                System.out.printf("║     • %s - ₱%.2f%n", addon.name, addon.price);
            }
        }
        
        System.out.println("║  💰 Total: ₱" + item.getTotal());
        System.out.println("╠════════════════════════════════════════╣");
        System.out.println("║    1 🛒 Add to Basket                 ║");
        System.out.println("║    2 💳 Checkout Now                  ║");
        System.out.println("║    3 ❌ Cancel                        ║");
        System.out.println("╚════════════════════════════════════════╝");
        
        int choice = BrewiseCoffeeShop.getInt("Choice [1-3]: ", 1, 3);
        
        String orderName = product.name + " (" + product.flavour + ")";
        List<String> addons = new ArrayList<>();
        Map<String, Integer> addonQtys = new HashMap<>();
        
        for (AddOn addon : item.getAddOns()) {
            addons.add(addon.name);
            addonQtys.put(addon.name, item.getAddOnQuantity(addon));
        }
        
        Order order = new Order(
            orderName,
            quantity,
            sugar,
            addons,
            addonQtys,
            item.getTotal(),
            product.price
        );
        
        switch (choice) {
            case 1:
                // Add to basket
                BrewiseCoffeeShop.Order basketOrder = new BrewiseCoffeeShop.Order(order.drink, order.quantity, order.sugar, order.addOns, order.addOnsQty, order.totalPrice, order.basePricePerUnit);
                basketOrder.orderNumber = Integer.parseInt(store.generateOrderCode().substring(3));
                store.savePendingOrder(basketOrder);
                System.out.println("✅ Added to basket! Your order number is: " + basketOrder.orderNumber);
                System.out.println("Please give this ORDER NUMBER to the cashier when paying.");
                System.out.println("Copy this code: " + basketOrder.orderNumber);
                BrewiseCoffeeShop.returnToMain = true;
                break;
            case 2:
                // Direct checkout
                store.savePendingOrder(new BrewiseCoffeeShop.Order(order.drink, order.quantity, order.sugar, order.addOns, order.addOnsQty, order.totalPrice, order.basePricePerUnit));
                BrewiseCoffeeShop.checkoutOrder(new BrewiseCoffeeShop.Order(order.drink, order.quantity, order.sugar, order.addOns, order.addOnsQty, order.totalPrice, order.basePricePerUnit));
                break;
            default:
                System.out.println("❌ Order cancelled.");
                break;
        }
    }
}
