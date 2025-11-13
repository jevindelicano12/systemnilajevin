import java.io.Serializable;

/**
 * Represents a cashier account with username and password
 */
public class CashierAccount implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public String username;
    public String password;
    public String status;  // "ACTIVE" or "INACTIVE"
    public long createdDate;
    
    public CashierAccount(String username, String password) {
        this.username = username;
        this.password = password;
        this.status = "INACTIVE";  // New cashiers start as INACTIVE
        this.createdDate = System.currentTimeMillis();
    }
    
    public CashierAccount(String username, String password, String status) {
        this.username = username;
        this.password = password;
        this.status = status;
        this.createdDate = System.currentTimeMillis();
    }
    
    public void deactivate() {
        this.status = "INACTIVE";
    }
    
    public void activate() {
        this.status = "ACTIVE";
    }
    
    public boolean isActive() {
        return status.equals("ACTIVE");
    }
    
    public void changePassword(String newPassword) {
        this.password = newPassword;
    }
    
    @Override
    public String toString() {
        return username + " [" + status + "]";
    }
}
