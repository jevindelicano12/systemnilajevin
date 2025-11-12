# ☕ Brewise Coffee Shop

A Java-based coffee shop management system with customer ordering, basket management, and cashier functionality.

## 📋 Prerequisites

- **Java JDK 17 or higher** ([Download here](https://www.oracle.com/java/technologies/downloads/))
- A Java IDE (VS Code, IntelliJ IDEA, Eclipse, or NetBeans)

## 🚀 How to Run

### Windows (PowerShell)
```powershell
# Navigate to the project directory
cd path\to\BrewiseCoffeeShop.java

# Compile all Java files
javac *.java

# Run the main program
java BrewiseCoffeeShop
```

### Mac/Linux (Terminal)
```bash
# Navigate to the project directory
cd path/to/BrewiseCoffeeShop.java

# Compile all Java files
javac *.java

# Run the main program
java BrewiseCoffeeShop
```

## 🎯 Features

- **Customer Panel**: Browse menu, customize drinks, manage basket
- **Basket System**: Add, edit, and remove orders before checkout
- **Cashier System**: Process payments and generate receipts
- **Admin Panel**: View inventory and manage products
- **Persistence**: Orders and data saved automatically

## 📂 Project Structure

```
BrewiseCoffeeShop.java/
├── BrewiseCoffeeShop.java    # Main entry point
├── Store.java                # Product & order management
├── CustomerModule.java       # Customer interface
├── CashierModule.java        # Cashier interface
├── AdminModule.java          # Admin interface
├── Order.java                # Order model (separate)
├── OrderItem.java            # Order item model
├── Product.java              # Product model
├── AddOn.java                # Add-on model
├── InventoryItem.java        # Inventory model
├── PersistenceManager.java   # Data persistence
└── Constants.java            # System constants
```

## 🔐 Login Credentials

- **Admin Password**: `admin123`
- **Cashier Password**: `cashier123`
- **Admin Login**: Type `admin:login` in main menu
- **Cashier Login**: Type `cashier:login` in main menu

## 🛠️ Troubleshooting

**Error: "javac is not recognized"**
- Install Java JDK and add it to your PATH environment variable

**Error: "Could not find or load main class"**
- Make sure you're in the correct directory
- Recompile with `javac *.java`

## 📝 Notes

- Data is automatically saved to `store.json`
- Orders persist between sessions
- Basket is cleared after checkout

## 👥 Contributing

Feel free to fork and submit pull requests!

## 📄 License

Free to use for educational purposes.
