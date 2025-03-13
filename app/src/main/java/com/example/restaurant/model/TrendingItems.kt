package com.example.restaurant.model

data class TrendingItems(
    val imageUrl: String,
    val name: String,
    val rating: Float,
    val deliveryTime: String,
    val restaurantName: String,
    val restaurantAddress: String
)
