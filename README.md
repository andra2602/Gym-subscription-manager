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

---

## 👥 User Roles and Main Features

### 👤 Member

1. **Subscriptions**
    - View subscription details
    - Create, update, or cancel a subscription

2. **Fitness Classes**
    - View available fitness classes
    - Schedule participation in a class

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

6. **Promotions**
    - View active and upcoming promotions

7. **Account**
    - Permanently delete your member account

---

### 💪 Trainer

1. **Members You Train**
    - View the members assigned to you
    - Filter by experience level

2. **Manage Fitness Classes**
    - Add new fitness classes (between 7 and 30 days in advance)
    - Delete existing classes
    - View all classes you coordinate

3. **Your Schedule**
    - View your schedule for any given date
    - Time slots are dynamically populated with classes or personal bookings

4. **Your Rating**
    - View individual reviews and your overall average score

---

### 👨‍💼 Manager

1. **Members & Trainers**
    - View a list of all registered members and trainers

2. **Promotions**
    - Add new promotions (with validations for dates and content)
    - Edit, deactivate, reactivate, or delete promotions

3. **Finance (future extension)**
    - View earnings in a selected time frame
    - Export data to `.csv` for audit or analysis

---

## ✅ Input Validations

- **Username**: must be unique and valid
- **Email**: format validated
- **Phone number**: Romanian format expected
- **Password**: minimum 8 characters, includes uppercase, number, and symbol
- **Dates and Times**:
    - Cannot book in the past
    - Fitness classes must be scheduled 7–30 days in advance
    - Time slots are restricted to 06:00–23:00

---

## 🧪 Predefined Users

The app starts with:
- 1 predefined trainer (with empty schedule and reviews)
- 1 predefined test member
- 2 additional members assigned to the trainer

---

## 🔧 Technical Overview

- `StartUp.java`: main entry point, manages menus, login, and registration
- Service classes (`MemberService`, `TrainerService`, `ManagerService`, etc.) handle business logic
- `Validator.java`: enforces strict input validation
- Console-based UI with structured menus per user type

---

## 🚀 Technologies

- Java 21
- Full Object-Oriented Design 
- Use of Java Collections (`List`, `Set`, `Map`)
- `Scanner` for input
- Clear menu-driven interface in console

---

## 💡 For Future Work

- Add data persistence (file storage, JSON, or database integration)
- Implement full audit logging


---

🎓 This project was created as part of the Object-Oriented Programming Laboratory in Java.

