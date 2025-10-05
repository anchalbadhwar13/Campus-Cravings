# 🚀 Quick Start Guide - Campus Cravings

## ⚡ 5-Minute Setup

### Step 1: Add Sample Data to Firebase (3 minutes)

1. Go to [Firebase Console](https://console.firebase.google.com)
2. Select your project: `campus-cravings-5dd6f`
3. Click **Firestore Database** in the left menu
4. Click **Start collection**

#### Add Restaurants Collection

Collection ID: `restaurants`

Add these 4 documents (click "Add document" for each):

**Document ID: `rest1`**
```
name: "Tim Hortons"
description: "Coffee, donuts, and breakfast sandwiches"
location: "AQ Building - Ground Floor"
imageUrl: ""
rating: 4.5
deliveryFee: 2.99
estimatedTime: "10-15 min"
isOpen: true
```

**Document ID: `rest2`**
```
name: "Subway"
description: "Fresh subs and salads"
location: "Student Union Building"
imageUrl: ""
rating: 4.2
deliveryFee: 2.99
estimatedTime: "15-20 min"
isOpen: true
```

**Document ID: `rest3`**
```
name: "Starbucks"
description: "Premium coffee and pastries"
location: "Library - Main Floor"
imageUrl: ""
rating: 4.7
deliveryFee: 3.99
estimatedTime: "10-15 min"
isOpen: true
```

**Document ID: `rest4`**
```
name: "Pita Pit"
description: "Healthy pitas and wraps"
location: "Cornerstone Building"
imageUrl: ""
rating: 4.4
deliveryFee: 2.99
estimatedTime: "15-20 min"
isOpen: true
```

#### Add Menu Items Collection

Collection ID: `menuItems`

Add at least 3 items per restaurant (12 total). Here are 3 examples:

**Document ID: `item1`**
```
restaurantId: "rest1"
name: "Double Double Coffee"
description: "Classic Tim's coffee with 2 cream, 2 sugar"
price: 2.49
imageUrl: ""
category: "Beverages"
isAvailable: true
```

**Document ID: `item2`**
```
restaurantId: "rest1"
name: "Boston Cream Donut"
description: "Chocolate glazed donut with cream filling"
price: 1.99
imageUrl: ""
category: "Donuts"
isAvailable: true
```

**Document ID: `item3`**
```
restaurantId: "rest1"
name: "Breakfast Sandwich"
description: "Egg, cheese, and bacon on English muffin"
price: 4.99
imageUrl: ""
category: "Breakfast"
isAvailable: true
```

*See `firebase_sample_data.json` for all 12 items*

### Step 2: Enable Authentication (1 minute)

1. In Firebase Console, click **Authentication**
2. Click **Get Started**
3. Click **Email/Password**
4. Enable it and click **Save**

### Step 3: Update Firestore Rules (1 minute)

1. In Firebase Console, click **Firestore Database**
2. Click **Rules** tab
3. Replace with:
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
4. Click **Publish**

### Step 4: Build and Run

Open in Android Studio and click Run! 🎉

---

## 📱 Testing the App (2 devices recommended)

### Device 1 - Customer Flow

1. **Sign Up**
   - Email: `customer@test.com`
   - Password: `password123`
   - Name: `John Student`
   - Phone: `604-123-4567`
   - Role: **Order Food** ✅

2. **Browse & Order**
   - See list of restaurants
   - Click on "Tim Hortons"
   - Add items to cart
   - Click "Checkout"
   - Enter location: `AQ 3005`
   - Place order

3. **Track Order**
   - Click "View My Orders"
   - See order status update in real-time!

### Device 2 - Delivery Flow

1. **Sign Up**
   - Email: `delivery@test.com`
   - Password: `password123`
   - Name: `Jane Delivery`
   - Phone: `604-987-6543`
   - Role: **Deliver Food** ✅

2. **Accept Order**
   - See available orders
   - Click "Accept Order"
   - Order moves to "Active" tab

3. **Complete Delivery**
   - Click "Mark as Picked Up"
   - (Device 1 sees update!)
   - Click "Mark as Delivered"
   - (Device 1 sees update!)
   - Earn delivery fee! 💰

---

## 🎯 Demo Script for Judges

**Introduction (30 seconds)**
> "Hi! We're presenting Campus Cravings - a student-to-student food delivery app. The problem? Students are too busy studying to leave their spot and get food. Our solution? Connect them with other students who can deliver food and earn money!"

**Demo Customer Side (1 minute)**
> "Let me show you the customer experience. [Sign up] I'm a student in the library. [Browse restaurants] I can see nearby campus restaurants. [Select Tim Hortons] Let me order a coffee and donut. [Add to cart, checkout] I enter my location - AQ Building, Room 3005. [Place order] Done! Now I wait."

**Demo Delivery Side (1 minute)**
> "Now on the delivery side - [Sign up as delivery] I'm another student near Tim Hortons. [Show available orders] I see the order! [Accept] I accept it. [Show customer device] Look - the customer sees I accepted! [Mark picked up] I got the food. [Show customer device] They see it's on the way! [Mark delivered] Done! I earned $2.99."

**Highlight Real-time (30 seconds)**
> "The killer feature? Everything is real-time. Watch both screens - instant updates! This is powered by Firebase Firestore listeners."

**Closing (30 seconds)**
> "Built with modern Android - Kotlin, Jetpack Compose, MVVM architecture. It's scalable to any campus, any restaurant. Students helping students. Thank you!"

---

## 🐛 Troubleshooting

### App crashes on launch
- Check Firebase `google-services.json` is in `app/` folder
- Verify package name matches: `com.example.campuscravings`

### No restaurants showing
- Add sample data to Firestore (see Step 1)
- Check Firestore rules allow authenticated reads

### Can't sign in
- Enable Email/Password auth in Firebase Console
- Check internet connection

### Build errors
- Sync Gradle files
- Clean and rebuild: `./gradlew clean build`
- Check Android Studio is up to date

---

## 📞 Need Help?

1. Check `IMPLEMENTATION_SUMMARY.md` for full details
2. Check `SETUP_INSTRUCTIONS.md` for detailed setup
3. Check `README.md` for project overview

---

**You're ready to win this hackathon! 🏆**
