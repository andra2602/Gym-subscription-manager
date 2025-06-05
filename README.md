# 💪 Fitness Center Management App (Java Console-Based Application)

## 📌 Overview

This is a Java-based console application designed to manage a fitness center.

It offers a complete experience for **members**, **trainers**, and **managers**, with personalized menus and role-specific functionality.

The app allows:
- User registration and login
- Personal training session booking
- Fitness class scheduling and management
- Payment history tracking
- Promotion creation and management
- Leaving and viewing trainer reviews (anonymous)

## ℹ️ Interactive Usage Only – No Code Modification Required

All application features are available **exclusively through the interactive console menus**, structured by user roles (Member, Trainer, Manager).

You can:
- Register or log in
- Create/update/delete subscriptions, trainers, classes, reviews, payments, and more
- All actions are **menu-driven**, guided by clear prompts
- **No manual code editing** is needed to create or test data
- All modifications are reflected in real-time in the SQLite database


## 🗂️ Database Details
Type: SQLite

Driver: sqlite-jdbc (v3.49.1.0)

Database file: GYMFinalDatabase.db (auto-generated in project root)

All tables are created programmatically using SQL statements in **DatabaseSetup.java**.

Run **DatabaseSetup.java** manually once before first use to initialize the schema.

## ⚙️ Setup Instructions (Creating the Database)

To create the SQLite database and all necessary tables, please follow these steps:

1. Run `DatabaseSetup.java` once.  
   This will automatically create all required tables using `CREATE TABLE` SQL statements embedded in the code.  
   ❗ There is no need to run external `.sql` files — everything is handled programmatically.

2. Then run `StartUp.java`.  
   At startup, the app checks if the database is empty and automatically populates it with test data by running `DatabaseSeeder.java`.

## Test Data (Seeder)
The app includes automatic test data insertion via **DatabaseSeeder.java**, which runs automatically at startup if the database is empty.

Preloaded content:

✔️ 3 Trainers

✔️ 3 Members (some already assigned to trainers)

✔️ 2 Promotions:
- 1 active
- 1 upcoming

## 👥 User Roles and Main Features

### 👤 Member

1. **Subscriptions**
    - View, create, extend, or cancel a subscription
    - Extend is allowed only when the subscription is close to expiry (≤5 days)
    - Optionally apply promotion codes
    - Students benefit from a built-in 30% discount

2. **Fitness Classes**
   - View available fitness classes
   - Book a class if spots are available
   - Apply active promotions when booking
   - Refund is issued if class is cancelled

3. **Trainers**
    - Browse all active trainers
    - Use the personal trainer submenu:
        - Assign a personal trainer
        - View current personal trainer
        - Remove personal trainer
        - Book a session with either your trainer or another one

4. **Reviews**
    - Leave a review (1–5 stars) for a trainer *only after attending a session*
    - All reviews are **anonymous** — trainers cannot see who rated them
    - Trainers have a visible average rating

5. **Payments**
    - View all payments (subscriptions, classes, personal training)
    - See total money spent
    - Refunds appear in payment history

6. **Promotions**
   - View **active** and **upcoming** promotions
   - Apply them when creating subscriptions or joining classes

7. **Account Deletion**
   - Fully deletes:
      - Your subscription
      - Payments
      - Class bookings
      - Personal trainer & trainer slots


### 💪 Trainer

1. **Members You Train**
    - View the members assigned to you
    - Filter by experience level

2. **Manage Fitness Classes**
    - Add new fitness classes (between 7 and 30 days in advance)
    - Delete existing classes — participants get refunds
    - View all classes you coordinate

3. **Your Schedule**
    - View your schedule for any given date
    - Time slots are dynamically populated with classes or personal bookings

4. **Your Rating**
    - View individual reviews and your overall average score
    - 
5. **Update Hourly Rate**
    - Trainers can update their price per hour directly from the menu
    - All changes are logged in the audit system

6. **Account Deletion**
    - Fully deletes trainer profile and related data


### 👨‍💼 Manager

1. **Members & Trainers**
    - View a list of all registered members and trainers

2. **Promotions**
    - Add new promotions (with validations for dates and content)
    - Edit, deactivate, reactivate, or delete promotions

3. **Finance**
    - View earnings in a selected time frame
    
4. **Audit**
    - View a .csv log of key actions: deletions, payments, reviews, bookings
    - You can select to view full audit or for a period of time.


## ✅ Input Validations

- **Username**: must be unique and valid
- **Email**: format validated
- **Phone number**: Romanian format expected
- **Password**: minimum 8 characters, includes uppercase, number, and symbol
- **Dates and Times**:
    - Cannot book in the past
    - Fitness classes must be scheduled 7–30 days in advance
    - Time slots are restricted to 06:00–23:00


## 🔧 Technical Overview

- `StartUp.java`: Main entry point — launches the interactive console, manages login, registration, and role-based menus
- `DBConnection.java`: Singleton class for managing SQLite database connections
- `DatabaseSetup.java`: Creates all tables (users, subscriptions, trainers, payments, etc.) using SQL — must be run manually once at the beginning
- `DatabaseSeeder.java`: Populates test data (trainers, members, promotions) — runs automatically at startup if the DB is empty
- `AuditService.java`: Logs important user actions to `audit.csv` (account deletions, reviews, bookings, payments...)
- `Validator.java`: Strict format validation for email, phone, password, dates, and time
- `DAO Layer`: Handles direct SQL interaction with each table
- `Service Layer`: Contains all business logic, grouped by role (MemberService, TrainerService, etc.)
- UI: Console-only interface, menu-driven with user prompts and validations  
  
**No code editing required** — all operations are accessible through guided menus

## 📟 Core Functionality

- Full CRUD operations for Members, Trainers, Promotions, Subscriptions, Payments, Fitness Classes, and more
- Interactive, role-based menu UI (Member, Trainer, Manager)
- Booking system with seat validation and refund mechanism
- Personal trainer scheduling and session booking
- Anonymous reviews with post-session validation and star-rating system
- Dynamic trainer assignment/removal with checks
- Subscription extension with date validations and discount rules
- Promotion system with date and usage validation
- Account deletion with cascading cleanup
- Audit logging system to track sensitive actions to CSV

## 📄 Database Integration

- SQLite relational database with normalized schema
- Version: sqlite-jdbc v3.49.1.0
- All tables created in `DatabaseSetup.java` (programmatically)
- DAO pattern used per entity, with clear separation of concerns
- `BaseDAO` utility for shared DB logic
- Singleton pattern across DAO and Service layers
- Service Layer encapsulates validation and coordination logic
- All data inserted and read using JDBC
- No `.sql` files required — schema and seed handled 100% in Java

## Optional Tools Used

To **inspect and validate database changes in real time**, I used:

 **DB Browser for SQLite**  
A lightweight GUI that allowed me to monitor inserts, updates, and deletes as they occurred — no SQL queries required.

This made it easy to:
- Track class bookings or subscription changes instantly
- Debug database issues quickly
- Confirm integrity of cascading deletions

> Highly recommended for anyone reviewing or testing this project.


## Technologies

- Java 21
- IntelliJ IDEA
- JDBC
- SQLite
- Full Object-Oriented Design 

## 💡 Future Improvements

- Full GUI interface (JavaFX or Swing)
- Email notifications for class changes

## 🧭 How to Run the Application

### Follow these simple steps to get started:

- ✅ Run DatabaseSetup.java (only once)

This will automatically create all required tables in the GYMFinalDatabase.db SQLite file.

- Run StartUp.java

The application launches into the interactive console.

If the database is empty, it will automatically run DatabaseSeeder.java to load test data:

→ Trainers, Members, Promotions, etc.

- 🎮 Use the console menus

All actions (registration, login, bookings, payments, reviews, etc.) are performed entirely from the menu system

No code editing is needed to insert or modify data

All changes are stored and reflected live in the database

___

🎓 This project was created as part of the Object-Oriented Programming Laboratory in Java.

