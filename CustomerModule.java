import java.util.List;

public class CustomerModule {
    public static void customerMode() {
        Store store = BrewiseCoffeeShop.store;
        List<String> categories = store.getCategories();
        
        while (true) {
            System.out.println("\n" + ColorConstants.BORDER + "╔═══════════════════════════════════════════════════════════╗" + ColorConstants.RESET);
            System.out.println(ColorConstants.BORDER + "║" + ColorConstants.RESET + ColorConstants.MENU_TITLE + "         CUSTOMER MENU                                     " + ColorConstants.RESET + ColorConstants.BORDER + "║" + ColorConstants.RESET);
            System.out.println(ColorConstants.BORDER + "╠═══════════════════════════════════════════════════════════╣" + ColorConstants.RESET);
            System.out.println(ColorConstants.BORDER + "║" + ColorConstants.RESET + "                                                           " + ColorConstants.BORDER + "║" + ColorConstants.RESET);
            
            // Show all product categories dynamically without individual choice numbers
            for (int i = 0; i < categories.size(); i++) {
                String line = String.format(ColorConstants.BORDER + "║" + ColorConstants.RESET + "  " + ColorConstants.MENU_ITEM + "[%d]" + ColorConstants.RESET + "  %s" + ColorConstants.BORDER + "║" + ColorConstants.RESET, 
                    (i + 1), BrewiseCoffeeShop.padRight(categories.get(i), 49));
                System.out.println(line);
            }
            
            String backLine = String.format(ColorConstants.BORDER + "║" + ColorConstants.RESET + "  " + ColorConstants.WARNING + "[%d]" + ColorConstants.RESET + "  %s" + ColorConstants.BORDER + "║" + ColorConstants.RESET, 
                (categories.size() + 1), BrewiseCoffeeShop.padRight("Back", 49));
            System.out.println(backLine);
            System.out.println(ColorConstants.BORDER + "║" + ColorConstants.RESET + "                                                           " + ColorConstants.BORDER + "║" + ColorConstants.RESET);
            System.out.println(ColorConstants.BORDER + "╚═══════════════════════════════════════════════════════════╝" + ColorConstants.RESET);
            
            int choice = BrewiseCoffeeShop.getInt(ColorConstants.INPUT_PROMPT + "  Enter your choice [1-" + (categories.size() + 1) + "]: " + ColorConstants.RESET, 
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
            System.out.println("\n" + ColorConstants.BORDER + "╔═══════════════════════════════════════════════════════════╗" + ColorConstants.RESET);
            System.out.println(ColorConstants.BORDER + "║" + ColorConstants.RESET + "  " + BrewiseCoffeeShop.padCenter(ColorConstants.HEADER + category.toUpperCase() + ColorConstants.RESET, 55) + ColorConstants.BORDER + "║" + ColorConstants.RESET);
            System.out.println(ColorConstants.BORDER + "╠═══════════════════════════════════════════════════════════╣" + ColorConstants.RESET);
            System.out.println(ColorConstants.BORDER + "║" + ColorConstants.RESET + "                                                           " + ColorConstants.BORDER + "║" + ColorConstants.RESET);
            
            // Show all products in this category with description style
            for (int i = 0; i < products.size(); i++) {
                Product p = products.get(i);
                
                // Create product line: [1]  Product Name.............................PHP 120.00
                String choiceStr = ColorConstants.MENU_ITEM + "[" + (i + 1) + "]" + ColorConstants.RESET;
                String priceStr = ColorConstants.PRICE + "PHP " + String.format("%.2f", p.price) + ColorConstants.RESET;
                
                // Available space in box: 61 chars - 2 (║║) - 2 (spaces) - 2 (choice bracket) - 3 (spaces) = 51 chars
                // Space for: name + dots + price
                int totalSpace = 49;  // 61 - 2 (║) - 2 (two spaces at start) - 2 (closing space and ║)
                int dotsSpace = totalSpace - p.name.length() - priceStr.length() + ColorConstants.PRICE.length() + ColorConstants.RESET.length();
                String dots = ColorConstants.BORDER + ".".repeat(Math.max(0, dotsSpace - 10)) + ColorConstants.RESET;
                
                System.out.printf(ColorConstants.BORDER + "║" + ColorConstants.RESET + "  %s  %s%s%s" + ColorConstants.BORDER + "║" + ColorConstants.RESET + "%n", choiceStr, p.name, dots, priceStr);
                
                // Show description on next line
                String desc = p.description;
                if (desc.length() > 51) {
                    desc = desc.substring(0, 48) + "...";
                }
                System.out.printf(ColorConstants.BORDER + "║" + ColorConstants.RESET + "       %s" + ColorConstants.BORDER + "║" + ColorConstants.RESET + "%n", BrewiseCoffeeShop.padRight(desc, 51));
                System.out.println(ColorConstants.BORDER + "║" + ColorConstants.RESET + "                                                           " + ColorConstants.BORDER + "║" + ColorConstants.RESET);
            }
            
            // Back option
            String choiceStr = ColorConstants.WARNING + "[" + (products.size() + 1) + "]" + ColorConstants.RESET;
            String backStr = "Back";
            String backUpper = "BACK";
            int backDotsSpace = 49 - backStr.length() - backUpper.length();
            String backDots = ".".repeat(Math.max(0, backDotsSpace));
            
            System.out.printf(ColorConstants.BORDER + "║" + ColorConstants.RESET + "  %s  %s%s%s" + ColorConstants.BORDER + "║" + ColorConstants.RESET + "%n", choiceStr, backStr, backDots, backUpper);
            System.out.println(ColorConstants.BORDER + "║" + ColorConstants.RESET + "                                                           " + ColorConstants.BORDER + "║" + ColorConstants.RESET);
            System.out.println(ColorConstants.BORDER + "╚═══════════════════════════════════════════════════════════╝" + ColorConstants.RESET);
            
            int choice = BrewiseCoffeeShop.getInt(ColorConstants.INPUT_PROMPT + "  Enter your choice [1-" + (products.size() + 1) + "]: " + ColorConstants.RESET, 
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
        System.out.println("\n" + ColorConstants.BORDER + "╔═══════════════════════════════════════════════════════════╗" + ColorConstants.RESET);
        System.out.println(ColorConstants.BORDER + "║" + ColorConstants.RESET + ColorConstants.HEADER + "                   CUSTOMIZE ORDER                         " + ColorConstants.RESET + ColorConstants.BORDER + "║" + ColorConstants.RESET);
        System.out.println(ColorConstants.BORDER + "╠═══════════════════════════════════════════════════════════╣" + ColorConstants.RESET);
        System.out.println("  Product: " + ColorConstants.MENU_ITEM + product.name + ColorConstants.RESET);
        System.out.println("  Price: " + ColorConstants.PRICE + product.price + " PHP" + ColorConstants.RESET);
        System.out.println("  " + product.description);
        System.out.println(ColorConstants.BORDER + "╠═══════════════════════════════════════════════════════════╣" + ColorConstants.RESET);
        
        int quantity = BrewiseCoffeeShop.getInt(ColorConstants.INPUT_PROMPT + "  Quantity [1-10]: " + ColorConstants.RESET, 1, 10);
        
        // Get sugar level
        System.out.println("\n  Sugar Level:");
        System.out.println("  " + ColorConstants.MENU_ITEM + "[1]" + ColorConstants.RESET + "  Less Sweet");
        System.out.println("  " + ColorConstants.MENU_ITEM + "[2]" + ColorConstants.RESET + "  Standard");
        System.out.println("  " + ColorConstants.MENU_ITEM + "[3]" + ColorConstants.RESET + "  Sweet");
        int sugarChoice = BrewiseCoffeeShop.getInt(ColorConstants.INPUT_PROMPT + "  Choice [1-3]: " + ColorConstants.RESET, 1, 3);
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
            System.out.println("\n" + ColorConstants.BORDER + "╔═══════════════════════════════════════════════════════════╗" + ColorConstants.RESET);
            System.out.println(ColorConstants.BORDER + "║" + ColorConstants.RESET + "  " + BrewiseCoffeeShop.padCenter(ColorConstants.HEADER + "ADD-ONS FOR " + category + ColorConstants.RESET, 55) + ColorConstants.BORDER + "║" + ColorConstants.RESET);
            System.out.println(ColorConstants.BORDER + "╠═══════════════════════════════════════════════════════════╣" + ColorConstants.RESET);
            System.out.println("  Current Total: " + ColorConstants.PRICE + String.format("%.2f", item.getTotal()) + " PHP" + ColorConstants.RESET);
            
            if (!item.getAddOns().isEmpty()) {
                System.out.println(ColorConstants.BORDER + "╠═══════════════════════════════════════════════════════════╣" + ColorConstants.RESET);
                System.out.println("  Selected Add-ons:");
                for (AddOn addon : item.getAddOns()) {
                    System.out.printf("    • %s - " + ColorConstants.PRICE + "%.2f PHP" + ColorConstants.RESET + "%n", addon.name, addon.price);
                }
            }
            
            // Get context-aware add-ons for this product category
            List<AddOn> contextAddOns = store.getAddOnsForCategory(category);
            
            System.out.println(ColorConstants.BORDER + "╠═══════════════════════════════════════════════════════════╣" + ColorConstants.RESET);
            if (contextAddOns.isEmpty()) {
                System.out.println("  No add-ons available for this item");
            } else {
                for (int i = 0; i < contextAddOns.size(); i++) {
                    AddOn addon = contextAddOns.get(i);
                    System.out.printf("    " + ColorConstants.MENU_ITEM + "[%d]" + ColorConstants.RESET + "  %-40s " + ColorConstants.PRICE + "%.2f PHP" + ColorConstants.RESET + "%n", 
                                    (i + 1), addon.name, addon.price);
                }
            }
            System.out.printf("    " + ColorConstants.WARNING + "[%d]" + ColorConstants.RESET + "  %s%n", 
                            (contextAddOns.size() + 1), "Done");
            System.out.println(ColorConstants.BORDER + "╚═══════════════════════════════════════════════════════════╝" + ColorConstants.RESET);
            
            int choice = BrewiseCoffeeShop.getInt(ColorConstants.INPUT_PROMPT + "  Choice [1-" + (contextAddOns.size() + 1) + "]: " + ColorConstants.RESET, 
                                                 1, contextAddOns.size() + 1);
            
            if (choice == contextAddOns.size() + 1) break;
            
            // Add selected add-on
            AddOn selected = contextAddOns.get(choice - 1);
            int addonQty = BrewiseCoffeeShop.getInt(ColorConstants.INPUT_PROMPT + "  Quantity [1-5]: " + ColorConstants.RESET, 1, 5);
            item.addAddOn(selected, addonQty);
        }
        
        // Confirm and create order
        System.out.println("\n" + ColorConstants.BORDER + "╔═══════════════════════════════════════════════════════════╗" + ColorConstants.RESET);
        System.out.println(ColorConstants.BORDER + "║" + ColorConstants.RESET + ColorConstants.HEADER + "                  ORDER SUMMARY                            " + ColorConstants.RESET + ColorConstants.BORDER + "║" + ColorConstants.RESET);
        System.out.println(ColorConstants.BORDER + "╠═══════════════════════════════════════════════════════════╣" + ColorConstants.RESET);
        System.out.println("  Item: " + ColorConstants.MENU_ITEM + product.name + ColorConstants.RESET);
        System.out.println("  Quantity: " + ColorConstants.PRICE + "x" + quantity + ColorConstants.RESET);
        System.out.println("  Unit Price: " + ColorConstants.PRICE + product.price + " PHP" + ColorConstants.RESET);
        System.out.println("  Sugar Level: " + sugar);
        
        if (!item.getAddOns().isEmpty()) {
            System.out.println("  Add-ons:");
            for (AddOn addon : item.getAddOns()) {
                System.out.printf("    • %s - " + ColorConstants.PRICE + "%.2f PHP" + ColorConstants.RESET + "%n", addon.name, addon.price);
            }
        }
        
        System.out.println("  TOTAL: " + ColorConstants.PRICE + String.format("%.2f", item.getTotal()) + " PHP" + ColorConstants.RESET);
        System.out.println(ColorConstants.BORDER + "╠═══════════════════════════════════════════════════════════╣" + ColorConstants.RESET);
        System.out.println(ColorConstants.BORDER + "║" + ColorConstants.RESET + "                                                           " + ColorConstants.BORDER + "║" + ColorConstants.RESET);
        System.out.println(ColorConstants.BORDER + "║" + ColorConstants.RESET + "    " + ColorConstants.MENU_ITEM + "[1]" + ColorConstants.RESET + "  Add to Basket                                     " + ColorConstants.BORDER + "║" + ColorConstants.RESET);
        System.out.println(ColorConstants.BORDER + "║" + ColorConstants.RESET + "    " + ColorConstants.MENU_ITEM + "[2]" + ColorConstants.RESET + "  Checkout Now                                      " + ColorConstants.BORDER + "║" + ColorConstants.RESET);
        System.out.println(ColorConstants.BORDER + "║" + ColorConstants.RESET + "    " + ColorConstants.WARNING + "[3]" + ColorConstants.RESET + "  Cancel                                            " + ColorConstants.BORDER + "║" + ColorConstants.RESET);
        System.out.println(ColorConstants.BORDER + "║" + ColorConstants.RESET + "                                                           " + ColorConstants.BORDER + "║" + ColorConstants.RESET);
        System.out.println(ColorConstants.BORDER + "╚═══════════════════════════════════════════════════════════╝" + ColorConstants.RESET);
        
        int choice = BrewiseCoffeeShop.getInt(ColorConstants.INPUT_PROMPT + "  Choice [1-3]: " + ColorConstants.RESET, 1, 3);
        
        if (choice == 1) {
            // Add to basket
            store.addToBasket(item);
            System.out.println("  " + ColorConstants.colorize("[SUCCESS]", ColorConstants.SUCCESS) + " Added to basket!");
            BrewiseCoffeeShop.returnToMain = true;
        } else if (choice == 2) {
            // Direct checkout
            store.addToBasket(item);
            java.util.List<String> orderCodes = store.checkoutBasket();
            System.out.println("\n" + ColorConstants.BORDER + "╔═══════════════════════════════════════════════════════════╗" + ColorConstants.RESET);
            System.out.println(ColorConstants.BORDER + "║" + ColorConstants.RESET + ColorConstants.colorize("               ORDER CONFIRMED!", ColorConstants.SUCCESS) + "                            " + ColorConstants.BORDER + "║" + ColorConstants.RESET);
            System.out.println(ColorConstants.BORDER + "╠═══════════════════════════════════════════════════════════╣" + ColorConstants.RESET);
            System.out.println(ColorConstants.BORDER + "║" + ColorConstants.RESET + "  Your Order Number(s):                                    " + ColorConstants.BORDER + "║" + ColorConstants.RESET);
            for (String code : orderCodes) {
                System.out.println(ColorConstants.BORDER + "║" + ColorConstants.RESET + "                                                           " + ColorConstants.BORDER + "║" + ColorConstants.RESET);
                System.out.println(ColorConstants.BORDER + "║" + ColorConstants.RESET + "           " + ColorConstants.PRICE + "ORDER: " + code + ColorConstants.RESET);
                System.out.println(ColorConstants.BORDER + "║" + ColorConstants.RESET + "                                                           " + ColorConstants.BORDER + "║" + ColorConstants.RESET);
            }
            System.out.println(ColorConstants.BORDER + "╠═══════════════════════════════════════════════════════════╣" + ColorConstants.RESET);
            System.out.println(ColorConstants.BORDER + "║" + ColorConstants.RESET + "  Please show this number to the cashier to complete      " + ColorConstants.BORDER + "║" + ColorConstants.RESET);
            System.out.println(ColorConstants.BORDER + "║" + ColorConstants.RESET + "  your payment!                                            " + ColorConstants.BORDER + "║" + ColorConstants.RESET);
            System.out.println(ColorConstants.BORDER + "╚═══════════════════════════════════════════════════════════╝" + ColorConstants.RESET);
            BrewiseCoffeeShop.returnToMain = true; // Exit back to main menu after checkout
        } else {
            System.out.println("  " + ColorConstants.colorize("[INFO]", ColorConstants.INFO) + " Order cancelled.");
        }
    }
}
