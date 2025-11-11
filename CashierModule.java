import java.util.*;

public class CashierModule {
    public static void cashierSystem() {
        // Cashier loop with logout
        while (true) {
            System.out.println("\n--- CASHIER SYSTEM ---");
            System.out.println("1. Process Order by Order Number");
            System.out.println("2. Logout");
            int choice = BrewiseCoffeeShop.getInt("Enter choice: ", 1, 2);
            if (choice == 2) return;

            System.out.print("Enter Order Number: ");
            int orderNumber = Integer.parseInt(BrewiseCoffeeShop.sc.nextLine());
            Order o = BrewiseCoffeeShop.store.findPendingOrder(String.valueOf(orderNumber));
            if (o == null) { System.out.println("❌ Order not found!"); continue; }

            System.out.println("\n--- ORDER DETAILS ---");
            o.printSummary();
            // generate transaction id when cashier processes the payment
            if (o.transactionId == null) o.transactionId = BrewiseCoffeeShop.generateTransactionId();
            System.out.println("(Will print receipt with Transaction ID)");
            System.out.println("Payment Method: Cash Only");
            System.out.print("Received Cash: ₱");
            double cash = Double.parseDouble(BrewiseCoffeeShop.sc.nextLine());

            double change = cash - o.totalPrice;
            if (change < 0) { System.out.println("❌ Not enough cash!"); continue; }

            System.out.println("\n--- PAYMENT SUCCESS ---");
            System.out.println("Transaction ID: " + o.transactionId);
            System.out.println("Received Cash: ₱" + cash);
            System.out.println("Change: ₱" + change);
            System.out.println("Total: ₱" + o.totalPrice);
            o.paid = true;
            BrewiseCoffeeShop.store.completeOrder(String.valueOf(orderNumber));

            // Generate and print receipt
            System.out.println("\n--- RECEIPT ---");
            o.printSummary();
            System.out.println("Transaction ID: " + o.transactionId);
            System.out.println("Payment Method: Cash");
            System.out.println("Received Cash: ₱" + cash);
            System.out.println("Change: ₱" + change);
            System.out.println("Thank you for your purchase!");
        }
    }
}
