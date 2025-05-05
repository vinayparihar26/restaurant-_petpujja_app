below is folder structure

com.example.restaurant
│
├── activities                      # Activities for the app
│   ├── LoginActivity.kt            # Login screen activity
│   ├── MainActivity.kt             # Main screen activity
│   ├── UpdatePasswordActivity.kt   # Update password screen activity
│   ├── UpdateProfileActivity.kt    # Update profile screen activity
│
├── adapter                         # RecyclerView Adapters
│   └── WishlistAdapter.kt          # Adapter for wishlist items
│
├── api                             # Network and API-related code
│   ├── RetrofitClient.kt           # Retrofit client initialization
│   └── ApiService.kt               # API endpoints interface
│
├── fragments                       # Fragments for different screens
│   ├── ProfileFragment.kt          # User profile screen fragment
│   └── WishlistFragment.kt         # Wishlist screen fragment
│
├── model                           # Data models and response classes
│   ├── ApiResponse.kt              # API response base class
│   ├── LoginResponse.kt            # Login response model
│   ├── WishlistItem.kt             # Wishlist item model
│   └── WishlistResponse.kt         # Wishlist response model
│
├── repository                      # Data handling and repository classes
│   └── AuthRepository.kt           # Authentication-related logic
│
├── utils                           # Utility classes (if any)
│   └── SharedPreferencesUtils.kt   # Shared preferences utility class (optional, for managing shared preferences)
│
├── viewmodel                       # ViewModels to interact with UI and repository
│   └── AuthViewModel.kt            # ViewModel for auth-related actions (login, register)
│
└── MyApplication.kt                # Application class (for app-wide settings)
