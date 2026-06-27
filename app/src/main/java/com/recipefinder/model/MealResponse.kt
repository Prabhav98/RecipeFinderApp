package com.recipefinder.model

import com.google.gson.annotations.SerializedName

data class MealResponse(
    @SerializedName("meals")
    val meals: List<Meal>?
)