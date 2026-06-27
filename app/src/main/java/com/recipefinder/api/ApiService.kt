package com.recipefinder.api

import com.recipefinder.model.CategoryResponse
import com.recipefinder.model.MealDetailResponse
import com.recipefinder.model.MealResponse
import retrofit2.http.GET
import retrofit2.http.Query


// Using TheMealDB API - 100% FREE, no API key needed
// Base URL: https://www.themealdb.com/api/json/v1/1/
interface ApiService {

    // Get all categories
    @GET("categories.php")
    suspend fun getCategories(): CategoryResponse

    // Filter meals by category
    @GET("filter.php")
    suspend fun getMealsByCategory(
        @Query("c") category: String
    ): MealResponse

    // Search meals by name
    @GET("search.php")
    suspend fun searchMeals(
        @Query("s") mealName: String
    ): MealResponse

    // Get meal detail by ID
    @GET("lookup.php")
    suspend fun getMealById(
        @Query("i") mealId: String
    ): MealDetailResponse

    // Get random meal
    @GET("random.php")
    suspend fun getRandomMeal(): MealDetailResponse
}