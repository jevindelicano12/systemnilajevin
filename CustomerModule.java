import java.util.List;

public class CustomerModule {
    public static void customerMode() {
        Store store = BrewiseCoffeeShop.store;
        List<String> categories = store.getCategories();
        
        while (true) {
            System.out.println("\n╔═══════════════════════════════════════════════════════════╗");
            System.out.println("║         CUSTOMER MENU                                     ║");
            System.out.println("╠═══════════════════════════════════════════════════════════╣");
            System.out.println("║                                                           ║");
            
            // Show all product categories dynamically without individual choice numbers
            for (int i = 0; i < categories.size(); i++) {
                String line = String.format("║  [%d]  %s║", (i + 1), BrewiseCoffeeShop.padRight(categories.get(i), 49));
                System.out.println(line);
            }
            
            String backLine = String.format("║  [%d]  %s║", (categories.size() + 1), BrewiseCoffeeShop.padRight("Back", 49));
            System.out.println(backLine);
            System.out.println("║                                                           ║");
            System.out.println("╚═══════════════════════════════════════════════════════════╝");
            
            int choice = BrewiseCoffeeShop.getInt("  Enter your choice [1-" + (categories.size() + 1) + "]: ", 
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
            System.out.println("\n╔═══════════════════════════════════════════════════════════╗");
            System.out.println("║  " + BrewiseCoffeeShop.padCenter(category.toUpperCase(), 55) + "║");
            System.out.println("╠═══════════════════════════════════════════════════════════╣");
            System.out.println("║                                                           ║");
            
            // Show all products in this category with description style
            for (int i = 0; i < products.size(); i++) {
                Product p = products.get(i);
                
                // Create product line: [1]  Product Name.............................PHP 120.00
                String choiceStr = "[" + (i + 1) + "]";
                String priceStr = "PHP " + String.format("%.2f", p.price);
                
                // Available space in box: 61 chars - 2 (║║) - 2 (spaces) - 2 (choice bracket) - 3 (spaces) = 51 chars
                // Space for: name + dots + price
                int totalSpace = 49;  // 61 - 2 (║) - 2 (two spaces at start) - 2 (closing space and ║)
                int dotsSpace = totalSpace - p.name.length() - priceStr.length();
                String dots = ".".repeat(Math.max(0, dotsSpace));
                
                System.out.printf("║  %s  %s%s%s║%n", choiceStr, p.name, dots, priceStr);
                
                // Show description on next line
                String desc = p.description;
                if (desc.length() > 51) {
                    desc = desc.substring(0, 48) + "...";
                }
                System.out.printf("║       %s║%n", BrewiseCoffeeShop.padRight(desc, 51));
                System.out.println("║                                                           ║");
            }
            
            // Back option
            String choiceStr = "[" + (products.size() + 1) + "]";
            String backStr = "Back";
            String backUpper = "BACK";
            int backDotsSpace = 49 - backStr.length() - backUpper.length();
            String backDots = ".".repeat(Math.max(0, backDotsSpace));
            
            System.out.printf("║  %s  %s%s%s║%n", choiceStr, backStr, backDots, backUpper);
            System.out.println("║                                                           ║");
            System.out.println("╚═══════════════════════════════════════════════════════════╝");
            
            int choice = BrewiseCoffeeShop.getInt("  Enter your choice [1-" + (products.size() + 1) + "]: ", 
                                                 1, products.size() + 1);
            
            if (choice == products.size() + 1) return;
            
            Product selected = products.get(choice - 1);
            customizeProduct(selected, category);
            
            // If order added to basket, unwind to main menu
            if (BrewiseCoffeeShop.returnToMain) return;
        }
    }

    private static void customizeProduct(Product product, String category) {
        Store store = BrewiseCoffeeShop.store;
        System.out.println("\n╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║                   CUSTOMIZE ORDER                         ║");
        System.out.println("╠═══════════════════════════════════════════════════════════╣");
        System.out.println("  Product: " + product.name);
        System.out.println("  Price: " + product.price + " PHP");
        System.out.println("  " + product.description);
        System.out.println("╠═══════════════════════════════════════════════════════════╣");
        
        int quantity = BrewiseCoffeeShop.getInt("  Quantity [1-10]: ", 1, 10);
        
        // Get sugar level
        System.out.println("\n  Sugar Level:");
        System.out.println("  [1]  Less Sweet");
        System.out.println("  [2]  Standard");
        System.out.println("  [3]  Sweet");
        int sugarChoice = BrewiseCoffeeShop.getInt("  Choice [1-3]: ", 1, 3);
        String sugar = switch (sugarChoice) {
            case 1 -> "Less Sweet";
            case 2 -> "Standard";
            default -> "Sweet";
        };
        
        // Create OrderItem
        OrderItem item = new OrderItem(product, quantity);
        item.setSugarLevel(sugar);
        
        // Handle add-ons (context-aware based on product category)
        while (true) {
            System.out.println("\n╔═══════════════════════════════════════════════════════════╗");
            System.out.println("║  ADD-ONS FOR " + BrewiseCoffeeShop.padCenter(category, 41) + "║");
            System.out.println("╠═══════════════════════════════════════════════════════════╣");
            System.out.println("  Current Total: " + String.format("%.2f", item.getTotal()) + " PHP");
            
            if (!item.getAddOns().isEmpty()) {
                System.out.println("╠═══════════════════════════════════════════════════════════╣");
                System.out.println("  Selected Add-ons:");
                for (AddOn addon : item.getAddOns()) {
                    System.out.printf("    • %s - %.2f PHP%n", addon.name, addon.price);
                }
            }
            
            // Get context-aware add-ons for this product category
            List<AddOn> contextAddOns = store.getAddOnsForCategory(category);
            
            System.out.println("╠═══════════════════════════════════════════════════════════╣");
            if (contextAddOns.isEmpty()) {
                System.out.println("  No add-ons available for this item");
            } else {
                for (int i = 0; i < contextAddOns.size(); i++) {
                    AddOn addon = contextAddOns.get(i);
                    System.out.printf("    [%d]  %-40s %.2f PHP%n", 
                                    (i + 1), addon.name, addon.price);
                }
            }
            System.out.printf("    [%d]  %s%n", 
                            (contextAddOns.size() + 1), "Done");
            System.out.println("╚═══════════════════════════════════════════════════════════╝");
            
            int choice = BrewiseCoffeeShop.getInt("  Choice [1-" + (contextAddOns.size() + 1) + "]: ", 
                                                 1, contextAddOns.size() + 1);
            
            if (choice == contextAddOns.size() + 1) break;
            
            // Add selected add-on
            AddOn selected = contextAddOns.get(choice - 1);
            int addonQty = BrewiseCoffeeShop.getInt("  Quantity [1-5]: ", 1, 5);
            item.addAddOn(selected, addonQty);
        }
        
        // Confirm and create order
        System.out.println("\n╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║                  ORDER SUMMARY                            ║");
        System.out.println("╠═══════════════════════════════════════════════════════════╣");
        System.out.println("  Item: " + product.name);
        System.out.println("  Quantity: x" + quantity);
        System.out.println("  Unit Price: " + product.price + " PHP");
        System.out.println("  Sugar Level: " + sugar);
        
        if (!item.getAddOns().isEmpty()) {
            System.out.println("  Add-ons:");
            for (AddOn addon : item.getAddOns()) {
                System.out.printf("    • %s - %.2f PHP%n", addon.name, addon.price);
            }
        }
        
        System.out.println("  TOTAL: " + String.format("%.2f", item.getTotal()) + " PHP");
        System.out.println("╠═══════════════════════════════════════════════════════════╣");
        System.out.println("║                                                           ║");
        System.out.println("║    [1]  Add to Basket                                     ║");
        System.out.println("║    [2]  Checkout Now                                      ║");
        System.out.println("║    [3]  Cancel                                            ║");
        System.out.println("║                                                           ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        
        int choice = BrewiseCoffeeShop.getInt("  Choice [1-3]: ", 1, 3);
        
        if (choice == 1) {
            // Add to basket
            store.addToBasket(item);
            System.out.println("  [SUCCESS] Added to basket!");
            BrewiseCoffeeShop.returnToMain = true;
        } else if (choice == 2) {
            // Direct checkout
            store.addToBasket(item);
            java.util.List<String> orderCodes = store.checkoutBasket();
            System.out.println("\n╔═══════════════════════════════════════════════════════════╗");
            System.out.println("║               ORDER CONFIRMED!                            ║");
            System.out.println("╠═══════════════════════════════════════════════════════════╣");
            System.out.println("║  Your Order Number(s):                                    ║");
            for (String code : orderCodes) {
                System.out.println("║                                                           ║");
                System.out.println("║           ORDER: " + code);
                System.out.println("║                                                           ║");
            }
            System.out.println("╠═══════════════════════════════════════════════════════════╣");
            System.out.println("║  Please show this number to the cashier to complete      ║");
            System.out.println("║  your payment!                                            ║");
            System.out.println("╚═══════════════════════════════════════════════════════════╝");
            BrewiseCoffeeShop.returnToMain = true; // Exit back to main menu after checkout
        } else {
            System.out.println("  [INFO] Order cancelled.");
        }
    }
}
