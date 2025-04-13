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

---

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

## 🧪 Test Data Loaded on Startup

- [x] 3 Trainers
- [x] 3 Members (assigned to trainers)
- [x] 3 Promotions:
   - 1 Active
   - 1 Upcoming
   - 1 Expired
- [x] One member has a subscription expiring in 3 days

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

## 💡 Future Improvements

- Add data persistence (file storage, JSON, or database integration)
- Implement full audit logging


---

🎓 This project was created as part of the Object-Oriented Programming Laboratory in Java.

