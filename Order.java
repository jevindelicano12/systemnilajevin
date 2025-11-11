import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;

public class Order implements Serializable {
    private static final long serialVersionUID = 1L;
    static final double TAX_RATE = 0.12;

    String orderCode;
    LocalDateTime datetime;
    String location;
    String contactNumber;
    List<OrderItem> items;
    double subtotal;
    double tax;
    double total;
    double paymentReceived;
    double change;
    String dineOption;     // "Dine-in" or "Take-out"
    String customerComplaint;
    String cashierName;
    boolean paid = false;

    // Old system fields for compatibility
    int orderNumber;
    String transactionId;
    String drink;
    int quantity;
    String sugar;
    List<String> addOns;
    Map<String, Integer> addOnsQty;
    double totalPrice;
    double basePricePerUnit;

    public Order() {
        this(BrewiseCoffeeShop.store.generateOrderCode());
    }

    public Order(String orderCode) {
        this.orderCode = orderCode;
        this.datetime = LocalDateTime.now();
        this.location = Constants.LOCATION_NAME;
        this.contactNumber = Constants.CONTACT_NUMBER;
        this.items = new ArrayList<>();
        this.subtotal = 0;
        this.tax = 0;
        this.total = 0;
        this.paymentReceived = 0;
        this.change = 0;
        this.dineOption = "";
        this.customerComplaint = "";
        this.cashierName = "";
        this.paid = false;
        // Initialize old system fields
        this.orderNumber = 0;
        this.transactionId = null;
        this.drink = "";
        this.quantity = 0;
        this.sugar = "";
        this.addOns = new ArrayList<>();
        this.addOnsQty = new HashMap<>();
        this.totalPrice = 0;
        this.basePricePerUnit = 0;
    }

    // Old system constructor for compatibility
    public Order(String drink, int quantity, String sugar, List<String> addOns, Map<String,Integer> addOnsQty, double totalPrice, double basePricePerUnit) {
        this.drink = drink; this.quantity = quantity; this.sugar = sugar;
        this.addOns = new ArrayList<>(addOns);
        this.addOnsQty = new HashMap<>(addOnsQty);
        this.basePricePerUnit = basePricePerUnit;
        this.totalPrice = totalPrice;
        this.transactionId = null;
        this.paid = false;
        // Initialize new system fields
        this.orderCode = "";
        this.datetime = LocalDateTime.now();
        this.location = Constants.LOCATION_NAME;
        this.contactNumber = Constants.CONTACT_NUMBER;
        this.items = new ArrayList<>();
        this.subtotal = totalPrice;
        this.tax = 0;
        this.total = totalPrice;
        this.paymentReceived = 0;
        this.change = 0;
        this.dineOption = "";
        this.customerComplaint = "";
        this.cashierName = "";
    }

    public void addItem(OrderItem item) {
        items.add(item);
        recalculate();
    }

    public void removeItem(OrderItem item) {
        items.remove(item);
        recalculate();
    }

    void recalculate() {
        this.subtotal = items.stream().mapToDouble(OrderItem::getTotal).sum();
        this.tax = Math.round(this.subtotal * TAX_RATE * 100) / 100.0;
        this.total = this.subtotal + this.tax;
    }

    // Old system recalcTotal for compatibility
    void recalcTotal() {
        double addonsTotal = 0.0;
        for (Integer v : addOnsQty.values()) addonsTotal += v * 10.0;
        this.totalPrice = basePricePerUnit * quantity + addonsTotal;
        this.subtotal = totalPrice;
        this.total = totalPrice;
    }

    void printSummary() {
        // Check if this is an old system order (has drink field) or new system order (has items)
        if (!drink.isEmpty()) {
            // Old system order
            System.out.println("║  Order Number: " + orderNumber);
            System.out.println("║  🥤 Drink: " + drink);
            System.out.println("║     └─ Size/Variant & Qty: x" + quantity + "");
            System.out.println("║     └─ Unit Price: ₱" + String.format("%.2f", basePricePerUnit));

            if (!addOns.isEmpty()) {
                System.out.println("║  ➕ Add-ons:");
                // show each unique addon and its qty
                List<String> unique = new ArrayList<>(new HashSet<String>(addOns));
                for (String a : unique) {
                    int qty = addOnsQty.getOrDefault(a, 0);
                    System.out.println("║     • " + a + " x" + qty + " = ₱" + (qty * 10));
                }
            }
            System.out.println("║  🍯 Sugar Level: " + sugar);
            System.out.println("║  💰 Item Total: ₱" + String.format("%.2f", totalPrice));
        } else {
            // New system order
            System.out.println("╔════════════════════════════════════════╗");
            System.out.println("║         📝 ORDER SUMMARY 📝           ║");
            System.out.println("╠════════════════════════════════════════╣");
            System.out.println("║  Order Code: " + orderCode);
            System.out.println("║  Date/Time: " + datetime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            System.out.println("║  Location: " + location);
            System.out.println("║  Contact: " + contactNumber);
            if (!items.isEmpty()) {
                System.out.println("╠════════════════════════════════════════╣");
                System.out.println("║  ITEMS:");
                for (OrderItem item : items) {
                    Product p = item.product;
                    System.out.println("║  🥤 " + p.name + " " + p.flavour);
                    System.out.println("║     └─ Sugar Level: " + item.sugarLevel);
                    System.out.println("║     └─ Quantity: x" + item.quantity);
                    System.out.println("║     └─ Unit Price: ₱" + String.format("%.2f", p.price));
                    if (!item.getAddOns().isEmpty()) {
                        System.out.println("║     └─ Add-ons:");
                        for (AddOn addon : item.getAddOns()) {
                            int qty = item.getAddOnQuantity(addon);
                            System.out.println("║        • " + addon.name + " x" + qty +
                                    " = ₱" + String.format("%.2f", addon.price * qty));
                        }
                    }
                    System.out.println("║     └─ Item Total: ₱" + String.format("%.2f", item.getTotal()));
                }
            }
            System.out.println("╠════════════════════════════════════════╣");
            System.out.println("║  Subtotal: ₱" + String.format("%.2f", subtotal));
            System.out.println("║  Tax: ₱" + String.format("%.2f", tax));
            System.out.println("║  TOTAL: ₱" + String.format("%.2f", total));
            if (paid) {
                System.out.println("║  Payment Received: ₱" + String.format("%.2f", paymentReceived));
                System.out.println("║  Change: ₱" + String.format("%.2f", change));
            }
            if (!dineOption.isEmpty()) {
                System.out.println("║  Dine Option: " + dineOption);
            }
            if (!customerComplaint.isEmpty()) {
                System.out.println("║  Customer Notes: " + customerComplaint);
            }
            if (!cashierName.isEmpty()) {
                System.out.println("║  Cashier: " + cashierName);
            }
            System.out.println("╚════════════════════════════════════════╝");
        }
    }
}