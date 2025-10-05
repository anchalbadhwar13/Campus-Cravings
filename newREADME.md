# 🍔 Campus Cravings

A student-focused food delivery app built for the Surge Hackathon. Connect hungry students with nearby restaurants and student delivery partners on campus.

## 🎯 Problem Statement

Students are often too busy studying or working to leave their spot and get food. Campus Cravings solves this by enabling:
- **Students** to order food from nearby campus restaurants
- **Other students** to earn money by delivering food to their peers

## ✨ Features

### For Customers 🛒
- Browse nearby campus restaurants
- View menus and add items to cart
- Place orders with specific delivery locations (building/room)
- Track order status in real-time
- View order history

### For Delivery Partners 🚗
- View available delivery orders
- Accept orders and earn delivery fees
- Update order status (Accepted → Picked Up → Delivered)
- Track active deliveries

### Core Functionality ⚡
- **Real-time Updates**: Orders update instantly using Firestore listeners
- **Role-based Access**: Separate interfaces for customers and delivery partners
- **Secure Authentication**: Email/password authentication via Firebase
- **Clean Architecture**: MVVM pattern with Hilt dependency injection

## 🛠️ Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose with Material 3
- **Architecture**: MVVM (Model-View-ViewModel)
- **Dependency Injection**: Hilt
- **Backend**: Firebase (Authentication + Firestore)
- **Async Operations**: Coroutines + Flow
- **Navigation**: Compose Navigation

## 📱 Screenshots

*Add screenshots here after running the app*

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog or later
- Android SDK 30+
- Firebase project (already configured)

### Setup

1. **Clone the repository**
   ```bash
   git clone <your-repo-url>
   cd CampusCravings
   ```

2. **Add sample data to Firebase**
   - Follow the instructions in `SETUP_INSTRUCTIONS.md`
   - Add restaurants and menu items to Firestore

3. **Build and run**
   ```bash
   ./gradlew clean build
   ```
   - Run on emulator or physical device (API 30+)

4. **Test the app**
   - Create a customer account and place an order
   - Create a delivery account (different email) and accept the order
   - Watch real-time updates!

## 📂 Project Structure

```
app/src/main/java/com/example/campuscravings/
├── data/
│   ├── model/          # Data classes (User, Restaurant, Order, etc.)
│   └── repository/     # Firebase data operations
├── di/                 # Hilt dependency injection modules
├── ui/
│   ├── auth/          # Authentication screens
│   ├── customer/      # Customer flow screens
│   ├── delivery/      # Delivery partner screens
│   ├── navigation/    # Navigation graph
│   └── theme/         # Material 3 theming
├── CampusCravingsApp.kt  # Application class
└── MainActivity.kt       # Entry point
```

## 🔥 Firebase Collections

### `users`
- User profiles with role (CUSTOMER/DELIVERY)
- Current location and availability status

### `restaurants`
- Restaurant details (name, location, rating, etc.)
- Delivery fees and estimated times

### `menuItems`
- Menu items linked to restaurants
- Prices, descriptions, and availability

### `orders`
- Order details with real-time status updates
- Links customers, delivery partners, and restaurants

## 🎨 Design Decisions

1. **Real-time Updates**: Used Firestore listeners to provide instant order status updates
2. **Role-based UI**: Completely different interfaces for customers vs delivery partners
3. **Cart Management**: Local state management for cart, persisted only on checkout
4. **Material 3**: Modern, accessible UI following Google's latest design guidelines
5. **MVVM + Hilt**: Clean separation of concerns for maintainability

## 🚧 Future Enhancements

- [ ] Google Maps integration for live tracking
- [ ] Push notifications for order updates
- [ ] Rating and review system
- [ ] Payment integration (Stripe/PayPal)
- [ ] Chat between customer and delivery partner
- [ ] Promo codes and discounts
- [ ] Restaurant partner dashboard
- [ ] Delivery partner earnings tracking

## 👥 Team

Built for the Surge Hackathon by [Your Name/Team]

## 📄 License

This project is built for educational purposes as part of a hackathon.

## 🙏 Acknowledgments

- Firebase for backend infrastructure
- Material Design for UI components
- SFU Campus community for inspiration

---

**Made with ❤️ for students, by students**
