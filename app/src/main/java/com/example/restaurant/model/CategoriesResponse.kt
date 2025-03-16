package com.example.restaurant.model

import com.google.gson.annotations.SerializedName


data class CategoriesResponse(
    val status: Int,
    val message: String,
    @SerializedName("data") val data: List<CategoryModel>,
)