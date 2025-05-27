# Point of Sale (POS) System

A comprehensive Java-based Point of Sale system built with Swing UI and MySQL database, designed for retail businesses.

## Project Structure

### Root Directory
- `pom.xml` - Maven project configuration and dependency management
- `pos.jar` - Executable JAR file
- `manifest.txt` - JAR manifest file
- SQL Files:
  - `pos_full_schema.sql` - Complete database schema
  - `init_database.sql` - Database initialization script
  - `categories.sql` - Categories data
  - `reset_cashier_password.sql` - Password reset script

### Source Code (`src/`)
#### Main Source (`src/main/java/com/pos/`)
- `Main.java` - Application entry point

#### Core Components
1. **Model Layer** (`model/`)
   - Data models and entities
   - Business objects (Products, Orders, Users)
   - Data validation and business rules

2. **Data Access Layer** (`dao/`)
   - Database access objects
   - CRUD operations
   - Data persistence management

3. **Service Layer** (`service/`)
   - Business logic implementation
   - Transaction management
   - Data processing and validation

4. **User Interface** (`ui/`)
   - Swing-based GUI components
   - Main application windows
   - User interaction handling

5. **Database** (`db/`)
   - Database connection management
   - Query execution
   - Connection pooling

6. **Utilities** (`utils/` and `util/`)
   - Helper classes and utilities
   - Constants and configuration
   - Shared functionality

### Resources (`src/main/resources/`)
- Configuration files
- Properties files
- Static resources

### Test Directory (`src/test/`)
- Unit tests
- Integration tests
- Test resources

### Libraries (`lib/`)
- External dependencies
- Third-party libraries

### Build Output (`target/`)
- Compiled classes
- Generated resources
- Build artifacts

## Database
The system uses MySQL database with the following features:
- Complete schema defined in `pos_db.sql`
- Initial data setup in `init_database.sql`
- Category management in `categories.sql`
- Cashier password management in `reset_cashier_password.sql`

## Building and Running
1. Prerequisites:
   - Java Development Kit (JDK) 8 or higher
   - Maven
   - MySQL Database

2. Build the project:
   ```bash
   mvn clean install
   ```

3. Run the application:
   ```bash
   java -jar pos.jar
   ```

## Dependencies
- Java Swing for UI
- MySQL Connector for database connectivity
- Maven for build management
- FlatLaf for modern UI components

## Features
- User authentication and authorization
- Product management
- Order processing
- Sales reporting
- Category management
- Cashier management
- Hardware integration (barcode scanners, receipt printers)

## Technical Details

### Data Access Layer (DAO) Structure

The DAO layer manages data persistence and database operations:

1. **ReceiptDesignDAO.java**
   - Receipt template management
   - Design persistence
   - Template configuration

2. **PrinterConfigDAO.java**
   - Printer settings management
   - Configuration persistence
   - Device preferences

### Utility Files

1. **Printing Tools**
   - `ReceiptPrinter.java` - Receipt printing functionality
   - `ReceiptGenerator.java` - Receipt template generation
   - `PrinterUtil.java` - Printer management and configuration

2. **Security Tools**
   - `CheckCashierAccount.java` - Cashier authentication
   - `CashierPasswordReset.java` - Password management
   - `PasswordGenerator.java` - Secure password generation
   - `PasswordTest.java` - Password strength validation
   - `AuthenticationTest.java` - Security testing

3. **UI Components**
   - `ThemeManager.java` - UI theme management
   - `FormatUtils.java` - Data formatting utilities

4. **Reporting and Logging**
   - `ReportGenerator.java` - Business reports generation
   - `AuditLogger.java` - System audit logging
   - `LogManager.java` - Log management

5. **System Management**
   - `ConfigLoader.java` - Configuration management
   - `BackupManager.java` - System backup functionality
   - `SessionManager.java` - User session management
   - `DatabaseReset.java` - Database maintenance

6. **Business Logic**
   - `BarcodeScanner.java` - Barcode processing
   - `ValidationUtils.java` - Data validation
   - `ExceptionHandler.java` - Error handling
   - `SaleNumberGenerator.java` - Transaction numbering

### System Architecture

1. **Transaction Processing**
   - Receipt generation and printing
   - Transaction numbering
   - Audit logging

2. **User Management**
   - Authentication and authorization
   - Session management
   - UI customization

3. **Error Handling**
   - Exception management
   - Logging
   - Backup procedures

4. **Configuration Management**
   - Settings management
   - Data validation
   - Audit tracking

### File Organization

```
src/main/java/com/pos/
├── dao/                    # Data Access Objects
│   ├── ReceiptDesignDAO.java
│   ├── PrinterConfigDAO.java
│   └── impl/              # Implementation details
│
└── utils/                 # Utility Classes
    ├── printing/         # Receipt and printer utilities
    ├── security/        # Security and authentication
    ├── ui/             # User interface utilities
    ├── reports/        # Reporting utilities
    ├── system/         # System management
    └── store/          # Business logic utilities
```

## System Requirements

- Java Development Kit (JDK) 8 or higher
- MySQL Database
- NetBeans IDE (recommended)
- FlatLaf library
- MySQL JDBC Connector

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Contributors

- Your Name
- Other Contributors

## Acknowledgments

- FlatLaf for the modern UI components
- MySQL for the database
- NetBeans IDE for development support

## Project Structure

```
/pos-system
│
├── /src
│   ├── /main
│       ├── /java
│           ├── /com/pos
│               ├── /ui (Swing GUI classes)
│               ├── /db (JDBC connection and DAO)
│               ├── /model (POJOs: Product, User, Sale, etc.)
│               ├── /utils (Utilities for printing, security, etc.)
│               └── Main.java
│       ├── /resources (icons, splash screen image, etc.)
│
├── /sql (database schema)
├── /lib (external libraries)
├── pom.xml (Maven project file)
├── build.bat (Windows build script)
├── run.bat (Windows run script)
└── README.md (this file)
```

## Using the POS System

### Admin Dashboard

1. **Inventory Management**: Add, edit, or remove products. Set low stock thresholds.
2. **User Management**: Add new users, edit permissions, reset passwords.
3. **Reports**: Generate and export sales, inventory, and low stock reports.

### Cashier Dashboard

1. **Checkout**: Scan or select products, adjust quantities, and process payment.
2. **Search Products**: Find products by name or scan barcodes.
3. **Receipts**: Print receipts for completed transactions.

## Hardware Integration

- **Barcode Scanner**: Any barcode scanner that acts as a keyboard input device
- **Receipt Printer**: Compatible with ESC/POS commands
- **Cash Drawer**: Opens via commands sent through the receipt printer

## Troubleshooting

If you encounter issues:

1. Check database connection settings in `DatabaseConnection.java`
2. Verify all required libraries are in the `lib` directory
3. Check the console for error messages
4. Ensure the MySQL server is running

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Contributors

- Your Name
- Other Contributors

## Acknowledgments

- FlatLaf for the modern UI components
- MySQL for the database
- NetBeans IDE for development support

## Technical Details for Developers 🔧

### Data Access Layer (DAO) Structure

The DAO (Data Access Object) layer is like a special messenger between our store helper and its memory. Here's how it works:

1. **ReceiptDesignDAO.java**
   - Handles how our receipts look
   - Saves and loads receipt designs
   - Manages receipt templates

2. **PrinterConfigDAO.java**
   - Takes care of printer settings
   - Saves how we want our printer to work
   - Remembers printer preferences

### Utility Files (The Helper Tools)

Our store helper has many special tools to make everything work smoothly:

1. **Printing Tools**
   - `ReceiptPrinter.java` - The main tool that prints receipts
   - `ReceiptGenerator.java` - Creates the receipt design
   - `PrinterUtil.java` - Helps with printer setup and management

2. **Security Tools**
   - `CheckCashierAccount.java` - Makes sure only real cashiers can use the system
   - `CashierPasswordReset.java` - Helps cashiers reset their passwords
   - `PasswordGenerator.java` - Creates strong passwords
   - `PasswordTest.java` - Tests if passwords are strong enough
   - `AuthenticationTest.java` - Tests if the security system works

3. **Look and Feel**
   - `ThemeManager.java` - Makes our store helper look pretty
   - `FormatUtils.java` - Makes numbers and text look nice

4. **Reports and Logs**
   - `ReportGenerator.java` - Creates reports about our store
   - `AuditLogger.java` - Keeps track of important changes
   - `LogManager.java` - Manages all our store's records

5. **System Management**
   - `ConfigLoader.java` - Loads all our store helper's settings
   - `BackupManager.java` - Makes copies of our store's information
   - `SessionManager.java` - Keeps track of who's using the system
   - `DatabaseReset.java` - Helps fix the store's memory if something goes wrong

6. **Store Tools**
   - `BarcodeScanner.java` - Reads barcodes on products
   - `ValidationUtils.java` - Makes sure all information is correct
   - `ExceptionHandler.java` - Catches and fixes problems
   - `SaleNumberGenerator.java` - Creates unique numbers for each sale

### How Everything Works Together

1. **When Someone Buys Something**:
   - The store helper uses `ReceiptPrinter.java` to print a receipt
   - `SaleNumberGenerator.java` gives the sale a special number
   - `AuditLogger.java` remembers what was sold

2. **When a Cashier Logs In**:
   - `CheckCashierAccount.java` checks if they're allowed
   - `SessionManager.java` keeps track of their work
   - `ThemeManager.java` shows them their special screen

3. **When Something Goes Wrong**:
   - `ExceptionHandler.java` catches the problem
   - `LogManager.java` writes down what happened
   - `BackupManager.java` makes sure we don't lose information

4. **When We Need to Change Something**:
   - `ConfigLoader.java` loads the current settings
   - `ValidationUtils.java` makes sure changes are correct
   - `AuditLogger.java` remembers what was changed

### File Organization

```
src/main/java/com/pos/
├── dao/                    # Data Access Objects
│   ├── ReceiptDesignDAO.java
│   ├── PrinterConfigDAO.java
│   └── impl/              # Implementation details
│
└── utils/                 # Helper Tools
    ├── printing/         # Receipt and printer tools
    ├── security/        # Password and safety tools
    ├── ui/             # Look and feel tools
    ├── reports/        # Report making tools
    ├── system/         # System management tools
    └── store/          # Store operation tools
```

Each tool has a special job, and they all work together to make our store helper smart and helpful! 🎯 