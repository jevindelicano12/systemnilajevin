import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.Serializable;

public class Store {
    // Products/Menu
    private final List<Product> products = new ArrayList<>();
    private final List<AddOn> addOns = new ArrayList<>();
    private final Map<String, InventoryItem> inventory = new HashMap<>();
    private List<OrderItem> currentBasketItems = new ArrayList<>();
    private final Map<String, BrewiseCoffeeShop.Order> pendingOrders = new HashMap<>();
    private final Map<String, BrewiseCoffeeShop.Order> completedOrders = new HashMap<>();
    private final List<SalesRecord> salesHistory = new ArrayList<>();
    
    // Cashier Management
    private final List<CashierAccount> cashiers = new ArrayList<>();
    
    private int orderCounter = 1000;

    public Store() {
        this(true);
    }

    Store(boolean initialize) {
        if (initialize) {
            initializeProducts();
            initializeAddOns();
            initializeInventory();
            initializeCashiers();  // Initialize default cashiers
            this.currentBasketItems = new ArrayList<>();
        }
    }

    public String generateOrderCode() {
        // Generate random 4-digit number (1000-9999)
        Random random = new Random();
        int randomNum = 1000 + random.nextInt(9000);
        return String.valueOf(randomNum);
    }

    // For persistence
    List<Product> getProducts() { return new ArrayList<>(products); }
    List<AddOn> getAllAddOns() { return new ArrayList<>(addOns); }
    Map<String, InventoryItem> getInventory() { return new HashMap<>(inventory); }
    Map<String, BrewiseCoffeeShop.Order> getPendingOrders() {
        Map<String, BrewiseCoffeeShop.Order> result = new HashMap<>();
        for (Map.Entry<String, BrewiseCoffeeShop.Order> entry : pendingOrders.entrySet()) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }
    Map<String, BrewiseCoffeeShop.Order> getCompletedOrders() {
        Map<String, BrewiseCoffeeShop.Order> result = new HashMap<>();
        for (Map.Entry<String, BrewiseCoffeeShop.Order> entry : completedOrders.entrySet()) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }
    List<SalesRecord> getSalesHistory() { return new ArrayList<>(salesHistory); }

    // For persistence - setters that populate actual lists
    void setProducts(List<Product> newProducts) {
        products.clear();
        products.addAll(newProducts);
    }
    
    void setAddOns(List<AddOn> newAddOns) {
        addOns.clear();
        addOns.addAll(newAddOns);
    }
    
    void setInventory(Map<String, InventoryItem> newInventory) {
        inventory.clear();
        inventory.putAll(newInventory);
    }
    
    void setPendingOrders(Map<String, BrewiseCoffeeShop.Order> newOrders) {
        pendingOrders.clear();
        pendingOrders.putAll(newOrders);
    }
    
    void setCompletedOrders(Map<String, BrewiseCoffeeShop.Order> newOrders) {
        completedOrders.clear();
        completedOrders.putAll(newOrders);
    }
    
    void setSalesHistory(List<SalesRecord> newHistory) {
        salesHistory.clear();
        salesHistory.addAll(newHistory);
    }

    private void initializeProducts() {
        // Coffee (3 flavours) - 10 stocks each
        addProduct("P-C-ESP", "Coffee", "Espresso", "Original", 
                  "Strong brewed espresso shot; bold and rich.", 80, 10);
        addProduct("P-C-CAR", "Coffee", "Caramel Latte", "Caramel", 
                  "Espresso with steamed milk and caramel syrup.", 110, 10);
        addProduct("P-C-HAZ", "Coffee", "Hazelnut Mocha", "Hazelnut", 
                  "Chocolate and hazelnut with espresso and milk.", 120, 10);

        // MilkTea (3 flavours) - 10 stocks each
        addProduct("P-M-THA", "Milk Tea", "Thai", "Classic", 
                  "Classic Thai tea with milk.", 90, 10);
        addProduct("P-M-TAR", "Milk Tea", "Taro", "Taro", 
                  "Rich taro milk tea.", 100, 10);
        addProduct("P-M-MAT", "Milk Tea", "Matcha", "Green Tea", 
                  "Japanese green tea with milk.", 110, 10);

        // Frappe (3 flavours) - 10 stocks each
        addProduct("P-F-MOC", "Frappe", "Mocha", "Chocolate", 
                  "Coffee and chocolate blended with ice.", 130, 10);
        addProduct("P-F-CAR", "Frappe", "Caramel", "Caramel", 
                  "Coffee with caramel syrup blended with ice.", 130, 10);
        addProduct("P-F-VAN", "Frappe", "Vanilla", "Vanilla", 
                  "Coffee with vanilla syrup blended with ice.", 130, 10);

        // Fruit Tea (3 flavours) - 10 stocks each
        addProduct("P-T-LEM", "Fruit Tea", "Lemon", "Citrus", 
                  "Fresh lemon black tea.", 80, 10);
        addProduct("P-T-PEA", "Fruit Tea", "Peach", "Peach", 
                  "Sweet peach black tea.", 90, 10);
        addProduct("P-T-LYC", "Fruit Tea", "Lychee", "Lychee", 
                  "Lychee-infused black tea.", 90, 10);
    }

    private void initializeAddOns() {
        // Coffee Add-ons: Spices, Sweeteners, Creamy additions
        addAddOn("A-CIN", "Cinnamon", 10);
        addAddOn("A-NUT", "Nutmeg", 10);
        addAddOn("A-HON", "Honey", 15);
        addAddOn("A-MAP", "Maple Syrup", 15);
        addAddOn("A-MIL", "Milk", 10);
        addAddOn("A-WHC", "Whipped Cream", 20);
        
        // Milk Tea Add-ons: Boba, Sweeteners, Creamy
        addAddOn("A-BOBA", "Boba / Pearls", 25);
        addAddOn("A-JEL", "Jelly", 15);
        addAddOn("A-CFS", "Cheese Foam", 35);
        
        // Frappe Add-ons: All of above
        // Fruit Tea Add-ons: Boba, Sweeteners
    }

    private void initializeInventory() {
        // STANDARD: 20 units as starting quantity, 10 units as low-stock threshold
        
        // Basic supplies - 20 pieces each, reorder at 10
        addInventoryItem("I-CUP-S", "Small Cups", "pieces", 20, 10);
        addInventoryItem("I-CUP-M", "Medium Cups", "pieces", 20, 10);
        addInventoryItem("I-CUP-L", "Large Cups", "pieces", 20, 10);
        addInventoryItem("I-LID", "Cup Lids", "pieces", 20, 10);
        addInventoryItem("I-STR", "Straws", "pieces", 20, 10);

        // Coffee basics - 20 grams/ml each, reorder at 10
        addInventoryItem("I-BEANS", "Coffee Beans", "grams", 20, 10);
        addInventoryItem("I-MILK", "Fresh Milk", "ml", 20, 10);

        // Tea basics - 20 grams each, reorder at 10
        addInventoryItem("I-TEA-B", "Black Tea", "grams", 20, 10);
        addInventoryItem("I-TEA-G", "Green Tea", "grams", 20, 10);

        // Add-ons and toppings - 20 grams/ml each, reorder at 10
        addInventoryItem("I-BOBA", "Boba Pearls", "grams", 20, 10);
        addInventoryItem("I-WHC", "Whipping Cream", "ml", 20, 10);
        addInventoryItem("I-SYR-V", "Vanilla Syrup", "ml", 20, 10);
        addInventoryItem("I-SYR-C", "Caramel Syrup", "ml", 20, 10);
        addInventoryItem("I-CFOAM", "Cheese Foam Mix", "grams", 20, 10);
        addInventoryItem("I-ALT", "Almond Milk", "ml", 20, 10);

        // Ice and water - 20 units each, reorder at 10
        addInventoryItem("I-ICE", "Ice", "kg", 20, 10);
        addInventoryItem("I-WAT", "Filtered Water", "L", 20, 10);
    }

    private void addProduct(String id, String category, String name, String flavour, 
                          String description, double price, int stock) {
        products.add(new Product(id, category, name, flavour, description, price, stock));
    }

    private void addAddOn(String id, String name, double price) {
        addOns.add(new AddOn(id, name, price));
    }

    private void addInventoryItem(String id, String name, String unit, double qty, double threshold) {
        inventory.put(id, new InventoryItem(id, name, unit, qty, threshold));
    }

    // ==================== CASHIER MANAGEMENT ====================
    
    private void initializeCashiers() {
        // Create default cashier accounts
        cashiers.add(new CashierAccount("cashier1", "password123", "ACTIVE"));
        cashiers.add(new CashierAccount("admin", "admin123", "ACTIVE"));
    }
    
    public List<CashierAccount> getCashiers() {
        return new ArrayList<>(cashiers);
    }
    
    // For persistence loading - sets cashiers directly (not a copy)
    public void setCashiers(List<CashierAccount> newCashiers) {
        cashiers.clear();
        if (newCashiers != null) {
            cashiers.addAll(newCashiers);
        }
    }
    
    public CashierAccount findCashier(String username) {
        for (CashierAccount c : cashiers) {
            if (c.username.equals(username)) {
                return c;
            }
        }
        return null;
    }
    
    public boolean addCashier(String username, String password) {
        // Check if cashier already exists
        if (findCashier(username) != null) {
            return false;  // Cashier already exists
        }
        cashiers.add(new CashierAccount(username, password, "ACTIVE"));
        return true;  // Successfully added
    }
    
    public boolean removeCashier(String username) {
        // Don't allow removing all cashiers
        if (cashiers.size() <= 1) {
            return false;
        }
        // Don't allow removing admin
        if (username.equals("admin")) {
            return false;
        }
        for (int i = 0; i < cashiers.size(); i++) {
            if (cashiers.get(i).username.equals(username)) {
                cashiers.remove(i);
                return true;
            }
        }
        return false;
    }
    
    public boolean editCashierPassword(String username, String newPassword) {
        CashierAccount cashier = findCashier(username);
        if (cashier != null) {
            cashier.changePassword(newPassword);
            return true;
        }
        return false;
    }
    
    public boolean deactivateCashier(String username) {
        CashierAccount cashier = findCashier(username);
        if (cashier != null && !username.equals("admin")) {  // Don't deactivate admin
            cashier.deactivate();
            return true;
        }
        return false;
    }
    
    public boolean activateCashier(String username) {
        CashierAccount cashier = findCashier(username);
        if (cashier != null) {
            cashier.activate();
            return true;
        }
        return false;
    }

    // ============================================================

    public List<Product> getProductsByCategory(String category) {
        List<Product> result = new ArrayList<>();
        for (Product p : products) {
            if (p.category.equals(category)) result.add(p);
        }
        return result;
    }

    public List<String> getCategories() {
        List<String> categories = new ArrayList<>();
        for (Product p : products) {
            if (!categories.contains(p.category)) {
                categories.add(p.category);
            }
        }
        return categories;
    }

    public Product findProduct(String id) {
        for (Product p : products) {
            if (p.id.equals(id)) return p;
        }
        return null;
    }

    public AddOn findAddOn(String id) {
        for (AddOn a : addOns) {
            if (a.id.equals(id)) return a;
        }
        return null;
    }

    public List<AddOn> getAddOns() {
        return new ArrayList<>(addOns);
    }

    public void savePendingOrder(BrewiseCoffeeShop.Order order) {
        // Make sure the order has an order code
        if (order.orderCode.isEmpty()) {
            order.orderCode = generateOrderCode();
        }
        pendingOrders.put(order.orderCode, order);
    }

    public BrewiseCoffeeShop.Order getCurrentBasket() {
        // Create a summary order from all current basket items
        if (currentBasketItems.isEmpty()) {
            return new BrewiseCoffeeShop.Order("No Items", 0, "", new ArrayList<>(), new HashMap<>(), 0, 0);
        }
        // For now, combine all items into one order (simplified)
        double totalPrice = 0;
        List<String> allAddOns = new ArrayList<>();
        Map<String, Integer> allAddOnsQty = new HashMap<>();
        
        for (OrderItem item : currentBasketItems) {
            totalPrice += item.getTotal();
            for (AddOn addon : item.getAddOns()) {
                allAddOns.add(addon.name);
                allAddOnsQty.put(addon.name, item.getAddOnQuantity(addon));
            }
        }
        
        return new BrewiseCoffeeShop.Order("", currentBasketItems.size(), "", allAddOns, allAddOnsQty, totalPrice, totalPrice);
    }

    public List<OrderItem> getCurrentBasketItems() {
        return new ArrayList<>(currentBasketItems);
    }

    public void addToBasket(OrderItem item) {
        currentBasketItems.add(item);
    }

    public void removeFromBasket(OrderItem item) {
        currentBasketItems.remove(item);
    }

    public void clearBasket() {
        currentBasketItems.clear();
    }

    public java.util.List<String> checkoutBasket() {
        java.util.List<String> orderCodes = new java.util.ArrayList<>();
        if (!currentBasketItems.isEmpty()) {
            // Create ONE single order with all basket items
            double totalPrice = 0;
            for (OrderItem item : currentBasketItems) {
                totalPrice += item.subtotal;
            }
            
            // Create a single order using the first item's details (will be overridden with multi-item)
            OrderItem firstItem = currentBasketItems.get(0);
            BrewiseCoffeeShop.Order order = new BrewiseCoffeeShop.Order(
                firstItem.product.name, 1, firstItem.sugarLevel, 
                new java.util.ArrayList<>(), new java.util.HashMap<>(), 
                totalPrice, firstItem.product.price
            );
            
            // Set items list for multi-item display
            order.items = new java.util.ArrayList<>(currentBasketItems);
            order.totalPrice = totalPrice;
            
            // ==================== INVENTORY DEDUCTION ====================
            // Deduct product stock and supplies for each item in the order
            for (OrderItem item : currentBasketItems) {
                // Deduct product stock
                deductProductStock(item.product.id, item.quantity);
                
                // Deduct 1 cup per order (assume medium cup, adjust as needed)
                deductInventory("I-CUP-M", item.quantity);
                
                // Deduct 1 lid per order
                deductInventory("I-LID", item.quantity);
                
                // Deduct 1 straw per order
                deductInventory("I-STR", item.quantity);
                
                // Deduct ingredients based on product category
                String category = item.product.category;
                if ("Coffee".equalsIgnoreCase(category)) {
                    deductInventory("I-BEANS", item.quantity * 18); // ~18g per espresso shot
                    deductInventory("I-MILK", item.quantity * 100); // ~100ml milk per coffee
                } else if ("Milk Tea".equalsIgnoreCase(category)) {
                    deductInventory("I-TEA-B", item.quantity * 5); // ~5g black tea
                    deductInventory("I-MILK", item.quantity * 150); // ~150ml milk per tea
                } else if ("Frappe".equalsIgnoreCase(category)) {
                    deductInventory("I-BEANS", item.quantity * 20); // ~20g beans
                    deductInventory("I-ICE", item.quantity * 0.2); // ~200g ice
                } else if ("Fruit Tea".equalsIgnoreCase(category)) {
                    deductInventory("I-TEA-B", item.quantity * 5); // ~5g black tea
                    deductInventory("I-ICE", item.quantity * 0.15); // ~150g ice
                }
                
                // Deduct add-ons inventory if used
                for (AddOn addon : item.getAddOns()) {
                    int addonQty = item.getAddOnQuantity(addon);
                    // Map add-ons to inventory items
                    if ("Whipped Cream".equalsIgnoreCase(addon.name)) {
                        deductInventory("I-WHC", addonQty * 15); // ~15ml per serving
                    } else if ("Boba / Pearls".equalsIgnoreCase(addon.name)) {
                        deductInventory("I-BOBA", addonQty * 30); // ~30g per serving
                    } else if ("Cheese Foam".equalsIgnoreCase(addon.name)) {
                        deductInventory("I-CFOAM", addonQty * 25); // ~25g per serving
                    } else if ("Milk".equalsIgnoreCase(addon.name)) {
                        deductInventory("I-MILK", addonQty * 50); // ~50ml extra milk
                    } else if ("Honey".equalsIgnoreCase(addon.name)) {
                        // Honey not in inventory, skip
                    } else if ("Maple Syrup".equalsIgnoreCase(addon.name) || 
                               "Cinnamon".equalsIgnoreCase(addon.name) ||
                               "Nutmeg".equalsIgnoreCase(addon.name) ||
                               "Jelly".equalsIgnoreCase(addon.name)) {
                        // Spices/syrups/jelly not tracked in detail, skip
                    }
                }
            }
            
            savePendingOrder(order);
            orderCodes.add(order.orderCode);
            
            // Display inventory status after checkout
            System.out.println("\n[INFO] INVENTORY UPDATED:");
            System.out.println("  [SUCCESS] Product stock deducted");
            System.out.println("  [SUCCESS] Cups, straws, and ingredients deducted");
            System.out.println("  [WARNING] Check admin panel for low-stock alerts");
            
            clearBasket();
        }
        return orderCodes;
    }

    public BrewiseCoffeeShop.Order findPendingOrder(String orderCode) {
        return pendingOrders.get(orderCode);
    }

    public BrewiseCoffeeShop.Order findCompletedOrder(String orderCode) {
        return completedOrders.get(orderCode);
    }

    public void completeOrder(String orderCode) {
        BrewiseCoffeeShop.Order order = pendingOrders.remove(orderCode);
        if (order != null) {
            completedOrders.put(order.orderCode, order);
            recordSale(order);
        }
    }

    public void removePendingOrder(String code) {
        pendingOrders.remove(code);
    }

    public void recordSale(BrewiseCoffeeShop.Order order) {
        String date = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        SalesRecord sale = new SalesRecord(
            date,
            order.orderCode,
            0,  // subtotal - would need to be calculated from order
            0,  // tax
            order.totalPrice  // total
        );
        salesHistory.add(sale);
    }

    // Get add-ons for a specific category
    public List<AddOn> getAddOnsForCategory(String category) {
        List<AddOn> contextAddOns = new ArrayList<>();
        
        if ("Coffee".equalsIgnoreCase(category)) {
            // Coffee: Spices (Cinnamon, Nutmeg), Sweeteners (Honey, Maple), Creamy (Milk, Whipped Cream)
            addIfExists(contextAddOns, "Cinnamon");
            addIfExists(contextAddOns, "Nutmeg");
            addIfExists(contextAddOns, "Honey");
            addIfExists(contextAddOns, "Maple Syrup");
            addIfExists(contextAddOns, "Milk");
            addIfExists(contextAddOns, "Whipped Cream");
        } else if ("Milk Tea".equalsIgnoreCase(category)) {
            // Milk Tea: Boba, Jelly, Cheese Foam (NO Extra Shot)
            addIfExists(contextAddOns, "Boba / Pearls");
            addIfExists(contextAddOns, "Jelly");
            addIfExists(contextAddOns, "Cheese Foam");
        } else if ("Frappe".equalsIgnoreCase(category)) {
            // Frappe: All add-ons available
            addIfExists(contextAddOns, "Cinnamon");
            addIfExists(contextAddOns, "Nutmeg");
            addIfExists(contextAddOns, "Honey");
            addIfExists(contextAddOns, "Maple Syrup");
            addIfExists(contextAddOns, "Milk");
            addIfExists(contextAddOns, "Whipped Cream");
            addIfExists(contextAddOns, "Boba / Pearls");
            addIfExists(contextAddOns, "Jelly");
            addIfExists(contextAddOns, "Cheese Foam");
        } else if ("Fruit Tea".equalsIgnoreCase(category)) {
            // Fruit Tea: Boba, Jelly, Sweeteners
            addIfExists(contextAddOns, "Boba / Pearls");
            addIfExists(contextAddOns, "Jelly");
            addIfExists(contextAddOns, "Honey");
            addIfExists(contextAddOns, "Maple Syrup");
        }
        
        return contextAddOns;
    }
    
    private void addIfExists(List<AddOn> list, String addonName) {
        for (AddOn addon : addOns) {
            if (addon.name.equalsIgnoreCase(addonName)) {
                list.add(addon);
                return;
            }
        }
    }
    
    // Check stock availability
    public boolean isProductAvailable(String productId) {
        for (Product p : products) {
            if (p.id.equals(productId)) {
                return p.stock > 0;
            }
        }
        return false;
    }
    
    // Get product stock level
    public int getProductStock(String productId) {
        for (Product p : products) {
            if (p.id.equals(productId)) {
                return p.stock;
            }
        }
        return 0;
    }
    
    // Deduct product stock
    public boolean deductProductStock(String productId, int quantity) {
        for (Product p : products) {
            if (p.id.equals(productId) && p.stock >= quantity) {
                p.stock -= quantity;
                return true;
            }
        }
        return false;
    }
    
    // Deduct inventory items (cups, straws, ingredients)
    public void deductInventory(String itemId, double quantity) {
        if (inventory.containsKey(itemId)) {
            InventoryItem item = inventory.get(itemId);
            item.quantity -= quantity;
            if (item.quantity < item.reorderThreshold) {
                System.out.println("[WARNING] LOW STOCK: " + item.name + " is running low! Current: " + (int)item.quantity);
            }
        }
    }

    public List<SalesRecord> getSales(String startDate, String endDate) {
        List<SalesRecord> filtered = new ArrayList<>();
        for (SalesRecord r : salesHistory) {
            if (r.getDate().compareTo(startDate) >= 0 && r.getDate().compareTo(endDate) <= 0) {
                filtered.add(r);
            }
        }
        return filtered;
    }

    public static class SalesRecord implements Serializable {
        private static final long serialVersionUID = 1L;
        public final String date;
        public final String orderCode;
        public final double subtotal;
        public final double tax;
        public final double total;

        public SalesRecord(String date, String orderCode, double subtotal, double tax, double total) {
            this.date = date;
            this.orderCode = orderCode;
            this.subtotal = subtotal;
            this.tax = tax;
            this.total = total;
        }

        public String getDate() { return date; }
        public String getOrderCode() { return orderCode; }
        public double getSubtotal() { return subtotal; }
        public double getTax() { return tax; }
        public double getTotal() { return total; }
    }
}