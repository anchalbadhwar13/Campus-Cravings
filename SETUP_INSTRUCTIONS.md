# Campus Cravings - Setup Instructions

## 🚀 Quick Start

### 1. Firebase Setup (Already Done ✅)
- Firebase project created: `campus-cravings-5dd6f`
- google-services.json added
- Authentication and Firestore enabled

### 2. Add Sample Data to Firebase

You need to add sample restaurants and menu items to your Firestore database. Go to Firebase Console > Firestore Database and add the following collections:

#### Collection: `restaurants`

**Document 1:**
```
id: "rest1"
name: "Tim Hortons"
description: "Coffee, donuts, and breakfast sandwiches"
location: "AQ Building - Ground Floor"
imageUrl: ""
rating: 4.5
deliveryFee: 2.99
estimatedTime: "10-15 min"
isOpen: true
```

**Document 2:**
```
id: "rest2"
name: "Subway"
description: "Fresh subs and salads"
location: "Student Union Building"
imageUrl: ""
rating: 4.2
deliveryFee: 2.99
estimatedTime: "15-20 min"
isOpen: true
```

**Document 3:**
```
id: "rest3"
name: "Starbucks"
description: "Premium coffee and pastries"
location: "Library - Main Floor"
imageUrl: ""
rating: 4.7
deliveryFee: 3.99
estimatedTime: "10-15 min"
isOpen: true
```

**Document 4:**
```
id: "rest4"
name: "Pita Pit"
description: "Healthy pitas and wraps"
location: "Cornerstone Building"
imageUrl: ""
rating: 4.4
deliveryFee: 2.99
estimatedTime: "15-20 min"
isOpen: true
```

#### Collection: `menuItems`

**For Tim Hortons (rest1):**

```
Document: "item1"
id: "item1"
restaurantId: "rest1"
name: "Double Double Coffee"
description: "Classic Tim's coffee with 2 cream, 2 sugar"
price: 2.49
imageUrl: ""
category: "Beverages"
isAvailable: true
```

```
Document: "item2"
id: "item2"
restaurantId: "rest1"
name: "Boston Cream Donut"
description: "Chocolate glazed donut with cream filling"
price: 1.99
imageUrl: ""
category: "Donuts"
isAvailable: true
```

```
Document: "item3"
id: "item3"
restaurantId: "rest1"
name: "Breakfast Sandwich"
description: "Egg, cheese, and bacon on English muffin"
price: 4.99
imageUrl: ""
category: "Breakfast"
isAvailable: true
```

**For Subway (rest2):**

```
Document: "item4"
id: "item4"
restaurantId: "rest2"
name: "Italian BMT"
description: "Ham, salami, pepperoni with veggies"
price: 7.99
imageUrl: ""
category: "Subs"
isAvailable: true
```

```
Document: "item5"
id: "item5"
restaurantId: "rest2"
name: "Veggie Delite"
description: "Fresh vegetables on your choice of bread"
price: 5.99
imageUrl: ""
category: "Subs"
isAvailable: true
```

```
Document: "item6"
id: "item6"
restaurantId: "rest2"
name: "Chicken Teriyaki"
description: "Grilled chicken with teriyaki sauce"
price: 8.49
imageUrl: ""
category: "Subs"
isAvailable: true
```

**For Starbucks (rest3):**

```
Document: "item7"
id: "item7"
restaurantId: "rest3"
name: "Caffe Latte"
description: "Espresso with steamed milk"
price: 5.25
imageUrl: ""
category: "Beverages"
isAvailable: true
```

```
Document: "item8"
id: "item8"
restaurantId: "rest3"
name: "Caramel Macchiato"
description: "Vanilla, espresso, milk, and caramel"
price: 5.95
imageUrl: ""
category: "Beverages"
isAvailable: true
```

```
Document: "item9"
id: "item9"
restaurantId: "rest3"
name: "Blueberry Muffin"
description: "Fresh baked blueberry muffin"
price: 3.45
imageUrl: ""
category: "Food"
isAvailable: true
```

**For Pita Pit (rest4):**

```
Document: "item10"
id: "item10"
restaurantId: "rest4"
name: "Chicken Caesar Pita"
description: "Grilled chicken with Caesar dressing"
price: 8.99
imageUrl: ""
category: "Pitas"
isAvailable: true
```

```
Document: "item11"
id: "item11"
restaurantId: "rest4"
name: "Falafel Pita"
description: "Crispy falafel with hummus and veggies"
price: 7.99
imageUrl: ""
category: "Pitas"
isAvailable: true
```

```
Document: "item12"
id: "item12"
restaurantId: "rest4"
name: "Greek Pita"
description: "Gyro meat with tzatziki sauce"
price: 9.49
imageUrl: ""
category: "Pitas"
isAvailable: true
```

### 3. Firebase Rules

Make sure your Firestore rules allow read/write access (for development):

```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /{document=**} {
      allow read, write: if request.auth != null;
    }
  }
}
```

### 4. Build and Run

```bash
./gradlew clean
./gradlew build
```

Then run the app on your Android device or emulator.

## 📱 How to Test

### As a Customer:
1. Sign up with email/password, select "Order Food"
2. Browse restaurants
3. Select a restaurant and add items to cart
4. Checkout with delivery location (e.g., "AQ 3005")
5. View your orders and track status

### As a Delivery Person:
1. Sign up with different email, select "Deliver Food"
2. View available orders
3. Accept an order
4. Mark as "Picked Up" when you get the food
5. Mark as "Delivered" when complete

## 🎯 Key Features Implemented

✅ User Authentication (Email/Password)
✅ Role-based access (Customer/Delivery)
✅ Restaurant browsing
✅ Menu viewing and cart management
✅ Order placement with delivery details
✅ Real-time order tracking
✅ Delivery person dashboard
✅ Order acceptance and status updates
✅ Clean MVVM architecture with Hilt DI
✅ Material 3 Design
✅ Compose Navigation

## 🔥 Firebase Collections Structure

- `users/` - User profiles with roles
- `restaurants/` - Restaurant information
- `menuItems/` - Menu items for each restaurant
- `orders/` - Orders with real-time updates

## 🎨 Tech Stack

- Kotlin
- Jetpack Compose
- Firebase Auth & Firestore
- Hilt for Dependency Injection
- Coroutines & Flow
- Material 3 Design
- MVVM Architecture

## 📝 Notes for Hackathon Demo

- Make sure to have 2 devices/emulators to demo customer and delivery flows simultaneously
- Pre-populate some restaurants and menu items before the demo
- Consider adding your actual campus building names for authenticity
- The app uses real-time listeners, so order status updates instantly!

Good luck with your hackathon! 🚀
