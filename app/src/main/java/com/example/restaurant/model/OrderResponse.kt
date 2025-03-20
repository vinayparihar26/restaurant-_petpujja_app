package com.example.restaurant.model

data class OrderResponse(
    val orders: List<OrdersItem?>? = null,
    val grandTotal: Int? = null,
    val status: Int? = null
)
