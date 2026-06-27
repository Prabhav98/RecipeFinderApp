package com.recipefinder.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.recipefinder.model.Category
import com.recipefinder.model.Meal
import com.recipefinder.repository.MealRepository
import com.recipefinder.utils.Resource
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: MealRepository) : ViewModel() {

    // Categories LiveData
    private val _categories = MutableLiveData<Resource<List<Category>>>()
    val categories: LiveData<Resource<List<Category>>> = _categories

    // Meals LiveData
    private val _meals = MutableLiveData<Resource<List<Meal>>>()
    val meals: LiveData<Resource<List<Meal>>> = _meals

    // Search LiveData
    private val _searchResults = MutableLiveData<Resource<List<Meal>>>()
    val searchResults: LiveData<Resource<List<Meal>>> = _searchResults

    // Track selected category
    private val _selectedCategory = MutableLiveData<String>()
    val selectedCategory: LiveData<String> = _selectedCategory

    // Debounce job for search
    private var searchJob: Job? = null

    // Keep track of current category for refresh
    private var currentCategory: String = "Beef"

    init {
        fetchCategories()
        fetchMealsByCategory(currentCategory)
    }

    fun fetchCategories() {
        _categories.value = Resource.Loading()

        viewModelScope.launch {
            val result = repository.getCategories()
            when (result) {
                is Resource.Success -> {
                    _categories.value = Resource.Success(result.data?.categories ?: emptyList())
                }
                is Resource.Error -> {
                    _categories.value = Resource.Error(result.message ?: "Error fetching categories")
                }
                else -> {}
            }
        }
    }

    fun fetchMealsByCategory(category: String) {
        currentCategory = category
        _selectedCategory.value = category
        _meals.value = Resource.Loading()
        viewModelScope.launch {
            val result = repository.getMealsByCategory(category)
            when (result) {
                is Resource.Success -> {
                    _meals.value = Resource.Success(result.data?.meals ?: emptyList())
                }
                is Resource.Error -> {
                    _meals.value = Resource.Error(result.message ?: "Error fetching meals")
                }
                else -> {}
            }
        }
    }

    fun searchMeals(query: String) {
        // Cancel previous search job
        searchJob?.cancel()

        if (query.isBlank()) {
            // If query is empty, revert to category meals
            fetchMealsByCategory(currentCategory)
            return
        }

        searchJob = viewModelScope.launch {
            // Debounce 500ms
            delay(500)
            _searchResults.value = Resource.Loading()
            val result = repository.searchMeals(query)
            when (result) {
                is Resource.Success -> {
                    _searchResults.value = Resource.Success(result.data?.meals ?: emptyList())
                }
                is Resource.Error -> {
                    _searchResults.value = Resource.Error(result.message ?: "Error searching meals")
                }
                else -> {}
            }
        }
    }

    fun refreshCurrentCategory() {
        fetchMealsByCategory(currentCategory)
    }
}