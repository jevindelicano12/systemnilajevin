import java.util.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CashierModule {
    public static void cashierSystem() {
        // Cashier loop with logout
        while (true) {
            System.out.println("\n╔═══════════════════════════════════════════════════════════╗");
            System.out.println("║               CASHIER PAYMENT SYSTEM                        ║");
            System.out.println("╠═══════════════════════════════════════════════════════════╣");
            System.out.println("║                                                           ║");
            System.out.println("║    [1]  Process Payment by Order Number                   ║");
            System.out.println("║    [2]  Logout                                            ║");
            System.out.println("║                                                           ║");
            System.out.println("╚═══════════════════════════════════════════════════════════╝");
            int choice = BrewiseCoffeeShop.getInt("  Enter choice [1-2]: ", 1, 2);
            if (choice == 2) return;

            System.out.print("  Enter Order Number (e.g., 5847): ");
            String orderCode = BrewiseCoffeeShop.sc.nextLine().trim();
            
            BrewiseCoffeeShop.Order o = BrewiseCoffeeShop.store.findPendingOrder(orderCode);
            if (o == null) { System.out.println("  [ERROR] Order not found!"); continue; }

            System.out.println("\n╔═══════════════════════════════════════════════════════════╗");
            System.out.println("║              ORDER DETAILS FOR PAYMENT                     ║");
            System.out.println("╠═══════════════════════════════════════════════════════════╣");
            System.out.println("  Order Number: " + o.orderCode);
            o.printSummary();
            
            // generate transaction id when cashier processes the payment
            if (o.transactionId == null) o.transactionId = BrewiseCoffeeShop.generateTransactionId();
            System.out.println("  Transaction ID: " + o.transactionId);
            System.out.println("  Payment Method: Cash Only");
            System.out.println("  Total Amount Due: " + String.format("%.2f", o.totalPrice) + " PHP");
            System.out.print("\n  Received Cash Amount: ");
            
            double cash = 0;
            try {
                cash = Double.parseDouble(BrewiseCoffeeShop.sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("  [ERROR] Invalid cash amount!");
                continue;
            }

            double change = cash - o.totalPrice;
            if (change < 0) { 
                double amountNeeded = Math.abs(change);
                System.out.println("\n╔═══════════════════════════════════════════════════════════╗");
                System.out.println("║             PAYMENT INCOMPLETE - INSUFFICIENT FUNDS        ║");
                System.out.println("╠═══════════════════════════════════════════════════════════╣");
                System.out.println("║  Total Amount Due:  " + String.format("%-37.2f", o.totalPrice) + "║");
                System.out.println("║  Cash Received:     " + String.format("%-37.2f", cash) + "║");
                System.out.println("║  Amount Needed:     " + String.format("%-37.2f", amountNeeded) + "║");
                System.out.println("║                                                           ║");
                System.out.println("║  Please collect an additional " + String.format("%.2f", amountNeeded) + " PHP            ║");
                System.out.println("╚═══════════════════════════════════════════════════════════╝");
                continue; 
            }

            System.out.println("\n╔═══════════════════════════════════════════════════════════╗");
            System.out.println("║              PAYMENT SUCCESSFUL                            ║");
            System.out.println("╠═══════════════════════════════════════════════════════════╣");
            System.out.println("  Transaction ID: " + o.transactionId);
            System.out.println("  Cash Received:  " + String.format("%.2f", cash) + " PHP");
            System.out.println("  Change:         " + String.format("%.2f", change) + " PHP");
            System.out.println("  Total:          " + String.format("%.2f", o.totalPrice) + " PHP");
            System.out.println("╚═══════════════════════════════════════════════════════════╝");
            o.paid = true;
            o.paymentTime = LocalDateTime.now();
            BrewiseCoffeeShop.store.completeOrder(orderCode);

            // Generate detailed receipt
            printDetailedReceipt(o, cash, change);
        }
    }
    
    private static void printDetailedReceipt(BrewiseCoffeeShop.Order o, double cash, double change) {
        String currentCashier = BrewiseCoffeeShop.currentCashier != null ? BrewiseCoffeeShop.currentCashier : "System";
        LocalDateTime now = o.paymentTime != null ? o.paymentTime : LocalDateTime.now();
        String formattedTime = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        
        System.out.println("\n╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║                    OFFICIAL RECEIPT                        ║");
        System.out.println("╠═══════════════════════════════════════════════════════════╣");
        System.out.println("║                 BREWISE COFFEE SHOP                        ║");
        System.out.println("║                                                           ║");
        System.out.println("║  Order #:          " + padRight(o.orderCode, 38) + "║");
        System.out.println("║  Transaction ID:   " + padRight(o.transactionId, 38) + "║");
        System.out.println("╠═══════════════════════════════════════════════════════════╣");
        
        // Items ordered
        System.out.println("║  ITEMS ORDERED:                                           ║");
        if (o.items != null && !o.items.isEmpty()) {
            for (int i = 0; i < o.items.size(); i++) {
                OrderItem item = o.items.get(i);
                String itemName = item.product.name + " x" + item.quantity;
                String itemPrice = String.format("%.2f", item.subtotal) + " PHP";
                System.out.println("║    " + padRight(itemName, 35) + padRight(itemPrice, 20) + "║");
                
                // Add-ons
                if (item.getAddOns() != null && !item.getAddOns().isEmpty()) {
                    for (AddOn addon : item.getAddOns()) {
                        int qty = item.getAddOnQuantity(addon);
                        String addonLine = "      + " + addon.name + " x" + qty;
                        System.out.println("║    " + padRight(addonLine, 55) + "║");
                    }
                }
            }
        } else {
            System.out.println("║    " + padRight(o.drink, 51) + "║");
        }
        
        System.out.println("╠═══════════════════════════════════════════════════════════╣");
        System.out.println("║  Cashier:          " + padRight(currentCashier, 38) + "║");
        System.out.println("║  Date & Time:      " + padRight(formattedTime, 38) + "║");
        System.out.println("╠═══════════════════════════════════════════════════════════╣");
        System.out.println("║  Total Amount:        " + String.format("%32.2f", o.totalPrice) + " PHP  ║");
        System.out.println("║  Cash Received:       " + String.format("%32.2f", cash) + " PHP  ║");
        System.out.println("║  Change:              " + String.format("%32.2f", change) + " PHP  ║");
        System.out.println("╠═══════════════════════════════════════════════════════════╣");
        System.out.println("║  Payment Method:   CASH                                    ║");
        System.out.println("║  Status:           PAID [COMPLETE]                         ║");
        System.out.println("╠═══════════════════════════════════════════════════════════╣");
        System.out.println("║  Thank you for your business!                             ║");
        System.out.println("║  We appreciate your patronage!                            ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
    }
    
    private static String padRight(String s, int n) {
        return String.format("%-" + n + "s", s).substring(0, Math.min(n, s.length() + n - s.length()));
    }
}

