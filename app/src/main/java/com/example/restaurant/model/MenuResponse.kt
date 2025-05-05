package com.example.restaurant.model

data class MenuResponse(
    val status: Int,
    val message: String,
    val data: List<MenuItem>,
)


data class SearchResponse(
    val status: Int,
    val message: String,
    val menus: List<MenuItem>?
)
