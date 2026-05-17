# Fashion Store Management System

Java Swing desktop application for a group project.

## Features

- Login page with username/password
- Dashboard for adding and viewing products
- SQLite database connection using a singleton `DatabaseManager`
- Add product and retrieve product list operations
- Search products by name or category

## Default login

- Username: `admin`
- Password: `password123`

## Run the application

1. Install Java 17+ and Maven.
2. From the project root, run:
   ```bash
   mvn clean package
   mvn exec:java -Dexec.mainClass="com.fashionstore.Main"
   ```

Or run the generated jar:

```bash
java -jar target/fashion-store-management-1.0-SNAPSHOT.jar
```

## Database

The app uses a local SQLite file named `fashion_store.db` created automatically when the application starts.
