package com.example.restaurant.model

import com.google.gson.annotations.SerializedName

data class CategoryModel(
    @SerializedName("category_id") val categoryId: String,
    @SerializedName("category_name") val categoryName: String,
    @SerializedName("category_img") val categoryImg: String,
)