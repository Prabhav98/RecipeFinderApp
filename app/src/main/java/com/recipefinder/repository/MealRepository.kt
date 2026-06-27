package com.recipefinder.repository

import com.recipefinder.model.CategoryResponse
import com.recipefinder.model.MealDetailResponse
import com.recipefinder.model.MealResponse
import com.recipefinder.api.RetrofitInstance
import com.recipefinder.utils.Resource

class MealRepository {

    private val api = RetrofitInstance.api

    suspend fun getCategories(): Resource<CategoryResponse> {
        return try {
            val response = api.getCategories()
            Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An unknown error occurred")
        }
    }

    suspend fun getMealsByCategory(category: String): Resource<MealResponse> {
        return try {
            val response = api.getMealsByCategory(category)
            Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An unknown error occurred")
        }
    }

    suspend fun searchMeals(query: String): Resource<MealResponse> {
        return try {
            val response = api.searchMeals(query)
            Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An unknown error occurred")
        }
    }

    suspend fun getMealById(mealId: String): Resource<MealDetailResponse> {
        return try {
            val response = api.getMealById(mealId)
            Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An unknown error occurred")
        }
    }

    suspend fun getRandomMeal(): Resource<MealDetailResponse> {
        return try {
            val response = api.getRandomMeal()
            Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An unknown error occurred")
        }
    }
}