# Fashion Store Management System

Java Swing desktop application for a group project with a client dashboard, product catalog, cart checkout, and SQLite persistence.

## Latest updates

- Client dashboard product prices now display in Nigerian Naira (`₦`).
- Checkout totals and shipping options have been updated to use Naira as the currency.
- Improved cart experience with clearer order summaries.
- Consistent UI updates for a smoother shopping workflow.

## Features

- Login page with username/password
- Client dashboard with searchable product listings
- Add products to cart and review order details
- Checkout workflow with shipping and payment steps
- SQLite database connection using a singleton `DatabaseManager`
- Search products by name or category

## Default login

- Admin username: `admin`
- Admin password: `password123`

## Client login

- Client username: `client`
- Client password: `client123`

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
