import java.util.List;

public class CustomerModule {
    public static void customerMode() {
        Store store = BrewiseCoffeeShop.store;
        List<String> categories = store.getCategories();
        
        while (true) {
            System.out.println("\n" + ColorConstants.MENU_BOX_BG + ColorConstants.BORDER + "╔═══════════════════════════════════════════════════════════╗" + ColorConstants.RESET);
            System.out.println(ColorConstants.MENU_BOX_BG + ColorConstants.BORDER + "║" + ColorConstants.RESET + ColorConstants.MENU_BOX_BG + ColorConstants.HEADER + "         CUSTOMER MENU                                     " + ColorConstants.RESET + ColorConstants.MENU_BOX_BG + ColorConstants.BORDER + "║" + ColorConstants.RESET);
            System.out.println(ColorConstants.MENU_BOX_BG + ColorConstants.BORDER + "╠═══════════════════════════════════════════════════════════╣" + ColorConstants.RESET);
            System.out.println(ColorConstants.MENU_BOX_BG + ColorConstants.BORDER + "║" + ColorConstants.RESET + ColorConstants.MENU_BOX_BG + "                                                           " + ColorConstants.RESET + ColorConstants.MENU_BOX_BG + ColorConstants.BORDER + "║" + ColorConstants.RESET);
            
            // Show all product categories dynamically
            for (int i = 0; i < categories.size(); i++) {
                String content = "  " + ColorConstants.MENU_ITEM + "[" + (i + 1) + "]" + ColorConstants.RESET + ColorConstants.MENU_BOX_BG + "  " + BrewiseCoffeeShop.padRight(categories.get(i), 46);
                System.out.println(ColorConstants.MENU_BOX_BG + ColorConstants.BORDER + "║" + ColorConstants.RESET + ColorConstants.MENU_BOX_BG + content + ColorConstants.RESET + ColorConstants.MENU_BOX_BG + ColorConstants.BORDER + "║" + ColorConstants.RESET);
            }
            
            String backContent = "  " + ColorConstants.WARNING + "[" + (categories.size() + 1) + "]" + ColorConstants.RESET + ColorConstants.MENU_BOX_BG + "  " + BrewiseCoffeeShop.padRight("Back", 46);
            System.out.println(ColorConstants.MENU_BOX_BG + ColorConstants.BORDER + "║" + ColorConstants.RESET + ColorConstants.MENU_BOX_BG + backContent + ColorConstants.RESET + ColorConstants.MENU_BOX_BG + ColorConstants.BORDER + "║" + ColorConstants.RESET);
            
            System.out.println(ColorConstants.MENU_BOX_BG + ColorConstants.BORDER + "║" + ColorConstants.RESET + ColorConstants.MENU_BOX_BG + "                                                           " + ColorConstants.RESET + ColorConstants.MENU_BOX_BG + ColorConstants.BORDER + "║" + ColorConstants.RESET);
            System.out.println(ColorConstants.MENU_BOX_BG + ColorConstants.BORDER + "╚═══════════════════════════════════════════════════════════╝" + ColorConstants.RESET);
            
            int choice = BrewiseCoffeeShop.getInt(ColorConstants.INPUT_PROMPT + "  Enter your choice [1-" + (categories.size() + 1) + "]: " + ColorConstants.RESET, 
                                                 1, categories.size() + 1);

            if (choice == categories.size() + 1) return;
            
            String selectedCategory = categories.get(choice - 1);
            productMenu(selectedCategory);

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
            System.out.println("\n" + ColorConstants.MENU_BOX_BG + ColorConstants.BORDER + "╔═══════════════════════════════════════════════════════════╗" + ColorConstants.RESET);
            System.out.println(ColorConstants.MENU_BOX_BG + ColorConstants.BORDER + "║" + ColorConstants.RESET + ColorConstants.MENU_BOX_BG + ColorConstants.HEADER + BrewiseCoffeeShop.padCenter(category.toUpperCase(), 57) + ColorConstants.RESET + ColorConstants.MENU_BOX_BG + ColorConstants.BORDER + "║" + ColorConstants.RESET);
            System.out.println(ColorConstants.MENU_BOX_BG + ColorConstants.BORDER + "╠═══════════════════════════════════════════════════════════╣" + ColorConstants.RESET);
            System.out.println(ColorConstants.MENU_BOX_BG + ColorConstants.BORDER + "║" + ColorConstants.RESET + ColorConstants.MENU_BOX_BG + "                                                           " + ColorConstants.RESET + ColorConstants.MENU_BOX_BG + ColorConstants.BORDER + "║" + ColorConstants.RESET);
            
            // Show all products in this category with proper alignment
            for (int i = 0; i < products.size(); i++) {
                Product p = products.get(i);
                String choiceStr = ColorConstants.MENU_ITEM + "[" + (i + 1) + "]" + ColorConstants.RESET;
                String priceStr = ColorConstants.PRICE + "PHP " + String.format("%.2f", p.price) + ColorConstants.RESET;
                int dotsSpace = 37 - p.name.length();
                String dots = ".".repeat(Math.max(1, dotsSpace));
                
                // Build content: [1]  Name.....PHP 120.00  (55 visible chars)
                // Then add borders to make 61 total
                String content = "  " + choiceStr + ColorConstants.MENU_BOX_BG + "  " + p.name + dots + priceStr + ColorConstants.MENU_BOX_BG + "  ";
                System.out.println(ColorConstants.MENU_BOX_BG + ColorConstants.BORDER + "║" + ColorConstants.RESET + ColorConstants.MENU_BOX_BG + content + ColorConstants.RESET + ColorConstants.MENU_BOX_BG + ColorConstants.BORDER + "║" + ColorConstants.RESET);
                
                // Show description on next line
                String desc = p.description;
                if (desc.length() > 51) {
                    desc = desc.substring(0, 48) + "...";
                }
                System.out.println(ColorConstants.MENU_BOX_BG + ColorConstants.BORDER + "║" + ColorConstants.RESET + ColorConstants.MENU_BOX_BG + "       " + BrewiseCoffeeShop.padRight(desc, 48) + ColorConstants.RESET + ColorConstants.MENU_BOX_BG + ColorConstants.BORDER + "║" + ColorConstants.RESET);
                System.out.println(ColorConstants.MENU_BOX_BG + ColorConstants.BORDER + "║" + ColorConstants.RESET + ColorConstants.MENU_BOX_BG + "                                                           " + ColorConstants.RESET + ColorConstants.MENU_BOX_BG + ColorConstants.BORDER + "║" + ColorConstants.RESET);
            }
            
            // Back option
            String backChoiceStr = ColorConstants.WARNING + "[" + (products.size() + 1) + "]" + ColorConstants.RESET;
            String backStr = "Back";
            String backUpper = "BACK";
            int backDotsSpace = 37 - backStr.length();
            String backDots = ".".repeat(Math.max(1, backDotsSpace));
            
            String backContent = "  " + backChoiceStr + ColorConstants.MENU_BOX_BG + "  " + backStr + backDots + backUpper + ColorConstants.MENU_BOX_BG + "  ";
            System.out.println(ColorConstants.MENU_BOX_BG + ColorConstants.BORDER + "║" + ColorConstants.RESET + ColorConstants.MENU_BOX_BG + backContent + ColorConstants.RESET + ColorConstants.MENU_BOX_BG + ColorConstants.BORDER + "║" + ColorConstants.RESET);
            System.out.println(ColorConstants.MENU_BOX_BG + ColorConstants.BORDER + "║" + ColorConstants.RESET + ColorConstants.MENU_BOX_BG + "                                                           " + ColorConstants.RESET + ColorConstants.MENU_BOX_BG + ColorConstants.BORDER + "║" + ColorConstants.RESET);
            System.out.println(ColorConstants.MENU_BOX_BG + ColorConstants.BORDER + "╚═══════════════════════════════════════════════════════════╝" + ColorConstants.RESET);
            
            int choice = BrewiseCoffeeShop.getInt(ColorConstants.INPUT_PROMPT + "  Enter your choice [1-" + (products.size() + 1) + "]: " + ColorConstants.RESET, 
                                                 1, products.size() + 1);
            
            if (choice == products.size() + 1) return;
            
            Product selected = products.get(choice - 1);
            customizeProduct(selected, category);
            
            if (BrewiseCoffeeShop.returnToMain) return;
        }
    }

    private static void customizeProduct(Product product, String category) {
        Store store = BrewiseCoffeeShop.store;
        System.out.println("\n" + ColorConstants.MENU_BOX_BG + ColorConstants.BORDER + "╔═══════════════════════════════════════════════════════════╗" + ColorConstants.RESET);
        System.out.println(ColorConstants.MENU_BOX_BG + ColorConstants.BORDER + "║" + ColorConstants.RESET + ColorConstants.MENU_BOX_BG + ColorConstants.HEADER + "                   CUSTOMIZE ORDER                         " + ColorConstants.RESET + ColorConstants.MENU_BOX_BG + ColorConstants.BORDER + "║" + ColorConstants.RESET);
        System.out.println(ColorConstants.MENU_BOX_BG + ColorConstants.BORDER + "╠═══════════════════════════════════════════════════════════╣" + ColorConstants.RESET);
        System.out.println(ColorConstants.MENU_BOX_BG + "  Product: " + ColorConstants.MENU_ITEM + product.name + ColorConstants.RESET + ColorConstants.MENU_BOX_BG);
        System.out.println(ColorConstants.MENU_BOX_BG + "  Price: " + ColorConstants.PRICE + product.price + " PHP" + ColorConstants.RESET + ColorConstants.MENU_BOX_BG);
        System.out.println(ColorConstants.MENU_BOX_BG + "  " + product.description + ColorConstants.RESET);
        System.out.println(ColorConstants.MENU_BOX_BG + ColorConstants.BORDER + "╠═══════════════════════════════════════════════════════════╣" + ColorConstants.RESET);
        
        int quantity = BrewiseCoffeeShop.getInt(ColorConstants.INPUT_PROMPT + "  Quantity [1-10]: " + ColorConstants.RESET, 1, 10);
        
        // Get sugar level
        System.out.println("\n" + ColorConstants.MENU_BOX_BG + "  Sugar Level:" + ColorConstants.RESET);
        System.out.println(ColorConstants.MENU_BOX_BG + "  " + ColorConstants.MENU_ITEM + "[1]" + ColorConstants.RESET + ColorConstants.MENU_BOX_BG + "  Less Sweet" + ColorConstants.RESET);
        System.out.println(ColorConstants.MENU_BOX_BG + "  " + ColorConstants.MENU_ITEM + "[2]" + ColorConstants.RESET + ColorConstants.MENU_BOX_BG + "  Standard" + ColorConstants.RESET);
        System.out.println(ColorConstants.MENU_BOX_BG + "  " + ColorConstants.MENU_ITEM + "[3]" + ColorConstants.RESET + ColorConstants.MENU_BOX_BG + "  Sweet" + ColorConstants.RESET);
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
