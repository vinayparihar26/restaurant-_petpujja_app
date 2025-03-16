package com.example.restaurant.model

data class MenuResponse(
    val status: Int,
    val message: String,
    val data: List<com.example.restaurant.model.MenuItem>,
)
