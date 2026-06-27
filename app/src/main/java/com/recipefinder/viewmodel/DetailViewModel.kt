package com.recipefinder.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.recipefinder.model.MealDetail
import com.recipefinder.repository.MealRepository
import com.recipefinder.utils.Resource
import kotlinx.coroutines.launch

class DetailViewModel(private val repository: MealRepository) : ViewModel() {

    private val _mealDetail = MutableLiveData<Resource<MealDetail>>()
    val mealDetail: LiveData<Resource<MealDetail>> = _mealDetail

    fun fetchMealDetail(mealId: String) {
        _mealDetail.value = Resource.Loading()
        viewModelScope.launch {
            val result = repository.getMealById(mealId)
            when (result) {
                is Resource.Success -> {
                    val meal = result.data?.meals?.firstOrNull()
                    if (meal != null) {
                        _mealDetail.value = Resource.Success(meal)
                    } else {
                        _mealDetail.value = Resource.Error("Meal not found")
                    }
                }
                is Resource.Error -> {
                    _mealDetail.value = Resource.Error(result.message ?: "Error fetching meal detail")
                }
                else -> {}
            }
        }
    }
}