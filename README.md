# My Mall

This is an open-sourced & full-stack E-Commerce App. I started this project out of curiosity with absolutely no vision, but now it has evolved into a full-size Shopping App. This repository holds the basic customer version of the app. You can download the demo app to see what it looks like now. I am looking forward to making an Admin version of the My Mall app. If you are interested, feel free to contribute!

## Demo

[Try the Demo on PlayStore](https://play.google.com/store/apps/details?id=com.blogspot.mido_mymall)

## Sample preview (The full preview is below)

<p>
<img src="https://github.com/MML-Pro/ImagesAndVideos/blob/3a55b428f7c828efd33197b223c40ec3d736b580/My%20Mall/screenshots/1l.png" width="150">
<img src="https://github.com/MML-Pro/ImagesAndVideos/blob/3a55b428f7c828efd33197b223c40ec3d736b580/My%20Mall/screenshots/6l.png" width="150">
<img src="https://github.com/MML-Pro/ImagesAndVideos/blob/3a55b428f7c828efd33197b223c40ec3d736b580/My%20Mall/screenshots/1d.png" width="150">
<img src="https://github.com/MML-Pro/ImagesAndVideos/blob/3a55b428f7c828efd33197b223c40ec3d736b580/My%20Mall/screenshots/2d.png" width="150">
</p>

## Features

### Product Management
- Display all products in three different views (View Pager, Grid, Horizontal).
- Show detailed product information: name, price, discounted price, description, coupons, offers, and rating.
- Search for products.
- Add and delete products in the wishlist.
- Add and delete products in the cart list.
- Added Arabic translations for product details.
- Users can rate products and view average ratings in real-time.

### User Management
- Create a regular user (email & password) or sign in with one touch via Google, and save user info.
- Edit/update user info: username, email, password.
- Add new addresses with update and delete options.
- Support for English and Arabic languages.
- Support for Dark and Light modes.

### Orders and Payments
- Make orders and secure payments via RazorPay payment gateway.
- Show order status (Ordered, Packed, Shipped, Delivered) in the Orders section.
- Display order details: user order rate, ordered date, packed date, shipped date, delivered date, shipping details, username, addresses, and order amount summary.
- Improved price display precision and order summary.
- Resolved bugs in address deletion and delivery screen.


## Tech Stack & Open-source Libraries

- **Minimum SDK level**: 23
- Kotlin-based, Coroutines together with Flow for asynchronous streams and one-side ViewModel to fragment communication.
- Dagger Hilt for dependency injection.
- Firebase Firestore for the backend database.
- Firebase Authentication for handling user auth (create/sign-up and sign-in).
- Datastore: To store key-value pairs or typed objects with protocol buffers.
- Material 3 with XML for the UI.
- Glide: A fast and efficient open-source media management and image loading framework for Android that wraps media decoding, memory and disk caching, and resource pooling into a simple and easy-to-use interface.
- JetPack:
  - Fragments: Present a reusable portion of the app's UI.
  - ViewModel: UI-related data holder, lifecycle aware.
  - Navigation Component: Makes it easier to navigate between different screens and pass data in a type-safe way.
- **Architecture**: Android Clean Architecture (Onion-Arch) with Repository pattern.


# Preview
<p>
<img src="https://github.com/MML-Pro/ImagesAndVideos/blob/3a55b428f7c828efd33197b223c40ec3d736b580/My%20Mall/screenshots/1l.png" width="150">
<img src="https://github.com/MML-Pro/ImagesAndVideos/blob/3a55b428f7c828efd33197b223c40ec3d736b580/My%20Mall/screenshots/2l.png" width="150">
<img src="https://github.com/MML-Pro/ImagesAndVideos/blob/3a55b428f7c828efd33197b223c40ec3d736b580/My%20Mall/screenshots/3l.png" width="150">
<img src="https://github.com/MML-Pro/ImagesAndVideos/blob/3a55b428f7c828efd33197b223c40ec3d736b580/My%20Mall/screenshots/4l.png" width="150">
<img src="https://github.com/MML-Pro/ImagesAndVideos/blob/3a55b428f7c828efd33197b223c40ec3d736b580/My%20Mall/screenshots/5l.png" width="150">
<img src="https://github.com/MML-Pro/ImagesAndVideos/blob/3a55b428f7c828efd33197b223c40ec3d736b580/My%20Mall/screenshots/6l.png" width="150">
<img src="https://github.com/MML-Pro/ImagesAndVideos/blob/3a55b428f7c828efd33197b223c40ec3d736b580/My%20Mall/screenshots/7l.png" width="150">
<img src="https://github.com/MML-Pro/ImagesAndVideos/blob/3a55b428f7c828efd33197b223c40ec3d736b580/My%20Mall/screenshots/8l.png" width="150">
<img src="https://github.com/MML-Pro/ImagesAndVideos/blob/3a55b428f7c828efd33197b223c40ec3d736b580/My%20Mall/screenshots/9l.png" width="150">
<img src="https://github.com/MML-Pro/ImagesAndVideos/blob/3a55b428f7c828efd33197b223c40ec3d736b580/My%20Mall/screenshots/10l.png" width="150">
<img src="https://github.com/MML-Pro/ImagesAndVideos/blob/3a55b428f7c828efd33197b223c40ec3d736b580/My%20Mall/screenshots/11l.png" width="150">
<img src="https://github.com/MML-Pro/ImagesAndVideos/blob/3a55b428f7c828efd33197b223c40ec3d736b580/My%20Mall/screenshots/12l.png" width="150">
<img src="https://github.com/MML-Pro/ImagesAndVideos/blob/c9c9216c57a1acb0963e681404cace426ce71523/My%20Mall/screenshots/13l.jpg" width="150">
<img src="https://github.com/MML-Pro/ImagesAndVideos/blob/c9c9216c57a1acb0963e681404cace426ce71523/My%20Mall/screenshots/14l.jpg" width="150">
<img src="https://github.com/MML-Pro/ImagesAndVideos/blob/c9c9216c57a1acb0963e681404cace426ce71523/My%20Mall/screenshots/15l.jpg" width="150">
</p>

<p>
<img src="https://github.com/MML-Pro/ImagesAndVideos/blob/c9c9216c57a1acb0963e681404cace426ce71523/My%20Mall/screenshots/1d.png" width="150">
<img src="https://github.com/MML-Pro/ImagesAndVideos/blob/c9c9216c57a1acb0963e681404cace426ce71523/My%20Mall/screenshots/2d.png" width="150">
<img src="https://github.com/MML-Pro/ImagesAndVideos/blob/c9c9216c57a1acb0963e681404cace426ce71523/My%20Mall/screenshots/3d.png" width="150">
<img src="https://github.com/MML-Pro/ImagesAndVideos/blob/c9c9216c57a1acb0963e681404cace426ce71523/My%20Mall/screenshots/4d.png" width="150">
<img src="https://github.com/MML-Pro/ImagesAndVideos/blob/c9c9216c57a1acb0963e681404cace426ce71523/My%20Mall/screenshots/5d.png" width="150">
<img src="https://github.com/MML-Pro/ImagesAndVideos/blob/c9c9216c57a1acb0963e681404cace426ce71523/My%20Mall/screenshots/6d.png" width="150">
<img src="https://github.com/MML-Pro/ImagesAndVideos/blob/c9c9216c57a1acb0963e681404cace426ce71523/My%20Mall/screenshots/7d.png" width="150">
<img src="https://github.com/MML-Pro/ImagesAndVideos/blob/c9c9216c57a1acb0963e681404cace426ce71523/My%20Mall/screenshots/8d.png" width="150">
<img src="https://github.com/MML-Pro/ImagesAndVideos/blob/c9c9216c57a1acb0963e681404cace426ce71523/My%20Mall/screenshots/9d.png" width="150">
<img src="https://github.com/MML-Pro/ImagesAndVideos/blob/c9c9216c57a1acb0963e681404cace426ce71523/My%20Mall/screenshots/10d.png" width="150">
<img src="https://github.com/MML-Pro/ImagesAndVideos/blob/c9c9216c57a1acb0963e681404cace426ce71523/My%20Mall/screenshots/11d.png" width="150">
<img src="https://github.com/MML-Pro/ImagesAndVideos/blob/c9c9216c57a1acb0963e681404cace426ce71523/My%20Mall/screenshots/13d.png" width="150">
<img src="https://github.com/MML-Pro/ImagesAndVideos/blob/c9c9216c57a1acb0963e681404cace426ce71523/My%20Mall/screenshots/14d.png" width="150">
<img src="https://github.com/MML-Pro/ImagesAndVideos/blob/c9c9216c57a1acb0963e681404cace426ce71523/My%20Mall/screenshots/15d.png" width="150">
<img src="https://github.com/MML-Pro/ImagesAndVideos/blob/c9c9216c57a1acb0963e681404cace426ce71523/My%20Mall/screenshots/16d.png" width="150">
</p>
