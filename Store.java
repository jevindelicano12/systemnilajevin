import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.Serializable;

public class Store {
    // Products/Menu
    private final List<Product> products = new ArrayList<>();
    private final List<AddOn> addOns = new ArrayList<>();
    private final Map<String, InventoryItem> inventory = new HashMap<>();
    private final Map<String, Order> pendingOrders = new HashMap<>();
    private final Map<String, Order> completedOrders = new HashMap<>();
    private final List<SalesRecord> salesHistory = new ArrayList<>();
    private int orderCounter = 1000;

    public Store() {
        this(true);
    }

    Store(boolean initialize) {
        if (initialize) {
            initializeProducts();
            initializeAddOns();
            initializeInventory();
        }
    }

    public String generateOrderCode() {
        return String.format("ORD%04d", ++orderCounter);
    }

    // For persistence
    List<Product> getProducts() { return new ArrayList<>(products); }
    List<AddOn> getAllAddOns() { return new ArrayList<>(addOns); }
    Map<String, InventoryItem> getInventory() { return new HashMap<>(inventory); }
    Map<String, BrewiseCoffeeShop.Order> getPendingOrders() {
        Map<String, BrewiseCoffeeShop.Order> result = new HashMap<>();
        for (Map.Entry<String, Order> entry : pendingOrders.entrySet()) {
            result.put(entry.getKey(), new BrewiseCoffeeShop.Order(entry.getValue().drink, entry.getValue().quantity, entry.getValue().sugar, entry.getValue().addOns, entry.getValue().addOnsQty, entry.getValue().totalPrice, entry.getValue().basePricePerUnit));
        }
        return result;
    }
    Map<String, BrewiseCoffeeShop.Order> getCompletedOrders() {
        Map<String, BrewiseCoffeeShop.Order> result = new HashMap<>();
        for (Map.Entry<String, Order> entry : completedOrders.entrySet()) {
            result.put(entry.getKey(), new BrewiseCoffeeShop.Order(entry.getValue().drink, entry.getValue().quantity, entry.getValue().sugar, entry.getValue().addOns, entry.getValue().addOnsQty, entry.getValue().totalPrice, entry.getValue().basePricePerUnit));
        }
        return result;
    }
    List<SalesRecord> getSalesHistory() { return new ArrayList<>(salesHistory); }

    private void initializeProducts() {
        // Coffee (3 flavours)
        addProduct("P-C-ESP", "Coffee", "Espresso", "Original", 
                  "Strong brewed espresso shot; bold and rich.", 80, 50);
        addProduct("P-C-CAR", "Coffee", "Caramel Latte", "Caramel", 
                  "Espresso with steamed milk and caramel syrup.", 110, 40);
        addProduct("P-C-HAZ", "Coffee", "Hazelnut Mocha", "Hazelnut", 
                  "Chocolate and hazelnut with espresso and milk.", 120, 30);

        // MilkTea (3 flavours)
        addProduct("P-M-THA", "Milk Tea", "Thai", "Classic", 
                  "Classic Thai tea with milk.", 90, 50);
        addProduct("P-M-TAR", "Milk Tea", "Taro", "Taro", 
                  "Rich taro milk tea.", 100, 40);
        addProduct("P-M-MAT", "Milk Tea", "Matcha", "Green Tea", 
                  "Japanese green tea with milk.", 110, 30);

        // Frappe (3 flavours)
        addProduct("P-F-MOC", "Frappe", "Mocha", "Chocolate", 
                  "Coffee and chocolate blended with ice.", 130, 30);
        addProduct("P-F-CAR", "Frappe", "Caramel", "Caramel", 
                  "Coffee with caramel syrup blended with ice.", 130, 30);
        addProduct("P-F-VAN", "Frappe", "Vanilla", "Vanilla", 
                  "Coffee with vanilla syrup blended with ice.", 130, 30);

        // Fruit Tea (3 flavours)
        addProduct("P-T-LEM", "Fruit Tea", "Lemon", "Citrus", 
                  "Fresh lemon black tea.", 80, 50);
        addProduct("P-T-PEA", "Fruit Tea", "Peach", "Peach", 
                  "Sweet peach black tea.", 90, 40);
        addProduct("P-T-LYC", "Fruit Tea", "Lychee", "Lychee", 
                  "Lychee-infused black tea.", 90, 40);
    }

    private void initializeAddOns() {
        addAddOn("A-EXS", "Extra Shot", 30);
        addAddOn("A-BOBA", "Boba / Pearls", 25);
        addAddOn("A-WHC", "Whipped Cream", 20);
        addAddOn("A-SYR", "Flavored Syrup", 15);
        addAddOn("A-CFS", "Cheese Foam", 35);
        addAddOn("A-ALT", "Almond/Oat Milk Upgrade", 25);
    }

    private void initializeInventory() {
        // Basic supplies
        addInventoryItem("I-CUP-S", "Small Cups", "pieces", 1000, 100);
        addInventoryItem("I-CUP-M", "Medium Cups", "pieces", 1000, 100);
        addInventoryItem("I-CUP-L", "Large Cups", "pieces", 1000, 100);
        addInventoryItem("I-LID", "Cup Lids", "pieces", 3000, 300);
        addInventoryItem("I-STR", "Straws", "pieces", 3000, 300);

        // Coffee basics
        addInventoryItem("I-BEANS", "Coffee Beans", "grams", 10000, 2000);
        addInventoryItem("I-MILK", "Fresh Milk", "ml", 20000, 5000);

        // Tea basics
        addInventoryItem("I-TEA-B", "Black Tea", "grams", 5000, 1000);
        addInventoryItem("I-TEA-G", "Green Tea", "grams", 5000, 1000);

        // Add-ons and toppings
        addInventoryItem("I-BOBA", "Boba Pearls", "grams", 10000, 2000);
        addInventoryItem("I-WHC", "Whipping Cream", "ml", 5000, 1000);
        addInventoryItem("I-SYR-V", "Vanilla Syrup", "ml", 5000, 1000);
        addInventoryItem("I-SYR-C", "Caramel Syrup", "ml", 5000, 1000);
        addInventoryItem("I-CFOAM", "Cheese Foam Mix", "grams", 5000, 1000);
        addInventoryItem("I-ALT", "Almond Milk", "ml", 10000, 2000);

        // Ice and water
        addInventoryItem("I-ICE", "Ice", "kg", 100, 20);
        addInventoryItem("I-WAT", "Filtered Water", "L", 200, 50);
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
        if (order.orderNumber == 0) {
            order.orderNumber = Integer.parseInt(generateOrderCode().substring(3));
        }
        pendingOrders.put(String.valueOf(order.orderNumber), new Order(order.drink, order.quantity, order.sugar, order.addOns, order.addOnsQty, order.totalPrice, order.basePricePerUnit));
    }

    public Order findPendingOrder(String orderCode) {
        return pendingOrders.get(orderCode);
    }

    public Order findCompletedOrder(String orderCode) {
        return completedOrders.get(orderCode);
    }

    public void completeOrder(String orderCode) {
        Order order = pendingOrders.remove(orderCode);
        if (order != null) {
            completedOrders.put(order.orderCode, order);
            recordSale(order);
        }
    }

    public void removePendingOrder(String code) {
        pendingOrders.remove(code);
    }

    public void recordSale(Order order) {
        String date = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        SalesRecord sale = new SalesRecord(
            date,
            order.orderCode,
            order.total,
            order.tax,
            order.total
        );
        salesHistory.add(sale);
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