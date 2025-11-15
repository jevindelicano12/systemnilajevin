import java.io.Serializable;

public class InventoryItem implements Serializable {
    private static final long serialVersionUID = 1L;
    String id;
    String name;
    String unit;
    double quantity;
    double reorderThreshold;
    boolean isLowStock;  // Flag to track if item is low on stock

    InventoryItem(String id, String name, String unit, double quantity, double reorderThreshold) {
        this.id = id;
        this.name = name;
        this.unit = unit;
        this.quantity = quantity;
        this.reorderThreshold = reorderThreshold;
        this.isLowStock = quantity <= reorderThreshold;  // Check if already below threshold
    }
    
    // Check if stock is low and update flag
    boolean checkAndUpdateLowStock() {
        this.isLowStock = quantity <= reorderThreshold;
        return this.isLowStock;
    }
}