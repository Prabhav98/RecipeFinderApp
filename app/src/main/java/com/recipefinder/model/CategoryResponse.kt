package com.recipefinder.model

import com.google.gson.annotations.SerializedName

data class CategoryResponse(
    @SerializedName("categories")
    val categories: List<Category>
)